/*
 * This file is part of ChatEx
 * Copyright (C) 2022 ChatEx Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.jeter.chatex;

import de.jeter.chatex.api.events.*;
import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.*;
import de.jeter.chatex.utils.adManager.AdManager;
import de.jeter.chatex.utils.adManager.SimpleAdManager;
import de.jeter.chatex.utils.adManager.SmartAdManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final AdManager adManager = Config.ADS_SMART_MANAGER.getBoolean() ? new SmartAdManager() : new SimpleAdManager();
    private EventPriority registeredPriority;

    /**
     * Registers this listener for {@link AsyncPlayerChatEvent} at the priority configured
     * in {@link Config#PRIORITY}, instead of relying on one fixed {@code @EventHandler} per priority.
     */
    public void register(Plugin plugin) {
        registeredPriority = resolvePriority();
        Bukkit.getPluginManager().registerEvent(
                AsyncPlayerChatEvent.class,
                this,
                registeredPriority,
                (listener, event) -> executeChatEvent((AsyncPlayerChatEvent) event),
                plugin
        );
    }

    /**
     * Re-registers this listener if the configured {@link Config#PRIORITY} changed, e.g. after {@code /chatex reload}.
     */
    public void reregister(Plugin plugin) {
        if (resolvePriority() == registeredPriority) {
            return;
        }
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        register(plugin);
    }

    private EventPriority resolvePriority() {
        try {
            return EventPriority.valueOf(Config.PRIORITY.getString().toUpperCase());
        } catch (IllegalArgumentException ex) {
            ChatEx.getInstance().getLogger().warning("Unknown EventPriority '" + Config.PRIORITY.getString() + "' configured, falling back to LOWEST.");
            return EventPriority.LOWEST;
        }
    }

    /**
     * Carries the message-format pipeline state between the individual processing steps below.
     */
    private record RoutingResult(String format, String chatMessage, boolean global) {
    }

    private void executeChatEvent(AsyncPlayerChatEvent event) {
        LogHelper.debug("ChatEvent fired with priority: " + Config.PRIORITY.getString().toUpperCase() + ", ChatEx reacting to it...");
        Player player = event.getPlayer();

        if (!hasChatPermission(event, player)) {
            return;
        }

        String format = PluginManager.getMessageFormat(player);
        logFormatDebug(player, format);

        String chatMessage = event.getMessage();

        // Every apply*-step below returns null if it already cancelled/handled the event itself.
        chatMessage = applyAntiSpam(event, player, chatMessage);
        if (chatMessage == null) {
            return;
        }

        chatMessage = applyAdBlocker(event, player, chatMessage);
        if (chatMessage == null) {
            return;
        }

        chatMessage = applyWordBlocker(event, player, chatMessage);
        if (chatMessage == null) {
            return;
        }

        RoutingResult routing = applyRouting(event, player, format, chatMessage);
        if (routing == null) {
            return;
        }

        forwardCrossServerMessage(player, routing);
        finalizeMessage(event, player, routing);
    }

    private boolean hasChatPermission(AsyncPlayerChatEvent event, Player player) {
        if (player.hasPermission("chatex.allowchat")) {
            return true;
        }
        String msg = Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.allowchat");
        player.sendMessage(msg);
        event.setCancelled(true);
        return false;
    }

    private void logFormatDebug(Player player, String format) {
        LogHelper.debug("Format: " + format);
        LogHelper.debug("Prefix: " + PluginManager.getPrefix(player));
        LogHelper.debug("Suffix: " + PluginManager.getSuffix(player));
    }

    private String applyAntiSpam(AsyncPlayerChatEvent event, Player player, String chatMessage) {
        if (!AntiSpamManager.getInstance().isAllowed(player)) {
            long remainingTime = AntiSpamManager.getInstance().getRemainingSeconds(player);
            String message = Locales.ANTI_SPAM_DENIED.getString(player).replaceAll("%time%", remainingTime + "");
            MessageBlockedBySpamManagerEvent blockedEvent = new MessageBlockedBySpamManagerEvent(player, chatMessage, message, remainingTime);
            Bukkit.getPluginManager().callEvent(blockedEvent);
            event.setCancelled(!blockedEvent.isCancelled());
            if (!blockedEvent.isCancelled()) {
                player.sendMessage(blockedEvent.getPluginMessage());
                return null;
            }
            chatMessage = blockedEvent.getMessage();
        }
        AntiSpamManager.getInstance().put(player);
        LogHelper.debug("Player did not activate the AntiSpam. Continuing...");
        return chatMessage;
    }

    private String applyAdBlocker(AsyncPlayerChatEvent event, Player player, String chatMessage) {
        if (adManager.checkForAds(chatMessage, player)) {
            String message = Locales.MESSAGES_AD.getString(null).replaceAll("%perm", "chatex.bypassads");
            MessageBlockedByAdManagerEvent blockedEvent = new MessageBlockedByAdManagerEvent(player, chatMessage, message);
            Bukkit.getPluginManager().callEvent(blockedEvent);
            chatMessage = blockedEvent.getMessage();
            event.setCancelled(!blockedEvent.isCancelled());
            if (!blockedEvent.isCancelled()) {
                player.sendMessage(blockedEvent.getPluginMessage());
                return null;
            }
        }
        LogHelper.debug("Player did not activate the AdBlocker. Continuing...");
        return chatMessage;
    }

    private String applyWordBlocker(AsyncPlayerChatEvent event, Player player, String chatMessage) {
        for (String block : Config.BLOCKED_WORDS.getStringList()) {
            if (chatMessage.toLowerCase().contains(block.toLowerCase())) {
                LogHelper.debug("Player activated wordblocker! ChatMessage: " + chatMessage + " contains blockedWord: " + block);
                String message = Locales.MESSAGES_BLOCKED.getString(null);
                MessageContainsBlockedWordEvent blockedEvent = new MessageContainsBlockedWordEvent(player, chatMessage, message);
                Bukkit.getPluginManager().callEvent(blockedEvent);
                event.setCancelled(!blockedEvent.isCancelled());
                chatMessage = blockedEvent.getMessage();
                if (!blockedEvent.isCancelled()) {
                    player.sendMessage(blockedEvent.getPluginMessage());
                    return null;
                }
            }
        }
        LogHelper.debug("Player did not use a blocked word. Continuing...");
        LogHelper.debug("ChatMessage: " + chatMessage);
        return chatMessage;
    }

    private RoutingResult applyRouting(AsyncPlayerChatEvent event, Player player, String format, String chatMessage) {
        boolean global = false;

        if (Config.RANGEMODE.getBoolean() || Config.BUNGEECORD.getBoolean()) {
            LogHelper.debug("Message starts with prefix (" + Config.RANGEPREFIX.getString() + "): " + chatMessage.startsWith(Config.RANGEPREFIX.getString()));
            if ((Config.RANGEMODE.getBoolean() && chatMessage.startsWith(Config.RANGEPREFIX.getString())) || Config.BUNGEECORD.getBoolean()) {
                LogHelper.debug("Global mode enabled!");
                if (!player.hasPermission("chatex.chat.global")) {
                    player.sendMessage(Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.chat.global"));
                    event.setCancelled(true);
                    return null;
                }

                chatMessage = chatMessage.replaceFirst(Pattern.quote(Config.RANGEPREFIX.getString()), "");
                format = PluginManager.getGlobalMessageFormat(player);
                global = true;

                PlayerUsesGlobalChatEvent globalChatEvent = new PlayerUsesGlobalChatEvent(player, chatMessage);
                Bukkit.getPluginManager().callEvent(globalChatEvent);
                chatMessage = globalChatEvent.getMessage();
                if (globalChatEvent.isCancelled()) {
                    event.setCancelled(true);
                    return null;
                }
            } else if (Config.RANGEMODE.getBoolean()) {
                LogHelper.debug("Range mode enabled!");
                event.getRecipients().clear();
                if (Utils.getLocalRecipients(player).size() == 1 && Config.SHOW_NO_RECEIVER_MSG.getBoolean()) {
                    player.sendMessage(Locales.NO_LISTENING_PLAYERS.getString(player));
                    event.setCancelled(true);
                    return null;
                }

                event.getRecipients().addAll(Utils.getLocalRecipients(player));

                PlayerUsesRangeModeEvent rangeModeEvent = new PlayerUsesRangeModeEvent(player, chatMessage);
                Bukkit.getPluginManager().callEvent(rangeModeEvent);
                chatMessage = rangeModeEvent.getMessage();
                if (rangeModeEvent.isCancelled()) {
                    event.setCancelled(true);
                    return null;
                }
            }
        }

        return new RoutingResult(format, chatMessage, global);
    }

    private void forwardCrossServerMessage(Player player, RoutingResult routing) {
        if (routing.global() && Config.BUNGEECORD.getBoolean()) {
            LogHelper.debug("Local mode & Bungeecord mode enabled! Spreading Cross server message...");
            String msgToSend = Utils.replacePlayerPlaceholders(player, routing.format().replaceAll("%message", Matcher.quoteReplacement(routing.chatMessage())));
            ChannelHandler.getInstance().sendMessage(player, msgToSend);
        }
    }

    private void finalizeMessage(AsyncPlayerChatEvent event, Player player, RoutingResult routing) {
        LogHelper.debug("Replacing Placeholder in format...");
        String format = Utils.replacePlayerPlaceholders(player, routing.format());
        format = Utils.escape(format);
        format = format.replace("%%message", "%2$s");
        LogHelper.debug("Format after replacing: " + format);

        try {
            event.setFormat(format);
        } catch (UnknownFormatConversionException ex) {
            System.out.println(format);
            ChatEx.getInstance().getLogger().severe("Placeholder in format is not allowed!");
            format = format.replaceAll("%\\\\?.*?%", "");
            event.setFormat(format);
        }

        event.setMessage(Utils.translateColorCodes(routing.chatMessage(), player));
        ChatLogger.writeToFile(player, routing.chatMessage());
        LogHelper.debug("Everything done! Method end.");
    }

}

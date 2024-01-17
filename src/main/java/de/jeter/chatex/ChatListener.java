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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final AdManager adManager = Config.ADS_SMART_MANAGER.getBoolean() ? new SmartAdManager() : new SimpleAdManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLowest(final AsyncPlayerChatEvent event) {
        if (Config.PRIORITY.getString().equalsIgnoreCase("LOWEST")) {
            executeChatEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLow(final AsyncPlayerChatEvent event) {
        if (Config.PRIORITY.getString().equalsIgnoreCase("LOW")) {
            executeChatEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNormal(final AsyncPlayerChatEvent event) {
        if (Config.PRIORITY.getString().equalsIgnoreCase("NORMAL")) {
            executeChatEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHigh(final AsyncPlayerChatEvent event) {
        if (Config.PRIORITY.getString().equalsIgnoreCase("HIGH")) {
            executeChatEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHighest(final AsyncPlayerChatEvent event) {
        if (Config.PRIORITY.getString().equalsIgnoreCase("HIGHEST")) {
            executeChatEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitor(final AsyncPlayerChatEvent event) {
        if (Config.PRIORITY.getString().equalsIgnoreCase("MONITOR")) {
            executeChatEvent(event);
        }
    }

    private void executeChatEvent(AsyncPlayerChatEvent event) {
        LogHelper.debug("ChatEvent fired with priority: " + Config.PRIORITY.getString().toUpperCase() + ", ChatEx reacting to it...");
        Player player = event.getPlayer();

        if (!player.hasPermission("chatex.allowchat")) {
            String msg = Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.allowchat");
            player.sendMessage(msg);
            event.setCancelled(true);
            return;
        }

        String format = PluginManager.getInstance().getMessageFormat(event.getPlayer());
        LogHelper.debug("Format: " + format);
        LogHelper.debug("Prefix: " + PluginManager.getInstance().getPrefix(event.getPlayer()));
        LogHelper.debug("Suffix: " + PluginManager.getInstance().getSuffix(event.getPlayer()));

        String chatMessage = event.getMessage();

        if (!AntiSpamManager.getInstance().isAllowed(event.getPlayer())) {
            long remainingTime = AntiSpamManager.getInstance().getRemainingSeconds(event.getPlayer());
            String message = Locales.ANTI_SPAM_DENIED.getString(event.getPlayer()).replaceAll("%time%", remainingTime + "");
            MessageBlockedBySpamManagerEvent messageBlockedBySpamManagerEvent = new MessageBlockedBySpamManagerEvent(event.getPlayer(), chatMessage, message, remainingTime);
            Bukkit.getPluginManager().callEvent(messageBlockedBySpamManagerEvent);
            event.setCancelled(!messageBlockedBySpamManagerEvent.isCancelled());
            if (!messageBlockedBySpamManagerEvent.isCancelled()) {
                event.getPlayer().sendMessage(messageBlockedBySpamManagerEvent.getPluginMessage());
                return;
            }
            chatMessage = messageBlockedBySpamManagerEvent.getMessage();
        }
        AntiSpamManager.getInstance().put(player);

        LogHelper.debug("Player did not activate the AntiSpam. Continuing...");

        if (adManager.checkForAds(chatMessage, player)) {
            String message = Locales.MESSAGES_AD.getString(null).replaceAll("%perm", "chatex.bypassads");
            MessageBlockedByAdManagerEvent messageBlockedByAdManagerEvent = new MessageBlockedByAdManagerEvent(player, chatMessage, message);
            Bukkit.getPluginManager().callEvent(messageBlockedByAdManagerEvent);
            chatMessage = messageBlockedByAdManagerEvent.getMessage();
            event.setCancelled(!messageBlockedByAdManagerEvent.isCancelled());
            if (!messageBlockedByAdManagerEvent.isCancelled()) {
                event.getPlayer().sendMessage(messageBlockedByAdManagerEvent.getPluginMessage());
                return;
            }
        }

        LogHelper.debug("Player did not activate the AdBlocker. Continuing...");

        for(String block : Config.BLOCKED_WORDS.getStringList()) {
            if(chatMessage.toLowerCase().contains(block.toLowerCase())) {
                LogHelper.debug("Player activated wordblocker! ChatMessage: " + chatMessage + " contains blockedWord: " + block);
                String message = Locales.MESSAGES_BLOCKED.getString(null);
                MessageContainsBlockedWordEvent messageContainsBlockedWordEvent = new MessageContainsBlockedWordEvent(player, chatMessage, message);
                Bukkit.getPluginManager().callEvent(messageContainsBlockedWordEvent);
                event.setCancelled(!messageContainsBlockedWordEvent.isCancelled());
                chatMessage = messageContainsBlockedWordEvent.getMessage();
                if (!messageContainsBlockedWordEvent.isCancelled()) {
                    event.getPlayer().sendMessage(messageContainsBlockedWordEvent.getPluginMessage());
                    return;
                }
            }
        }

        LogHelper.debug("Player did not use a blocked word. Continuing...");
        LogHelper.debug("ChatMessage: " + chatMessage);
        boolean global = false;

        if (Config.RANGEMODE.getBoolean() || Config.BUNGEECORD.getBoolean()) {
            LogHelper.debug("Message starts with prefix (" + Config.RANGEPREFIX.getString() + "): " + chatMessage.startsWith(Config.RANGEPREFIX.getString()));
            if ((Config.RANGEMODE.getBoolean() && chatMessage.startsWith(Config.RANGEPREFIX.getString())) || Config.BUNGEECORD.getBoolean()) {
                LogHelper.debug("Global mode enabled!");
                if (player.hasPermission("chatex.chat.global")) {
                    chatMessage = chatMessage.replaceFirst(Pattern.quote(Config.RANGEPREFIX.getString()), "");
                    format = PluginManager.getInstance().getGlobalMessageFormat(player);
                    global = true;

                    PlayerUsesGlobalChatEvent playerUsesGlobalChatEvent = new PlayerUsesGlobalChatEvent(player, chatMessage);
                    Bukkit.getPluginManager().callEvent(playerUsesGlobalChatEvent);
                    chatMessage = playerUsesGlobalChatEvent.getMessage();
                    if (playerUsesGlobalChatEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    player.sendMessage(Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.chat.global"));
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (Config.RANGEMODE.getBoolean()) {
                    LogHelper.debug("Range mode enabled!");
                    event.getRecipients().clear();
                    if (Utils.getLocalRecipients(player).size() == 1 && Config.SHOW_NO_RECEIVER_MSG.getBoolean()) {
                        player.sendMessage(Locales.NO_LISTENING_PLAYERS.getString(player));
                        event.setCancelled(true);
                        return;
                    } else {
                        event.getRecipients().addAll(Utils.getLocalRecipients(player));

                        PlayerUsesRangeModeEvent playerUsesRangeModeEvent = new PlayerUsesRangeModeEvent(player, chatMessage);
                        Bukkit.getPluginManager().callEvent(playerUsesRangeModeEvent);
                        chatMessage = playerUsesRangeModeEvent.getMessage();
                        if (playerUsesRangeModeEvent.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (global && Config.BUNGEECORD.getBoolean()) {
            LogHelper.debug("Local mode & Bungeecord mode enabled! Spreading Cross server message...");
            String msgToSend = Utils.replacePlayerPlaceholders(player, format.replaceAll("%message", Matcher.quoteReplacement(chatMessage)));
            ChannelHandler.getInstance().sendMessage(player, msgToSend);
        }

        LogHelper.debug("Replacing Placeholder in format...");
        format = Utils.replacePlayerPlaceholders(player, format);
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

        event.setMessage(Utils.translateColorCodes(chatMessage, player));
        ChatLogger.writeToFile(player, chatMessage);
        LogHelper.debug("Everything done! Method end.");
    }

}

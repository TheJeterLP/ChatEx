/*
 * This file is part of ChatEx
 * Copyright (C) 2020 ChatEx Team
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

import de.jeter.chatex.api.events.MessageBlockedByAdManagerEvent;
import de.jeter.chatex.api.events.MessageBlockedBySpamManagerEvent;
import de.jeter.chatex.api.events.MessageContainsBlockedWordEvent;
import de.jeter.chatex.api.events.PlayerUsesRangeModeEvent;
import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.*;
import de.jeter.chatex.utils.adManager.AdManager;
import de.jeter.chatex.utils.adManager.SimpleAdManager;
import de.jeter.chatex.utils.adManager.SmartAdManager;
import org.bukkit.Bukkit;
import java.util.UnknownFormatConversionException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    
    private final AdManager adManager = Config.ADS_SMART_MANAGER.getBoolean() ? new SmartAdManager() : new SimpleAdManager();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!player.hasPermission("chatex.allowchat")) {
            String msg = Locales.COMMAND_RESULT_NO_PERM.getString(event.getPlayer()).replaceAll("%perm", "chatex.allowchat");
            event.getPlayer().sendMessage(msg);
            event.setCancelled(true);
            return;
        }

        if (!AntiSpamManager.getInstance().isAllowed(event.getPlayer())) {
            long remainingTime = AntiSpamManager.getInstance().getRemaingSeconds(event.getPlayer());
            String message = Locales.ANTI_SPAM_DENIED.getString(event.getPlayer()).replaceAll("%time%", remainingTime + "");
            MessageBlockedBySpamManagerEvent messageBlockedBySpamManagerEvent = new MessageBlockedBySpamManagerEvent(event.getPlayer(),event.getMessage(), message,remainingTime);
            Bukkit.getPluginManager().callEvent(messageBlockedBySpamManagerEvent);
            event.setCancelled(!messageBlockedBySpamManagerEvent.isCancelled());
            if(!messageBlockedBySpamManagerEvent.isCancelled()){
                event.getPlayer().sendMessage(messageBlockedBySpamManagerEvent.getPluginMessage());
                return;
            }
        }
        AntiSpamManager.getInstance().put(event.getPlayer());

        String format = PluginManager.getInstance().getMessageFormat(event.getPlayer());

        String chatMessage = event.getMessage();

        if (adManager.checkForAds(chatMessage, player)) {
            String message = Locales.MESSAGES_AD.getString(null).replaceAll("%perm", "chatex.bypassads");
            MessageBlockedByAdManagerEvent messageBlockedByAdManagerEvent = new MessageBlockedByAdManagerEvent(player, chatMessage, message);
            Bukkit.getPluginManager().callEvent(messageBlockedByAdManagerEvent);
            event.setMessage(messageBlockedByAdManagerEvent.getMessage());
            event.setCancelled(!messageBlockedByAdManagerEvent.isCancelled());
            if(!messageBlockedByAdManagerEvent.isCancelled()){
                event.getPlayer().sendMessage(messageBlockedByAdManagerEvent.getPluginMessage());
                return;
            }
        }

        if (Utils.checkForBlocked(event.getMessage())) {
            String message = Locales.MESSAGES_BLOCKED.getString(null);
            MessageContainsBlockedWordEvent messageContainsBlockedWordEvent = new MessageContainsBlockedWordEvent(player, chatMessage, message);
            Bukkit.getPluginManager().callEvent(messageContainsBlockedWordEvent);
            event.setCancelled(!messageContainsBlockedWordEvent.isCancelled());
            if(!messageContainsBlockedWordEvent.isCancelled()){
                event.getPlayer().sendMessage(messageContainsBlockedWordEvent.getPluginMessage());
                return;
            }
        }

        boolean global = false;
        if (Config.RANGEMODE.getBoolean() || Config.BUNGEECORD.getBoolean()) {
            if (chatMessage.startsWith("!")) {
                if (player.hasPermission("chatex.chat.global")) {
                    chatMessage = chatMessage.replaceFirst("!", "");
                    format = PluginManager.getInstance().getGlobalMessageFormat(player);
                    global = true;
                } else {
                    player.sendMessage(Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.chat.global"));
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (Config.RANGEMODE.getBoolean()) {
                    event.getRecipients().clear();
                    if (Utils.getLocalRecipients(player).size() == 1 && Config.SHOW_NO_RECEIVER_MSG.getBoolean()) {
                        player.sendMessage(Locales.NO_LISTENING_PLAYERS.getString(player));
                        event.setCancelled(true);
                        return;
                    } else {
                        event.getRecipients().addAll(Utils.getLocalRecipients(player));
                    }
                }
            }
        }

        if (global && Config.BUNGEECORD.getBoolean()) {
            PlayerUsesRangeModeEvent playerUsesRangeModeEvent = new PlayerUsesRangeModeEvent(player, chatMessage);
            Bukkit.getPluginManager().callEvent(playerUsesRangeModeEvent);
            if(!playerUsesRangeModeEvent.isCancelled()){
                String msgToSend = Utils.replacePlayerPlaceholders(player, format.replaceAll("%message", Utils.translateColorCodes(playerUsesRangeModeEvent.getMessage(), player)));
                ChannelHandler.getInstance().sendMessage(player, msgToSend);
            }
        }

        format = format.replace("%message", "%2$s");
        format = Utils.replacePlayerPlaceholders(player, format);

        try {
            event.setFormat(format);
        } catch (UnknownFormatConversionException ex) {
            ChatEx.getInstance().getLogger().severe("Placeholder in format is not allowed!");
            format = format.replaceAll("%\\\\?.*?%", "");
            event.setFormat(format);
        }

        event.setMessage(Utils.translateColorCodes(chatMessage, player));
        ChatLogger.writeToFile(player, chatMessage);
    }

}

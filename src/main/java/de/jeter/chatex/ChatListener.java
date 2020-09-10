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

import de.jeter.chatex.pipe.*;
import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.ChatLogger;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.Utils;
import de.jeter.chatex.utils.adManager.SimpleAdManager;
import de.jeter.chatex.utils.adManager.SmartAdManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UnknownFormatConversionException;

public class ChatListener implements Listener {
    private final ChatPipe chatPipe;

    public ChatListener() {
        this.chatPipe = new ChatPipe();

        this.chatPipe.addLast(new ChatPermissionHandler());
        this.chatPipe.addLast(new SpamManagerHandler());
        this.chatPipe.addLast(new AdHandler(
                Config.ADS_SMART_MANAGER.getBoolean() ? new SmartAdManager() : new SimpleAdManager()
        ));
        this.chatPipe.addLast(new BlockedMessageHandler());
        this.chatPipe.addLast(new BungeeHandler());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        String format = PluginManager.getInstance().getMessageFormat(event.getPlayer());
        NonCheckingAsyncChatEvent nonCheckingAsyncChatEvent = new NonCheckingAsyncChatEvent(event.isAsynchronous(), event.getPlayer(), event.getMessage(), event.getRecipients(), format);
        chatPipe.handle(nonCheckingAsyncChatEvent);
        format = Utils.replacePlayerPlaceholders(player, nonCheckingAsyncChatEvent.getFormat());
        format = Utils.escape(format);
        format = format.replace("%%message", "%2$s");

        event.setCancelled(nonCheckingAsyncChatEvent.isCancelled());

        try {
            event.setFormat(format);
        } catch (UnknownFormatConversionException ex) {
            System.out.println(format);
            ChatEx.getInstance().getLogger().severe("Placeholder in format is not allowed!");
            format = format.replaceAll("%\\\\?.*?%", "");
            event.setFormat(format);
        }

        event.setMessage(Utils.translateColorCodes(nonCheckingAsyncChatEvent.getMessage(), player));
        ChatLogger.writeToFile(player, nonCheckingAsyncChatEvent.getMessage());
    }

}

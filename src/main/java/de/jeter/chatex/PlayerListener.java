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

import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.Utils;
import de.jeter.updatechecker.Result;
import de.jeter.updatechecker.UpdateChecker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        if (Config.CHANGE_JOIN_AND_QUIT.getBoolean()) {
            String msg = Locales.PLAYER_JOIN.getString(e.getPlayer());
            e.setJoinMessage(Utils.replacePlayerPlaceholders(e.getPlayer(), msg));
        }

        if (Config.CHANGE_TABLIST_NAME.getBoolean()) {
            String name = Config.TABLIST_FORMAT.getString();
            name = Utils.replacePlayerPlaceholders(e.getPlayer(), name);
            e.getPlayer().setPlayerListName(name);
        }

        UpdateChecker checker = ChatEx.getInstance().getUpdateChecker();

        if (Config.CHECK_UPDATE.getBoolean() && e.getPlayer().hasPermission("chatex.notifyupdate") && checker != null) {
            if (checker.getResult() == Result.UPDATE_FOUND) {
                try {
                    Class.forName("net.md_5.bungee.api.chat.TextComponent");
                    TextComponent msg = new TextComponent(Locales.UPDATE_FOUND.getString(null).replaceAll("%oldversion", ChatEx.getInstance().getDescription().getVersion()).replaceAll("%newversion", ChatEx.getInstance().getUpdateChecker().getLatestRemoteVersion()));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aClick to download")));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, checker.getDownloadLink()));
                    e.getPlayer().spigot().sendMessage(msg);
                } catch (ClassNotFoundException ex) {
                    e.getPlayer().sendMessage(Locales.UPDATE_FOUND.getString(null).replaceAll("%oldversion", ChatEx.getInstance().getDescription().getVersion()).replaceAll("%newversion", ChatEx.getInstance().getUpdateChecker().getLatestRemoteVersion()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(final PlayerQuitEvent e) {
        if (!Config.CHANGE_JOIN_AND_QUIT.getBoolean()) {
            return;
        }
        String msg = Locales.PLAYER_QUIT.getString(e.getPlayer());
        e.setQuitMessage(Utils.replacePlayerPlaceholders(e.getPlayer(), msg));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKick(final PlayerKickEvent e) {
        if (!Config.CHANGE_JOIN_AND_QUIT.getBoolean()) {
            return;
        }
        String msg = Locales.PLAYER_KICK.getString(e.getPlayer());
        e.setLeaveMessage(Utils.replacePlayerPlaceholders(e.getPlayer(), msg));
    }

}

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
package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import de.jeter.chatex.EssentialsAFKListener;
import de.jeter.chatex.plugins.PluginManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String translateColorCodes(String string, Player p) {
        return p.hasPermission("chatex.chat.color") ? replaceColors(string) : string;
    }

    public static String replaceColors(String message) {
        message = RGBColors.translateCustomColorCodes(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<Player> getLocalRecipients(Player sender) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new ArrayList<>();

        double squaredDistance = Math.pow(Config.RANGE.getInt(), 2);
        for (Player recipient : sender.getWorld().getPlayers()) {
            if (Config.RANGE.getInt() > 0 && (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance)) {
                continue;
            }
            recipients.add(recipient);
        }

        return recipients;
    }

    public static String replacePlayerPlaceholders(Player player, String format) {
        if (player == null) {
            return format;
        }
        String result = format;

        if (HookManager.checkPlaceholderAPI()) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        if ((HookManager.checkEssentials() || HookManager.checkPurpur()) && Config.AFK_PLACEHOLDER.getBoolean()) {
            result = result.replace("%afk", "");
        }

        result = result.replace("%displayname", player.getDisplayName());
        result = result.replace("%prefix", PluginManager.getInstance().getPrefix(player));
        result = result.replace("%suffix", PluginManager.getInstance().getSuffix(player));
        result = result.replace("%player", player.getName());
        result = result.replace("%world", player.getWorld().getName());
        result = result.replace("%group", PluginManager.getInstance().getGroupNames(player).length > 0 ? PluginManager.getInstance().getGroupNames(player)[0] : "none");
        result = replaceColors(result);

        return result;
    }

    public static String escape(String string) {
        return string.replace("%", "%%");
    }

    public static boolean checkForBypassString(String message) {
        for (String block : Config.ADS_BYPASS.getStringList()) {
            if (message.toLowerCase().contains(block.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static void notifyOps(String msg) {
        for (Player op : ChatEx.getInstance().getServer().getOnlinePlayers()) {
            if (!op.hasPermission("chatex.notifyad")) {
                continue;
            }
            op.sendMessage(msg);
        }
    }

}

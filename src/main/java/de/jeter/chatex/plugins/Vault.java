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
package de.jeter.chatex.plugins;

import de.jeter.chatex.utils.Config;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault implements PermissionsPlugin {

    private static Chat chat = null;

    @Override
    public String getPrefix(Player p) {
        if (!Config.MULTIPREFIXES.getBoolean()) {
            return chat.getPlayerPrefix(p.getWorld().getName(), p);
        }
        StringBuilder finalPrefix = new StringBuilder();
        int i = 0;
        for (String group : chat.getPlayerGroups(p)) {
            String groupPrefix = chat.getGroupPrefix(p.getWorld(), group);
            if (groupPrefix != null && !groupPrefix.isEmpty()) {
                if (i > 1) {
                    finalPrefix.append(" ");
                }
                finalPrefix.append(groupPrefix);
                i++;
            }
        }
        return finalPrefix.toString();
    }

    @Override
    public String getSuffix(Player p) {
        if (!Config.MULTIPREFIXES.getBoolean()) {
            return chat.getPlayerSuffix(p.getWorld().getName(), p);
        }
        StringBuilder finalSuffix = new StringBuilder();
        int i = 0;
        for (String group : chat.getPlayerGroups(p)) {
            String groupSuffix = chat.getGroupSuffix(p.getWorld(), group);
            if (groupSuffix != null && !groupSuffix.isEmpty()) {
                if (i > 1) {
                    finalSuffix.append(" ");
                }
                i++;
                finalSuffix.append(groupSuffix);
            }
        }
        return finalSuffix.toString();
    }

    @Override
    public String[] getGroupNames(Player p) {
        return chat.getPlayerGroups(p);
    }

    @Override
    public String getName() {
        return chat.getName();
    }

    @Override
    public String getMessageFormat(Player p) {
        return Config.FORMAT.getString();
    }

    @Override
    public String getGlobalMessageFormat(Player p) {
        return Config.GLOBALFORMAT.getString();
    }

    protected static boolean setupChat() {
        try {
            RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            if (chatProvider != null) {
                chatProvider.getProvider();
                chat = chatProvider.getProvider();
                return chat.isEnabled();
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }
}

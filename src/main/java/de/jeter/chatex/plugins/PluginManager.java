/*
 * This file is part of ChatEx
 * Copyright (C) 2022 ChatEx Team
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.jeter.chatex.plugins;

import de.jeter.chatex.ChatEx;
import de.jeter.chatex.EssentialsAFKListener;
import de.jeter.chatex.PurpurAFKListener;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.HookManager;
import de.jeter.chatex.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Picks and delegates to whichever {@link PermissionsPlugin} handler is available on this
 * server (LuckPerms, Vault or a no-op fallback). Purely static: there is no per-instance state,
 * so unlike an earlier version of this class it no longer wraps that in a redundant singleton.
 */
public class PluginManager {

    private static PermissionsPlugin handler;

    public static void load() {
        if (HookManager.checkLuckperms()) {
            handler = new LuckPerms();
        } else if (HookManager.checkVault() && Vault.setupChat()) {
            handler = new Vault();
        } else {
            handler = new Nothing();
        }
        ChatEx.getInstance().getLogger().info("Successfully hooked into: " + handler.getName());

        if (HookManager.checkPlaceholderAPI()) {
            ChatEx.getInstance().getLogger().info("Hooked into PlaceholderAPI");
        }

        if (Config.AFK_PLACEHOLDER.getBoolean()) {
            if (HookManager.checkEssentials()) {
                ChatEx.getInstance().getLogger().info("Hooked into Essentials");
                ChatEx.getInstance().getServer().getPluginManager().registerEvents(new EssentialsAFKListener(), ChatEx.getInstance());
            } else if (HookManager.checkPurpur()) {
                ChatEx.getInstance().getLogger().info("Hooked into Purpur");
                ChatEx.getInstance().getServer().getPluginManager().registerEvents(new PurpurAFKListener(), ChatEx.getInstance());
            } else {
                ChatEx.getInstance().getLogger().warning("Error while enabling AFK placeholder, neither essentials nor purpur were found!");
            }
        }
    }

    public static String getName() {
        return handler.getName();
    }

    public static String getPrefix(Player p) {
        return handler.getPrefix(p);
    }

    public static String getSuffix(Player p) {
        return handler.getSuffix(p);
    }

    public static String[] getGroupNames(Player p) {
        return handler.getGroupNames(p);
    }

    public static String getMessageFormat(Player p) {
        return Utils.replaceColors(handler.getMessageFormat(p));
    }

    public static String getGlobalMessageFormat(Player p) {
        return Utils.replaceColors(handler.getGlobalMessageFormat(p));
    }
}

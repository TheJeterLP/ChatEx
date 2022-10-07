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
package de.jeter.chatex.api;

import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.AntiSpamManager;
import org.bukkit.entity.Player;

public class ChatExAPI {

    public String getPermissionHandlerName() {
        return PluginManager.getInstance().getName();
    }

    public AntiSpamManager getAntiSpamManager() {
        return AntiSpamManager.getInstance();
    }

    public String getPrefix(Player p) {
        return PluginManager.getInstance().getPrefix(p);
    }

    public String getSuffix(Player p) {
        return PluginManager.getInstance().getSuffix(p);
    }

    public String[] getGroupNames(Player p) {
        return PluginManager.getInstance().getGroupNames(p);
    }

    public String getMessageFormat(Player p) {
        return PluginManager.getInstance().getMessageFormat(p);
    }

    public String getGlobalMessageFormat(Player p) {
        return PluginManager.getInstance().getGlobalMessageFormat(p);
    }
}
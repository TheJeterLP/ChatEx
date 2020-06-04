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

import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.ChatLogger;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

public class ChatEx extends JavaPlugin {

    private static ChatEx INSTANCE;
    private UpdateChecker updatechecker = null;

    @Override
    public void onEnable() {
        INSTANCE = this;

        Config.load();
        Locales.load();
        PluginManager.load();
        ChatLogger.load();

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("chatex").setExecutor(new CommandHandler());

        if (Config.CHECK_UPDATE.getBoolean()) {
            updatechecker = new UpdateChecker(this, 71041);
        }

        ChannelHandler.load();

        if (Config.B_STATS.getBoolean()) {
            Metrics metrics = new Metrics(this, 7744);
            getLogger().info("Thanks for using bstats, it was enabled!");
        }

        getLogger().info("is now enabled!");
    }

    @Override
    public void onDisable() {
        ChatLogger.close();
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("is now disabled!");
    }

    public static ChatEx getInstance() {
        return INSTANCE;
    }

    public UpdateChecker getUpdateChecker() {
        return this.updatechecker;
    }
}

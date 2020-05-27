package de.jeter.chatex;

import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.ChatLogger;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheJeterLP
 */
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

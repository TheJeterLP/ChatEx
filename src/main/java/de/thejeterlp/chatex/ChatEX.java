package de.thejeterlp.chatex;

import de.thejeterlp.chatex.utils.Locales;
import de.thejeterlp.chatex.plugins.PluginManager;
import de.thejeterlp.chatex.utils.Config;
import de.thejeterlp.chatex.utils.UpdateChecker;
import de.thejeterlp.chatex.utils.UpdateChecker.UpdateType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheJeterLP
 */
public class ChatEX extends JavaPlugin {

    private static ChatEX INSTANCE;
    private UpdateChecker updatechecker = null;

    @Override
    public void onEnable() {
        INSTANCE = this;

        Config.load();
        Locales.load();
        PluginManager.load();

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("chatex").setExecutor(new CommandHandler());

        if (Config.CHECK_UPDATE.getBoolean()) {
            UpdateType ud = Config.DOWNLOAD_UPDATE.getBoolean() ? UpdateChecker.UpdateType.CHECK_DOWNLOAD : UpdateChecker.UpdateType.VERSION_CHECK;
            updatechecker = new UpdateChecker(this, 71041, this.getFile(), ud);
        }

        ChannelHandler.load();

        getLogger().info("is now enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("is now disabled!");
    }

    public static ChatEX getInstance() {
        return INSTANCE;
    }

    public UpdateChecker getUpdateChecker() {
        return this.updatechecker;
    }
}

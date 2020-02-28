package de.thejeterlp.chatex;

import de.thejeterlp.chatex.utils.Locales;
import de.thejeterlp.chatex.command.CommandManager;
import de.thejeterlp.chatex.plugins.PermissionsPlugin;
import de.thejeterlp.chatex.plugins.PluginManager;
import de.thejeterlp.chatex.utils.Config;
import de.thejeterlp.chatex.utils.UpdateChecker;
import java.io.File;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheJeterLP
 */
public class ChatEX extends JavaPlugin {

    private static ChatEX INSTANCE;
    private static PluginManager manager;
    private CommandManager cmanager;
    private UpdateChecker updatechecker = null;

    @Override
    public void onEnable() {
        try {
            INSTANCE = this;
            Config.load();
            debug("Loaded Config!");
            if (!Config.ENABLE.getBoolean()) {
                getServer().getPluginManager().disablePlugin(this);
                getLogger().info("disabled, check config!");
                return;
            }
            Locales.load();

            File localeFolder = new File(getDataFolder(), "locales");

            if (!new File(localeFolder, Config.LOCALE.getString() + "_readme.txt").exists()) {
                debug("Saving readme to " + localeFolder.getAbsolutePath());
                saveResource("locales" + File.separator + Config.LOCALE.getString() + "_readme.txt", true);
            }
            manager = new PluginManager();
            getLogger().info("Successfully hooked into: " + PluginManager.getInstance().getName());
            debug("registering Listener...");
            ChatListener listener = new ChatListener();
            listener.register();
            cmanager = new CommandManager(this);
            cmanager.registerClass(ChatExCommandHandler.class);

            if (Config.CHECK_UPDATE.getBoolean()) {
                getLogger().info("UpdateChecker enabled in config.");
                updatechecker = new UpdateChecker(this, 71041, this.getFile(), UpdateChecker.UpdateType.CHECK_DOWNLOAD);
            } else {
                getLogger().warning("UpdateChecker disabled in config!");
            }

            getLogger().info("is now enabled!");
        } catch (Exception e) {
            getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("is now disabled!");
    }

    public static ChatEX getInstance() {
        return INSTANCE;
    }

    public static PermissionsPlugin getManager() {
        return manager;
    }

    public static void debug(String message) {
        if (!Config.DEBUG.getBoolean()) {
            return;
        }
        String output = "[DEBUG] " + message;
        getInstance().getLogger().info(output);
    }

    public UpdateChecker getUpdateChecker() {
        return this.updatechecker;
    }
}

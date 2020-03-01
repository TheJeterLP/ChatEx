package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import java.io.File;
import java.io.IOException;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public enum Locales {

    COMMAND_RELOAD_DESCRIPTION("Commands.Reload.Description", "Reloads the plugin and its configuration."),
    COMMAND_CLEAR_DESCRIPTION("Commands.Clear.Description", "Clears the chat."),
    COMMAND_CLEAR_CONSOLE("Commands.Clear.Console", "CONSOLE"),
    COMMAND_CLEAR_UNKNOWN("Commands.Clear.Unknown", "UNKNOWN"),
    MESSAGES_RELOAD("Messages.Commands.Reload.Success", "&aConfig was reloaded."),
    MESSAGES_CLEAR("Messages.Commands.Clear.Success", "&aThe chat has been cleared by "),
    MESSAGES_AD("Messages.Chat.AdDetected", "&4[ERROR] &7Advertising is not allowed! &c(%perm)"),
    MESSAGES_BLOCKED("Messages.Chat.BlockedWord", "&4[ERROR] &7You tried to write a word that is blocked!"),
    MESSAGES_AD_NOTIFY("Messages.Chat.AdNotify", "&c%player tried to write an ad in chat. He wrote: \n&a %message"),
    COMMAND_RESULT_NO_PERM("Messages.CommandResult.NoPermission", "&4[ERROR] &7You don't have permission for this! &c(%perm)"),
    COMMAND_RESULT_WRONG_USAGE("Messages.CommandResult.WrongUsage", "&c[ERROR] &7Wrong usage! Please type &6/%cmd help&7!"),
    PLAYER_JOIN("Messages.Player.Join", "%prefix%displayname%suffix &ejoined the game!"),
    PLAYER_KICK("Messages.Player.Kick", "%prefix%displayname%suffix &ewas kicked from the game!"),
    PLAYER_QUIT("Messages.Player.Quit", "%prefix%displayname%suffix &eleft the game!"),
    NO_LISTENING_PLAYERS("Messages.Chat.NoOneListens", "&cNo players are near you to hear you talking! Use the ranged mode to chat."),
    UPDATE_FOUND("Messages.UpdateFound", "&a[ChatEx]&7 A new update has been found on SpigotMC. Current version: %oldversion New version: %newversion"),
    UPDATE_DOWNLOADED("Messages.UpdateDownloaded", "&a[ChatEx]&7 A new update was downloaded and installed from SpigotMC. It will apply on the next server restart.");

    private final String value;
    private final String path;
    private static YamlConfiguration cfg;
    private static final File localeFolder = new File(ChatEx.getInstance().getDataFolder(), "locales");
    private static File f;

    private Locales(String path, String val) {
        this.path = path;
        this.value = val;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultValue() {
        return value;
    }

    public String getString(Player p) {
        String ret = cfg.getString(path).replaceAll("&((?i)[0-9a-fk-or])", "§$1");

        if (HookManager.checkPlaceholderAPI() && p != null) {
            ret = PlaceholderAPI.setPlaceholders(p, ret);
        }

        return ret;
    }

    public static void load() {
        localeFolder.mkdirs();
        f = new File(localeFolder, Config.LOCALE.getString() + ".yml");
        if (!f.exists()) {
            try {
                ChatEx.getInstance().saveResource("locales" + File.separator + Config.LOCALE.getString() + ".yml", true);
                File locale = new File(ChatEx.getInstance().getDataFolder(), Config.LOCALE.getString() + ".yml");
                if (locale.exists()) {
                    locale.delete();
                }
                reload(false);
            } catch (IllegalArgumentException ex) {
                reload(false);
                try {
                    for (Locales c : values()) {
                        if (!cfg.contains(c.getPath())) {
                            c.set(c.getDefaultValue(), false);
                        }
                    }
                    cfg.save(f);
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        } else {
            reload(false);
            try {
                for (Locales c : values()) {
                    if (!cfg.contains(c.getPath())) {
                        c.set(c.getDefaultValue(), false);
                    }
                }
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }        
    }

    public void set(Object value, boolean save) throws IOException {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    public static void reload(boolean complete) {
        if (!complete) {
            cfg = YamlConfiguration.loadConfiguration(f);
            return;
        }
        load();
    }
}

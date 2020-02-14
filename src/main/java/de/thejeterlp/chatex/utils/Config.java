package de.thejeterlp.chatex.utils;

import de.thejeterlp.chatex.ChatEX;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author TheJeterLP
 */
public enum Config {

    ENABLE("enable", true, "Should the plugin be enabled?"),
    CHECK_UPDATE("check-for-updates", true, "Should the plugin check for updates by itself?"),
    FORMAT("message-format", "%prefix%displayname%suffix: %message", "The standard message-format."),
    GLOBALFORMAT("global-message-format", "&9[%world] %prefix%displayname%suffix: &e%message", "The message-format if ranged-mode is enabled."),
    RANGEMODE("ranged-mode", false, "Should the ranged-mode be enabled?"),
    RANGE("chat-range", 100.0, "The range to talk to other players."),
    MULTIPREFIXES("multi-prefixes", false, "Should the multi-prefixes be enabled? See readme.txt for more info."),
    MULTISUFFIXES("multi-suffixes", false, "Should the multi-suffixes be enabled? See readme.txt for more info."),
    LOGCHAT("logChat", true, "Should the chat be logged?"),
    DEBUG("Debug", false, "Enables debug mode. Enable this if you get a bug."),
    LOCALE("Locale", "en-EN", "Which language do you want? (You can choose betwenn de-DE, fr-FR, pt-BR and en-EN by default.)"),
    ADS_ENABLED("Ads.Enabled", true, "Should we check for ads?"),
    ADS_BYPASS("Ads.Bypass", Arrays.asList("127.0.0.1", "my-domain.com"), "A list with allowed ips or domains."),
    ADS_LOG("Ads.Log", true, "Should the ads be loged in a file?"),
    BLOCKED_WORDS("BlockedWords", new ArrayList<String>(), "A list of words that should be blocked."),
    CHANGE_TABLIST_NAME("Tablist.Change", true, "Do you want to have the prefixes and suffixes in the tablist?"),
    TABLIST_FORMAT("Tablist.format", "%prefix%displayname%suffix", "The format of the tablist name"),
    CHANGE_JOIN_AND_QUIT("Messages.JoinAndQuit.Enabled", false, "Do you want to change the join and the quit messages?");

    private final Object value;
    private final String path;
    private final String description;
    private static YamlConfiguration cfg;
    private static final File f = new File(ChatEX.getInstance().getDataFolder(), "config.yml");

    private Config(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Object getDefaultValue() {
        return value;
    }

    public boolean getBoolean() {
        return cfg.getBoolean(path);
    }

    public double getDouble() {
        return cfg.getDouble(path);
    }

    public String getString() {
        return Utils.replaceColors(cfg.getString(path));
    }

    public List<String> getStringList() {
        return cfg.getStringList(path);
    }

    public static void load() {
        ChatEX.getInstance().getDataFolder().mkdirs();
        reload(false);
        String header = "";
        for (Config c : values()) {
            header += c.getPath() + ": " + c.getDescription() + System.lineSeparator();
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header);
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void set(Object value, boolean save) {
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

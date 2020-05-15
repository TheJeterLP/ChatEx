package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author TheJeterLP
 */
public enum Config {

    CHECK_UPDATE("check-for-updates", true, "Should the plugin check for updates by itself?"),
    BUNGEECORD("bungeecord", false, "If you use bungeecord, players can chat cross-server wide with the range mode (! in front of the message)."),
    FORMAT("message-format", "%prefix%player%suffix: %message", "The standard message-format."),
    GLOBALFORMAT("global-message-format", "&9[%world] %prefix%player%suffix: &e%message", "The message-format if ranged-mode is enabled."),
    MULTIPREFIXES("multi-prefixes", false, "Should the multi-prefixes be enabled?"),
    MULTISUFFIXES("multi-suffixes", false, "Should the multi-suffixes be enabled?"),
    RANGEMODE("ranged-mode", false, "Should the ranged-mode be enabled?"),
    SHOW_NO_RECEIVER_MSG("show-no-players-near", true, "Should we check if any player would receiver your chat message?"),
    RANGE("chat-range", 100, "The range to talk to other players. Set to -1 to enable world-wide-chat"),
    LOGCHAT("logChat", false, "Should the chat be logged?"),
    DEBUG("debug", false, "Should the debug log be enabled?"),
    LOCALE("Locale", "en-EN", "Which language do you want? (You can choose betwenn de-DE, fr-FR, pt-BR and en-EN by default.)"),
    ADS_ENABLED("Ads.Enabled", true, "Should we check for ads?"),
    ADS_BYPASS("Ads.Bypass", Arrays.asList("127.0.0.1", "my-domain.com"), "A list with allowed ips or domains."),
    ADS_THRESHOLD("Ads.Threshold.Block", 0.25,"The threshold required to cancel a message, stacks per message  error = error+(error over length) "),
    ADS_REDUCE_THRESHOLD("Ads.Threshold.ReduceThreshold", 0.1,"How much threshold is removed per message"),
    ADS_MAX_LENGTH("Ads.Threshold.MaxLinkLength", 10, "What the max detected link length is smaller = less detections, bigger = messages get blocked faster but more false positives"),
    ADS_LOG("Ads.Log", true, "Should the ads be logged in a file?"),
    ANTISPAM_SECONDS("AntiSpam.Seconds", 5, "The delay between player messages to prevent spam"),
    ANTISPAM_ENABLED("AntiSpam.Enable", true, "Should antispam be enabled?"),
    BLOCKED_WORDS("BlockedWords", Arrays.asList("shit", "@everyone"), "A list of words that should be blocked."),
    CHANGE_TABLIST_NAME("Tablist.Change", true, "Do you want to have the prefixes and suffixes in the tablist?"),
    TABLIST_FORMAT("Tablist.format", "%prefix%player%suffix", "The format of the tablist name"),
    CHANGE_JOIN_AND_QUIT("Messages.JoinAndQuit.Enabled", false, "Do you want to change the join and the quit messages?");   

    private final Object value;
    private final String path;
    private final String description;
    private static YamlConfiguration cfg;
    private static final File f = new File(ChatEx.getInstance().getDataFolder(), "config.yml");

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

    public int getInt() {
        return cfg.getInt(path);
    }

    public String getString() {
        return Utils.replaceColors(cfg.getString(path));
    }

    public List<String> getStringList() {
        return cfg.getStringList(path);
    }

    public static void load() {
        ChatEx.getInstance().getDataFolder().mkdirs();
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

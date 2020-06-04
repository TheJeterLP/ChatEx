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
package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public enum Config {

    CHECK_UPDATE("check-for-updates", true, "Should the plugin check for updates by itself?"),
    B_STATS("enable-bstats", true, "Do you want to use bstats?"),
    BUNGEECORD("bungeecord", false, "If you use bungeecord, players can chat cross-server wide with the range mode (! in front of the message)."),
    FORMAT("message-format", "%prefix%player%suffix: %message", "The standard message-format."),
    GLOBALFORMAT("global-message-format", "&9[%world] %prefix%player%suffix: &e%message", "The message-format if ranged-mode is enabled."),
    MULTIPREFIXES("multi-prefixes", false, "Should the multi-prefixes be enabled?"),
    MULTISUFFIXES("multi-suffixes", false, "Should the multi-suffixes be enabled?"),
    RANGEMODE("ranged-mode", false, "Should the ranged-mode be enabled?"),
    RANGEPREFIX("ranged-prefix", "!", "The Prefix to use for Range Mode"),
    SHOW_NO_RECEIVER_MSG("show-no-players-near", true, "Should we check if any player would receiver your chat message?"),
    RANGE("chat-range", 100, "The range to talk to other players. Set to -1 to enable world-wide-chat"),
    LOGCHAT("logChat", false, "Should the chat be logged?"),
    DEBUG("debug", false, "Should the debug log be enabled?"),
    LOCALE("Locale", "en-EN", "Which language do you want? (You can choose betwenn de-DE, fr-FR, pt-BR and en-EN by default.)"),
    ADS_ENABLED("Ads.Enabled", true, "Should we check for ads?"),
    ADS_BYPASS("Ads.Bypass", Arrays.asList("127.0.0.1", "my-domain.com"), "A list with allowed ips or domains."),
    ADS_LOG("Ads.Log", true, "Should the ads be logged in a file?"),
    ADS_SMART_MANAGER("Ads.SmartManager", true, "Should the \"Smart Manager\" be used? (For more information read: https://github.com/TheJeterLP/ChatEx/wiki/Ad-Manager)"),
    ADS_SMART_DOMAIN_ENDINGS("Ads.SmartConfig.DomainEndings", Arrays.asList(
            "com", "net", "org", "de", "icu", "uk", "ru", "me", "info", "top", "xyz", "tk", "cn", "ga", "cf", "nl", "eu"
    ), "The endings the SmartManager applies the multiplier to."),
    ADS_REPLACE_COMMAS("Ads.ReplaceCommas", false, "Should commas be replaced with \".\" for the add test?"),
    ADS_SMART_MULTIPLIER("Ads.SmartConfig.Multiplier", 4, "If a domain pattern contains an ending from Ads.SmartConfig.DomainEndings the score get multiplied by this number."),
    ADS_SMART_UN_MULTIPLIER("Ads.SmartConfig.UnMultiplier", 1, "If a domain pattern contains NOT an ending from Ads.SmartConfig.DomainEndings the score get multiplied by this number."),
    ADS_THRESHOLD("Ads.Threshold.Block", 0.3, "The threshold required to cancel a message."),
    ADS_REDUCE_THRESHOLD("Ads.Threshold.ReduceThreshold", 0.1, "How much threshold is removed per message"),
    ADS_MAX_LENGTH("Ads.Threshold.MaxLinkLength", 10, "What the max detected link length is (For more information read: https://github.com/TheJeterLP/ChatEx/wiki/Ad-Manager)"),
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

    Config(String path, Object val, String description) {
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
        StringBuilder header = new StringBuilder();
        for (Config c : values()) {
            header.append(c.getPath()).append(": ").append(c.getDescription()).append(System.lineSeparator());
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header.toString());
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

package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class RGBColors {
    private static final HashMap<String, String> placeHolderColorMap = new HashMap<>();

    public static void load() {
        ChatEx.getInstance().getLogger().info("Server version:" + Bukkit.getVersion());
        if (!Bukkit.getVersion().contains("1.16")) {
            ChatEx.getInstance().getLogger().info("This server version doesn't support custom color codes!");
            return;
        }
        ChatEx.getInstance().getLogger().info("Loading RGB ColorCodes!");
        ConfigurationSection configurationSection = Config.RGB_COLORS.getConfigurationSection();

        if (configurationSection == null) {
            ChatEx.getInstance().getLogger().info("No ColorCodes Specified!");
            return;
        }

        for (Map.Entry<String, Object> stringObjectEntry : configurationSection.getValues(false).entrySet()) {
            String key = stringObjectEntry.getKey();
            String value = (String) stringObjectEntry.getValue();
            String clearedValue = value.replaceFirst("#", "");
            char[] valueChars = clearedValue.toCharArray();
            StringBuilder rgbColor = new StringBuilder();
            rgbColor.append("§x");
            for (int i = 0; i < clearedValue.length(); i++) {
                rgbColor.append("§").append(valueChars[i]);
            }
            placeHolderColorMap.put(key, rgbColor.toString());
        }
    }

    public static String translateCustomColorCodes(String s) {
        if (!Bukkit.getVersion().contains("1.16")) {
            return s;
        }
        for (Map.Entry<String, String> stringColorEntry : placeHolderColorMap.entrySet()) {
            s = s.replace(stringColorEntry.getKey(), stringColorEntry.getValue());
        }
        return s;
    }
}

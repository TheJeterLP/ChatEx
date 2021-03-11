package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class RGBColors {
    private static final HashMap<String, String> placeHolderColorMap = new HashMap<>();
    private static final List<String> supportedVersions = Arrays.asList(
            "1.16",
            "1.17");

    private static Boolean supported = null;

    public static void load() {
        ChatEx.getInstance().getLogger().info("Server version:" + Bukkit.getVersion());
        if (isNotSupported()) {
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
        if (isNotSupported()) {
            return s;
        }
        s = translateSingleMessageColorCodes(s);
        for (Map.Entry<String, String> stringColorEntry : placeHolderColorMap.entrySet()) {
            s = s.replace(stringColorEntry.getKey(), stringColorEntry.getValue());
        }
        return s;
    }

    public static String translateSingleMessageColorCodes(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.length() - i > 8) {
                String tempString = s.substring(i, i + 8);
                if (tempString.startsWith("&#")) {
                    char[] tempChars = tempString.replaceFirst("&#", "").toCharArray();
                    StringBuilder rgbColor = new StringBuilder();
                    rgbColor.append("§x");
                    for (char tempChar : tempChars) {
                        rgbColor.append("§").append(tempChar);
                    }
                    s = s.replaceAll(tempString, rgbColor.toString());
                }
            }
        }
        return s;
    }

    private static boolean isNotSupported(){
        if(supported == null) {
            final String version = Bukkit.getVersion();
            supported = supportedVersions.stream().anyMatch(version::contains);
        }
        return !supported;
    }
}

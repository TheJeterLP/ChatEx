package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGBColors {

    private static final HashMap<String, String> placeHolderColorMap = new HashMap<>();

    private static Boolean supported = null;

    public static void load() {
        ChatEx.getInstance().getLogger().info("Server version:" + Bukkit.getVersion());
        if (isNotSupported()) {
            ChatEx.getInstance().getLogger().info("This server version doesn't support custom color codes!");
            return;
        }
        ChatEx.getInstance().getLogger().info("Version is later than 1.16. Loading RGB ColorCodes!");
        ConfigurationSection configurationSection = Config.RGB_COLORS.getConfigurationSection();

        if (configurationSection == null) {
            ChatEx.getInstance().getLogger().info("No ColorCodes Specified!");
            return;
        }

        for (Map.Entry<String, Object> stringObjectEntry : configurationSection.getValues(false).entrySet()) {
            String key = stringObjectEntry.getKey();
            String value = (String) stringObjectEntry.getValue();
            LogHelper.debug(("Loading custom color code " + key + " with value " + value + " from config!"));
            String clearedValue = value.replaceFirst("#", "");
            char[] valueChars = clearedValue.toCharArray();
            StringBuilder rgbColor = new StringBuilder();
            rgbColor.append("§x");
            for (int i = 0; i < clearedValue.length(); i++) {
                rgbColor.append("§").append(valueChars[i]);
            }
            LogHelper.debug("Putting KEY: " + key + " value: " + rgbColor);
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

    private static boolean isNotSupported() {
        if (supported == null) {
            try {
                final String version = Bukkit.getVersion();
                String ver = version.split("\\(MC: ")[1];
                String[] numbers = ver.replaceAll("\\)", "").split("\\.");
                ver = numbers[0] + numbers[1];
                int toCheck = Integer.valueOf(ver);
                LogHelper.debug(ver + "  INT: " + toCheck);
                supported = toCheck >= 116;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return !supported;
    }

    public static String translateGradientCodes(String message) {
        final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

}

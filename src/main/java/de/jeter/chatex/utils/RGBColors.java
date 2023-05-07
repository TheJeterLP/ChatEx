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
        Pattern pattern = Pattern.compile("&g#([A-Fa-f0-9]{6})#([A-Fa-f0-9]{6})#([^&]+)&g");
        Matcher matcher = pattern.matcher(message);
        //for all matches
        while (matcher.find()) {
            String hexColor1 = "#" + matcher.group(1);
            String hexColor2 = "#" + matcher.group(2);
            String text = matcher.group(3);
            message = message.replace(matcher.group(0), gradientText(text, hexColor1, hexColor2)+ ChatColor.RESET);
        }
        return message;
    }

    public static String gradientText(String text, String hexColor1, String hexColor2) {
        StringBuilder result = new StringBuilder();
        int length = text.length();
        int[][] rgbValues = new int[2][3];
        float[][] colorSteps = new float[2][3];
        int[] currentRGB = new int[3];

        rgbValues[0][0] = Integer.parseInt(hexColor1.substring(1, 3), 16);
        rgbValues[0][1] = Integer.parseInt(hexColor1.substring(3, 5), 16);
        rgbValues[0][2] = Integer.parseInt(hexColor1.substring(5, 7), 16);

        rgbValues[1][0] = Integer.parseInt(hexColor2.substring(1, 3), 16);
        rgbValues[1][1] = Integer.parseInt(hexColor2.substring(3, 5), 16);
        rgbValues[1][2] = Integer.parseInt(hexColor2.substring(5, 7), 16);

        for (int i = 0; i < 3; i++) {
            colorSteps[0][i] = (float)(rgbValues[1][i] - rgbValues[0][i]) / length;
            currentRGB[i] = rgbValues[0][i];
        }

        for (int i = 0; i < length; i++) {
            String hexColor = String.format("#%02x%02x%02x", currentRGB[0], currentRGB[1], currentRGB[2]);
            result.append("&" + hexColor + text.charAt(i));

            // Update RGB values for next character
            for (int j = 0; j < 3; j++) {
                currentRGB[j] += colorSteps[0][j];
            }
        }

        return result.toString();
    }

}

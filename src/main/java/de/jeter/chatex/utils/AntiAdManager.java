package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiAdManager {
    private static Map<UUID, Double> uuidErrorMap = new HashMap<>();
    private static final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    private static final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+~#?&//=]*)?");

    //replace any spaces in the range of ADS_MAX_LENGTH near . or , | removes () to prevent example(.)com
    private static final String urlCompactorPatternString = "[\\(\\)]|(\\s(?=.{0," + Config.ADS_MAX_LENGTH.getInt() + "}[,\\.]))|((?<=[,\\.].{0,3})\\s*)";


    //Ips are clear
    private static double checkForIPPattern(String message) {
        message = message.replaceAll(" ", "");
        Matcher regexMatcher = ipPattern.matcher(message);
        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];

                if (text.split("\\.").length > 4) {
                    String[] domains = text.split("\\.");
                    String one = domains[domains.length - 1];
                    String two = domains[domains.length - 2];
                    String three = domains[domains.length - 3];
                    String four = domains[domains.length - 4];
                    text = one + "." + two + "." + three + "." + four;
                }

                if (ipPattern.matcher(text).find()) {
                    if (!Config.ADS_BYPASS.getStringList().contains(regexMatcher.group().trim())) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    private static double checkForWebPattern(String message) {
        double messageLength = message.length();
        double error = 0;
        message = message.replaceAll(",", ".");
        message = message.replaceAll(urlCompactorPatternString, "");
        Matcher regexMatcher = webpattern.matcher(message);
        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];

                if (text.split("\\.").length > 2) {
                    String[] domains = text.split("\\.");
                    String toplevel = domains[domains.length - 1];
                    String second = domains[domains.length - 2];
                    text = second + "." + toplevel;
                }
                if (webpattern.matcher(text).find()) {
                    if (!Config.ADS_BYPASS.getStringList().contains(text)) {
                        error += text.length();
                    }
                }
            }
        }
        return error > 0 ? error / messageLength : 0;
    }

    public static boolean checkForAds(String msg, Player p) {
        if (p.hasPermission("chatex.bypassads")) {
            return false;
        }
        if (!Config.ADS_ENABLED.getBoolean()) {
            return false;
        }
        if (!uuidErrorMap.containsKey(p.getUniqueId()) || uuidErrorMap.get(p.getUniqueId()) < 0) {
            uuidErrorMap.put(p.getUniqueId(), 0d);
        }
        double error = checkForIPPattern(msg) + checkForWebPattern(msg);
        uuidErrorMap.put(p.getUniqueId(), uuidErrorMap.get(p.getUniqueId()) + error);
        boolean canceled = uuidErrorMap.get(p.getUniqueId()) > Config.ADS_THRESHOLD.getDouble();
        if (canceled) {
            uuidErrorMap.put(p.getUniqueId(), Config.ADS_THRESHOLD.getDouble());
            for (Player op : ChatEx.getInstance().getServer().getOnlinePlayers()) {
                if (!op.hasPermission("chatex.notifyad")) {
                    continue;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("%player", p.getName());
                map.put("%message", msg);

                String message = Locales.MESSAGES_AD_NOTIFY.getString(p).replaceAll("%player", p.getName()).replaceAll("%message", msg);
                op.sendMessage(message);
            }
            ChatLogger.writeToAdFile(p, msg);
        } else {
            uuidErrorMap.put(p.getUniqueId(), uuidErrorMap.get(p.getUniqueId()) - Config.ADS_REDUCE_THRESHOLD.getDouble());
        }
        return canceled;
    }
}
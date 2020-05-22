package de.jeter.chatex.utils.adManager;

import de.jeter.chatex.ChatEx;
import de.jeter.chatex.utils.ChatLogger;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleAdManager implements AdManager {
    private static final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    private static final Pattern webpattern = Pattern.compile("((?:[\\w-]+)(?:\\.[\\w-]+)+)(?:[\\w.,@?^=%&amp;:\\/~+#-]*[\\w@?^=%&\\/~+#-])?");

    private static boolean checkForIPPattern(String message) {
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkForWebPattern(String message) {
        message = message.replaceAll(" ", "");
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkForAds(String msg, Player p) {
        if (p.hasPermission("chatex.bypassads")) {
            return false;
        }
        if (!Config.ADS_ENABLED.getBoolean()) {
            return false;
        }
        boolean found = checkForIPPattern(msg) || checkForWebPattern(msg);
        if (found) {
            for (Player op : ChatEx.getInstance().getServer().getOnlinePlayers()) {
                if (!op.hasPermission("chatex.notifyad")) {
                    continue;
                }
                String message = Locales.MESSAGES_AD_NOTIFY.getString(p).replaceAll("%player", p.getName()).replaceAll("%message", msg);
                op.sendMessage(message);
            }
            ChatLogger.writeToAdFile(p, msg);
        }
        return found;
    }

    public static boolean checkForBlocked(String msg) {
        List<String> blocked = Config.BLOCKED_WORDS.getStringList();
        for (String block : blocked) {
            if (msg.toLowerCase().contains(block.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

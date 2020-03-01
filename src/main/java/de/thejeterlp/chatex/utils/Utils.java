package de.thejeterlp.chatex.utils;

import de.thejeterlp.chatex.ChatEX;
import de.thejeterlp.chatex.ChatListener;
import de.thejeterlp.chatex.plugins.PluginManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public class Utils {

    private static final Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
    private static final Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
    private static final Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
    private static final Pattern chatStrikethroughPattern = Pattern.compile("(?i)&([M])");
    private static final Pattern chatUnderlinePattern = Pattern.compile("(?i)&([N])");
    private static final Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
    private static final Pattern chatResetPattern = Pattern.compile("(?i)&([R])");
    private static final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    private static final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+~#?&//=]*)?");
    private static final String permissionChatColor = "chatex.chat.color";
    private static final String permissionChatMagic = "chatex.chat.magic";
    private static final String permissionChatBold = "chatex.chat.bold";
    private static final String permissionChatStrikethrough = "chatex.chat.strikethrough";
    private static final String permissionChatUnderline = "chatex.chat.underline";
    private static final String permissionChatItalic = "chatex.chat.italic";
    private static final String permissionChatReset = "chatex.chat.reset";

    // Time Display Formats
    private static final DateFormat dateMonths = new SimpleDateFormat("MM");
    private static final DateFormat dateHours12 = new SimpleDateFormat("hh");
    private static final DateFormat dateHours24 = new SimpleDateFormat("HH");
    private static final DateFormat dateMinutes = new SimpleDateFormat("mm");
    private static final DateFormat dateSeconds = new SimpleDateFormat("ss");

    public static String translateColorCodes(String string, Player p) {
        if (string == null) {
            return "";
        }
        String newstring = string;
        if (p.hasPermission(permissionChatColor)) {
            newstring = chatColorPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (p.hasPermission(permissionChatMagic)) {
            newstring = chatMagicPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (p.hasPermission(permissionChatBold)) {
            newstring = chatBoldPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (p.hasPermission(permissionChatStrikethrough)) {
            newstring = chatStrikethroughPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (p.hasPermission(permissionChatUnderline)) {
            newstring = chatUnderlinePattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (p.hasPermission(permissionChatItalic)) {
            newstring = chatItalicPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (p.hasPermission(permissionChatReset)) {
            newstring = chatResetPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        return newstring;
    }

    public static String replaceColors(String message) {
        return message.replaceAll("&((?i)[0-9a-fk-or])", "§$1");
    }

    public static List<Player> getLocalRecipients(Player sender) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new ArrayList<>();
        double squaredDistance = Math.pow(Config.RANGE.getDouble(), 2);
        for (Player recipient : sender.getWorld().getPlayers()) {
            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) {
                continue;
            }
            recipients.add(recipient);
        }
        return recipients;
    }

    public static String replacePlayerPlaceholders(Player player, String format) {
        String result = format;
        result = result.replace("%displayname", player.getDisplayName());
        result = result.replace("%prefix", PluginManager.getInstance().getPrefix(player));
        result = result.replace("%suffix", PluginManager.getInstance().getSuffix(player));
        result = result.replace("%player", player.getDisplayName());
        result = result.replace("%world", player.getWorld().getName());
        result = result.replace("%group", PluginManager.getInstance().getGroupNames(player)[0]);
        result = replaceTime(result);
        result = replaceColors(result);
        return result;
    }

    private static String replaceTime(String message) {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        if (message.contains("%time")) {
            DateFormat date = new SimpleDateFormat("HH:mm:ss");
            message = message.replace("%time", date.format(currentDate));
        }
        // Check for padded versions first
        if (message.contains("%MM")) {
            message = message.replace("%MM", dateMonths.format(currentDate));
        }
        if (message.contains("%hh")) {
            message = message.replace("%hh", dateHours12.format(currentDate));
        }
        if (message.contains("%HH")) {
            message = message.replace("%HH", dateHours24.format(currentDate));
        }
        if (message.contains("%ii")) {
            message = message.replace("%ii", dateMinutes.format(currentDate));
        }
        if (message.contains("%ss")) {
            message = message.replace("%ss", dateSeconds.format(currentDate));
        }

        if (message.contains("%h")) {
            final String hour = String.valueOf(calendar.get(Calendar.HOUR));
            message = message.replace("%h", hour);
        }
        if (message.contains("%H")) {
            final String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            message = message.replace("%H", hour);
        }
        if (message.contains("%i")) {
            final String minute = String.valueOf(calendar.get(Calendar.MINUTE));
            message = message.replace("%i", minute);
        }
        if (message.contains("%s")) {
            final String second = String.valueOf(calendar.get(Calendar.SECOND));
            message = message.replace("%s", second);
        }
        if (message.contains("%a")) {
            message = message.replace("%a", (calendar.get(Calendar.AM_PM) == 0) ? "am" : "pm");
        }
        if (message.contains("%A")) {
            message = message.replace("%A", (calendar.get(Calendar.AM_PM) == 0) ? "AM" : "PM");
        }
        if (message.contains("%m")) {
            final String month = String.valueOf(calendar.get(Calendar.MONTH));
            message = message.replace("%m", month);
        }
        if (message.contains("%M")) {
            String month = "";
            final int monat = calendar.get(Calendar.MONTH) + 1;
            switch (monat) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
                default:
                    month = Locales.COMMAND_CLEAR_UNKNOWN.getString(null);
                    break;
            }
            message = message.replace("%M", month);
        }

        if (message.contains("%y")) {
            final String year = String.valueOf(calendar.get(Calendar.YEAR));
            message = message.replace("%m", year);
        }

        if (message.contains("%Y")) {
            int year = calendar.get(Calendar.YEAR);
            String year_new = String.valueOf(year);
            year_new = year_new.replace("19", "").replace("20", "");
            message = message.replace("%Y", year_new);
        }

        if (message.contains("%d")) {
            final String day = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
            message = message.replace("%d", day);
        }

        if (message.contains("%D")) {
            final String day = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
            message = message.replace("%D", day);
        }

        message = replaceColors(message);
        return message;
    }

    private static boolean checkForIPPattern(String message) {
        ChatEX.debug("IP: Searching for ip: " + message);
        message = message.replaceAll(" ", "");
        Matcher regexMatcher = ipPattern.matcher(message);
        while (regexMatcher.find()) {
            ChatEX.debug("IP: Found!");
            if (regexMatcher.group().length() != 0) {
                String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];
                ChatEX.debug("IP: AdCheck ip: " + text);
                ChatEX.debug("IP: AdCheck length: " + text.split("\\.").length);

                if (text.split("\\.").length > 4) {
                    ChatEX.debug("IP: Removing subdomains...");
                    String[] domains = text.split("\\.");
                    ChatEX.debug("IP: AdCheck 1:" + Arrays.toString(domains));
                    String one = domains[domains.length - 1];
                    ChatEX.debug("IP: AdCheck 2:" + one);
                    String two = domains[domains.length - 2];
                    ChatEX.debug("IP: AdCheck 3:" + two);
                    String three = domains[domains.length - 3];
                    ChatEX.debug("IP: AdCheck 4:" + three);
                    String four = domains[domains.length - 4];
                    ChatEX.debug("IP: AdCheck 5:" + four);
                    text = one + "." + two + "." + three + "." + four;
                    ChatEX.debug("AdCheck 6:" + text);
                }

                if (ipPattern.matcher(text).find()) {
                    if (!Config.ADS_BYPASS.getStringList().contains(regexMatcher.group().trim())) {
                        ChatEX.debug("IP: Found ad: " + text);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkForWebPattern(String message) {
        ChatEX.debug("WEB: Searching for url: " + message);
        message = message.replaceAll(" ", "");
        Matcher regexMatcher = webpattern.matcher(message);
        while (regexMatcher.find()) {
            ChatEX.debug("WEB: Found!");
            if (regexMatcher.group().length() != 0) {
                String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];
                ChatEX.debug("WEB: AdCheck url: " + text);
                ChatEX.debug("WEB: AdCheck length: " + text.split("\\.").length);

                if (text.split("\\.").length > 2) {
                    ChatEX.debug("WEB: Removing subdomains...");
                    String[] domains = text.split("\\.");
                    ChatEX.debug("WEB: AdCheck 1:" + Arrays.toString(domains));
                    String toplevel = domains[domains.length - 1];
                    ChatEX.debug("WEB: AdCheck 2:" + toplevel);
                    String second = domains[domains.length - 2];
                    ChatEX.debug("WEB: AdCheck 3:" + second);
                    text = second + "." + toplevel;
                    ChatEX.debug("WEB: AdCheck 4:" + text);
                }

                if (webpattern.matcher(text).find()) {
                    if (!Config.ADS_BYPASS.getStringList().contains(text)) {
                        ChatEX.debug("WEB: Found ad: " + text);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean check(String msg, Player p) {
        ChatEX.debug("Checking for advertising...");
        if (p.hasPermission("chatex.bypassads")) {
            return false;
        }
        if (!Config.ADS_ENABLED.getBoolean()) {
            return false;
        }
        boolean found = checkForIPPattern(msg) || checkForWebPattern(msg);
        if (found) {
            for (Player op : ChatEX.getInstance().getServer().getOnlinePlayers()) {
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
        }
        return found;
    }
}

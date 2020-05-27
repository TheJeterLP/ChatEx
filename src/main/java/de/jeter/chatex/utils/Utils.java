package de.jeter.chatex.utils;

import de.jeter.chatex.plugins.PluginManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author TheJeterLP
 */
public class Utils {

    // Time Display Formats
    private static final DateFormat dateMonths = new SimpleDateFormat("MM");
    private static final DateFormat dateHours12 = new SimpleDateFormat("hh");
    private static final DateFormat dateHours24 = new SimpleDateFormat("HH");
    private static final DateFormat dateMinutes = new SimpleDateFormat("mm");
    private static final DateFormat dateSeconds = new SimpleDateFormat("ss");

    public static String translateColorCodes(String string, Player p) {
        return p.hasPermission("chatex.chat.color") ? replaceColors(string) : string;
    }

    public static String replaceColors(String message) {
        return message.replaceAll("&((?i)[0-9a-fk-or])", "§$1");
    }

    public static List<Player> getLocalRecipients(Player sender) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new ArrayList<>();

        double squaredDistance = Math.pow(Config.RANGE.getInt(), 2);
        for (Player recipient : sender.getWorld().getPlayers()) {
            if (Config.RANGE.getInt() > 0 && (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance)) {
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
        result = result.replace("%player", player.getName());
        result = result.replace("%world", player.getWorld().getName());
        result = result.replace("%group", PluginManager.getInstance().getGroupNames(player).length > 0 ? PluginManager.getInstance().getGroupNames(player)[0] : "none");
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

    public static boolean checkForBlocked(String msg) {
        List<String> blocked = Config.BLOCKED_WORDS.getStringList();
        for (String block : blocked) {
            if (msg.toLowerCase().contains(block.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkForBypassString(String message) {
        for (String block : Config.ADS_BYPASS.getStringList()) {
            if (message.toLowerCase().contains(block.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

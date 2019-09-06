package de.thejeterlp.chatex.utils;

import de.thejeterlp.chatex.ChatEX;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public class ChatLogger {

    public static void writeToFile(Player player, String message) {
        if (!Config.LOGCHAT.getBoolean()) return;
        BufferedWriter bw = null;
        File file = new File(ChatEX.getInstance().getDataFolder().getAbsolutePath() + File.separator + "logs");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            bw = new BufferedWriter(new FileWriter(file + File.separator + fileName(), true));
            bw.write(prefix(false) + player.getName() + " (uuid: " + player.getUniqueId() + "): " + message);
            bw.newLine();
        } catch (Exception ex) {
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    public static void writeToAdFile(Player player, String message) {
        if (!Config.ADS_LOG.getBoolean()) return;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(ChatEX.getInstance().getDataFolder().getAbsolutePath() + File.separator + "ads.log", true));
            bw.write(prefix(true) + player.getName() + " (uuid: " + player.getUniqueId() + "): " + message);
            bw.newLine();
        } catch (Exception ex) {
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    private static String fileName() {
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return date.format(cal.getTime()) + ".log";
    }

    private static String prefix(boolean day) {
        DateFormat date = day ? new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ") : new SimpleDateFormat("[HH:mm:ss] ");
        Calendar cal = Calendar.getInstance();
        return date.format(cal.getTime());
    }

}

package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author TheJeterLP
 */
public class ChatLogger {

    private static BufferedWriter chatWriter = null;
    private static BufferedWriter adWriter = null;

    public static void load() {
        try {
            File logFolder = new File(ChatEx.getInstance().getDataFolder(), "logs");
            logFolder.mkdirs();

            File chatLog = new File(logFolder, fileName());
            if (!chatLog.exists()) {
                chatLog.createNewFile();
            }
            chatWriter = new BufferedWriter(new FileWriter(chatLog, true));

            File adLog = new File(logFolder, "ads.log");
            if (!adLog.exists()) {
                adLog.createNewFile();
            }
            adWriter = new BufferedWriter(new FileWriter(adLog, true));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (chatWriter != null) {

                chatWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeToFile(Player player, String message) {
        if (!Config.LOGCHAT.getBoolean() || chatWriter == null) {
            return;
        }

        try {
            chatWriter.write(prefix(false) + player.getName() + " (uuid: " + player.getUniqueId() + "): " + message);
            chatWriter.newLine();
            chatWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeToAdFile(Player player, String message) {
        if (!Config.ADS_LOG.getBoolean()) {
            return;
        }
        try {
            adWriter.write(prefix(true) + player.getName() + " (uuid: " + player.getUniqueId() + "): " + message);
            adWriter.newLine();
            adWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
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

/*
 * This file is part of ChatEx
 * Copyright (C) 2022 ChatEx Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class ChatLogger {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_ONLY_FORMAT = DateTimeFormatter.ofPattern("[HH:mm:ss] ");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss] ");

    private static BufferedWriter chatWriter = null;
    private static BufferedWriter adWriter = null;
    private static LocalDate chatWriterDate = null;

    public static synchronized void load() {
        try {
            File logFolder = new File(ChatEx.getInstance().getDataFolder(), "logs");
            if (Config.LOGCHAT.getBoolean() || Config.ADS_LOG.getBoolean()) {
                logFolder.mkdirs();
            }
            if (Config.LOGCHAT.getBoolean()) {
                chatWriterDate = LocalDate.now();
                chatWriter = openWriter(logFolder, fileName(chatWriterDate));
            }
            if (Config.ADS_LOG.getBoolean()) {
                adWriter = openWriter(logFolder, "ads.log");
            }
        } catch (IOException ex) {
            ChatEx.getInstance().getLogger().log(Level.SEVERE, "Could not open chat log files", ex);
        }
    }

    private static BufferedWriter openWriter(File logFolder, String name) throws IOException {
        File logFile = new File(logFolder, name);
        logFile.createNewFile();
        return new BufferedWriter(new FileWriter(logFile, true));
    }

    public static synchronized void close() {
        try {
            if (chatWriter != null) {
                chatWriter.close();
                chatWriter = null;
            }
            if (adWriter != null) {
                adWriter.close();
                adWriter = null;
            }
        } catch (IOException ex) {
            ChatEx.getInstance().getLogger().log(Level.SEVERE, "Could not close chat log files", ex);
        }
    }

    public static synchronized void writeToFile(Player player, String message) {
        if (!Config.LOGCHAT.getBoolean() || chatWriter == null) {
            return;
        }

        try {
            rotateIfNeeded();
            chatWriter.write(prefix(false) + player.getName() + " (uuid: " + player.getUniqueId() + "): " + message);
            chatWriter.newLine();
            chatWriter.flush();
        } catch (IOException ex) {
            ChatEx.getInstance().getLogger().log(Level.SEVERE, "Could not write to chat log", ex);
        }
    }

    public static synchronized void writeToAdFile(Player player, String message) {
        if (!Config.ADS_LOG.getBoolean() || adWriter == null) {
            return;
        }
        try {
            adWriter.write(prefix(true) + player.getName() + " (uuid: " + player.getUniqueId() + "): " + message);
            adWriter.newLine();
            adWriter.flush();
        } catch (IOException ex) {
            ChatEx.getInstance().getLogger().log(Level.SEVERE, "Could not write to ad log", ex);
        }
    }

    private static void rotateIfNeeded() throws IOException {
        LocalDate today = LocalDate.now();
        if (today.equals(chatWriterDate)) {
            return;
        }

        chatWriter.close();
        File logFolder = new File(ChatEx.getInstance().getDataFolder(), "logs");
        chatWriter = openWriter(logFolder, fileName(today));
        chatWriterDate = today;
    }

    private static String fileName(LocalDate date) {
        return FILE_DATE_FORMAT.format(date) + ".log";
    }

    private static String prefix(boolean withDate) {
        LocalDateTime now = LocalDateTime.now();
        return (withDate ? DATE_TIME_FORMAT : TIME_ONLY_FORMAT).format(now);
    }

}

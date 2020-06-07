/*
 * This file is part of ChatEx
 * Copyright (C) 2020 ChatEx Team
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatLogger {

    private static BufferedWriter chatWriter = null;
    private static BufferedWriter adWriter = null;

    public static void load() {
        try {
            File logFolder = new File(ChatEx.getInstance().getDataFolder(), "logs");
            if (Config.LOGCHAT.getBoolean() || Config.ADS_LOG.getBoolean()) {
                logFolder.mkdirs();
            }
            if (Config.LOGCHAT.getBoolean()) {
                File chatLog = new File(logFolder, fileName());
                chatLog.createNewFile();
                chatWriter = new BufferedWriter(new FileWriter(chatLog, true));
            }
            if (Config.ADS_LOG.getBoolean()) {
                File adLog = new File(logFolder, "ads.log");
                adLog.createNewFile();
                adWriter = new BufferedWriter(new FileWriter(adLog, true));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (chatWriter != null) {
                chatWriter.close();
            }
            if (adWriter != null) {
                adWriter.close();
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
        if (!Config.ADS_LOG.getBoolean() || adWriter == null) {
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
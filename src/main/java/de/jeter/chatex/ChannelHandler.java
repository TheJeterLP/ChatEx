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
package de.jeter.chatex;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.jeter.chatex.utils.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ChannelHandler implements PluginMessageListener {

    private static ChannelHandler INSTANCE;

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("ChatEx")) {
            //String serverName = in.readUTF();

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            String msg;
            long millis = 0;
            try {
                millis = msgin.readLong();
                msg = msgin.readUTF();
            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "null";
            }

            if ((System.currentTimeMillis() - millis) < TimeUnit.SECONDS.toMillis(Config.CROSS_SERVER_TIMEOUT.getInt())) {
                ChatEx.getInstance().getServer().broadcastMessage(msg);
            }
        }
    }

    public static ChannelHandler getInstance() {
        return INSTANCE;
    }

    public static void load() {
        if (Config.BUNGEECORD.getBoolean()) {
            INSTANCE = new ChannelHandler();
            ChatEx.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(ChatEx.getInstance(), "BungeeCord");
            ChatEx.getInstance().getServer().getMessenger().registerIncomingPluginChannel(ChatEx.getInstance(), "BungeeCord", INSTANCE);
        }
    }

    public void sendMessage(Player p, String message) {
        if (Config.BUNGEECORD.getBoolean()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF("ALL");
            out.writeUTF("ChatEx"); // The channel name to check if this your data
            //out.writeUTF("GetServer");

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeLong(System.currentTimeMillis());
                msgout.writeUTF(message);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            p.sendPluginMessage(ChatEx.getInstance(), "BungeeCord", out.toByteArray());
        }
    }

}

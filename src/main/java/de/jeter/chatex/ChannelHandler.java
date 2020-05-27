package de.jeter.chatex;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.jeter.chatex.utils.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;

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
            try {
                msg = msgin.readUTF();
            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "null";
            }

            ChatEx.getInstance().getServer().broadcastMessage(msg);
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

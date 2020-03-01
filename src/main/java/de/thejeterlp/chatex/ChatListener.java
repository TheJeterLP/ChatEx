package de.thejeterlp.chatex;

import de.thejeterlp.chatex.plugins.PluginManager;
import de.thejeterlp.chatex.utils.ChatLogger;
import de.thejeterlp.chatex.utils.Config;
import de.thejeterlp.chatex.utils.Locales;
import de.thejeterlp.chatex.utils.Utils;
import java.util.List;
import java.util.UnknownFormatConversionException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author TheJeterLP
 */
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("chatex.allowchat")) {
            String msg = Locales.COMMAND_RESULT_NO_PERM.getString(event.getPlayer()).replaceAll("%perm", "chatex.allowchat");
            event.getPlayer().sendMessage(msg);
            event.setCancelled(true);
            return;
        }

        String format = PluginManager.getInstance().getMessageFormat(event.getPlayer());
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();

        if (Utils.checkForAds(chatMessage, player)) {
            String msg = Locales.MESSAGES_AD.getString(event.getPlayer()).replaceAll("%perm", "chatex.bypassads");
            event.getPlayer().sendMessage(msg);
            event.setCancelled(true);
            return;
        }

        List<String> blocked = Config.BLOCKED_WORDS.getStringList();
        for (String block : blocked) {
            if (event.getMessage().toLowerCase().contains(block.toLowerCase())) {
                ChatEX.debug("Found blocked word " + block);
                event.setCancelled(true);

                String msg = Locales.MESSAGES_BLOCKED.getString(event.getPlayer()).replaceAll("%word", block);
                event.getPlayer().sendMessage(msg);
                return;
            }
        }

        boolean global = false;
        if (Config.RANGEMODE.getBoolean() || Config.BUNGEECORD.getBoolean()) {
            if (chatMessage.startsWith("!")) {
                if (player.hasPermission("chatex.chat.global")) {
                    ChatEX.debug("Global message!");
                    chatMessage = chatMessage.replaceFirst("!", "");
                    format = PluginManager.getInstance().getGlobalMessageFormat(event.getPlayer());
                    global = true;
                } else {
                    player.sendMessage(Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.chat.global"));
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (Config.RANGEMODE.getBoolean()) {
                    global = false;
                    event.getRecipients().clear();
                    ChatEX.debug("Adding recipients to the message " + Utils.getLocalRecipients(player).size());
                    if (Utils.getLocalRecipients(player).size() == 1) {
                        player.sendMessage(Locales.NO_LISTENING_PLAYERS.getString(player));
                        event.setCancelled(true);
                        return;
                    } else {
                        event.getRecipients().addAll(Utils.getLocalRecipients(player));
                    }
                }
            }
        }

        if (global && Config.BUNGEECORD.getBoolean()) {
            String msgToSend = Utils.replacePlayerPlaceholders(player, format.replaceAll("%message", Utils.translateColorCodes(chatMessage, player)));
            ChannelHandler.getInstance().sendMessage(player, msgToSend);
        }

        try {
            format = format.replace("%message", "%2$s");
            format = Utils.replacePlayerPlaceholders(player, format);
            ChatEX.debug("Setting format to " + format);
            event.setFormat(format);
        } catch (UnknownFormatConversionException ex) {
            ChatEX.getInstance().getLogger().severe("Placeholder in format is not allowed!");
            format = format.replace("%message", "%2$s");
            format = Utils.replacePlayerPlaceholders(player, format);
            ChatEX.debug("Current format is " + format);

            format = format.replaceAll("%\\\\?.*?%", "");

            ChatEX.debug("After removing all unreplaced placeholders: " + format);
            event.setFormat(format);
        }
        chatMessage = Utils.translateColorCodes(chatMessage, player);
        ChatEX.debug("Setting message to " + chatMessage);
        event.setMessage(chatMessage);
        ChatEX.debug("Logging chatmessage...");
        ChatLogger.writeToFile(event.getPlayer(), event.getMessage());
    }

}

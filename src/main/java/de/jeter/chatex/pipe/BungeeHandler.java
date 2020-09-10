package de.jeter.chatex.pipe;

import de.jeter.chatex.ChannelHandler;
import de.jeter.chatex.api.events.PlayerUsesGlobalChatEvent;
import de.jeter.chatex.api.events.PlayerUsesRangeModeEvent;
import de.jeter.chatex.plugins.PluginManager;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BungeeHandler implements PipeHandler {
    @Override
    public boolean handle(AsyncPlayerChatEvent event) {
        String chatMessage = event.getMessage();
        Player player = event.getPlayer();
        String format = event.getFormat();
        boolean global = false;
        if (Config.RANGEMODE.getBoolean() || Config.BUNGEECORD.getBoolean()) {
            if (chatMessage.startsWith(Config.RANGEPREFIX.getString())) {
                if (player.hasPermission("chatex.chat.global")) {
                    chatMessage = chatMessage.replaceFirst(Pattern.quote(Config.RANGEPREFIX.getString()), "");
                    format = PluginManager.getInstance().getGlobalMessageFormat(player);
                    global = true;

                    PlayerUsesGlobalChatEvent playerUsesGlobalChatEvent = new PlayerUsesGlobalChatEvent(player, chatMessage);
                    Bukkit.getPluginManager().callEvent(playerUsesGlobalChatEvent);
                    chatMessage = playerUsesGlobalChatEvent.getMessage();
                    if (playerUsesGlobalChatEvent.isCancelled()) {
                        event.setCancelled(true);
                        return false;
                    }

                } else {
                    player.sendMessage(Locales.COMMAND_RESULT_NO_PERM.getString(player).replaceAll("%perm", "chatex.chat.global"));
                    event.setCancelled(true);
                    return false;
                }
            } else {
                if (Config.RANGEMODE.getBoolean()) {
                    event.getRecipients().clear();
                    if (Utils.getLocalRecipients(player).size() == 1 && Config.SHOW_NO_RECEIVER_MSG.getBoolean()) {
                        player.sendMessage(Locales.NO_LISTENING_PLAYERS.getString(player));
                        event.setCancelled(true);
                        return false;
                    } else {
                        event.getRecipients().addAll(Utils.getLocalRecipients(player));

                        PlayerUsesRangeModeEvent playerUsesRangeModeEvent = new PlayerUsesRangeModeEvent(player, chatMessage);
                        Bukkit.getPluginManager().callEvent(playerUsesRangeModeEvent);
                        chatMessage = playerUsesRangeModeEvent.getMessage();
                        if (playerUsesRangeModeEvent.isCancelled()) {
                            event.setCancelled(true);
                            return false;
                        }
                    }
                }
            }
        }

        if (global && Config.BUNGEECORD.getBoolean()) {
            String msgToSend = Utils.replacePlayerPlaceholders(player, format.replaceAll("%message", Matcher.quoteReplacement(chatMessage)));
            ChannelHandler.getInstance().sendMessage(player, msgToSend);
        }

        event.setFormat(format);
        event.setMessage(chatMessage);
        return true;
    }
}

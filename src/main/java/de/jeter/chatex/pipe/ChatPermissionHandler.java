package de.jeter.chatex.pipe;

import de.jeter.chatex.utils.Locales;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatPermissionHandler implements PipeHandler{
    @Override
    public boolean handle(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("chatex.allowchat")) {
            String msg = Locales.COMMAND_RESULT_NO_PERM.getString(event.getPlayer()).replaceAll("%perm", "chatex.allowchat");
            event.getPlayer().sendMessage(msg);
            event.setCancelled(true);
            return false;
        }
        return true;
    }
}

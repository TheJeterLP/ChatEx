package de.jeter.chatex.pipe;

import de.jeter.chatex.api.events.MessageContainsBlockedWordEvent;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class BlockedMessageHandler implements PipeHandler {
    @Override
    public boolean handle(AsyncPlayerChatEvent event) {
        if (Utils.checkForBlocked(event.getMessage())) {
            String message = Locales.MESSAGES_BLOCKED.getString(null);
            MessageContainsBlockedWordEvent messageContainsBlockedWordEvent = new MessageContainsBlockedWordEvent(event.getPlayer(), event.getMessage(), message);
            Bukkit.getPluginManager().callEvent(messageContainsBlockedWordEvent);
            event.setCancelled(!messageContainsBlockedWordEvent.isCancelled());
            event.setMessage(messageContainsBlockedWordEvent.getMessage());
            if (!messageContainsBlockedWordEvent.isCancelled()) {
                event.getPlayer().sendMessage(messageContainsBlockedWordEvent.getPluginMessage());
                return false;
            }
        }
        return true;
    }
}

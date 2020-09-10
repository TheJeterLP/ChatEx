package de.jeter.chatex.pipe;

import de.jeter.chatex.api.events.MessageBlockedBySpamManagerEvent;
import de.jeter.chatex.utils.AntiSpamManager;
import de.jeter.chatex.utils.Locales;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SpamManagerHandler implements PipeHandler {
    @Override
    public boolean handle(AsyncPlayerChatEvent event) {
        if (!AntiSpamManager.getInstance().isAllowed(event.getPlayer())) {
            long remainingTime = AntiSpamManager.getInstance().getRemainingSeconds(event.getPlayer());
            String message = Locales.ANTI_SPAM_DENIED.getString(event.getPlayer()).replaceAll("%time%", remainingTime + "");
            MessageBlockedBySpamManagerEvent messageBlockedBySpamManagerEvent = new MessageBlockedBySpamManagerEvent(event.getPlayer(), event.getMessage(), message, remainingTime);
            Bukkit.getPluginManager().callEvent(messageBlockedBySpamManagerEvent);
            event.setCancelled(!messageBlockedBySpamManagerEvent.isCancelled());
            if (!messageBlockedBySpamManagerEvent.isCancelled()) {
                event.getPlayer().sendMessage(messageBlockedBySpamManagerEvent.getPluginMessage());
                return false;
            }
            event.setMessage(messageBlockedBySpamManagerEvent.getMessage());
        }
        AntiSpamManager.getInstance().put(event.getPlayer());
        return true;
    }
}

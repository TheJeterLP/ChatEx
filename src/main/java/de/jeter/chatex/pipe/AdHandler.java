package de.jeter.chatex.pipe;

import de.jeter.chatex.api.events.MessageBlockedByAdManagerEvent;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.adManager.AdManager;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AdHandler implements PipeHandler{
    private final AdManager adManager;

    public AdHandler(AdManager adManager) {
        this.adManager = adManager;
    }

    @Override
    public boolean handle(AsyncPlayerChatEvent event) {
        if (adManager.checkForAds(event.getMessage(), event.getPlayer())) {
            String message = Locales.MESSAGES_AD.getString(null).replaceAll("%perm", "chatex.bypassads");
            MessageBlockedByAdManagerEvent messageBlockedByAdManagerEvent = new MessageBlockedByAdManagerEvent(event.getPlayer(), event.getMessage(), message);
            Bukkit.getPluginManager().callEvent(messageBlockedByAdManagerEvent);
            event.setMessage(messageBlockedByAdManagerEvent.getMessage());
            event.setCancelled(!messageBlockedByAdManagerEvent.isCancelled());
            if (!messageBlockedByAdManagerEvent.isCancelled()) {
                event.getPlayer().sendMessage(messageBlockedByAdManagerEvent.getPluginMessage());
                return false;
            }
        }
        return true;
    }
}

package de.jeter.chatex;

import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.purpurmc.purpur.event.PlayerAFKEvent;

import java.lang.Thread;

public class PurpurAFKListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAFKChangeEvent(PlayerAFKEvent event) {
        Thread purpurAFKEvent = new Thread(() -> {
            Player player = event.getPlayer();
            if (event.isGoingAfk()) {
                String format = Config.TABLIST_FORMAT.getString().replace("%afk", Config.AFK_FORMAT.getString());
                player.setPlayerListName(Utils.replacePlayerPlaceholders(player, format));
                return;
            }
            player.setPlayerListName(Utils.replacePlayerPlaceholders(player, Config.TABLIST_FORMAT.getString()));
        });
        purpurAFKEvent.start();
    }
}

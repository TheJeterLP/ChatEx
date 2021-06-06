package de.jeter.chatex;

import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Utils;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsAFKListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAFKChangeEvent(AfkStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        if (event.getValue()) {
            String format = Config.TABLIST_FORMAT.getString().replace("%afk", Config.AFK_FORMAT.getString());
            player.setPlayerListName(Utils.replacePlayerPlaceholders(player, format));
            return;
        }
        player.setPlayerListName(Utils.replacePlayerPlaceholders(player, Config.TABLIST_FORMAT.getString()));
    }
}

package de.jeter.chatex;

import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.HookManager;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.UpdateChecker;
import de.jeter.chatex.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        if (Config.CHANGE_JOIN_AND_QUIT.getBoolean()) {
            String msg = Locales.PLAYER_JOIN.getString(e.getPlayer());
            e.setJoinMessage(Utils.replacePlayerPlaceholders(e.getPlayer(), msg));
        }
        
        if (Config.CHANGE_TABLIST_NAME.getBoolean()) {
            String name = Config.TABLIST_FORMAT.getString();
            
            if (HookManager.checkPlaceholderAPI()) {
                name = PlaceholderAPI.setPlaceholders(e.getPlayer(), name);
            }
            
            name = Utils.replacePlayerPlaceholders(e.getPlayer(), name);
            e.getPlayer().setPlayerListName(name);
        }
        
        if (Config.CHECK_UPDATE.getBoolean() && e.getPlayer().hasPermission("chatex.notifyupdate") && ChatEx.getInstance().getUpdateChecker() != null) {
            if (ChatEx.getInstance().getUpdateChecker().getResult() == UpdateChecker.Result.UPDATE_FOUND) {
                e.getPlayer().sendMessage(Locales.UPDATE_FOUND.getString(null).replaceAll("%oldversion", ChatEx.getInstance().getDescription().getVersion()).replaceAll("%newversion", ChatEx.getInstance().getUpdateChecker().getVersion()));
            } 
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(final PlayerQuitEvent e) {
        if (!Config.CHANGE_JOIN_AND_QUIT.getBoolean()) {
            return;
        }
        String msg = Locales.PLAYER_QUIT.getString(e.getPlayer());
        e.setQuitMessage(Utils.replacePlayerPlaceholders(e.getPlayer(), msg));
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onKick(final PlayerKickEvent e) {
        if (!Config.CHANGE_JOIN_AND_QUIT.getBoolean()) {
            return;
        }
        String msg = Locales.PLAYER_KICK.getString(e.getPlayer());
        e.setLeaveMessage(Utils.replacePlayerPlaceholders(e.getPlayer(), msg));
    }
    
}

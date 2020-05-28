package de.jeter.chatex.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerUsesRangeModeEvent extends ChatExEvent {
    private static final HandlerList handlers = new HandlerList();
    private String message;
    private Player player;

    public PlayerUsesRangeModeEvent(Player player, String message){
        this.player = player;
        this.message = message;
    }

    /**
     * @return the player which fired this event
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the message which the player would have written.
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message set the message which the player writes.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }
}

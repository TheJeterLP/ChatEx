package de.jeter.chatex.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class MessageBlockedByAntiSpamManagerEvent extends ChatExEvent implements Cancellable {
    private Player player;
    private String message;
    private long remainingTime;
    private String playerMessage;
    private boolean canceled = true;

    public MessageBlockedByAntiSpamManagerEvent(Player player, String message, String playerMessage, long remaining) {
        this.player = player;
        this.message = message;
        this.playerMessage = playerMessage;
        this.remainingTime = remaining;
    }

    /**
     * @return the player which fired this event
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Return the message which the player will receive.
     */
    public String getMessage() {
        return message;
    }


    /**
     * @param message the message which the player will receive.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the message which the player would have written.
     */
    public String getPlayerMessage() {
        return playerMessage;
    }


    /**
     * @return the remaining time a player is muted
     */
    public long getRemainingTime() {
        return remainingTime;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }
}

package de.jeter.chatex.api.events;

import org.bukkit.entity.Player;

public class MessageContainsBlockedWordEvent extends ChatExEvent {
    private Player player;
    private String message;
    private String pluginMessage;
    private boolean canceled = true;

    public MessageContainsBlockedWordEvent(Player player, String message, String pluginMessage) {
        this.player = player;
        this.message = message;
        this.pluginMessage = pluginMessage;
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

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return Return the message which the player will receive.
     */
    public String getPluginMessage() {
        return pluginMessage;
    }

    /**
     * @param pluginMessage the message the player will receive
     */
    public void setPluginMessage(String pluginMessage) {
        this.pluginMessage = pluginMessage;
    }


    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

}

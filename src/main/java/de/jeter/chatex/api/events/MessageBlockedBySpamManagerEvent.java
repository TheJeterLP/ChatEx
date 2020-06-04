/*
 * This file is part of ChatEx
 * Copyright (C) 2020 ChatEx Team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.jeter.chatex.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageBlockedBySpamManagerEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final long remainingTime;
    private String message;
    private String pluginMessage;
    private boolean canceled = false;

    /**
     *
     * @param player the player which fired the event
     * @param message the message of the player
     * @param pluginMessage the message which the plugin sends to the player.
     * @param remaining the remaining time in seconds.
     */
    public MessageBlockedBySpamManagerEvent(Player player, String message, String pluginMessage, long remaining) {
        super(true);
        this.player = player;
        this.message = message;
        this.pluginMessage = pluginMessage;
        this.remainingTime = remaining;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}

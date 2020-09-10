package de.jeter.chatex.pipe;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class NonCheckingAsyncChatEvent extends AsyncPlayerChatEvent {
    private String uncheckedFormat;

    public NonCheckingAsyncChatEvent(boolean async, Player who, String message, Set<Player> players, String format) {
        super(async, who, message, players);
        uncheckedFormat = format;
    }


    @Override
    public void setFormat(String uncheckedFormat) {
        this.uncheckedFormat = uncheckedFormat;
    }

    @Override
    public String getFormat() {
        return uncheckedFormat;
    }
}

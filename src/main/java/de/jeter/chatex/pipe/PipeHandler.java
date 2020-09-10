package de.jeter.chatex.pipe;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface PipeHandler {

    /**
     * Is called when the {@link ChatPipe} reaches this handler.
     * @param asyncPlayerChatEvent the ChatEvent which started the pipe
     * @return Continue the Pipe
     */
    boolean handle(AsyncPlayerChatEvent asyncPlayerChatEvent);
}

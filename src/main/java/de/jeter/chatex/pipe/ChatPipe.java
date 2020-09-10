package de.jeter.chatex.pipe;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class ChatPipe {
    private final List<PipeHandler> handlerList = new ArrayList<>();

    public ChatPipe() {
    }

    /**
     * Add a {@link PipeHandler} to the end of this pipe
     * @param pipeHandler the handler to add to this pipe.
     */
    public void addLast(PipeHandler pipeHandler) {
        handlerList.add(pipeHandler);
    }

    /**
     * Fires the pipe.
     * @param asyncPlayerChatEvent the event which to use when fired.
     */
    public void handle(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        for (PipeHandler pipeHandler : handlerList) {
            System.out.println(pipeHandler.getClass().toString());
            if(!pipeHandler.handle(asyncPlayerChatEvent)) {
                break;
            }
        }
    }
}

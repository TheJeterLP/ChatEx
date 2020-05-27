package de.jeter.chatex.api.events;

import de.jeter.chatex.api.events.ChatExEvent;
import de.jeter.chatex.api.events.ChatExListener;

public interface EventManager {
    void registerEvents(ChatExListener chatExListener);
    <T extends ChatExEvent> T handleEvent(T event);
}

package de.jeter.chatex.api;

import de.jeter.chatex.api.events.EventManager;
import de.jeter.chatex.events.DefaultEventManager;

public class ChatExAPI {
    public ChatExAPI() {
    }

    public EventManager getEventManager() {
        return DefaultEventManager.getInstance();
    }


}

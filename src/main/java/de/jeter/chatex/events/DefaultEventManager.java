package de.jeter.chatex.events;

import de.jeter.chatex.api.events.EventManager;
import de.jeter.chatex.api.events.ChatExEvent;
import de.jeter.chatex.api.events.ChatExEventHandler;
import de.jeter.chatex.api.events.ChatExListener;
import de.jeter.chatex.api.events.RegisteredEvent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DefaultEventManager implements EventManager {
    private static DefaultEventManager instance = new DefaultEventManager();
    private final Map<Class<? extends ChatExEvent>, HashSet<RegisteredEvent>> eventClassMap = new HashMap<>();

    private DefaultEventManager() {
    }

    public static DefaultEventManager getInstance() {
        return instance;
    }
    
    public void registerEvents(ChatExListener chatExListener) {
        for (Method method : chatExListener.getClass().getDeclaredMethods()) {
            ChatExEventHandler cEEH = method.getAnnotation(ChatExEventHandler.class);
            Class<?> checkClass;
            if (cEEH == null) continue;
            if (method.getParameterTypes().length != 1 || !ChatExEvent.class.isAssignableFrom(checkClass = method.getParameterTypes()[0]))
                continue;
            Class<? extends ChatExEvent> eventClass = checkClass.asSubclass(ChatExEvent.class);

            if (!eventClassMap.containsKey(eventClass)) {
                eventClassMap.put(eventClass, new HashSet<>());
            }
            ChatExEventHandler chatExEventHandler = method.getAnnotation(ChatExEventHandler.class);
            eventClassMap.get(eventClass).add(new RegisteredEvent(chatExListener, method));
        }
    }

    public <T extends ChatExEvent> T handleEvent(T event) {
        if (!eventClassMap.containsKey(event.getClass())) return event;
        for (RegisteredEvent registeredEvent : eventClassMap.get(event.getClass())) {
            registeredEvent.fireEvent(event);
        }
        return event;
    }

}

package de.jeter.chatex.api.events;

import java.lang.reflect.Method;

public interface IRegisteredEvent {
    Object getInstance();
    Method getMethod();
}

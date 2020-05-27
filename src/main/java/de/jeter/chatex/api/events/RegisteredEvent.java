package de.jeter.chatex.api.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RegisteredEvent implements IRegisteredEvent {
    private Method method;
    private Object instance;

    public RegisteredEvent(Object obj, Method meth){
        instance = obj;
        method = meth;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public void fireEvent(ChatExEvent chatExEvent){
        method.setAccessible(true);
        try {
            method.invoke(getInstance(), chatExEvent);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

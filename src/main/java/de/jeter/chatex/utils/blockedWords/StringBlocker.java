package de.jeter.chatex.utils.blockedWords;

public class StringBlocker implements Blocker {

    private final String blocking;

    public StringBlocker(String blocking) {
        this.blocking = blocking.toLowerCase();
    }

    @Override
    public boolean test(String s) {
        return s.toLowerCase().contains(blocking);
    }
}

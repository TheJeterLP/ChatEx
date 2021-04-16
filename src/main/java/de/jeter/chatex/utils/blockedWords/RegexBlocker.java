package de.jeter.chatex.utils.blockedWords;

import java.util.regex.Pattern;

public class RegexBlocker implements Blocker {
    private final Pattern pattern;

    public RegexBlocker(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean test(String s) {
        return pattern.matcher(s).find();
    }
}

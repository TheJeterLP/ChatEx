package de.jeter.chatex.utils.blockedWords;

import de.jeter.chatex.ChatEx;
import de.jeter.chatex.utils.Config;

import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class BlockedWords {
    public static final String REGEX_PREFIX = "%reg:";
    private final LinkedList<Blocker> blockers = new LinkedList<>();

    public BlockedWords() {
        for (String word : Config.BLOCKED_WORDS.getStringList()) {
            if (word.startsWith(REGEX_PREFIX)) {
                String stringPattern = word.substring(REGEX_PREFIX.length());
                try {
                    Pattern pattern = Pattern.compile(stringPattern);
                    blockers.add(new RegexBlocker(pattern));
                } catch (PatternSyntaxException exception) {
                    ChatEx.getInstance().getLogger().warning("Failed to compile regex pattern " + stringPattern);
                }
                continue;
            }
            blockers.add(new StringBlocker(word));
        }
    }

    public boolean isBlocked(String string) {
        return blockers.stream().anyMatch(b -> b.test(string));
    }
}

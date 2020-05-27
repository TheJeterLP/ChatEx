package de.jeter.chatex.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AntiSpamManager {

    private static final Map<Player, Long> map = new HashMap<>();

    public static void put(Player chatter) {
        map.put(chatter, System.currentTimeMillis());
    }

    public static boolean isAllowed(Player chatter) {
        if (!map.containsKey(chatter) || !Config.ANTISPAM_ENABLED.getBoolean() || chatter.hasPermission("chatex.antispam.bypass")) {
            return true;
        }

        long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
        long current = System.currentTimeMillis();

        return current > lastChat;
    }

    public static long getRemaingSeconds(Player chatter) {
        if (isAllowed(chatter)) {
            return 0;
        }

        long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
        long current = System.currentTimeMillis();

        long diff = lastChat - current;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        return seconds;
    }

}

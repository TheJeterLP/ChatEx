package de.jeter.chatex.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AntiSpamManager {

    private final Map<Player, Long> map = new HashMap<>();
    private static AntiSpamManager instance = new AntiSpamManager();
    public void put(Player chatter) {
        map.put(chatter, System.currentTimeMillis());
    }

    private AntiSpamManager(){

    }

    public static AntiSpamManager getInstance() {
        return instance;
    }

    public boolean isAllowed(Player chatter) {
        if (!map.containsKey(chatter) || !Config.ANTISPAM_ENABLED.getBoolean() || chatter.hasPermission("chatex.antispam.bypass")) {
            return true;
        }

        long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
        long current = System.currentTimeMillis();

        return current > lastChat;
    }

    public long getRemaingSeconds(Player chatter) {
        if (isAllowed(chatter)) {
            return 0;
        }

        long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
        long current = System.currentTimeMillis();

        long diff = lastChat - current;
        return TimeUnit.MILLISECONDS.toSeconds(diff);
    }

    public long getRemainingMillis(Player chatter){
        if (isAllowed(chatter)) {
            return 0;
        }
        long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
        long current = System.currentTimeMillis();

        return lastChat - current;
    }

}

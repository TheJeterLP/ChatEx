package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;

public class LogHelper {

    public static void debug(String text) {
        if (!Config.DEBUG.getBoolean()) {
            return;
        }
        ChatEx.getInstance().getLogger().info("[DEBUG] " + text);
    }

}

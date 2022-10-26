/*
 * This file is part of ChatEx
 * Copyright (C) 2022 ChatEx Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.jeter.chatex.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.lang.Thread;

public class AntiSpamManager {

    private static AntiSpamManager instance = new AntiSpamManager();
    private final Map<Player, Long> map = new HashMap<>();

    private AntiSpamManager() {

    }

    public static AntiSpamManager getInstance() {
        return instance;
    }

    public void put(Player chatter) {
        map.put(chatter, System.currentTimeMillis());
    }

    public boolean isAllowed(Player chatter) {
        Thread allowedCheck = new Thread(() -> {
            if (!map.containsKey(chatter) || !Config.ANTISPAM_ENABLED.getBoolean() || chatter.hasPermission("chatex.antispam.bypass")) {
                return true;
            }

            long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
            long current = System.currentTimeMillis();

            return current > lastChat;
        });
        allowedCheck.start();
    }

    public long getRemainingSeconds(Player chatter) {
        Thread secondsRemain = new Thread(() -> {
            if (isAllowed(chatter)) {
                return 0;
            }

            long lastChat = map.get(chatter) + (Config.ANTISPAM_SECONDS.getInt() * 1000);
            long current = System.currentTimeMillis();

            long diff = lastChat - current;
            return TimeUnit.MILLISECONDS.toSeconds(diff);
        });
        secondsRemain.start();
    }                                      

    public void clear() {
        map.clear();
    }

}

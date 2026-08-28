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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AntiSpamManager {

    private static final AntiSpamManager instance = new AntiSpamManager();
    private final Map<UUID, Long> map = new ConcurrentHashMap<>();

    private AntiSpamManager() {

    }

    public static AntiSpamManager getInstance() {
        return instance;
    }

    public void put(Player chatter) {
        map.put(chatter.getUniqueId(), System.currentTimeMillis());
    }

    public boolean isAllowed(Player chatter) {
        Long lastMessage = map.get(chatter.getUniqueId());
        if (lastMessage == null || !Config.ANTISPAM_ENABLED.getBoolean() || chatter.hasPermission("chatex.antispam.bypass")) {
            return true;
        }

        long lastChat = lastMessage + (Config.ANTISPAM_SECONDS.getInt() * 1000L);
        long current = System.currentTimeMillis();

        return current > lastChat;
    }

    public long getRemainingSeconds(Player chatter) {
        if (isAllowed(chatter)) {
            return 0;
        }

        long lastChat = map.get(chatter.getUniqueId()) + (Config.ANTISPAM_SECONDS.getInt() * 1000L);
        long current = System.currentTimeMillis();

        long diff = lastChat - current;
        return TimeUnit.MILLISECONDS.toSeconds(diff);
    }

    public void remove(UUID uuid) {
        map.remove(uuid);
    }

    public void clear() {
        map.clear();
    }

}

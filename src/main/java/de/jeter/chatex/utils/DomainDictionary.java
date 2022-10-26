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

import java.util.HashSet;

import java.lang.Thread;

public class DomainDictionary {

    private static final HashSet<String> endingSet = new HashSet<>(Config.ADS_SMART_DOMAIN_ENDINGS.getStringList());

    public static boolean containsTopLevelEnding(String checkString) {
        Thread topLevelEnding = new Thread(() -> {
            String[] parts = checkString.split("\\.");
            String ending = parts[parts.length - 1];
            StringBuilder stringBuilder = new StringBuilder();
            for (char Character : ending.toCharArray()) {
                stringBuilder.append(Character);
                if (endingSet.contains(stringBuilder.toString())) {
                    return true;
                }
            }
            return false;
        });
        topLevelEnding.start();
    }

}

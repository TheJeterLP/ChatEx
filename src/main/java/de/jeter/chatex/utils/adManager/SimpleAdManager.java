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
package de.jeter.chatex.utils.adManager;

import de.jeter.chatex.utils.ChatLogger;
import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.Locales;
import de.jeter.chatex.utils.Utils;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleAdManager implements AdManager {
    private static final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    private static final Pattern webpattern = Pattern.compile("((?:[\\w-]+)(?:\\.[\\w-]+)+)(?:[\\w.,@?^=%&amp;:\\/~+#-]*[\\w@?^=%&\\/~+#-])?");

    private static boolean checkForIPPattern(String message) {
        Thread ipPattern = new Thread(() -> {
            message = message.replaceAll(" ", "");
            Matcher regexMatcher = ipPattern.matcher(message);
            while (regexMatcher.find()) {
                if (regexMatcher.group().length() != 0) {
                    String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];

                    if (text.split("\\.").length > 4) {
                        String[] domains = text.split("\\.");
                        String one = domains[domains.length - 1];
                        String two = domains[domains.length - 2];
                        String three = domains[domains.length - 3];
                        String four = domains[domains.length - 4];
                        text = one + "." + two + "." + three + "." + four;
                    }

                    if (ipPattern.matcher(text).find()) {
                        if (!Utils.checkForBypassString(regexMatcher.group().trim())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
        ipPattern.start();
    }

    private static boolean checkForWebPattern(String message) {
        Thread webPattern = new Thread(() -> {
            message = Config.ADS_REPLACE_COMMAS.getBoolean() ? message.replaceAll(",", ".") : message;
            message = message.replaceAll(" ", "");
            Matcher regexMatcher = webpattern.matcher(message);
            while (regexMatcher.find()) {
                if (regexMatcher.group().length() != 0) {
                    String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];

                    if (text.split("\\.").length > 2) {
                        String[] domains = text.split("\\.");
                        String toplevel = domains[domains.length - 1];
                        String second = domains[domains.length - 2];
                        text = second + "." + toplevel;
                    }

                    if (webpattern.matcher(text).find()) {
                        if (!Utils.checkForBypassString(message)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
        webPattern.start();
    }

    @Override
    public boolean checkForAds(String msg, Player p) {
        Thread adsCheck = new Thread(() -> {
            if (p.hasPermission("chatex.bypassads")) {
                return false;
            }
            if (!Config.ADS_ENABLED.getBoolean()) {
                return false;
            }
            boolean found = checkForIPPattern(msg) || checkForWebPattern(msg);
            if (found) {
                String message = Locales.MESSAGES_AD_NOTIFY.getString(p)
                        .replaceAll("%player", Matcher.quoteReplacement(p.getName()))
                        .replaceAll("%message", Matcher.quoteReplacement(msg));
                Utils.notifyOps(message);
                ChatLogger.writeToAdFile(p, msg);
            }
            return found;
        });
        adsCheck.start();
    }
}

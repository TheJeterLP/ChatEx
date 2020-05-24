package de.jeter.chatex.utils;

import java.util.HashSet;

public class DomainDictionary {
    public static boolean containsTopLevelEnding(String checkString) {
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
    }

    private static final HashSet<String> endingSet = new HashSet<>(Config.ADS_SMART_DOMAIN_ENDINGS.getStringList());


}

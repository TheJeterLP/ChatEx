package de.jeter.chatex.utils;

import java.util.Arrays;
import java.util.HashSet;

public class DomainDictionary {
    public static boolean containsTopLevelEnding(String checkString){
        String[] parts = checkString.split("\\.");
        String ending = parts[parts.length-1];
        StringBuffer stringBuffer = new StringBuffer();
        for(char Character : ending.toCharArray()){
            stringBuffer.append(Character);
            if(endingSet.contains(stringBuffer.toString())){
                return true;
            }
        }
        return false;
    }
    private static final HashSet<String> endingSet = new HashSet<>(Arrays.asList(
            "com", "net", "org", "de", "icu", "uk", "ru", "me", "info", "top", "xyz", "tk", "cn", "ga", "cf", "nl"
    ));


}

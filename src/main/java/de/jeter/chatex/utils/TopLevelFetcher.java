package de.jeter.chatex.utils;

import de.jeter.chatex.ChatEx;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TopLevelFetcher {

    private static final String LINK = "http://data.iana.org/TLD/tlds-alpha-by-domain.txt";
    private static final List<String> TLD = new ArrayList<>();

    public static void setup() {
        TLD.clear();

        ChatEx.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatEx.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(LINK);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("#")) {
                            continue;
                        }
                        TLD.add(line.toLowerCase());
                    }
                    in.close();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                for (String tld : TLD) {
                    LogHelper.debug(tld);
                }
            }
        });
    }

    public static boolean isTLD(String tld) {
        return TLD.contains(tld.toLowerCase());
    }

}

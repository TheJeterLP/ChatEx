package de.jeter.chatex.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateChecker {

    private final String USER_AGENT;

    private final JavaPlugin plugin;
    private final int id;
    private final Thread thread;

    private Result result = Result.NO_UPDATE;
    private String version;
    private static final String VERSIONS = "/versions/latest";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";

    public UpdateChecker(JavaPlugin plugin, int id) {
        this.plugin = plugin;
        this.id = id;
        this.USER_AGENT = plugin.getName() + " UpdateChecker";
        thread = new Thread(new UpdaterRunnable());
        thread.start();
    }

    public enum Result {
        UPDATE_FOUND,
        NO_UPDATE,
        FAILED,
        BAD_ID
    }

    /**
     * Get the result of the update.
     *
     * @return result of the update.
     * @see Result
     */
    public Result getResult() {
        waitThread();
        return result;
    }

    /**
     * Get the latest version from spigot.
     *
     * @return latest version.
     */
    public String getVersion() {
        waitThread();
        return version;
    }

    /**
     * Check if id of resource is valid
     *
     * @param link link of the resource
     * @return true if id of resource is valid
     */
    private boolean checkResource(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            int code = connection.getResponseCode();

            if (code != 200) {
                connection.disconnect();
                result = Result.BAD_ID;
                return false;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Checks if there is any update available.
     */
    private void checkUpdate() {
        try {
            plugin.getLogger().info("Checking for update...");
            URL url = new URL(API_RESOURCE + id + VERSIONS);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JsonObject object = element.getAsJsonObject();
            element = object.get("name");
            version = element.toString().replaceAll("\"", "");

            plugin.getLogger().info("Version installed is " + plugin.getDescription().getVersion());
            plugin.getLogger().info("Latest version found online is " + version);

            if (shouldUpdate(version, plugin.getDescription().getVersion())) {
                result = Result.UPDATE_FOUND;
                plugin.getLogger().info("Update found!");
            } else {
                plugin.getLogger().info("No update found.");
                result = Result.NO_UPDATE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if plugin should be updated
     *
     * @param newVersion remote version
     * @param oldVersion current version
     */
    private boolean shouldUpdate(String newVersion, String oldVersion) {
        try {
            float oldV = Float.valueOf(oldVersion.replaceAll("\\.", "").replace("v", "."));
            float newV = Float.valueOf(newVersion.replaceAll("\\.", "").replace("v", "."));
            return oldV < newV;
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return !newVersion.equalsIgnoreCase(oldVersion);
        }
    }

    /**
     * Updater depends on thread's completion, so it is necessary to wait for
     * thread to finish.
     */
    private void waitThread() {
        if (thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    public class UpdaterRunnable implements Runnable {

        @Override
        public void run() {
            if (checkResource(API_RESOURCE + id)) {
                checkUpdate();
            }
        }
    }
}

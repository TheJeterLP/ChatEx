package de.jeter.chatex.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {

    private final String USER_AGENT;

    private final JavaPlugin plugin;
    private final File updateFolder;
    private final File file;
    private final int id;
    private final Thread thread;
    private final String downloadLink;

    private final UpdateType updateType;
    private Result result = Result.SUCCESS;

    private String version;
    private String jenkinsBuildNumber;

    private static final String VERSIONS = "/updates";
    private static final String FIELDS = "?fields=title";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";

    public UpdateChecker(JavaPlugin plugin, int id, File file, UpdateType updateType) {
        this.plugin = plugin;
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.updateFolder.mkdirs();
        this.id = id;
        this.file = file;
        this.updateType = updateType;
        this.USER_AGENT = plugin.getName() + " UpdateChecker";
        this.downloadLink = "https://jenkins.jeter.de/job/" + plugin.getName() + "/%build%/artifact/target/" + file.getName();

        thread = new Thread(new UpdaterRunnable());
        thread.start();
    }

    public enum UpdateType {
        // Checks only the version
        VERSION_CHECK,
        // If updater finds new version automatically it downloads it.
        CHECK_DOWNLOAD

    }

    public enum Result {

        UPDATE_FOUND,
        NO_UPDATE,
        SUCCESS,
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
            URL url = new URL(API_RESOURCE + id + VERSIONS + FIELDS);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JsonArray jsonArray = element.getAsJsonArray();

            element = jsonArray.get(jsonArray.size() - 1);

            JsonObject object = element.getAsJsonObject();
            element = object.get("title");
            String name = element.toString().replaceAll("\"", "");
            String[] nameArray = name.split("v");

            version = nameArray[0];
            jenkinsBuildNumber = nameArray[1];

            plugin.getLogger().info("Version installed is " + plugin.getDescription().getVersion());
            plugin.getLogger().info("Latest version found online is " + version + " Jenkins Build Number is " + jenkinsBuildNumber);

            if (shouldUpdate(version, plugin.getDescription().getVersion())) {
                result = Result.UPDATE_FOUND;
                if (updateType == UpdateType.VERSION_CHECK) {
                    plugin.getLogger().info("Update found!");
                } else {
                    plugin.getLogger().info("Update found, downloading now...");
                    download();
                }
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
            int oldV = Integer.valueOf(oldVersion.replaceAll("\\.", ""));
            int newV = Integer.valueOf(newVersion.replaceAll("\\.", ""));
            return oldV < newV;
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return !newVersion.equalsIgnoreCase(oldVersion);
        }
    }

    /**
     * Downloads the file
     */
    private void download() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            URL url = new URL(downloadLink.replaceAll("%build%", jenkinsBuildNumber));

            plugin.getLogger().info("Downloading update from " + url.toExternalForm());

            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", plugin.getName() + " auto-updater");

            in = new BufferedInputStream(httpConnection.getInputStream());
            fout = new FileOutputStream(new File(updateFolder, file.getName()));

            int grabSize = 2048;

            byte[] data = new byte[grabSize];
            int grab;
            while ((grab = in.read(data, 0, grabSize)) >= 0) {
                fout.write(data, 0, grab);
            }
            plugin.getLogger().info("Download done.");
            result = Result.SUCCESS;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Updater tried to download the update, but was unsuccessful.");
            e.printStackTrace();
            result = Result.FAILED;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
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

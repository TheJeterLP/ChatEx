package de.thejeterlp.chatex.utils;

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

    private int page = 1;
    private final UpdateType updateType;
    private Result result = Result.SUCCESS;
    private String downloadLink;
    private boolean emptyPage;
    private String version;

    private static final String DOWNLOAD = "/download";
    private static final String VERSIONS = "/versions";
    private static final String PAGE = "?page=";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";

    public UpdateChecker(JavaPlugin plugin, int id, File file, UpdateType updateType) {
        this.plugin = plugin;
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.id = id;
        this.file = file;
        this.updateType = updateType;
        this.USER_AGENT = plugin.getName() + " UpdateChecker";

        downloadLink = API_RESOURCE + id;

        thread = new Thread(new UpdaterRunnable());
        thread.start();
    }

    public enum UpdateType {
        // Checks only the version
        VERSION_CHECK,
        // Downloads without checking the version
        DOWNLOAD,
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
            String page = Integer.toString(this.page);

            URL url = new URL(API_RESOURCE + id + VERSIONS + PAGE + page);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JsonArray jsonArray = element.getAsJsonArray();

            if (jsonArray.size() == 10 && !emptyPage) {
                connection.disconnect();
                this.page++;
                checkUpdate();
            } else if (jsonArray.size() == 0) {
                emptyPage = true;
                this.page--;
                checkUpdate();
            } else if (jsonArray.size() < 10) {
                element = jsonArray.get(jsonArray.size() - 1);

                JsonObject object = element.getAsJsonObject();
                element = object.get("name");
                version = element.toString().replaceAll("\"", "").replace("v", "");

                plugin.getLogger().info("Checking for update...");

                if (shouldUpdate(version, plugin.getDescription().getVersion()) && updateType == UpdateType.VERSION_CHECK) {
                    result = Result.UPDATE_FOUND;
                    plugin.getLogger().info("Update found!");
                } else if (updateType == UpdateType.DOWNLOAD) {
                    plugin.getLogger().info("Downloading update... version not checked");
                    download();
                } else if (updateType == UpdateType.CHECK_DOWNLOAD) {
                    if (shouldUpdate(version, plugin.getDescription().getVersion())) {
                        plugin.getLogger().info("Update found, downloading now...");
                        download();
                    } else {
                        plugin.getLogger().info("Update not found");
                        result = Result.NO_UPDATE;
                    }
                } else {
                    plugin.getLogger().info("Update not found");
                    result = Result.NO_UPDATE;
                }
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
        return !newVersion.equalsIgnoreCase(oldVersion);
    }

    /**
     * Downloads the file
     */
    private void download() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            URL url = new URL(downloadLink);
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(new File(updateFolder, file.getName()));

            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Updater tried to download the update, but was unsuccessful.");

            result = Result.FAILED;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, null, e);
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

        public void run() {
            if (checkResource(downloadLink)) {
                downloadLink = downloadLink + DOWNLOAD;
                checkUpdate();
            }
        }
    }
}

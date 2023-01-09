package de.jeter.chatex.utils;

import de.jeter.chatex.plugins.PluginManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class CustomCharts {

    public static void addUpdateCheckerChart(Metrics metrics) {
        metrics.addCustomChart(new SimplePie("updatechecker_enabled", () -> String.valueOf(Config.CHECK_UPDATE.getBoolean())));
    }

    public static void addPermissionsPluginChart(Metrics metrics) {
        metrics.addCustomChart(new SimplePie("used_permissions_plugin", () -> PluginManager.getInstance().getName()));
    }

}

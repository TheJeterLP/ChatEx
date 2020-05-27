package de.jeter.chatex.api;

import de.jeter.chatex.plugins.PluginManager;
import org.bukkit.entity.Player;

public class ChatExAPI {
    public String getPermissionHandlerName() {
        return PluginManager.getInstance().getName();
    }

    public String getPrefix(Player p) {
        return PluginManager.getInstance().getPrefix(p);
    }

    public String getSuffix(Player p) {
        return PluginManager.getInstance().getSuffix(p);
    }

    public String[] getGroupNames(Player p) {
        return PluginManager.getInstance().getGroupNames(p);
    }

    public String getMessageFormat(Player p) {
        return PluginManager.getInstance().getMessageFormat(p);
    }

    public String getGlobalMessageFormat(Player p) {
        return PluginManager.getInstance().getGlobalMessageFormat(p);
    }
}
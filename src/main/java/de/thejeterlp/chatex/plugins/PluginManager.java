package de.thejeterlp.chatex.plugins;

import de.thejeterlp.chatex.ChatEX;
import de.thejeterlp.chatex.utils.HookManager;
import de.thejeterlp.chatex.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public class PluginManager implements PermissionsPlugin {

    private static PermissionsPlugin handler;

    public static PermissionsPlugin getInstance() {
        return ChatEX.getManager();
    }

    public PluginManager() {
        ChatEX.debug("Checking for Plugins...");
        if (HookManager.checkPEX()) {
            handler = new PermissionsEx();
        } else if (HookManager.checkVault() && Vault.setupChat()) {
            handler = new Vault();
        } else {
            handler = new Nothing();
        }
        ChatEX.debug("Plugin found!");
        ChatEX.getInstance().getLogger().info("Successfully hooked into: " + PluginManager.getInstance().getName());
    }

    @Override
    public String getName() {
        ChatEX.debug("Getting name of plugin...");
        if (!HookManager.checkPlaceholderAPI()) {
            return handler.getName();
        } else {
            return handler.getName() + ", PlaceholderAPI";
        }
    }

    @Override
    public String getPrefix(Player p) {
        ChatEX.debug("Getting prefix from " + p.getName());
        return handler.getPrefix(p);
    }

    @Override
    public String getSuffix(Player p) {
        ChatEX.debug("Getting suffix from " + p.getName());
        return handler.getSuffix(p);
    }

    @Override
    public String[] getGroupNames(Player p) {
        ChatEX.debug("Getting groups from " + p.getName());
        return handler.getGroupNames(p);
    }

    @Override
    public String getMessageFormat(Player p) {
        ChatEX.debug("Getting message-format from " + p.getName());
        if (!HookManager.checkPlaceholderAPI()) {
            return Utils.replaceColors(handler.getMessageFormat(p));
        } else {
            ChatEX.debug("replacing placeholders! " + Utils.replaceColors(PlaceholderAPI.setPlaceholders(p, handler.getMessageFormat(p))));
            return Utils.replaceColors(PlaceholderAPI.setPlaceholders(p, handler.getMessageFormat(p)));
        }
    }

    @Override
    public String getGlobalMessageFormat(Player p) {
        ChatEX.debug("Getting global message-format from " + p.getName());
        if (!HookManager.checkPlaceholderAPI()) {
            return Utils.replaceColors(handler.getGlobalMessageFormat(p));
        } else {
            return Utils.replaceColors(PlaceholderAPI.setPlaceholders(p, handler.getGlobalMessageFormat(p)));
        }
    }
}

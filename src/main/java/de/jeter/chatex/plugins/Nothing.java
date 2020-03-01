package de.jeter.chatex.plugins;

import de.jeter.chatex.utils.Config;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public class Nothing implements PermissionsPlugin {

    @Override
    public String getPrefix(Player p) {
        return "";
    }

    @Override
    public String getSuffix(Player p) {
        return "";
    }

    @Override
    public String[] getGroupNames(Player p) {
        String[] data = {""};
        return data;
    }

    @Override
    public String getName() {
        return "Nothing was found!";
    }

    @Override
    public String getMessageFormat(Player p) {
        return Config.FORMAT.getString();
    }

    @Override
    public String getGlobalMessageFormat(Player p) {
        return Config.GLOBALFORMAT.getString();
    }

}

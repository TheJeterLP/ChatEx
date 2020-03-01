package de.jeter.chatex.plugins;

import org.bukkit.entity.Player;

/**
 *
 * @author TheJeterLP
 */
public interface PermissionsPlugin {

    public String getName();

    public String getPrefix(Player p);

    public String getSuffix(Player p);

    public String[] getGroupNames(Player p);

    public String getMessageFormat(Player p);

    public String getGlobalMessageFormat(Player p);
}

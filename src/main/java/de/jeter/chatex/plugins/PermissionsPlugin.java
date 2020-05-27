package de.jeter.chatex.plugins;

import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
public interface PermissionsPlugin {

    String getName();

    String getPrefix(Player p);

    String getSuffix(Player p);

    String[] getGroupNames(Player p);

    String getMessageFormat(Player p);

    String getGlobalMessageFormat(Player p);
}

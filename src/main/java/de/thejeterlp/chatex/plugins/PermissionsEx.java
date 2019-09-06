package de.thejeterlp.chatex.plugins;

import de.thejeterlp.chatex.utils.Config;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

public class PermissionsEx implements PermissionsPlugin {

    @Override
    public String getPrefix(Player p) {
        PermissionUser user = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().getUser(p);
        if (user == null) {
            return "";
        }
        if (!Config.MULTIPREFIXES.getBoolean()) {
            return user.getPrefix(p.getWorld().getName());
        }
        String personalPrefix = user.getOwnPrefix();
        String finalPrefix = "";
        if (personalPrefix != null && !personalPrefix.isEmpty()) {
            finalPrefix = personalPrefix;
        }
        PermissionGroup[] userGroups = user.getGroups();
        int i = 0;
        for (PermissionGroup group : userGroups) {
            String groupPrefix = group.getPrefix();
            if (groupPrefix != null && !groupPrefix.isEmpty()) {
            	if(i > 1) {
            		finalPrefix += " ";
            	}
                finalPrefix += groupPrefix;
                i ++;
            }
        }
        return finalPrefix;
    }

    @Override
    public String getSuffix(Player p) {
        PermissionUser user = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().getUser(p);
        if (user == null) {
            return "";
        }
        if (!Config.MULTISUFFIXES.getBoolean()) {
            return user.getSuffix(p.getWorld().getName());
        }
        String personalSuffix = user.getOwnSuffix();
        String finalSuffix = "";
        if (personalSuffix != null && !personalSuffix.isEmpty()) {
            finalSuffix = personalSuffix;
           
        }
        
        PermissionGroup[] userGroups = user.getGroups();
        int i = 0;
        for (PermissionGroup group : userGroups) {
            String groupSuffix = group.getSuffix();
            if (groupSuffix != null && !groupSuffix.isEmpty()) {
            	 if(i > 1) {
                 	finalSuffix += " ";
                 }
                 i ++;
                finalSuffix += groupSuffix;
            }
        }
        return finalSuffix;
    }

    @Override
    public String[] getGroupNames(Player p) {
        PermissionUser user = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().getUser(p);
        if (user != null) {
            return user.getGroupsNames();
        }
        String[] data = {""};
        return data;
    }

    @Override
    public String getName() {
        return ru.tehkode.permissions.bukkit.PermissionsEx.getPlugin().getName();
    }

    @Override
    public String getMessageFormat(Player p) {
        PermissionUser user = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().getUser(p);
        return user.getOption(Config.FORMAT.getPath(), p.getWorld().getName(), Config.FORMAT.getString());
    }

    @Override
    public String getGlobalMessageFormat(Player p) {
        PermissionUser user = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().getUser(p);
        return user.getOption(Config.GLOBALFORMAT.getPath(), p.getWorld().getName(), Config.GLOBALFORMAT.getString());
    }
}

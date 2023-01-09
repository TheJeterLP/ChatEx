package de.jeter.chatex.plugins;

import de.jeter.chatex.utils.Config;
import de.jeter.chatex.utils.LogHelper;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LuckPerms implements PermissionsPlugin {

    private net.luckperms.api.LuckPerms getAPI() {
        return LuckPermsProvider.get();
    }

    private Collection<Group> getGroups(Player p) {
        PlayerAdapter<Player> playerAdapter = getAPI().getPlayerAdapter(Player.class);
        User user = playerAdapter.getUser(p);
        Collection<Group> groups = user.getInheritedGroups(playerAdapter.getQueryOptions(p));
        return groups;
    }

    @Override
    public String getName() {
        return "LuckPerms";
    }

    @Override
    public String getPrefix(Player p) {
        if (Config.MULTIPREFIXES.getBoolean()) {
            LogHelper.debug("Getting multiprefixes from " + p.getName());
            String retprefix = "";

            for (String prefix : getAPI().getPlayerAdapter(Player.class).getMetaData(p).getPrefixes().values()) {
                LogHelper.debug(prefix);
                if (prefix.length() == 2 && prefix.startsWith("&")) {
                    retprefix += prefix;
                } else {
                    retprefix += (prefix + " ");
                }
            }

            return retprefix;
        } else {
            String prefix = getAPI().getPlayerAdapter(Player.class).getMetaData(p).getPrefix();
            return prefix != null ? prefix : "";
        }
    }

    @Override
    public String getSuffix(Player p) {
        if (Config.MULTISUFFIXES.getBoolean()) {
            LogHelper.debug("Getting multisuffixes from " + p.getName());
            String retsuffix = "";

            for (String suffix : getAPI().getPlayerAdapter(Player.class).getMetaData(p).getSuffixes().values()) {
                LogHelper.debug(suffix);
                if (suffix.length() == 2 && suffix.startsWith("&")) {
                    retsuffix += suffix;
                } else {
                    retsuffix += (suffix + " ");
                }
            }

            return retsuffix;
        } else {
            String suffix = getAPI().getPlayerAdapter(Player.class).getMetaData(p).getSuffix();
            return suffix != null ? suffix : "";
        }

    }

    @Override
    public String[] getGroupNames(Player p) {
        Collection<Group> groups = getGroups(p);
        List<String> list = new ArrayList<>();
        for (Group group : groups) {
            String name = group.getDisplayName() == null ? group.getName() : group.getDisplayName();
            list.add(name);
        }
        return list.toArray(String[]::new);
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

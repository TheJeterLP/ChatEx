package de.thejeterlp.chatex.command;

import de.thejeterlp.chatex.ChatEX;
import de.thejeterlp.chatex.command.BaseCommand.Sender;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheJeterLP
 */
public class CommandManager implements CommandExecutor {

    private final HashMap<BaseCommand, MethodContainer> cmds = new HashMap<>();
    private final CommandMap cmap;
    private final JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        CommandMap map;
        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception ex) {
            map = null;
            ex.printStackTrace();
        }

        cmap = map;
        ChatEX.debug("Getting CommandMap was " + (cmap != null ? "successfull" : "unsuccessfull"));
    }

    private void registerCommand(String name) {
        if (cmap.getCommand(name) != null) {
            return;
        }
        BukkitCommand cmd = new BukkitCommand(name);
        cmap.register(plugin.getName().toLowerCase(), cmd);
        cmd.setExecutor(this);
        ChatEX.debug("Set executor for " + name);
    }

    private BaseCommand getCommand(Command c, CommandArgs args, Sender sender) {
        BaseCommand ret = null;
        for (BaseCommand bc : cmds.keySet()) {
            if (bc.sender() != sender) {
                continue;
            }
            ChatEX.debug("bc.sender() equals sender Line 53");
            if (bc.command().equalsIgnoreCase(c.getName())) {
                ChatEX.debug("bc.command() equals c.getName()! Line 53");
                if (args.isEmpty() && bc.subCommand().trim().isEmpty()) {
                    ChatEX.debug("args and subcommand are empty! Line 55");
                    ret = bc;
                } else if (!args.isEmpty() && bc.subCommand().equalsIgnoreCase(args.getString(0))) {
                    ChatEX.debug("args are not empty and matched bc.subCommand() Line 58");
                    ret = bc;
                }
            }
        }
        ChatEX.debug("returning \n\n" + (ret != null ? ret.toString() : "null") + " \n\nline 63");
        return ret;
    }

    private Object getCommandObject(Command c, Sender sender, CommandArgs args) throws Exception {
        BaseCommand bcmd = getCommand(c, args, sender);
        if (bcmd == null) {
            for (BaseCommand bc : cmds.keySet()) {
                if (bc.sender() != sender) {
                    continue;
                }
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        ChatEX.debug("returning \n\n" + (bcmd != null ? bcmd.toString() : "null") + " \n\nline 77");
        MethodContainer container = cmds.get(bcmd);
        Method me = container.getMethod(sender);
        return me.getDeclaringClass().newInstance();
    }

    /**
     * Use this to tell the system that there are commands in the class!
     *
     * @param clazz the classfile where your command /-s are stored.
     */
    public void registerClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(CommandHandler.class)) {
            plugin.getLogger().severe("Class is no CommandHandler");
            return;
        }

        HashMap<BaseCommand, HashMap<Sender, Method>> list = new HashMap<>();

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(BaseCommand.class)) {
                ChatEX.debug("BaseCommand is present. Line 98");
                BaseCommand bc = m.getAnnotation(BaseCommand.class);

                registerCommand(bc.command());

                if (!list.containsKey(bc)) {
                    ChatEX.debug("Putting empty hashmap in list Line 104");
                    list.put(bc, new HashMap<Sender, Method>());
                }

                HashMap<Sender, Method> map = list.get(bc);

                map.put(bc.sender(), m);

                list.remove(bc);
                list.put(bc, map);
                ChatEX.debug("Putting bc, map in list Line 114");
            }
        }

        for (BaseCommand command : list.keySet()) {
            ChatEX.debug("!!Registering command " + command.command() + " subcmd " + command.subCommand() + "!!");
            HashMap<Sender, Method> map = list.get(command);

            if (cmds.containsKey(command)) {
                MethodContainer container = cmds.get(command);
                for (Sender s : container.getMethodMap().keySet()) {
                    Method m = container.getMethod(s);
                    map.put(s, m);
                }
                cmds.remove(command);
            }
            cmds.put(command, new MethodContainer(map));
        }
    }

    private Method getMethod(Command c, Sender sender, CommandArgs args) {
        BaseCommand bcmd = getCommand(c, args, sender);
        if (bcmd == null) {
            for (BaseCommand bc : cmds.keySet()) {
                if (bc.sender() != sender) {
                    continue;
                }
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        ChatEX.debug("returning \n\n" + (bcmd != null ? bcmd.toString() : "null") + " \n\nline 144");
        MethodContainer container = cmds.get(bcmd);
        Method m = container.getMethod(sender);
        try {
            return m;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean executeCommand(Command c, CommandSender s, String[] args) {
        CommandArgs a = CommandArgs.getArgs(args, 0);
        Sender sender;
        if (s instanceof Player) {
            sender = Sender.PLAYER;
        } else {
            sender = Sender.CONSOLE;
        }

        Method m = getMethod(c, sender, a);

        if (m != null) {
            m.setAccessible(true);

            BaseCommand bc = m.getAnnotation(BaseCommand.class);
            if (!bc.subCommand().trim().isEmpty() && bc.subCommand().equalsIgnoreCase(a.getString(0))) {
                a = CommandArgs.getArgs(args, 1);
            }

            CommandResult cr;

            try {
                if (sender == Sender.PLAYER) {
                    Player p = (Player) s;

                    if (bc.permission() != null && !bc.permission().trim().isEmpty()) {
                        if (!p.hasPermission(bc.permission())) {
                            cr = CommandResult.NO_PERMISSION;
                        } else {
                            cr = (CommandResult) m.invoke(getCommandObject(c, sender, a), p, a);
                        }
                    } else {
                        cr = (CommandResult) m.invoke(getCommandObject(c, sender, a), p, a);
                    }

                } else {
                    cr = (CommandResult) m.invoke(getCommandObject(c, sender, a), s, a);
                }

            } catch (Exception e) {
                e.printStackTrace();
                cr = CommandResult.SUCCESS;
            }

            if (cr
                    != null && cr.getMessage()
                    != null) {
                String perm = bc.permission() != null ? bc.permission() : "";
                s.sendMessage(cr.getMessage().replace("%cmd%", bc.command()).replace("%perm%", perm));
            }
        } else {
            s.sendMessage("§4The command was not made for your sender type!");
        }

        return true;
    }

    /*
     * Bukkit method, just ignore it.
     * Commands will be executed by itself.
     */
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        return executeCommand(cmnd, cs, strings);
    }
}

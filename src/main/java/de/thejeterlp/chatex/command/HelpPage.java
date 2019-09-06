package de.thejeterlp.chatex.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author TheJeterLP
 */
public class HelpPage {

    public HelpPage(String command) {
        this.command = command;
    }

    private final List<CommandHelp> helpPages = new ArrayList<>();
    private final List<String> HELP_TEXT = new ArrayList<>();
    private final String command;

    public void addPage(String argument, String description) {
        if (argument.isEmpty()) {
            helpPages.add(new CommandHelp(command, description));
        } else {
            helpPages.add(new CommandHelp(command + " " + argument, description));
        }
    }

    public void prepare() {
        if (helpPages == null || helpPages.isEmpty()) return;
        HELP_TEXT.add(ChatColor.GREEN + "------------------------" + ChatColor.BLUE + "Help" + ChatColor.GREEN + "-------------------------");
        for (CommandHelp ch : helpPages) {
            HELP_TEXT.add(ch.getText());
        }
        HELP_TEXT.add(ChatColor.GREEN + "-----------------------------------------------------");
    }

    public boolean sendHelp(CommandSender s, CommandArgs args) {
        if (args.getLength() == 1 && (args.getString(0).equalsIgnoreCase("?") || args.getString(0).equalsIgnoreCase("help")) && !HELP_TEXT.isEmpty()) {
            for (String string : HELP_TEXT) {
                s.sendMessage(string);
            }
            return true;
        }
        return false;
    }

    private class CommandHelp {

        private final String FULL_TEXT;

        public CommandHelp(String cmd, String description) {
            this.FULL_TEXT = ChatColor.GOLD + cmd + ChatColor.GRAY + " - " + description;
        }

        public String getText() {
            return FULL_TEXT;
        }

    }

}

package de.thejeterlp.chatex;

import de.thejeterlp.chatex.command.BaseCommand;
import de.thejeterlp.chatex.command.CommandArgs;
import de.thejeterlp.chatex.command.CommandHandler;
import de.thejeterlp.chatex.command.CommandResult;
import de.thejeterlp.chatex.command.HelpPage;
import de.thejeterlp.chatex.utils.Locales;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author TheJeterLP
 */
@CommandHandler
public class ChatExCommandHandler {

    private final HelpPage helpPage = new HelpPage("chatex");

    public ChatExCommandHandler() {
        helpPage.addPage("reload", Locales.COMMAND_RELOAD_DESCRIPTION.getString());
        helpPage.addPage("clear", Locales.COMMAND_CLEAR_DESCRIPTION.getString());
        helpPage.prepare();
    }

    @BaseCommand(command = "chatex", sender = BaseCommand.Sender.CONSOLE)
    public CommandResult executeConsole(CommandSender sender, CommandArgs args) {
        if (helpPage.sendHelp(sender, args)) return CommandResult.SUCCESS;
        if (!args.isEmpty()) return CommandResult.ERROR;
        sender.sendMessage("§aChatEx plugin by " + ChatEX.getInstance().getDescription().getAuthors());
        return CommandResult.SUCCESS;
    }

    @BaseCommand(command = "chatex", sender = BaseCommand.Sender.PLAYER)
    public CommandResult executePlayer(Player sender, CommandArgs args) {
        return executeConsole(sender, args);
    }

    @BaseCommand(command = "chatex", sender = BaseCommand.Sender.CONSOLE, permission = "chatex.reload", subCommand = "reload")
    public CommandResult executeSubReloadConsole(CommandSender sender, CommandArgs args) {
        Bukkit.getPluginManager().disablePlugin(ChatEX.getInstance());
        Bukkit.getPluginManager().enablePlugin(ChatEX.getInstance());
        sender.sendMessage(Locales.MESSAGES_RELOAD.getString());
        return CommandResult.SUCCESS;
    }

    @BaseCommand(command = "chatex", sender = BaseCommand.Sender.PLAYER, permission = "chatex.reload", subCommand = "reload")
    public CommandResult executeSubReloadPlayer(Player sender, CommandArgs args) {
        return executeSubReloadConsole(sender, args);
    }

    @BaseCommand(command = "chatex", sender = BaseCommand.Sender.CONSOLE, permission = "chatex.clear", subCommand = "clear")
    public CommandResult executeSubClearConsole(CommandSender sender, CommandArgs args) {
        for (int i = 0; i < 25; i++) {
            Bukkit.broadcastMessage("\n");
        }
        String who = Locales.COMMAND_CLEAR_UNKNOWN.getString();
        if ((sender instanceof ConsoleCommandSender) || (sender instanceof BlockCommandSender)) {
            who = Locales.COMMAND_CLEAR_CONSOLE.getString();
        } else if (sender instanceof Player) {
            who = sender.getName();
        }
        Bukkit.broadcastMessage(Locales.MESSAGES_CLEAR.getString() + who);
        return CommandResult.SUCCESS;
    }

    @BaseCommand(command = "chatex", sender = BaseCommand.Sender.PLAYER, permission = "chatex.clear", subCommand = "clear")
    public CommandResult executeSubClearPlayer(Player sender, CommandArgs args) {
        return executeSubClearConsole(sender, args);
    }

}

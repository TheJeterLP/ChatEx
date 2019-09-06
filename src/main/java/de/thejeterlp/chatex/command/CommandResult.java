package de.thejeterlp.chatex.command;

import org.bukkit.ChatColor;


public enum CommandResult {


    SUCCESS(null),
    NO_PERMISSION(ChatColor.DARK_RED + "[ERROR] " + ChatColor.GRAY + "You don't have permission for this! " + ChatColor.RED + "(%perm%)"),
    ERROR(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "Wrong usage! Please type " + ChatColor.GOLD + "/%cmd% help " + ChatColor.GRAY + "!"),
    ONLY_PLAYER(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "This command is only for players!"),
    NOT_ONLINE(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "That player is not online."),
    NOT_A_NUMBER(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "It has to be a number!");
    
    private final String msg;

    CommandResult(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return this.msg;
    }
}

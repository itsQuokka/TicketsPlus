package com.ticketsplus.commands;

import org.bukkit.command.CommandSender;

public abstract class CommandExecutor {

    private String command;
    private String permission;
    private String usage;
    private int length;

    public abstract void execute(CommandSender sender, String[] args);

    protected void setCommand(String command) {
        this.command = command;
    }

    protected void setPermission(String permission) {
        this.permission = permission;
    }

    protected void setLength(int length) {
        this.length = length;
    }

    protected void setUsage(String usage) {
        this.usage = usage;
    }

    String getCommand() {
        return command;
    }

    String getPermission() {
        return permission;
    }

    int getLength() {
        return length;
    }

    String getUsage() {
        return usage;
    }

}

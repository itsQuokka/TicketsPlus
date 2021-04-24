package com.ticketsplus.commands;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.custom.*;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;

public class CommandHandler implements org.bukkit.command.CommandExecutor {

    private HashMap<String, CommandExecutor> commands = new HashMap<>();
    private TicketsPlus plugin;

    public CommandHandler(TicketsPlus plugin) {

        this.plugin = plugin;

        commands.put("create", new T_Create(plugin));
        commands.put("notify", new T_ToggleNotifications(plugin));
        commands.put("status", new T_Status(plugin));
        commands.put("debug", new T_Debug(plugin));
        commands.put("info", new T_Info(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("ticket")) {

            if (args[0] != null) {

                String name = args[0].toLowerCase();

                if (commands.containsKey(name)) {

                    final CommandExecutor command = commands.get(name);

                    if (sender instanceof ConsoleCommandSender){
                        sender.sendMessage("You cannot use this command from here.");
                        return true;
                    }

                    if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                        sender.sendMessage(StringUtils.color("&7[&cTicket&7] &cYou cannot issue that command!"));
                        return true;
                    }

                    if (command.getCommand().equalsIgnoreCase("create")){
                        // ticket create this is my message
                        if (args.length <= 1){
                            sender.sendMessage(ChatColor.RED + "&7[&cTicket&7] &fYou need to specify an issue!");
                            return true;
                        }
                        command.execute(sender, args);
                        return true;
                    }

                    if (command.getLength() > args.length){
                        sender.sendMessage(ChatColor.RED + "&7[&cTicket&7] &cCorrect usage: &7" + command.getUsage());
                        return true;
                    }

                    command.execute(sender, args);
                }
            }
        }
        return false;
    }
}

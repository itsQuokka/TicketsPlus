package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class T_Create extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Create(TicketsPlus plugin) {
        this.plugin = plugin;
        this.setCommand("create");
        this.setPermission("ticket.create");
        this.setUsage("/ticket create <message>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final Player player = (Player) sender;

        if (args.length <= 1){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou have to specify a message!"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++){

            stringBuilder.append(args[i]).append(' ');

        }

        Ticket t = new Ticket(player, stringBuilder.toString());

        plugin.getTicketManager().addTicket(t);

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've successfully submitted a ticket!"));

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou can check you're ticket status with &c/ticket status&f!"));


    }
}

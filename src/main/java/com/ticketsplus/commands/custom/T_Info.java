package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class T_Info extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Info(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("info");
        this.setPermission("ticket.info");
        this.setLength(2);
        this.setUsage("/ticket info <id>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final Player player = (Player) sender;

        if (args.length <= 0){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou need to specify an ID to lookup!"));
            return;
        }

        final String tempId = args[1];
        final Ticket tempTicket = plugin.getTicketManager().findTicket(tempId);

        if (tempTicket == null){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThere was no ticket found under that ID!"));
            return;
        }

        plugin.getTicketManager().openTicketInventory(player, tempTicket);

    }
}

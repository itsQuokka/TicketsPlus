package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class T_Status extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Status(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("status");
        this.setPermission("ticket.status");
        this.setLength(2);
        this.setUsage("/ticket status");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        final Ticket tempTicket = plugin.getTicketManager().findTicket(player);

        if (tempTicket == null){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou don't have any open tickets!"));
            return;
        }

        plugin.getTicketManager().openTicketInventory(player, tempTicket);
    }
}

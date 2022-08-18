package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.events.custom.TicketUpdateEvent;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.obj.UpdateType;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class T_Claim extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Claim(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("claim");
        this.setPermission("ticket.claim");
        this.setLength(2);
        this.setUsage("/ticket claim <id>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        Ticket ticket = plugin.getTicketManager().findTicket(args[1]);

        if (ticket == null){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThere was no ticket found under that ID!"));
            return;
        }

        if (ticket.getIntStatus() == 2) {
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThat ticket seems to have been closed!"));
            return;
        }

        if (ticket.hasAssignee()){
            if (ticket.getAssignedUUID().equals(player.getUniqueId())) {
                player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou're already assigned to that ticket!"));
                return;
            }
            if (!player.hasPermission("ticket.claim.override")) player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThat ticket already has an assignee!"));
            return;
        }

        if (ticket.isPlayerOnline()) {
            ticket.sendMessage(StringUtils.color("&7[&cTicket&7] &fYour ticket has been updated! Check with &c/ticket status&f!"));
        }

        ticket.setAssignee(player);

        Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.ASSIGNED));

        plugin.getTicketManager().addComment(ticket, StringUtils.color("&f" + ticket.getDate() + " - &c" + player.getName() + "&f claimed the ticket."), true);

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've been assigned yourself to &c" + ticket.getPlayerName() + "&f's ticket!"));

    }
}

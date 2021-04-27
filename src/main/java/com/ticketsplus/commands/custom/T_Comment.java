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

import java.util.Objects;

public class T_Comment extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Comment(TicketsPlus plugin) {
        this.plugin = plugin;
        this.setCommand("comment");
        this.setPermission("ticket.comment");
        this.setUsage("/ticket comment <id> <message>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        Ticket ticket = plugin.getTicketManager().findTicket(args[1]);

        if (ticket == null){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThere was no ticket found under that ID!"));
            return;
        }

        if (ticket.getCurrentStatus().equalsIgnoreCase("closed")) {
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThat ticket seems to have been closed!"));
            return;
        }

        if (!player.hasPermission("ticket.comment.override")) {
            if (ticket.hasAssignee()) {
                if (!ticket.getAssignedUUID().equals(player.getUniqueId())) {
                    player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou are not assigned to that ticket!"));
                    return;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            stringBuilder.append(args[i] + ' ');
        }

        if (ticket.isPlayerOnline()) {

            Objects.requireNonNull(Bukkit.getPlayer(ticket.getPlayerUUID())).sendMessage(StringUtils.color(
                    "&7[&cTicket&7] &fYour ticket has been updated! Check with &c/ticket status&f!"));
        }

        // Required to be first in order to process correct ticket event.
        ticket.addComment(StringUtils.color("&c" + player.getName() + " &f- &c" + ticket.getDate()));

        ticket.addComment(StringUtils.color("&f" + stringBuilder.toString()));

        ticket.addStaffNote(StringUtils.color("&f" + ticket.getDate() + " - &c" + player.getName() + "&f commented on ticket."));

        Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.COMMENT));

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &fSuccessfully added a comment to the ticket!"));

    }
}

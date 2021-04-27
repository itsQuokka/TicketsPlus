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

public class T_Close extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Close(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("close");
        this.setPermission("ticket.close");
        this.setLength(2);
        this.setUsage("/ticket close <id>");
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
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThat ticket seems to already have been closed!"));
            return;
        }

        if (ticket.hasAssignee()){
            if (!player.hasPermission("ticket.close.override")) {
                if (!ticket.getAssignedUUID().equals(player.getUniqueId())) {
                    player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThat ticket can only be closed by: " + ticket.getAssignedName()));
                    return;
                }
            }
        }

        if (ticket.isPlayerOnline()) {
            Objects.requireNonNull(Bukkit.getPlayer(ticket.getPlayerUUID())).sendMessage(StringUtils.color(
                    "&7[&cTicket&7] &fYour ticket has been closed by &c"  + player.getName() + "&f!"));
        }

        // Required to be first in order to process correct ticket event.
        ticket.setCurrentStatus(2);

        Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.CLOSED));

        ticket.addStaffNote(StringUtils.color("&f" + ticket.getDate() + " - &c" + player.getName() + "&f closed the ticket."));

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've closed &c" + ticket.getPlayerName() + "&f's ticket!"));

    }
}

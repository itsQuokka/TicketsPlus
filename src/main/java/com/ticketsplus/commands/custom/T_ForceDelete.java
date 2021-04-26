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

public class T_ForceDelete extends CommandExecutor {

    private TicketsPlus plugin;
    public T_ForceDelete(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("delete");
        this.setPermission("ticket.forcedelete");
        this.setLength(2);
        this.setUsage("/ticket delete <id>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        Ticket ticket = plugin.getTicketManager().findTicket(args[1]);

        if (ticket == null){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThere was no ticket found under that ID!"));
            return;
        }

        if (ticket.isPlayerOnline()) {

            Objects.requireNonNull(Bukkit.getPlayer(ticket.getPlayerUUID())).sendMessage(
                    StringUtils.color("&7[&cTicket&7] &fYour ticket has been deleted by: " + player.getName() + "!"));
        }

        Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.DELETED));

        plugin.getTicketManager().deleteTicket(ticket, true);

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've forcefully deleted " + ticket.getPlayerName() + "'s ticket!"));

    }
}

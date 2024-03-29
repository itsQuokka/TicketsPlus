package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class T_Debug extends CommandExecutor {

    private TicketsPlus plugin;
    public T_Debug(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("debug");
        this.setPermission("ticket.debug");
        this.setLength(2);
        this.setUsage("/ticket debug <id>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final Player player = (Player) sender;

        final Ticket ticket = plugin.getTicketManager().findTicket(args[1]);

        if (ticket == null){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThere was no ticket found under that ID!"));
            return;
        }

        player.sendMessage(StringUtils.color("&fGrabbing ticket details for errors."));

        player.sendMessage(StringUtils.color("&cTicket ID: &f" + check(ticket.getID())));
        player.sendMessage(StringUtils.color("&cPlayer Name: &f" + check(ticket.getPlayerName())));
        player.sendMessage(StringUtils.color("&cCreation Date: &f" + check(ticket.getCreationDate())));
        player.sendMessage(StringUtils.color("&cIssue Message: &f" + check(ticket.getIssuedMessage())));
        player.sendMessage(StringUtils.color("&cLocation: &f" + check(ticket.getLocation())));

        player.sendMessage(StringUtils.color("&7[&cTicket&7] &f&m--------------------"));

    }

    private String check(Object obj){
        return Objects.isNull(obj) ? "Not found." : "Found.";
    }
}

package com.ticketsplus.events.listeners;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.obj.UpdateType;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TicketUpdateEvent implements Listener {

    private TicketsPlus plugin;
    public TicketUpdateEvent(TicketsPlus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onTicketCreated(com.ticketsplus.events.custom.TicketUpdateEvent event){

        if (event.isCancelled()) return;

        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.hasPermission("ticket.notify.all")) {
                if (!plugin.getPlayerManager().getPlayerList().contains(player.getUniqueId())) {
                    player.sendMessage(StringUtils.color(getUpdateMessage(event.getTicket(), event.getUpdateType())));
                }
            }
        }
    }

    private String getUpdateMessage(Ticket ticket, UpdateType updateType){
        String message = "";
        switch (updateType) {
            case OPEN:
                message = "&7[&cTicket&7] &c" + ticket.getPlayerName() + " &fhas created a ticket. #&c" + ticket.getID() + "&f!";
                break;
            case CLOSED:
                message = "&7[&cTicket&7] &c" + ticket.getPlayerName() + " &fhas closed a ticket. #&c" + ticket.getID() + "&f!";
                break;
            case ASSIGNED:
                message = "&7[&cTicket&7] &c" + ticket.getAssignedName() + " &fhas assigned themselves to a ticket. #&c" + ticket.getID() + "&f!";
                break;
            case COMMENT:
                message = "&7[&cTicket&7] &c" + ticket.getAssignedName() + " &fhas added a comment to #&c" + ticket.getID() + "&f!";
                break;
            case DELETED:
                message = "&7[&cTicket&7] &c" + ticket.getPlayerName() + " has deleted &c" + ticket.getPlayerName() + "&f's ticket!";
                break;
            case SELF_CLOSED:
                message = "&7[&cTicket&7] &c" + ticket.getPlayerName() + " &fhas closed their own ticket.";
                break;
        }
        return message;
    }
}

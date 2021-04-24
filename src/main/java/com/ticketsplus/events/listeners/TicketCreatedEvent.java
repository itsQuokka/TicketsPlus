package com.ticketsplus.events.listeners;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TicketCreatedEvent implements Listener {

    private TicketsPlus plugin;
    public TicketCreatedEvent(TicketsPlus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onTicketCreated(com.ticketsplus.events.custom.TicketCreatedEvent event){

        if (event.isCancelled()) return;

        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.hasPermission("ticket.notify")) {
                if (!plugin.getPlayerManager().getPlayerList().contains(player.getUniqueId())) {
                    player.sendMessage(StringUtils.color("&7[&cTicket&7] &fNew ticket has been created by: "
                            + event.getTicket().getPlayerName()));
                }
            }
        }
    }
}

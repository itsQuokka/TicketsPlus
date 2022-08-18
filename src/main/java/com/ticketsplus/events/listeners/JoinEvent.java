package com.ticketsplus.events.listeners;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private TicketsPlus plugin;
    public JoinEvent(TicketsPlus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!event.getPlayer().isOnline()) return;
            Ticket tempTicket = plugin.getTicketManager().findTicket(event.getPlayer());

            if (tempTicket == null) return;

            event.getPlayer().sendMessage(StringUtils.color("&7[&cTicket&7] &fMake sure to check your ticket with: &c/ticket status " + tempTicket.getID()));
            event.getPlayer().sendMessage(StringUtils.color("&7[&cTicket&7] &fOnce you close your own ticket, it is gone permanently!"));
        }, 20 * 2L);
    }
}

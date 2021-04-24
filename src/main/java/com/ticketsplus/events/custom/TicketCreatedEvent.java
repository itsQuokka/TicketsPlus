package com.ticketsplus.events.custom;

import com.ticketsplus.obj.Ticket;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TicketCreatedEvent extends Event {

    private final Ticket ticket;
    private boolean isCancelled;

    public TicketCreatedEvent (Ticket ticket){
        this.ticket = ticket;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Ticket getTicket() {
        return ticket;
    }
}

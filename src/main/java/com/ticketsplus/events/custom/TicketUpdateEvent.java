package com.ticketsplus.events.custom;

import com.ticketsplus.obj.Ticket;
import com.ticketsplus.obj.UpdateType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TicketUpdateEvent extends Event {

    private final Ticket ticket;
    private final UpdateType updateType;
    private boolean isCancelled;

    public TicketUpdateEvent(Ticket ticket, UpdateType updateType){
        this.ticket = ticket;
        this.updateType = updateType;
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

    public UpdateType getUpdateType() {
        return updateType;
    }
}

package com.ticketsplus;

import com.ticketsplus.commands.CommandHandler;
import com.ticketsplus.events.listeners.JoinEvent;
import com.ticketsplus.events.listeners.TicketCreatedEvent;
import com.ticketsplus.events.listeners.TicketUpdateEvent;
import com.ticketsplus.managers.PlayerManager;
import com.ticketsplus.managers.TicketManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TicketsPlus extends JavaPlugin {

    private TicketManager ticketManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {

        runWipe();

        registerManagers();

        registerCommands();

        registerEvents();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerManagers() {

        playerManager = new PlayerManager();
        ticketManager = new TicketManager(this);

    }

    private void registerCommands() {

        getCommand("ticket").setExecutor(new CommandHandler(this));

    }

    private void registerEvents() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new TicketCreatedEvent(this), this);
        pluginManager.registerEvents(new TicketUpdateEvent(this), this);
        pluginManager.registerEvents(new JoinEvent(this), this);

    }

    private void runWipe(){
        ZonedDateTime nextTime = ZonedDateTime.ofInstant(Instant.now(),
                ZoneId.systemDefault()).plusDays(this.getConfig().getInt("database-clearance")).truncatedTo(ChronoUnit.DAYS);

        long delay = Duration.between(ZonedDateTime.now(), nextTime).getSeconds() * 20;

        this.getServer().getScheduler().runTaskLater(this, () -> {

            // Todo: Get rid of all MySQL tickets with a ticket status of 2.

            runWipe();
        }, delay);
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public PlayerManager getPlayerManager() { return playerManager; }
}

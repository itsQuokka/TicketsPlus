package com.ticketsplus;

import com.ticketsplus.commands.CommandHandler;
import com.ticketsplus.events.listeners.InventoryClick;
import com.ticketsplus.events.listeners.JoinEvent;
import com.ticketsplus.events.listeners.TicketCreatedEvent;
import com.ticketsplus.events.listeners.TicketUpdateEvent;
import com.ticketsplus.managers.PlayerManager;
import com.ticketsplus.managers.StorageManager;
import com.ticketsplus.managers.TicketManager;
import com.ticketsplus.obj.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TicketsPlus extends JavaPlugin {

    private TicketManager ticketManager;
    private PlayerManager playerManager;
    private StorageManager storageManager;

    /**
     * Project is ran.
     */
    @Override
    public void onEnable() {

        registerManagers();
        registerCommands();
        registerEvents();

        runWipe();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerManagers() {

        playerManager = new PlayerManager();
        ticketManager = new TicketManager(this);
        storageManager = new StorageManager(this);

    }

    private void registerCommands() {

        getCommand("ticket").setExecutor(new CommandHandler(this));

    }

    private void registerEvents() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new TicketCreatedEvent(this), this);
        pluginManager.registerEvents(new TicketUpdateEvent(this), this);
        pluginManager.registerEvents(new JoinEvent(this), this);
        pluginManager.registerEvents(new InventoryClick(), this);

    }

    private void runWipe() {

        this.getServer().getScheduler().runTaskLater(this, () -> {
            int success = 0, attempt = 0, failure = 0;
            List<String> idRemovalList = new ArrayList<>();
            if (!this.ticketManager.getTicketCache().isEmpty()) {
                for (Ticket ticket : this.ticketManager.getTicketCache()) {
                    if (ticket.getIntStatus() == 2){
                        idRemovalList.add(ticket.getID());
                        this.ticketManager.getTicketCache().remove(ticket);
                        attempt++;
                    }
                }
            }

            if (this.storageManager.isDatabase()){
                // Todo: Database clear.
            } else {
                File storageFile = new File(this.getDataFolder(), "tickets");
                File[] files = storageFile.listFiles((file, s) -> {
                    file.getName().endsWith(".yml");
                    return false;
                });
                if (files != null) {
                    for (File file : files) {
                        for (String str : idRemovalList) {
                            final String fileName = file.getName().substring(0, file.getName().length() - 4);
                            if (fileName.equalsIgnoreCase(str)) {
                                if (file.delete()) {
                                    success++;
                                } else {
                                    failure++;
                                }
                            }
                        }
                    }
                    this.getLogger().log(Level.INFO, "Success/Failed/Total Tickets Cleared - " + success + "/" + failure + "/" + attempt);
                }
            }
            runWipe();
        },  60 * 60 * 20L * this.getConfig().getInt("ticket-clearance"));
    }

    public boolean isDatabase(){
        return this.getConfig().getBoolean("database-storage");

    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public PlayerManager getPlayerManager() { return playerManager; }

    public StorageManager getStorageManager() { return storageManager; }
}

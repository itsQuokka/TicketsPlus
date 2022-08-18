package com.ticketsplus.managers;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.obj.Ticket;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class StorageManager {

    private boolean database;

    private TicketsPlus plugin;
    public StorageManager(TicketsPlus plugin){
        this.plugin = plugin;

        this.database = plugin.isDatabase();
        makeTicketFolder();
        loadTickets();
    }

    private void loadTickets() {

        if (plugin.getResource("messages.yml") != null) {
            try (InputStream inputStream = plugin.getResource("messages.yml")) {

                File file = new File("second.file.path");

                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!database){

            File folder = new File(plugin.getDataFolder(), "tickets");
            File[] files = new File(folder.getPath()).listFiles((dir, name) -> name.endsWith(".yml"));

            for (int i = 0; i < files.length; i++) {

                String identifier = Objects.requireNonNull(files[i].getName().substring(0, files[i].getName().length() - 4));

                System.out.println(identifier);

                File customYaml = new File(plugin.getDataFolder(), "tickets/" + identifier + ".yml");
                FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYaml);

                plugin.getTicketManager().getTicketCache().add(
                        new Ticket(identifier,
                                customConfig.getString("ticket.playerName"),
                                customConfig.get("ticket.playerUUID") == null ? null : UUID.fromString(customConfig.getString("playerUUID")),
                                (Location) customConfig.get("ticket.location"),
                                customConfig.getString("ticket.issuedMessage"),
                                customConfig.getString("ticket.creationDate"),
                                customConfig.getInt("ticket.status"),
                                customConfig.getString("ticket.assigneeName"),
                                customConfig.get("ticket.assigneeName") == null ? null : UUID.fromString(customConfig.getString("assigneeUUID")),
                                customConfig.getStringList("ticket.comments"),
                                customConfig.getStringList("ticket.staffNotes")));
            }
        }
    }

    void saveTicket(Ticket ticket){

        if (!database) {

            makeTicketFolder();

            File customYaml = new File(plugin.getDataFolder(), "tickets/" + ticket.getID() + ".yml");
            FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYaml);

            customConfig.set("ticket.playerName", ticket.getPlayerName());
            customConfig.set("ticket.playerUUID", ticket.getPlayerUUID().toString());
            customConfig.set("ticket.location", ticket.getLocation());
            customConfig.set("ticket.issuedMessage", ticket.getIssuedMessage());
            customConfig.set("ticket.creationDate", ticket.getCreationDate());
            customConfig.set("ticket.status", ticket.getIntStatus());
            customConfig.set("ticket.assigneeName", ticket.getAssignedName());
            customConfig.set("ticket.assigneeUUID", Objects.isNull(ticket.getAssignedName()) ? null : ticket.getAssignedUUID().toString());
            customConfig.set("ticket.comments", ticket.getComments().isEmpty() ? Collections.emptyList() : ticket.getComments());
            customConfig.set("ticket.staffNotes", ticket.getStaffNotes().isEmpty() ? Collections.emptyList() : ticket.getStaffNotes());

            try {
                customConfig.save(customYaml);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void updateTicket(String uid, HashMap<String, Object> map){

        if (!database) {
            File customConfig = new File(plugin.getDataFolder(), "tickets/" + uid + ".yml");
            FileConfiguration customConfigFile = YamlConfiguration.loadConfiguration(customConfig);

            for (String str : map.keySet()){
                if (customConfigFile.get("ticket." + str) == null) {
                    customConfigFile.addDefault("ticket." + str, map.get(str));
                } else {
                    customConfigFile.set("ticket." + str, map.get(str));
                }
            }

            try {
                customConfigFile.save(customConfig);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
            }
        }
    }

    public boolean isDatabase(){
        return database;
    }

    private void makeTicketFolder() {
        File ticketFolder = new File(plugin.getDataFolder(), "tickets");
        if (!ticketFolder.exists()) {
            ticketFolder.mkdirs();
        }
    }
}

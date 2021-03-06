package com.ticketsplus.obj;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Ticket {

    private String identifier;

    /* 4P Variables */

    private String playerName;
    private UUID playerUUID;

    private String issuedMessage;

    private String creationDate;
    private Location location;

    /* 4S Variables */

    private String assignedName;
    private UUID assignedUUID;

    private List<String> comments, staffnotes;
    private int ticketStatus;

    public Ticket(Player player, String message){
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        this.issuedMessage = message;
        this.location = player.getLocation();
        this.creationDate = this.getDate();
        this.identifier = generateID();
        this.ticketStatus = 0;
    }

    public void setAssignee(Player player){
        this.assignedName = player.getName();
        this.assignedUUID = player.getUniqueId();

        this.ticketStatus = 1;
    }

    public String getID() {
        return this.identifier;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getIssuedMessage() {
        return issuedMessage;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Location getLocation() {
        return location;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public UUID getAssignedUUID() {
        return assignedUUID;
    }

    public List<String> getComments() {
        return comments;
    }

    public boolean hasAssignee() { return assignedUUID != null; }

    public boolean isPlayerOnline() {
        return Objects.requireNonNull(Bukkit.getPlayer(this.playerUUID)).isOnline();
    }

    /* Utility Methods */

    public void addComment(String string){
        if (this.comments == null){
            this.comments = new ArrayList<>();
        }
        this.comments.add(string);
    }

    public void addStaffNote(String string){
        if (this.staffnotes == null){
            this.staffnotes = new ArrayList<>();
        }
        this.staffnotes.add(string);
    }

    public String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private String generateID() {
        String uid = UUID.randomUUID().toString();
        return uid.substring(0, uid.indexOf('-'));
    }

    public void setCurrentStatus(int status){
        this.ticketStatus = status;
    }

    public String getCurrentStatus(){
        if (this.ticketStatus == 0){
            return "OPEN";
        } else if (this.ticketStatus == 1){
            return "ASSIGNED";
        } else if (this.ticketStatus == 2){
            return "CLOSED";
        }
        return "";
    }

}

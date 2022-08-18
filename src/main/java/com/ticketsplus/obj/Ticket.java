package com.ticketsplus.obj;

import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        this.identifier = generateID();
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        this.location = player.getLocation();
        this.issuedMessage = message;
        this.creationDate = this.getDate();
        this.ticketStatus = 0;
        this.assignedName = null;
        this.assignedUUID = null;
        this.comments = Collections.emptyList();
        this.staffnotes = Collections.emptyList();
    }

    public Ticket(String identifier, String playerName, UUID playerUUID, Location location, String issuedMessage, String creationDate, int status, String assigneeName, UUID assigneeUUID, List<String> comments, List<String> staffNotes) {
        this.identifier = identifier;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.location = location;
        this.issuedMessage = issuedMessage;
        this.creationDate = creationDate;
        this.ticketStatus = status;
        this.assignedName = assigneeName;
        this.assignedUUID = assigneeUUID;
        this.comments = comments.isEmpty() ? Collections.emptyList() : comments;
        this.staffnotes = staffNotes.isEmpty() ? Collections.emptyList() : staffNotes;
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

    public boolean hasAssignee() { return getIntStatus() == 1; }

    public boolean isPlayerOnline() {
        return Objects.requireNonNull(Bukkit.getPlayer(this.playerUUID)).isOnline();
    }

    /* Utility Methods */

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

    public String getTextStatus(){
        if (this.ticketStatus == 0){
            return "OPEN";
        } else if (this.ticketStatus == 1){
            return "ASSIGNED";
        } else if (this.ticketStatus == 2){
            return "CLOSED";
        }
        return "";
    }

    public Integer getIntStatus() {
        return ticketStatus;
    }

    public List<String> getStaffNotes() {
        return staffnotes;
    }

    public void setComments(ArrayList<String> strings) {
        this.comments = strings;
    }

    public void setStaffnotes(ArrayList<String> strings){
        this.staffnotes = strings;
    }

    public void sendMessage(String message) {
        Objects.requireNonNull(Bukkit.getPlayer(this.getPlayerUUID())).sendMessage(StringUtils.color(message));
    }
}

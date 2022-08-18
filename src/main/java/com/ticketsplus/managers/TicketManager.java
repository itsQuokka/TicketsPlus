package com.ticketsplus.managers;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.events.custom.TicketUpdateEvent;
import com.ticketsplus.inventory.CustomHolder;
import com.ticketsplus.inventory.Icon;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.obj.UpdateType;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class TicketManager {

    private List<Ticket> ticketCache;
    private int bukkitRunnable;

    private TicketsPlus plugin;
    public TicketManager(TicketsPlus plugin) {
        this.plugin = plugin;

        this.ticketCache = new ArrayList<>();

        if (plugin.isDatabase()){

        }
    }

    /**
     * Add a ticket to the current server cache. Prepare for DB upload.
     * @param ticket the ticket object to add to cache list.
     */
    public void addTicket(Ticket ticket) {

        if (ticketCache == null){
            Bukkit.getLogger().log(Level.SEVERE, "TC-AT - Cache null.");
            return;
        }

        ticketCache.add(ticket);

        /* Testing */
        plugin.getStorageManager().saveTicket(ticket);

        Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.OPEN));

    }

    /**
     * Delete the ticket from the cache, and also try and remove from Database.
     * @param ticket the ticket to be removed
     * @return completion of ticket removal
     */
    public boolean deleteTicket(Ticket ticket, boolean force){

        if (ticketCache == null){
            Bukkit.getLogger().log(Level.SEVERE, "TM-DT - Cache null.");
            return false;
        }

        Ticket tempTicket = ticketCache.stream().filter(t -> t.getID().equals(ticket.getID())).findFirst().orElse(null);

        if (tempTicket == null) {
            Bukkit.getLogger().log(Level.SEVERE, "TM-DT - Unable to delete ticket ID - " + ticket.getID());
            return false;
        }

        if (force){
            ticketCache.remove(ticket);
            File f = new File(plugin.getDataFolder(), "tickets/" + ticket.getID() + ".yml");
            if (f.exists()){
                f.delete();
            }
        } else {
            ticket.setCurrentStatus(2);
        }

        return true;
    }

    /**
     * Add a comment to a ticket, rather it be for the player or staff notes.
     * @param ticket the ticket to add the comment to
     * @param message the message to add
     * @param privateNote if it is meant for the player, or for staff
     */
    public void addComment(Ticket ticket, String message, boolean privateNote){
        if (!privateNote) {
            if (ticket.getComments() == Collections.EMPTY_LIST) {
                ticket.setComments(new ArrayList<>());
            }
            ticket.getComments().add(message);
        } else {
            if (ticket.getStaffNotes() == Collections.EMPTY_LIST){
                ticket.setStaffnotes(new ArrayList<>());
            }
            ticket.getStaffNotes().add(message);
        }
    }

    /**
     * Database transfer method.
     * @param ticket the ticket to upload to database.
     */
    private void uploadTicket(Ticket ticket) {

    }

    /**
     * Search for a ticket in the cache, and if not found, check the database.
     * @param player search for ticket under player object.
     * @return returns ticket if not null.
     */
    public Ticket findTicket(Player player) {
        for (Ticket ticket : ticketCache){
            if (ticket.getPlayerUUID().toString().equals(player.getUniqueId().toString())){
                return ticket;
            }
        }
        return null;
    }

    /**
     * Search for a ticket in the cache, and if not found, check the database.
     * @param uid search for ticket with unique identifier
     * @return returns ticket if not null
     */
    public Ticket findTicket(String uid){
        for (Ticket ticket : ticketCache){
            if (ticket.getID().equals(uid)){
                return ticket;
            }
        }
        return null;
    }

    /**
     * Uses both search methods to find a ticket in the cache, and/or database.
     * @param uid the uid of the ticket
     * @param player the player who created the ticket
     * @return returns the ticket found if not null
     */
    public Ticket search(String uid, Player player) {
        Ticket uidTicket = findTicket(uid);
        if (uidTicket != null) return uidTicket;
        return findTicket(player);
    }
    /**
     * Open the inventory to view a ticket.
     * @param player the player to open the inventory.
     * @param ticket the ticket to base the inventory on
     */
    public void openTicketInventory(Player player, Ticket ticket) {

        /* Location [ Compass ] */
        Icon locationIcon = new Icon(
            new ItemManager(Material.COMPASS, 1)
                .setDisplayName("&c&lLocation")
                .addLoreLine(new ArrayList<String>() {{
                    this.add("&fThe location where the ticket was created.");
                    this.add("&fX: &c" + ticket.getLocation().getX()
                            + " &fY: &c" + ticket.getLocation().getY()
                            + " &fZ: &c" + ticket.getLocation().getZ());
                    if (player.hasPermission("ticket.teleport")) {
                        this.add("&7&m---------------");
                        this.add("&aYou can click me to teleport to the location!");
                    }
                }}).build()
        ).addClickAction(p -> {
            if (p.hasPermission("ticket.teleport")) {
                p.teleport(ticket.getLocation());
            }
        });

        /* Map [ Comments ] */

        ItemManager itemManager = new ItemManager(Material.MAP, 1);
        itemManager.setDisplayName("&c&lStaff Comments");
        if (!Objects.isNull(ticket.getComments()) && !ticket.getComments().isEmpty()) {
            for (String str : ticket.getComments()){
                itemManager.addLoreLine(str);
            }
        } else {
            itemManager.addLoreLine("&fThere are no comments available!");
        }

        Icon commentsIcon = new Icon(itemManager.build());

        /* Leather Helmet [ Location ] */

        Icon assigneeIcon = new Icon(
            new ItemManager(Material.LEATHER_HELMET, 1)
                .setDisplayName("&c&lAssignee")
                .addLoreLine(Objects.isNull(ticket.getAssignedName())
                        ? "&fNobody has been assigned to this ticket!"
                        : "&fThe ticket has been assigned to: &c" + ticket.getAssignedName() + "&f!")
                .addLoreLine("&7&m---------------")
                .addLoreLine(Objects.isNull(ticket.getAssignedName()) ? "&aClick me to assign yourself to this ticket!" : "",
                        player,
                        Collections.singletonList("ticket.claim"))
                .build()
        ).addClickAction(p -> {
                    if (p.hasPermission("ticket.claim")) {
                        ticket.setAssignee(p);

                        HashMap<String, Object> newMap = new HashMap<>();
                        newMap.put("assigneeName", p.getName());
                        newMap.put("assigneeUUID", p.getUniqueId());
                        newMap.put("status", 1);

                        plugin.getStorageManager().updateTicket(ticket.getID(), newMap);

                        p.closeInventory();

                        this.openTicketInventory(p, ticket);

                        if (ticket.isPlayerOnline()) {
                            Objects.requireNonNull(Bukkit.getPlayer(ticket.getPlayerUUID()))
                                    .sendMessage(StringUtils.color("&7[&cTicket&7] &f" + p.getName() + " has been assigned to your ticket!"));
                        }
                    }
        });

        /* Sun Dial [ Date ] */

        Icon dateIcon = new Icon(
            new ItemManager(Material.ITEM_FRAME, 1)
                .setDisplayName("&c&lTime Created")
                .addLoreLine("&fThe ticket was issued on: &c" + ticket.getCreationDate() + "&f!")
        .build());

        /* Sign [ Ticket Message ] */

        Icon messageIcon = new Icon(
            new ItemManager(Material.BOOK, 1)
                .setDisplayName("&c&lTicket Message")
                .addLoreLine("&f" + ticket.getIssuedMessage())
        .build());

        /* Exit Inventory Button*/

        Icon exitIcon = new Icon(
            new ItemManager(Material.BARRIER, 1)
                .setDisplayName("&c&lExit")
                .addLoreLine("&fClose the ticket inventory.")
            .build()
        ).addClickAction(Player::closeInventory);

        /* Close Ticket Button */

        Icon closeIcon = new Icon(
            new ItemManager(Material.ANVIL, 1)
                .setDisplayName("&c&lClose Ticket")
                .addLoreLine("&fA staff member can close your ticket here!")
                .addLoreLine("&fClick me to close the ticket!", player, Arrays.asList("ticket.close", "ticket.close.self"))
                .build()
        ).addClickAction(p -> {
            if (p.hasPermission("ticket.close") ||
                    ticket.getPlayerUUID().equals(player.getUniqueId())
                            && player.hasPermission("ticket.close.self")) {
                verifyDeleteInventory(p, ticket);
            }
        });

        /* Current Ticket Status */

        Icon statusIcon = new Icon(
            new ItemManager(Material.NAME_TAG, 1)
                .setDisplayName("&c&lCurrent Ticket Status")
                .addLoreLine("&fThe status of the ticket is: &c" + ticket.getTextStatus() + "&f!")
        .build());

        /* Custom Holder Creation */

        CustomHolder customHolder = new CustomHolder(54, StringUtils.color("&fViewing &c" + ticket.getPlayerName() + "&f's Ticket"));

        customHolder.setIcon(4, messageIcon);
        customHolder.setIcon(19, locationIcon);
        customHolder.setIcon(21, assigneeIcon);
        customHolder.setIcon(23, commentsIcon);
        customHolder.setIcon(25, dateIcon);

        customHolder.setIcon(38, statusIcon);
        customHolder.setIcon(40, exitIcon);
        customHolder.setIcon(42, closeIcon);

        Inventory inventory = customHolder.getInventory();
        player.openInventory(inventory);

    }

    /**
     * Open the verification inventory to close self ticket.
     * @param player the player to open the inventory
     * @param ticket the ticket which is being verified to close.
     */
    private void verifyDeleteInventory(Player player, Ticket ticket) {

        Icon confirmIcon = new Icon(
            new ItemManager(Material.EMERALD, 1)
                .setDisplayName("&c&lConfirm")
                .addLoreLine("&fClick to permanently close the ticket.")
            .build()
        ).addClickAction(p -> {
            p.closeInventory();

            if (ticket.getPlayerUUID().equals(p.getUniqueId())) {

                p.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've successfully closed your ticket!"));

                deleteTicket(ticket, true);

                Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.SELF_CLOSED));

            } else {

                deleteTicket(ticket, false);

                plugin.getStorageManager().updateTicket(ticket.getID(), new HashMap<String, Object>() {{
                    put("status", 1);
                }});

                Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.DELETED));

                this.addComment(ticket, "Ticket closed by: " + player.getName(), true);

            }
        });

        Icon declineIcon = new Icon(
            new ItemManager(Material.BARRIER, 1)
                .setDisplayName("&c&lCancel")
                .addLoreLine("&fClick to return to the ticket menu.")
            .build()
        ).addClickAction(p -> {
            p.closeInventory();
            this.openTicketInventory(p, ticket);
        });

        CustomHolder customHolder = new CustomHolder(9, StringUtils.color("&cAre you sure?"));

        customHolder.setIcon(3, declineIcon);
        customHolder.setIcon(5, confirmIcon);

        player.openInventory(customHolder.getInventory());
    }

    /**
     * Get the list of the ticket cache.
     * @return ticketcache if not null
     */
    public List<Ticket> getTicketCache() {
        return ticketCache;
    }
}

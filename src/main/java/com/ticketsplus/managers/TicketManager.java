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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class TicketManager {

    private List<Ticket> ticketCache;
    private int bukkitRunnable;

    private TicketsPlus plugin;
    public TicketManager(TicketsPlus plugin) {
        this.plugin = plugin;

        this.ticketCache = new ArrayList<>();
        startRunnable();
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
            /**
             * Todo: Remove from Database.
             */
        } else {
            ticket.setCurrentStatus(2);
        }

        return true;
    }

    /**
     * Database transfer method.
     * @param ticket the ticket to upload to database.
     */
    private void uploadTicket(Ticket ticket) {

    }

    /**
     * Start the cache to database transfer runnable.
     */
    private void startRunnable() {

        if (plugin != null) {

                this.bukkitRunnable = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

                    if (ticketCache != null && !ticketCache.isEmpty()) {

                        long currentTime = System.currentTimeMillis();
                        int success = 0, failure = 0, total;

                        for (Ticket ticket : plugin.getTicketManager().ticketCache) {

                            try {

                                uploadTicket(ticket);
                                success++;

                            } catch (Exception e) {

                                Bukkit.getLogger().log(Level.SEVERE, "TM-SR - Ticket failed to upload.");
                                Bukkit.getLogger().log(Level.SEVERE, "TM-SR - Use /ticket debug " + ticket.getID());
                                Bukkit.getLogger().log(Level.SEVERE, e.getCause().toString());
                                failure++;

                            }
                        }

                        total = success + failure;
                        Bukkit.getLogger().log(failure > 0 ? Level.SEVERE : Level.INFO,
                                "Ticket DB Transfer Status - Success: " + success + "/" + total + ". Failure: " + failure + "/" + total);
                        Bukkit.getLogger().log(Level.INFO, "Completed transfer in: " + (currentTime - System.currentTimeMillis()) + "ms!");

                    } else {

                        Bukkit.getLogger().log(Level.INFO, "There were 0 tickets submitted on this wave.");

                    }

                }, 0L, 60 * 20 * plugin.getConfig().getInt("cache-to-database-minutes"));
        }
    }

    /**
     * Stop the cache to database transfer runnable.
     * @param restart true to have the runnable start again.
     */
    private void cancelRunnable(boolean restart) {
        if (Bukkit.getScheduler().isCurrentlyRunning(bukkitRunnable)) {
            Bukkit.getScheduler().cancelTask(bukkitRunnable);
            if (restart) {
                startRunnable();
            }
        }
    }

    /**
     * Search for a ticket in the cache, and if not found, check the database.
     * @param player search for ticket under player object.
     * @return returns ticket if not null.
     */
    public Ticket findTicket(Player player) {
        for (Ticket ticket : ticketCache){
            if (ticket.getPlayerUUID().equals(player.getUniqueId())){
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

    /** Inventory Layout.
        0 1 2 3   4  5 6 7 8
        9 10 1 2  3  4 5 6 7
        8 9 20 1  2  3 4 5 6
        7 8 9 30  1  2 3 4 5
     **/

        ItemStack map = new ItemStack(Material.COMPASS);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.setDisplayName(StringUtils.color("&c&lLocation"));
        mapMeta.setLore(Arrays.asList(
                "The location when the ticket was created.",
                player.hasPermission("ticket.teleport") ? "You can click me to teleport to the location!" : "",
                "X: " + ticket.getLocation().getX() + " Y: " + ticket.getLocation().getY() + " Z: " + ticket.getLocation().getZ()));
        map.setItemMeta(mapMeta);

        Icon teleportIcon = new Icon(map).addClickAction(p -> {
            if (!p.hasPermission("ticket.teleport")) return;
            p.teleport(ticket.getLocation());
        });

        /* Map [ Comments ] */

        ItemStack comments = new ItemStack(Material.MAP);
        ItemMeta commentsMeta = comments.getItemMeta();
        commentsMeta.setDisplayName(StringUtils.color("&c&lStaff Comments"));
        commentsMeta.setLore(ticket.getComments().isEmpty() ? Collections.singletonList("No comments have been added.") : ticket.getComments());
        comments.setItemMeta(commentsMeta);

        Icon commentsIcon = new Icon(comments);

        /* Diamond Helmet [ Location ] */

        ItemStack assignee = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta assigneeMeta = assignee.getItemMeta();
        assigneeMeta.setDisplayName(StringUtils.color("&c&lAssigned To"));
        assigneeMeta.setLore(Objects.isNull(ticket.getAssignedName())
                ? Arrays.asList("There is no one assigned to this ticket!", player.hasPermission("ticket.assign") ? "Click me to assign yourself!" : "")
                : Collections.singletonList("The ticket has been assigned to: " + ticket.getAssignedName()));
        assignee.setItemMeta(assigneeMeta);

        Icon assigneeIcon = new Icon(assignee).addClickAction(p -> {
            if (!p.hasPermission("ticket.assign")) return;

            p.closeInventory();
            this.openTicketInventory(p, ticket);
            ticket.setAssignee(p);

            if (ticket.isPlayerOnline()) {
                Objects.requireNonNull(Bukkit.getPlayer(ticket.getPlayerUUID())).sendMessage(StringUtils.color("&7[&cTicket&7] &f" + p.getName() + " has been assigned to your ticket!"));
            }
        });

        /* Sun Dial [ Date ] */

        ItemStack date = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta dateMeta = date.getItemMeta();
        dateMeta.setDisplayName(StringUtils.color("&c&lTime Created"));
        dateMeta.setLore(Collections.singletonList("The ticket was issued on: " + ticket.getCreationDate()));
        date.setItemMeta(dateMeta);

        Icon dateIcon = new Icon(date);

        /* Close Inventory Button*/

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(StringUtils.color("&c&lExit"));
        exit.setItemMeta(exitMeta);

        Icon exitIcon = new Icon(exit).addClickAction(Player::closeInventory);

        /* Close Inventory Button*/

        ItemStack statusChange = new ItemStack(Material.ANVIL);
        ItemMeta statusChangeMeta = statusChange.getItemMeta();
        statusChangeMeta.setDisplayName(StringUtils.color("&c&lClose Ticket"));
        statusChangeMeta.setLore(Collections.singletonList("Click me to close the ticket!"));
        statusChange.setItemMeta(statusChangeMeta);

        Icon statusChangeIcon = new Icon(statusChange).addClickAction(p -> verifyDeleteInventory(p, ticket));

        /* Custom Holder Creation */

        CustomHolder customHolder = new CustomHolder(36, StringUtils.color("&cViewing " + ticket.getPlayerName() + "'s Ticket"));

        customHolder.setIcon(10, teleportIcon);
        customHolder.setIcon(12, assigneeIcon);
        customHolder.setIcon(14, commentsIcon);
        customHolder.setIcon(16, dateIcon);

        customHolder.setIcon(31, exitIcon);
        customHolder.setIcon(33, statusChangeIcon);

        player.openInventory(customHolder.getInventory());
    }

    /**
     * Open the verification inventory to close self ticket.
     * @param player the player to open the inventory
     * @param ticket the ticket which is being verified to close.
     */
    private void verifyDeleteInventory(Player player, Ticket ticket){

        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(StringUtils.color("&c&lConfirm"));
        confirm.setItemMeta(confirmMeta);

        Icon confirmIcon = new Icon(confirm).addClickAction(pl -> {

            pl.closeInventory();

            if (ticket.getPlayerUUID().equals(pl.getUniqueId())) {

                pl.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've closed your own ticket!"));

                deleteTicket(ticket, true);

                Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.CLOSED));

            } else {

                Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.DELETED));

                ticket.addComment("Ticket closed by: " + player.getName());

                ticket.setCurrentStatus(2);

            }

        });

        ItemStack deny = new ItemStack(Material.RED_WOOL);
        ItemMeta denyMeta = deny.getItemMeta();
        denyMeta.setDisplayName(StringUtils.color("&c&lCancel"));
        deny.setItemMeta(denyMeta);

        Icon denyIcon = new Icon(deny).addClickAction(p -> {

            p.closeInventory();

            this.openTicketInventory(p, ticket);

        });

        CustomHolder customHolder = new CustomHolder(9, StringUtils.color("&cAre you sure?"));
        customHolder.setIcon(3, confirmIcon);
        customHolder.setIcon(5, denyIcon);

        player.openInventory(customHolder.getInventory());
    }

    public List<Ticket> getTicketCache() {
        return ticketCache;
    }
}

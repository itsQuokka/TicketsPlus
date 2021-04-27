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
                        Bukkit.getLogger().log(Level.INFO, "Completed transfer in: " + (System.currentTimeMillis() - currentTime) + "ms!");

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
        6 7 8 9   4  1 2 3 4
     **/

        ItemStack map = new ItemStack(Material.COMPASS);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.setDisplayName(StringUtils.color("&c&lLocation"));
        mapMeta.setLore(Arrays.asList(StringUtils.color("&fThe location when the ticket was created."),
                player.hasPermission("ticket.teleport") ? " " : "",
                player.hasPermission("ticket.teleport") ?
                        StringUtils.color("&fYou can click me to teleport to the location!") : "",
                StringUtils.color(
                        "&fX: &c" + ticket.getLocation().getX()
                        + " &fY: &c" + ticket.getLocation().getY()
                                + "&f Z: &c" + ticket.getLocation().getZ())));
        map.setItemMeta(mapMeta);

        Icon teleportIcon = new Icon(map).addClickAction(p -> {
            if (!p.hasPermission("ticket.teleport")) return;
            p.teleport(ticket.getLocation());
        });

        /* Map [ Comments ] */

        ItemStack comments = new ItemStack(Material.MAP);
        ItemMeta commentsMeta = comments.getItemMeta();
        commentsMeta.setDisplayName(StringUtils.color("&c&lStaff Comments"));
        List<String> commentList = new ArrayList<>();
        if (!Objects.isNull(ticket.getComments()) && !ticket.getComments().isEmpty()) {
            for (String str : ticket.getComments()){
                commentList.add(StringUtils.color("&f" + str));
            }
            if (commentList.isEmpty()){
                commentList = null;
            }
        }

        commentsMeta.setLore(Objects.isNull(commentList) ? Collections.singletonList(StringUtils.color("&fNo comments have been added.")) : ticket.getComments());
        comments.setItemMeta(commentsMeta);

        Icon commentsIcon = new Icon(comments);

        /* Diamond Helmet [ Location ] */

        ItemStack assignee = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta assigneeMeta = assignee.getItemMeta();
        assigneeMeta.setDisplayName(StringUtils.color("&c&lAssigned To"));
        assigneeMeta.setLore(Objects.isNull(ticket.getAssignedName())
                ? Arrays.asList(StringUtils.color("&fThere is no one assigned to this ticket!"),
                player.hasPermission("ticket.claim") ? StringUtils.color("&fClick me to assign yourself!")
                        : "")
                : Collections.singletonList(StringUtils.color("&fThe ticket has been assigned to: &c" + ticket.getAssignedName())));
        assignee.setItemMeta(assigneeMeta);

        Icon assigneeIcon = new Icon(assignee).addClickAction(p -> {
            if (!p.hasPermission("ticket.claim")) return;

            ticket.setAssignee(p);
            p.closeInventory();

            this.openTicketInventory(p, ticket);

            if (ticket.isPlayerOnline()) {
                Objects.requireNonNull(Bukkit.getPlayer(ticket.getPlayerUUID())).sendMessage(StringUtils.color("&7[&cTicket&7] &f" + p.getName() + " has been assigned to your ticket!"));
            }
        });

        /* Sun Dial [ Date ] */

        ItemStack date = new ItemStack(Material.ITEM_FRAME);
        ItemMeta dateMeta = date.getItemMeta();
        dateMeta.setDisplayName(StringUtils.color("&c&lTime Created"));
        dateMeta.setLore(Collections.singletonList(StringUtils.color("&fThe ticket was issued on: " + ticket.getCreationDate())));
        date.setItemMeta(dateMeta);

        Icon dateIcon = new Icon(date);

        /* Sign [ Ticket Message ] */

        ItemStack message = new ItemStack(Material.BOOK);
        ItemMeta messageMeta = message.getItemMeta();
        messageMeta.setDisplayName(StringUtils.color("&c&lTicket Message"));
        messageMeta.setLore(Collections.singletonList(StringUtils.color("&f" + ticket.getIssuedMessage())));
        message.setItemMeta(messageMeta);

        Icon messageIcon = new Icon(message);

        /* Close Inventory Button*/

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(StringUtils.color("&c&lExit"));
        exit.setItemMeta(exitMeta);

        Icon exitIcon = new Icon(exit).addClickAction(Player::closeInventory);

        /* Close Inventory Button*/

        ItemStack close = new ItemStack(Material.ANVIL);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(StringUtils.color("&c&lClose Ticket"));
        closeMeta.setLore(Collections.singletonList(StringUtils.color("&fClick me to close the ticket!")));
        close.setItemMeta(closeMeta);

        Icon closeIcon = new Icon(close).addClickAction(p -> verifyDeleteInventory(p, ticket));

        /* Current Ticket Status */

        ItemStack statusChange = new ItemStack(Material.NAME_TAG);
        ItemMeta statusChangeMeta = statusChange.getItemMeta();
        statusChangeMeta.setDisplayName(StringUtils.color("&c&lCurrent Ticket Status"));
        statusChangeMeta.setLore(Collections.singletonList(StringUtils.color("&fThe status of the ticket is: &c" + ticket.getCurrentStatus() + "&f!")));
        statusChange.setItemMeta(statusChangeMeta);

        Icon statusIcon = new Icon(statusChange);

        /* Custom Holder Creation */

        CustomHolder customHolder = new CustomHolder(45, StringUtils.color("&cViewing " + ticket.getPlayerName() + "'s Ticket"));

        customHolder.setIcon(4, messageIcon);
        customHolder.setIcon(19, teleportIcon);
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
    private void verifyDeleteInventory(Player player, Ticket ticket){

        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(StringUtils.color("&c&lConfirm"));
        confirm.setItemMeta(confirmMeta);

        Icon confirmIcon = new Icon(confirm).addClickAction(pl -> {

            pl.closeInventory();

            if (ticket.getPlayerUUID().equals(pl.getUniqueId())) {

                pl.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've closed your own ticket!"));

                deleteTicket(ticket, true);

                Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.SELF_CLOSED));

            } else {

                Bukkit.getServer().getPluginManager().callEvent(new TicketUpdateEvent(ticket, UpdateType.DELETED));

                ticket.addComment("Ticket closed by: " + player.getName());

                ticket.setCurrentStatus(2);

            }

        });

        ItemStack deny = new ItemStack(Material.BARRIER);
        ItemMeta denyMeta = deny.getItemMeta();
        denyMeta.setDisplayName(StringUtils.color("&c&lCancel"));
        deny.setItemMeta(denyMeta);

        Icon denyIcon = new Icon(deny).addClickAction(p -> {

            p.closeInventory();

            this.openTicketInventory(p, ticket);

        });

        CustomHolder customHolder = new CustomHolder(9, StringUtils.color("&cAre you sure?"));
        customHolder.setIcon(5, confirmIcon);
        customHolder.setIcon(3, denyIcon);

        player.openInventory(customHolder.getInventory());
    }

    public List<Ticket> getTicketCache() {
        return ticketCache;
    }
}

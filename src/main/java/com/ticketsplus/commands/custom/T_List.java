package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.inventory.CustomHolder;
import com.ticketsplus.inventory.Icon;
import com.ticketsplus.obj.Ticket;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class T_List extends CommandExecutor {

    private TicketsPlus plugin;
    public T_List(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("list");
        this.setPermission("ticket.list");
        this.setLength(1);
        this.setUsage("/ticket list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (plugin.getTicketManager().getTicketCache().isEmpty()){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fThere are no open tickets!"));
            return;
        }

        CustomHolder customHolder = new CustomHolder(54, StringUtils.color("&cCurrently Opened Tickets"));

        int i = 0;
        for (Ticket ticket : plugin.getTicketManager().getTicketCache()) {

            ItemStack ticketItem = new ItemStack(Material.MAP);
            ItemMeta ticketItemMeta = ticketItem.getItemMeta();
            ticketItemMeta.setDisplayName(StringUtils.color("&c&l" + ticket.getPlayerName() + "&f's Ticket"));
            ticketItemMeta.setLore(Collections.singletonList(StringUtils.color("&fClick me to open ticket!")));
            ticketItem.setItemMeta(ticketItemMeta);

            Icon theIcon = new Icon(ticketItem).addClickAction(p -> {
                p.closeInventory();
                plugin.getTicketManager().openTicketInventory(p, ticket);
            });

            customHolder.setIcon(i, theIcon);
            i++;
        }

        player.openInventory(customHolder.getInventory());
    }
}

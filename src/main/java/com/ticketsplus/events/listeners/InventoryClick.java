package com.ticketsplus.events.listeners;

import com.ticketsplus.inventory.ClickAction;
import com.ticketsplus.inventory.CustomHolder;
import com.ticketsplus.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        //Check if the inventory is custom
        if (event.getView().getTopInventory().getHolder() instanceof CustomHolder) {
            //Cancel the event
            event.setCancelled(true);

            //Check if who clicked is a Player
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();

                //Check if the item the player clicked on is valid
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) return;

                //Get our CustomHolder
                CustomHolder customHolder = (CustomHolder) event.getView().getTopInventory().getHolder();

                //Check if the clicked slot is any icon
                Icon icon = customHolder.getIcon(event.getRawSlot());
                if (icon == null) return;

                //Execute all the actions
                for (ClickAction clickAction : icon.getClickActions()) {
                    clickAction.execute(player);
                }
            }
        }
    }
}

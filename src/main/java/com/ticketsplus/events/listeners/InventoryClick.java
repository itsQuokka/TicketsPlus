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

        if (event.getView().getTopInventory().getHolder() instanceof CustomHolder) {

            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();

                ItemStack itemStack = event.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) return;

                CustomHolder customHolder = (CustomHolder) event.getView().getTopInventory().getHolder();

                Icon icon = customHolder.getIcon(event.getRawSlot());
                if (icon == null) return;

                for (ClickAction clickAction : icon.getClickActions()) {
                    clickAction.execute(player);
                }
            }
        }
    }
}

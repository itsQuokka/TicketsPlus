package com.ticketsplus.managers;

import com.ticketsplus.utilities.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final ItemStack item;
    private final List<String> lore = new ArrayList<>();
    private final ItemMeta meta;

    public ItemManager(Material mat, short subId, int amount) {
        this.item = new ItemStack(mat, amount, subId);
        this.meta = this.item.getItemMeta();
    }

    public ItemManager(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemManager(Material mat, short subId) {
        this.item = new ItemStack(mat, 1, subId);
        this.meta = this.item.getItemMeta();
    }

    public ItemManager(Material mat, int amount) {
        this.item = new ItemStack(mat, amount, (short) 0);
        this.meta = this.item.getItemMeta();
    }

    public ItemManager(Material mat) {
        this.item = new ItemStack(mat, 1, (short) 0);
        this.meta = this.item.getItemMeta();
    }

    public ItemManager setAmount(int value) {
        this.item.setAmount(value);
        return this;
    }

    public ItemManager setNoName() {
        this.meta.setDisplayName(" ");
        return this;
    }

    public ItemManager setGlow() {
        this.meta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemManager addLoreLine(String line) {
        this.lore.add(StringUtils.color(line));
        return this;
    }

    public ItemManager addLoreLine(String line, Player player, List<String> permission) {
        boolean added = false;
        for (String strPermission : permission){
            if (player.hasPermission(strPermission)) {
                if (!added) {
                    this.lore.add(StringUtils.color(line));
                    added = true;
                }
            }
        }
        return this;
    }

    public ItemManager addLoreLine(ArrayList<String> strings) {
        for (String str : strings) {
            this.lore.add(StringUtils.color(str));
        }
        return this;
    }

    public ItemManager setDisplayName(String name) {
        this.meta.setDisplayName(StringUtils.color(name));
        return this;
    }

    public ItemStack build() {
        if (!this.lore.isEmpty()) {
            this.meta.setLore(this.lore);
        }
        this.item.setItemMeta(this.meta);
        return this.item;
    }

}

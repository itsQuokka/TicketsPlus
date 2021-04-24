package com.ticketsplus.utilities;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('*', s);
    }

}

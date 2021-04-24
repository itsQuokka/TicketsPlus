package com.ticketsplus.commands.custom;

import com.ticketsplus.TicketsPlus;
import com.ticketsplus.commands.CommandExecutor;
import com.ticketsplus.utilities.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class T_ToggleNotifications extends CommandExecutor {

    private TicketsPlus plugin;
    public T_ToggleNotifications(TicketsPlus plugin) {
        this.plugin = plugin;

        this.setCommand("notify");
        this.setPermission("ticket.notifytoggle");
        this.setLength(2);
        this.setUsage("/ticket notify <ON|OFF>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (args.length <= 0){
            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou have to specify on or off!"));
            return;
        }

        if (args[1].equalsIgnoreCase("on")) {

            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've turned ticket notifications on!"));

            plugin.getPlayerManager().add(player);

        } else if (args[1].equalsIgnoreCase("off")) {

            player.sendMessage(StringUtils.color("&7[&cTicket&7] &fYou've turned ticket notifications off"));

            plugin.getPlayerManager().remove(player);

        } else {

        }

    }
}

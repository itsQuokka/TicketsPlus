package com.ticketsplus.managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private List<UUID> playerList;

    public PlayerManager(){
        this.playerList = new ArrayList<>();
    }

    public void add(Player player) {
        if (playerList != null){
            playerList.add(player.getUniqueId());
        }
    }

    public void remove(Player player) {
        if (playerList != null){
            if (playerList.contains(player.getUniqueId())){
                playerList.remove(player.getUniqueId());
            }
        }
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }
}

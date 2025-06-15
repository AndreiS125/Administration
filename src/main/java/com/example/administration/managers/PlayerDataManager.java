package com.example.administration.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {
    
    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Set<UUID> godModePlayers = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Set<UUID> spyPlayers = new HashSet<>();
    private final Set<UUID> socialSpyPlayers = new HashSet<>();
    private final Set<UUID> flyingPlayers = new HashSet<>();
    
    // Vanish methods
    public void setVanished(Player player, boolean vanished) {
        if (vanished) {
            vanishedPlayers.add(player.getUniqueId());
            // Hide player from all other players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(player) && !onlinePlayer.hasPermission("administration.vanish.see")) {
                    onlinePlayer.hidePlayer(Bukkit.getPluginManager().getPlugin("Administration"), player);
                }
            }
            // Add invisibility effect for extra stealth
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            // Show player to all other players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(Bukkit.getPluginManager().getPlugin("Administration"), player);
            }
            // Remove invisibility effect
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
    
    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }
    
    public void clearAllVanishedPlayers() {
        for (UUID uuid : vanishedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                setVanished(player, false);
            }
        }
        vanishedPlayers.clear();
    }
    
    // God mode methods
    public void setGodMode(Player player, boolean godMode) {
        if (godMode) {
            godModePlayers.add(player.getUniqueId());
        } else {
            godModePlayers.remove(player.getUniqueId());
        }
    }
    
    public boolean isInGodMode(Player player) {
        return godModePlayers.contains(player.getUniqueId());
    }
    
    // Freeze methods
    public void setFrozen(Player player, boolean frozen) {
        if (frozen) {
            frozenPlayers.add(player.getUniqueId());
        } else {
            frozenPlayers.remove(player.getUniqueId());
        }
    }
    
    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }
    
    // Spy methods
    public void setCommandSpy(Player player, boolean spy) {
        if (spy) {
            spyPlayers.add(player.getUniqueId());
        } else {
            spyPlayers.remove(player.getUniqueId());
        }
    }
    
    public boolean hasCommandSpy(Player player) {
        return spyPlayers.contains(player.getUniqueId());
    }
    
    public Set<UUID> getCommandSpyPlayers() {
        return new HashSet<>(spyPlayers);
    }
    
    // Social spy methods
    public void setSocialSpy(Player player, boolean socialSpy) {
        if (socialSpy) {
            socialSpyPlayers.add(player.getUniqueId());
        } else {
            socialSpyPlayers.remove(player.getUniqueId());
        }
    }
    
    public boolean hasSocialSpy(Player player) {
        return socialSpyPlayers.contains(player.getUniqueId());
    }
    
    public Set<UUID> getSocialSpyPlayers() {
        return new HashSet<>(socialSpyPlayers);
    }
    
    // Flying methods
    public void setFlying(Player player, boolean flying) {
        if (flying) {
            flyingPlayers.add(player.getUniqueId());
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            flyingPlayers.remove(player.getUniqueId());
            if (!player.getGameMode().name().equals("CREATIVE") && !player.getGameMode().name().equals("SPECTATOR")) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
    }
    
    public boolean isFlying(Player player) {
        return flyingPlayers.contains(player.getUniqueId());
    }
    
    // Handle new player joins for vanish
    public void handlePlayerJoin(Player player) {
        // Hide vanished players from the new player
        for (UUID vanishedUuid : vanishedPlayers) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedUuid);
            if (vanishedPlayer != null && vanishedPlayer.isOnline() && !player.hasPermission("administration.vanish.see")) {
                player.hidePlayer(Bukkit.getPluginManager().getPlugin("Administration"), vanishedPlayer);
            }
        }
    }
} 
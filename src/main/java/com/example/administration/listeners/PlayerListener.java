package com.example.administration.listeners;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class PlayerListener implements Listener {
    
    private final AdministrationPlugin plugin;
    
    public PlayerListener(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Handle vanish visibility for new players
        plugin.getPlayerDataManager().handlePlayerJoin(player);
        
        // Handle disguise visibility for new players
        if (plugin.getDisguiseManager() != null) {
            plugin.getDisguiseManager().handlePlayerJoin(player);
        }
        
        // Don't show join message for vanished players
        if (plugin.getPlayerDataManager().isVanished(player)) {
            event.joinMessage(null);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Don't show quit message for vanished players
        if (plugin.getPlayerDataManager().isVanished(player)) {
            event.quitMessage(null);
        }
        
        // Clean up disguise on quit
        if (plugin.getDisguiseManager() != null && plugin.getDisguiseManager().isDisguised(player)) {
            plugin.getDisguiseManager().undisguise(player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        // Cancel damage for players in god mode
        if (plugin.getPlayerDataManager().isInGodMode(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Cancel movement for frozen players
        if (plugin.getPlayerDataManager().isFrozen(player)) {
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getZ() != event.getTo().getZ() ||
                event.getFrom().getY() != event.getTo().getY()) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You are frozen and cannot move!", NamedTextColor.RED));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        
        // Prevent frozen players from using commands (except admin commands)
        if (plugin.getPlayerDataManager().isFrozen(player) && 
            !player.hasPermission("administration.freeze.bypass") &&
            !command.toLowerCase().startsWith("/administration")) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You are frozen and cannot use commands!", NamedTextColor.RED));
            return;
        }
        
        // Command spy functionality
        for (UUID spyUuid : plugin.getPlayerDataManager().getCommandSpyPlayers()) {
            Player spy = Bukkit.getPlayer(spyUuid);
            if (spy != null && spy.isOnline() && !spy.equals(player)) {
                spy.sendMessage(Component.text("[CommandSpy] " + player.getName() + ": " + command, NamedTextColor.GRAY));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Handle private message detection for social spy
        if (message.toLowerCase().startsWith("/msg ") || 
            message.toLowerCase().startsWith("/tell ") || 
            message.toLowerCase().startsWith("/whisper ") ||
            message.toLowerCase().startsWith("/w ")) {
            
            for (UUID spyUuid : plugin.getPlayerDataManager().getSocialSpyPlayers()) {
                Player spy = Bukkit.getPlayer(spyUuid);
                if (spy != null && spy.isOnline() && !spy.equals(player)) {
                    spy.sendMessage(Component.text("[SocialSpy] " + player.getName() + ": " + message, NamedTextColor.YELLOW));
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Prevent frozen players from interacting
        if (plugin.getPlayerDataManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You are frozen and cannot interact!", NamedTextColor.RED));
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        // Prevent frozen players from dropping items
        if (plugin.getPlayerDataManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You are frozen and cannot drop items!", NamedTextColor.RED));
        }
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        
        // Prevent frozen players from picking up items
        if (plugin.getPlayerDataManager().isFrozen(player)) {
            event.setCancelled(true);
        }
    }
} 
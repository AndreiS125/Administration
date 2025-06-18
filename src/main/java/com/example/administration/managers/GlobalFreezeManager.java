package com.example.administration.managers;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.time.Duration;

/**
 * Global Freeze Manager - Controls server-wide freeze state
 * When active, ALL online players are frozen regardless of individual freeze status
 */
public class GlobalFreezeManager implements Listener {
    
    private final AdministrationPlugin plugin;
    private boolean globalFreezeActive = false;
    private String freezeReason = "Server is frozen";
    
    public GlobalFreezeManager(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Enable global freeze for all players
     * @param reason The reason for the freeze (optional)
     */
    public void enableGlobalFreeze(String reason) {
        this.globalFreezeActive = true;
        this.freezeReason = reason != null ? reason : "Server is frozen";
        
        // Notify all online players
        Title freezeTitle = Title.title(
            Component.text("🧊 SERVER FROZEN 🧊", NamedTextColor.AQUA),
            Component.text(this.freezeReason, NamedTextColor.GRAY),
            Title.Times.times(
                Duration.ofMillis(500),
                Duration.ofSeconds(3),
                Duration.ofSeconds(1)
            )
        );
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(freezeTitle);
            player.sendMessage(Component.text("🧊 " + this.freezeReason, NamedTextColor.AQUA));
        }
        
        plugin.getLogger().info("[GLOBAL FREEZE] Server frozen: " + this.freezeReason);
    }
    
    /**
     * Disable global freeze
     */
    public void disableGlobalFreeze() {
        this.globalFreezeActive = false;
        
        // Notify all online players
        Title unfreezeTitle = Title.title(
            Component.text("✅ SERVER UNFROZEN ✅", NamedTextColor.GREEN),
            Component.text("You can move again!", NamedTextColor.GRAY),
            Title.Times.times(
                Duration.ofMillis(500),
                Duration.ofSeconds(2),
                Duration.ofSeconds(1)
            )
        );
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(unfreezeTitle);
            player.sendMessage(Component.text("✅ Server unfrozen! You can move again.", NamedTextColor.GREEN));
        }
        
        plugin.getLogger().info("[GLOBAL FREEZE] Server unfrozen!");
    }
    
    /**
     * Toggle global freeze state
     * @param reason The reason for freezing (if enabling)
     * @return true if freeze was enabled, false if disabled
     */
    public boolean toggleGlobalFreeze(String reason) {
        if (globalFreezeActive) {
            disableGlobalFreeze();
            return false;
        } else {
            enableGlobalFreeze(reason);
            return true;
        }
    }
    
    /**
     * Check if global freeze is currently active
     * @return true if global freeze is active
     */
    public boolean isGlobalFreezeActive() {
        return globalFreezeActive;
    }
    
    /**
     * Get the current freeze reason
     * @return the freeze reason
     */
    public String getFreezeReason() {
        return freezeReason;
    }
    
    /**
     * Check if a player should be frozen
     * This includes both global freeze and individual player freeze
     */
    private boolean shouldPlayerBeFrozen(Player player) {
        // Admin bypass - players with freeze permission can move during global freeze
        if (globalFreezeActive && player.hasPermission("administration.freeze.bypass")) {
            return false;
        }
        
        // Global freeze affects everyone (except admins with bypass)
        if (globalFreezeActive) {
            return true;
        }
        
        // Check individual freeze status
        return plugin.getPlayerDataManager().isFrozen(player);
    }
    
    // Event handlers to block all player actions during global freeze
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (shouldPlayerBeFrozen(player)) {
            // Allow head movement but block body movement
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getZ() != event.getTo().getZ() ||
                event.getFrom().getY() != event.getTo().getY()) {
                
                event.setCancelled(true);
                
                // Send reminder message occasionally
                if (System.currentTimeMillis() % 3000 < 100) { // Roughly every 3 seconds
                    if (globalFreezeActive) {
                        player.sendMessage(Component.text("🧊 " + freezeReason, NamedTextColor.AQUA));
                    } else {
                        player.sendMessage(Component.text("🧊 You are frozen!", NamedTextColor.AQUA));
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (shouldPlayerBeFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("🧊 You cannot break blocks while frozen!", NamedTextColor.RED));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (shouldPlayerBeFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("🧊 You cannot place blocks while frozen!", NamedTextColor.RED));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (shouldPlayerBeFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("🧊 You cannot interact while frozen!", NamedTextColor.RED));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && shouldPlayerBeFrozen(player)) {
            event.setCancelled(true);
            player.sendActionBar(Component.text("🧊 You cannot use inventory while frozen!", NamedTextColor.RED));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (shouldPlayerBeFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("🧊 You cannot drop items while frozen!", NamedTextColor.RED));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (shouldPlayerBeFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && shouldPlayerBeFrozen(player)) {
            event.setCancelled(true);
            player.sendActionBar(Component.text("🧊 You cannot attack while frozen!", NamedTextColor.RED));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (shouldPlayerBeFrozen(player)) {
            String command = event.getMessage().toLowerCase();
            
            // Allow certain commands even when frozen
            if (command.startsWith("/msg") || command.startsWith("/tell") || 
                command.startsWith("/r") || command.startsWith("/reply") ||
                command.startsWith("/help") || command.startsWith("/?")) {
                return;
            }
            
            // Block all other commands
            event.setCancelled(true);
            player.sendActionBar(Component.text("🧊 Most commands are disabled while frozen!", NamedTextColor.RED));
        }
    }
    
    /**
     * Clean up when plugin disables
     */
    public void cleanup() {
        if (globalFreezeActive) {
            disableGlobalFreeze();
        }
    }
} 
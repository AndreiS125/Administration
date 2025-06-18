package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalFreezeCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public GlobalFreezeCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.globalfreeze")) {
            // In stealth mode, don't show permission denied messages
            if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
                return true;
            }
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            showUsage(sender);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "on":
            case "enable":
            case "start":
                handleFreeze(sender, args);
                break;
                
            case "off":
            case "disable":
            case "stop":
                handleUnfreeze(sender);
                break;
                
            case "toggle":
                handleToggle(sender, args);
                break;
                
            case "status":
            case "info":
                handleStatus(sender);
                break;
                
            default:
                showUsage(sender);
                break;
        }
        
        return true;
    }
    
    private void handleFreeze(CommandSender sender, String[] args) {
        if (plugin.getGlobalFreezeManager().isGlobalFreezeActive()) {
            sendMessage(sender, Component.text("Global freeze is already active!", NamedTextColor.YELLOW));
            return;
        }
        
        // Build reason from remaining arguments
        String reason = "Server maintenance";
        if (args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) reasonBuilder.append(" ");
                reasonBuilder.append(args[i]);
            }
            reason = reasonBuilder.toString();
        }
        
        plugin.getGlobalFreezeManager().enableGlobalFreeze(reason);
        
        sendMessage(sender, Component.text("🧊 Global freeze activated!", NamedTextColor.AQUA));
        sendMessage(sender, Component.text("Reason: " + reason, NamedTextColor.GRAY));
        sendMessage(sender, Component.text("Frozen players: " + Bukkit.getOnlinePlayers().size(), NamedTextColor.GRAY));
        
        // Log to console
        plugin.getLogger().info("[GLOBAL FREEZE] Activated by " + sender.getName() + " - Reason: " + reason);
    }
    
    private void handleUnfreeze(CommandSender sender) {
        if (!plugin.getGlobalFreezeManager().isGlobalFreezeActive()) {
            sendMessage(sender, Component.text("Global freeze is not currently active!", NamedTextColor.YELLOW));
            return;
        }
        
        plugin.getGlobalFreezeManager().disableGlobalFreeze();
        
        sendMessage(sender, Component.text("✅ Global freeze deactivated!", NamedTextColor.GREEN));
        sendMessage(sender, Component.text("All players can move again.", NamedTextColor.GRAY));
        
        // Log to console
        plugin.getLogger().info("[GLOBAL FREEZE] Deactivated by " + sender.getName());
    }
    
    private void handleToggle(CommandSender sender, String[] args) {
        String reason = "Server maintenance";
        if (args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) reasonBuilder.append(" ");
                reasonBuilder.append(args[i]);
            }
            reason = reasonBuilder.toString();
        }
        
        boolean enabled = plugin.getGlobalFreezeManager().toggleGlobalFreeze(reason);
        
        if (enabled) {
            sendMessage(sender, Component.text("🧊 Global freeze activated!", NamedTextColor.AQUA));
            sendMessage(sender, Component.text("Reason: " + reason, NamedTextColor.GRAY));
        } else {
            sendMessage(sender, Component.text("✅ Global freeze deactivated!", NamedTextColor.GREEN));
        }
        
        // Log to console
        plugin.getLogger().info("[GLOBAL FREEZE] Toggled by " + sender.getName() + " - " + 
            (enabled ? "Enabled" : "Disabled"));
    }
    
    private void handleStatus(CommandSender sender) {
        boolean isActive = plugin.getGlobalFreezeManager().isGlobalFreezeActive();
        
        if (isActive) {
            String reason = plugin.getGlobalFreezeManager().getFreezeReason();
            sendMessage(sender, Component.text("🧊 Global Freeze Status: ACTIVE", NamedTextColor.AQUA));
            sendMessage(sender, Component.text("Reason: " + reason, NamedTextColor.GRAY));
            sendMessage(sender, Component.text("Affected players: " + Bukkit.getOnlinePlayers().size(), NamedTextColor.GRAY));
            
            // Count admins with bypass permission
            long bypassCount = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("administration.freeze.bypass"))
                .count();
            
            if (bypassCount > 0) {
                sendMessage(sender, Component.text("Admins with bypass: " + bypassCount, NamedTextColor.YELLOW));
            }
        } else {
            sendMessage(sender, Component.text("✅ Global Freeze Status: INACTIVE", NamedTextColor.GREEN));
            sendMessage(sender, Component.text("All players can move freely.", NamedTextColor.GRAY));
        }
    }
    
    private void showUsage(CommandSender sender) {
        sendMessage(sender, Component.text("=== Global Freeze Command ===", NamedTextColor.GOLD));
        sendMessage(sender, Component.text("/globalfreeze on [reason] - Enable global freeze", NamedTextColor.YELLOW));
        sendMessage(sender, Component.text("/globalfreeze off - Disable global freeze", NamedTextColor.YELLOW));
        sendMessage(sender, Component.text("/globalfreeze toggle [reason] - Toggle freeze state", NamedTextColor.YELLOW));
        sendMessage(sender, Component.text("/globalfreeze status - Check current status", NamedTextColor.YELLOW));
        sendMessage(sender, Component.text("", NamedTextColor.WHITE));
        sendMessage(sender, Component.text("Examples:", NamedTextColor.GRAY));
        sendMessage(sender, Component.text("/globalfreeze on Server restart in progress", NamedTextColor.GRAY));
        sendMessage(sender, Component.text("/globalfreeze on Emergency maintenance", NamedTextColor.GRAY));
        sendMessage(sender, Component.text("/globalfreeze toggle Admin meeting", NamedTextColor.GRAY));
        sendMessage(sender, Component.text("", NamedTextColor.WHITE));
        sendMessage(sender, Component.text("Note: Admins with 'administration.freeze.bypass' can move during global freeze.", NamedTextColor.GRAY));
    }
    
    private void sendMessage(CommandSender sender, Component message) {
        // Check if sender is in stealth mode and modify message accordingly
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            // Send stealth-styled message
            String plainText = ((net.kyori.adventure.text.TextComponent) message).content();
            player.sendMessage("§8[§7Silent§8] §f" + plainText);
        } else {
            sender.sendMessage(message);
        }
    }
} 
package com.example.administration.utils;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StealthUtils {
    
    private final AdministrationPlugin plugin;
    
    public StealthUtils(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Send a message respecting stealth mode
     */
    public static void sendStealthMessage(AdministrationPlugin plugin, CommandSender sender, Component message) {
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            // Send stealth-styled message
            String plainText = extractPlainText(message);
            player.sendMessage("§8[§7Silent§8] §f" + plainText);
        } else {
            sender.sendMessage(message);
        }
    }
    
    /**
     * Send a message respecting stealth mode with custom prefix
     */
    public static void sendStealthMessage(AdministrationPlugin plugin, CommandSender sender, String message) {
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            // Send stealth-styled message
            player.sendMessage("§8[§7Silent§8] §f" + message);
        } else {
            sender.sendMessage(message);
        }
    }
    
    /**
     * Check if sender has permission, respecting stealth mode
     */
    public static boolean hasPermissionStealth(AdministrationPlugin plugin, CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            // In stealth mode, don't show permission denied messages
            if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
                return false; // Just return false, don't send message
            }
            sender.sendMessage("§cYou don't have permission to use this command!");
            return false;
        }
        return true;
    }
    
    /**
     * Send error message respecting stealth mode
     */
    public static void sendErrorStealth(AdministrationPlugin plugin, CommandSender sender, String message) {
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            player.sendMessage("§8[§7Silent§8] §c" + message);
        } else {
            sender.sendMessage("§c" + message);
        }
    }
    
    /**
     * Send success message respecting stealth mode
     */
    public static void sendSuccessStealth(AdministrationPlugin plugin, CommandSender sender, String message) {
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            player.sendMessage("§8[§7Silent§8] §a" + message);
        } else {
            sender.sendMessage("§a" + message);
        }
    }
    
    /**
     * Send info message respecting stealth mode
     */
    public static void sendInfoStealth(AdministrationPlugin plugin, CommandSender sender, String message) {
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            player.sendMessage("§8[§7Silent§8] §7" + message);
        } else {
            sender.sendMessage("§7" + message);
        }
    }
    
    /**
     * Check if a player is in stealth mode
     */
    public static boolean isInStealthMode(AdministrationPlugin plugin, Player player) {
        return plugin.getStealthManager().isInStealthMode(player);
    }
    
    /**
     * Extract plain text from Adventure Component
     */
    private static String extractPlainText(Component component) {
        if (component instanceof TextComponent textComponent) {
            return textComponent.content();
        }
        // Fallback for other component types
        return component.toString();
    }
    
    /**
     * Send a completely silent message (no prefix, just the message)
     */
    public static void sendSilentMessage(Player player, String message) {
        player.sendMessage(message);
    }
    
    /**
     * Check if command should be completely hidden from logs
     */
    public static boolean shouldHideCommand(AdministrationPlugin plugin, Player player, String command) {
        return plugin.getStealthManager().isInStealthMode(player);
    }
} 
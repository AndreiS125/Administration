package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import com.example.administration.managers.StealthManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StealthCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    private final StealthManager stealthManager;
    
    public StealthCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
        this.stealthManager = plugin.getStealthManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Pre-emptively hide this command execution from logs
        if (sender instanceof Player player) {
            stealthManager.hideCommandExecution(player, "/" + label + " " + String.join(" ", args));
        }
        
        if (!sender.hasPermission("administration.stealth")) {
            // Don't even show permission denied message for maximum stealth
            return true;
        }
        
        if (args.length == 0) {
            // Toggle stealth for sender
            if (!(sender instanceof Player player)) {
                // Console cannot use stealth mode
                return true;
            }
            
            stealthManager.toggleStealthMode(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "on", "enable", "true" -> {
                Player target = getTargetPlayer(sender, args);
                if (target != null) {
                    stealthManager.enableStealthMode(target);
                    if (!sender.equals(target)) {
                        // Silent confirmation to command sender
                        sendStealthMessage(sender, "Enabled stealth mode for " + target.getName());
                    }
                }
            }
            
            case "off", "disable", "false" -> {
                Player target = getTargetPlayer(sender, args);
                if (target != null) {
                    stealthManager.disableStealthMode(target);
                    if (!sender.equals(target)) {
                        // Silent confirmation to command sender
                        sendStealthMessage(sender, "Disabled stealth mode for " + target.getName());
                    }
                }
            }
            
            case "toggle" -> {
                Player target = getTargetPlayer(sender, args);
                if (target != null) {
                    stealthManager.toggleStealthMode(target);
                    if (!sender.equals(target)) {
                        boolean isNowStealth = stealthManager.isInStealthMode(target);
                        sendStealthMessage(sender, (isNowStealth ? "Enabled" : "Disabled") + " stealth mode for " + target.getName());
                    }
                }
            }
            
            case "status", "check" -> {
                Player target = getTargetPlayer(sender, args);
                if (target != null) {
                    boolean isStealth = stealthManager.isInStealthMode(target);
                    sendStealthMessage(sender, target.getName() + " stealth mode: " + (isStealth ? "§aENABLED" : "§cDISABLED"));
                }
            }
            
            case "help" -> {
                sendStealthHelp(sender);
            }
            
            default -> {
                // Try to parse as player name for toggle
                Player target = Bukkit.getPlayer(subCommand);
                if (target != null) {
                    stealthManager.toggleStealthMode(target);
                    if (!sender.equals(target)) {
                        boolean isNowStealth = stealthManager.isInStealthMode(target);
                        sendStealthMessage(sender, (isNowStealth ? "Enabled" : "Disabled") + " stealth mode for " + target.getName());
                    }
                } else {
                    sendStealthHelp(sender);
                }
            }
        }
        
        return true;
    }
    
    private Player getTargetPlayer(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            // Target specified
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendStealthMessage(sender, "§cPlayer not found: " + args[1]);
                return null;
            }
            return target;
        } else {
            // Use sender as target
            if (!(sender instanceof Player)) {
                sendStealthMessage(sender, "§cConsole must specify a target player");
                return null;
            }
            return (Player) sender;
        }
    }
    
    private void sendStealthMessage(CommandSender sender, String message) {
        // Send message without any logging
        if (sender instanceof Player player) {
            player.sendMessage("§8[§7Stealth§8] §f" + message);
        } else {
            // For console, use a more discrete format
            sender.sendMessage("[Administration] " + message);
        }
    }
    
    private void sendStealthHelp(CommandSender sender) {
        sendStealthMessage(sender, "§6Stealth Mode Commands:");
        sendStealthMessage(sender, "§7/stealth §8- §fToggle your stealth mode");
        sendStealthMessage(sender, "§7/stealth on [player] §8- §fEnable stealth mode");
        sendStealthMessage(sender, "§7/stealth off [player] §8- §fDisable stealth mode");
        sendStealthMessage(sender, "§7/stealth toggle [player] §8- §fToggle stealth mode");
        sendStealthMessage(sender, "§7/stealth status [player] §8- §fCheck stealth status");
        sendStealthMessage(sender, "§8");
        sendStealthMessage(sender, "§8Stealth mode hides ALL command logs and traces.");
        sendStealthMessage(sender, "§8Perfect for undercover operations and investigations.");
    }
} 
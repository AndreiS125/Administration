package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public VanishCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.vanish")) {
            // In stealth mode, don't show permission denied messages
            if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
                return true;
            }
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        Player target;
        
        if (args.length == 0) {
            // Toggle vanish for sender
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console cannot vanish! Specify a player.", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        } else {
            // Toggle vanish for specified player
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessage(sender, Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
                return true;
            }
        }
        
        boolean isVanished = plugin.getPlayerDataManager().isVanished(target);
        plugin.getPlayerDataManager().setVanished(target, !isVanished);
        
        if (!isVanished) {
            // Player is now vanished
            sendMessage(target, Component.text("You are now vanished!", NamedTextColor.GREEN));
            if (!sender.equals(target)) {
                sendMessage(sender, Component.text(target.getName() + " is now vanished!", NamedTextColor.GREEN));
            }
        } else {
            // Player is no longer vanished
            sendMessage(target, Component.text("You are no longer vanished!", NamedTextColor.YELLOW));
            if (!sender.equals(target)) {
                sendMessage(sender, Component.text(target.getName() + " is no longer vanished!", NamedTextColor.YELLOW));
            }
        }
        
        return true;
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
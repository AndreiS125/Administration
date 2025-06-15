package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public TeleportCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.teleport")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /teleport <player> [target]", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 1) {
            // Teleport sender to target
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Console cannot teleport! Specify both players.", NamedTextColor.RED));
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
                return true;
            }
            
            player.teleport(target.getLocation());
            player.sendMessage(Component.text("Teleported to " + target.getName() + "!", NamedTextColor.GREEN));
            
        } else {
            // Teleport first player to second player
            Player player1 = Bukkit.getPlayer(args[0]);
            Player player2 = Bukkit.getPlayer(args[1]);
            
            if (player1 == null) {
                sender.sendMessage(Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
                return true;
            }
            
            if (player2 == null) {
                sender.sendMessage(Component.text("Player '" + args[1] + "' not found!", NamedTextColor.RED));
                return true;
            }
            
            player1.teleport(player2.getLocation());
            player1.sendMessage(Component.text("You have been teleported to " + player2.getName() + "!", NamedTextColor.GREEN));
            sender.sendMessage(Component.text("Teleported " + player1.getName() + " to " + player2.getName() + "!", NamedTextColor.GREEN));
        }
        
        return true;
    }
} 
package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public GodCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.god")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        Player target;
        
        if (args.length == 0) {
            // Toggle god mode for sender
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console cannot have god mode! Specify a player.", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        } else {
            // Toggle god mode for specified player
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
                return true;
            }
        }
        
        boolean isInGodMode = plugin.getPlayerDataManager().isInGodMode(target);
        plugin.getPlayerDataManager().setGodMode(target, !isInGodMode);
        
        if (!isInGodMode) {
            // Player now has god mode
            target.sendMessage(Component.text("God mode enabled! You are now invincible.", NamedTextColor.GREEN));
            if (!sender.equals(target)) {
                sender.sendMessage(Component.text("Enabled god mode for " + target.getName() + "!", NamedTextColor.GREEN));
            }
        } else {
            // Player no longer has god mode
            target.sendMessage(Component.text("God mode disabled! You are no longer invincible.", NamedTextColor.YELLOW));
            if (!sender.equals(target)) {
                sender.sendMessage(Component.text("Disabled god mode for " + target.getName() + "!", NamedTextColor.YELLOW));
            }
        }
        
        return true;
    }
} 
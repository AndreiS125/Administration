package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public FlyCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.fly")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        Player target;
        
        if (args.length == 0) {
            // Toggle fly for sender
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console cannot fly! Specify a player.", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        } else {
            // Toggle fly for specified player
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
                return true;
            }
        }
        
        boolean isFlying = plugin.getPlayerDataManager().isFlying(target);
        plugin.getPlayerDataManager().setFlying(target, !isFlying);
        
        if (!isFlying) {
            // Player can now fly
            target.sendMessage(Component.text("Flight enabled!", NamedTextColor.GREEN));
            if (!sender.equals(target)) {
                sender.sendMessage(Component.text("Enabled flight for " + target.getName() + "!", NamedTextColor.GREEN));
            }
        } else {
            // Player can no longer fly
            target.sendMessage(Component.text("Flight disabled!", NamedTextColor.YELLOW));
            if (!sender.equals(target)) {
                sender.sendMessage(Component.text("Disabled flight for " + target.getName() + "!", NamedTextColor.YELLOW));
            }
        }
        
        return true;
    }
} 
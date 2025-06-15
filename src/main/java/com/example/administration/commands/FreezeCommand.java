package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public FreezeCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.freeze")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /freeze <player>", NamedTextColor.RED));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
            return true;
        }
        
        boolean isFrozen = plugin.getPlayerDataManager().isFrozen(target);
        plugin.getPlayerDataManager().setFrozen(target, !isFrozen);
        
        if (!isFrozen) {
            // Player is now frozen
            target.sendMessage(Component.text("You have been frozen! You cannot move or interact.", NamedTextColor.RED));
            sender.sendMessage(Component.text("Frozen " + target.getName() + "!", NamedTextColor.GREEN));
        } else {
            // Player is no longer frozen
            target.sendMessage(Component.text("You have been unfrozen! You can now move and interact.", NamedTextColor.GREEN));
            sender.sendMessage(Component.text("Unfrozen " + target.getName() + "!", NamedTextColor.YELLOW));
        }
        
        return true;
    }
} 
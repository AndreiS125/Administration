package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import com.example.administration.managers.DisguiseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndisguiseCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    private final DisguiseManager disguiseManager;
    
    public UndisguiseCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
        this.disguiseManager = plugin.getDisguiseManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.disguise")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        Player target;
        
        if (args.length == 0) {
            // Remove disguise from sender
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console must specify a target player!", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        } else {
            // Remove disguise from specified player
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[0] + "' not found!", NamedTextColor.RED));
                return true;
            }
        }
        
        if (!disguiseManager.isDisguised(target)) {
            sender.sendMessage(Component.text(target.getName() + " is not disguised!", NamedTextColor.YELLOW));
            return true;
        }
        
        DisguiseManager.DisguiseData disguise = disguiseManager.getDisguise(target);
        disguiseManager.undisguise(target);
        
        target.sendMessage(Component.text("Your disguise has been removed!", NamedTextColor.GREEN));
        if (!sender.equals(target)) {
            sender.sendMessage(Component.text("Removed " + target.getName() + "'s disguise (" + disguise.getName() + ")!", NamedTextColor.GREEN));
        }
        
        return true;
    }
} 
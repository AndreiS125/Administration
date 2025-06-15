package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public GamemodeCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.gamemode")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /gamemode <mode> [player]", NamedTextColor.RED));
            return true;
        }
        
        GameMode gameMode = parseGameMode(args[0]);
        if (gameMode == null) {
            sender.sendMessage(Component.text("Invalid gamemode! Use: survival, creative, adventure, spectator", NamedTextColor.RED));
            return true;
        }
        
        Player target;
        
        if (args.length == 1) {
            // Change sender's gamemode
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console cannot change gamemode! Specify a player.", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        } else {
            // Change specified player's gamemode
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[1] + "' not found!", NamedTextColor.RED));
                return true;
            }
        }
        
        target.setGameMode(gameMode);
        target.sendMessage(Component.text("Your gamemode has been changed to " + gameMode.name().toLowerCase() + "!", NamedTextColor.GREEN));
        
        if (!sender.equals(target)) {
            sender.sendMessage(Component.text("Changed " + target.getName() + "'s gamemode to " + gameMode.name().toLowerCase() + "!", NamedTextColor.GREEN));
        }
        
        return true;
    }
    
    private GameMode parseGameMode(String mode) {
        return switch (mode.toLowerCase()) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }
} 
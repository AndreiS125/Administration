package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpyCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public SpyCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }
        
        if (!sender.hasPermission("administration.spy")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        boolean hasCommandSpy = plugin.getPlayerDataManager().hasCommandSpy(player);
        plugin.getPlayerDataManager().setCommandSpy(player, !hasCommandSpy);
        
        if (!hasCommandSpy) {
            // Command spy is now enabled
            player.sendMessage(Component.text("Command spy enabled! You will now see other players' commands.", NamedTextColor.GREEN));
        } else {
            // Command spy is now disabled
            player.sendMessage(Component.text("Command spy disabled! You will no longer see other players' commands.", NamedTextColor.YELLOW));
        }
        
        return true;
    }
} 
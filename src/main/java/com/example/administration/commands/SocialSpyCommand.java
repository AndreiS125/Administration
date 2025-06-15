package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public SocialSpyCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }
        
        if (!sender.hasPermission("administration.socialspy")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        boolean hasSocialSpy = plugin.getPlayerDataManager().hasSocialSpy(player);
        plugin.getPlayerDataManager().setSocialSpy(player, !hasSocialSpy);
        
        if (!hasSocialSpy) {
            // Social spy is now enabled
            player.sendMessage(Component.text("Social spy enabled! You will now see private messages.", NamedTextColor.GREEN));
        } else {
            // Social spy is now disabled
            player.sendMessage(Component.text("Social spy disabled! You will no longer see private messages.", NamedTextColor.YELLOW));
        }
        
        return true;
    }
} 
package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import com.example.administration.managers.DisguiseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DisguiseCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    private final DisguiseManager disguiseManager;
    
    // Common mob types for disguising
    private static final List<EntityType> ALLOWED_MOBS = Arrays.asList(
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER,
        EntityType.ENDERMAN, EntityType.WITCH, EntityType.VILLAGER, EntityType.COW,
        EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN, EntityType.WOLF,
        EntityType.CAT, EntityType.HORSE, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM,
        EntityType.BLAZE, EntityType.GHAST, EntityType.SLIME, EntityType.MAGMA_CUBE,
        EntityType.WITHER_SKELETON, EntityType.STRAY, EntityType.HUSK, EntityType.PHANTOM,
        EntityType.DROWNED, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VEX
    );
    
    public DisguiseCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
        this.disguiseManager = plugin.getDisguiseManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.disguise")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        String type = args[0].toLowerCase();
        Player target;
        
        // Determine target player
        if (args.length >= 3) {
            // Target specified
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[2] + "' not found!", NamedTextColor.RED));
                return true;
            }
        } else {
            // Use sender as target
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console must specify a target player!", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        }
        
        switch (type) {
            case "player", "p" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /disguise player <playername> [target]", NamedTextColor.RED));
                    return true;
                }
                String playerName = args[1];
                disguiseManager.disguiseAsPlayer(target, playerName);
                
                target.sendMessage(Component.text("You are now disguised as " + playerName + "!", NamedTextColor.GREEN));
                if (!sender.equals(target)) {
                    sender.sendMessage(Component.text("Disguised " + target.getName() + " as " + playerName + "!", NamedTextColor.GREEN));
                }
            }
            
            case "mob", "m" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /disguise mob <mobtype> [target]", NamedTextColor.RED));
                    sender.sendMessage(Component.text("Available mobs: " + getMobList(), NamedTextColor.GRAY));
                    return true;
                }
                
                EntityType mobType;
                try {
                    mobType = EntityType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid mob type: " + args[1], NamedTextColor.RED));
                    sender.sendMessage(Component.text("Available mobs: " + getMobList(), NamedTextColor.GRAY));
                    return true;
                }
                
                if (!ALLOWED_MOBS.contains(mobType)) {
                    sender.sendMessage(Component.text("That mob type is not allowed for disguises!", NamedTextColor.RED));
                    sender.sendMessage(Component.text("Available mobs: " + getMobList(), NamedTextColor.GRAY));
                    return true;
                }
                
                disguiseManager.disguiseAsMob(target, mobType);
                
                target.sendMessage(Component.text("You are now disguised as a " + mobType.name().toLowerCase() + "!", NamedTextColor.GREEN));
                if (!sender.equals(target)) {
                    sender.sendMessage(Component.text("Disguised " + target.getName() + " as a " + mobType.name().toLowerCase() + "!", NamedTextColor.GREEN));
                }
            }
            
            case "block", "b" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /disguise block <blocktype> [target]", NamedTextColor.RED));
                    sender.sendMessage(Component.text("Example: /disguise block stone", NamedTextColor.GRAY));
                    return true;
                }
                
                String blockType = args[1].toUpperCase();
                try {
                    Material.valueOf(blockType);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid block type: " + args[1], NamedTextColor.RED));
                    return true;
                }
                
                disguiseManager.disguiseAsBlock(target, blockType);
                
                target.sendMessage(Component.text("You are now disguised as " + blockType.toLowerCase() + "!", NamedTextColor.GREEN));
                if (!sender.equals(target)) {
                    sender.sendMessage(Component.text("Disguised " + target.getName() + " as " + blockType.toLowerCase() + "!", NamedTextColor.GREEN));
                }
            }
            
            default -> {
                sender.sendMessage(Component.text("Invalid disguise type: " + type, NamedTextColor.RED));
                sendUsage(sender);
            }
        }
        
        return true;
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("Disguise Command Usage:", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/disguise player <playername> [target] - Disguise as another player", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/disguise mob <mobtype> [target] - Disguise as a mob", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/disguise block <blocktype> [target] - Disguise as a block", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("Examples:", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("  /disguise player Notch", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  /disguise mob zombie", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  /disguise block stone", NamedTextColor.GRAY));
    }
    
    private String getMobList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ALLOWED_MOBS.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(ALLOWED_MOBS.get(i).name().toLowerCase());
            if (i >= 10) { // Limit display to avoid spam
                sb.append("...");
                break;
            }
        }
        return sb.toString();
    }
} 
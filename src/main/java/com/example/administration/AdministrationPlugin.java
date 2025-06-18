package com.example.administration;

import com.example.administration.commands.*;
import com.example.administration.listeners.PlayerListener;
import com.example.administration.managers.DisguiseManager;
import com.example.administration.managers.PlayerDataManager;
import com.example.administration.managers.ProtectionManager;
import com.example.administration.managers.StealthManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdministrationPlugin extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private DisguiseManager disguiseManager;
    private StealthManager stealthManager;
    private ProtectionManager protectionManager;

    @Override
    public void onEnable() {
        // Check for ProtocolLib
        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib is required for disguise functionality!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers
        this.playerDataManager = new PlayerDataManager();
        this.disguiseManager = new DisguiseManager(this);
        this.stealthManager = new StealthManager(this);
        this.protectionManager = new ProtectionManager(this);
        
        // Register commands
        registerCommands();
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(stealthManager, this);
        getServer().getPluginManager().registerEvents(protectionManager, this);
        
        // Stealth mode: Hide plugin enable message after a delay
        getServer().getScheduler().runTaskLater(this, () -> {
            // This message will be filtered by stealth mode if active
            getLogger().info("Administration plugin has been enabled!");
        }, 1L);
    }

    @Override
    public void onDisable() {
        // Clean up any vanished players on shutdown
        if (playerDataManager != null) {
            playerDataManager.clearAllVanishedPlayers();
        }
        
        // Clean up any disguised players on shutdown
        if (disguiseManager != null) {
            disguiseManager.clearAllDisguises();
        }
        
        // Clean up stealth manager
        if (stealthManager != null) {
            stealthManager.cleanup();
        }
        
        // Clean up protection manager
        if (protectionManager != null) {
            protectionManager.cleanup();
        }
        
        getLogger().info("Administration plugin has been disabled!");
    }
    
    private void registerCommands() {
        // Register all command executors
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("enderchest").setExecutor(new EnderchestCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("spy").setExecutor(new SpyCommand(this));
        getCommand("stealth").setExecutor(new StealthCommand(this));
        getCommand("protect").setExecutor(new ProtectCommand(this));
        getCommand("fakechat").setExecutor(new FakeChatCommand(this));
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }
    
    public StealthManager getStealthManager() {
        return stealthManager;
    }
    
    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }
} 
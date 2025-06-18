package com.example.administration.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.example.administration.AdministrationPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseManager {
    
    private final AdministrationPlugin plugin;
    private final ProtocolManager protocolManager;
    private final Map<UUID, DisguiseData> disguisedPlayers = new HashMap<>();
    
    public DisguiseManager(AdministrationPlugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }
    
    public void disguiseAsPlayer(Player player, String targetName) {
        undisguise(player); // Remove any existing disguise
        
        // Create fake game profile with random UUID to avoid conflicts
        WrappedGameProfile fakeProfile = new WrappedGameProfile(UUID.randomUUID(), targetName);
        
        DisguiseData disguise = new DisguiseData(DisguiseType.PLAYER, targetName, fakeProfile);
        disguisedPlayers.put(player.getUniqueId(), disguise);
        
        // Update disguise for all players except the disguised player
        updateDisguiseForAllPlayers(player);
    }
    
    public void disguiseAsMob(Player player, EntityType mobType) {
        if (!mobType.isAlive() || mobType == EntityType.PLAYER) {
            return; // Invalid mob type
        }
        
        undisguise(player); // Remove any existing disguise
        
        DisguiseData disguise = new DisguiseData(DisguiseType.MOB, mobType.name(), mobType);
        disguisedPlayers.put(player.getUniqueId(), disguise);
        
        // Update disguise for all players except the disguised player
        updateDisguiseForAllPlayers(player);
    }
    
    public void disguiseAsBlock(Player player, String blockType) {
        undisguise(player); // Remove any existing disguise
        
        DisguiseData disguise = new DisguiseData(DisguiseType.BLOCK, blockType, blockType);
        disguisedPlayers.put(player.getUniqueId(), disguise);
        
        // Update disguise for all players except the disguised player
        updateDisguiseForAllPlayers(player);
    }
    
    public void undisguise(Player player) {
        DisguiseData disguise = disguisedPlayers.remove(player.getUniqueId());
        if (disguise == null) return;
        
        // Restore original appearance for all players
        updateDisguiseForAllPlayers(player);
    }
    
    public boolean isDisguised(Player player) {
        return disguisedPlayers.containsKey(player.getUniqueId());
    }
    
    public DisguiseData getDisguise(Player player) {
        return disguisedPlayers.get(player.getUniqueId());
    }
    
    public void handlePlayerJoin(Player newPlayer) {
        // Send disguise information for all currently disguised players to the new player
        for (Player disguisedPlayer : Bukkit.getOnlinePlayers()) {
            if (isDisguised(disguisedPlayer) && !newPlayer.equals(disguisedPlayer)) {
                updateDisguiseForPlayer(disguisedPlayer, newPlayer);
            }
        }
    }
    
    public void clearAllDisguises() {
        for (UUID uuid : new HashMap<>(disguisedPlayers).keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                undisguise(player);
            }
        }
        disguisedPlayers.clear();
    }
    
    private void updateDisguiseForAllPlayers(Player disguisedPlayer) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!viewer.equals(disguisedPlayer)) {
                updateDisguiseForPlayer(disguisedPlayer, viewer);
            }
        }
    }
    
    private void updateDisguiseForPlayer(Player disguisedPlayer, Player viewer) {
        try {
            DisguiseData disguise = disguisedPlayers.get(disguisedPlayer.getUniqueId());
            
            if (disguise == null) {
                // No disguise - show normal player
                refreshPlayerForViewer(disguisedPlayer, viewer);
                return;
            }
            
            // Apply disguise based on type
            switch (disguise.getType()) {
                case PLAYER -> applyPlayerDisguise(disguisedPlayer, viewer, (WrappedGameProfile) disguise.getData());
                case MOB -> applyMobDisguise(disguisedPlayer, viewer, (EntityType) disguise.getData());
                case BLOCK -> applyBlockDisguise(disguisedPlayer, viewer, (String) disguise.getData());
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to update disguise for " + disguisedPlayer.getName() + 
                                     " viewed by " + viewer.getName() + ": " + e.getMessage());
            // Fallback: show normal player
            refreshPlayerForViewer(disguisedPlayer, viewer);
        }
    }
    
    private void applyPlayerDisguise(Player disguisedPlayer, Player viewer, WrappedGameProfile fakeProfile) {
        try {
            // First, remove the original player entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Small delay to ensure packet is processed
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    // Spawn new player entity with fake profile
                    PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
                    
                    // Set entity ID
                    spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
                    
                    // Set fake UUID
                    spawnPacket.getUUIDs().write(0, fakeProfile.getUUID());
                    
                    // Set location
                    Location loc = disguisedPlayer.getLocation();
                    spawnPacket.getDoubles().write(0, loc.getX());
                    spawnPacket.getDoubles().write(1, loc.getY());
                    spawnPacket.getDoubles().write(2, loc.getZ());
                    
                    // Set rotation
                    spawnPacket.getBytes().write(0, (byte) (loc.getYaw() * 256.0F / 360.0F));
                    spawnPacket.getBytes().write(1, (byte) (loc.getPitch() * 256.0F / 360.0F));
                    
                    protocolManager.sendServerPacket(viewer, spawnPacket);
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to send player disguise spawn packet: " + e.getMessage());
                }
            }, 1L);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to apply player disguise: " + e.getMessage());
        }
    }
    
    private void applyMobDisguise(Player disguisedPlayer, Player viewer, EntityType mobType) {
        try {
            // Remove the original player entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Small delay to ensure packet is processed
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    // Spawn mob entity
                    PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
                    
                    // Set entity ID
                    spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
                    
                    // Set UUID
                    spawnPacket.getUUIDs().write(0, disguisedPlayer.getUniqueId());
                    
                    // Set entity type
                    spawnPacket.getEntityTypeModifier().write(0, mobType);
                    
                    // Set location
                    Location loc = disguisedPlayer.getLocation();
                    spawnPacket.getDoubles().write(0, loc.getX());
                    spawnPacket.getDoubles().write(1, loc.getY());
                    spawnPacket.getDoubles().write(2, loc.getZ());
                    
                    // Set velocity (usually 0)
                    spawnPacket.getIntegers().write(1, 0); // Velocity X
                    spawnPacket.getIntegers().write(2, 0); // Velocity Y
                    spawnPacket.getIntegers().write(3, 0); // Velocity Z
                    
                    // Set rotation
                    spawnPacket.getBytes().write(0, (byte) (loc.getYaw() * 256.0F / 360.0F));
                    spawnPacket.getBytes().write(1, (byte) (loc.getPitch() * 256.0F / 360.0F));
                    spawnPacket.getBytes().write(2, (byte) (loc.getYaw() * 256.0F / 360.0F)); // Head yaw
                    
                    protocolManager.sendServerPacket(viewer, spawnPacket);
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to send mob disguise spawn packet: " + e.getMessage());
                }
            }, 1L);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to apply mob disguise: " + e.getMessage());
        }
    }
    
    private void applyBlockDisguise(Player disguisedPlayer, Player viewer, String blockType) {
        try {
            // For simplicity, we'll disguise as a falling block (similar to mob)
            // Remove the original player entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Small delay to ensure packet is processed
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    // Spawn falling block entity
                    PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
                    
                    // Set entity ID
                    spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
                    
                    // Set UUID
                    spawnPacket.getUUIDs().write(0, disguisedPlayer.getUniqueId());
                    
                    // Set as falling block
                    spawnPacket.getEntityTypeModifier().write(0, EntityType.FALLING_BLOCK);
                    
                    // Set location
                    Location loc = disguisedPlayer.getLocation();
                    spawnPacket.getDoubles().write(0, loc.getX());
                    spawnPacket.getDoubles().write(1, loc.getY());
                    spawnPacket.getDoubles().write(2, loc.getZ());
                    
                    protocolManager.sendServerPacket(viewer, spawnPacket);
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to send block disguise spawn packet: " + e.getMessage());
                }
            }, 1L);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to apply block disguise: " + e.getMessage());
        }
    }
    
    private void refreshPlayerForViewer(Player player, Player viewer) {
        try {
            // Hide and then show the player to refresh their appearance
            viewer.hidePlayer(plugin, player);
            
            // Small delay before showing again
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                viewer.showPlayer(plugin, player);
            }, 2L);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to refresh player appearance: " + e.getMessage());
        }
    }
    
    // Inner classes
    public static class DisguiseData {
        private final DisguiseType type;
        private final String name;
        private final Object data;
        
        public DisguiseData(DisguiseType type, String name, Object data) {
            this.type = type;
            this.name = name;
            this.data = data;
        }
        
        public DisguiseType getType() { return type; }
        public String getName() { return name; }
        public Object getData() { return data; }
    }
    
    public enum DisguiseType {
        PLAYER, MOB, BLOCK
    }
} 
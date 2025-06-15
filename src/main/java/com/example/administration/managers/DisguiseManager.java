package com.example.administration.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.example.administration.AdministrationPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
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
        
        // Create fake game profile
        WrappedGameProfile fakeProfile = new WrappedGameProfile(UUID.randomUUID(), targetName);
        
        DisguiseData disguise = new DisguiseData(DisguiseType.PLAYER, targetName, fakeProfile);
        disguisedPlayers.put(player.getUniqueId(), disguise);
        
        // Send packets to all other players
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!viewer.equals(player)) {
                sendPlayerDisguisePackets(viewer, player, fakeProfile);
            }
        }
    }
    
    public void disguiseAsMob(Player player, EntityType mobType) {
        if (!mobType.isAlive() || mobType == EntityType.PLAYER) {
            return; // Invalid mob type
        }
        
        undisguise(player); // Remove any existing disguise
        
        DisguiseData disguise = new DisguiseData(DisguiseType.MOB, mobType.name(), mobType);
        disguisedPlayers.put(player.getUniqueId(), disguise);
        
        // Send packets to all other players
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!viewer.equals(player)) {
                sendMobDisguisePackets(viewer, player, mobType);
            }
        }
    }
    
    public void disguiseAsBlock(Player player, String blockType) {
        undisguise(player); // Remove any existing disguise
        
        DisguiseData disguise = new DisguiseData(DisguiseType.BLOCK, blockType, blockType);
        disguisedPlayers.put(player.getUniqueId(), disguise);
        
        // For block disguise, we'll use a falling block entity
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!viewer.equals(player)) {
                sendBlockDisguisePackets(viewer, player, blockType);
            }
        }
    }
    
    public void undisguise(Player player) {
        DisguiseData disguise = disguisedPlayers.remove(player.getUniqueId());
        if (disguise == null) return;
        
        // Send packets to restore original appearance
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!viewer.equals(player)) {
                sendUndisguisePackets(viewer, player);
            }
        }
    }
    
    public boolean isDisguised(Player player) {
        return disguisedPlayers.containsKey(player.getUniqueId());
    }
    
    public DisguiseData getDisguise(Player player) {
        return disguisedPlayers.get(player.getUniqueId());
    }
    
    public void handlePlayerJoin(Player newPlayer) {
        // Send disguise packets for all currently disguised players to the new player
        for (Player disguisedPlayer : Bukkit.getOnlinePlayers()) {
            DisguiseData disguise = disguisedPlayers.get(disguisedPlayer.getUniqueId());
            if (disguise != null && !newPlayer.equals(disguisedPlayer)) {
                switch (disguise.getType()) {
                    case PLAYER -> sendPlayerDisguisePackets(newPlayer, disguisedPlayer, (WrappedGameProfile) disguise.getData());
                    case MOB -> sendMobDisguisePackets(newPlayer, disguisedPlayer, (EntityType) disguise.getData());
                    case BLOCK -> sendBlockDisguisePackets(newPlayer, disguisedPlayer, (String) disguise.getData());
                }
            }
        }
    }
    
    public void clearAllDisguises() {
        for (UUID uuid : disguisedPlayers.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                undisguise(player);
            }
        }
        disguisedPlayers.clear();
    }
    
    private void sendPlayerDisguisePackets(Player viewer, Player disguisedPlayer, WrappedGameProfile fakeProfile) {
        try {
            // Remove the original player entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Spawn new player entity with fake profile
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
            spawnPacket.getUUIDs().write(0, fakeProfile.getUUID());
            
            Location loc = disguisedPlayer.getLocation();
            spawnPacket.getDoubles().write(0, loc.getX());
            spawnPacket.getDoubles().write(1, loc.getY());
            spawnPacket.getDoubles().write(2, loc.getZ());
            
            spawnPacket.getBytes().write(0, (byte) (loc.getYaw() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(1, (byte) (loc.getPitch() * 256.0F / 360.0F));
            
            protocolManager.sendServerPacket(viewer, spawnPacket);
            
            // Send player info packet with fake profile
            PacketContainer playerInfoPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            // Note: This is simplified - full implementation would need proper player info handling
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send player disguise packets: " + e.getMessage());
        }
    }
    
    private void sendMobDisguisePackets(Player viewer, Player disguisedPlayer, EntityType mobType) {
        try {
            // Remove the original player entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Spawn mob entity
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
            spawnPacket.getUUIDs().write(0, disguisedPlayer.getUniqueId());
            spawnPacket.getEntityTypeModifier().write(0, mobType);
            
            Location loc = disguisedPlayer.getLocation();
            spawnPacket.getDoubles().write(0, loc.getX());
            spawnPacket.getDoubles().write(1, loc.getY());
            spawnPacket.getDoubles().write(2, loc.getZ());
            
            spawnPacket.getIntegers().write(1, 0); // Velocity X
            spawnPacket.getIntegers().write(2, 0); // Velocity Y
            spawnPacket.getIntegers().write(3, 0); // Velocity Z
            
            spawnPacket.getBytes().write(0, (byte) (loc.getYaw() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(1, (byte) (loc.getPitch() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(2, (byte) (loc.getYaw() * 256.0F / 360.0F)); // Head yaw
            
            protocolManager.sendServerPacket(viewer, spawnPacket);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send mob disguise packets: " + e.getMessage());
        }
    }
    
    private void sendBlockDisguisePackets(Player viewer, Player disguisedPlayer, String blockType) {
        try {
            // Remove the original player entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Spawn falling block entity (simplified approach)
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
            spawnPacket.getUUIDs().write(0, disguisedPlayer.getUniqueId());
            spawnPacket.getEntityTypeModifier().write(0, EntityType.FALLING_BLOCK);
            
            Location loc = disguisedPlayer.getLocation();
            spawnPacket.getDoubles().write(0, loc.getX());
            spawnPacket.getDoubles().write(1, loc.getY());
            spawnPacket.getDoubles().write(2, loc.getZ());
            
            protocolManager.sendServerPacket(viewer, spawnPacket);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send block disguise packets: " + e.getMessage());
        }
    }
    
    private void sendUndisguisePackets(Player viewer, Player disguisedPlayer) {
        try {
            // Remove the disguised entity
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntegerArrays().write(0, new int[]{disguisedPlayer.getEntityId()});
            protocolManager.sendServerPacket(viewer, destroyPacket);
            
            // Respawn the original player
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawnPacket.getIntegers().write(0, disguisedPlayer.getEntityId());
            spawnPacket.getUUIDs().write(0, disguisedPlayer.getUniqueId());
            
            Location loc = disguisedPlayer.getLocation();
            spawnPacket.getDoubles().write(0, loc.getX());
            spawnPacket.getDoubles().write(1, loc.getY());
            spawnPacket.getDoubles().write(2, loc.getZ());
            
            spawnPacket.getBytes().write(0, (byte) (loc.getYaw() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(1, (byte) (loc.getPitch() * 256.0F / 360.0F));
            
            protocolManager.sendServerPacket(viewer, spawnPacket);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send undisguise packets: " + e.getMessage());
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
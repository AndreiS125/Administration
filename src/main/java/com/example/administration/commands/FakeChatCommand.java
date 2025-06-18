package com.example.administration.commands;

import com.example.administration.AdministrationPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class FakeChatCommand implements CommandExecutor {
    
    private final AdministrationPlugin plugin;
    
    public FakeChatCommand(AdministrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("administration.fakechat")) {
            // В режиме скрытности не показываем сообщения об отказе доступа
            if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
                return true;
            }
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length < 2) {
            sendMessage(sender, Component.text("Usage: /fakechat <player> <message>", NamedTextColor.YELLOW));
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        // Проверим, существует ли игрок (онлайн или оффлайн)
        if (target == null && !Bukkit.getOfflinePlayer(targetName).hasPlayedBefore()) {
            sendMessage(sender, Component.text("Player '" + targetName + "' not found!", NamedTextColor.RED));
            return true;
        }
        
        // Собираем сообщение из оставшихся аргументов
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(args[i]);
        }
        String message = messageBuilder.toString();
        
        // Отправляем поддельное сообщение в чат
        sendFakeMessage(targetName, message);
        
        // Уведомляем администратора об успешной отправке
        sendMessage(sender, Component.text("Message sent as " + targetName + ": " + message, NamedTextColor.GREEN));
        
        // Логируем действие для безопасности
        plugin.getLogger().info("[FAKE CHAT] " + sender.getName() + " sent message as " + targetName + ": " + message);
        
        return true;
    }
    
    private void sendFakeMessage(String playerName, String message) {
        // Создаем форматированное сообщение в стиле обычного чата
        Component chatMessage = Component.text("<" + playerName + "> " + message, NamedTextColor.WHITE);
        
        // Отправляем всем игрокам онлайн
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(chatMessage);
        }
        
        // Также отправляем в консоль
        Bukkit.getConsoleSender().sendMessage(chatMessage);
    }
    
    private void sendMessage(CommandSender sender, Component message) {
        // Проверяем, находится ли отправитель в режиме скрытности
        if (sender instanceof Player player && plugin.getStealthManager().isInStealthMode(player)) {
            // Отправляем сообщение в стиле скрытности
            String plainText = ((net.kyori.adventure.text.TextComponent) message).content();
            player.sendMessage("§8[§7Silent§8] §f" + plainText);
        } else {
            sender.sendMessage(message);
        }
    }
} 
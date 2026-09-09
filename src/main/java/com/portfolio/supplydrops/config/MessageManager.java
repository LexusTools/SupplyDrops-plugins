package com.portfolio.supplydrops.config;

import com.portfolio.supplydrops.SupplyDropsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class MessageManager {

    private final SupplyDropsPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private FileConfiguration messages;
    private String prefix;

    public MessageManager(SupplyDropsPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(file);
        this.prefix = messages.getString("prefix", "<gray>[</gray><gold>SupplyDrops</gold><gray>]</gray> ");
    }

    public Component get(String key) {
        String raw = messages.getString(key, "<red>Missing: " + key + "</red>");
        return miniMessage.deserialize(raw.replace("%prefix%", prefix));
    }

    public Component get(String key, Map<String, String> placeholders) {
        String raw = messages.getString(key, "<red>Missing: " + key + "</red>");
        raw = raw.replace("%prefix%", prefix);
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            raw = raw.replace("%" + e.getKey() + "%", e.getValue());
        }
        return miniMessage.deserialize(raw);
    }

    public void send(Player player, String key) {
        player.sendMessage(get(key));
    }

    public void broadcast(String key, Map<String, String> placeholders) {
        Component msg = get(key, placeholders);
        plugin.getServer().broadcast(msg);
    }
}

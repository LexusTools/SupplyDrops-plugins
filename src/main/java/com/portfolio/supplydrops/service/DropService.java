package com.portfolio.supplydrops.service;

import com.portfolio.supplydrops.SupplyDropsPlugin;
import com.portfolio.supplydrops.config.ConfigManager;
import com.portfolio.supplydrops.config.MessageManager;
import com.portfolio.supplydrops.model.SupplyDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DropService {

    private final SupplyDropsPlugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final Map<String, SupplyDrop> drops = new ConcurrentHashMap<>();
    private final Map<String, BukkitTask> tasks = new ConcurrentHashMap<>();

    public DropService(SupplyDropsPlugin plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    public void shutdown() {
        for (BukkitTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
        for (SupplyDrop drop : drops.values()) {
            if (drop.getState() == SupplyDrop.State.AVAILABLE || drop.getState() == SupplyDrop.State.FALLING) {
                drop.transitionTo(SupplyDrop.State.CANCELLED);
            }
        }
        drops.clear();
    }

    public String createDrop(Player admin, Location loc, List<ItemStack> contents) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Location start = loc.clone();
        start.setY(start.getY() + configManager.getStartHeight());
        SupplyDrop drop = new SupplyDrop(id, loc, contents);
        drops.put(id, drop);
        drop.transitionTo(SupplyDrop.State.FALLING);
        Map<String, String> ph = Map.of(
                "drop_id", id,
                "x", String.valueOf(loc.getBlockX()),
                "y", String.valueOf(loc.getBlockY()),
                "z", String.valueOf(loc.getBlockZ())
        );
        messageManager.broadcast("drop-created", ph);
        int duration = configManager.getFallDurationTicks();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                ticks++;
                if (ticks >= duration) {
                    land(drop);
                    BukkitTask t = tasks.remove(id);
                    if (t != null) t.cancel();
                }
            }
        }, 1L, 1L);
        tasks.put(id, task);
        return id;
    }

    private void land(SupplyDrop drop) {
        if (!drop.transitionTo(SupplyDrop.State.LANDED)) {
            return;
        }
        Location loc = drop.getLocation();
        Block block = loc.getBlock();
        block.setType(Material.CHEST);
        if (block.getState() instanceof Chest chest) {
            for (ItemStack item : drop.getContents()) {
                chest.getInventory().addItem(item);
            }
        }
        drop.transitionTo(SupplyDrop.State.AVAILABLE);
        Map<String, String> ph = Map.of("drop_id", drop.getId());
        messageManager.broadcast("drop-landed", ph);
        int groundTicks = configManager.getGroundDurationSeconds() * 20;
        BukkitTask expire = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (drop.getState() == SupplyDrop.State.AVAILABLE) {
                drop.transitionTo(SupplyDrop.State.EXPIRED);
                loc.getBlock().setType(Material.AIR);
            }
        }, groundTicks);
        tasks.put(drop.getId() + "-expire", expire);
    }

    public SupplyDrop getDrop(String id) {
        return drops.get(id);
    }

    public List<SupplyDrop> getAllDrops() {
        return new ArrayList<>(drops.values());
    }

    public boolean cancelDrop(String id) {
        SupplyDrop drop = drops.get(id);
        if (drop == null) {
            return false;
        }
        BukkitTask t = tasks.remove(id);
        if (t != null) t.cancel();
        return drop.transitionTo(SupplyDrop.State.CANCELLED);
    }

    public boolean removeDrop(String id) {
        SupplyDrop drop = drops.remove(id);
        if (drop == null) {
            return false;
        }
        BukkitTask t = tasks.remove(id);
        if (t != null) t.cancel();
        if (drop.getLocation().getBlock().getType() == Material.CHEST) {
            drop.getLocation().getBlock().setType(Material.AIR);
        }
        return true;
    }
}

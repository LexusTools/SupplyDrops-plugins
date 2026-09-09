package com.portfolio.supplydrops.config;

import com.portfolio.supplydrops.SupplyDropsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigManager {

    private final SupplyDropsPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(SupplyDropsPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public int getStartHeight() {
        return Math.max(20, config.getInt("drop.start-height", 100));
    }

    public int getFallDurationTicks() {
        return Math.max(20, config.getInt("drop.fall-duration-ticks", 100));
    }

    public int getGroundDurationSeconds() {
        return Math.max(10, config.getInt("drop.ground-duration-seconds", 300));
    }

    public boolean isParticles() {
        return config.getBoolean("drop.particles", true);
    }

    public boolean isSounds() {
        return config.getBoolean("drop.sounds", true);
    }
}

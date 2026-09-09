package com.portfolio.supplydrops;

import com.portfolio.supplydrops.command.AdsCommand;
import com.portfolio.supplydrops.config.ConfigManager;
import com.portfolio.supplydrops.config.MessageManager;
import com.portfolio.supplydrops.gui.GuiListener;
import com.portfolio.supplydrops.service.DropService;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SupplyDropsPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DropService dropService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.dropService = new DropService(this, configManager, messageManager);
        Objects.requireNonNull(getCommand("ads")).setExecutor(new AdsCommand(this, dropService, messageManager));
        getServer().getPluginManager().registerEvents(new GuiListener(dropService), this);
        getLogger().info("SupplyDrops enabled.");
    }

    @Override
    public void onDisable() {
        if (dropService != null) {
            dropService.shutdown();
        }
        getLogger().info("SupplyDrops disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DropService getDropService() {
        return dropService;
    }
}

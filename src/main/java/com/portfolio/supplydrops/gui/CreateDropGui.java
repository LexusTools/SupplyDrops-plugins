package com.portfolio.supplydrops.gui;

import com.portfolio.supplydrops.SupplyDropsPlugin;
import com.portfolio.supplydrops.service.DropService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class CreateDropGui {

    public static final String GUI_ID = "supplydrops:create";

    private final SupplyDropsPlugin plugin;
    private final DropService dropService;
    private final Player player;
    private Inventory inventory;

    public CreateDropGui(SupplyDropsPlugin plugin, DropService dropService, Player player) {
        this.plugin = plugin;
        this.dropService = dropService;
        this.player = player;
    }

    public void open() {
        inventory = Bukkit.createInventory(new CreateDropHolder(GUI_ID), 54, MiniMessage.miniMessage().deserialize("<gold>Create Supply Drop</gold>"));
        inventory.setItem(53, createButton(Material.EMERALD_BLOCK, "<green>Confirm"));
        inventory.setItem(45, createButton(Material.REDSTONE_BLOCK, "<red>Cancel"));
        player.openInventory(inventory);
    }

    private ItemStack createButton(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    public void confirm() {
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.getType().isAir()) {
                contents.add(item.clone());
            }
        }
        if (contents.isEmpty()) {
            player.sendMessage("Add items first.");
            return;
        }
        dropService.createDrop(player, player.getLocation(), contents);
        player.closeInventory();
    }
}

package com.portfolio.supplydrops.gui;

import com.portfolio.supplydrops.service.DropService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public final class GuiListener implements Listener {

    private final DropService dropService;

    public GuiListener(DropService dropService) {
        this.dropService = dropService;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof CreateDropHolder holder)) {
            return;
        }
        if (!CreateDropGui.GUI_ID.equals(holder.getId())) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot >= 45) {
            event.setCancelled(true);
            if (slot == 53) {
                new CreateDropGui(null, dropService, player).confirm();
            } else if (slot == 45) {
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof CreateDropHolder) {
            for (int slot : event.getRawSlots()) {
                if (slot >= 45) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}

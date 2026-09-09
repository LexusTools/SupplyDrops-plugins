package com.portfolio.supplydrops.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class CreateDropHolder implements InventoryHolder {

    private final String id;

    public CreateDropHolder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

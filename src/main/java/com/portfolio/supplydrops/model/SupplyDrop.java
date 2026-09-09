package com.portfolio.supplydrops.model;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SupplyDrop {

    public enum State {
        CREATING, FALLING, LANDED, AVAILABLE, BEING_LOOTED, LOOTED, DESTROYED, EXPIRED, CANCELLED
    }

    private final String id;
    private final Location location;
    private final List<ItemStack> contents;
    private State state;
    private final long createdAt;

    public SupplyDrop(String id, Location location, List<ItemStack> contents) {
        this.id = id;
        this.location = location.clone();
        this.contents = new ArrayList<>();
        for (ItemStack item : contents) {
            if (item != null) {
                this.contents.add(item.clone());
            }
        }
        this.state = State.CREATING;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location.clone();
    }

    public List<ItemStack> getContents() {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack item : contents) {
            copy.add(item.clone());
        }
        return copy;
    }

    public State getState() {
        return state;
    }

    public boolean transitionTo(State next) {
        if (isValidTransition(state, next)) {
            this.state = next;
            return true;
        }
        return false;
    }

    private boolean isValidTransition(State from, State to) {
        return switch (from) {
            case CREATING -> to == State.FALLING || to == State.CANCELLED;
            case FALLING -> to == State.LANDED || to == State.CANCELLED || to == State.DESTROYED;
            case LANDED -> to == State.AVAILABLE || to == State.DESTROYED || to == State.EXPIRED;
            case AVAILABLE -> to == State.BEING_LOOTED || to == State.DESTROYED || to == State.EXPIRED;
            case BEING_LOOTED -> to == State.LOOTED || to == State.AVAILABLE || to == State.DESTROYED;
            case LOOTED, DESTROYED, EXPIRED, CANCELLED -> false;
        };
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isLootable() {
        return state == State.AVAILABLE || state == State.BEING_LOOTED;
    }
}

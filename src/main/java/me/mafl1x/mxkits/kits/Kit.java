package me.mafl1x.mxkits.kits;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit {

    private final String name;
    private double delayCooldown;
    private int point;
    private Map<Integer, ItemStack> itemsBySlot = new HashMap<>();
    private String permission;

    public Kit(String name, Map<Integer, ItemStack> itemsBySlot, double delayCooldown, int point, String permission) {
        this.name = name;
        this.itemsBySlot = (itemsBySlot != null) ? itemsBySlot : new HashMap<>();
        this.delayCooldown = delayCooldown;
        this.point = point;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, ItemStack> getItemsBySlot() {
        return itemsBySlot;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(itemsBySlot.values());
    }

    public void setItemsBySlot(Map<Integer, ItemStack> itemsBySlot) {
        this.itemsBySlot = (itemsBySlot != null) ? itemsBySlot : new HashMap<>();
    }

    public double getDelayCooldown() {
        return delayCooldown;
    }

    public int getPoint() {
        return point;
    }

    public String getPermission() {
        return permission;
    }

    public void setDelayCooldown(double delayCooldown) {
        this.delayCooldown = delayCooldown;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}

package me.mafl1x.mxkits.kits;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private final String name;
    private double delayCooldown;
    private int point;
    private List<ItemStack> items = new ArrayList<>();
    private String[] groups;

    public Kit(String name, List<ItemStack> items, double delayCooldown, int point, String[] groups) {
        this.name = name;
        this.items = (items != null) ? items : new ArrayList<>();
        this.delayCooldown = delayCooldown;
        this.point = point;
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public double getDelayCooldown() {
        return delayCooldown;
    }

    public int getPoint() {
        return point;
    }

    public String[] getGroups() {
        return groups;
    }
}

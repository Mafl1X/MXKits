package me.mafl1x.mxkits.menu.item;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemMenu {

    private List<Integer> slots;
    private ItemStack itemStack;

    public ItemMenu(List<Integer> slots, ItemStack itemStack) {
        this.slots = slots;
        this.itemStack = itemStack;
    }

    public List<Integer> getSlot() {
        return slots;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}

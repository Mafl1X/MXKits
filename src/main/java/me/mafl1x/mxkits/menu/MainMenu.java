package me.mafl1x.mxkits.menu;

import me.mafl1x.mxkits.menu.item.ItemMenu;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface MainMenu {
    String name();
    int rows();
    Sound openSound();
    List<ItemMenu> itemsDefault();
    List<ItemStack> itemsInKit();
    boolean isModify();
    Inventory getInventory();
}

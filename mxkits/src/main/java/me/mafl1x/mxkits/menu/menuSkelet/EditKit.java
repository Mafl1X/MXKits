package me.mafl1x.mxkits.menu.menuSkelet;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.menu.MainMenu;
import me.mafl1x.mxkits.menu.item.ItemMenu;
import me.mafl1x.mxkits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EditKit implements MainMenu, IEditKit {

    private final String kitName;

    public EditKit(String kitName) {
        this.kitName = kitName;
    }

    @Override
    public String name() {
        return Utils.color(MXKits.getInstance().getConfig().getString("menuEdit.settings.name", "&8Edit Kit"));
    }

    @Override
    public int rows() {
        return 4;
    }

    @Override
    public Sound openSound() {
        try {
            return Sound.valueOf(MXKits.getInstance().getConfig().getString("menuEdit.settings.soundOpen", "BLOCK_CHEST_OPEN"));
        } catch (IllegalArgumentException e) {
            return Sound.BLOCK_CHEST_OPEN;
        }
    }

    @Override
    public List<ItemMenu> itemsDefault() {
        return new ArrayList<>();
    }

    @Override
    public List<ItemStack> itemsInKit() {
        return MXKits.getInstance().getKitManager().getKit(kitName) != null
                ? MXKits.getInstance().getKitManager().getKit(kitName).getItems()
                : new ArrayList<>();
    }

    @Override
    public boolean isModify() {
        return true;
    }

    @Override
    public Inventory getInventory() {
        int size = rows() * 9;
        Inventory inventory = Bukkit.createInventory(null, size, name());
        for (ItemStack item : itemsInKit()) {
            if (item != null) inventory.addItem(item);
        }
        return inventory;
    }

    public String getKitName() {
        return kitName;
    }
}

package me.mafl1x.mxkits.menu.menuSkelet;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.kits.Kit;
import me.mafl1x.mxkits.menu.MainMenu;
import me.mafl1x.mxkits.menu.item.ItemMenu;
import me.mafl1x.mxkits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Preview implements MainMenu, IPreview {

    private final String kitName;

    public Preview(String kitName) {
        this.kitName = kitName;
    }

    @Override
    public String name() {
        return Utils.color(MXKits.getInstance().getConfig().getString("menuPreview.settings.name", "&8Kit Preview"));
    }

    @Override
    public int rows() {
        return MXKits.getInstance().getConfig().getInt("menuPreview.settings.rows", 6);
    }

    @Override
    public Sound openSound() {
        try {
            return Sound.valueOf(MXKits.getInstance().getConfig().getString("menuPreview.settings.soundOpen", "BLOCK_CHEST_OPEN"));
        } catch (IllegalArgumentException e) {
            return Sound.BLOCK_CHEST_OPEN;
        }
    }

    @Override
    public boolean isModify() {
        return false;
    }

    @Override
    public List<ItemMenu> itemsDefault() {
        List<ItemMenu> items = new ArrayList<>();
        if (MXKits.getInstance().getConfig().getConfigurationSection("menuPreview.other") == null) return items;
        for (String key : MXKits.getInstance().getConfig().getConfigurationSection("menuPreview.other").getKeys(false)) {
            items.add(Utils.getItemInMenu("menuPreview.other." + key));
        }
        return items;
    }

    @Override
    public List<ItemStack> itemsInKit() {
        Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
        return kit != null ? kit.getItems() : new ArrayList<>();
    }

    @Override
    public Inventory getInventory() {
        int size = rows() * 9;
        Inventory inventory = Bukkit.createInventory(null, size, name());

        for (ItemMenu itemMenu : itemsDefault()) {
            for (int slot : itemMenu.getSlot()) {
                inventory.setItem(slot, itemMenu.getItemStack());
            }
        }

        Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
        if (kit != null) {
            Map<Integer, ItemStack> itemsBySlot = kit.getItemsBySlot();
            for (Map.Entry<Integer, ItemStack> entry : itemsBySlot.entrySet()) {
                int slot = entry.getKey();
                ItemStack item = entry.getValue();
                if (item == null) continue;
                if (slot >= 0 && slot < size && inventory.getItem(slot) == null) {
                    inventory.setItem(slot, item);
                } else {
                    inventory.addItem(item);
                }
            }
        }

        for (int slot : buyKit().getSlot()) {
            inventory.setItem(slot, buyKit().getItemStack());
        }

        for (int slot : takeItKit().getSlot()) {
            inventory.setItem(slot, takeItKit().getItemStack());
        }

        return inventory;
    }

    @Override
    public ItemMenu buyKit() {
        Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
        ItemMenu buyItem = Utils.getItemInMenu("menuPreview.buyItem");
        ItemStack stack = buyItem.getItemStack().clone();
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = new ArrayList<>();
            String points = String.valueOf(kit != null ? kit.getPoint() : 0);
            for (String line : meta.getLore()) {
                lore.add(line.replace("%points", points));
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        buyItem.setItemStack(stack);
        return buyItem;
    }

    @Override
    public ItemMenu takeItKit() {
        return Utils.getItemInMenu("menuPreview.takeIt");
    }

    public String getKitName() {
        return kitName;
    }
}

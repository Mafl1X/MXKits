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
import java.util.Map;

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
        return MXKits.getInstance().getConfig().getInt("menuEdit.settings.rows", 6);
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
        List<ItemMenu> items = new ArrayList<>();
        if (MXKits.getInstance().getConfig().getConfigurationSection("menuEdit.other") != null) {
            for (String key : MXKits.getInstance().getConfig().getConfigurationSection("menuEdit.other").getKeys(false)) {
                items.add(Utils.getItemInMenu("menuEdit.other." + key));
            }
        }
        return items;
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

        for (ItemMenu itemMenu : itemsDefault()) {
            for (int slot : itemMenu.getSlot()) {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, itemMenu.getItemStack());
                }
            }
        }

        if (MXKits.getInstance().getKitManager().getKit(kitName) != null) {
            Map<Integer, ItemStack> itemsBySlot =
                    MXKits.getInstance().getKitManager().getKit(kitName).getItemsBySlot();
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

        if (hasEditItems()) {
            ItemMenu pointsItem = applyKitPlaceholders(editPointsItem());
            for (int slot : pointsItem.getSlot()) {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, pointsItem.getItemStack());
                }
            }
            ItemMenu delayItem = applyKitPlaceholders(editDelayItem());
            for (int slot : delayItem.getSlot()) {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, delayItem.getItemStack());
                }
            }
            ItemMenu permissionItem = applyKitPlaceholders(editPermissionItem());
            for (int slot : permissionItem.getSlot()) {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, permissionItem.getItemStack());
                }
            }
        }
        return inventory;
    }

    public String getKitName() {
        return kitName;
    }

    public ItemMenu editPointsItem() {
        return Utils.getItemInMenu("menuEdit.edit.points");
    }

    public ItemMenu editDelayItem() {
        return Utils.getItemInMenu("menuEdit.edit.delay");
    }

    public ItemMenu editPermissionItem() {
        return Utils.getItemInMenu("menuEdit.edit.permission");
    }

    public boolean hasEditItems() {
        return MXKits.getInstance().getConfig().getConfigurationSection("menuEdit.edit") != null;
    }

    private ItemMenu applyKitPlaceholders(ItemMenu menu) {
        ItemStack stack = menu.getItemStack().clone();
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = new ArrayList<>();
            int points = 0;
            long delaySeconds = 0;
            String permission = "none";
            if (MXKits.getInstance().getKitManager().getKit(kitName) != null) {
                points = MXKits.getInstance().getKitManager().getKit(kitName).getPoint();
                delaySeconds = (long) (MXKits.getInstance().getKitManager().getKit(kitName).getDelayCooldown() / 1000.0);
                String kitPermission = MXKits.getInstance().getKitManager().getKit(kitName).getPermission();
                if (kitPermission != null && !kitPermission.trim().isEmpty()) {
                    permission = kitPermission;
                }
            }
            String cooldown = Utils.convertSecondsToDHMS((int) delaySeconds);
            for (String line : meta.getLore()) {
                lore.add(line
                        .replace("%points", String.valueOf(points))
                        .replace("%cooldown", cooldown)
                        .replace("%permission", permission));
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        menu.setItemStack(stack);
        return menu;
    }
}

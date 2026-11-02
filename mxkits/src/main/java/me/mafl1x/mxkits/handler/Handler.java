package me.mafl1x.mxkits.handler;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.events.InventoryKitOpenEvent;
import me.mafl1x.mxkits.kits.Kit;
import me.mafl1x.mxkits.menu.MainMenu;
import me.mafl1x.mxkits.menu.menuSkelet.EditKit;
import me.mafl1x.mxkits.menu.menuSkelet.IEditKit;
import me.mafl1x.mxkits.menu.menuSkelet.IPreview;
import me.mafl1x.mxkits.menu.menuSkelet.Preview;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Handler implements Listener {

    private final Map<UUID, MainMenu> openMenus = new HashMap<>();

    @EventHandler
    public void onInventoryKitOpen(InventoryKitOpenEvent e) {
        openMenus.put(e.getPlayer().getUniqueId(), e.getMenu());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        MainMenu mainMenu = openMenus.get(uuid);

        if (mainMenu != null && mainMenu instanceof IEditKit
                && isSameInventory(e.getInventory(), mainMenu.getInventory())) {
            EditKit editKit = (EditKit) mainMenu;
            Kit kit = MXKits.getInstance().getKitManager().getKit(editKit.getKitName());
            if (kit != null) {
                kit.getItems().clear();
                for (int i = 0; i < e.getInventory().getSize(); i++) {
                    ItemStack item = e.getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        kit.getItems().add(item);
                    }
                }
            }
        }

        openMenus.remove(uuid);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        MainMenu mainMenu = openMenus.get(uuid);
        if (mainMenu == null) return;

        if (!isSameInventory(mainMenu.getInventory(), e.getInventory())) return;

        if (!mainMenu.isModify()) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();

            if (mainMenu instanceof IPreview) {
                Preview preview = (Preview) mainMenu;
                int slot = e.getSlot();

                if (preview.buyKit().getSlot().contains(slot)) {
                    MXKits.getInstance().getKitManager().buyKit(player, preview.getKitName());
                }
                if (preview.takeItKit().getSlot().contains(slot)) {
                    MXKits.getInstance().getKitManager().takeItKit(player, preview.getKitName());
                }
            }
        }
    }

    private boolean isSameInventory(Inventory inv1, Inventory inv2) {
        return inv1.getHolder() == inv2.getHolder();
    }
}

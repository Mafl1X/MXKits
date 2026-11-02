package me.mafl1x.mxkits.handler;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.events.InventoryKitOpenEvent;
import me.mafl1x.mxkits.kits.Kit;
import me.mafl1x.mxkits.menu.MainMenu;
import me.mafl1x.mxkits.menu.menuSkelet.EditKit;
import me.mafl1x.mxkits.menu.menuSkelet.IEditKit;
import me.mafl1x.mxkits.menu.menuSkelet.IPreview;
import me.mafl1x.mxkits.menu.menuSkelet.Preview;
import me.mafl1x.mxkits.menu.item.ItemMenu;
import me.mafl1x.mxkits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Handler implements Listener {

    private final Map<UUID, MainMenu> openMenus = new HashMap<>();
    private final Map<UUID, EditSession> editSessions = new HashMap<>();

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
                Set<Integer> reservedSlots = getReservedEditSlots();
                Map<Integer, ItemStack> itemsBySlot = new HashMap<>();
                for (int i = 0; i < e.getInventory().getSize(); i++) {
                    if (reservedSlots.contains(i)) continue;
                    ItemStack item = e.getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        itemsBySlot.put(i, item);
                    }
                }
                kit.setItemsBySlot(itemsBySlot);
                MXKits.getInstance().getKitManager().saveKit(kit);
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

        Player player = (Player) e.getWhoClicked();

        if (mainMenu instanceof IPreview) {
            e.setCancelled(true);
            Preview preview = (Preview) mainMenu;
            int slot = e.getSlot();

            if (preview.buyKit().getSlot().contains(slot)) {
                MXKits.getInstance().getKitManager().buyKit(player, preview.getKitName());
            }
            if (preview.takeItKit().getSlot().contains(slot)) {
                MXKits.getInstance().getKitManager().takeItKit(player, preview.getKitName());
            }
        }

        if (mainMenu instanceof IEditKit) {
            EditKit editKit = (EditKit) mainMenu;
            int slot = e.getSlot();

            if (editKit.hasEditItems()) {
                ItemMenu pointsItem = editKit.editPointsItem();
                ItemMenu delayItem = editKit.editDelayItem();
                ItemMenu permissionItem = editKit.editPermissionItem();

                if (pointsItem.getSlot().contains(slot)) {
                    e.setCancelled(true);
                    beginEdit(player, editKit.getKitName(), EditType.POINTS);
                    return;
                }
                if (delayItem.getSlot().contains(slot)) {
                    e.setCancelled(true);
                    beginEdit(player, editKit.getKitName(), EditType.DELAY);
                    return;
                }
                if (permissionItem.getSlot().contains(slot)) {
                    e.setCancelled(true);
                    beginEdit(player, editKit.getKitName(), EditType.PERMISSION);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        EditSession session = editSessions.remove(uuid);
        if (session == null) return;

        e.setCancelled(true);
        String msg = e.getMessage().trim();
        if (msg.equalsIgnoreCase("cancel")) {
            e.getPlayer().sendMessage(Utils.message("messages.editCancelled"));
            return;
        }

        Kit kit = MXKits.getInstance().getKitManager().getKit(session.kitName);
        if (kit == null) {
            e.getPlayer().sendMessage(Utils.message("messages.kitNotFound"));
            return;
        }

        switch (session.type) {
            case POINTS:
                Integer points = parseInt(msg);
                if (points == null || points < 0) {
                    e.getPlayer().sendMessage(Utils.message("messages.editInvalidNumber"));
                    return;
                }
                kit.setPoint(points);
                break;
            case DELAY:
                Double seconds = parseDouble(msg);
                if (seconds == null || seconds < 0) {
                    e.getPlayer().sendMessage(Utils.message("messages.editInvalidNumber"));
                    return;
                }
                kit.setDelayCooldown(seconds * 1000.0);
                break;
            case PERMISSION:
                if (msg.equalsIgnoreCase("none") || msg.equalsIgnoreCase("clear")) {
                    kit.setPermission("");
                } else {
                    kit.setPermission(msg);
                }
                break;
            default:
                return;
        }

        MXKits.getInstance().getKitManager().saveKit(kit);
        e.getPlayer().sendMessage(Utils.message("messages.editSaved"));

        Bukkit.getScheduler().runTask(MXKits.getInstance(), () -> {
            EditKit editKit = new EditKit(kit.getName());
            e.getPlayer().openInventory(editKit.getInventory());
            Bukkit.getPluginManager().callEvent(new InventoryKitOpenEvent(e.getPlayer(), kit, editKit));
        });
    }

    private boolean isSameInventory(Inventory inv1, Inventory inv2) {
        return inv1.getHolder() == inv2.getHolder();
    }

    private void beginEdit(Player player, String kitName, EditType type) {
        editSessions.put(player.getUniqueId(), new EditSession(kitName, type));
        player.closeInventory();
        switch (type) {
            case POINTS:
                player.sendMessage(Utils.message("messages.editPromptPoints"));
                break;
            case DELAY:
                player.sendMessage(Utils.message("messages.editPromptDelay"));
                break;
            case PERMISSION:
                player.sendMessage(Utils.message("messages.editPromptPermission"));
                break;
        }
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Set<Integer> getReservedEditSlots() {
        Set<Integer> reserved = new HashSet<>();
        ConfigurationSection other = MXKits.getInstance().getConfig().getConfigurationSection("menuEdit.other");
        if (other != null) {
            for (String key : other.getKeys(false)) {
                reserved.addAll(MXKits.getInstance().getConfig().getIntegerList("menuEdit.other." + key + ".slots"));
            }
        }
        ConfigurationSection edit = MXKits.getInstance().getConfig().getConfigurationSection("menuEdit.edit");
        if (edit != null) {
            for (String key : edit.getKeys(false)) {
                reserved.addAll(MXKits.getInstance().getConfig().getIntegerList("menuEdit.edit." + key + ".slots"));
            }
        }
        return reserved;
    }

    private enum EditType {
        POINTS,
        DELAY,
        PERMISSION
    }

    private static class EditSession {
        private final String kitName;
        private final EditType type;

        private EditSession(String kitName, EditType type) {
            this.kitName = kitName;
            this.type = type;
        }
    }
}

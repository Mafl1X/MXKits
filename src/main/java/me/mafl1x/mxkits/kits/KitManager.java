package me.mafl1x.mxkits.kits;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {

    private final List<Kit> kits = new ArrayList<>();

    public KitManager() {
        createTables();
        loadKitsFromDatabase();
    }

    public void onDisable() {
    }

    public List<Kit> getKits() {
        return kits;
    }

    public boolean buyKit(Player player, String kitName) {
        if (!isKitCreated(kitName)) return false;

        Kit kit = getKit(kitName);
        if (MXKits.getInstance().getPlayerPointsAPI().look(player.getUniqueId()) < kit.getPoint()) {
            player.sendMessage(Utils.message("messages.pointsNotFound"));
            return false;
        }

        MXKits.getInstance().getPlayerPointsAPI().take(player.getUniqueId(), kit.getPoint());
        giveItems(player, kit);
        player.sendMessage(Utils.message("messages.successfullyBuyKit"));
        player.updateInventory();
        return true;
    }

    public void giveKit(Player player, String kitName) {
        Kit kit = getKit(kitName);
        if (kit == null) return;
        giveItems(player, kit);
        player.updateInventory();
    }

    public boolean createKit(Player player, String kitName) {
        if (isKitCreated(kitName)) return false;

        Map<Integer, ItemStack> itemsBySlot = new HashMap<>();
        PlayerInventory inv = player.getInventory();
        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            itemsBySlot.put(i, item.clone());
        }

        double delay = MXKits.getInstance().getConfig().getDouble("kitDefaults.delay", 0);
        int points = MXKits.getInstance().getConfig().getInt("kitDefaults.points", 0);
        String permission = MXKits.getInstance().getConfig().getString("kitDefaults.permission", "");

        Kit kit = new Kit(kitName, itemsBySlot, delay, points, permission);
        kits.add(kit);
        saveKit(kit);
        return true;
    }

    public boolean takeItKit(Player player, String kitName) {
        if (!isKitCreated(kitName)) return false;

        Kit kit = getKit(kitName);

        if (!hasPermission(player, kit.getPermission())) {
            player.sendMessage(Utils.message("messages.noHavePermissionTakeItKits"));
            return false;
        }

        if (!player.hasPermission("mxkits.bypasscooldown")) {
            if (!MXKits.getInstance().getKitsApi().isCooldown(player.getUniqueId(), kitName)) {
                long remaining = (long) MXKits.getInstance().getKitsApi().getRemainingTimeUntilExpiration(player.getUniqueId(), kitName);
                player.sendMessage(Utils.message("messages.hasCooldown")
                        .replace("%cooldown", Utils.convertSecondsToDHMS((int) remaining)));
                return false;
            }
            MXKits.getInstance().getKitsApi().setCooldown(player.getUniqueId(), kitName);
        }
        giveItems(player, kit);
        player.sendMessage(Utils.message("messages.successfullyTakeItKit"));
        player.updateInventory();
        return true;
    }

    private void giveItems(Player player, Kit kit) {
        PlayerInventory inv = player.getInventory();
        for (Map.Entry<Integer, ItemStack> entry : kit.getItemsBySlot().entrySet()) {
            ItemStack item = entry.getValue();
            if (item == null) continue;
            int slot = entry.getKey();
            ItemStack toGive = item.clone();
            if (slot >= 0 && slot < inv.getSize()) {
                ItemStack current = inv.getItem(slot);
                if (current == null || current.getType() == Material.AIR) {
                    inv.setItem(slot, toGive);
                } else {
                    Map<Integer, ItemStack> leftover = inv.addItem(toGive);
                    for (ItemStack left : leftover.values()) {
                        player.getWorld().dropItem(player.getLocation(), left);
                    }
                }
            } else {
                Map<Integer, ItemStack> leftover = inv.addItem(toGive);
                for (ItemStack left : leftover.values()) {
                    player.getWorld().dropItem(player.getLocation(), left);
                }
            }
        }
    }

    private boolean hasPermission(Player player, String permission) {
        if (permission == null || permission.trim().isEmpty()) return true;
        return player.hasPermission(permission.trim());
    }

    public boolean isKitCreated(String kitName) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(kitName)) return true;
        }
        return false;
    }

    public Kit getKit(String kitName) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(kitName)) return kit;
        }
        return null;
    }

    public boolean deleteKit(String kitName) {
        Kit kit = getKit(kitName);
        if (kit == null) return false;
        kits.remove(kit);
        MXKits.getInstance().getDatabase().update(
                "DELETE FROM kit_items WHERE kit=?",
                kit.getName()
        );
        MXKits.getInstance().getDatabase().update(
                "DELETE FROM kits WHERE name=?",
                kit.getName()
        );
        MXKits.getInstance().getDatabase().update(
                "DELETE FROM kitsapi WHERE kit=?",
                kit.getName()
        );
        return true;
    }

    public void saveKit(Kit kit) {
        if (kit == null) return;
        if (kitExistsInDatabase(kit.getName())) {
            MXKits.getInstance().getDatabase().update(
                    "UPDATE kits SET delay=?, points=?, groups=? WHERE name=?",
                    kit.getDelayCooldown(), kit.getPoint(), kit.getPermission(), kit.getName()
            );
        } else {
            MXKits.getInstance().getDatabase().update(
                    "INSERT INTO kits (name, delay, points, groups) VALUES (?,?,?,?)",
                    kit.getName(), kit.getDelayCooldown(), kit.getPoint(), kit.getPermission()
            );
        }

        MXKits.getInstance().getDatabase().update(
                "DELETE FROM kit_items WHERE kit=?",
                kit.getName()
        );
        for (Map.Entry<Integer, ItemStack> entry : kit.getItemsBySlot().entrySet()) {
            ItemStack item = entry.getValue();
            if (item == null) continue;
            MXKits.getInstance().getDatabase().update(
                    "INSERT INTO kit_items (kit, slot, item) VALUES (?,?,?)",
                    kit.getName(), entry.getKey(), Utils.itemToBase64(item)
            );
        }
    }

    private void createTables() {
        MXKits.getInstance().getDatabase().update(
                "CREATE TABLE IF NOT EXISTS kits (name VARCHAR(64) PRIMARY KEY, delay DOUBLE, points INT, groups TEXT)"
        );
        MXKits.getInstance().getDatabase().update(
                "CREATE TABLE IF NOT EXISTS kit_items (kit VARCHAR(64), slot INT, item TEXT)"
        );
    }

    private void loadKitsFromDatabase() {
        try (ResultSet rs = MXKits.getInstance().getDatabase().query(
                "SELECT name, delay, points, groups FROM kits")) {
            if (rs == null) return;
            while (rs.next()) {
                String kitName = rs.getString("name");
                double delay = rs.getDouble("delay");
                int points = rs.getInt("points");
                String permission = rs.getString("groups");

                Map<Integer, ItemStack> itemsBySlot = loadItemsForKit(kitName);
                Kit kit = new Kit(kitName, itemsBySlot, delay, points, permission);
                kits.add(kit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, ItemStack> loadItemsForKit(String kitName) {
        Map<Integer, ItemStack> items = new HashMap<>();
        try (ResultSet rs = MXKits.getInstance().getDatabase().query(
                "SELECT slot, item FROM kit_items WHERE kit=?",
                kitName)) {
            if (rs == null) return items;
            while (rs.next()) {
                int slot = rs.getInt("slot");
                String data = rs.getString("item");
                ItemStack item = Utils.itemFromBase64(data);
                if (item != null) items.put(slot, item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private boolean kitExistsInDatabase(String kitName) {
        try (ResultSet rs = MXKits.getInstance().getDatabase().query(
                "SELECT count(*) AS count FROM kits WHERE name=?",
                kitName)) {
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

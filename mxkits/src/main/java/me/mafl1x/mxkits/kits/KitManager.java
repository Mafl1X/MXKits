package me.mafl1x.mxkits.kits;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.api.vault.VaultAPI;
import me.mafl1x.mxkits.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitManager {

    private final List<Kit> kits = new ArrayList<>();

    public KitManager() {
        if (MXKits.getInstance().getConfig().getConfigurationSection("kits") == null) return;

        for (String kitName : MXKits.getInstance().getConfig().getConfigurationSection("kits").getKeys(false)) {
            double delay = MXKits.getInstance().getConfig().getDouble("kits." + kitName + ".delay");
            int points = MXKits.getInstance().getConfig().getInt("kits." + kitName + ".points");

            Object itemsObj = MXKits.getInstance().getConfig().get("kits." + kitName + ".items");
            List<ItemStack> itemStack;
            if (itemsObj instanceof List) {
                itemStack = (List<ItemStack>) itemsObj;
            } else {
                itemStack = new ArrayList<>();
            }

            String groupsStr = MXKits.getInstance().getConfig().getString("kits." + kitName + ".groups", "");
            String[] groups = groupsStr.isEmpty() ? new String[0] : groupsStr.split(";");

            Kit kit = new Kit(kitName, itemStack, delay, points, groups);
            kits.add(kit);
        }
    }

    public void onDisable() {
        for (Kit kit : kits) {
            String base = "kits." + kit.getName();
            MXKits.getInstance().getConfig().set(base + ".delay", kit.getDelayCooldown());
            MXKits.getInstance().getConfig().set(base + ".points", kit.getPoint());
            MXKits.getInstance().getConfig().set(base + ".groups", String.join(";", kit.getGroups()));
            MXKits.getInstance().getConfig().set(base + ".items", kit.getItems());
        }
        MXKits.getInstance().saveConfig();
    }

    public List<Kit> getKits() {
        return kits;
    }

    public boolean buyKit(Player player, String kitName) {
        if (!isKitCreated(kitName)) return false;

        Kit kit = getKit(kitName);
        if (MXKits.getInstance().getPlayerPointsAPI().look(player.getUniqueId()) < kit.getPoint()) {
            player.sendMessage(Utils.color(MXKits.getInstance().getConfig().getString("messages.pointsNotFound")));
            return false;
        }

        MXKits.getInstance().getPlayerPointsAPI().take(player.getUniqueId(), kit.getPoint());
        giveItems(player, kit);
        player.sendMessage(Utils.color(MXKits.getInstance().getConfig().getString("messages.successfullyBuyKit")));
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

        List<ItemStack> items = new ArrayList<>();
        addInventoryItems(items, player.getInventory().getContents());
        addInventoryItems(items, player.getInventory().getArmorContents());
        addInventoryItems(items, player.getInventory().getExtraContents());

        double delay = MXKits.getInstance().getConfig().getDouble("kitDefaults.delay", 0);
        int points = MXKits.getInstance().getConfig().getInt("kitDefaults.points", 0);
        String groupsStr = MXKits.getInstance().getConfig().getString("kitDefaults.groups", "");
        String[] groups = groupsStr.isEmpty() ? new String[0] : groupsStr.split(";");

        Kit kit = new Kit(kitName, items, delay, points, groups);
        kits.add(kit);

        String base = "kits." + kitName;
        MXKits.getInstance().getConfig().set(base + ".delay", delay);
        MXKits.getInstance().getConfig().set(base + ".points", points);
        MXKits.getInstance().getConfig().set(base + ".groups", groupsStr);
        MXKits.getInstance().getConfig().set(base + ".items", items);
        MXKits.getInstance().saveConfig();
        return true;
    }

    public boolean takeItKit(Player player, String kitName) {
        if (!isKitCreated(kitName)) return false;

        Kit kit = getKit(kitName);

        if (!hasGroup(player, Arrays.asList(kit.getGroups()))) {
            player.sendMessage(Utils.color(MXKits.getInstance().getConfig().getString("messages.noHavePermissionTakeItKits")));
            return false;
        }

        if (!MXKits.getInstance().getKitsApi().isCooldown(player.getUniqueId(), kitName)) {
            long remaining = (long) MXKits.getInstance().getKitsApi().getRemainingTimeUntilExpiration(player.getUniqueId(), kitName);
            player.sendMessage(Utils.color(MXKits.getInstance().getConfig()
                    .getString("messages.hasCooldown")
                    .replace("%cooldown", Utils.convertSecondsToDHMS((int) remaining))));
            return false;
        }

        MXKits.getInstance().getKitsApi().setCooldown(player.getUniqueId(), kitName);
        giveItems(player, kit);
        player.sendMessage(Utils.color(MXKits.getInstance().getConfig().getString("messages.successfullyTakeItKit")));
        player.updateInventory();
        return true;
    }

    private void giveItems(Player player, Kit kit) {
        for (ItemStack item : kit.getItems()) {
            if (item == null) continue;
            if (isInventoryFull(player)) {
                player.getWorld().dropItem(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }
        }
    }

    private boolean hasGroup(Player player, List<String> groups) {
        for (String group : groups) {
            if (VaultAPI.hasGroup(group, player)) return true;
        }
        return false;
    }

    private boolean isInventoryFull(Player player) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) return false;
        }
        return true;
    }

    private void addInventoryItems(List<ItemStack> target, ItemStack[] items) {
        if (items == null) return;
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) continue;
            target.add(item.clone());
        }
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
}

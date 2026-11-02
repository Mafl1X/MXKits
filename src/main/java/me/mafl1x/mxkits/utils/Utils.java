package me.mafl1x.mxkits.utils;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.menu.item.ItemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Base64;
import java.util.stream.Collectors;

public class Utils {

    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String message(String path) {
        String prefix = color(MXKits.getInstance().getConfig().getString("messages.prefix", ""));
        String msg = MXKits.getInstance().getConfig().getString(path, "");
        msg = msg.replace("%prefix%", prefix);
        return color(msg);
    }

    public static String convertSecondsToDHMS(int seconds) {
        if (seconds <= 0) return "0s";
        int day = seconds / 86400;
        int hour = (seconds % 86400) / 3600;
        int minute = (seconds % 3600) / 60;
        int second = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (day > 0) sb.append(day).append("d ");
        if (hour > 0) sb.append(hour).append("h ");
        if (minute > 0) sb.append(minute).append("m ");
        if (second > 0 || sb.length() == 0) sb.append(second).append("s");
        return sb.toString().trim();
    }

    public static ItemMenu getItemInMenu(String path) {
        String materialName = MXKits.getInstance().getConfig().getString(path + ".material", "STONE");
        Material m = Material.matchMaterial(materialName);
        if (m == null) m = Material.STONE;

        String name = color(MXKits.getInstance().getConfig().getString(path + ".name", ""));
        List<String> lore = MXKits.getInstance().getConfig().getStringList(path + ".lore")
                .stream().map(Utils::color).collect(Collectors.toList());
        List<Integer> slots = MXKits.getInstance().getConfig().getIntegerList(path + ".slots");

        ItemStack itemStack = new ItemStack(m);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }

        return new ItemMenu(slots, itemStack);
    }

    public static String itemToBase64(ItemStack item) {
        if (item == null) return "";
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ItemStack itemFromBase64(String data) {
        if (data == null || data.isEmpty()) return null;
        byte[] bytes = Base64.getDecoder().decode(data);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

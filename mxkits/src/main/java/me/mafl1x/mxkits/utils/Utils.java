package me.mafl1x.mxkits.utils;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.menu.item.ItemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
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
}

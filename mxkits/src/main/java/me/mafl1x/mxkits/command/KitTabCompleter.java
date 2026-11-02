package me.mafl1x.mxkits.command;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class KitTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> sub = new ArrayList<>();
            sub.add("preview");
            if (sender.hasPermission("mxkits.modify")) sub.add("editkit");
            if (sender.hasPermission("mxkits.give")) sub.add("give");
            if (sender.hasPermission("mxkits.create")) sub.add("create");
            return filter(sub, args[0]);
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("preview") || sub.equals("editkit")) {
                return filter(getKitNames(), args[1]);
            }
            if (sub.equals("give")) {
                return filter(getOnlinePlayerNames(), args[1]);
            }
            return new ArrayList<>();
        }

        if (args.length == 3) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("give")) {
                return filter(getKitNames(), args[2]);
            }
        }

        return new ArrayList<>();
    }

    private List<String> getKitNames() {
        return MXKits.getInstance().getKitManager().getKits().stream()
                .map(Kit::getName)
                .collect(Collectors.toList());
    }

    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> filter(List<String> values, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return values.stream()
                .filter(v -> v.toLowerCase(Locale.ROOT).startsWith(lower))
                .collect(Collectors.toList());
    }
}

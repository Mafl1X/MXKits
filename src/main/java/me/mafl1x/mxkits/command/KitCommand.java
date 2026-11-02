package me.mafl1x.mxkits.command;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.events.InventoryKitOpenEvent;
import me.mafl1x.mxkits.kits.Kit;
import me.mafl1x.mxkits.menu.menuSkelet.EditKit;
import me.mafl1x.mxkits.menu.menuSkelet.Preview;
import me.mafl1x.mxkits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Utils.message("messages.help"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "preview": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.message("messages.onlyPlayers"));
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(Utils.message("messages.help"));
                    return true;
                }
                Player player = (Player) sender;
                String kitName = args[1];
                if (!MXKits.getInstance().getKitManager().isKitCreated(kitName)) {
                    player.sendMessage(Utils.message("messages.kitNotFound"));
                    return true;
                }
                Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
                Preview preview = new Preview(kitName);
                player.openInventory(preview.getInventory());
                player.sendMessage(Utils.message("messages.successfullyPreviewOpen"));
                Bukkit.getPluginManager().callEvent(new InventoryKitOpenEvent(player, kit, preview));
                return true;
            }

            case "give": {
                if (!sender.hasPermission("mxkits.give")) {
                    sender.sendMessage(Utils.message("messages.noPermission"));
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(Utils.message("messages.help"));
                    return true;
                }
                String kitName = args[2];
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Utils.message("messages.playerNotFound"));
                    return true;
                }
                if (!MXKits.getInstance().getKitManager().isKitCreated(kitName)) {
                    sender.sendMessage(Utils.message("messages.kitNotFound"));
                    return true;
                }
                MXKits.getInstance().getKitManager().giveKit(target, kitName);
                return true;
            }

            case "create": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.message("messages.onlyPlayers"));
                    return true;
                }
                Player player = (Player) sender;
                if (!player.hasPermission("mxkits.create")) {
                    player.sendMessage(Utils.message("messages.noPermission"));
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage(Utils.message("messages.help"));
                    return true;
                }
                String kitName = args[1];
                if (MXKits.getInstance().getKitManager().isKitCreated(kitName)) {
                    player.sendMessage(Utils.message("messages.kitAlreadyExists"));
                    return true;
                }
                boolean created = MXKits.getInstance().getKitManager().createKit(player, kitName);
                if (created) {
                    player.sendMessage(Utils.message("messages.kitCreated"));
                } else {
                    player.sendMessage(Utils.message("messages.kitNotCreated"));
                }
                return true;
            }

            case "editkit": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.message("messages.onlyPlayers"));
                    return true;
                }
                Player player = (Player) sender;
                if (!player.hasPermission("mxkits.modify")) {
                    player.sendMessage(Utils.message("messages.noPermission"));
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(Utils.message("messages.help"));
                    return true;
                }
                String kitName = args[1];
                if (!MXKits.getInstance().getKitManager().isKitCreated(kitName)) {
                    player.sendMessage(Utils.message("messages.kitNotFound"));
                    return true;
                }
                Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
                EditKit editKit = new EditKit(kitName);
                player.openInventory(editKit.getInventory());
                player.sendMessage(Utils.message("messages.successfullyPreviewOpen"));
                Bukkit.getPluginManager().callEvent(new InventoryKitOpenEvent(player, kit, editKit));
                return true;
            }

            case "delete": {
                if (!sender.hasPermission("mxkits.delete")) {
                    sender.sendMessage(Utils.message("messages.noPermission"));
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(Utils.message("messages.help"));
                    return true;
                }
                String kitName = args[1];
                boolean deleted = MXKits.getInstance().getKitManager().deleteKit(kitName);
                if (deleted) {
                    sender.sendMessage(Utils.message("messages.kitDeleted"));
                } else {
                    sender.sendMessage(Utils.message("messages.kitNotFound"));
                }
                return true;
            }

            default:
                sender.sendMessage(Utils.message("messages.help"));
                return true;
        }
    }
}

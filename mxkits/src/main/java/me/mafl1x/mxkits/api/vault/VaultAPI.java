package me.mafl1x.mxkits.api.vault;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;

public class VaultAPI {

    private static Chat chat;

    public static boolean hasGroup(String group, Player player) {
        if (chat == null) return false;
        String[] groups = chat.getPlayerGroups(player);
        return groups != null && Arrays.asList(groups).contains(group);
    }

    public static boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return false;
        chat = rsp.getProvider();
        return chat != null;
    }
}

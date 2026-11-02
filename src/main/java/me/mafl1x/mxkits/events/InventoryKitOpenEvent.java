package me.mafl1x.mxkits.events;

import me.mafl1x.mxkits.kits.Kit;
import me.mafl1x.mxkits.menu.MainMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InventoryKitOpenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Kit kit;
    private final MainMenu menu;

    public InventoryKitOpenEvent(Player player, Kit kit, MainMenu menu) {
        this.player = player;
        this.kit = kit;
        this.menu = menu;
    }

    public Player getPlayer() { return player; }
    public Kit getKit() { return kit; }
    public MainMenu getMenu() { return menu; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}

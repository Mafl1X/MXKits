package me.mafl1x.mxkits;

import me.mafl1x.mxkits.api.kits.KitsApi;
import me.mafl1x.mxkits.api.vault.VaultAPI;
import me.mafl1x.mxkits.command.KitCommand;
import me.mafl1x.mxkits.command.KitTabCompleter;
import me.mafl1x.mxkits.database.DatabaseFactory;
import me.mafl1x.mxkits.database.IDatabase;
import me.mafl1x.mxkits.handler.Handler;
import me.mafl1x.mxkits.kits.KitManager;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MXKits extends JavaPlugin {

    private static MXKits instance;

    private KitManager kitManager;
    private IDatabase database;
    private KitsApi kitsApi;
    private PlayerPoints playerPoints;
    private PlayerPointsAPI playerPointsAPI;

    public static MXKits getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // PlayerPoints check
        if (!Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            getLogger().severe("PlayerPoints not found! Please install it.");
            getLogger().severe("https://www.spigotmc.org/resources/playerpoints.80745/");
            setEnabled(false);
            return;
        }
        playerPoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
        playerPointsAPI = playerPoints.getAPI();

        // Vault check
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().severe("Vault not found! Please install it.");
            getLogger().severe("https://dev.bukkit.org/projects/vault/files/2615750");
            setEnabled(false);
            return;
        }
        VaultAPI.setupChat();

        // Database
        database = DatabaseFactory.create();

        // Kit manager
        kitManager = new KitManager();

        // KitsApi (cooldowns)
        kitsApi = new KitsApi();
        kitsApi.createTables();

        // Commands
        getCommand("mxkit").setExecutor(new KitCommand());
        getCommand("mxkit").setTabCompleter(new KitTabCompleter());

        // Events
        Bukkit.getPluginManager().registerEvents(new Handler(), this);

        getLogger().info("MXKits enabled! Author: mafl1x");
    }

    @Override
    public void onDisable() {
        if (kitManager != null) kitManager.onDisable();
        if (database != null) database.close();
        getLogger().info("MXKits disabled.");
    }

    public KitManager getKitManager() { return kitManager; }
    public IDatabase getDatabase() { return database; }
    public KitsApi getKitsApi() { return kitsApi; }
    public PlayerPointsAPI getPlayerPointsAPI() { return playerPointsAPI; }
}

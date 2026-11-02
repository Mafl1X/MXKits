package me.mafl1x.mxkits.api.kits;

import java.util.UUID;

public interface IKitsApi {
    boolean isCooldown(UUID uuid, String kitName);
    double getCooldown(UUID uuid, String kitName);
    boolean isPlayerExists(UUID uuid, String kitName);
    void setCooldown(UUID uuid, String kitName);
    double getRemainingTimeUntilExpiration(UUID uuid, String kitName);
    void createTables();
}

package me.mafl1x.mxkits.api.kits;

import me.mafl1x.mxkits.MXKits;
import me.mafl1x.mxkits.database.IDatabase;
import me.mafl1x.mxkits.kits.Kit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

public class KitsApi implements IKitsApi {

    private IDatabase database() {
        return MXKits.getInstance().getDatabase();
    }

    @Override
    public boolean isCooldown(UUID uuid, String kitName) {
        if (!isPlayerExists(uuid, kitName)) return true;

        double startTime = getCooldown(uuid, kitName);
        double elapsed = System.currentTimeMillis() - startTime;
        Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
        return elapsed >= kit.getDelayCooldown();
    }

    @Override
    public void setCooldown(UUID uuid, String kitName) {
        double now = System.currentTimeMillis();
        if (!isPlayerExists(uuid, kitName)) {
            database().update(
                    "INSERT INTO kitsapi (player, kit, cooldown) VALUES (?,?,?)",
                    uuid.toString(), kitName, String.valueOf(now)
            );
        } else {
            database().update(
                    "UPDATE kitsapi SET cooldown=? WHERE player=? AND kit=?",
                    String.valueOf(now), uuid.toString(), kitName
            );
        }
    }

    @Override
    public double getCooldown(UUID uuid, String kitName) {
        try (ResultSet rs = database().query(
                "SELECT cooldown FROM kitsapi WHERE player=? AND kit=?",
                uuid.toString(), kitName)) {
            if (rs != null && rs.next()) {
                return Double.parseDouble(rs.getString("cooldown"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public boolean isPlayerExists(UUID uuid, String kitName) {
        try (ResultSet rs = database().query(
                "SELECT count(*) AS count FROM kitsapi WHERE player=? AND kit=?",
                uuid.toString(), kitName)) {
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public double getRemainingTimeUntilExpiration(UUID uuid, String kitName) {
        Kit kit = MXKits.getInstance().getKitManager().getKit(kitName);
        double startTime = getCooldown(uuid, kitName);
        if (startTime == 0.0) return 0.0;

        double elapsed = System.currentTimeMillis() - startTime;
        double remaining = kit.getDelayCooldown() - elapsed;
        return Duration.ofMillis((long) remaining).getSeconds();
    }

    @Override
    public void createTables() {
        database().update(
                "CREATE TABLE IF NOT EXISTS kitsapi (player VARCHAR(255), kit VARCHAR(255), cooldown VARCHAR(255))"
        );
    }
}

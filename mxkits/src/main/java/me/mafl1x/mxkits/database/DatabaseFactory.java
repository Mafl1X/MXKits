package me.mafl1x.mxkits.database;

import me.mafl1x.mxkits.MXKits;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseFactory {

    public static IDatabase create() {
        FileConfiguration config = MXKits.getInstance().getConfig();
        String type = config.getString("database.type", "sqlite").toLowerCase();

        if (type.equals("mysql")) {
            String host = config.getString("database.host", "127.0.0.1");
            int port = config.getInt("database.port", 3306);
            String database = config.getString("database.database", "mxkits");
            String user = config.getString("database.user", "root");
            String password = config.getString("database.password", "password");
            int poolSize = config.getInt("database.pool-size", 10);
            long connectionTimeout = config.getLong("database.connection-timeout", 30000);
            MXKits.getInstance().getLogger().info("Using MySQL database.");
            return new MySQLDatabase(host, port, database, user, password, poolSize, connectionTimeout);
        } else {
            MXKits.getInstance().getLogger().info("Using SQLite database.");
            return new SQLiteDatabase();
        }
    }
}

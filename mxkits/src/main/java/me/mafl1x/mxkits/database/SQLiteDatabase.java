package me.mafl1x.mxkits.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.mafl1x.mxkits.MXKits;

import java.io.File;

public class SQLiteDatabase extends AbstractDatabase {

    public SQLiteDatabase() {
        File dataFolder = MXKits.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        String path = new File(dataFolder, "data.db").getAbsolutePath();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1); // SQLite doesn't support concurrent writes
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("MXKits-SQLite");
        this.dataSource = new HikariDataSource(config);
    }
}

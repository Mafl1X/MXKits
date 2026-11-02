package me.mafl1x.mxkits.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDatabase {
    void update(String query, Object... params);
    ResultSet query(String query, Object... params);
    Connection getConnection() throws SQLException;
    void close();
}

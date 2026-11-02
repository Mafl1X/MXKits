package me.mafl1x.mxkits.database;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public abstract class AbstractDatabase implements IDatabase {

    protected HikariDataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void update(String query, Object... params) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            setParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet query(String query, Object... params) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                CachedRowSet cached = RowSetProvider.newFactory().createCachedRowSet();
                cached.populate(rs);
                return cached;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void setParams(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object obj = params[i];
            if (obj instanceof String) {
                ps.setString(i + 1, (String) obj);
            } else if (obj instanceof Integer) {
                ps.setInt(i + 1, (Integer) obj);
            } else if (obj instanceof Long) {
                ps.setLong(i + 1, (Long) obj);
            } else if (obj instanceof Double) {
                ps.setDouble(i + 1, (Double) obj);
            } else if (obj instanceof Float) {
                ps.setFloat(i + 1, (Float) obj);
            } else if (obj instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) obj);
            } else if (obj instanceof java.sql.Timestamp) {
                ps.setTimestamp(i + 1, (java.sql.Timestamp) obj);
            } else if (obj instanceof java.sql.Date) {
                ps.setDate(i + 1, (java.sql.Date) obj);
            } else {
                ps.setObject(i + 1, obj);
            }
        }
    }
}

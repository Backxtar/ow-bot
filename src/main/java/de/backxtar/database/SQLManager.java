package de.backxtar.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Arrays;

public class SQLManager {
    private final HikariConfig config = new HikariConfig();
    private final HikariDataSource dataSource;

    public SQLManager(final String host,
                      final String database,
                      final String user,
                      final String passwd) {
        this.config.setDriverClassName("org.mariadb.jdbc.Driver");
        this.config.setJdbcUrl("jdbc:mariadb://" + host + ":3306/" + database);
        this.config.setUsername(user);
        this.config.setPassword(passwd);
        this.config.addDataSourceProperty( "cachePrepStmts" , "true" );
        this.config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        this.config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        this.dataSource = new HikariDataSource(this.config);
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public HikariConfig getConfig() {
        return config;
    }

    public void insertQuery(final String table, final String[] fields, final Object[] values) {
        StringBuilder stmtString = new StringBuilder(
                "INSERT INTO " + table + Arrays.toString(fields)
                        .replace("[", "(").replace("]", ")") + " VALUES (");
        for (Object ignored : values)
            stmtString.append("?,");
        stmtString = new StringBuilder(stmtString.substring(0, stmtString.length() - 1) + ")");

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(stmtString.toString());
            parseParam(false, values, stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void updateQuery(final String[] fields, final String table, final String condition, final Object[] values) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE " + table + " SET " + Arrays.toString(fields)
                    .replace("[", "")
                    .replace("]", " = ?")
                    .replace(",", "= ? ,") + " WHERE " + condition);
            parseParam(false, values, stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void deleteQuery(final String table, final String condition, final Object[] values) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + table + " WHERE " + condition);
            parseParam(false, values, stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public ResultSet selectQuery(final String[] fields, final String table, final String condition, final Object[] values) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT " + Arrays.toString(fields)
                    .replace("[", "")
                    .replace("]", "") + " FROM " + table + " WHERE " + condition);
            return parseParam(true, values, stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public ResultSet selectQuery(final String table, final String condition, final Object[] values) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + condition);
            return parseParam(true, values, stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public ResultSet selectQuery(final String table) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet resultSet = stmt.executeQuery();
            stmt.close();
            return resultSet;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    private ResultSet parseParam(boolean need_return, Object[] values, PreparedStatement stmt) throws SQLException {
        int i = 1;
        for (Object value : values) {
            switch (value.getClass().getName()) {
                case "java.lang.String" -> stmt.setString(i++, (String) value);
                case "java.lang.Integer" -> stmt.setInt(i++, (Integer) value);
                case "java.lang.Long" -> stmt.setLong(i++, (Long) value);
                case "java.lang.Boolean" -> stmt.setBoolean(i++, (Boolean) value);
                case "java.sql.Timestamp" -> stmt.setTimestamp(i++, (Timestamp) value);
            }
        }
        ResultSet resultSet = null;
        if (need_return) resultSet = stmt.executeQuery();
        else stmt.executeUpdate();
        stmt.close();
        return resultSet;
    }
}

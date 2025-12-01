package net.vertrauterdavid.homes.database;

import lombok.Getter;
import lombok.SneakyThrows;
import net.vertrauterdavid.homes.Homes;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.Properties;

@Getter
@SuppressWarnings("unused")
public class SqlConnection {

    private Connection connection;

    @SneakyThrows
    public void setup() {
        final FileConfiguration fileConfiguration = Homes.getInstance().getConfig();

        String url = String.format(
                "jdbc:mysql://%s:%s/%s?autoReconnect=true",
                fileConfiguration.getString("Sql.Host"),
                fileConfiguration.getString("Sql.Port"),
                fileConfiguration.getString("Sql.Database")
        );

        final Properties properties = new Properties();
        properties.put("user", fileConfiguration.getString("Sql.Username"));
        properties.put("password", fileConfiguration.getString("Sql.Password"));
        properties.put("autoReconnect", "true");

        connection = DriverManager.getConnection(url, properties);
    }

    @SneakyThrows
    public void createTables() {
        if (!isConnected()) return;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Homes (
                    UUID VARCHAR(100) NOT NULL,
                    Home1 VARCHAR(100) NOT NULL DEFAULT '-',
                    Home2 VARCHAR(100) NOT NULL DEFAULT '-',
                    Home3 VARCHAR(100) NOT NULL DEFAULT '-',
                    Home4 VARCHAR(100) NOT NULL DEFAULT '-',
                    Home5 VARCHAR(100) NOT NULL DEFAULT '-',
                    Home6 VARCHAR(100) NOT NULL DEFAULT '-',
                    Home7 VARCHAR(100) NOT NULL DEFAULT '-'
                );
            """);
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @SneakyThrows
    public void update(String query) {
        if (!isConnected()) return;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    @SneakyThrows
    public ResultSet getResult(String query) {
        return isConnected() ? connection.createStatement().executeQuery(query) : null;
    }

    @SneakyThrows
    public String get(String table, String value) {
        return get(table, value, "");
    }

    @SneakyThrows
    public String get(String table, String value, String where) {
        if (!isConnected()) return "";
        String sql = "SELECT " + value + " FROM " + table +
                (where == null || where.isBlank() ? "" : " WHERE " + where);

        try (ResultSet rs = getResult(sql)) {
            return (rs != null && rs.next()) ? rs.getString(value) : "";
        }
    }

    @SneakyThrows
    public void close() {
        if (isConnected()) connection.close();
    }
}

package sphere.plugin.lifestealSMP.database;

import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.sql.*;

public class MySQL implements SQL {

    private final LifestealSMP plugin;
    private Connection connection;

    public MySQL(LifestealSMP plugin) {
        this.plugin = plugin;
        this.connection = null;
    }

    @Override
    public void connect() {
        if (connection != null) {
            Bukkit.getLogger().warning("[LifestealSMP] MySQL connection already exists.");
            return;
        }

        String host = plugin.getConfigManager().getMysqlHost();
        int port = plugin.getConfigManager().getMysqlPort();
        String database = plugin.getConfigManager().getMysqlDatabase();
        String username = plugin.getConfigManager().getMysqlUsername();
        String password = plugin.getConfigManager().getMysqlPassword();

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true",
                    username,
                    password
            );
            Bukkit.getLogger().info("[LifestealSMP] MySQL connection established.");
        } catch (SQLException e) {
            connection = null;
            Bukkit.getLogger().severe("[LifestealSMP] Failed to connect to MySQL: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) {
            Bukkit.getLogger().warning("[LifestealSMP] No MySQL connection to disconnect.");
            return;
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("[LifestealSMP] MySQL connection closed.");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] Error while closing MySQL connection: " + e.getMessage());
        }
    }

    @Override
    public void createTables() {
        if (connection == null) {
            Bukkit.getLogger().severe("[LifestealSMP] Cannot create tables: connection is null.");
            return;
        }

        String sql = """
            CREATE TABLE IF NOT EXISTS player_hearts (
                uuid VARCHAR(36) PRIMARY KEY,
                hearts INT NOT NULL
            );
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] MySQL table 'player_hearts' ensured.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] Failed to create player_hearts table: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        throw new SQLException("MySQL connection is not established or already closed.");
    }

    @Override
    public int getHearts(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] getHearts called with null/empty UUID.");
            return plugin.getConfigManager().getStartingHearts();
        }

        if (connection == null) {
            Bukkit.getLogger().severe("[LifestealSMP] getHearts failed: connection is null.");
            return plugin.getConfigManager().getStartingHearts();
        }

        String query = "SELECT hearts FROM player_hearts WHERE uuid = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hearts");
                } else {
                    Bukkit.getLogger().info("[LifestealSMP] No record found for UUID: " + uuid);
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] Error retrieving hearts for UUID " + uuid + ": " + e.getMessage());
        }

        return plugin.getConfigManager().getStartingHearts();
    }

    @Override
    public void setHearts(String uuid, int hearts) {
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] setHearts called with null/empty UUID.");
            return;
        }

        if (connection == null) {
            Bukkit.getLogger().severe("[LifestealSMP] setHearts failed: connection is null.");
            return;
        }

        String sql = "REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, hearts);
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] Updated hearts for UUID " + uuid + " to " + hearts + ".");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] Failed to update hearts for UUID " + uuid + ": " + e.getMessage());
        }
    }
}

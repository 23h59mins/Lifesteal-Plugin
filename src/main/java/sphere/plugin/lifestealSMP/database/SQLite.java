package sphere.plugin.lifestealSMP.database;

import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.io.File;
import java.sql.*;

/**
 * SQLite implementation for interacting with the LifestealSMP plugin's database.
 * Handles connection lifecycle, schema setup, and player heart data storage.
 */
public class SQLite implements SQL {

    private final LifestealSMP plugin;
    private Connection connection;

    public SQLite(LifestealSMP plugin) {
        this.plugin = plugin;
        this.connection = null;
    }

    @Override
    public void connect() {
        if (connection != null) {
            Bukkit.getLogger().warning("[LifestealSMP] SQLite connection already established.");
            return;
        }

        try {
            File dbFile = new File(plugin.getDataFolder(), "hearts.db");
            if (!dbFile.exists()) {
                plugin.getDataFolder().mkdirs(); // ensure folder exists
                dbFile.createNewFile();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            Bukkit.getLogger().info("[LifestealSMP] SQLite connection established.");
        } catch (Exception e) {
            connection = null;
            Bukkit.getLogger().severe("[LifestealSMP] Failed to connect to SQLite: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) {
            Bukkit.getLogger().warning("[LifestealSMP] SQLite connection already null or not initialized.");
            return;
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("[LifestealSMP] SQLite connection closed.");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] Error closing SQLite connection: " + e.getMessage());
        }
    }

    @Override
    public void createTables() {
        if (connection == null) {
            Bukkit.getLogger().severe("[LifestealSMP] Cannot create SQLite tables: connection is null.");
            return;
        }

        String sql = """
            CREATE TABLE IF NOT EXISTS player_hearts (
                uuid TEXT PRIMARY KEY,
                hearts INTEGER NOT NULL
            );
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] SQLite table 'player_hearts' ensured.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite table creation failed: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        throw new SQLException("SQLite connection is not established or is closed.");
    }

    @Override
    public int getHearts(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            Bukkit.getLogger().warning("[LifestealSMP] getHearts called with null or empty UUID.");
            return plugin.getConfigManager().getStartingHearts();
        }

        if (connection == null) {
            Bukkit.getLogger().severe("[LifestealSMP] Cannot get hearts: SQLite connection is null.");
            return plugin.getConfigManager().getStartingHearts();
        }

        String query = "SELECT hearts FROM player_hearts WHERE uuid=?;";
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
            Bukkit.getLogger().severe("[LifestealSMP] SQLite getHearts failed for UUID " + uuid + ": " + e.getMessage());
        }

        return plugin.getConfigManager().getStartingHearts();
    }

    @Override
    public void setHearts(String uuid, int hearts) {
        if (uuid == null || uuid.isBlank()) {
            Bukkit.getLogger().warning("[LifestealSMP] setHearts called with null or empty UUID.");
            return;
        }

        if (connection == null) {
            Bukkit.getLogger().severe("[LifestealSMP] Cannot set hearts: SQLite connection is null.");
            return;
        }

        String sql = "REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, hearts);
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] Updated hearts for UUID " + uuid + " to " + hearts + ".");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite setHearts failed for UUID " + uuid + ": " + e.getMessage());
        }
    }
}

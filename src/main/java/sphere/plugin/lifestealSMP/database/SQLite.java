package sphere.plugin.lifestealSMP.database;

import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.io.File;
import java.sql.*;

/**
 * SQLite implementation for interacting with the LifestealSMP plugin's database.
 * Provides methods for connecting, disconnecting, creating tables, and interacting with player hearts data.
 */
public class SQLite implements SQL {

    private final LifestealSMP plugin;
    private Connection connection;

    /**
     * Constructor to initialize the SQLite instance with the plugin reference.
     *
     * @param plugin The LifestealSMP plugin instance.
     */
    public SQLite(LifestealSMP plugin) {
        this.plugin = plugin;
        this.connection = null; // Ensure connection is initialized as null
    }

    @Override
    public void connect() {
        if (connection != null) {
            Bukkit.getLogger().warning("[LifestealSMP] SQLite connection already established.");
            return;
        }

        try {
            File dbFile = new File(plugin.getDataFolder(), "hearts.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            Bukkit.getLogger().info("[LifestealSMP] SQLite connection established successfully.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite connection failed: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) {
            Bukkit.getLogger().warning("[LifestealSMP] SQLite connection is already null or closed.");
            return;
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("[LifestealSMP] SQLite connection closed successfully.");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite disconnect failed: " + e.getMessage());
        }
    }

    @Override
    public void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_hearts (
                uuid TEXT PRIMARY KEY,
                hearts INTEGER NOT NULL
            );
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] SQLite table created successfully.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite table creation failed: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        } else {
            throw new SQLException("SQLite connection is not established or is closed.");
        }
    }

    @Override
    public int getHearts(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] UUID provided is null or empty.");
            return plugin.getConfigManager().getStartingHearts();  // Return default starting hearts if invalid UUID
        }

        String query = "SELECT hearts FROM player_hearts WHERE uuid=?;";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hearts");
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite getHearts failed for UUID " + uuid + ": " + e.getMessage());
        }

        return plugin.getConfigManager().getStartingHearts();  // Return default hearts if no record found or error occurs
    }

    @Override
    public void setHearts(String uuid, int hearts) {
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] UUID provided is null or empty.");
            return;
        }

        String sql = "REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, hearts);
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] SQLite setHearts successfully for UUID " + uuid);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite setHearts failed for UUID " + uuid + " with hearts " + hearts + ": " + e.getMessage());
        }
    }
}

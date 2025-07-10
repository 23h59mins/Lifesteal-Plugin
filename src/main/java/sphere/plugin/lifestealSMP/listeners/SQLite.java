package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.database.SQL;

import java.io.File;
import java.sql.*;

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
            Bukkit.getLogger().warning("[LifestealSMP] SQLite connection is already active.");
            return;
        }

        try {
            File dbFolder = plugin.getDataFolder();
            if (!dbFolder.exists()) dbFolder.mkdirs();

            File dbFile = new File(dbFolder, "hearts.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            Bukkit.getLogger().info("[LifestealSMP] SQLite connection established successfully.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite connection failed: " + e.getMessage());
            connection = null;
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) {
            Bukkit.getLogger().warning("[LifestealSMP] SQLite disconnect called but connection is null.");
            return;
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("[LifestealSMP] SQLite connection closed successfully.");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite disconnect failed: " + e.getMessage());
        } finally {
            connection = null;
        }
    }

    @Override
    public void createTables() {
        final String sql = """
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
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] getHearts called with null/empty UUID.");
            return plugin.getConfigManager().getStartingHearts();
        }

        final String query = "SELECT hearts FROM player_hearts WHERE uuid = ?;";
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

        return plugin.getConfigManager().getStartingHearts();
    }

    @Override
    public void setHearts(String uuid, int hearts) {
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] setHearts called with null/empty UUID.");
            return;
        }

        final String sql = "REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, hearts);
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] setHearts successful for UUID " + uuid + ", hearts: " + hearts);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite setHearts failed for UUID " + uuid + ": " + e.getMessage());
        }
    }
}

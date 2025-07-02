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
    }

    @Override
    public void connect() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "hearts.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            Bukkit.getLogger().info("[LifestealSMP] SQLite connection established successfully.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite connection failed: " + e.getMessage());
            connection = null;  // Ensuring the connection is null in case of failure
        }
    }

    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    Bukkit.getLogger().info("[LifestealSMP] SQLite connection closed successfully.");
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[LifestealSMP] SQLite disconnect failed: " + e.getMessage());
            }
        } else {
            Bukkit.getLogger().warning("[LifestealSMP] SQLite connection is already null or closed.");
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
        }
        throw new SQLException("SQLite connection is not established or is closed.");
    }

    @Override
    public int getHearts(String uuid) {
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
        return plugin.getConfigManager().getStartingHearts();  // Default value if there's an error
    }

    @Override
    public void setHearts(String uuid, int hearts) {
        String sql = "REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, hearts);
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] SQLite setHearts succeeded for UUID " + uuid + " with hearts " + hearts);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite setHearts failed for UUID " + uuid + " with hearts " + hearts + ": " + e.getMessage());
        }
    }
}

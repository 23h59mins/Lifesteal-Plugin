package sphere.plugin.lifestealSMP.database;

import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;

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
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite connection failed: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
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
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite table creation failed: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public int getHearts(String uuid) {
        String query = "SELECT hearts FROM player_hearts WHERE uuid=?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("hearts");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite getHearts failed: " + e.getMessage());
        }
        return plugin.getConfigManager().getStartingHearts();
    }

    @Override
    public void setHearts(String uuid, int hearts) {
        String sql = "REPLACE INTO player_hearts (uuid, hearts) VALUES (?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, hearts);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] SQLite setHearts failed: " + e.getMessage());
        }
    }
}

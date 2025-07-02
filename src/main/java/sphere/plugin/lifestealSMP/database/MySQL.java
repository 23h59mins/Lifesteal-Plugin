package sphere.plugin.lifestealSMP.database;

import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.sql.*;

public class MySQL implements SQL {

    private final LifestealSMP plugin;
    private Connection connection;

    /**
     * Initializes the MySQL connection with the plugin instance.
     *
     * @param plugin The LifestealSMP plugin instance.
     */
    public MySQL(LifestealSMP plugin) {
        this.plugin = plugin;
        this.connection = null;  // Initialize to null, will be set during connection
    }

    @Override
    public void connect() {
        if (connection != null) {
            Bukkit.getLogger().warning("[LifestealSMP] MySQL connection is already established.");
            return;
        }

        // Get MySQL connection details from config
        String host = plugin.getConfigManager().getMysqlHost();
        int port = plugin.getConfigManager().getMysqlPort();
        String database = plugin.getConfigManager().getMysqlDatabase();
        String username = plugin.getConfigManager().getMysqlUsername();
        String password = plugin.getConfigManager().getMysqlPassword();

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
                    username, password
            );
            Bukkit.getLogger().info("[LifestealSMP] MySQL connection established successfully.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] MySQL connection failed: " + e.getMessage());
            connection = null;  // Ensure that connection is null if failed
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) {
            Bukkit.getLogger().warning("[LifestealSMP] MySQL connection is already null or closed.");
            return;
        }

        try {
            if (!connection.isClosed()) {
                connection.close();
                Bukkit.getLogger().info("[LifestealSMP] MySQL connection closed successfully.");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] MySQL disconnect failed: " + e.getMessage());
        }
    }

    @Override
    public void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_hearts (
                uuid VARCHAR(36) PRIMARY KEY,
                hearts INT NOT NULL
            );
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            Bukkit.getLogger().info("[LifestealSMP] MySQL table created successfully.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] MySQL table creation failed: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        } else {
            throw new SQLException("MySQL connection is not established or is closed.");
        }
    }

    @Override
    public int getHearts(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            Bukkit.getLogger().warning("[LifestealSMP] UUID provided is null or empty.");
            return plugin.getConfigManager().getStartingHearts();  // Return default starting hearts if UUID is invalid
        }

        String query = "SELECT hearts FROM player_hearts WHERE uuid=?;";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hearts");
                } else {
                    // If no record is found for the player, return default hearts value
                    Bukkit.getLogger().warning("[LifestealSMP] No hearts record found for UUID: " + uuid);
                    return plugin.getConfigManager().getStartingHearts();
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] MySQL getHearts failed for UUID " + uuid + ": " + e.getMessage());
        }
        return plugin.getConfigManager().getStartingHearts();  // Return default starting hearts if an error occurs
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
            Bukkit.getLogger().info("[LifestealSMP] MySQL setHearts successfully for UUID " + uuid);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[LifestealSMP] MySQL setHearts failed for UUID " + uuid + " with hearts " + hearts + ": " + e.getMessage());
        }
    }
}

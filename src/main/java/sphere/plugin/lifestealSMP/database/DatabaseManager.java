package sphere.plugin.lifestealSMP.database;

import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.models.PlayerData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final LifestealSMP plugin;
    private SQL sql;

    public DatabaseManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.sql = initializeDatabaseConnection();
    }

    public void setup() throws SQLException {
        this.sql = initializeDatabaseConnection();
        connectAndSetupDatabase();
    }

    private void connectAndSetupDatabase() throws SQLException {
        if (sql == null) {
            plugin.getLogger().severe("[DatabaseManager] SQL instance is null during setup.");
            throw new SQLException("SQL instance is null.");
        }
        sql.connect();
        sql.createTables();
    }

    public void close() throws SQLException {
        if (sql != null) {
            sql.disconnect();
        } else {
            plugin.getLogger().warning("[DatabaseManager] SQL was null during close.");
        }
    }

    public Connection getConnection() throws SQLException {
        if (sql != null) {
            return sql.getConnection();
        } else {
            plugin.getLogger().severe("[DatabaseManager] Tried to get connection but SQL is null.");
            throw new SQLException("Database connection not established.");
        }
    }

    public PlayerData loadPlayer(Player player) throws SQLException {
        if (player == null) {
            plugin.getLogger().warning("[DatabaseManager] Attempted to load data for null player.");
            return null;
        }

        UUID uuid = player.getUniqueId();
        if (uuid == null) {
            plugin.getLogger().warning("[DatabaseManager] Player UUID is null: " + player.getName());
            return null;
        }

        int hearts = getHearts(uuid);
        return new PlayerData(player, hearts);
    }

    public PlayerData loadPlayer(UUID uuid, String name) throws SQLException {
        if (uuid == null || name == null || name.isBlank()) {
            plugin.getLogger().warning("[DatabaseManager] Cannot load player data: UUID or name is null/blank.");
            return null;
        }

        int hearts = getHearts(uuid);
        return new PlayerData(uuid, name, hearts);
    }

    public void savePlayer(PlayerData data) throws SQLException {
        if (data == null) {
            plugin.getLogger().warning("[DatabaseManager] Attempted to save null PlayerData.");
            return;
        }

        UUID uuid = data.getUuid();
        if (uuid == null) {
            plugin.getLogger().warning("[DatabaseManager] PlayerData UUID is null.");
            return;
        }

        setHearts(uuid, data.getHearts());
    }

    public int getHearts(UUID uuid) throws SQLException {
        if (uuid == null) {
            plugin.getLogger().warning("[DatabaseManager] UUID is null when retrieving hearts.");
            return 0;
        }

        try {
            return sql.getHearts(uuid.toString());
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[DatabaseManager] Failed to get hearts for UUID: " + uuid, e);
            throw e;
        }
    }

    public void setHearts(UUID uuid, int hearts) throws SQLException {
        if (uuid == null) {
            plugin.getLogger().warning("[DatabaseManager] UUID is null when setting hearts.");
            return;
        }

        try {
            sql.setHearts(uuid.toString(), hearts);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[DatabaseManager] Failed to set hearts for UUID: " + uuid, e);
            throw e;
        }
    }

    private SQL initializeDatabaseConnection() {
        try {
            if (plugin.getConfigManager().isMysqlEnabled()) {
                plugin.getLogger().info("[DatabaseManager] Using MySQL backend.");
                return new MySQL(plugin);
            } else {
                plugin.getLogger().info("[DatabaseManager] Using SQLite backend.");
                return new SQLite(plugin);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[DatabaseManager] Failed to initialize database backend.", e);
            return null;
        }
    }
}

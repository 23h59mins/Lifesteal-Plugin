package sphere.plugin.lifestealSMP.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.models.PlayerData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    private final LifestealSMP plugin;
    private SQL sql;

    public DatabaseManager(LifestealSMP plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (plugin.getConfigManager().isMysqlEnabled()) {
            sql = new MySQL(plugin);
        } else {
            sql = new SQLite(plugin);
        }
        sql.connect();
        sql.createTables();
    }

    public void close() {
        if (sql != null) sql.disconnect();
    }

    public Connection getConnection() throws SQLException {
        return sql.getConnection();
    }

    // New clean PlayerData based methods

    public PlayerData loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        int hearts = sql.getHearts(uuid.toString());
        return new PlayerData(player, hearts);
    }

    public PlayerData loadPlayer(UUID uuid, String name) {
        int hearts = sql.getHearts(uuid.toString());
        return new PlayerData(uuid, name, hearts);
    }

    public void savePlayer(PlayerData data) {
        sql.setHearts(data.getUuid().toString(), data.getHearts());
    }

    // Low-level legacy (used internally by HeartManager)
    public int getHearts(UUID uuid) {
        return sql.getHearts(uuid.toString());
    }

    public void setHearts(UUID uuid, int hearts) {
        sql.setHearts(uuid.toString(), hearts);
    }
}

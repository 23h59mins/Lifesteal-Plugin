package sphere.plugin.lifestealSMP.database;

import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.models.PlayerData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final LifestealSMP plugin;  // Plugin is final, cannot be null if the class is initialized correctly
    private SQL sql;  // Keep plugin field initialized as requested

    /**
     * Initializes the DatabaseManager with the plugin instance and sets up the database connection.
     * Will choose MySQL or SQLite based on the configuration.
     *
     * @param plugin The LifestealSMP plugin instance.
     */
    public DatabaseManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.sql = initializeDatabaseConnection();  // Initialize the database connection
    }

    /**
     * Setup the database connection and tables based on configuration.
     */
    public void setup() throws SQLException {
        // Reinitialize connection only when necessary (based on settings)
        this.sql = initializeDatabaseConnection();
        connectAndSetupDatabase();
    }

    /**
     * Connects to the database and creates necessary tables.
     * Safeguard to ensure database connections are properly initialized.
     */
    private void connectAndSetupDatabase() throws SQLException {
        sql.connect();
        sql.createTables();
    }

    /**
     * Gracefully closes the database connection if it's not null.
     * Proper resource management to avoid memory leaks.
     */
    public void close() throws SQLException {
        if (sql != null) {
            sql.disconnect();
        }
    }

    /**
     * Returns a connection to the database, handling any potential SQLExceptions.
     *
     * @return A Connection to the database.
     * @throws SQLException If the connection fails.
     */
    public Connection getConnection() throws SQLException {
        if (sql != null) {
            return sql.getConnection();
        } else {
            throw new SQLException("Database connection is not established.");
        }
    }

    // New clean PlayerData based methods

    /**
     * Loads player data from the database using the player's unique ID.
     *
     * @param player The player whose data is to be loaded.
     * @return The player's data.
     * @throws SQLException If an error occurs while loading player data.
     */
    public PlayerData loadPlayer(Player player) throws SQLException {
        if (player == null || player.getUniqueId() == null) {
            plugin.getLogger().warning("Attempted to load player data for a null player.");
            return null;
        }
        UUID uuid = player.getUniqueId();
        int hearts = getHearts(uuid);
        return new PlayerData(player, hearts);
    }

    /**
     * Loads player data using the player's UUID and name.
     *
     * @param uuid The player's UUID.
     * @param name The player's name.
     * @return The player's data.
     * @throws SQLException If an error occurs while loading player data.
     */
    public PlayerData loadPlayer(UUID uuid, String name) throws SQLException {
        if (uuid == null || name == null) {
            plugin.getLogger().warning("UUID or name is null when loading player data.");
            return null;
        }
        int hearts = getHearts(uuid);
        return new PlayerData(uuid, name, hearts);
    }

    /**
     * Saves the player's data into the database.
     *
     * @param data The player data to save.
     * @throws SQLException If an error occurs while saving player data.
     */
    public void savePlayer(PlayerData data) throws SQLException {
        if (data == null || data.getUuid() == null) {
            plugin.getLogger().warning("Attempted to save null or incomplete player data.");
            return;
        }
        setHearts(data.getUuid(), data.getHearts());
    }

    // Low-level legacy methods for managing player hearts in the database

    /**
     * Retrieves the number of hearts for a player from the database.
     *
     * @param uuid The player's UUID.
     * @return The number of hearts.
     * @throws SQLException If an error occurs while retrieving the hearts.
     */
    public int getHearts(UUID uuid) throws SQLException {
        if (uuid == null) {
            plugin.getLogger().warning("UUID is null when retrieving hearts.");
            return 0;
        }
        try {
            return sql.getHearts(uuid.toString());
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to retrieve hearts for UUID: " + uuid, e);
            throw e;
        }
    }

    /**
     * Sets the number of hearts for a player in the database.
     *
     * @param uuid   The player's UUID.
     * @param hearts The number of hearts to set.
     * @throws SQLException If an error occurs while setting the hearts.
     */
    public void setHearts(UUID uuid, int hearts) throws SQLException {
        if (uuid == null) {
            plugin.getLogger().warning("Cannot set hearts for null UUID.");
            return;
        }
        try {
            sql.setHearts(uuid.toString(), hearts);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set hearts for UUID: " + uuid, e);
            throw e;
        }
    }

    /**
     * Initializes the database connection based on config settings, ensuring the field is initialized.
     *
     * @return The initialized SQL connection object (either MySQL or SQLite).
     */
    private SQL initializeDatabaseConnection() {
        if (plugin.getConfigManager().isMysqlEnabled()) {
            return new MySQL(plugin);
        } else {
            return new SQLite(plugin);
        }
    }
}

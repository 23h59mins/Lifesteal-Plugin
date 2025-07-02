package sphere.plugin.lifestealSMP.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This interface defines the required operations for interacting with a database in the LifestealSMP plugin.
 * It provides methods for connecting, disconnecting, creating tables, and interacting with player heart data.
 *
 * Implementations of this interface should ensure proper exception handling and connection management.
 */
public interface SQL {

    /**
     * Establishes a connection to the database.
     * This method should handle connection retries, exceptions, and ensure the connection is established properly.
     */
    void connect() throws SQLException;

    /**
     * Closes the database connection.
     * If the connection is already closed or invalid, the method should handle it gracefully.
     */
    void disconnect() throws SQLException;

    /**
     * Creates the necessary tables in the database if they do not already exist.
     * Any necessary SQL commands should be executed here to ensure the database schema is set up correctly.
     */
    void createTables() throws SQLException;

    /**
     * Retrieves the active database connection.
     *
     * @return The active database connection.
     * @throws SQLException If the connection is not available or there is an error retrieving it.
     *         This exception may occur if the connection is closed, invalid, or uninitialized.
     */
    Connection getConnection() throws SQLException;

    /**
     * Retrieves the number of hearts for a specific player, identified by their UUID.
     *
     * @param uuid The player's UUID.
     * @return The number of hearts for the player, or a default value if an error occurs (e.g., if the UUID is not found).
     * @throws SQLException If there is an error fetching the hearts from the database.
     *         This exception is typically thrown when there is a database issue, such as a connection failure.
     */
    int getHearts(String uuid) throws SQLException;

    /**
     * Sets the number of hearts for a specific player, identified by their UUID.
     *
     * @param uuid   The player's UUID.
     * @param hearts The number of hearts to set.
     * @throws SQLException If there is an error updating the hearts in the database.
     *         This exception is typically thrown when there is a database issue during the update.
     */
    void setHearts(String uuid, int hearts) throws SQLException;
}

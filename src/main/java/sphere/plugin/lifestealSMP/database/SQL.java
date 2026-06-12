package sphere.plugin.lifestealSMP.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Defines the contract for SQL-based database access in the LifestealSMP plugin.
 * Implementing classes (e.g., MySQL, SQLite) must handle full lifecycle of DB access:
 * - Connection setup
 * - Schema preparation
 * - CRUD operations on player heart data
 * <p>
 * All implementations must be null-safe, robust, and performant.
 */
public interface SQL {

    /**
     * Establishes a connection to the SQL database.
     * Implementations should handle exceptions internally where possible and log detailed context.
     *
     * @throws SQLException If the connection cannot be established.
     */
    void connect() throws SQLException;

    /**
     * Closes the active SQL connection gracefully.
     * Implementations should safely handle redundant/disconnected states.
     *
     * @throws SQLException If an error occurs while closing the connection.
     */
    void disconnect() throws SQLException;

    /**
     * Initializes database schema and creates required tables if they don't exist.
     * This should be idempotent and safely re-runnable without side effects.
     *
     * @throws SQLException If table creation fails due to SQL or connection errors.
     */
    void createTables() throws SQLException;

    /**
     * Returns the active SQL connection for direct queries.
     * Callers must assume that the connection is live and valid.
     *
     * @return A non-null, active SQL Connection object.
     * @throws SQLException If the connection is unavailable, closed, or uninitialized.
     */
    Connection getConnection() throws SQLException;

    /**
     * Fetches the number of hearts associated with the given player UUID.
     *
     * @param uuid Non-null UUID string (36-char format). Must be validated by caller.
     * @return The number of hearts the player has in the database.
     * @throws SQLException If an error occurs during the retrieval operation.
     */
    int getHearts(String uuid) throws SQLException;

    /**
     * Sets the number of hearts for the player identified by the given UUID.
     *
     * @param uuid   Non-null UUID string (36-char format). Must be validated by caller.
     * @param hearts The heart count to persist (must be >= 0).
     * @throws SQLException If an error occurs during the update operation.
     */
    void setHearts(String uuid, int hearts) throws SQLException;
}

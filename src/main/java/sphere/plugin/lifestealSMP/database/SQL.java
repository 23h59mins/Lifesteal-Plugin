package sphere.plugin.lifestealSMP.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQL {

    void connect();

    void disconnect();

    void createTables();

    Connection getConnection() throws SQLException;

    int getHearts(String uuid);

    void setHearts(String uuid, int hearts);
}

package nl.lawinegevaar.yahoogroups.database;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.firebirdsql.management.FBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Builder
@Getter
@Slf4j
public class DatabaseInfo {

    @NonNull private final String hostname;
    private final int port;
    @NonNull private final String databaseName;
    @NonNull private final String user;
    @NonNull private final String password;

    /**
     * Creates a JDBC connection to the database.
     *
     * @return Database connection
     * @throws SQLException For failures to connect
     */
    public Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("charSet", "utf-8");

        return DriverManager.getConnection(getJdbcUrl(), properties);
    }

    /**
     * Creates the database if it doesn't already exist.
     *
     * @throws SQLException For failures to create the database
     */
    public void createDatabaseIfNecessary() throws SQLException {
        try {
            FBManager fbManager = getFbManager();
            // As forceCreate is false, will not do anything if db already exists
            fbManager.createDatabase(databaseName, user, password);
            fbManager.stop();
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            log.warn("ignored exception", e);
        }
    }

    private FBManager getFbManager() throws Exception {
        FBManager fbManager = new FBManager();
        fbManager.setServer(hostname);
        fbManager.setPort(port != 0 ? port : 3050);
        fbManager.start();
        return fbManager;
    }

    private String getJdbcUrl() {
        return String.format("jdbc:firebirdsql://%s:%d/%s", hostname, (port != 0 ? port : 3050), databaseName);
    }
}

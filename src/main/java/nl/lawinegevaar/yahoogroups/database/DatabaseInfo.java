package nl.lawinegevaar.yahoogroups.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.archiver.ScraperMain;
import org.firebirdsql.ds.FBSimpleDataSource;
import org.firebirdsql.management.FBManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

@Builder
@Getter
@Slf4j
public class DatabaseInfo implements AutoCloseable {

    @NonNull private final String hostname;
    private final int port;
    @NonNull private final String databaseName;
    @NonNull private final String user;
    @NonNull private final String password;
    @Getter(AccessLevel.NONE)
    private volatile boolean dataSourceInitialized;
    @Getter(lazy = true, value = AccessLevel.PUBLIC)
    private final DataSource dataSource = initDataSource();

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

    private DataSource initDataSource() {
        var fbDataSource = new FBSimpleDataSource();
        fbDataSource.setServerName(hostname);
        if (port != 0) {
            fbDataSource.setPortNumber(port);
        }
        fbDataSource.setDatabaseName(databaseName);
        fbDataSource.setUser(user);
        fbDataSource.setPassword(password);
        fbDataSource.setCharSet("utf-8");
        var config = new HikariConfig();
        config.setDataSource(fbDataSource);
        // see close()
        dataSourceInitialized = true;
        return new HikariDataSource(config);
    }

    private FBManager getFbManager() throws Exception {
        var fbManager = new FBManager();
        fbManager.setDefaultCharacterSet("UTF8");
        fbManager.setServer(hostname);
        fbManager.setPort(port != 0 ? port : 3050);
        fbManager.start();
        return fbManager;
    }

    public static DatabaseInfo createDatabaseInfo() {
        return createDatabaseInfo(readDatabaseConfiguration());
    }

    private static Properties readDatabaseConfiguration() {
        try (InputStream is = ScraperMain.class.getResourceAsStream("/database.properties")) {
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to access database.properties", e);
        }
    }
    
    private static DatabaseInfo createDatabaseInfo(Properties properties) {
        return DatabaseInfo.builder()
                .hostname(properties.getProperty("db.hostname", "localhost"))
                .port(intValue(properties.getProperty("db.port"), 3050))
                .databaseName(properties.getProperty("db.databaseName", "yahooarchive.fdb"))
                .user(properties.getProperty("db.user", "sysdba"))
                .password(properties.getProperty("db.password", "masterkey"))
                .build();
    }

    private static int intValue(String intString, int defaultValue) {
        if (intString == null || intString.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void close() {
        // Need a bit of trickery to avoid initializing the data source if it wasn't used yet, and the code rewrite
        // of the field to an AtomicReference by Lombok not being visible in IntelliJ
        if (dataSourceInitialized && getDataSource() instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }

}

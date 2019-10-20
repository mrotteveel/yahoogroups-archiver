package nl.lawinegevaar.yahoogroups.archiver;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.DatabaseInitializer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Scrapes data from Yahoo Groups
 */
@Slf4j
public class ScraperMain {

    public static void main(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        Properties properties = readDatabaseConfiguration();
        DatabaseInfo databaseInfo = createDatabaseInfo(properties);
        initialize(databaseInfo);
        if (commandLine.hasOption("init-only")) {
            String message = "Initialization only requested, exiting...";
            System.out.println(message);
            log.info(message);
            return;
        }

        String cookieString = commandLine.getOptionValue("c");
        if (commandLine.hasOption("u")) {
            boolean retryGaps = commandLine.hasOption("r");
            Updater updater = new Updater(databaseInfo, retryGaps, cookieString);
            updater.startUpdating();
        } else {
            String groupName = commandLine.getOptionValue("g");
            if (groupName == null || groupName.isEmpty()) {
                printUsage(buildCommandLineOptions());
                return;
            }

            Scraper scraper = new Scraper(databaseInfo, groupName, cookieString);
            scraper.startScraping();
        }
    }

    private static void initialize(DatabaseInfo databaseInfo) {
        DatabaseInitializer initializer = new DatabaseInitializer(databaseInfo);
        initializer.initializeDatabase();
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

    private static Properties readDatabaseConfiguration() {
        try (InputStream is = ScraperMain.class.getResourceAsStream("/database.properties")) {
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to access database.properties", e);
        }
    }

    private static CommandLine getCommandLine(String[] args) {
        Options options = buildCommandLineOptions();
        CommandLineParser clParser = new DefaultParser();
        try {
            CommandLine commandLine = clParser.parse(options, args);

            if (commandLine.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }
            return commandLine;
        } catch (ParseException e) {
            System.err.println("Invalid command line: " + e.getMessage());
            printUsage(options);
            System.exit(-1);
        }
        throw new AssertionError("should not get here");
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ScraperMain", options);
    }

    private static Options buildCommandLineOptions() {
        return new Options()
                .addOption("h", "help", false, "Prints usage instructions and exits")
                .addOption(Option.builder()
                        .longOpt("init-only")
                        .desc("Initialize database only and exit")
                        .build())
                .addOption("g", "group", true, "Yahoo Group to scrape")
                .addOption("u", "update", false, "Update currently registered groups")
                .addOption("r", "retry-gaps", false, "When updating, retry existing gaps")
                .addOption("c", "cookie-string", true, "Cookie string");
    }
}

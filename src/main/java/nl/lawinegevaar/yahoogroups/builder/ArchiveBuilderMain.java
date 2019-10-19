package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.archiver.*;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.DatabaseInitializer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ArchiveBuilderMain {
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

        String outputDirectory = commandLine.getOptionValue("o");
        if (outputDirectory == null || outputDirectory.isEmpty()) {
            printUsage(buildCommandLineOptions());
            return;
        }

        new ArchiveBuilder(outputDirectory, databaseInfo)
                .build();
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
        formatter.printHelp("ArchiveBuilderMain", options);
    }

    private static Options buildCommandLineOptions() {
        return new Options()
                .addOption("h", "help", false, "Prints usage instructions and exits")
                .addOption(Option.builder()
                        .longOpt("init-only")
                        .desc("Initialize database only and exit")
                        .build())
                .addOption("o", "output", true, "Output directory for archive (must be empty)");
    }
}

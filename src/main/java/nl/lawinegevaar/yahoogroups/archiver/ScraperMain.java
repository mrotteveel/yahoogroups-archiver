package nl.lawinegevaar.yahoogroups.archiver;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.DatabaseInitializer;
import org.apache.commons.cli.*;

/**
 * Scrapes data from Yahoo Groups
 */
@Slf4j
public class ScraperMain {

    public static void main(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        DatabaseInfo databaseInfo = DatabaseInfo.createDatabaseInfo();
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

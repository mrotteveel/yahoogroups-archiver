package nl.lawinegevaar.yahoogroups.common;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.DatabaseInitializer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.IOException;

@Slf4j
public abstract class AbstractApplication {

    private final String applicationName;

    protected AbstractApplication(String applicationName) {
        this.applicationName = applicationName;
    }

    protected final void run(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        try (var databaseInfo = DatabaseInfo.createDatabaseInfo()) {
            initialize(databaseInfo);
            if (commandLine.hasOption("init-only")) {
                String message = "Initialization only requested, exiting...";
                System.out.println(message);
                log.info(message);
                return;
            }

            runApplication(commandLine, databaseInfo);
        }
    }

    protected abstract void runApplication(CommandLine commandLine, DatabaseInfo databaseInfo);

    private static void initialize(DatabaseInfo databaseInfo) {
        var initializer = new DatabaseInitializer(databaseInfo);
        initializer.initializeDatabase();
    }

    protected final CommandLine getCommandLine(String[] args) {
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

    protected final void printUsage() {
        printUsage(buildCommandLineOptions());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void printUsage(Options options) {
        try {
            HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
            formatter.printHelp(applicationName, null, options, null, true);
        } catch (IOException e) {
            System.err.println("Could not print usage help.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Options buildCommandLineOptions() {
        var options = new Options();
        addDefaultOptions(options);
        addApplicationOptions(options);
        return options;
    }

    /**
     * Adds application specific options to the commandline options.
     *
     * @param options options to modify
     */
    protected abstract void addApplicationOptions(Options options);

    private void addDefaultOptions(Options options) {
        options.addOption("h", "help", false, "Prints usage instructions and exits")
                .addOption(Option.builder()
                        .longOpt("init-only")
                        .desc("Initialize database only and exit")
                        .get());
    }

}

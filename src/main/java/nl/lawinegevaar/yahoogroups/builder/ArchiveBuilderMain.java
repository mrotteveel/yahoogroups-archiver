package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.DatabaseInitializer;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class ArchiveBuilderMain {
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

        String outputDirectory = commandLine.getOptionValue("o");
        if (outputDirectory == null || outputDirectory.isEmpty()) {
            printUsage(buildCommandLineOptions());
            return;
        }

        boolean rebuildLinkInfo = commandLine.hasOption("r");
        boolean applyGzip = commandLine.hasOption("g");

        PathWriterFunction pathWriterFunction = applyGzip
                ? ArchiveBuilderMain::getGzippedWriter
                : Files::newBufferedWriter;

        new ArchiveBuilder(outputDirectory, rebuildLinkInfo, pathWriterFunction, databaseInfo)
                .build();
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
        formatter.printHelp("ArchiveBuilderMain", options);
    }

    private static Options buildCommandLineOptions() {
        return new Options()
                .addOption("h", "help", false, "Prints usage instructions and exits")
                .addOption(Option.builder()
                        .longOpt("init-only")
                        .desc("Initialize database only and exit")
                        .build())
                .addOption("o", "output", true, "Output directory for archive (must be empty)")
                .addOption("r", "rebuild-link-info", false, "Rebuild link info")
                .addOption("g", "gzip", false, "Gzip output files");
    }

    private static Writer getGzippedWriter(Path path) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        new GZIPOutputStream(
                                Files.newOutputStream(path)), StandardCharsets.UTF_8));
    }

}

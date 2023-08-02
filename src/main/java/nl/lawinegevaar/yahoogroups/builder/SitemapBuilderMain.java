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
public class SitemapBuilderMain {

    public static void main(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        try (var databaseInfo = DatabaseInfo.createDatabaseInfo()) {
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

            String sitePrefix = commandLine.getOptionValue("s");
            if (sitePrefix == null || sitePrefix.isEmpty()) {
                printUsage(buildCommandLineOptions());
                return;
            }

            boolean applyGzip = commandLine.hasOption("g");

            PathWriterFunction pathWriterFunction = applyGzip
                    ? SitemapBuilderMain::getGzippedWriter
                    : Files::newBufferedWriter;

            new SitemapBuilder(outputDirectory, sitePrefix, pathWriterFunction, databaseInfo)
                    .build();
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
        formatter.printHelp("SitemapBuilderMain", options);
    }

    private static Options buildCommandLineOptions() {
        return new Options()
                .addOption("h", "help", false, "Prints usage instructions and exits")
                .addOption(Option.builder()
                        .longOpt("init-only")
                        .desc("Initialize database only and exit")
                        .build())
                .addOption("o", "output", true, "Output directory for archive (must be empty)")
                .addOption("s", "site-prefix", true, "Url prefix (including protocol) for the site, without trailing slash")
                .addOption("g", "gzip", false, "Gzip output files");
    }

    private static Writer getGzippedWriter(Path path) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        new GZIPOutputStream(
                                Files.newOutputStream(path)), StandardCharsets.UTF_8));
    }

}

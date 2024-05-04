package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.common.AbstractApplication;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.zip.GZIPOutputStream;

@Slf4j
public final class SitemapBuilderMain extends AbstractApplication {

    private SitemapBuilderMain() {
        super("SitemapBuilderMain");
    }

    public static void main(String[] args) {
        new SitemapBuilderMain().run(args);
    }

    @Override
    protected void runApplication(CommandLine commandLine, DatabaseInfo databaseInfo) {
        String outputDirectory = commandLine.getOptionValue("o");
        if (outputDirectory == null || outputDirectory.isEmpty()) {
            printUsage();
            return;
        }

        String sitePrefix = commandLine.getOptionValue("s");
        if (sitePrefix == null || sitePrefix.isEmpty()) {
            printUsage();
            return;
        }

        boolean applyGzip = commandLine.hasOption("g");
        PathWriterFunction pathWriterFunction = applyGzip
                ? SitemapBuilderMain::getGzippedWriter
                : Files::newBufferedWriter;

        String lastModOverride = commandLine.getOptionValue("m");
        if (lastModOverride == null || lastModOverride.isEmpty()) {
            lastModOverride = null;
        } else {
            try {
                LocalDate.parse(lastModOverride);
            } catch (DateTimeParseException e) {
                System.err.println(
                        "Invalid date for option -m/--lastmod-override, expected format is yyyy-MM-dd, value was "
                                + lastModOverride);
                printUsage();
                return;
            }
        }

        new SitemapBuilder(outputDirectory, sitePrefix, lastModOverride, pathWriterFunction, databaseInfo)
                .build();
    }

    @Override
    protected void addApplicationOptions(Options options) {
        options.addOption("o", "output", true, "Output directory for archive (must be empty)")
                .addOption("s", "site-prefix", true, "Url prefix (including protocol) for the site, without trailing slash")
                .addOption("g", "gzip", false, "Gzip output files")
                .addOption("m", "lastmod-override", true, "Override lastmod value for all sitemap entries (note sitemap-index), format yyyy-MM-dd");
    }

    private static Writer getGzippedWriter(Path path) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        new GZIPOutputStream(
                                Files.newOutputStream(path)), StandardCharsets.UTF_8));
    }

}

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
import java.util.zip.GZIPOutputStream;

@Slf4j
public final class ArchiveBuilderMain extends AbstractApplication {

    private ArchiveBuilderMain() {
        super("ArchiveBuilderMain");
    }

    public static void main(String[] args) {
        new ArchiveBuilderMain().run(args);
    }

    @Override
    protected void runApplication(CommandLine commandLine, DatabaseInfo databaseInfo) {
        String outputDirectory = commandLine.getOptionValue("o");
        if (outputDirectory == null || outputDirectory.isEmpty()) {
            printUsage();
            return;
        }

        boolean applyGzip = commandLine.hasOption("g");
        PathWriterFunction pathWriterFunction = applyGzip
                ? ArchiveBuilderMain::getGzippedWriter
                : Files::newBufferedWriter;

        boolean rebuildLinkInfo = commandLine.hasOption("r");
        new ArchiveBuilder(outputDirectory, rebuildLinkInfo, pathWriterFunction, databaseInfo)
                .build();
    }

    @Override
    protected void addApplicationOptions(Options options) {
        options.addOption("o", "output", true, "Output directory for archive (must be empty)")
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

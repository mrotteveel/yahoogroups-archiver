package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Slf4j
final class SitemapBuilder {

    private final Path outputPath;
    private final String sitePrefix;
    private final PathWriterFunction pathWriterFunction;
    private final DatabaseInfo databaseInfo;

    SitemapBuilder(String outputDirectory, String sitePrefix, PathWriterFunction pathWriterFunction, DatabaseInfo databaseInfo) {
        this.outputPath = createOutputDirectory(outputDirectory);
        this.sitePrefix = sitePrefix;
        this.pathWriterFunction = pathWriterFunction;
        this.databaseInfo = databaseInfo;
    }

    void build() {
        log.info("Starting building of sitemap at {}", OffsetDateTime.now());
        SitemapGenerator sitemapGenerator = new SitemapGenerator(outputPath, sitePrefix, pathWriterFunction);

        sitemapGenerator.addSitemapEntry("/", LocalDateTime.now());
        
        try (var db = new DatabaseAccess(databaseInfo);
             var sitemapLinksStream = db.getSitemapLinks()) {
            sitemapLinksStream
                    .forEach(linksRecord ->
                            sitemapGenerator.addSitemapEntry(linksRecord.getPath(), linksRecord.getLastChange()));
        }
        log.info("Finished adding links, finishing sitemap index at {}", OffsetDateTime.now());
        sitemapGenerator.createSitemapIndex();
        log.info("Building sitemap complete at {}", OffsetDateTime.now());
    }

    private static Path createOutputDirectory(String outputDirectory) {
        Path outputPath = Path.of(outputDirectory).toAbsolutePath();
        try {
            log.info("Creating directories for output path {}", outputPath);
            Files.createDirectories(outputPath);
            return outputPath;
        } catch (IOException e) {
            // Either invalid path, or path represents a file
            throw new ArchiveBuildingException("Could not create directory for path " + outputPath, e);
        }
    }

}

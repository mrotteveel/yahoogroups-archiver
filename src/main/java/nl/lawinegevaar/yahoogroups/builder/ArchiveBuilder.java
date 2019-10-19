package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
class ArchiveBuilder {

    private final DatabaseInfo databaseInfo;
    private final Path outputPath;

    ArchiveBuilder(String outputDirectory, DatabaseInfo databaseInfo) {
        outputPath = createOutputDirectory(outputDirectory);
        if (isNotEmpty(outputPath)) {
            throw new ArchiveBuildingException("Output path " + outputPath + " is not empty");
        }
        this.databaseInfo = databaseInfo;
    }

    void build() {
        try (var db = new DatabaseAccess(databaseInfo)) {
            List<YgroupRecord> yahooGroupsInformation = db.getYahooGroupsInformation();
            log.info("Starting build for {} groups", yahooGroupsInformation.size());
            for (YgroupRecord group : yahooGroupsInformation) {
                new GroupBuilder(outputPath, group, db)
                        .build();
            }
        }
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

    private static boolean isNotEmpty(Path path) {
        try (var pathStream = Files.list(path)) {
            return pathStream.findAny().isPresent();
        } catch (IOException e) {
            throw new ArchiveBuildingException("Could not check if path " + path + " is empty", e);
        }
    }

}

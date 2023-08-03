package nl.lawinegevaar.yahoogroups.builder;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.AssignHelper;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.builder.handlebars.ArchiveHelpers;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Slf4j
class ArchiveBuilder {

    private final DatabaseInfo databaseInfo;
    private final Path outputPath;
    private final boolean rebuildLinkInfo;
    private final Properties siteProperties;
    private final Handlebars handlebars;
    private final PathWriterFunction pathWriterFunction;

    ArchiveBuilder(String outputDirectory, boolean rebuildLinkInfo, PathWriterFunction pathWriterFunction,
                   DatabaseInfo databaseInfo) {
        outputPath = createOutputDirectory(outputDirectory);
        this.rebuildLinkInfo = rebuildLinkInfo;
        if (isNotEmpty(outputPath)) {
            throw new ArchiveBuildingException("Output path " + outputPath + " is not empty");
        }
        this.databaseInfo = databaseInfo;
        siteProperties = readSiteProperties();
        handlebars = new Handlebars()
                .registerHelpers(ConditionalHelpers.class)
                .registerHelper(AssignHelper.NAME, AssignHelper.INSTANCE)
                .registerHelpers(ArchiveHelpers.class);
        this.pathWriterFunction = pathWriterFunction;
    }

    void build() {
        log.info("Starting building of groups at {}", OffsetDateTime.now());
        copyStyle(outputPath);
        try (var db = new DatabaseAccess(databaseInfo)) {
            List<YgroupRecord> yahooGroupsInformation = db.getYahooGroupsInformation();
            log.info("Starting build for {} groups", yahooGroupsInformation.size());
            for (YgroupRecord group : yahooGroupsInformation) {
                new GroupBuilder(outputPath, rebuildLinkInfo, pathWriterFunction, siteProperties, handlebars, group, db)
                        .build();
            }

            buildIndex(yahooGroupsInformation);
        }
        log.info("Awaiting task completion");
        if (!ForkJoinPool.commonPool().awaitQuiescence(5, TimeUnit.MINUTES)) {
            log.warn("Common pool still had tasks remaining after five minutes");
        }
        log.info("Building groups complete at {}", OffsetDateTime.now());
    }

    private void buildIndex(List<YgroupRecord> yahooGroupsInformation) {
        log.info("Building root index");
        List<String> groupNames = yahooGroupsInformation.stream()
                .map(YgroupRecord::getGroupname)
                .sorted()
                .toList();
        try (var writer = pathWriterFunction.getWriter(outputPath.resolve("index.html"))) {
            Template rootIndex = handlebars.compile("root-index");
            Map<String, Object> variables = Map.of(
                    "groups", groupNames,
                    "site", siteProperties);
            rootIndex.apply(variables, writer);
        } catch (IOException e) {
            throw new ArchiveBuildingException("Could not create root index", e);
        }
    }

    private void copyStyle(Path outputPath) {
        Path styleFile = outputPath.resolve("archive-style.css");
        log.info("Copying archive-style.css to {}", styleFile);
        try (var reader = new InputStreamReader(getClass().getResourceAsStream("/archive-style.css"));
             var writer = pathWriterFunction.getWriter(styleFile)) {
            reader.transferTo(writer);
        } catch (IOException e) {
            throw new ArchiveBuildingException(format("Could not copy archive-style.css to %s", outputPath), e);
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

    private static Properties readSiteProperties() {
        try (InputStream is = ArchiveBuilder.class.getResourceAsStream("/site.properties")) {
            var props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to access site.properties", e);
        }
    }
}

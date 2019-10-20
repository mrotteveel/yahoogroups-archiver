package nl.lawinegevaar.yahoogroups.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.builder.json.YgMessage;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.RawdataRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static java.lang.String.format;

@Slf4j
class GroupBuilder {

    private final Path outputPath;
    private final Properties siteProperties;
    private final YgroupRecord group;
    private final DatabaseAccess db;
    private final ObjectMapper objectMapper;
    private final Template template;

    GroupBuilder(Path outputPath, Properties siteProperties, YgroupRecord group, DatabaseAccess db) {
        this.outputPath = outputPath;
        this.siteProperties = siteProperties;
        this.group = group;
        this.db = db;
        objectMapper = new ObjectMapper();
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelpers(ConditionalHelpers.class);
        try {
            template = handlebars.compile("message");
        } catch (IOException e) {
            throw new ArchiveBuildingException("Could not compile handlebars template", e);
        }
    }

    void build() {
        String groupName = group.getGroupname();
        log.info("Building for group {}", groupName);
        Path groupPath = outputPath.resolve(groupName);
        if (!groupPath.startsWith(outputPath)) {
            throw new ArchiveBuildingException(
                    format("Path for group %s was %s which is outside %s", groupName, groupPath, outputPath));
        }
        try {
            log.info("Creating directory {}", groupPath);
            Files.createDirectories(groupPath);
        } catch (IOException e) {
            throw new ArchiveBuildingException(format("Could not create directory %s", groupPath), e);
        }

        try (Stream<RawdataRecord> rawdataRecordStream = db.getRawDataForGroup(groupName)) {
            rawdataRecordStream.forEach(rawdataRecord -> buildForRecord(groupName, rawdataRecord, groupPath));
        }
    }

    private void buildForRecord(String groupName, RawdataRecord rawdataRecord, Path groupPath) {
        int groupId = rawdataRecord.getGroupId();
        int messageId = rawdataRecord.getMessageId();
        try {
            var ygMessage = objectMapper.readValue(rawdataRecord.getMessageJson(), YgMessage.class);
            try (var writer = Files.newBufferedWriter(groupPath.resolve(messageId + ".html"))) {
                Map<String, Object> variables = Map.of(
                        "groupName", groupName,
                        "ygMessage", ygMessage,
                        "site", siteProperties);
                template.apply(variables, writer);
            }
        } catch (IOException e) {
            log.error(
                    format("Unable to parse message for groupId: %d, messageId: %d; skipping", groupId, messageId), e);
        }
    }

}

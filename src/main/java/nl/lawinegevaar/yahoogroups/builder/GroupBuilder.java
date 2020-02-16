package nl.lawinegevaar.yahoogroups.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.builder.json.YgData;
import nl.lawinegevaar.yahoogroups.builder.json.YgMessage;
import nl.lawinegevaar.yahoogroups.builder.model.*;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.PostInformationRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.RawdataRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.requireNonNull;

@Slf4j
class GroupBuilder {

    private static final Duration ALLOWED_POST_DATE_DIFFERENCE = Duration.ofHours(6);
    private final boolean rebuildLinkInfo;
    private final Properties siteProperties;
    private final DatabaseAccess db;
    private final ObjectMapper objectMapper;
    private final Path groupPath;
    private final String groupName;
    private final Template messageTemplate;

    private final Map<Integer, Map<Integer, YearMonth>> yearMonthDeduplication = new HashMap<>();
    private final GroupSummary groupSummary;
    private final Template monthIndex;
    private final Template yearIndex;
    private final Template groupIndex;

    GroupBuilder(Path outputPath, boolean rebuildLinkInfo, Properties siteProperties, Handlebars handlebars,
                 YgroupRecord group, DatabaseAccess db) {
        this.rebuildLinkInfo = rebuildLinkInfo;
        this.siteProperties = siteProperties;
        this.db = db;
        groupName = group.getGroupname();
        objectMapper = new ObjectMapper();
        groupPath = outputPath.resolve(groupName);
        if (!groupPath.startsWith(outputPath)) {
            throw new ArchiveBuildingException(
                    format("Path for group %s was %s which is outside %s", groupName, groupPath, outputPath));
        }
        try {
            messageTemplate = handlebars.compile("message");
            monthIndex = handlebars.compile("month-index");
            yearIndex = handlebars.compile("year-index");
            groupIndex = handlebars.compile("group-index");
        } catch (IOException e) {
            throw new ArchiveBuildingException("Could not compile handlebars template", e);
        }
        groupSummary = new GroupSummary(groupName);
    }

    void build() {
        log.info("Building index for group {}", groupName);
        try {
            log.info("Creating directory {}", groupPath);
            Files.createDirectories(groupPath);
        } catch (IOException e) {
            throw new ArchiveBuildingException(format("Could not create directory %s", groupPath), e);
        }

        populateLinkInformation();

        buildHtml();
    }

    private void buildHtml() {
        log.info("Building archive for {} on path {}", groupName, groupPath);
        buildMessages();
        buildMonthIndices();
        buildYearIndices();
        buildGroupIndex();
    }

    private void buildGroupIndex() {
        log.info("Building group index for {}", groupName);
        ForkJoinPool.commonPool().execute(() -> {
            List<PostsPerYear> postsPerYear = groupSummary.getPostsPerYear();
            try (var writer = Files.newBufferedWriter(groupPath.resolve("index.html"))) {
                Map<String, Object> variables = Map.of(
                        "postsPerYear", postsPerYear,
                        "groupName", groupName,
                        "site", siteProperties);
                groupIndex.apply(variables, writer);
            } catch (IOException e) {
                throw new ArchiveBuildingException(
                        format("Could not create group index for %s", groupName), e);
            }
        });
    }

    private void buildYearIndices() {
        log.info("Building year indices for {}", groupName);
        groupSummary.getPostsPerMonthPerYear()
                .forEach(this::buildYearIndex);
    }

    private void buildYearIndex(Integer year, List<PostsPerMonth> postsPerMonthList) {
        ForkJoinPool.commonPool().execute(() -> {
            postsPerMonthList.sort(comparingInt(postsPerMonth -> postsPerMonth.getYearMonth().getMonth()));
            Path yearPath = groupPath.resolve(String.valueOf(year));
            try {
                Files.createDirectories(yearPath);
                try (var writer = Files.newBufferedWriter(yearPath.resolve("index.html"))) {
                    Map<String, Object> variables = Map.of(
                            "year", year,
                            "postsPerMonth", postsPerMonthList,
                            "yearNavigation", createYearNavigation(year),
                            "groupName", groupName,
                            "site", siteProperties);
                    yearIndex.apply(variables, writer);
                }
            } catch (IOException e) {
                throw new ArchiveBuildingException(
                        format("Could not create year index for %s - %d", groupName, year), e);
            }
        });
    }

    private YearNavigation createYearNavigation(Integer year) {
        return new YearNavigation(previous(year), next(year));
    }

    private void buildMonthIndices() {
        log.info("Building month indices for {}", groupName);
        groupSummary.forEachMonth(this::buildMonthIndex);
    }

    private void buildMonthIndex(YearMonth yearMonth, List<PostSummary> postSummaries) {
        ForkJoinPool.commonPool().execute(() -> {
            postSummaries.sort(comparing(PostSummary::getMessageId));
            try {
                Path yearMonthPath = createPath(yearMonth);
                try (var writer = Files.newBufferedWriter(yearMonthPath.resolve("index.html"))) {
                    Map<String, Object> variables = Map.of(
                            "yearMonth", yearMonth,
                            "posts", postSummaries,
                            "monthNavigation", createMonthNavigation(yearMonth),
                            "groupName", groupName,
                            "site", siteProperties);
                    monthIndex.apply(variables, writer);
                }
            } catch (IOException e) {
                throw new ArchiveBuildingException(
                        format("Could not create month index for %s - %s", groupName, yearMonth), e);
            }
        });
    }

    private MonthNavigation createMonthNavigation(YearMonth yearMonth) {
        return new MonthNavigation(previous(yearMonth), next(yearMonth));
    }

    private void buildMessages() {
        log.info("Building messages for {}", groupName);
        try (Stream<PostInformationRecord> postInformationRecordStream = db.getPostInformationForGroup(groupName)) {
            postInformationRecordStream
                    .forEach(this::buildForRecord);
        } finally {
            groupSummary.doneAddingMessages();
        }
    }

    private void buildForRecord(PostInformationRecord postInformationRecord) {
        int groupId = postInformationRecord.getGroupId();
        int messageId = postInformationRecord.getMessageId();
        try {
            var ygMessage = objectMapper.readValue(postInformationRecord.getMessageJson(), YgMessage.class);
            YearMonth yearMonth = getYearMonth(postInformationRecord);
            Path yearMonthPath = createPath(yearMonth);

            ForkJoinPool.commonPool().execute(() -> {
                OffsetDateTime offsetPostDate = postInformationRecord.getPostDate().atOffset(ZoneOffset.UTC);
                try (var writer = Files.newBufferedWriter(yearMonthPath.resolve(messageId + ".html"))) {
                    Map<String, Object> variables = Map.of(
                            "ygMessage", ygMessage,
                            "postInfo", postInformationRecord,
                            "postDate", offsetPostDate,
                            "groupName", groupName,
                            "site", siteProperties);
                    messageTemplate.apply(variables, writer);
                } catch (Exception e) {
                    log.error(format("Unable to parse message for groupId: %d, messageId: %d; skipping",
                            groupId, messageId), e);
                }
            });

            groupSummary.addToSummary(yearMonth, ygMessage);
        } catch (Exception e) {
            log.error(
                    format("Unable to parse message for groupId: %d, messageId: %d; skipping", groupId, messageId), e);
        }
    }

    private Path createPath(YearMonth yearMonth) throws IOException {
        Path yearMonthPath = groupPath.resolve(yearMonth.getPath());
        Files.createDirectories(yearMonthPath);
        return yearMonthPath;
    }

    private YearMonth getYearMonth(PostInformationRecord postInformationRecord) {
        return getYearMonth(postInformationRecord.getPostYear(), postInformationRecord.getPostMonth());
    }

    private YearMonth getYearMonth(int year, int month) {
        return yearMonthDeduplication
                .computeIfAbsent(year, k -> new HashMap<>())
                .computeIfAbsent(month, k -> new YearMonth(year, month));
    }

    private YearMonth previous(YearMonth current) {
        return groupSummary.previous(current);
    }

    private Integer previous(Integer currentYear) {
        return groupSummary.previous(currentYear);
    }

    private YearMonth next(YearMonth current) {
        return groupSummary.next(current);
    }

    private Integer next(Integer currentYear) {
        return groupSummary.next(currentYear);
    }

    private void populateLinkInformation() {
        if (rebuildLinkInfo) {
            log.info("Clearing link info for {} to rebuild", groupName);
            db.deleteLinkInfo(groupName);
        }
        log.info("Populating link info for {}", groupName);
        try (DatabaseAccess db2 = db.copy()) {
            // We need a second connection otherwise the result set is closed by the inserts
            try (Stream<RawdataRecord> rawdataRecordStream = db2.getRawDataForLinkInfo(groupName)) {
                rawdataRecordStream.forEach(this::buildLinkInformation);
            }
        }
        log.info("Fixing up post dates for {}", groupName);
        fixupLinkInfoPostDates();
    }

    private void fixupLinkInfoPostDates() {
        // Problem encountered with actual data: postDate absent in json, fixing with date of previous message
        // NOTE: Most should already have been caught by previous step
        db.fixupMissingLinkInfoPostDates(groupName);
    }

    private void buildLinkInformation(RawdataRecord rawdataRecord) {
        int groupId = rawdataRecord.getGroupId();
        int messageId = rawdataRecord.getMessageId();
        try {
            YgData ygData = requireNonNull(getMessageData(rawdataRecord.getMessageJson()),
                    format("messageJson was null for groupId: %d, messageId: %d", groupId, messageId));
            YgData ygRawData = getMessageData(rawdataRecord.getRawMessageJson());
            OffsetDateTime dateTime = determinePostDate(ygData, ygRawData);
            db.insertLinkInfo(groupId, messageId, zeroAsNull(ygData.getTopicId()), zeroAsNull(ygData.getPrevInTopic()),
                    zeroAsNull(ygData.getPrevInTime()), asLocalDateTime(dateTime));
        } catch (Exception e) {
            log.error(
                    format("Unable to parse message for groupId: %d, messageId: %d; skipping", groupId, messageId), e);
        }
    }

    private YgData getMessageData(String messageJson) throws IOException {
        if (messageJson == null || messageJson.isEmpty()) {
            return null;
        }
        var ygMessage = objectMapper.readValue(messageJson, YgMessage.class);
        return ygMessage.getYgData();
    }

    private OffsetDateTime determinePostDate(YgData ygData, YgData ygRawData) {
        OffsetDateTime originalPostDate = ygData.getOffsetPostDate();
        return Optional.ofNullable(ygRawData)
                .flatMap(rawData -> RawMessageDateParser.findDateTime(rawData.getRawEmail()))
                .filter(foundDateTime -> originalPostDate == null ||
                        Duration.between(originalPostDate, foundDateTime).abs()
                                .compareTo(ALLOWED_POST_DATE_DIFFERENCE) > 0)
                .orElse(originalPostDate);
    }

    private static Integer zeroAsNull(int value) {
        return value != 0 ? value : null;
    }

    private static LocalDateTime asLocalDateTime(OffsetDateTime value) {
        return value != null ? value.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() : null;
    }

}

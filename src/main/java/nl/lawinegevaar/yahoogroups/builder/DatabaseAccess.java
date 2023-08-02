package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.LinkInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.PostInformationRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.RawdataRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.SitemapLinksRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;
import org.jooq.CloseableQuery;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.*;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.param;

@Slf4j
class DatabaseAccess implements AutoCloseable {

    private final DSLContext ctx;
    private final DatabaseInfo databaseInfo;
    private final CloseableQuery linkInfoInsert;
    private final CloseableQuery updateLinkInfoPostDate;

    DatabaseAccess(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        Settings settings = new Settings();
        settings.setParamCastMode(ParamCastMode.NEVER);
        ctx = DSL.using(databaseInfo.getDataSource(), SQLDialect.FIREBIRD, settings);
        linkInfoInsert = ctx.insertInto(LINK_INFO, LINK_INFO.GROUP_ID, LINK_INFO.MESSAGE_ID, LINK_INFO.Y_TOPIC_ID,
                LINK_INFO.Y_PREV_IN_TOPIC, LINK_INFO.Y_PREV_IN_TIME, LINK_INFO.POST_DATE)
                .values(
                        param("groupId", SQLDataType.INTEGER),
                        param("messageId", SQLDataType.INTEGER),
                        param("yTopicId", SQLDataType.INTEGER),
                        param("yPrevInTopic", SQLDataType.INTEGER),
                        param("yPrevInTime", SQLDataType.INTEGER),
                        param("postDate", SQLDataType.LOCALDATETIME))
                .keepStatement(true);
        updateLinkInfoPostDate = ctx.update(LINK_INFO)
                .set(LINK_INFO.POST_DATE,
                        param("postDate", SQLDataType.LOCALDATETIME))
                .where(LINK_INFO.GROUP_ID.eq(
                        param("groupId", SQLDataType.INTEGER)))
                .and(LINK_INFO.MESSAGE_ID.eq(
                        param("messageId", SQLDataType.INTEGER)))
                .keepStatement(true);
    }

    public DatabaseAccess copy() {
        return new DatabaseAccess(databaseInfo);
    }

    @Override
    public void close() {
        linkInfoInsert.close();
        updateLinkInfoPostDate.close();
    }

    List<YgroupRecord> getYahooGroupsInformation() {
        return ctx.transactionResult((Configuration config) -> {
            DSLContext txCtx = DSL.using(config);
            return txCtx
                    .selectFrom(YGROUP)
                    .fetch();
        });
    }

    Stream<PostInformationRecord> getPostInformationForGroup(String groupName) {
        return ctx.selectFrom(POST_INFORMATION)
                .where(POST_INFORMATION.GROUPNAME.equal(groupName))
                .stream();
    }

    Stream<RawdataRecord> getRawDataForLinkInfo(String groupName) {
        return ctx.select(RAWDATA.asterisk())
                .from(RAWDATA)
                .innerJoin(YGROUP).on(YGROUP.ID.eq(RAWDATA.GROUP_ID))
                .leftJoin(LINK_INFO).using(RAWDATA.GROUP_ID, RAWDATA.MESSAGE_ID)
                .where(YGROUP.GROUPNAME.eq(groupName))
                .and(LINK_INFO.MESSAGE_ID.isNull())
                .fetchStreamInto(RAWDATA);
    }

    Stream<SitemapLinksRecord> getSitemapLinks() {
        return ctx
                .selectFrom(SITEMAP_LINKS)
                .stream();
    }

    void insertLinkInfo(int groupId, int messageId, Integer yTopicId, Integer yPrevInTopic, Integer yPrevInTime,
                        LocalDateTime postDate) {
        linkInfoInsert
                .bind("groupId", groupId)
                .bind("messageId", messageId)
                .bind("yTopicId", yTopicId)
                .bind("yPrevInTopic", yPrevInTopic)
                .bind("yPrevInTime", yPrevInTime)
                .bind("postDate", postDate)
                .execute();
    }

    void deleteLinkInfo(String groupName) {
        ctx.deleteFrom(LINK_INFO)
                .where(LINK_INFO.GROUP_ID.eq(ctx.select(YGROUP.ID).from(YGROUP).where(YGROUP.GROUPNAME.eq(groupName))))
                .execute();
    }

    void fixupMissingLinkInfoPostDates(String groupName) {
        LinkInfo li = LINK_INFO.as("li");
        ctx.update(li)
                .set(li.POST_DATE, ctx.select(LINK_INFO.POST_DATE)
                        .from(LINK_INFO)
                        .where(LINK_INFO.GROUP_ID.eq(li.GROUP_ID)
                                .and(LINK_INFO.MESSAGE_ID.eq(coalesce(li.Y_PREV_IN_TIME, li.Y_PREV_IN_TOPIC)))))
                .where(li.POST_DATE.isNull())
                .and(li.GROUP_ID.eq(ctx.select(YGROUP.ID).from(YGROUP).where(YGROUP.GROUPNAME.eq(groupName))))
                .execute();
    }
}

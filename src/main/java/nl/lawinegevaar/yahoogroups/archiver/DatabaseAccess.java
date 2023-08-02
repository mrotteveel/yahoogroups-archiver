package nl.lawinegevaar.yahoogroups.archiver;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;
import org.jooq.*;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.RAWDATA;
import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.YGROUP;
import static org.jooq.impl.DSL.*;

@Slf4j
class DatabaseAccess implements AutoCloseable {

    private final Connection connection;
    private final DSLContext ctx;
    private final Query rawDataInsert;

    DatabaseAccess(DatabaseInfo databaseInfo) {
        try {
            connection = databaseInfo.getConnection();
            Settings settings = new Settings();
            settings.setParamCastMode(ParamCastMode.NEVER);
            ctx = DSL.using(connection, SQLDialect.FIREBIRD, settings);
        } catch (SQLException e) {
            throw new ScrapingFailureException("Could not create connection to database", e);
        }
        //noinspection resource
        rawDataInsert = ctx.insertInto(RAWDATA, RAWDATA.GROUP_ID, RAWDATA.MESSAGE_ID, RAWDATA.MESSAGE_JSON, RAWDATA.RAW_MESSAGE_JSON)
                .values(
                        param("groupId", SQLDataType.INTEGER),
                        param("messageId", SQLDataType.INTEGER),
                        param("message", SQLDataType.CLOB),
                        param("rawMessage", SQLDataType.CLOB))
                .keepStatement(true);
    }

    @Override
    public void close() {
        try {
            rawDataInsert.close();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Exception closing connection of DatabaseAccess", e);
            }
        }
    }

    YgroupRecord getYahooGroupInformation(String groupName) {
        return ctx
                .selectFrom(YGROUP)
                .where(YGROUP.GROUPNAME.eq(groupName))
                .fetchOne();
    }

    YgroupRecord getOrCreateYahooGroupInformation(String groupName) {
        return ctx.transactionResult((Configuration config) -> {
            DSLContext txCtx = DSL.using(config);
            YgroupRecord ygroupRecord = txCtx
                    .selectFrom(YGROUP)
                    .where(YGROUP.GROUPNAME.eq(groupName))
                    .fetchOne();
            if (ygroupRecord != null) {
                return ygroupRecord;
            }

            return txCtx
                    .insertInto(YGROUP, YGROUP.GROUPNAME).values(groupName).returning()
                    .fetchOne();
        });
    }

    List<YgroupRecord> getYahooGroupsInformation() {
        return ctx
                .selectFrom(YGROUP)
                .fetch();
    }

    int getHighestMessageId(int groupId) {
        return ctx
                .select(max(RAWDATA.MESSAGE_ID)).from(RAWDATA).where(RAWDATA.GROUP_ID.eq(groupId))
                .fetchOptional()
                .map(Record1::value1)
                .orElse(0);
    }

    Stream<Record2<Integer, Integer>> getGapsForGroupId(int groupId) {
        TableLike<Record3<Integer, Integer, Integer>> nextMessageIdSelect = ctx
                .select(
                        RAWDATA.GROUP_ID,
                        RAWDATA.MESSAGE_ID,
                        lead(RAWDATA.MESSAGE_ID)
                                .over(partitionBy(RAWDATA.GROUP_ID).orderBy(RAWDATA.MESSAGE_ID)).as("NEXT_MESSAGE_ID"))
                .from(RAWDATA)
                .where(RAWDATA.GROUP_ID.eq(groupId));

        Field<Integer> messageId = nextMessageIdSelect.field(1, Integer.class);
        Field<Integer> nextMessageId = nextMessageIdSelect.field(2, Integer.class);
        return ctx.select(messageId, nextMessageId)
                .from(nextMessageIdSelect)
                .where(nextMessageId.minus(messageId).greaterThan(1))
                .fetchStream();
    }

    void insertRawdata(int groupId, int messageId, String message, String rawMessage) {
        rawDataInsert
                .bind("groupId", groupId)
                .bind("messageId", messageId)
                .bind("message", message)
                .bind("rawMessage", rawMessage)
                .execute();
    }
}

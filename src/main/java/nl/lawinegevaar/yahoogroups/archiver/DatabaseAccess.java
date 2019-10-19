package nl.lawinegevaar.yahoogroups.archiver;

import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import org.jooq.*;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.RAWDATA;
import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.YGROUP;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.param;

class DatabaseAccess implements AutoCloseable {

    private final DSLContext ctx;
    private final Query rawDataInsert;

    DatabaseAccess(DatabaseInfo databaseInfo) {
        try {
            Connection connection = databaseInfo.getConnection();
            Settings settings = new Settings();
            settings.setParamCastMode(ParamCastMode.NEVER);
            ctx = DSL.using(connection, SQLDialect.FIREBIRD, settings);
        } catch (SQLException e) {
            throw new ScrapingFailureException("Could not create connection to database", e);
        }
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
            ctx.close();
        }
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
        return ctx.transactionResult((Configuration config) -> {
            DSLContext txCtx = DSL.using(config);
            return txCtx
                    .selectFrom(YGROUP)
                    .fetch();
        });
    }

    int getHighestMessageId(int groupId) {
        return ctx
                .select(max(RAWDATA.MESSAGE_ID)).from(RAWDATA).where(RAWDATA.GROUP_ID.eq(groupId))
                .fetchOptional()
                .map(Record1::value1)
                .orElse(0);
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

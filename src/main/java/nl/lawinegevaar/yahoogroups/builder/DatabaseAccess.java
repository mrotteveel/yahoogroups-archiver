package nl.lawinegevaar.yahoogroups.builder;

import nl.lawinegevaar.yahoogroups.archiver.ScrapingFailureException;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.RawdataRecord;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.RAWDATA;
import static nl.lawinegevaar.yahoogroups.database.jooq.Tables.YGROUP;

class DatabaseAccess implements AutoCloseable {

    private final DSLContext ctx;

    DatabaseAccess(DatabaseInfo databaseInfo) {
        try {
            Connection connection = databaseInfo.getConnection();
            Settings settings = new Settings();
            settings.setParamCastMode(ParamCastMode.NEVER);
            ctx = DSL.using(connection, SQLDialect.FIREBIRD, settings);
        } catch (SQLException e) {
            throw new ScrapingFailureException("Could not create connection to database", e);
        }
    }

    @Override
    public void close() {
        try {
            // todo
        } finally {
            ctx.close();
        }
    }

    List<YgroupRecord> getYahooGroupsInformation() {
        return ctx.transactionResult((Configuration config) -> {
            DSLContext txCtx = DSL.using(config);
            return txCtx
                    .selectFrom(YGROUP)
                    .fetch();
        });
    }

    Stream<RawdataRecord> getRawDataForGroup(String groupName) {
        return ctx.select(RAWDATA.asterisk())
                .from(RAWDATA)
                .innerJoin(YGROUP).on(YGROUP.ID.eq(RAWDATA.GROUP_ID))
                .where(YGROUP.GROUPNAME.eq(groupName))
                .orderBy(RAWDATA.LAST_UPDATE.desc())
//                .limit(10)
                .fetchStreamInto(RAWDATA);
    }
}

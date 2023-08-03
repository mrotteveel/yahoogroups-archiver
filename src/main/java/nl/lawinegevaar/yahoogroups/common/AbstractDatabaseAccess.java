package nl.lawinegevaar.yahoogroups.common;

import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public abstract class AbstractDatabaseAccess implements AutoCloseable {

    private final DSLContext ctx;

    protected AbstractDatabaseAccess(DatabaseInfo databaseInfo) {
        this(databaseInfo, defaultSettings());
    }

    protected AbstractDatabaseAccess(DatabaseInfo databaseInfo, Settings settings) {
        ctx = DSL.using(databaseInfo.getDataSource(), SQLDialect.FIREBIRD, settings);
    }

    private static Settings defaultSettings() {
        var settings = new Settings();
        settings.setParamCastMode(ParamCastMode.NEVER);
        return settings;
    }

    protected final DSLContext ctx() {
        return ctx;
    }

}

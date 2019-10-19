package nl.lawinegevaar.yahoogroups.archiver;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;

import java.util.List;

@Slf4j
class Updater {

    private final DatabaseInfo databaseInfo;
    private final String cookieString;

    Updater(DatabaseInfo databaseInfo, String cookieString) {
        this.databaseInfo = databaseInfo;
        this.cookieString = cookieString;
    }

    void startUpdating() {
        log.info("Updating existing groups");
        List<YgroupRecord> yahooGroups;
        try (DatabaseAccess dao = new DatabaseAccess(databaseInfo)) {
            yahooGroups = dao.getYahooGroupsInformation();
        }
        yahooGroups
                .forEach(this::updateGroup);
    }

    private void updateGroup(YgroupRecord yahooGroupInformation) {
        try {
            new Scraper(databaseInfo, yahooGroupInformation.getGroupname(), cookieString)
                    .startScraping();
        } catch (ScrapingFailureException e) {
            // Most failures are caused by group archive not being publicly available
            log.error("Scrapping for group {} failed", yahooGroupInformation.getGroupname(), e);
        }
    }
}

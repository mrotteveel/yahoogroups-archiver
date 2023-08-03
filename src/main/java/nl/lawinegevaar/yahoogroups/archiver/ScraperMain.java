package nl.lawinegevaar.yahoogroups.archiver;

import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.common.AbstractApplication;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import org.apache.commons.cli.*;

/**
 * Scrapes data from Yahoo Groups
 */
@Slf4j
public final class ScraperMain extends AbstractApplication {

    private ScraperMain() {
        super("ScraperMain");
    }

    public static void main(String[] args) {
        new ScraperMain().run(args);
    }

    @Override
    protected void runApplication(CommandLine commandLine, DatabaseInfo databaseInfo) {
        String cookieString = commandLine.getOptionValue("c");
        if (commandLine.hasOption("u")) {
            boolean retryGaps = commandLine.hasOption("r");
            Updater updater = new Updater(databaseInfo, retryGaps, cookieString);
            updater.startUpdating();
        } else {
            String groupName = commandLine.getOptionValue("g");
            if (groupName == null || groupName.isEmpty()) {
                printUsage();
                return;
            }

            Scraper scraper = new Scraper(databaseInfo, groupName, cookieString);
            scraper.startScraping();
        }
    }

    @Override
    protected void addApplicationOptions(Options options) {
        options.addOption("g", "group", true, "Yahoo Group to scrape")
                .addOption("u", "update", false, "Update currently registered groups")
                .addOption("r", "retry-gaps", false, "When updating, retry existing gaps")
                .addOption("c", "cookie-string", true, "Cookie string");
    }

}

package nl.lawinegevaar.yahoogroups.archiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.lawinegevaar.yahoogroups.database.DatabaseInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.YgroupRecord;
import nl.lawinegevaar.yahoogroups.restclient.YahooGroupsClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jooq.Record2;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;

@Slf4j
class Scraper {

    private final OkHttpClient httpClient;
    private final Retrofit retrofit;
    private final YahooGroupsClient client;
    private final DatabaseInfo databaseInfo;
    private final String group;

    Scraper(DatabaseInfo databaseInfo, String group, String cookieString) {
        this.databaseInfo = databaseInfo;
        this.group = group;
        var httpClientBuilder = new OkHttpClient.Builder();
        if (cookieString != null) {
            httpClientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request authorized = original.newBuilder()
                        .addHeader("Cookie", cookieString)
                        .build();
                return chain.proceed(authorized);
            });
        }
        httpClient = httpClientBuilder.build();
        retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://groups.yahoo.com/")
                .build();

        client = retrofit.create(YahooGroupsClient.class);
    }

    void startScraping() {
        log.info("Start scraping {}", group);
        try (var dao = new DatabaseAccess(databaseInfo)) {
            YgroupRecord ygroup = dao.getOrCreateYahooGroupInformation(group);
            int groupId = ygroup.getId();
            log.info("GroupId: {}, Group: {}", groupId, ygroup.getGroupname());
            final int ygHighestMessageId = getHighestMessageIdFromYahooGroups();
            final int dbHighestMessageId = dao.getHighestMessageId(groupId);
            log.info("Highest message ids: Yahoo Groups: {}, Database: {}", ygHighestMessageId, dbHighestMessageId);

            if (ygHighestMessageId <= dbHighestMessageId) {
                log.info("All messages fetched, nothing to scrape");
                return;
            }

            for (int messageId = dbHighestMessageId + 1; messageId <= ygHighestMessageId; messageId++) {
                retrieveAndStoreMessage(dao, groupId, messageId);
                if (messageId % 100 == 0) {
                    log.info("Scraped message id {}", messageId);
                }
            }
        } finally {
            httpClient.connectionPool().evictAll();
            log.info("Stopped scraping");
        }
    }

    void retryGaps() {
        log.info("Retying gaps for {}", group);
        try (var dao = new DatabaseAccess(databaseInfo)) {
            YgroupRecord ygroup = dao.getYahooGroupInformation(group);
            int groupId = ygroup.getId();
            log.info("GroupId: {}, Group: {}", groupId, ygroup.getGroupname());
            try (Stream<Record2<Integer, Integer>> gapStream = dao.getGapsForGroupId(groupId)) {
                gapStream.forEach(gap -> {
                    int beforeGap = gap.component1();
                    int afterGap = gap.component2();
                    if (beforeGap >= afterGap) {
                        throw new IllegalStateException(
                                format("Invalid gap: before: %d, after %d", beforeGap, afterGap));
                    }
                    log.info("Retrying gap between {} and {}", beforeGap, afterGap);
                    for (int messageId = beforeGap + 1; messageId < afterGap; messageId++) {
                        retrieveAndStoreMessage(dao, groupId, messageId);
                    }
                });
            }
        } finally {
            httpClient.connectionPool().evictAll();
            log.info("Done retrying gaps");
        }
    }

    private void retrieveAndStoreMessage(DatabaseAccess dao, int groupId, int nextMessage) {
        Optional<String> message = getMessage(nextMessage);
        Optional<String> rawMessage = getRawMessage(nextMessage);
        if (message.isPresent() || rawMessage.isPresent()) {
            dao.insertRawdata(groupId, nextMessage, message.orElse(null), rawMessage.orElse(null));
        }
    }

    private Optional<String> getMessage(int messageId) {
        Call<ResponseBody> messageCall = client.getMessage(group, messageId);
        return getMessageBody(messageId, messageCall);
    }

    private Optional<String> getRawMessage(int messageId) {
        Call<ResponseBody> rawMessageCall = client.getRawMessage(group, messageId);
        return getMessageBody(messageId, rawMessageCall);
    }

    private Optional<String> getMessageBody(int messageId, Call<ResponseBody> responseBodyCall) {
        try {
            Response<ResponseBody> response = responseBodyCall.execute();
            if (response.isSuccessful()) {
                return Optional.of(response.body().string());
            } else if (response.code() == 404) {
                log.info("Message with id {} not found", messageId);
                return Optional.empty();
            } else if (response.code() == 500) {
                log.info("Message with id {} yields internal server error: {}",
                        messageId, response.errorBody().string());
                return Optional.empty();
            } else {
                // TODO Handle rate limiting?
                String error = response.errorBody().string();
                log.error("Error retrieving message with id {}: HTTP code:{}, message: {}",
                        messageId, response.code(), error);
                throw new ScrapingFailureException("Error retrieving message: " + error);
            }
        } catch (IOException e) {
            throw new ScrapingFailureException("Exception during request of id " + messageId, e);
        }
    }

    private int getHighestMessageIdFromYahooGroups() {
        try {
            Call<ResponseBody> latestMessageCall = client.getLatestMessage(group);
            Response<ResponseBody> response = latestMessageCall.execute();
            if (response.isSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                try (ResponseBody responseBody = response.body();
                     Reader reader = responseBody.charStream()) {
                    JsonNode documentRoot = mapper.readTree(reader);
                    return documentRoot.at("/ygData/lastRecordId").asInt();
                }
            } else {
                String error = response.errorBody().string();
                log.error("Unable to get highest message id: HTTP code: {}, message: {}", response.code(), error);
                throw new ScrapingFailureException("Could not retrieve highest message id");
            }
        } catch (IOException e) {
            throw new ScrapingFailureException("Could not retrieve highest message id", e);
        }
    }

}

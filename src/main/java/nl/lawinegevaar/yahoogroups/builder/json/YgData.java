package nl.lawinegevaar.yahoogroups.builder.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class YgData {

    private long userId;
    private String authorName;
    private String subject;
    private String postDate;
    private int msgId;
    private int prevInTopic;
    private int prevInTime;
    private int topicId;
    private String messageBody;
    private String rawEmail;

    public OffsetDateTime getOffsetPostDate() {
        long postDateEpochSeconds = Long.parseLong(postDate);
        Instant instant = Instant.ofEpochSecond(postDateEpochSeconds);
        return instant.atOffset(ZoneOffset.UTC);
    }

}

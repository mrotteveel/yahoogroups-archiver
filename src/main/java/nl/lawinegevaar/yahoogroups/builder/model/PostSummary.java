package nl.lawinegevaar.yahoogroups.builder.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.lawinegevaar.yahoogroups.builder.json.YgData;
import nl.lawinegevaar.yahoogroups.builder.json.YgMessage;

@Getter
@AllArgsConstructor
public final class PostSummary {

    private final int messageId;
    private final String subject;
    private final String authorName;

    public static PostSummary of(YgMessage message) {
        return of(message.getYgData());
    }

    public static PostSummary of(YgData data) {
        return new PostSummary(data.getMsgId(), data.getSubject(), data.getAuthorName());
    }
}

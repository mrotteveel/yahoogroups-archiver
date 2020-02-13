package nl.lawinegevaar.yahoogroups.builder.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.lawinegevaar.yahoogroups.builder.json.YgData;

@Getter
@AllArgsConstructor
public class PostSummary {

    private final int messageId;
    private final String subject;
    private final String authorName;

    public static PostSummary of(YgData data) {
        return new PostSummary(data.getMsgId(), data.getSubject(), data.getAuthorName());
    }
}

package nl.lawinegevaar.yahoogroups.builder.model;

import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.PostInformationRecord;

import java.time.LocalDateTime;

public final class PostInfo {

    private final PostInformationRecord postInformation;

    public PostInfo(PostInformationRecord postInformation) {
        this.postInformation = postInformation;
    }

    // Additional logic/derivation for templating

    public boolean isTopicStart() {
        return getMessageId() == getTopicId();
    }

    // Direct forwarding to postInformation

    public int getGroupId() {
        return postInformation.getGroupId();
    }

    public int getMessageId() {
        return postInformation.getMessageId();
    }

    public String getGroupname() {
        return postInformation.getGroupname();
    }

// Not forwarding postDate, as templating should use the OffsetDateTime generated in GroupBuilder
//    public LocalDateTime getPostDate() {
//        return postInformation.getPostDate();
//    }

    public short getPostYear() {
        return postInformation.getPostYear();
    }

    public short getPostMonth() {
        return postInformation.getPostMonth();
    }

    public int getTopicId() {
        return postInformation.getTopicId();
    }

    public short getTopicYear() {
        return postInformation.getTopicYear();
    }

    public short getTopicMonth() {
        return postInformation.getTopicMonth();
    }

    public Integer getPrevInTopic() {
        return postInformation.getPrevInTopic();
    }

    public Short getPrevInTopicYear() {
        return postInformation.getPrevInTopicYear();
    }

    public Short getPrevInTopicMonth() {
        return postInformation.getPrevInTopicMonth();
    }

    public Integer getNextInTopic() {
        return postInformation.getNextInTopic();
    }

    public Short getNextInTopicYear() {
        return postInformation.getNextInTopicYear();
    }

    public Short getNextInTopicMonth() {
        return postInformation.getNextInTopicMonth();
    }

    public Integer getPrevInTime() {
        return postInformation.getPrevInTime();
    }

    public Short getPrevInTimeYear() {
        return postInformation.getPrevInTimeYear();
    }

    public Short getPrevInTimeMonth() {
        return postInformation.getPrevInTimeMonth();
    }

    public Integer getNextInTime() {
        return postInformation.getNextInTime();
    }

    public Short getNextInTimeYear() {
        return postInformation.getNextInTimeYear();
    }

    public Short getNextInTimeMonth() {
        return postInformation.getNextInTimeMonth();
    }

// Not forwarding messageJson, as it shouldn't be used in templates
//    public String getMessageJson() {
//        return postInformation.getMessageJson();
//    }

}

/*
 * This file is generated by jOOQ.
 */
package nl.lawinegevaar.yahoogroups.database.jooq.tables.records;


import java.time.LocalDateTime;

import javax.annotation.processing.Generated;

import nl.lawinegevaar.yahoogroups.database.jooq.tables.LinkInfo;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LinkInfoRecord extends UpdatableRecordImpl<LinkInfoRecord> implements Record8<Integer, Integer, Integer, Integer, Integer, LocalDateTime, Short, Short> {

    private static final long serialVersionUID = 1945682350;

    /**
     * Setter for <code>LINK_INFO.GROUP_ID</code>.
     */
    public void setGroupId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINK_INFO.GROUP_ID</code>.
     */
    public Integer getGroupId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>LINK_INFO.MESSAGE_ID</code>.
     */
    public void setMessageId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINK_INFO.MESSAGE_ID</code>.
     */
    public Integer getMessageId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>LINK_INFO.Y_TOPIC_ID</code>.
     */
    public void setYTopicId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>LINK_INFO.Y_TOPIC_ID</code>.
     */
    public Integer getYTopicId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>LINK_INFO.Y_PREV_IN_TOPIC</code>.
     */
    public void setYPrevInTopic(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>LINK_INFO.Y_PREV_IN_TOPIC</code>.
     */
    public Integer getYPrevInTopic() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>LINK_INFO.Y_PREV_IN_TIME</code>.
     */
    public void setYPrevInTime(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>LINK_INFO.Y_PREV_IN_TIME</code>.
     */
    public Integer getYPrevInTime() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>LINK_INFO.POST_DATE</code>.
     */
    public void setPostDate(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>LINK_INFO.POST_DATE</code>.
     */
    public LocalDateTime getPostDate() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>LINK_INFO.POST_YEAR</code>.
     */
    public void setPostYear(Short value) {
        set(6, value);
    }

    /**
     * Getter for <code>LINK_INFO.POST_YEAR</code>.
     */
    public Short getPostYear() {
        return (Short) get(6);
    }

    /**
     * Setter for <code>LINK_INFO.POST_MONTH</code>.
     */
    public void setPostMonth(Short value) {
        set(7, value);
    }

    /**
     * Getter for <code>LINK_INFO.POST_MONTH</code>.
     */
    public Short getPostMonth() {
        return (Short) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Integer, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, LocalDateTime, Short, Short> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, LocalDateTime, Short, Short> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return LinkInfo.LINK_INFO.GROUP_ID;
    }

    @Override
    public Field<Integer> field2() {
        return LinkInfo.LINK_INFO.MESSAGE_ID;
    }

    @Override
    public Field<Integer> field3() {
        return LinkInfo.LINK_INFO.Y_TOPIC_ID;
    }

    @Override
    public Field<Integer> field4() {
        return LinkInfo.LINK_INFO.Y_PREV_IN_TOPIC;
    }

    @Override
    public Field<Integer> field5() {
        return LinkInfo.LINK_INFO.Y_PREV_IN_TIME;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return LinkInfo.LINK_INFO.POST_DATE;
    }

    @Override
    public Field<Short> field7() {
        return LinkInfo.LINK_INFO.POST_YEAR;
    }

    @Override
    public Field<Short> field8() {
        return LinkInfo.LINK_INFO.POST_MONTH;
    }

    @Override
    public Integer component1() {
        return getGroupId();
    }

    @Override
    public Integer component2() {
        return getMessageId();
    }

    @Override
    public Integer component3() {
        return getYTopicId();
    }

    @Override
    public Integer component4() {
        return getYPrevInTopic();
    }

    @Override
    public Integer component5() {
        return getYPrevInTime();
    }

    @Override
    public LocalDateTime component6() {
        return getPostDate();
    }

    @Override
    public Short component7() {
        return getPostYear();
    }

    @Override
    public Short component8() {
        return getPostMonth();
    }

    @Override
    public Integer value1() {
        return getGroupId();
    }

    @Override
    public Integer value2() {
        return getMessageId();
    }

    @Override
    public Integer value3() {
        return getYTopicId();
    }

    @Override
    public Integer value4() {
        return getYPrevInTopic();
    }

    @Override
    public Integer value5() {
        return getYPrevInTime();
    }

    @Override
    public LocalDateTime value6() {
        return getPostDate();
    }

    @Override
    public Short value7() {
        return getPostYear();
    }

    @Override
    public Short value8() {
        return getPostMonth();
    }

    @Override
    public LinkInfoRecord value1(Integer value) {
        setGroupId(value);
        return this;
    }

    @Override
    public LinkInfoRecord value2(Integer value) {
        setMessageId(value);
        return this;
    }

    @Override
    public LinkInfoRecord value3(Integer value) {
        setYTopicId(value);
        return this;
    }

    @Override
    public LinkInfoRecord value4(Integer value) {
        setYPrevInTopic(value);
        return this;
    }

    @Override
    public LinkInfoRecord value5(Integer value) {
        setYPrevInTime(value);
        return this;
    }

    @Override
    public LinkInfoRecord value6(LocalDateTime value) {
        setPostDate(value);
        return this;
    }

    @Override
    public LinkInfoRecord value7(Short value) {
        setPostYear(value);
        return this;
    }

    @Override
    public LinkInfoRecord value8(Short value) {
        setPostMonth(value);
        return this;
    }

    @Override
    public LinkInfoRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Integer value5, LocalDateTime value6, Short value7, Short value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LinkInfoRecord
     */
    public LinkInfoRecord() {
        super(LinkInfo.LINK_INFO);
    }

    /**
     * Create a detached, initialised LinkInfoRecord
     */
    public LinkInfoRecord(Integer groupId, Integer messageId, Integer yTopicId, Integer yPrevInTopic, Integer yPrevInTime, LocalDateTime postDate, Short postYear, Short postMonth) {
        super(LinkInfo.LINK_INFO);

        set(0, groupId);
        set(1, messageId);
        set(2, yTopicId);
        set(3, yPrevInTopic);
        set(4, yPrevInTime);
        set(5, postDate);
        set(6, postYear);
        set(7, postMonth);
    }
}

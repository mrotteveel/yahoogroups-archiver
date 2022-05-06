/*
 * This file is generated by jOOQ.
 */
package nl.lawinegevaar.yahoogroups.database.jooq.tables;


import java.time.LocalDateTime;

import nl.lawinegevaar.yahoogroups.database.jooq.DefaultSchema;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.PostInformationRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row22;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PostInformation extends TableImpl<PostInformationRecord> {

    private static final long serialVersionUID = 796536154;

    /**
     * The reference instance of <code>POST_INFORMATION</code>
     */
    public static final PostInformation POST_INFORMATION = new PostInformation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PostInformationRecord> getRecordType() {
        return PostInformationRecord.class;
    }

    /**
     * The column <code>POST_INFORMATION.GROUP_ID</code>.
     */
    public final TableField<PostInformationRecord, Integer> GROUP_ID = createField(DSL.name("GROUP_ID"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.MESSAGE_ID</code>.
     */
    public final TableField<PostInformationRecord, Integer> MESSAGE_ID = createField(DSL.name("MESSAGE_ID"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.GROUPNAME</code>.
     */
    public final TableField<PostInformationRecord, String> GROUPNAME = createField(DSL.name("GROUPNAME"), org.jooq.impl.SQLDataType.VARCHAR(50), this, "");

    /**
     * The column <code>POST_INFORMATION.POST_DATE</code>.
     */
    public final TableField<PostInformationRecord, LocalDateTime> POST_DATE = createField(DSL.name("POST_DATE"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>POST_INFORMATION.POST_YEAR</code>.
     */
    public final TableField<PostInformationRecord, Short> POST_YEAR = createField(DSL.name("POST_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.POST_MONTH</code>.
     */
    public final TableField<PostInformationRecord, Short> POST_MONTH = createField(DSL.name("POST_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.TOPIC_ID</code>.
     */
    public final TableField<PostInformationRecord, Integer> TOPIC_ID = createField(DSL.name("TOPIC_ID"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.TOPIC_YEAR</code>.
     */
    public final TableField<PostInformationRecord, Short> TOPIC_YEAR = createField(DSL.name("TOPIC_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.TOPIC_MONTH</code>.
     */
    public final TableField<PostInformationRecord, Short> TOPIC_MONTH = createField(DSL.name("TOPIC_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.PREV_IN_TOPIC</code>.
     */
    public final TableField<PostInformationRecord, Integer> PREV_IN_TOPIC = createField(DSL.name("PREV_IN_TOPIC"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.PREV_IN_TOPIC_YEAR</code>.
     */
    public final TableField<PostInformationRecord, Short> PREV_IN_TOPIC_YEAR = createField(DSL.name("PREV_IN_TOPIC_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.PREV_IN_TOPIC_MONTH</code>.
     */
    public final TableField<PostInformationRecord, Short> PREV_IN_TOPIC_MONTH = createField(DSL.name("PREV_IN_TOPIC_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.NEXT_IN_TOPIC</code>.
     */
    public final TableField<PostInformationRecord, Integer> NEXT_IN_TOPIC = createField(DSL.name("NEXT_IN_TOPIC"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.NEXT_IN_TOPIC_YEAR</code>.
     */
    public final TableField<PostInformationRecord, Short> NEXT_IN_TOPIC_YEAR = createField(DSL.name("NEXT_IN_TOPIC_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.NEXT_IN_TOPIC_MONTH</code>.
     */
    public final TableField<PostInformationRecord, Short> NEXT_IN_TOPIC_MONTH = createField(DSL.name("NEXT_IN_TOPIC_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.PREV_IN_TIME</code>.
     */
    public final TableField<PostInformationRecord, Integer> PREV_IN_TIME = createField(DSL.name("PREV_IN_TIME"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.PREV_IN_TIME_YEAR</code>.
     */
    public final TableField<PostInformationRecord, Short> PREV_IN_TIME_YEAR = createField(DSL.name("PREV_IN_TIME_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.PREV_IN_TIME_MONTH</code>.
     */
    public final TableField<PostInformationRecord, Short> PREV_IN_TIME_MONTH = createField(DSL.name("PREV_IN_TIME_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.NEXT_IN_TIME</code>.
     */
    public final TableField<PostInformationRecord, Integer> NEXT_IN_TIME = createField(DSL.name("NEXT_IN_TIME"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>POST_INFORMATION.NEXT_IN_TIME_YEAR</code>.
     */
    public final TableField<PostInformationRecord, Short> NEXT_IN_TIME_YEAR = createField(DSL.name("NEXT_IN_TIME_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.NEXT_IN_TIME_MONTH</code>.
     */
    public final TableField<PostInformationRecord, Short> NEXT_IN_TIME_MONTH = createField(DSL.name("NEXT_IN_TIME_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>POST_INFORMATION.MESSAGE_JSON</code>.
     */
    public final TableField<PostInformationRecord, String> MESSAGE_JSON = createField(DSL.name("MESSAGE_JSON"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>POST_INFORMATION</code> table reference
     */
    public PostInformation() {
        this(DSL.name("POST_INFORMATION"), null);
    }

    /**
     * Create an aliased <code>POST_INFORMATION</code> table reference
     */
    public PostInformation(String alias) {
        this(DSL.name(alias), POST_INFORMATION);
    }

    /**
     * Create an aliased <code>POST_INFORMATION</code> table reference
     */
    public PostInformation(Name alias) {
        this(alias, POST_INFORMATION);
    }

    private PostInformation(Name alias, Table<PostInformationRecord> aliased) {
        this(alias, aliased, null);
    }

    private PostInformation(Name alias, Table<PostInformationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"POST_INFORMATION\" as SELECT\n  a.GROUP_ID,\n  a.MESSAGE_ID,\n  a.GROUPNAME,\n  a.POST_DATE,\n  a.POST_YEAR,\n  a.POST_MONTH,\n  a.TOPIC_ID,\n  topic.POST_YEAR AS TOPIC_YEAR,\n  topic.POST_MONTH AS TOPIC_MONTH,\n  a.PREV_IN_TOPIC,\n  prev_in_topic.POST_YEAR AS PREV_IN_TOPIC_YEAR,\n  prev_in_topic.POST_MONTH AS PREV_IN_TOPIC_MONTH,\n  a.NEXT_IN_TOPIC,\n  next_in_topic.POST_YEAR AS NEXT_IN_TOPIC_YEAR,\n  next_in_topic.POST_MONTH AS NEXT_IN_TOPIC_MONTH,\n  a.PREV_IN_TIME,\n  prev_in_time.POST_YEAR AS PREV_IN_TIME_YEAR,\n  prev_in_time.POST_MONTH AS PREV_IN_TIME_MONTH,\n  a.NEXT_IN_TIME,\n  next_in_time.POST_YEAR AS NEXT_IN_TIME_YEAR,\n  next_in_time.POST_MONTH AS NEXT_IN_TIME_MONTH,\n  a.MESSAGE_JSON\nFROM (\n    SELECT \n      GROUP_ID, \n      MESSAGE_ID,\n      g.GROUPNAME,\n      -- Using the MESSAGE_ID as an indication of time ordering between messages, not the POST_DATE\n      FIRST_VALUE(MESSAGE_ID) OVER (PARTITION BY GROUP_ID, li.Y_TOPIC_ID ORDER BY MESSAGE_ID) AS TOPIC_ID,\n      LAG(MESSAGE_ID) OVER (PARTITION BY GROUP_ID, li.Y_TOPIC_ID ORDER BY MESSAGE_ID) AS PREV_IN_TOPIC,\n      LEAD(MESSAGE_ID) OVER (PARTITION BY GROUP_ID, li.Y_TOPIC_ID ORDER BY MESSAGE_ID) AS NEXT_IN_TOPIC,\n      LAG(MESSAGE_ID) OVER (PARTITION BY GROUP_ID ORDER BY MESSAGE_ID) AS PREV_IN_TIME,\n      LEAD(MESSAGE_ID) OVER (PARTITION BY GROUP_ID ORDER BY MESSAGE_ID) AS NEXT_IN_TIME,\n      POST_DATE,\n      POST_YEAR,\n      POST_MONTH,\n      r.MESSAGE_JSON\n    FROM LINK_INFO li\n    INNER JOIN RAWDATA r USING (GROUP_ID, MESSAGE_ID)\n    INNER JOIN YGROUP g ON g.ID = GROUP_ID\n) a\nINNER JOIN LINK_INFO topic ON topic.GROUP_ID = a.GROUP_ID AND topic.MESSAGE_ID = a.TOPIC_ID\nLEFT JOIN LINK_INFO prev_in_topic ON prev_in_topic.GROUP_ID = a.GROUP_ID AND prev_in_topic.MESSAGE_ID = a.PREV_IN_TOPIC\nLEFT JOIN LINK_INFO next_in_topic ON next_in_topic.GROUP_ID = a.GROUP_ID AND next_in_topic.MESSAGE_ID = a.NEXT_IN_TOPIC \nLEFT JOIN LINK_INFO prev_in_time ON prev_in_time.GROUP_ID = a.GROUP_ID AND prev_in_time.MESSAGE_ID = a.PREV_IN_TIME\nLEFT JOIN LINK_INFO next_in_time ON next_in_time.GROUP_ID = a.GROUP_ID AND next_in_time.MESSAGE_ID = a.NEXT_IN_TIME"));
    }

    public <O extends Record> PostInformation(Table<O> child, ForeignKey<O, PostInformationRecord> key) {
        super(child, key, POST_INFORMATION);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public PostInformation as(String alias) {
        return new PostInformation(DSL.name(alias), this);
    }

    @Override
    public PostInformation as(Name alias) {
        return new PostInformation(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PostInformation rename(String name) {
        return new PostInformation(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PostInformation rename(Name name) {
        return new PostInformation(name, null);
    }

    // -------------------------------------------------------------------------
    // Row22 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row22<Integer, Integer, String, LocalDateTime, Short, Short, Integer, Short, Short, Integer, Short, Short, Integer, Short, Short, Integer, Short, Short, Integer, Short, Short, String> fieldsRow() {
        return (Row22) super.fieldsRow();
    }
}

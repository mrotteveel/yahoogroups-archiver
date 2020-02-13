/*
 * This file is generated by jOOQ.
 */
package nl.lawinegevaar.yahoogroups.database.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import nl.lawinegevaar.yahoogroups.database.jooq.DefaultSchema;
import nl.lawinegevaar.yahoogroups.database.jooq.Keys;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.records.LinkInfoRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class LinkInfo extends TableImpl<LinkInfoRecord> {

    private static final long serialVersionUID = -446332881;

    /**
     * The reference instance of <code>LINK_INFO</code>
     */
    public static final LinkInfo LINK_INFO = new LinkInfo();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LinkInfoRecord> getRecordType() {
        return LinkInfoRecord.class;
    }

    /**
     * The column <code>LINK_INFO.GROUP_ID</code>.
     */
    public final TableField<LinkInfoRecord, Integer> GROUP_ID = createField(DSL.name("GROUP_ID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>LINK_INFO.MESSAGE_ID</code>.
     */
    public final TableField<LinkInfoRecord, Integer> MESSAGE_ID = createField(DSL.name("MESSAGE_ID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>LINK_INFO.Y_TOPIC_ID</code>.
     */
    public final TableField<LinkInfoRecord, Integer> Y_TOPIC_ID = createField(DSL.name("Y_TOPIC_ID"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>LINK_INFO.Y_PREV_IN_TOPIC</code>.
     */
    public final TableField<LinkInfoRecord, Integer> Y_PREV_IN_TOPIC = createField(DSL.name("Y_PREV_IN_TOPIC"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>LINK_INFO.Y_PREV_IN_TIME</code>.
     */
    public final TableField<LinkInfoRecord, Integer> Y_PREV_IN_TIME = createField(DSL.name("Y_PREV_IN_TIME"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>LINK_INFO.POST_DATE</code>.
     */
    public final TableField<LinkInfoRecord, LocalDateTime> POST_DATE = createField(DSL.name("POST_DATE"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>LINK_INFO.POST_YEAR</code>.
     */
    public final TableField<LinkInfoRecord, Short> POST_YEAR = createField(DSL.name("POST_YEAR"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>LINK_INFO.POST_MONTH</code>.
     */
    public final TableField<LinkInfoRecord, Short> POST_MONTH = createField(DSL.name("POST_MONTH"), org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * Create a <code>LINK_INFO</code> table reference
     */
    public LinkInfo() {
        this(DSL.name("LINK_INFO"), null);
    }

    /**
     * Create an aliased <code>LINK_INFO</code> table reference
     */
    public LinkInfo(String alias) {
        this(DSL.name(alias), LINK_INFO);
    }

    /**
     * Create an aliased <code>LINK_INFO</code> table reference
     */
    public LinkInfo(Name alias) {
        this(alias, LINK_INFO);
    }

    private LinkInfo(Name alias, Table<LinkInfoRecord> aliased) {
        this(alias, aliased, null);
    }

    private LinkInfo(Name alias, Table<LinkInfoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> LinkInfo(Table<O> child, ForeignKey<O, LinkInfoRecord> key) {
        super(child, key, LINK_INFO);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<LinkInfoRecord> getPrimaryKey() {
        return Keys.PK_LINK_INFO;
    }

    @Override
    public List<UniqueKey<LinkInfoRecord>> getKeys() {
        return Arrays.<UniqueKey<LinkInfoRecord>>asList(Keys.PK_LINK_INFO);
    }

    @Override
    public List<ForeignKey<LinkInfoRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LinkInfoRecord, ?>>asList(Keys.FK_LINK_INFO_RAWDATA);
    }

    public Rawdata rawdata() {
        return new Rawdata(this, Keys.FK_LINK_INFO_RAWDATA);
    }

    @Override
    public LinkInfo as(String alias) {
        return new LinkInfo(DSL.name(alias), this);
    }

    @Override
    public LinkInfo as(Name alias) {
        return new LinkInfo(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LinkInfo rename(String name) {
        return new LinkInfo(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LinkInfo rename(Name name) {
        return new LinkInfo(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, LocalDateTime, Short, Short> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}

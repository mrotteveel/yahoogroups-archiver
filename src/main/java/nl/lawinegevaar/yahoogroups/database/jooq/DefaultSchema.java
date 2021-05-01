/*
 * This file is generated by jOOQ.
 */
package nl.lawinegevaar.yahoogroups.database.jooq;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import nl.lawinegevaar.yahoogroups.database.jooq.tables.Dbversion;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.LinkInfo;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.PostInformation;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.Rawdata;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.SitemapLinks;
import nl.lawinegevaar.yahoogroups.database.jooq.tables.Ygroup;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 443948731;

    /**
     * The reference instance of <code></code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>DBVERSION</code>.
     */
    public final Dbversion DBVERSION = nl.lawinegevaar.yahoogroups.database.jooq.tables.Dbversion.DBVERSION;

    /**
     * The table <code>LINK_INFO</code>.
     */
    public final LinkInfo LINK_INFO = nl.lawinegevaar.yahoogroups.database.jooq.tables.LinkInfo.LINK_INFO;

    /**
     * The table <code>POST_INFORMATION</code>.
     */
    public final PostInformation POST_INFORMATION = nl.lawinegevaar.yahoogroups.database.jooq.tables.PostInformation.POST_INFORMATION;

    /**
     * The table <code>RAWDATA</code>.
     */
    public final Rawdata RAWDATA = nl.lawinegevaar.yahoogroups.database.jooq.tables.Rawdata.RAWDATA;

    /**
     * The table <code>SITEMAP_LINKS</code>.
     */
    public final SitemapLinks SITEMAP_LINKS = nl.lawinegevaar.yahoogroups.database.jooq.tables.SitemapLinks.SITEMAP_LINKS;

    /**
     * The table <code>YGROUP</code>.
     */
    public final Ygroup YGROUP = nl.lawinegevaar.yahoogroups.database.jooq.tables.Ygroup.YGROUP;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.SQL$DEFAULT);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Dbversion.DBVERSION,
            LinkInfo.LINK_INFO,
            PostInformation.POST_INFORMATION,
            Rawdata.RAWDATA,
            SitemapLinks.SITEMAP_LINKS,
            Ygroup.YGROUP);
    }
}

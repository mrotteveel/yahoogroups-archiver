package nl.lawinegevaar.yahoogroups.builder;

import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

final class SitemapGenerator {

    private static final int MAX_ELEMENTS = 50_000;

    private static final XMLOutputFactory xof = XMLOutputFactory.newInstance();
    public static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    private final List<String> sitemapFiles = new ArrayList<>();
    private final Path outputPath;
    private final String sitePrefix;
    private final PathWriterFunction pathWriterFunction;
    private int siteMapId = 0;
    private int currentLinkCount = 0;
    private XMLStreamWriter2 currentSiteMapWriter;

    SitemapGenerator(Path outputPath, String sitePrefix, PathWriterFunction pathWriterFunction) {
        this.outputPath = outputPath;
        this.sitePrefix = sitePrefix;
        this.pathWriterFunction = pathWriterFunction;
    }

    private XMLStreamWriter2 requireSitemapWriter() throws IOException, XMLStreamException {
        if (currentSiteMapWriter == null) {
            nextSitemapWriter();
        }
        return currentSiteMapWriter;
    }

    private void nextSitemapWriter() throws IOException, XMLStreamException {
        if (currentSiteMapWriter != null) {
            finishCurrentSiteMap();
        }

        String sitemapFilename = "sitemap-" + (++siteMapId) + ".xml";
        sitemapFiles.add(sitemapFilename);

        Path sitemapPath = outputPath.resolve(sitemapFilename);
        Writer writer = pathWriterFunction.getWriter(sitemapPath);

        XMLStreamWriter2 siteMapWriter = (XMLStreamWriter2) xof.createXMLStreamWriter(writer);
        siteMapWriter.writeStartDocument("UTF-8", "1.0");
        siteMapWriter.setDefaultNamespace(SITEMAP_NAMESPACE);
        siteMapWriter.writeCharacters("\n");
        siteMapWriter.writeStartElement("urlset");
        siteMapWriter.writeDefaultNamespace(SITEMAP_NAMESPACE);
        siteMapWriter.writeCharacters("\n");
        currentSiteMapWriter = siteMapWriter;
    }

    void addSitemapEntry(String page, LocalDateTime lastChange) {
        try {
            XMLStreamWriter2 siteMapWriter = requireSitemapWriter();

            siteMapWriter.writeStartElement("url");
            siteMapWriter.writeStartElement("loc");
            siteMapWriter.writeCharacters(sitePrefix + page);
            siteMapWriter.writeEndElement();
            siteMapWriter.writeStartElement("lastMod");
            siteMapWriter.writeCharacters(lastChange.format(DateTimeFormatter.ISO_DATE));
            siteMapWriter.writeEndElement();
            siteMapWriter.writeStartElement("changefreq");
            siteMapWriter.writeCharacters("never");
            siteMapWriter.writeEndElement();
            siteMapWriter.writeEndElement();
            siteMapWriter.writeCharacters("\n");
            currentLinkCount++;
            if (currentLinkCount >= MAX_ELEMENTS) {
                finishCurrentSiteMap();
                currentLinkCount = 0;
            }
        } catch (IOException | XMLStreamException e) {
            throw new ArchiveBuildingException("Unable to add sitemap entry", e);
        }
    }

    void createSitemapIndex() {
        try {
            if (currentSiteMapWriter != null) {
                finishCurrentSiteMap();
            }

            Path sitemapIndexPath = outputPath.resolve("sitemap-index.xml");

            try (Writer writer = pathWriterFunction.getWriter(sitemapIndexPath)) {
                XMLStreamWriter2 siteMapWriter = (XMLStreamWriter2) xof.createXMLStreamWriter(writer);
                try {
                    siteMapWriter.writeStartDocument("UTF-8", "1.0");
                    siteMapWriter.setDefaultNamespace(SITEMAP_NAMESPACE);
                    siteMapWriter.writeCharacters("\n");
                    siteMapWriter.writeStartElement("sitemapindex");
                    siteMapWriter.writeDefaultNamespace(SITEMAP_NAMESPACE);
                    siteMapWriter.writeCharacters("\n");
                    for (String sitemapFile : sitemapFiles) {
                        siteMapWriter.writeStartElement("sitemap");
                        siteMapWriter.writeStartElement("loc");
                        siteMapWriter.writeCharacters(sitePrefix + '/' + sitemapFile);
                        siteMapWriter.writeEndElement();
                        siteMapWriter.writeStartElement("lastmod");
                        siteMapWriter.writeCharacters(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        siteMapWriter.writeEndElement();
                        siteMapWriter.writeEndElement();
                        siteMapWriter.writeCharacters("\n");
                    }
                    siteMapWriter.writeEndDocument();
                } finally {
                    siteMapWriter.close();
                }
            }
        } catch (IOException | XMLStreamException e) {
            throw new ArchiveBuildingException("Unable to build sitemap index", e);
        }
    }

    private void finishCurrentSiteMap() throws XMLStreamException {
        XMLStreamWriter2 siteMapWriter = currentSiteMapWriter;
        if (siteMapWriter == null) {
            throw new IllegalStateException("No current site map writer to finish");
        }
        try {
            siteMapWriter.writeEndDocument();
            siteMapWriter.closeCompletely();
        } finally {
            currentSiteMapWriter = null;
        }
    }


}

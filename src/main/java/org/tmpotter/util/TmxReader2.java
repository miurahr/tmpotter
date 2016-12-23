/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015-2016 Hiroshi Miura
 *
 *  This file come from OmegaT project
 *
 *  Copyright (C) 2010 Alex Buloichik, 2012 Thomas Cordonnier,
 *                2013 Alex Buloichik, 2014 Aaron Madlon-Kay
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

package org.tmpotter.util;

import static org.tmpotter.util.Localization.getString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Helper for read TMX files, using StAX.
 * <p>
 * TMX 1.4b specification:
 * http://ttt.org/oscarstandards/tmx/
 *
 * @author Alex Buloichik
 * @author Hiroshi Miura
 */
public class TmxReader2 {

    private final XMLInputFactory factory;
    private final SimpleDateFormat dateFormat1;
    private final SimpleDateFormat dateFormat2;
    private final SimpleDateFormat dateFormatOut;

    /**
     * Segment Type attribute value: "paragraph".
     */
    public static final String SEG_PARAGRAPH = "paragraph";
    /**
     * Segment Type attribute value: "sentence".
     */
    public static final String SEG_SENTENCE = "sentence";
    /**
     * Creation Tool attribute value.
     */
    public static final String CT_APP = "tmpotter";

    private XMLEventReader xml;

    private boolean isParagraphSegtype = true;
    private boolean isTmPotter = false;
    private boolean extTmxLevel2;
    private boolean useSlash;
    private static final Logger LOGGER = LoggerFactory.getLogger(TmxReader2.class);

    ParsedTu currentTu = new ParsedTu();
    ParsedHeader header = new ParsedHeader();

    // buffers for parse texts
    StringBuilder propContent = new StringBuilder();
    StringBuilder noteContent = new StringBuilder();
    StringBuilder segContent = new StringBuilder();
    StringBuilder segInlineTag = new StringBuilder();
    InlineTagHandler inlineTagHandler = new InlineTagHandler();

    private Language sourceLanguage;
    private Language targetLanguage;

    /**
     * Constructor.
     */
    public TmxReader2() {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        factory.setXMLReporter((message, errorType, info, location) -> LOGGER.info(String
                .format("{0}:{1}", message, info)));
        factory.setXMLResolver(TMX_DTD_RESOLVER_2);
        dateFormat1 = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
        dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        dateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormatOut = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
        dateFormatOut.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Read TMX file.
     *
     * @param file           to read
     * @param defaultSourceLanguage source language such as EN-US
     * @param defaultTargetLanguage target language such as JA-JP
     * @param extTmxLevel2   frag to specify Tmx level2
     * @param useSlash       use slash for after tag such as &lt;tag/&gt;
     * @param callback       callback to accept result
     * @throws java.lang.Exception when file read fails.
     */
    public void readTmx(File file, final Language defaultSourceLanguage,
                        final Language defaultTargetLanguage,
                        final boolean extTmxLevel2,
                        final boolean useSlash,
                        final LoadCallback callback) throws Exception {
        this.extTmxLevel2 = extTmxLevel2;
        this.useSlash = useSlash;
        this.sourceLanguage = defaultSourceLanguage;
        this.targetLanguage = defaultTargetLanguage;

        // log the parsing attempt
        LOGGER.info(String.format("%s: %s", getString("TMXR.INFO.READING_FILE"),
                file.getAbsolutePath()));

        boolean allFound = true;

        InputStream in;
        if (file.getName().endsWith(".gz")) {
            in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
        } else {
            in = new BufferedInputStream(new FileInputStream(file));
        }
        xml = factory.createXMLEventReader(in);
        try {
            while (xml.hasNext()) {
                XMLEvent evt = xml.nextEvent();
                switch (evt.getEventType()) {
                    case XMLEvent.START_ELEMENT:
                        StartElement eleStart = (StartElement) evt;
                        if ("tu".equals(eleStart.getName().getLocalPart())) {
                            parseTu(eleStart);
                            ParsedTuv origTuv = getTuvByLang(sourceLanguage);
                            ParsedTuv targetTuv = getTuvByLang(targetLanguage);
                            allFound &= callback.onEntry(currentTu, origTuv, targetTuv,
                                    isParagraphSegtype);
                        } else if ("header".equals(eleStart.getName().getLocalPart())) {
                            parseHeader(eleStart);
                        }
                        break;
                    default:
                        break;
                }
            }
        } finally {
            xml.close();
            in.close();
        }

        if (!allFound) {
            LOGGER.info(String.format("%s:%s %s", "TMXReader2", "readTMX",
                    "TMXR.WARNING.SOURCE_NOT_FOUND"));
        }
        LOGGER.info(String.format("%s:%s %s", "TMXReader2", "readTMX",
                  "TMXR.INFO.READING_COMPLETE"));
    }

    protected void parseHeader(StartElement element) throws Exception {
        isParagraphSegtype = SEG_PARAGRAPH.equals(getAttributeValue(element, "segtype"));
        isTmPotter = CT_APP.equals(getAttributeValue(element, "creationtool"));

        // log some details
        LOGGER.info(String.format("%s %s",
                getString("TMXR.INFO.CREATION_TOOL"), getAttributeValue(element,
                "creationtool")));
        LOGGER.info(String.format("%s %s",
                getString("TMXR.INFO.CREATION_TOOL_VERSION"), getAttributeValue(element,
                "creationtoolversion")));
        LOGGER.info(String.format("%s %s",
                getString("TMXR.INFO.SEG_TYPE"), getAttributeValue(element, "segtype")));
        LOGGER.info(String.format("%s %s",
                getString("TMXR.INFO.SOURCE_LANG"), getAttributeValue(element, "srclang")));

        // give a warning if the TMX source language is
        // different from the project source language
        String tmxSourceLanguage = getAttributeValue(element, "srclang");
        if (!tmxSourceLanguage.equalsIgnoreCase(sourceLanguage.getLanguage())) {
            LOGGER.info(String.format("%s %s %s",
                    getString("TMXR.WARNING.INCORRECT_SOURCE_LANG"), tmxSourceLanguage,
                    sourceLanguage));
            sourceLanguage = new Language(tmxSourceLanguage);
        }
        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    StartElement eleStart = (StartElement) evt;
                    if ("prop".equals(eleStart.getName().getLocalPart())) {
                        parseHeaderProp(eleStart);
                    } else if ("note".equals(eleStart.getName().getLocalPart())) {
                        parseHeaderNote(eleStart);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("header".equals(eleEnd.getName().getLocalPart())) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Parse prop elements in Header.
     *
     * @param element XML element.
     * @throws Exception when error happend.
     */
    protected void parseHeaderProp(StartElement element) throws Exception {
        String propType = getAttributeValue(element, "type");
        propContent.setLength(0);

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("prop".equals(eleEnd.getName().getLocalPart())) {
                        header.props.put(propType, propContent.toString());
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void parseHeaderNote(StartElement element) throws Exception {
        noteContent.setLength(0);

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("note".equals(eleEnd.getName().getLocalPart())) {
                        header.note = noteContent.toString();
                        return;
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    Characters ch = (Characters) evt;
                    noteContent.append(ch.getData());
                    break;
                default:
                    break;
            }
        }
    }


 
    protected void parseTu(StartElement element) throws Exception {
        currentTu.clear();

        currentTu.changeid = getAttributeValue(element, "changeid");
        currentTu.changedate = parseIso8601date(getAttributeValue(element, "changedate"));
        currentTu.creationid = getAttributeValue(element, "creationid");
        currentTu.creationdate = parseIso8601date(getAttributeValue(element, "creationdate"));

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    StartElement eleStart = (StartElement) evt;
                    if ("tuv".equals(eleStart.getName().getLocalPart())) {
                        parseTuv(eleStart);
                    } else if ("prop".equals(eleStart.getName().getLocalPart())) {
                        parseTuProp(eleStart);
                    } else if ("note".equals(eleStart.getName().getLocalPart())) {
                        parseTuNote(eleStart);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("tu".equals(eleEnd.getName().getLocalPart())) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void parseTuv(StartElement element) throws Exception {
        ParsedTuv tuv = new ParsedTuv();
        currentTu.tuvs.add(tuv);

        tuv.changeid = getAttributeValue(element, "changeid");
        tuv.changedate = parseIso8601date(getAttributeValue(element, "changedate"));
        tuv.creationid = getAttributeValue(element, "creationid");
        tuv.creationdate = parseIso8601date(getAttributeValue(element, "creationdate"));

        // find 'lang' or 'xml:lang' attribute
        for (Iterator<Attribute> it = element.getAttributes(); it.hasNext(); ) {
            Attribute att = it.next();
            if ("lang".equals(att.getName().getLocalPart())) {
                tuv.lang = att.getValue();
                break;
            }
        }

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    StartElement eleStart = (StartElement) evt;
                    if ("seg".equals(eleStart.getName().getLocalPart())) {
                        if (isTmPotter) {
                            parseSegOmegaT();
                        } else if (extTmxLevel2) {
                            parseSegExtLevel2();
                        } else {
                            parseSegExtLevel1();
                        }
                        tuv.text = StringUtil.normalizeUnicode(segContent);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("tuv".equals(eleEnd.getName().getLocalPart())) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void parseTuNote(StartElement element) throws Exception {
        noteContent.setLength(0);

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("note".equals(eleEnd.getName().getLocalPart())) {
                        currentTu.note = noteContent.toString();
                        return;
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    Characters ch = (Characters) evt;
                    noteContent.append(ch.getData());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Parse prop elements in Tu element.
     *
     * @param element XML element.
     * @throws Exception when error happened.
     */
    protected void parseTuProp(StartElement element) throws Exception {
        String propType = getAttributeValue(element, "type");
        propContent.setLength(0);

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("prop".equals(eleEnd.getName().getLocalPart())) {
                        currentTu.props.put(propType, propContent.toString());
                        return;
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    Characters ch = (Characters) evt;
                    propContent.append(ch.getData());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * OmegaT TMX - just read full text.
     */
    protected void parseSegOmegaT() throws Exception {
        segContent.setLength(0);

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = (EndElement) evt;
                    if ("seg".equals(eleEnd.getName().getLocalPart())) {
                        return;
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    Characters ch = (Characters) evt;
                    segContent.append(ch.getData());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * External TMX - level 1. Skip text inside inline tags.
     */
    protected void parseSegExtLevel1() throws Exception {
        segContent.setLength(0);

        int inlineLevel = 0;

        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    inlineLevel++;
                    break;
                case XMLEvent.END_ELEMENT:
                    inlineLevel--;
                    EndElement eleEnd = (EndElement) evt;
                    if ("seg".equals(eleEnd.getName().getLocalPart())) {
                        return;
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    if (inlineLevel == 0) {
                        Characters ch = (Characters) evt;
                        segContent.append(ch.getData());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * External TMX - level 2. Replace all tags into shortcuts.
     */
    protected void parseSegExtLevel2() throws Exception {
        segContent.setLength(0);
        segInlineTag.setLength(0);
        inlineTagHandler.reset();

        int inlineLevel = 0;
        while (true) {
            XMLEvent evt = xml.nextEvent();
            switch (evt.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    StartElement eleStart = evt.asStartElement();
                    if ("hi".equals(eleStart.getName().getLocalPart())) {
                        // tag should be skipped
                        break;
                    }
                    inlineLevel++;
                    segInlineTag.setLength(0);
                    if ("bpt".equals(eleStart.getName().getLocalPart())) {
                        inlineTagHandler.startBpt(getAttributeValue(eleStart, "i"),
                                getAttributeValue(eleStart, "x"));
                        inlineTagHandler.setTagShortcutLetter(StringUtil
                                .getFirstLetterLowercase(getAttributeValue(eleStart,
                                "type")));
                    } else if ("ept".equals(eleStart.getName().getLocalPart())) {
                        inlineTagHandler.startEpt(getAttributeValue(eleStart, "i"));
                    } else if ("it".equals(eleStart.getName().getLocalPart())) {
                        inlineTagHandler.startOther();
                        inlineTagHandler.setOtherTagShortcutLetter(StringUtil
                                .getFirstLetterLowercase(getAttributeValue(eleStart,
                                "type")));
                        inlineTagHandler.setCurrentPos(getAttributeValue(eleStart, "pos"));
                    } else if ("ph".equals(eleStart.getName().getLocalPart())) {
                        inlineTagHandler.startOther();
                        inlineTagHandler.setOtherTagShortcutLetter(StringUtil
                                .getFirstLetterLowercase(getAttributeValue(eleStart,
                                "type")));
                    } else {
                        inlineTagHandler.startOther();
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    EndElement eleEnd = evt.asEndElement();
                    if ("hi".equals(eleEnd.getName().getLocalPart())) {
                        // tag should be skipped
                        break;
                    }
                    inlineLevel--;
                    if ("seg".equals(eleEnd.getName().getLocalPart())) {
                        return;
                    }
                    boolean slashBefore = false;
                    boolean slashAfter = false;
                    int tagName = StringUtil.getFirstLetterLowercase(segInlineTag.toString());
                    Integer tagN;
                    if ("bpt".equals(eleEnd.getName().getLocalPart())) {
                        if (tagName != 0) {
                            inlineTagHandler.setTagShortcutLetter(tagName);
                        } else {
                            tagName = inlineTagHandler.getTagShortcutLetter();
                        }
                        tagN = inlineTagHandler.endBpt();
                    } else if ("ept".equals(eleEnd.getName().getLocalPart())) {
                        slashBefore = true;
                        tagName = inlineTagHandler.getTagShortcutLetter();
                        tagN = inlineTagHandler.endEpt();
                    } else if ("it".equals(eleEnd.getName().getLocalPart())) {
                        if (tagName != 0) {
                            inlineTagHandler.setOtherTagShortcutLetter(tagName);
                        } else {
                            tagName = inlineTagHandler.getOtherTagShortcutLetter();
                        }
                        tagN = inlineTagHandler.endOther();
                        if ("end".equals(inlineTagHandler.getCurrentPos())) {
                            slashBefore = true;
                        }
                    } else if ("ph".equals(eleEnd.getName().getLocalPart())) {
                        if (tagName != 0) {
                            inlineTagHandler.setOtherTagShortcutLetter(tagName);
                        } else {
                            tagName = inlineTagHandler.getOtherTagShortcutLetter();
                        }
                        tagN = inlineTagHandler.endOther();
                        if (useSlash) {
                            slashAfter = true;
                        }
                    } else {
                        tagN = inlineTagHandler.endOther();
                        if (useSlash) {
                            slashAfter = true;
                        }
                    }
                    if (tagName == 0) {
                        tagName = 'f';
                    }
                    if (tagN == null) {
                        // check error of TMX reading
                        LOGGER.info(getString("TMX.ERROR.READING_LEVEL2"));
                        segContent.setLength(0);
                        // wait for end seg
                        while (true) {
                            XMLEvent ev = xml.nextEvent();
                            switch (ev.getEventType()) {
                                case XMLEvent.END_ELEMENT:
                                    EndElement evEnd = (EndElement) ev;
                                    if ("seg".equals(evEnd.getName().getLocalPart())) {
                                        return;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    segContent.append('<');
                    if (slashBefore) {
                        segContent.append('/');
                    }
                    segContent.appendCodePoint(tagName);
                    segContent.append(Integer.toString(tagN));
                    if (slashAfter) {
                        segContent.append('/');
                    }
                    segContent.append('>');
                    break;
                case XMLEvent.CHARACTERS:
                    Characters ch = (Characters) evt;
                    if (inlineLevel == 0) {
                        segContent.append(ch.getData());
                    } else {
                        segInlineTag.append(ch.getData());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Get ParsedTuv from list of Tuv for specific language.
     * <p>
     * Language choosed by:<br>
     * - with the same language+country<br>
     * - if not exist, then with the same language but without country<br>
     * - if not exist, then with the same language with whatever country<br>
     */
    protected ParsedTuv getTuvByLang(Language lang) {
        String langLanguage = lang.getLanguageCode();
        String langCountry = lang.getCountryCode();
        ParsedTuv tuvLc = null; // Tuv with the same language+country
        ParsedTuv tuvL = null; // Tuv with the same language only, without country
        ParsedTuv tuvLw = null; // Tuv with the same language+whatever country
        for (int i = 0; i < currentTu.tuvs.size(); i++) {
            ParsedTuv tuv = currentTu.tuvs.get(i);
            String tuvLang = tuv.lang;
            if (!langLanguage.regionMatches(true, 0, tuvLang, 0, 2)) {
                // language not equals - there is no sense to processing
                continue;
            }
            if (tuvLang.length() < 3) {
                // language only, without country
                tuvL = tuv;
            } else if (langCountry.regionMatches(true, 0, tuvLang, 3, 2)) {
                // the same country
                tuvLc = tuv;
            } else {
                // other country
                tuvLw = tuv;
            }
        }
        ParsedTuv bestTuv;
        if (tuvLc != null) {
            bestTuv = tuvLc;
        } else if (tuvL != null) {
            bestTuv = tuvL;
        } else {
            bestTuv = tuvLw;
        }
        return bestTuv;
    }

    /**
     * Parse ISO8601 style date format.
     *
     * @param str date format
     * @return date number from epoch
     */
    public long parseIso8601date(String str) {
        if (str == null) {
            return 0;
        }
        try {
            return dateFormat1.parse(str).getTime();
        } catch (ParseException ex) {
            // FIXME
        }
        try {
            return dateFormat2.parse(str).getTime();
        } catch (ParseException ex) {
            // FIXME
        }

        return 0;
    }

    private static String getAttributeValue(StartElement ele, String attrName) {
        Attribute attr = ele.getAttributeByName(new QName(attrName));
        return attr != null ? attr.getValue() : null;
    }

    /**
     * Callback for receive data from TMX.
     */
    public interface LoadCallback {

        /**
         * callback function onEntry.
         *
         * @param tu                 Parsed TU tag
         * @param tuvSource          source attribute of TUV
         * @param tuvTarget          target attribute of TUV
         * @param isParagraphSegtype boolean to indicate paragraph seg type
         * @return true if TU contains required source and target info
         */
        boolean onEntry(ParsedTu tu, ParsedTuv tuvSource, ParsedTuv tuvTarget,
                        boolean isParagraphSegtype);
    }

    public static class ParsedHeader {

        public String creationtool;
        public String creationtoolversion;
        public String datatype;
        public String segtype;
        public String adminlang;
        public String srclang;
        public String o_tmf;
        public Map<String, String> props = new TreeMap<>();
        public String note;

        void clear() {
            creationtool = null;
            creationtoolversion = null;
            datatype = null;
            segtype = null;
            adminlang = null;
            srclang = null;
            o_tmf = null;
            props = new TreeMap<>();
            note = null;
        }
    }

    public static class ParsedTu {

        public String changeid;
        public long changedate;
        public String creationid;
        public long creationdate;
        public String note;
        public Map<String, String> props = new TreeMap<>();
        public List<ParsedTuv> tuvs = new ArrayList<>();

        void clear() {
            changeid = null;
            changedate = 0;
            creationid = null;
            creationdate = 0;
            props = new TreeMap<>(); // do not CLEAR, because it may be shared
            tuvs = new ArrayList<>();
            note = null;
        }
    }

    public static class ParsedTuv {

        public String lang;
        public String changeid;
        public long changedate;
        public String creationid;
        public long creationdate;
        public String text;
    }

    public static final EntityResolver TMX_DTD_RESOLVER = new EntityResolver() {
        @Override
        public InputSource resolveEntity(String publicId,
                                         String systemId) throws SAXException, IOException {
            if (systemId.endsWith("tmx11.dtd")) {
                return new InputSource(TmxReader2.class
                    .getResourceAsStream("/schemas/tmx11.dtd"));
            } else if (systemId.endsWith("tmx14.dtd")) {
                return new InputSource(TmxReader2.class
                    .getResourceAsStream("/schemas/tmx14.dtd"));
            } else {
                return null;
            }
        }
    };

    public static final XMLResolver TMX_DTD_RESOLVER_2 = new XMLResolver() {
        @Override
        public Object resolveEntity(String publicId, String systemId,
                                    String baseUri, String namespace) throws XMLStreamException {
            if (systemId.endsWith("tmx11.dtd")) {
                return TmxReader2.class.getResourceAsStream("/schemas/tmx11.dtd");
            } else if (systemId.endsWith("tmx14.dtd")) {
                return TmxReader2.class.getResourceAsStream("/schemas/tmx14.dtd");
            } else {
                return null;
            }
        }
    };
}

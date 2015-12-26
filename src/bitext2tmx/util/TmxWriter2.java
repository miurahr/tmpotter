/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  This file come from OmegaT project
 *
 *   Copyright (C) 2006 Henry Pijffers
 *              2010 Alex Buloichik
 *              2011 Alex Buloichik, Martin Fleurke
 *              2012 Alex Buloichik, Didier Briel
 *              2013 Aaron Madlon-Kay
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package bitext2tmx.util;

import bitext2tmx.core.TmxEntry;
import bitext2tmx.util.xml.XMLUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;


/**
 * Helper for write TMX files, using StAX.
 * 
 * We can't use JAXB for writing because it changes spaces on formatted output.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Martin Fleurke
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 */
public class TmxWriter2 {

  private static final XMLOutputFactory FACTORY;

  private final OutputStream out;
  private final XMLStreamWriter xml;

  private final String langSrc;
  private final String langTar;
  private final boolean levelTwo;
  private final boolean forceValidTmx;

  /* Segment Type attribute value: "paragraph" */
  public static final String SEG_PARAGRAPH = "paragraph";
  /* Segment Type attribute value: "sentence" */
  public static final String SEG_SENTENCE = "sentence";

  /**
   * DateFormat with format YYYYMMDDThhmmssZ able to display a date in UTC time.
   *
   * SimpleDateFormat IS NOT THREAD SAFE !!!
   */
  private final SimpleDateFormat tmxDateFormat;

  static {
    FACTORY = XMLOutputFactory.newInstance();
  }

  /**
   * TmxWriter2 write TMX file.
   * 
   * @param file to be written
   * @param sourceLanguage of data
   * @param targetLanguage of data, only one target allowed
   * @param sentenceSegmentingEnabled is sentence segmentaion or paragraph.
   * @param levelTwo When true, the tmx is made compatible with level 2 (TMX
   *     version 1.4)
   * @param forceValidTmx force to remove tags from element text 
   * @throws Exception when file write error.
   */
  public TmxWriter2(File file, final Language sourceLanguage,
          final Language targetLanguage, boolean sentenceSegmentingEnabled,
          boolean levelTwo, boolean forceValidTmx) throws Exception {
    this.levelTwo = levelTwo;
    this.forceValidTmx = forceValidTmx;

    out = new BufferedOutputStream(new FileOutputStream(file));
    xml = FACTORY.createXMLStreamWriter(out, AppConstants.ENCODINGS_UTF8);

    xml.writeStartDocument(AppConstants.ENCODINGS_UTF8, "1.0");
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);

    if (levelTwo) {
      xml.writeDTD("<!DOCTYPE tmx SYSTEM \"tmx14.dtd\">");
      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
      xml.writeStartElement("tmx");
      xml.writeAttribute("version", "1.4");
    } else {
      xml.writeDTD("<!DOCTYPE tmx SYSTEM \"tmx11.dtd\">");
      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
      xml.writeStartElement("tmx");
      xml.writeAttribute("version", "1.1");
    }
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);

    writeHeader(sourceLanguage, targetLanguage, sentenceSegmentingEnabled);

    xml.writeCharacters("  ");
    xml.writeStartElement("body");
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);

    langSrc = sourceLanguage.toString();
    langTar = targetLanguage.toString();

    tmxDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
    tmxDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  /**
   * Close tmx file.
   * 
   * @throws Exception when io error
   */
  public void close() throws Exception {
    try {
      xml.writeCharacters("  ");
      xml.writeEndElement(); // body

      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
      xml.writeEndElement(); // tmx

      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
      xml.writeEndDocument();
    } finally {
      xml.close();
      out.close();
    }
  }

  public void writeComment(String comment) throws Exception {
    xml.writeComment(comment);
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);
  }

  /**
   * Write one entry.
   *
   * @param source entry
   * @param translation entry
   * @param entry entry arguments
   * @param properties property name and values
   * @throws java.lang.Exception when file write error occord.
   */
  public void writeEntry(String source, String translation,
          TmxEntry entry, Map<String,String> properties) throws Exception {
    xml.writeCharacters("    ");
    xml.writeStartElement("tu");
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);

    // add properties
    if (properties != null) {
      for (Map.Entry<String, String> ent: properties.entrySet()) {
        xml.writeCharacters("      ");
        xml.writeStartElement("prop");
        xml.writeAttribute("type", ent.getKey());
        xml.writeCharacters(ent.getValue());
        xml.writeEndElement();
        xml.writeCharacters(FileUtil.LINE_SEPARATOR);
      }
    }

    // add note
    if (entry.note != null && !entry.note.equals("")) {
      String note = XMLUtil.removeXMLInvalidChars(entry.note);
      if (forceValidTmx) {
        note = XMLUtil.stripXmlTags(note);
      }
      xml.writeCharacters("      ");
      xml.writeStartElement("note");
      xml.writeCharacters(platformLineSeparator(note));
      xml.writeEndElement(); // note
      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
    }

    // write source segment
    source = XMLUtil.removeXMLInvalidChars(source);
    if (forceValidTmx) {
      source = XMLUtil.stripXmlTags(source);
    }
    xml.writeCharacters("      ");
    xml.writeStartElement("tuv");
    if (levelTwo) {
      xml.writeAttribute("xml", "", "lang", langSrc);
    } else {
      xml.writeAttribute("lang", langSrc);
    }
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);
    if (levelTwo) {
      writeLevelTwo(platformLineSeparator(source));
    } else {
      writeLevelOne(platformLineSeparator(source));
    }
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);
    xml.writeCharacters("      ");
    xml.writeEndElement(); // tuv
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);

    // write target segment
    if (translation != null) {
      translation = XMLUtil.removeXMLInvalidChars(translation);
      if (forceValidTmx) {
        translation = XMLUtil.stripXmlTags(translation);
      }

      xml.writeCharacters("      ");
      xml.writeStartElement("tuv");
      if (levelTwo) {
        xml.writeAttribute("xml", "", "lang", langTar);
      } else {
        xml.writeAttribute("lang", langTar);
      }
      if (!StringUtil.isEmpty(entry.changer)) {
        xml.writeAttribute("changeid", entry.changer);
      }
      if (entry.changeDate > 0) {
        xml.writeAttribute("changedate", tmxDateFormat.format(new Date(entry.changeDate)));
      }
      if (!StringUtil.isEmpty(entry.creator)) {
        xml.writeAttribute("creationid", entry.creator);
      }
      if (entry.creationDate > 0) {
        xml.writeAttribute("creationdate", tmxDateFormat.format(new Date(entry.creationDate)));
      }
      xml.writeCharacters(FileUtil.LINE_SEPARATOR);

      if (levelTwo) {
        writeLevelTwo(platformLineSeparator(translation));
      } else {
        writeLevelOne(platformLineSeparator(translation));
      }
      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
      xml.writeCharacters("      ");
      xml.writeEndElement(); // tuv
      xml.writeCharacters(FileUtil.LINE_SEPARATOR);
    }

    xml.writeCharacters("    ");
    xml.writeEndElement(); // tu
    xml.writeCharacters(FileUtil.LINE_SEPARATOR);
  }

  private void writeHeader(final Language sourceLanguage, final Language targetLanguage,
          boolean sentenceSegmentingEnabled) throws Exception {
    xml.writeCharacters("  ");
    xml.writeEmptyElement("header");

    xml.writeAttribute("creationtool", AppConstants.getApplicationDisplayName());
    xml.writeAttribute("o-tmf", "bitext2tmx TMX");
    xml.writeAttribute("adminlang", "EN-US");
    xml.writeAttribute("datatype", "plaintext");

    xml.writeAttribute("creationtoolversion", AppConstants.getVersion());

    xml.writeAttribute("segtype", sentenceSegmentingEnabled ? SEG_SENTENCE
            : SEG_PARAGRAPH);

    xml.writeAttribute("srclang", sourceLanguage.toString());

    xml.writeCharacters(FileUtil.LINE_SEPARATOR);
  }

  /**
   * Create simple segment.
   */
  private void writeLevelOne(String segment) throws Exception {
    xml.writeCharacters("        ");
    xml.writeStartElement("seg");
    xml.writeCharacters(segment);
    xml.writeEndElement();
  }

  protected static final Pattern TAGSANY = Pattern.compile("<(/?)([\\S&&[^/\\d]]+)(\\d+)(/?)>");

  enum TagType { SINGLE, START, END }

  private void writeLevelTwo(String segment) throws Exception {
    xml.writeCharacters("        ");
    xml.writeStartElement("seg");

    TagType tagType;
    int pos = 0;
    Matcher mat = TAGSANY.matcher(segment);
    while (true) {
      if (!mat.find(pos)) {
        break;
      }
      xml.writeCharacters(segment.substring(pos, mat.start()));
      pos = mat.end();

      if (!mat.group(1).isEmpty()) {
        tagType = TagType.END;
      } else if (!mat.group(4).isEmpty()) {
        tagType = TagType.SINGLE;
      } else {
        tagType = TagType.START;
      }

      String tagName = mat.group(2);
      String tagNumber = mat.group(3);

      switch (tagType) {
        case SINGLE:
          xml.writeStartElement("ph");
          xml.writeAttribute("x", tagNumber);
          xml.writeCharacters(mat.group());
          xml.writeEndElement();
          break;
        case START:
          String endTag = "</" + tagName + tagNumber + ">";
          if (segment.contains(endTag)) {
            xml.writeStartElement("bpt");
            xml.writeAttribute("i", tagNumber);
            xml.writeAttribute("x", tagNumber);
            xml.writeCharacters(mat.group());
            xml.writeEndElement();
          } else {
            xml.writeStartElement("it");
            xml.writeAttribute("pos", "begin");
            xml.writeAttribute("x", tagNumber);
            xml.writeCharacters(mat.group());
            xml.writeEndElement();
          }
          break;
        case END:
          String startTag = "<" + tagName + tagNumber + ">";
          if (segment.contains(startTag)) {
            xml.writeStartElement("ept");
            xml.writeAttribute("i", tagNumber);
            xml.writeCharacters(mat.group());
            xml.writeEndElement();
          } else {
            xml.writeStartElement("it");
            xml.writeAttribute("pos", "end");
            xml.writeAttribute("x", tagNumber);
            xml.writeCharacters(mat.group());
            xml.writeEndElement();
          }
          break;
        default:
          throw new RuntimeException("Unknow tag type");
      }
    }

    xml.writeCharacters(segment.substring(pos));

    xml.writeEndElement();
  }

  /**
   * Replaces \n with platform specific end of lines.
   *
   * @param text The string to be converted
   * @return The converted string
   */
  private String platformLineSeparator(String text) {
    return text.replace("\n", FileUtil.LINE_SEPARATOR);
  }
}

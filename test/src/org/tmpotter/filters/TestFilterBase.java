/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  Copyright (C) 2008 Alex Buloichik
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
 * ************************************************************************/

package org.tmpotter.filters;

import static junit.framework.Assert.*;

import org.tmpotter.util.Language;
import org.tmpotter.util.TmxReader2;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


/**
 * Base class for test filter parsing.
 *
 * @author Alex Buloichik <alex73mail@gmail.com>
 * @author Hiroshi Miura
 */
@SuppressWarnings("unchecked")
public abstract class TestFilterBase extends XMLTestCase {

  protected FilterContext context = new FilterContext(new Language("en"), new Language("be"), false);

  protected File outTestFile;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    outTestFile = new File("build/testdata/tmpotter_test-" + getClass().getName() + "-" + getName());
    outTestFile.getParentFile().mkdirs();
  }

  protected List<String> parse(AbstractFilter filter, String filename) throws Exception {
    final List<String> result = new ArrayList<>();

    filter.parseFile(new File(filename), Collections.EMPTY_MAP, context, new IParseCallback() {
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, IFilter filter) {
        addEntry(id, source, translation, isFuzzy, comment, null, filter);
      }

      @Override
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, String path, IFilter filter) {
        if (!source.isEmpty()) {
          result.add(source);
        }
      }
    });

    return result;
  }

  protected List<String> parse(AbstractFilter filter, String filename, Map<String, String> options)
      throws Exception {
    final List<String> result = new ArrayList<>();

    filter.parseFile(new File(filename), options, context, new IParseCallback() {
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, IFilter filter) {
        addEntry(id, source, translation, isFuzzy, comment, null, filter);
      }

      @Override
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, String path, IFilter filter) {
        if (!source.isEmpty()) {
          result.add(source);
        }
      }
    });

    return result;
  }

  protected void parse2(final AbstractFilter filter, final String filename,
      final Map<String, String> result, final Map<String, String> legacyTMX) throws Exception {

    filter.parseFile(new File(filename), Collections.EMPTY_MAP, context, new IParseCallback() {
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, IFilter filter) {
        addEntry(id, source, translation, isFuzzy, comment, null, filter);
      }

      @Override
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, String path, IFilter filter) {
        String segTranslation = isFuzzy ? null : translation;
        result.put(source, segTranslation);
        if (translation != null) {
          // Add systematically the TU as a legacy TMX
          String tmxSource = isFuzzy ? "[" + filter.getFuzzyMark() + "] " + source : source;
          addFileTMXEntry(tmxSource, translation);
        }
      }

      public void addFileTMXEntry(String source, String translation) {
        legacyTMX.put(source, translation);
      }
    });
  }

  protected List<ParsedEntry> parse3(AbstractFilter filter, String filename,
      Map<String, String> options)
      throws Exception {
    final List<ParsedEntry> result = new ArrayList<>();

    filter.parseFile(new File(filename), options, context, new IParseCallback() {
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, IFilter filter) {
        addEntry(id, source, translation, isFuzzy, comment, null, filter);
      }

      @Override
      public void addEntry(String id, String source, String translation, boolean isFuzzy,
          String comment, String path, IFilter filter) {
        if (source.isEmpty()) {
          return;
        }
        ParsedEntry e = new ParsedEntry();
        e.id = id;
        e.source = source;
        e.translation = translation;
        e.isFuzzy = isFuzzy;
        e.comment = comment;
        e.path = path;
        result.add(e);
      }
    });

    return result;
  }

  public static void compareBinary(File f1, File f2) throws Exception {
    ByteArrayOutputStream d1 = new ByteArrayOutputStream();
    FileUtils.copyFile(f1, d1);

    ByteArrayOutputStream d2 = new ByteArrayOutputStream();
    FileUtils.copyFile(f2, d2);

    assertEquals(d1.size(), d2.size());
    byte[] a1 = d1.toByteArray();
    byte[] a2 = d2.toByteArray();
    for (int i = 0; i < d1.size(); i++) {
      assertEquals(a1[i], a2[i]);
    }
  }

  /**
   * Remove version and toolname, then compare.
   * @param f1
   * @param f2
   * @throws java.lang.Exception
   */
  protected void compareTMX(File f1, File f2) throws Exception {
    XPathExpression exprVersion = XPathFactory.newInstance().newXPath()
        .compile("/tmx/header/@creationtoolversion");
    XPathExpression exprTool = XPathFactory.newInstance().newXPath()
        .compile("/tmx/header/@creationtool");

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver(TmxReader2.TMX_DTD_RESOLVER);

    Document doc1 = builder.parse(f1);
    Document doc2 = builder.parse(f2);

    Node n;

    n = (Node) exprVersion.evaluate(doc1, XPathConstants.NODE);
    n.setNodeValue("");

    n = (Node) exprVersion.evaluate(doc2, XPathConstants.NODE);
    n.setNodeValue("");

    n = (Node) exprTool.evaluate(doc1, XPathConstants.NODE);
    n.setNodeValue("");

    n = (Node) exprTool.evaluate(doc2, XPathConstants.NODE);
    n.setNodeValue("");

    assertXMLEqual(doc1, doc2);
  }

  /**
   *
   * @param f1
   * @param f2
   * @throws Exception
   */
  protected void compareXML(File f1, File f2) throws Exception {
    compareXML(f1.toURI().toURL(), f2.toURI().toURL());
  }

  /**
   *
   * @param f1
   * @param f2
   * @throws Exception
   */
  protected void compareXML(URL f1, URL f2) throws Exception {
    assertXMLEqual(new InputSource(f1.toExternalForm()), new InputSource(f2.toExternalForm()));
  }

  protected static class ParsedEntry {
    String id;
    String source;
    String translation;
    boolean isFuzzy;
    String comment;
    String path;
  }

}

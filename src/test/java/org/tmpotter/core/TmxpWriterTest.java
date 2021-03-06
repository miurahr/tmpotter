/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
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
 **************************************************************************/

package org.tmpotter.core;

import org.testng.annotations.Test;

import java.io.File;


/**
 *
 * @author miurahr
 */
public class TmxpWriterTest extends TmxTestBase {

  /**
   * Test of writeTmxp method, of class TmxWriter.
   * @throws java.lang.Exception
   */
  @Test
  public void testWriteTmx() throws Exception {
    System.out.println("writeTmxp");
    File outputFile = File.createTempFile("test_write_tmx", "tmx");
    File expectedFile = new File(this.getClass().getResource("/tmx/expected_write_tmx.tmx").getFile());
    Document originalDocument = new Document();
    originalDocument.add("Sentense one.");
    originalDocument.add("Sentense two.");
    String langOriginal = "EN";
    Document translationDocument = new Document();
    translationDocument.add("Sentense one in Japanese.");
    translationDocument.add("Sentense two in Japanese.");
    String langTranslation = "JA";
    ProjectProperties prop = new ProjectProperties();
    prop.setFilePathProject(outputFile);
    prop.setSourceLanguage(langOriginal);
    prop.setTargetLanguage(langTranslation);
    TmxpWriter.writeTmxp(prop, originalDocument,  translationDocument);
    compareTmx(outputFile, expectedFile, 4);
    outputFile.delete();
  }
}

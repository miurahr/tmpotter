/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
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

package bitext2tmx.core;

import bitext2tmx.util.AppConstants;

import java.io.File;


/**
 *
 * @author miurahr
 */
public class TmxWriterTest extends TmxTestBase {

  /**
   * Test of writeTmx method, of class TmxWriter.
   * @throws java.lang.Exception
   */
  public void testWriteTmx() throws Exception {
    System.out.println("writeTmx");
    File outputFile = new File("test/data/tmx/test_write_tmx.tmx");
    File expectedFile = new File("test/data/tmx/expected_write_tmx.tmx");
    Document originalDocument = new Document();
    originalDocument.add("Sentense one.");
    originalDocument.add("Sentense two.");
    String langOriginal = "EN";
    Document translationDocument = new Document();
    translationDocument.add("Sentense one in Japanese.");
    translationDocument.add("Sentense two in Japanese.");
    String langTranslation = "JA";
    String encoding = AppConstants.ENCODINGS_UTF8;
    TmxWriter.writeTmx(outputFile, originalDocument, langOriginal,
            translationDocument, langTranslation, encoding);
    compareTmx(outputFile, expectedFile, 4);
    outputFile.delete();
  }

  
}

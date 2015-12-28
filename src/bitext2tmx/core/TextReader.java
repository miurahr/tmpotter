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

import bitext2tmx.segmentation.Segmenter;
import bitext2tmx.segmentation.SRX;
import bitext2tmx.util.Language;
import bitext2tmx.util.Localization;
import bitext2tmx.util.Preferences;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * Document segmenting utility class.
 * 
 * @author Hiroshi Miura
 */
public class TextReader {

  /**
   *  Reads in document to string with the original or translation text
   *  so it can be segmented
   *
   * @param filePath original input document as String
   * @param language user provided langage string such as ja_JP
   * @param encoding user provided encoding string such as UTF-8
   * @return outDocument resulted document
   * @throws java.io.IOException Exception may happen while reading file
   */
  public static Document read(String filePath, String language,
          String encoding) throws IOException {
    final FileInputStream fis;
    final InputStreamReader isr;
    
    fis = new FileInputStream(filePath);
    if (encoding.equals(Localization.getString("ENCODING.DEFAULT"))) {
      isr = new InputStreamReader(fis);
    } else {
      isr = new InputStreamReader(fis, encoding);
    }
    Segmenter.srx = Preferences.getSRX();
    if (Segmenter.srx == null) {
      Segmenter.srx = SRX.getDefault();
    }
    String result = copyCleanString(new BufferedReader(isr));
    Language lang = new Language(language);
    Document res = new Document(Segmenter.segment(lang, result, null, null));
    return res;
    
  }

  private static String copyCleanString(BufferedReader br) throws IOException {
    String linea;
    StringBuilder sb = new StringBuilder();
    
    while ((linea = br.readLine()) != null) {
      linea = linea.trim();
      if (!linea.equals("")) {
        linea = linea + "\n";
        sb.append(linea);
      }
    }
    return sb.toString();
  }  
}

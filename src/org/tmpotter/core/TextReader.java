/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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

package org.tmpotter.core;

import org.tmpotter.util.Language;
import org.tmpotter.util.Localization;
import org.tmpotter.filters.TextHandler;

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
    TextHandler tr = new TextHandler();
    Language lang = new Language(language);
    try {
      return tr.read(isr, lang);
    } catch (Exception ex) {
      return null;
    }

  }
}

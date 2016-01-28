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

package org.tmpotter.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.tmpotter.core.Document;
import org.tmpotter.segmentation.SRX;
import org.tmpotter.segmentation.Segmenter;
import org.tmpotter.util.Language;
import org.tmpotter.util.Preferences;

/**
 * Bi-Text loader.
 * 
 * <p> TBD
 * 
 * @author Hiroshi Miura
 */
public class TextHandler implements IImportFilter {
  private Document resultDocument;

  @Override
  public boolean isCombinedFileFormat() {
    return false;
  }

  @Override
  public String getFileFormatName() {
    return "bi-text";
  }
  
  @Override
  public void read(InputStreamReader isr, Language olang, Language tLang) throws Exception {
    return;
  }
  
  @Override
  public final Document read(InputStreamReader isr, Language lang) throws Exception {
    Segmenter.srx = Preferences.getSrx();
    if (Segmenter.srx == null) {
      Segmenter.srx = SRX.getDefault();
    }
    String result = copyCleanString(new BufferedReader(isr));
    resultDocument = new Document(Segmenter.segment(lang, result, null, null));
    return resultDocument;
    
  }

  @Override
  public Document getOriginalDocument() {
    return resultDocument;
  }

  @Override
  public Document getTranslationDocument() {
    return resultDocument;
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

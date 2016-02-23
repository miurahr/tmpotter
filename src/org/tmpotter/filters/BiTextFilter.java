/* *************************************************************************
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
 * *************************************************************************/

package org.tmpotter.filters;

import org.tmpotter.segmentation.SRX;
import org.tmpotter.segmentation.Segmenter;
import org.tmpotter.util.Language;
import org.tmpotter.util.Preferences;
import org.tmpotter.util.TranslationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;


/**
 * Bi-Text loader.
 * 
 * <p> TBD
 * 
 * @author Hiroshi Miura
 */
public class BiTextFilter extends AbstractFilter implements IFilter {
  @Override
  public boolean isCombinedFileFormat() {
    return false;
  }

  @Override
  protected boolean isFileSupported(BufferedReader reader) {
    return true;
  }

  @Override
  public String getFileFormatName() {
    return "bi-text";
  }

  @Override
  public String getHint() {
    return "Reading from two text files. One is for source, and another is for translation.";
  }

  @Override
  public boolean isSourceEncodingVariable() {
    return true;
  }
  
  @Override
  public boolean isTargetEncodingVariable() {
    return true;
  }

  @Override
  public void processFile(BufferedReader br, FilterContext fc) throws IOException,
      TranslationException {
    List<String> result;
    try {
      result = processTextFile(br, fc.getSourceLang());
      for (String item: result) {
        align(item, "", null, null);
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }
  
  @Override
  public void alignFile(BufferedReader sourceFile, BufferedReader translatedFile,
      FilterContext fc) throws Exception {
    List<String> result;
    try {
      result = processTextFile(translatedFile, fc.getTargetLang());
      for (String item: result) {
        align(null, item, null, null);
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }

  }

  /**
   * Process input file.
   * 
   * @param br BufferedReader to input
   * @param lang analyze by lang
   * @return List of String
   * @throws Exception when file error
   */
  public final List<String> processTextFile(BufferedReader br, Language lang) throws Exception {
    Segmenter.srx = Preferences.getSrx();
    if (Segmenter.srx == null) {
      Segmenter.srx = SRX.getDefault();
    }
    String result = copyCleanString(br);
    return Segmenter.segment(lang, result, null, null);
  }

  /**
   * Align source and translation test input.
   * 
   * @param source source
   * @param translation translation
   * @param comments comments
   * @param path  not used.
   */
  protected void align(String source, String translation, String comments, String path) {
    if (translation != null && translation.isEmpty()) {
      translation = null;
    }
    if (entryParseCallback != null) {
      entryParseCallback.addEntry(null, source, translation, false, comments, path,
            this);
    } else if (entryAlignCallback != null) {
      entryAlignCallback.addTranslation(null, source, translation, false, path, this);
    }
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

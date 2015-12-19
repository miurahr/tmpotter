/*
 * Copyright (C) 2015 miurahr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bitext2tmx.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import bitext2tmx.util.RuntimePreferences;
import static bitext2tmx.util.Localization.getString;


/**
 *
 * @author miurahr
 */
public class DocumentSegmenter {

  /**
   *
   *  Segments texts according to the programmed rules considering
   *  that a newline is not a segmentation boundary (two newlines
   *  are however)
   *
   *  @param inputString
   *        :string containing the whole text
   *
   */
  private static Document segmentWithoutBreak(final String inputString) {
    String result = "";
    Document outDocument = new Document();
    
    char car = ' ';
    char carAnt = ' ';
    int cont = 0;
    boolean kpunto = false;
    boolean kcar = false;
    for (int i = 0; i < inputString.length(); i++) {
      car = inputString.charAt(i);
      if (car == '\n' || car == '\t') {
        result = result + ' ';
      } else {
        result = result + car;
      }
      if (car == ' ') {
        if (carAnt == '.' || carAnt == ';' || carAnt == ':' || carAnt == '?' || carAnt == '!') {
          if (!result.equals("")) {
            outDocument.add(cont, result.trim());
          }
          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        } else if (carAnt == '"' && kpunto) {
          if (!result.equals("")) {
            outDocument.add(cont, result);
          }
          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        } else if (kpunto && kcar) {
          if (!result.equals("")) {
            outDocument.add(cont, result);
          }
          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
      } else if (car == '\n' && (carAnt == '\n' || carAnt == '.')) {
        if (!result.equals("")) {
          outDocument.add(cont, result.trim());
        }
        cont++;
        car = ' ';
        carAnt = ' ';
        result = "";
        kpunto = false;
        kcar = false;
      } else if (car == '.') {
        kpunto = true;
      } else {
        kcar = true;
      }
      carAnt = car;
    }
    if (!result.equals("")) {
      outDocument.add(cont, result);
    }
    return outDocument;
  }

  /**
   *  Reads in document to string with the original or translation text
   *  so it can be segmented
   *
   * @param original: original input document as String
   * @param language: user provided langage string such as ja_JP
   * @param encoding: user provided encoding string such as UTF-8
   * @return outDocument: resulted document
   * @throws java.io.IOException
   */
  public static Document readDocument(String original, String language, String encoding) throws IOException {
    final FileInputStream fis;
    final InputStreamReader isr;
    String result;
    
    fis = new FileInputStream(original);
    if (encoding.equals(getString("ENCODING.DEFAULT"))) {
      isr = new InputStreamReader(fis);
    } else {
      isr = new InputStreamReader(fis, encoding);
    }
    
    result = copyCleanString(new BufferedReader(isr));
    
    if (RuntimePreferences.isSegmentByLineBreak()) {
      return segmentWithBreak(result);
    } else {
      return segmentWithoutBreak(result);
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

  private static Document segmentWithBreak(final String document) {
    String result = "";
    Document outDocument = new Document();
    
    char car = ' ';
    char carAnt = ' ';
    int cont = 0;
    boolean kpunto = false;
    boolean kcar = false;
    for (int i = 0; i < document.length(); i++) {
      car = document.charAt(i);
      if (car == '\n' || car == '\t') {
        result = result + ' ';
      } else {
        result = result + car;
      }
      if (car == '\n') {
        if (!result.equals("")) {
          outDocument.add(cont, result.trim());
        }
        cont++;
        car = ' ';
        carAnt = ' ';
        result = "";
        kpunto = false;
        kcar = false;
      } else if (car == ' ') {
        if (carAnt == '.' || carAnt == ';' || carAnt == ':' || carAnt == '?' || carAnt == '!') {
          if (!result.equals("")) {
            outDocument.add(cont, result.trim());
          }
          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        } else if (carAnt == '"' && kpunto) {
          if (!result.equals("")) {
            outDocument.add(cont, result.trim());
          }
          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        } else if (kpunto && kcar) {
          if (!result.equals("")) {
            outDocument.add(cont, result.trim());
          }
          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
      } else if (car == '.') {
        kpunto = true;
      } else {
        kcar = true;
      }
      carAnt = car;
    }
    if (!result.equals("")) {
      outDocument.add(cont, result.trim());
    }
    return outDocument;
  }
  
}

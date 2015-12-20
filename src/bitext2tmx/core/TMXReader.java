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

import bitext2tmx.util.Localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * TMX read class.
 * 
 * @author Hiroshi Miura
 */
public class TMXReader {
  
  private static String  languageOriginal    = "en";
  private static String  languageTranslation = "en";
  

  //  FixMe: only reads TMX 1.1-1.2
  //  ToDo: need to read TMX 1.1-1.4
  //  -> TMXReader2 will replace this
 /**
  *  Read in TMX.
  * 
   * @param fPathOriginal  TMX file path
   * @param encoding  Character encoding of the file
   * @param originalDocument Source text document to be returned
   * @param translationDocument Translation text document to be returned
  */
  public static void readTmx(final File fPathOriginal, final String encoding,
          Document originalDocument, Document translationDocument) {
    ArrayList<String> tmxSourceText = new ArrayList<>();
    
    try {
      final FileInputStream fis = new FileInputStream( fPathOriginal );
      final InputStreamReader inputReader;

      if (encoding.equals(Localization.getString("ENCODING.DEFAULT"))) {
        inputReader = new InputStreamReader( fis );
      } else if (encoding.equals("UTF-8")) {
        inputReader = new InputStreamReader( fis, "UTF-8" );
      } else if (encoding.equals( "UTF-16")) {
        inputReader = new InputStreamReader(fis, "UTF-16");
      } else {
        inputReader = new InputStreamReader(fis, "ISO-8859-1");
      }
      final BufferedReader br = new BufferedReader(inputReader);
      String linea;
      int index   = 0;
      int indice = 0;
      int aux    = 0;
      boolean praseIndicator  = false;
      boolean idioma1 = false;

      while ((linea = br.readLine()) != null)  {
        linea = linea.trim();
        tmxSourceText.add(index, linea );
        index++;

        if (!idioma1) {
          if (linea.contains("<tu tuid")) {
            aux = 0;
          
          } else if (linea.contains("</tu>")) {
            if (aux == 2) {
              idioma1 = true;
            } else { 
              aux = 0;
            }
          } else if (linea.contains("<tuv")) {
            if (aux == 0) {
              indice = linea.indexOf("lang=");
              languageOriginal = linea.substring(indice + 6, indice + 8)
                      .toLowerCase();
              aux++;
            } else {
              indice = linea.indexOf("lang=");
              languageTranslation = linea.substring(indice + 6, indice + 8)
                      .toLowerCase();
              aux++;
            }
          }
        }
      }

      idioma1 = false;
      index = 0;
      aux  = 0;

      while (index < tmxSourceText.size()) {
        linea = tmxSourceText.get(index);
        while (linea.contains("  ")) {
          linea = linea.substring( 0, linea.indexOf("  ") )
            + linea.substring( linea.indexOf("  ") + 1 );
        }

        if (linea.toLowerCase().contains("<tuv")) {
          if (linea.toLowerCase().contains(languageOriginal)) {
            praseIndicator = true;
          } else if (linea.toLowerCase().contains(languageTranslation)) {
            praseIndicator = false;
          } else {
            System.out.println("Unknown language" + linea);
          }
        } else if (linea.toLowerCase().contains("<seg>")) {
          linea = removeTag(linea, "seg");

          while (linea.contains("  ")) {
            linea = linea.substring( 0, linea.indexOf("  "))
              + linea.substring( linea.indexOf("  ") + 1);
          }

          if (praseIndicator) {
            originalDocument.add(aux, linea);
            translationDocument.add(aux, "");
            idioma1 = true;
            aux++;
          } else {
            if (!idioma1) {
              originalDocument.add(aux, "");
              translationDocument.add(aux, linea);
              aux++;
            } else {
              translationDocument.set(translationDocument.size() - 1, linea);
            }
          } 
        }

        index++;
      }
      br.close();
    } catch (final java.io.IOException ex) {
      System.out.println(ex);
    }
  }

  /**
   *  Remove tag.
   *
   *  @param linea source text string
   *  @param tagName  tag to be removed
   *  @return string which is removed the tag
   */
  private static String removeTag( final String linea, final String tagName ) {
    String cad = "";
    final String endTag = "</" + tagName + ">";

    if (!linea.contains(endTag)) {
      // FIXME
    } else {
      cad = linea.substring(tagName.length() + 2, linea.indexOf(endTag));
    }
    return (cad);
  }
}

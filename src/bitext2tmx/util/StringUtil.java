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

package bitext2tmx.util;

import java.text.MessageFormat;
import java.util.StringTokenizer;


/**
 * String utilities.
 *
 * @author Hiroshi Miura
 */
public class StringUtil {
  
  // ToDo: Check usage, Why 47?
  private static int  _KTAMTEXTAREA = 47;
  
  /**
   *  Funci�n FormatearTexto. Esta funci�n formatea el tama�o de la frase al
   *  tama�o de _KTAMTEXTAREA que es una constante con el tama�o del componente
   *  jTextArea.
   *
   *  @param cad : la cadena que hay que formatear
   *  @return cad con la cadena formateada
   */
  public static String formatText( final String cad ) {
    String palabra = "";
    String newCad  = "";
    String frase  = "";

    if ( cad.length() > _KTAMTEXTAREA ) {
      final StringTokenizer linea = new StringTokenizer( cad, " " );

      while ( linea.hasMoreTokens() ) {
        palabra = linea.nextToken();
        if ( ( palabra.length() + frase.length() ) < _KTAMTEXTAREA ) {
          //frase = frase + " ";
          //frase = frase + palabra;
          frase = frase + " " + palabra;
        } else {
          if ( newCad.equals( "" ) ) {
            newCad = frase;
          } else {
            newCad = newCad + "\n" + frase;
          }
          frase = "";
          frase = palabra;
        }
      }//  while()

      frase = frase.trim();
      newCad = newCad.trim();
      newCad = newCad + "\n" + frase;

      return ( newCad );
    }

    return ( cad );
  }

  /**
   *  Funci�n RestaurarTexto. Esta funci�n elimina \n de la frase.
   *
   *  @param cad : la frase a la que se tienen que eliminar los \n
   *  @return cad con la frase
   */
  public static String restoreText( final String cad ) {
    String newCad = "";
    String palabra = "";

    if ( cad.length() > _KTAMTEXTAREA ) {
      final StringTokenizer linea = new StringTokenizer( cad, "\n" );

      while ( linea.hasMoreTokens() ) {
        palabra = linea.nextToken();
        newCad = newCad + " " + palabra;
      }

      newCad = newCad.trim();

      return ( newCad );
    }

    return ( cad );
  }

  /**
   * ~inverse of String.split() refactor note: In future releases, this might
   * best be moved to a different file
   *
   * @param separator insert separator
   * @param items to join
   * @return String joining items with separator
   */
  public static String joinString(String separator, String[] items) {
    if (items.length < 1) {
      return "";
    }
    StringBuilder joined = new StringBuilder();
    for (int i = 0; i < items.length; i++) {
      joined.append(items[i]);
      if (i != items.length - 1) {
        joined.append(separator);
      }
    }
    return joined.toString();
  }

  /**
   * Formats UI strings.
   *
   * Note: This is only a first attempt at putting right what goes wrong in
   * MessageFormat. Currently it only duplicates single quotes, but it doesn't
   * even test if the string contains parameters (numbers in curly braces),
   * and it doesn't allow for string containg already escaped quotes.
   *
   * @param str
   *            The string to format
   * @param arguments
   *            Arguments to use in formatting the string
   *
   * @return The formatted string
   *
   * @author Henry Pijffers (henry.pijffers@saxnot.com)
   */
  public static String format(String str, Object... arguments) {
    str = str.replaceAll("'", "''");
    return MessageFormat.format(str, arguments);
  }

  public static boolean isEmpty(final String str) {
    return str == null || str.isEmpty();
  }


}

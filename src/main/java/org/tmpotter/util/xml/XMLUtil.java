/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  Part of this come from OmegaT.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
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

package org.tmpotter.util.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for XML processing.
 * 
 * @author Hiroshi Miura
 */
public class XMLUtil {

  private static final String RE_TAG = "<\\/?[a-zA-Z]+[0-9]+\\/?>";
  
  /**
   *  Converts a single char into a valid XML character
   *
   *  Output stream must convert stream to UTF-8 when saving to disk.
   */
  private static String getValidXMLChar( final char ch )
  {
    switch( ch )
    {
      //case '\'': return "&apos;";
      case '&': return "&amp;";
      case '>': return "&gt;";
      case '<': return "&lt;";
      case '"': return "&quot;";
      default : return String.valueOf( ch );
    }
  }

    /**
   *  Converts a plaintext string into valid XML string
   *
   *  Output stream must convert stream to UTF-8 when saving to disk.
   * 
   * @param plaintext
   * @return 
   */
  final public static String getValidXMLText( final String plaintext )
  {
    final StringBuilder out = new StringBuilder();
    final String text = fixChars( plaintext );

    for( int i=0; i<text.length(); i++ )
      out.append( getValidXMLChar( text.charAt( i ) ) );

    return( out.toString() );
  }
  
    public static String removeXMLInvalidChars(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        for (int c, i = 0; i < str.length(); i += Character.charCount(c)) {
            c = str.codePointAt(i);
            if (!isValidXMLChar(c)) {
                c = ' ';
            }
            sb.appendCodePoint(c);
        }
        return sb.toString();
    }

    public static boolean isValidXMLChar(int codePoint) {
        if (codePoint < 0x20) {
            if (codePoint != 0x09 && codePoint != 0x0A && codePoint != 0x0D) {
                return false;
            }
        } else if (codePoint >= 0x20 && codePoint <= 0xD7FF) {
        } else if (codePoint >= 0xE000 && codePoint <= 0xFFFD) {
        } else if (codePoint >= 0x10000 && codePoint <= 0x10FFFF) {
        } else {
            return false;
        }
        return true;
    }

    /**
     * Converts a single code point into valid XML. Output stream must convert stream
     * to UTF-8 when saving to disk.
     */
    public static String escapeXMLChars(int cp) {
        switch (cp) {
        // case '\'':
        // return "&apos;";
        case '&':
            return "&amp;";
        case '>':
            return "&gt;";
        case '<':
            return "&lt;";
        case '"':
            return "&quot;";
        default:
            return String.valueOf(Character.toChars(cp));
        }
    }

    /**
     * Converts a stream of plaintext into valid XML. Output stream must convert
     * stream to UTF-8 when saving to disk.
     */
    public static String makeValidXML(String plaintext) {
        StringBuilder out = new StringBuilder();
        String text = removeXMLInvalidChars(plaintext);
        for (int cp, i = 0; i < text.length(); i += Character.charCount(cp)) {
            cp = text.codePointAt(i);
            out.append(escapeXMLChars(cp));
        }
        return out.toString();
    }

  /**
   *  Replace invalid XML chars by spaces. See supported chars at
   *  http://www.w3.org/TR/2006/REC-xml-20060816/#charsets.
   *
   *  @param str input stream
   *  @return result stream
   */
  final public static String fixChars(final String str) {
    StringBuffer result = new StringBuffer(str.length());
    for (int i = 0; i < str.length(); i++) {
        int c = str.codePointAt(i);
        if (!isValidXMLChar(c)) {
           c = ' ';
        }
        result.append(Character.toChars(c));
    }
    return result.toString();
  }

    /**
   * Strips all XML tags (converts to plain text). Tags detected only by
   * pattern. Protected parts are not used.
   */
  public static String stripXmlTags(String xml) {
    return Pattern.compile(RE_TAG).matcher(xml).replaceAll("");
  }
}

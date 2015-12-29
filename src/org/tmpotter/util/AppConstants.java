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

package org.tmpotter.util;

import static org.tmpotter.util.Localization.getString;

import java.io.File;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


/**
 *  Constants - class level (global scope).
 *
 */
public class AppConstants {
  private static final String __VERSION_KEY = "version";
  private static final String __UPDATE_KEY = "update";
  private static final String __REVISION_KEY = "revision";
  private static String BRANDING = "";
  private static final String NAME = getString( "WND.APP.TITLE" );
  private static final String VERSION = ResourceBundle
          .getBundle("org/tmpotter/Version")
          .getString(__VERSION_KEY);
  private static final String UPDATE = ResourceBundle
          .getBundle("org/tmpotter/Version")
          .getString(__UPDATE_KEY);
  private static final String REVISION = ResourceBundle
          .getBundle("org/tmpotter/Version")
          .getString(__REVISION_KEY);
  public static final String COPYRIGHT   = "Copyright (C) 2015";
  public static final String LICENSE     = 
          "Released as Free Software under GPL v3 and later";
  public static final String AUTHORS     = 
          "Hiroshi Miura";
  public static final String CONTRIBUTORS =
          "TMPotter used components of OmegaT and bitext2tmx.\n"
          + "OmegaT: Alex Buloichik, Thomas Cordonnier,\n"
          + " Aaron Madlon-Kay, Zoltan Bartko,\n"
          + " Didier Briel, Maxym Mykhalchuk, "
          + "Keith Godfrey\n"
          + "bitext2tmx: Susana Santos Antón,\n"
          + " Raymond: Martin et al.";
  public static final String BUILDCLASSPATH = "build"
          + File.separator + "classes";
  public static final int READ_AHEAD_LIMIT = 65536;

  public static final Pattern XML_ENCODING = Pattern
            .compile("<\\?xml.*?encoding\\s*=\\s*\"(\\S+?)\".*?\\?>");

  public static final String APPLICATION_JAR = "tmpotter.jar";
  public static final String DEBUG_CLASSPATH = File.separator + "classes";

  // Encodings.java
  public static final String ENCODINGS_UTF8        = "UTF-8";
  public static final String ENCODINGS_ISO8859_1   = "ISO-8859-1";
  public static final String ENCODINGS_CP932       = "CP932";
  public static final String ENCODINGS_DEFAULT     = "Default";
  public static final String [] straEncodings = {
      ENCODINGS_UTF8, ENCODINGS_ISO8859_1, ENCODINGS_CP932, ENCODINGS_DEFAULT
  };
  public static final String TAG_REPLACEMENT = "\b";
  
  /**
   * Char which should be used instead protected parts. 
   * 
   * <p>It should be non-letter char, to be able to have
   * correct words counter.
   *
   * <p>This char can be placed around protected text for 
   * separate words inside protected text and words
   * outside if there are no spaces between they.
   */
  public static final char TAG_REPLACEMENT_CHAR = '\b';
  
  /** Pattern that detects language and country,
   * with an optionnal script in the middle.
   */
  public static final Pattern LANG_AND_COUNTRY = Pattern
          .compile("([A-Za-z]{1,8})(?:(?:-|_)(?:[A-Za-z]{4}(?:-|_))?([A-Za-z0-9]{1,8}))?");
  
  public static final Pattern SPACY_REGEX = Pattern
          .compile("((\\s|\\\\n|\\\\t|\\\\s)(\\+|\\*)?)+");

  /**
   * Make app name and version string for human.
   *
   * @return string to indicate for human reading
   */
  public static String getDisplayNameAndVersion() {
    if (UPDATE != null && !UPDATE.equals("0")) {
      return StringUtil.format(getString("app-version-template-pretty-update"),
                    getApplicationDisplayName(), VERSION, UPDATE);
    } else {
      return StringUtil.format(getString("app-version-template-pretty"),
                    getApplicationDisplayName(), VERSION);
    }
  }

  /**
   * Get the application name for display purposes (includes branding).
   *
   * @return  application name for human reading.
   */
  public static String getApplicationDisplayName() {
    return BRANDING.isEmpty() ? NAME : NAME + " " + BRANDING;
  }
  
  /**
   * Get the application description.
   * 
   * @return string to describe application
   */
  public static String getApplicationDescription() {
    return getString("WND.APP.DESCRIPTION");
  }
  
  public static String getAppNameAndVersion() {
    return StringUtil.format(getString("app-version-template"), NAME, VERSION, UPDATE, REVISION);
  }

  public static String getVersion() {
    return StringUtil.format(getString("version-template"), VERSION, UPDATE, REVISION);
  }
}

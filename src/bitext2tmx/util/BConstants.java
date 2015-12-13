/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009  Raymond: Martin
#            (C) 2015 Hiroshi Miura
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#######################################################################
*/

package bitext2tmx.util;

import java.io.File;
import java.util.regex.Pattern;
import java.util.ResourceBundle;

import static bitext2tmx.util.Localization.*;


/**
 *  Constants - class level (global scope)
 *
 */
public class BConstants
{
  private static String __VERSION_KEY = "version";
  private static String __UPDATE_KEY = "update";
  private static String __REVISION_KEY = "revision";

  private static String BRANDING = "";
  private static final String NAME = getString( "WND.APP.TITLE" );
  private static final String VERSION = ResourceBundle.getBundle("bitext2tmx/Version")
            .getString(__VERSION_KEY);
  private static final String UPDATE = ResourceBundle.getBundle("bitext2tmx/Version")
            .getString(__UPDATE_KEY);
  private static final String REVISION = ResourceBundle.getBundle("bitext2tmx/Version")
            .getString(__REVISION_KEY);
  public static final String COPYRIGHT   = "Copyright (C) 2005-2009,2015";
  public static final String LICENSE     = "Released as Free Software under GPL v3 and later";
  public static final String AUTHORS     = "Susana Santos Antón, Raymond: Martin, Hiroshi Miura et al.";
  public static final String BUILDCLASSPATH = "build" + File.separator+ "classes";

  public static final int READ_AHEAD_LIMIT = 65536;

  public static final Pattern XML_ENCODING = Pattern
            .compile("<\\?xml.*?encoding\\s*=\\s*\"(\\S+?)\".*?\\?>");

  public static final String APPLICATION_JAR = "bitext2mx.jar";
  public static final String DEBUG_CLASSPATH = File.separator + "classes";

  // Encodings.java
  public static final String ENCODINGS_UTF8        = "UTF-8";
  public static final String ENCODINGS_ISO8859_1   = "ISO-8859-1";
  public static final String ENCODINGS_CP932       = "CP932";
  public static final String ENCODINGS_DEFAULT     = "Default";
  public static final String [] straEncodings = {
      ENCODINGS_UTF8, ENCODINGS_ISO8859_1, ENCODINGS_CP932, ENCODINGS_DEFAULT
  };

  public static String getDisplayNameAndVersion() {
    if (UPDATE != null && !UPDATE.equals("0")) {
      return Utilities.format(getString("app-version-template-pretty-update"),
                    getApplicationDisplayName(), VERSION, UPDATE);
    } else {
      return Utilities.format(getString("app-version-template-pretty"),
                    getApplicationDisplayName(), VERSION);
    }
  }

  /**
   * Get the application name for display purposes (includes branding)
   */
  public static String getApplicationDisplayName() {;
    return BRANDING.isEmpty() ? NAME : NAME + " " + BRANDING;
  }
  public static String getApplicationDescription() {
    return getString("WND.APP.DESCRIPTION");
  }
  public static String getAppNameAndVersion() {
    return Utilities.format(getString("app-version-template"), NAME, VERSION, UPDATE, REVISION);
  }
  public static String getVersion() {
    return Utilities.format(getString("version-template"), VERSION, UPDATE, REVISION);
  }

}//  Constants{}



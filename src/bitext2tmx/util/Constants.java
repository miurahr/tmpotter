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
import java.io.IOException;
import java.text.*;
import java.util.jar.*;
import java.util.regex.Pattern;

import static bitext2tmx.util.Localization.*;


/**
 *  Constants - class level (global scope)
 *
 */
public class Constants
{
  //public static final String NAME = Localization.get( "WND_APP_TITLE" );
  public static final String NAME = l10n( "WND.APP.TITLE" );
  public static final String VERSION     = " 1.1.M1";
  public static final String BUILD       = " (build 20151210)";
  public static final String VERSIONNAME = NAME + VERSION;
  public static final String APPNAME     = NAME + VERSION + BUILD;
  public static final String LICENSE     = "Released as Free Software under GPL license v3 and later";

  public static final String APPLICATIONJAR = "bitext2tmx-1.1.M1.jar";
  public static final String BUILDCLASSPATH = "build" + File.separator+ "classes";

  public static final int READ_AHEAD_LIMIT = 65536;

  public static final Pattern XML_ENCODING = Pattern
            .compile("<\\?xml.*?encoding\\s*=\\s*\"(\\S+?)\".*?\\?>");

  public static final String APPLICATION_JAR = "bitext2mx.jar";
  public static final String DEBUG_CLASSPATH = File.separator + "classes";

}//  Constants{}



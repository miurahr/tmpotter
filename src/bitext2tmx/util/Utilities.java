/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009 Raymond: Martin et al
#
#  Includes code: Copyright (C) 2002-2006 Keith Godfrey et al.
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#######################################################################
*/


package bitext2tmx.util;

import java.awt.GraphicsEnvironment;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.*;

import javax.swing.ImageIcon;

import bitext2tmx.util.Platform.OsType;


/**
 *  Utility functions
 */
public class Utilities
{
  /* Variables */
  private static String m_configDir = null;

  /* Constants */
  private final static String WINDOWS_CONFIG_DIR = "\\Bitext2tmx\\";
  private final static String UNIX_CONFIG_DIR = "/.bitext2tmx/";
  private final static String OSX_CONFIG_DIR = "/Library/Preferences/Bitext2tmx/";

  /**
   *  Return names of all font families available
   */
  final public static String[] getFontNames()
  {
    final GraphicsEnvironment graphics =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    return( graphics.getAvailableFontFamilyNames() );
  }

  /**
   *  Converts a single char into a valid XML character
   *
   *  Output stream must convert stream to UTF-8 when saving to disk.
   */
  final public static String getValidXMLChar( final char c )
  {
    switch( c )
    {
      //case '\'': return "&apos;";
      case '&': return "&amp;";
      case '>': return "&gt;";
      case '<': return "&lt;";
      case '"': return "&quot;";
      default : return String.valueOf( c );
    }
  }

  /**
   *  Converts a plaintext string into valid XML string
   *
   *  Output stream must convert stream to UTF-8 when saving to disk.
   */
  final public static String getValidXMLText( final String plaintext )
  {
    final StringBuilder out = new StringBuilder();
    final String text = fixChars( plaintext );

    for( int i=0; i<text.length(); i++ )
      out.append( getValidXMLChar( text.charAt( i ) ) );

    return( out.toString() );
  }


  /**  On Linux OS? */
  final public static boolean isLinux()
  {
    final String strOS;

    try{ strOS = System.getProperty( "os.name" ); }
    catch( final SecurityException e ) { return( false ); }

    return( strOS.equals( "Linux" ) );
  }

  /**  On Mac OS X? */
  final public static boolean isMacOSX()
  {
    final String strOS;

    try{ strOS = System.getProperty( "os.name" ); }
    catch( final SecurityException e ) { return( false ); }

    return( strOS.equals( "Mac OS X" ) );
  }

  /**  On Unix type OS? */
  final public static boolean isUnix()
  {
    final String strOS;
    boolean bUnix = false;

    try{ strOS = System.getProperty( "os.name" ); }
    catch( final SecurityException e ) { return( bUnix ); }

    if( strOS.contains( "AIX" )   || strOS.equals( "Digital Unix" ) ||
        strOS.equals( "FreeBSD" ) || strOS.equals( "HP UX" )        ||
        strOS.equals( "Irix" )    || isLinux()                      ||
        strOS.equals( "MPE/iX" )  || strOS.equals( "Solaris" )      ||
        strOS.equals( "SunOS" ) )
      bUnix = true;

    return( bUnix );
  }

  /** On Windows? */
  final public static boolean isWindows()
  {
    final String strOS;

    try{ strOS = System.getProperty( "os.name" ); }
    catch( final SecurityException e ) { return( false ); }

    return( strOS.contains( "Windows" ) );
  }

  /**
   *  Replace invalid XML chars by spaces. See supported chars at
   *  http://www.w3.org/TR/2006/REC-xml-20060816/#charsets.
   *
   *  @param str input stream
   *  @return result stream
   */
  final public static String fixChars( final String str )
  {
    char[] result = new char[str.length()];

    for( int i = 0; i < str.length(); i++ )
    {
      char c = str.charAt( i );

      if( c < 0x20 )
      {
        if( c != 0x09 && c != 0x0A && c != 0x0D )  c = ' ';
      }
      else if( c >= 0x20    && c <= 0xD7FF )   {}
      else if( c >= 0xE000  && c <= 0xFFFD )   {}
      else if( c >= 0x10000 && c <= 0x10FFFF ) {}
      else c = ' ';

      result[i] = c;
    }

    return( new String( result ) );
  }
  
   /**
     * ~inverse of String.split() refactor note: In future releases, this might
     * best be moved to a different file
     */
    public static String joinString(String separator, String[] items) {
        if (items.length < 1)
            return "";
        StringBuilder joined = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            joined.append(items[i]);
            if (i != items.length - 1)
                joined.append(separator);
        }
        return joined.toString();
    }

    /**
     * Print UTF-8 text to stdout (useful for debugging)
     * 
     * @param output
     *            The UTF-8 format string to be printed.
     */
    public static void printUTF8(String output) {
        try {
            BufferedWriter out = UTF8WriterBuilder(System.out);
            out.write(output);

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new BufferedWriter configured for UTF-8 output and connects it to
     * an OutputStream
     * 
     * @param out
     *            Outputstream to connect to.
     */
    public static BufferedWriter UTF8WriterBuilder(OutputStream out) throws Exception {
        return new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    }

    /**
     * Save UTF-8 format data to file.
     * 
     * @param dir
     *            directory to write to.
     * @param filename
     *            filename of file to write.
     * @param output
     *            UTF-8 format text to write
     */
    public static void saveUTF8(String dir, String filename, String output) {
        try {
            // Page name can contain invalid characters, see [1878113]
            // Contributed by Anatoly Techtonik
            filename = filename.replaceAll("[\\\\/:\\*\\?\\\"\\|\\<\\>]", "_");
            File path = new File(dir, filename);
            FileOutputStream f = new FileOutputStream(path);
            BufferedWriter out = UTF8WriterBuilder(f);
            out.write(output);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // MessageFormat.format expects single quotes to be escaped
        // by duplicating them, otherwise the string will not be formatted
        str = str.replaceAll("'", "''");
        return MessageFormat.format(str, arguments);
    }

    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
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
     * Returns the location of the configuration directory, depending on the
     * user's platform. Also creates the configuration directory, if necessary.
     * If any problems occur while the location of the configuration directory
     * is being determined, an empty string will be returned, resulting in the
     * current working directory being used.
     *
     * <ul><li>Windows XP: &lt;Documents and Settings>\&lt;User name>\Application Data\OmegaT
     * <li>Windows Vista: User\&lt;User name>\AppData\Roaming 
     * <li>Linux: ~/.omegat 
     * <li>Solaris/SunOS: ~/.omegat
     * <li>FreeBSD: ~/.omegat 
     * <li>Mac OS X: ~/Library/Preferences/OmegaT 
     * <li>Other: User home directory
     * </ul>
     *
     * @return The full path of the directory containing the OmegaT
     *         configuration files, including trailing path separator.
     *
     * @author Henry Pijffers (henry.pijffers@saxnot.com)
     */
    public static String getConfigDir() {
        // if the configuration directory has already been determined, return it
        if (m_configDir != null) {
            return m_configDir;
        }

        OsType os = Platform.getOsType(); // name of operating system
        String home; // user home directory

        // get os and user home properties
        try {
            // get the user's home directory
            home = System.getProperty("user.home");
        } catch (SecurityException e) {
            // access to the os/user home properties is restricted,
            // the location of the config dir cannot be determined,
            // set the config dir to the current working dir
            m_configDir = new File(".").getAbsolutePath() + File.separator;
            return m_configDir;
        }

        // if os or user home is null or empty, we cannot reliably determine
        // the config dir, so we use the current working dir (= empty string)
        if (os == null || Utilities.isEmpty(home)) {
            // set the config dir to the current working dir
            m_configDir = new File(".").getAbsolutePath() + File.separator;
            return m_configDir;
        }

        // check for Windows versions
        if (os == OsType.WIN32 || os == OsType.WIN64) {
            String appData = null;

            // We do not use %APPDATA%
            // Trying first Vista/7, because "Application Data" exists also as virtual folder, 
            // so we would not be able to differentiate with 2000/XP otherwise
            File appDataFile = new File(home, "AppData\\Roaming");
            if (appDataFile.exists()) {
                appData = appDataFile.getAbsolutePath();
            } else {
                // Trying to locate "Application Data" for 2000 and XP
                // C:\Documents and Settings\<User>\Application Data
                appDataFile = new File(home, "Application Data");
                if (appDataFile.exists()) {
                    appData = appDataFile.getAbsolutePath();
                }
            }

            if (!Utilities.isEmpty(appData)) {
                // if a valid application data dir has been found,
                // append an OmegaT subdir to it
                m_configDir = appData + WINDOWS_CONFIG_DIR;
            } else {
                // otherwise set the config dir to the user's home directory,
                // usually
                // C:\Documents and Settings\<User>\OmegaT
                m_configDir = home + WINDOWS_CONFIG_DIR;
            }
        }
        // Check for UNIX varieties
        // Solaris is generally detected as SunOS
        else if (os == OsType.LINUX32 || os == OsType.LINUX64 || os == OsType.OTHER) {
            // set the config dir to the user's home dir + "/.omegat/", so it's
            // hidden
            m_configDir = home + UNIX_CONFIG_DIR;
        }
        // check for Mac OS X
        else if (Platform.isMacOSX()) {
            // set the config dir to the user's home dir +
            // "/Library/Preferences/OmegaT/"
            m_configDir = home + OSX_CONFIG_DIR;
        }
        // other OSes / default
        else {
            // use the user's home directory by default
            m_configDir = home + File.separator;
        }

        // create the path to the configuration dir, if necessary
        if (!m_configDir.isEmpty()) {
            try {
                // check if the dir exists
                File dir = new File(m_configDir);
                if (!dir.exists()) {
                    // create the dir
                    boolean created = dir.mkdirs();

                    // if the dir could not be created,
                    // set the config dir to the current working dir
                    if (!created) {
                        m_configDir = new File(".").getAbsolutePath() + File.separator;
                    }
                }
            } catch (SecurityException e) {
                // the system doesn't want us to write where we want to write
                // reset the config dir to the current working dir
                m_configDir = new File(".").getAbsolutePath() + File.separator;

            }
        }

        // we should have a correct, existing config dir now
        return m_configDir;
    }
    

    /** Caching install dir */
    private static String INSTALLDIR = null;

    /**
     * Returns Application installation directory
     */
    public static String installDir() {
        if (INSTALLDIR == null) {
            String cp = System.getProperty("java.class.path");
            
            // See if we are running from a JAR
            String path = extractClasspathElement(cp, File.separator + Constants.APPLICATION_JAR);
            
            if (path == null) {
                // We're not running from a JAR; probably debug mode (in IDE, etc.)
                path = extractClasspathElement(cp, Constants.DEBUG_CLASSPATH);
            }
            
            // WTF?!! Falling back to current directory
            if (path == null) {
                path = ".";
            }
            
            // Cache the absolute path
            INSTALLDIR = new File(path).getAbsolutePath();
        }
        return INSTALLDIR;
    }

    /**
     * Extracts an element of a class path.
     *
     * @param fullcp
     *            the classpath
     * @param posInsideElement
     *            position inside a class path string, that fits inside some
     *            classpath element.
     */
    private static String classPathElement(String fullcp, int posInsideElement) {
        // semicolon before the path to the Jar
        int semicolon1 = fullcp.lastIndexOf(File.pathSeparatorChar, posInsideElement);
        // semicolon after the path to the Jar
        int semicolon2 = fullcp.indexOf(File.pathSeparatorChar, posInsideElement);
        if (semicolon1 < 0)
            semicolon1 = -1;
        if (semicolon2 < 0)
            semicolon2 = fullcp.length();
        return fullcp.substring(semicolon1 + 1, semicolon2);
    }

    /**
     * Extract classpath element that ends with <code>ending</code> from
     * the full classpath <code>cp</code>, if present. If not present, returns
     * null.
     */
    private static String extractClasspathElement(String cp, String ending) {
        try {
            int pos = cp.indexOf(ending);
            if (pos >= 0) {
                String path = classPathElement(cp, pos);
                return path.substring(0, path.indexOf(ending));
            }
        } catch (Exception e) {
            // should never happen, but just in case ;-)
        }
        return null;
    }

}//  Utilities{}



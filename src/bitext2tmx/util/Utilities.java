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

import static bitext2tmx.util.Constants.*;



/**
 *  Utility functions
 */
public class Utilities
{
  final static public ImageIcon getIcon( final String iconName, final Object obj )
  {
    return( new ImageIcon( obj.getClass().
      getResource( "/../icons/" + iconName ) ) );
  }

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

}//  Utilities{}



/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009 Raymond: Martin et al.
#            (C) 2005-2006 Susana Santos Antón
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

package bitext2tmx.ui;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import javax.swing.border.*;


import static bitext2tmx.util.Constants.*;
import static bitext2tmx.util.Localization.*;


final public class Manual extends JDialog
{
  /**
   *
   */
  private static final long serialVersionUID = -7978170933387914555L;

  final JPanel                    panel1           = new JPanel();
  final BorderLayout              borderLayout1    = new BorderLayout();
  final ArrayList                 IdiomaDlg        = new ArrayList();

  public File fpathHome = new File( System.getProperty( "user.dir" ) );

  final JScrollPane  jScrollPaneAyuda = new JScrollPane();
  Border             border1;
  final JEditorPane  jEditorPaneAyuda = new JEditorPane();
  int                kIDIOMA          = 2;

  //  ISO language code
  final String m_strISOLang    = Locale.getDefault().getLanguage();
  final String m_strISOCountry = Locale.getDefault().getCountry();

  /** Caching install dir */
  private String INSTALLDIR = null;

  //  Documentation
  public static final String DOC_DIR        = "doc";
  //  ToDo: fix to get Locale/Language specific user guide
  public static final String MAN_DIR        = "guide/en";
  public static final String MAN_PATH = DOC_DIR + File.separatorChar +
    MAN_DIR;

  public Manual( final Frame frame, final String title, final boolean modal )
  {
    super( frame, title, modal );

    try
    {
      initialize();
      pack();
    }
    catch( final Exception ex ) { ex.printStackTrace(); }
  }

  public Manual()
  {
    //  No resize! -RM
    this( null, "", false );
    //this( null, "", true );
    setSize( new Dimension( 648, 400 ) );
  }

  final private void initialize() throws Exception
  {
    border1 = BorderFactory.createLineBorder( Color.gray, 2 );

    panel1.setLayout( borderLayout1 );

    jScrollPaneAyuda.setBorder( border1 );
    jEditorPaneAyuda.setText( "" );

    setModal( true );
    setTitle( getString( "DLG.HELP.TITLE" ) );

    getContentPane().add( panel1 );
    panel1.add( jScrollPaneAyuda, BorderLayout.CENTER );

    jScrollPaneAyuda.getViewport().add( jEditorPaneAyuda, null );

    jEditorPaneAyuda.setEditable( false );
    jEditorPaneAyuda.setContentType( "text/html" );

    setSize( new Dimension( 900, 720 ) );

    Ver();
  }

  final void Ver()
  {
    try
    {
      String ayuda_html;

      //  ISO language code
      //String strISOLang    = Locale.getDefault().getLanguage();
      //String strISOCountry = Locale.getDefault().getCountry();

      //  Bug - this won't work for certain implementations -RM
      //  FixMe - need fix for Mac application bundles -RM
      //  ToDo - need fix for Linux packaging (RPM, deb, ?) -RM
      //String strPath = "file:" + System.getProperty( "user.dir" ) +
      //  File.separator;
      //+ MAN_PATH + File.separator +
      //language + File.separator + file );

     //System.out.println( strPath );

      //  ToDo: fix to get Locale/Language specific user guide
      ayuda_html = "Bitext2tmx.html";
      
      //  -> String constants
/*
      if( m_strISOLang.equals( "ca" ) )       ayuda_html = "Bitext2tmx_ca.html";
      else if ( m_strISOLang.equals( "en" ) ) ayuda_html = "Bitext2tmx_en.html";
      else if ( m_strISOLang.equals( "es" ) )
      {
*/
        //  ToDo - need better localization selection mechanism
        //  similar to resource bundles -RM
        /*
        if( strISOCountry.equals( "ES" ) )
          ayuda_html = "file:doc/help/Bitext2tmx_es_ES.html";
        else
        */
/*        ayuda_html = "Bitext2tmx_es.html";
      }
      else ayuda_html = "Bitext2tmx_en.html";
*/

     //System.out.println( ayuda_html );


/*
      try
      {
        //  Duplicate code -RM
        FileWriter     wr = new FileWriter( fpathHome + "/config.txt" );
        BufferedWriter bw = new BufferedWriter( wr );
        PrintWriter    pw = new PrintWriter( bw );

        //  Crap
        pw.println( strPath );
        pw.println( ayuda_html );
        pw.close();
      }
      catch( java.io.IOException ioe )
      {
        //  Using println for exception handling!
        //  Better than nothing, not by much -RM
        //  NOT-LOCALIZED
        System.out.println( "ERROR al crear el config" );
        System.exit( 0 );
      }
*/
      final String fullname = getAbsolutePath( ayuda_html );

      //jEditorPaneAyuda.setPage( ayuda_html );
      final URL page = new URL( fullname );
      jEditorPaneAyuda.setPage( page );
      //m_filename = file;

    }
    catch( final java.io.IOException ex )
    {
      JOptionPane.showMessageDialog( panel1,
        getString( "MSG.ERROR.HELP.NOTFOUND" ),
        getString( "MSG.ERROR" ),
        JOptionPane.ERROR_MESSAGE );

      dispose();
    }
  }

  final private String getAbsolutePath( final String file )
  {
    try
    {  //  MAN_PATH = doc/help
      return( "file:" +
        ( new File( installDir()  + File.separator +
          MAN_PATH + File.separator + file ) ).getCanonicalPath() );
    }
    catch( final IOException exception )
    {
      //log( exception ); wrong type for current method
      System.out.println( "Exception occurred when trying to locate documentation" );
      return null;
    }
  }

  /**
  * Extracts an element of a class path.
  *
  * @param fullcp the classpath
  * @param posInsideElement position inside a class path string, that fits 
  *                          inside some classpath element.
  */
  final private String getClassPathElement( final String strFullClassPath,
    final int iPosInsideElement )
  {
    //  Semicolon before path to the Jar
    int iSemicolon1 = strFullClassPath.
      lastIndexOf( File.pathSeparatorChar, iPosInsideElement );
    //  Semicolon after path to the Jar
    int iSemicolon2 = strFullClassPath.
      indexOf( File.pathSeparatorChar, iPosInsideElement );

    if( iSemicolon1 < 0 ) iSemicolon1 = -1;
    if( iSemicolon2 < 0 ) iSemicolon2 = strFullClassPath.length();

    //return( strFullClassPath.substring( iSemicolon1 + 1, iSemicolon2 ) );
    return( strFullClassPath.substring( ++iSemicolon1, iSemicolon2 ) );
  }


  /** Trying to see if this ending is inside the classpath */
  final private String tryClasspathElement( final String strClassPath,
    final String strSuffix )
  {
    try
    {
      final int iPos = strClassPath.indexOf( strSuffix );

      if( iPos >= 0 )
      {
        String strPath = getClassPathElement( strClassPath, iPos );
        strPath = strPath.substring( 0, strPath.indexOf( strSuffix ) );

        return( strPath );
      }
    }
    catch( final Exception e ) {}

    return( null );
  }

  /**
    *  Return B2T installation directory
    *  Used to find the proper location of B2T documentation
    */
  final public String installDir()
  {
    if( INSTALLDIR != null ) return INSTALLDIR;

    final String strClassPath = System.getProperty( "java.class.path" );
    String strPath;

    //  running from a Jar ?
    strPath = tryClasspathElement( strClassPath, APPLICATIONJAR );

    //  again missed, we're not running from Jar, most probably debug mode
    if( strPath == null )
      strPath = tryClasspathElement( strClassPath, BUILDCLASSPATH );

    //  WTF?!! using current directory
    if( strPath == null ) strPath = ".";

    // absolutizing the path
    strPath = new File( strPath ).getAbsolutePath();

    INSTALLDIR = strPath;

    return( strPath );
  }

}//  Manual{}


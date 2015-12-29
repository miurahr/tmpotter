/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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

package org.tmpotter.ui.help;

import static org.tmpotter.util.Localization.getString;

import org.tmpotter.util.AppConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;


@SuppressWarnings("serial")
public final class Manual extends JDialog {
  
  private static final Logger LOG = Logger.getLogger(Manual.class.getName());


  final JPanel                    panel1           = new JPanel();
  final BorderLayout              borderLayout1    = new BorderLayout();
  final ArrayList                 idiomDialog      = new ArrayList();

  public File fpathHome = new File( System.getProperty( "user.dir" ) );

  final JScrollPane  scrollPanelAyuda = new JScrollPane();
  Border             border1;
  final JEditorPane  editorPanelAyuda = new JEditorPane();
  int                keyIdiom          = 2;

  //  ISO language code
  final String strIsoLang    = Locale.getDefault().getLanguage();
  final String strIsoCountry = Locale.getDefault().getCountry();

  //  Caching install dir
  private String installDir = null;

  //  Documentation
  public static final String DOC_DIR        = "doc";
  //  ToDo: fix to get Locale/Language specific user guide
  public static final String MAN_DIR        = "guide/en";
  public static final String MAN_PATH = DOC_DIR + File.separatorChar
      + MAN_DIR;

  /**
   * Show manual.
   *
   * @param frame main window frame
   * @param title dialog title
   * @param modal is modal?
   */
  public Manual( final Frame frame, final String title, final boolean modal ) {
    super( frame, title, modal );

    try {
      initialize();
      pack();
    } catch ( final Exception ex ) {
      LOG.log(Level.WARNING, "Manual error", ex);
    }
  }

  /**
   * Show manual.
   */
  public Manual() {
    //  No resize! -RM
    this( null, "", false );
    //this( null, "", true );
    setSize( new Dimension( 648, 400 ) );
  }

  private void initialize() throws Exception {
    border1 = BorderFactory.createLineBorder( Color.gray, 2 );

    panel1.setLayout( borderLayout1 );

    scrollPanelAyuda.setBorder( border1 );
    editorPanelAyuda.setText( "" );

    setModal( true );
    setTitle( getString( "DLG.HELP.TITLE" ) );

    getContentPane().add( panel1 );
    panel1.add(scrollPanelAyuda, BorderLayout.CENTER );

    scrollPanelAyuda.getViewport().add(editorPanelAyuda, null );

    editorPanelAyuda.setEditable( false );
    editorPanelAyuda.setContentType( "text/html" );

    setSize( new Dimension( 900, 720 ) );

    ver();
  }

  final void ver() {
    //  ToDo: fix to get Locale/Language specific user guide
    String manualHtmlFileName = "Bitext2tmx.html";

    try {    
      final String fullname = getAbsolutePath( manualHtmlFileName );
      final URL page = new URL( fullname );
      editorPanelAyuda.setPage( page );

    } catch ( final java.io.IOException ex ) {
      JOptionPane.showMessageDialog( panel1,
          getString( "MSG.ERROR.HELP.NOTFOUND" ),
          getString( "MSG.ERROR" ),
          JOptionPane.ERROR_MESSAGE );

      dispose();
    }
  }

  private String getAbsolutePath( final String file ) {
    try {
      //  MAN_PATH = doc/help
      return ( "file:"
        + ( new File( installDir() + File.separator 
                + MAN_PATH + File.separator + file ) ).getCanonicalPath() );
    } catch ( final IOException exception ) {
      LOG.log(Level.WARNING,
              "Exception occurred when trying to locate documentation" );
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
  private String getClassPathElement( final String strFullClassPath,
      final int positionInsideElement ) {
    //  Semicolon before path to the Jar
    int semiColon1 = strFullClassPath
        .lastIndexOf( File.pathSeparatorChar, positionInsideElement );
    //  Semicolon after path to the Jar
    int semiColon2 = strFullClassPath
        .indexOf( File.pathSeparatorChar, positionInsideElement );

    if ( semiColon1 < 0 ) {
      semiColon1 = -1;
    }
    if ( semiColon2 < 0 ) {
      semiColon2 = strFullClassPath.length();
    }

    return ( strFullClassPath.substring( ++semiColon1, semiColon2 ) );
  }


  /**
   * Trying to see if this ending is inside the classpath.
   */
  private String tryClasspathElement( final String strClassPath,
      final String strSuffix ) {
    try {
      final int position = strClassPath.indexOf( strSuffix );
      if ( position >= 0 ) {
        String strPath = getClassPathElement( strClassPath, position );
        strPath = strPath.substring( 0, strPath.indexOf( strSuffix ) );
        return ( strPath );
      }
    } catch ( final Exception e ) {
      // FIXME
    }
    return ( null );
  }

  /**
    *  Return B2T installation directory.
    * 
    * <p> Used to find the proper location of B2T documentation
    * 
    * @return install directory as string
    */
  public final String installDir() {
    if ( installDir != null ) {
      return installDir;
    }

    final String strClassPath = System.getProperty( "java.class.path" );
    String strPath;

    //  running from a Jar ?
    strPath = tryClasspathElement( strClassPath, AppConstants.APPLICATION_JAR );

    //  again missed, we're not running from Jar, most probably debug mode
    if ( strPath == null ) {
      strPath = tryClasspathElement( strClassPath, AppConstants.BUILDCLASSPATH );
    }
    
    //  WTF?!! using current directory
    if ( strPath == null ) {
      strPath = ".";
    }

    // absolutizing the path
    strPath = new File( strPath ).getAbsolutePath();

    installDir = strPath;

    return ( strPath );
  }
}
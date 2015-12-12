/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006 Susana Santos Antón
#            (C) 2006-2009 Raymond: Martin et al
#            (C) 2015 Hiroshi Miura
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


package bitext2tmx;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;

import java.util.Date;
import java.util.Locale;
import java.util.prefs.*;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.SwingUtilities;

import bitext2tmx.ui.Bitext2tmxWindow;
import bitext2tmx.ui.SplashScreen;
import bitext2tmx.util.AquaAdapter;
import bitext2tmx.util.Utilities;

import static bitext2tmx.util.Constants.*;
import static bitext2tmx.util.Utilities.*;


/**
 *
 */
final public class Bitext2tmx
{
  public Bitext2tmx()
  {
    displaySplash();
    setLnF();
    echoStartMsg();

    final Bitext2tmxWindow _wndB2T = new Bitext2tmxWindow();

    SwingUtilities.invokeLater( new Runnable()
      { public void run() { _wndB2T.setVisible( true ); } } );
  }

  public static void main( String[] straArgs ) { new Bitext2tmx(); }

  final private void echoStartMsg()
  {
    System.out.println( "\n" +
      ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" +
      "\n" + ";;  " +
      APPNAME +
      ", Locale: " + Locale.getDefault() +
      ", " + new Date() +
      "\n" );
  }

  /**  Set the Swing Look and Feel */
  final private void setLnF()
  {
    try
    {
      if( isMacOSX() )
      {
        System.setProperty( "apple.awt.graphics.UseQuartz", "true" );
        System.setProperty( "apple.laf.useScreenMenuBar", "true" );
        System.
          setProperty( "com.apple.mrj.application.apple.menu.about.name",
             "bitext2tmx" );
        //  ToDo: create (OS X) dock icon
        AquaAdapter.setDockIconImage( getIcon( "b2t-icon-large.png" ).getImage() );
      }

      UIManager.getInstalledLookAndFeels();
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    }
    //  ToDo: log proper exception messages
    catch( final ClassNotFoundException cnfe )
    {  }
    catch( final IllegalAccessException iae )
    {  }
    catch( final InstantiationException ie )
    {  }
    catch( final UnsupportedLookAndFeelException ulafe )
    {  }
  }

  private ImageIcon getIcon( final String iconName )
  { return( Utilities.getIcon( iconName, this ) ); }

  final private void displaySplash()
  {
    new Thread()
      {
        public void run()
        {
          final SplashScreen splash = new SplashScreen();
          splash.display();

          try{ sleep( 5000 ); }
          catch( InterruptedException ie ) {}

          splash.remove();
        }
      }.start();
  }

}



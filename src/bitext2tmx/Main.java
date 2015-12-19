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
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#######################################################################
*/

package bitext2tmx;


import java.util.Date;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.SwingUtilities;

import bitext2tmx.ui.Icons;
import bitext2tmx.ui.MainWindow;
import bitext2tmx.ui.SplashScreen;
import bitext2tmx.util.gui.AquaAdapter;

import bitext2tmx.util.AppConstants;
import bitext2tmx.util.Platform;


/**
 *
 */
final public class Main
{
  public Main()
  {
    setLnF();
    displaySplash();
    echoStartMsg();

    final MainWindow windowMain = new MainWindow();

    SwingUtilities.invokeLater(new Runnable()
      { public void run() { windowMain.setVisible( true ); } } );
  }

  public static void main( String[] straArgs ) { new Main(); }

  final private void echoStartMsg()
  {
    System.out.println("\n" +
      ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" +
      "\n" + ";;  " +
      AppConstants.getApplicationDisplayName() +
      ", Locale: " + Locale.getDefault() +
      ", " + new Date() +
      "\n" );
  }

  /**  Set the Swing Look and Feel */
  final private void setLnF()
  {
    try
    {
      if( Platform.isMacOSX() )
      {
        System.setProperty( "apple.awt.graphics.UseQuartz", "true" );
        System.setProperty( "apple.laf.useScreenMenuBar", "true" );
        System.
          setProperty( "com.apple.mrj.application.apple.menu.about.name",
             "bitext2tmx" );
        //  ToDo: create (OS X) dock icon
        AquaAdapter.setDockIconImage(Icons.getIcon( "icon-large.png" ).getImage() );
      }
      // Workaround for JDK bug 6389282
      // it should be called before setLookAndFeel() for GTK LookandFeel
      UIManager.getInstalledLookAndFeels();
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      System.setProperty("swing.aatext", "true");
    }
    //  ToDo: log proper exception messages
    catch( final ClassNotFoundException | IllegalAccessException | 
            InstantiationException | UnsupportedLookAndFeelException cnfe )
    {  }
  }

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



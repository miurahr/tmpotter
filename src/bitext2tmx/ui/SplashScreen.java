/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009  Raymond: Martin et al.
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
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#######################################################################
*/

package bitext2tmx.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;


public class SplashScreen extends JWindow
{
  public SplashScreen() 
  {
    ImageIcon image = null;
    JLabel    label;

    try{ label = new JLabel( Bitext2TmxIcons.getIcon( "b2t-splash.png") ); }
    catch( Exception ex )
    { label = new JLabel( "Error: unable to load image!" ); }

    getContentPane().add( label );
    pack();

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int)( dim.getWidth() - getWidth() ) / 2;
    int y = (int)( dim.getHeight() - getHeight() ) / 2;
    setLocation( x, y );
  }

  public void display(){ pack(); setVisible( true ); }
  public void remove(){ setVisible( false ); dispose(); }

}//  SpalshScreen{}


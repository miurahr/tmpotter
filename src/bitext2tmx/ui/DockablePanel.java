/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009  Raymond: Martin
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

import java.awt.Component;
import javax.swing.JPanel;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;


/**
 *  Dockable Panel
 *
 */
class DockablePanel extends JPanel implements Dockable
{
  boolean _bFloatable;

  DockKey _dockKey;
  DockingConstants.Hide _dcBorder;

  /**  Default constructor */
  protected DockablePanel( final String strKey )
  {
    _bFloatable = true;
    _dcBorder   = DockingConstants.HIDE_BOTTOM;

    //  Use default autohide position
    _dockKey = new DockKey( strKey );
    _dockKey.setAutoHideBorder( _dcBorder );
    _dockKey.setFloatEnabled( _bFloatable );
  }

  /**  Default constructor */
  protected DockablePanel( final String strKey, final String strName )
  {
    //  Use default autohide position
    this( strKey );
    _dockKey.setName( strName );
  }

  /**  Constructor invoked with additional autohide border */
  protected DockablePanel( final String strKey, final String strName, final DockingConstants.Hide dcBorder )
  {
    this( strKey, strName );
    _dockKey.setAutoHideBorder( dcBorder );
  }

  /**  Constructor invoked with additional floatable flag */
  protected DockablePanel( final String strKey, final String strName, final boolean bFloatable )
  {
    this( strKey, strName );
    _dockKey.setFloatEnabled( bFloatable );
  }

  /**  Constructor invoked with additional autohide border */
  protected DockablePanel( final String strKey, final String strName,
    final DockingConstants.Hide dcBorder, final boolean bFloatable )
  {
    this( strKey, strName, dcBorder );
    _dockKey.setFloatEnabled( bFloatable );
  }

  /**  Update docking panel name */
  final public void setName( final String strName ) { _dockKey.setName( strName ); }
  final public void setTooltip( final String strTooltip ) { _dockKey.setTooltip( strTooltip ); }

  final public DockKey getDockKey() { return( _dockKey ); }
  final public Component getComponent() { return( this ); }

}//  DockablePanel{}


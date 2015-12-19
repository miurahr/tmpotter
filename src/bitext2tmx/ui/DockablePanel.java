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
@SuppressWarnings("serial")
class DockablePanel extends JPanel implements Dockable
{
  private final DockKey dockKey;

  /**  Default constructor */
  protected DockablePanel(final String key)
  {
    //  Use default autohide position
    dockKey = new DockKey(key);
    dockKey.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    dockKey.setFloatEnabled(true);
  }

  /**  Default constructor */
  protected DockablePanel(final String key, final String name) {
    //  Use default autohide position
    this(key);
    dockKey.setName(name);
  }

  /**  Constructor invoked with additional autohide border */
  protected DockablePanel( final String key, final String name,
                           final DockingConstants.Hide dcBorder ) {
    this(key, name);
    dockKey.setAutoHideBorder(dcBorder);
  }

  /**  Constructor invoked with additional floatable flag */
  protected DockablePanel( final String key, final String name,
                           final boolean floatable ) {
    this(key, name);
    dockKey.setFloatEnabled(floatable);
  }

  /**  Constructor invoked with additional autohide border */
  protected DockablePanel( final String key, final String name,
    final DockingConstants.Hide dcBorder, final boolean floatable) {
    this(key, name, dcBorder);
    dockKey.setFloatEnabled(floatable);
  }

  /**  Update docking panel name */
  @Override
  final public void setName(final String name) {
    dockKey.setName(name);
  }
  
  final public void setTooltip(final String text) {
    dockKey.setTooltip(text);
  }

  @Override
  final public DockKey getDockKey() {
    return dockKey;
  }
  
  @Override
  final public Component getComponent() {
    return this;
  }
}//  DockablePanel{}


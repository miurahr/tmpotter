/**************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of tmpotter.
 *
 *  tmpotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  tmpotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with tmpotter.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package org.tmpotter.ui;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;

import java.awt.Component;
import javax.swing.JPanel;


/**
 *  Dockable Panel.
 *
 */
@SuppressWarnings("serial")
class DockablePanel extends JPanel implements Dockable {
  private final DockKey dockKey;

  /**
   * Default constructor.
   */
  protected DockablePanel(final String key) {
    //  Use default autohide position
    dockKey = new DockKey(key);
    dockKey.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    dockKey.setFloatEnabled(true);
  }

  /**
   * Default constructor.
   */
  protected DockablePanel(final String key, final String name) {
    //  Use default autohide position
    this(key);
    dockKey.setName(name);
  }

  /**
   * Constructor invoked with additional autohide border.
   */
  protected DockablePanel( final String key, final String name,
                           final DockingConstants.Hide dcBorder ) {
    this(key, name);
    dockKey.setAutoHideBorder(dcBorder);
  }

  /**
   * Constructor invoked with additional floatable flag.
   */
  protected DockablePanel( final String key, final String name,
                           final boolean floatable ) {
    this(key, name);
    dockKey.setFloatEnabled(floatable);
  }

  /**
   * Constructor invoked with additional autohide border.
   */
  protected DockablePanel( final String key, final String name,
      final DockingConstants.Hide dcBorder, final boolean floatable) {
    this(key, name, dcBorder);
    dockKey.setFloatEnabled(floatable);
  }

  /**
   * Update docking panel name.
   */
  @Override
  public final void setName(final String name) {
    dockKey.setName(name);
  }
  
  public final void setTooltip(final String text) {
    dockKey.setTooltip(text);
  }

  @Override
  public final DockKey getDockKey() {
    return dockKey;
  }
  
  @Override
  public final Component getComponent() {
    return this;
  }
}
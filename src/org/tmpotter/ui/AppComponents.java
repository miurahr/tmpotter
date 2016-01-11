/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
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

package org.tmpotter.ui;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.MultiSplitLayout;

import org.tmpotter.util.AppConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;


/**
 * Main window UI components.
 *
 * @author Hiroshi Miura
 */
public class AppComponents {
  private final MainWindow mainWindow;
  JXMultiSplitPane msp;
  JXPanel controlPane;
  JXPanel editPane;
  //  Statusbar
  protected JXStatusBar panelStatusBar;
  protected JXLabel labelStatusBar;
  private JXLabel tableRows; 
  //  Menubar
  protected final JMenuBar menuBar = new JMenuBar();

  /**
   * Create UI components.
   *
   * @param mainWindow parent frame
   */
  public AppComponents(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
    controlPane = new JXPanel();
    editPane = new JXPanel();
    labelStatusBar = new JXLabel(" ");
    panelStatusBar = new JXStatusBar();
    msp = new JXMultiSplitPane();
  }

  protected void makeUi() {
    // Make Status Bar
    panelStatusBar.setLayout(new BoxLayout(panelStatusBar, BoxLayout.LINE_AXIS));
    panelStatusBar.add(Box.createRigidArea(new Dimension(10, 0)));
    panelStatusBar.add(labelStatusBar, BorderLayout.SOUTH );
    // Create a Multi Split Pane model
    LinkedList<MultiSplitLayout.Node> editChildren = new LinkedList<>();
    MultiSplitLayout.Leaf leaf1 = new MultiSplitLayout.Leaf("leftEdit");
    leaf1.setWeight(0.5f);
    MultiSplitLayout.Leaf leaf2 = new MultiSplitLayout.Leaf("rightEdit");
    leaf2.setWeight(0.5f);
    editChildren.add(leaf1);
    editChildren.add(new MultiSplitLayout.Divider());
    editChildren.add(leaf2);
    MultiSplitLayout.Split edit = new MultiSplitLayout.Split();
    edit.setRowLayout(true);
    edit.setChildren(editChildren);
    LinkedList<MultiSplitLayout.Node> rootChildren = new LinkedList<>();
    MultiSplitLayout.Leaf leaf3 = new MultiSplitLayout.Leaf("view");
    leaf3.setWeight(0.5f);
    rootChildren.add(edit);
    rootChildren.add(new MultiSplitLayout.Divider());
    rootChildren.add(leaf3);
    MultiSplitLayout.Split root = new MultiSplitLayout.Split();
    root.setRowLayout(false);
    root.setChildren(rootChildren);
    msp.getMultiSplitLayout().setModel(root);    
    msp.getMultiSplitLayout().layoutByWeight( msp );
    // Arrange views
    msp.add(mainWindow.editLeftSegment, "leftEdit");
    msp.add(mainWindow.editRightSegment, "rightEdit");
    msp.add(mainWindow.tmView, "view");
    mainWindow.getContentPane().add(mainWindow.toolBar, BorderLayout.NORTH);
    mainWindow.getContentPane().add(msp);
    mainWindow.getContentPane().add(panelStatusBar, BorderLayout.SOUTH);
    mainWindow.setSize(new Dimension(1024, 768));
    mainWindow.setMinimumSize(new Dimension(640, 480));
    mainWindow.setTitle(AppConstants.getDisplayNameAndVersion());
  }

  /**
   * Updates status labels.
   */
  protected void updateStatusBar() {
    tableRows.setText("" + mainWindow.tmView.getRowCount());
  }
}

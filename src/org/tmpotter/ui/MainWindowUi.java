/**************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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

import static org.tmpotter.util.Localization.getString;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.ui.DockingUISettings;

import org.tmpotter.util.AppConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Main window UI components.
 *
 * @author Hiroshi Miura
 */
public class MainWindowUi {

  protected static void initDockingUi(MainWindow mainWindow) {
    DockingUISettings.getInstance().installUI();
    UIManager.put("DockViewTitleBar.titleFont",
            mainWindow.mainWindowFonts.getUserInterfaceFont());
    UIManager.put("DockViewTitleBar.close",
            mainWindow.getDesktopIcon("close.png"));
    UIManager.put("DockViewTitleBar.close.rollover",
            mainWindow.getDesktopIcon("close_hovered.png"));
    UIManager.put("DockViewTitleBar.close.pressed",
            mainWindow.getDesktopIcon("close_pressed.png"));
    UIManager.put("DockViewTitleBar.hide",
            mainWindow.getDesktopIcon("min.png"));
    UIManager.put("DockViewTitleBar.hide.rollover",
            mainWindow.getDesktopIcon("min_hovered.png"));
    UIManager.put("DockViewTitleBar.hide.pressed",
            mainWindow.getDesktopIcon("min_pressed.png"));
    UIManager.put("DockViewTitleBar.maximize",
            mainWindow.getDesktopIcon("max.png"));
    UIManager.put("DockViewTitleBar.maximize.rollover",
            mainWindow.getDesktopIcon("max_hovered.png"));
    UIManager.put("DockViewTitleBar.maximize.pressed",
            mainWindow.getDesktopIcon("max_pressed.png"));
    UIManager.put("DockViewTitleBar.restore",
            mainWindow.getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.restore.rollover",
            mainWindow.getDesktopIcon("restore_hovered.png"));
    UIManager.put("DockViewTitleBar.restore.pressed",
            mainWindow.getDesktopIcon("restore_pressed.png"));
    UIManager.put("DockViewTitleBar.dock",
            mainWindow.getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.dock.rollover",
            mainWindow.getDesktopIcon("restore_hovered.png"));
    UIManager.put("DockViewTitleBar.dock.pressed",
            mainWindow.getDesktopIcon("restore_pressed.png"));
    UIManager.put("DockViewTitleBar.float",
            mainWindow.getDesktopIcon("shade.png"));
    UIManager.put("DockViewTitleBar.float.rollover",
            mainWindow.getDesktopIcon("shade_hovered.png"));
    UIManager.put("DockViewTitleBar.float.pressed",
            mainWindow.getDesktopIcon("shade_pressed.png"));
    UIManager.put("DockViewTitleBar.attach",
            mainWindow.getDesktopIcon("un_shade.png"));
    UIManager.put("DockViewTitleBar.attach.rollover",
            mainWindow.getDesktopIcon("un_shade_hovered.png"));
    UIManager.put("DockViewTitleBar.attach.pressed",
            mainWindow.getDesktopIcon("un_shade_pressed.png"));
    UIManager.put("DockViewTitleBar.menu.hide",
            mainWindow.getDesktopIcon("min.png"));
    UIManager.put("DockViewTitleBar.menu.maximize",
            mainWindow.getDesktopIcon("max.png"));
    UIManager.put("DockViewTitleBar.menu.restore",
            mainWindow.getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.menu.dock",
            mainWindow.getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.menu.float",
            mainWindow.getDesktopIcon("shade.png"));
    UIManager.put("DockViewTitleBar.menu.attach",
            mainWindow.getDesktopIcon("un_shade.png"));
    UIManager.put("DockViewTitleBar.menu.close",
            mainWindow.getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.close",
            mainWindow.getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.close.rollover",
            mainWindow.getDesktopIcon("close_hovered.png"));
    UIManager.put("DockTabbedPane.close.pressed",
            mainWindow.getDesktopIcon("close_pressed.png"));
    UIManager.put("DockTabbedPane.menu.close",
            mainWindow.getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.menu.hide",
            mainWindow.getDesktopIcon("shade.png"));
    UIManager.put("DockTabbedPane.menu.maximize",
            mainWindow.getDesktopIcon("max.png"));
    UIManager.put("DockTabbedPane.menu.float",
            mainWindow.getDesktopIcon("shade.png"));
    UIManager.put("DockTabbedPane.menu.closeAll",
            mainWindow.getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.menu.closeAllOther",
            mainWindow.getDesktopIcon("close.png"));
    UIManager.put("DragControler.detachCursor",
            mainWindow.getDesktopIcon("shade.png").getImage());
    UIManager.put("DockViewTitleBar.closeButtonText",
            getString("VW.TITLEBAR.BTNCLOSE"));
    UIManager.put("DockViewTitleBar.minimizeButtonText",
            getString("VW.TITLEBAR.BTNMINIMIZE"));
    UIManager.put("DockViewTitleBar.maximizeButtonText",
            getString("VW.TITLEBAR.BTNMAXIMIZE"));
    UIManager.put("DockViewTitleBar.restoreButtonText",
            getString("VW.TITLEBAR.BTNRESTORE"));
    UIManager.put("DockViewTitleBar.floatButtonText",
            getString("VW.TITLEBAR.BTNFLOAT"));
    UIManager.put("DockViewTitleBar.attachButtonText",
            getString("VW.TITLEBAR.BTNATTACH"));
    UIManager.put("DockTabbedPane.closeButtonText",
            getString("TAB.BTNCLOSE"));
    UIManager.put("DockTabbedPane.minimizeButtonText",
            getString("TAB.BTNMINIMIZE"));
    UIManager.put("DockTabbedPane.restoreButtonText",
            getString("TAB.BTNRESTORE"));
    UIManager.put("DockTabbedPane.maximizeButtonText",
            getString("TAB.BTNMAXIMIZE"));
    UIManager.put("DockTabbedPane.floatButtonText",
            getString("TAB.BTNFLOAT"));
  }

  protected static void makeUi(MainWindow mainWindow) {
    mainWindow.labelStatusBar = new JLabel(" ");
    mainWindow.panelStatusBar = new JPanel();
    mainWindow.panelStatusBar.setLayout(new BoxLayout(mainWindow
            .panelStatusBar, BoxLayout.LINE_AXIS));
    mainWindow.panelStatusBar.add(Box.createRigidArea(new Dimension(10, 0)));
    mainWindow.panelStatusBar.add(mainWindow.labelStatusBar);
    mainWindow.desktop = new DockingDesktop();
    mainWindow.getContentPane().add(mainWindow.desktop, BorderLayout.CENTER);
    mainWindow.desktop.registerDockable(mainWindow.viewAlignments);
    mainWindow.desktop.registerDockable(mainWindow.editLeftSegment);
    mainWindow.desktop.registerDockable(mainWindow.editRightSegment);
    mainWindow.desktop.registerDockable(mainWindow.viewControls);
    DockKey keyLeftSegment = mainWindow.editLeftSegment.getDockKey();
    keyLeftSegment.setName(getString("VW.ORIGINAL.NAME"));
    keyLeftSegment.setTooltip(getString("VW.ORIGINAL.TOOLTIP"));
    keyLeftSegment.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    DockKey keyRightSegment = mainWindow.editRightSegment.getDockKey();
    keyRightSegment.setName(getString("VW.TRANSLATION.NAME"));
    keyRightSegment.setTooltip(getString("VW.TRANSLATION.TOOLTIP"));
    keyRightSegment.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    DockKey keyAlignmentTable = mainWindow.viewAlignments.getDockKey();
    keyAlignmentTable.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    DockKey keySegmentButtons = mainWindow.viewControls.getDockKey();
    keySegmentButtons.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    keyAlignmentTable.setFloatEnabled(true);
    keyLeftSegment.setFloatEnabled(true);
    keyRightSegment.setFloatEnabled(true);
    keySegmentButtons.setFloatEnabled(true);
    keyAlignmentTable.setCloseEnabled(false);
    keyLeftSegment.setCloseEnabled(false);
    keyRightSegment.setCloseEnabled(false);
    keySegmentButtons.setCloseEnabled(false);
    keySegmentButtons.setResizeWeight(0.1F);
    mainWindow.desktop.addDockable(mainWindow.viewAlignments);
    mainWindow.desktop.split(mainWindow.viewAlignments,
            mainWindow.editLeftSegment, DockingConstants.SPLIT_BOTTOM);
    mainWindow.desktop.split(mainWindow.editLeftSegment,
            mainWindow.viewControls, DockingConstants.SPLIT_BOTTOM);
    mainWindow.desktop.split(mainWindow.editLeftSegment,
            mainWindow.editRightSegment, DockingConstants.SPLIT_RIGHT);
    mainWindow.setSize(new Dimension(800, 600));
    mainWindow.setMinimumSize(new Dimension(640, 480));
    mainWindow.setTitle(AppConstants.getDisplayNameAndVersion());
    mainWindow.getContentPane().add(mainWindow.panelStatusBar, BorderLayout.SOUTH);
  }
  
}

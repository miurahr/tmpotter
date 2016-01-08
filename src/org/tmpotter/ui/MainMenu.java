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

import static org.openide.awt.Mnemonics.setLocalizedText;

import static org.tmpotter.util.Localization.getString;

import org.tmpotter.util.Platform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 * UI menu class.
 *
 * @author Hiroshi Miura
 */
final class MainMenu implements ActionListener, MenuListener {
  private static final Logger LOGGER =
          Logger.getLogger(MainMenu.class.getName());
  
  /**
   * MainWindow instance.
   */
  protected final MainWindow mainWindow;

  /**
   * MainWindow menu handler instance.
   */
  protected final MenuHandler menuHandler;

  ImageIcon getIcon(final String iconName) {
    return Icons.getIcon(iconName);
  }

  /**
   * Used by makeMenuComponent to select componenet type.
   */
  public static enum MenuComponentType {
    CHECKBOX, ITEM, MENU, RADIOBUTTON
  }

  //  File menu
  JMenu menuFile;
  JMenuItem menuItemFileTextOpen;
  JMenuItem menuItemFileClose;
  JMenuItem menuItemFileSaveAs;
  JMenuItem menuItemFileSave;
  JMenuItem menuItemFileQuit;
  JMenuItem menuItemFileOpen;
  //  Edit menu
  JMenu menuEdit;
  JMenuItem menuItemUndo;
  JMenuItem menuItemRedo;
  JMenuItem menuItemOriginalDelete;
  JMenuItem menuItemOriginalJoin;
  JMenuItem menuItemOriginalSplit;
  JMenuItem menuItemTranslationDelete;
  JMenuItem menuItemTranslationJoin;
  JMenuItem menuItemTranslationSplit;
  JMenuItem menuItemRemoveBlankRows;
  JMenuItem menuItemTuSplit;

  //  Settings menu
  JMenu menuSettings;
  JMenuItem menuItemSettingsFonts;
  //  Look and Feel submenu
  JMenu menuItemLaf;
  JMenuItem menuItemLafMetal;
  JMenuItem menuItemLafGtk;
  JMenuItem menuItemLafSystem;
  JMenuItem menuItemLafLiquid;
  JMenuItem menuItemLafNimbus;
  //  Help menu
  JMenu menuHelp;
  JMenuItem menuItemHelpAbout;
 
  /**
   * Menus initilizer/setter/getter.
   * 
   * @param mainWindow main window object
   * @param menuHandler menu handler object
   */
  public MainMenu(final MainWindow mainWindow,
          final MenuHandler menuHandler) {
    this.mainWindow = mainWindow;
    this.menuHandler = menuHandler;
    makeMenusComponents();
  }

  /**
   * Code for dispatching events from components to event handlers.
   *
   * @param evt event info
   */
  @Override
  public void menuSelected(MenuEvent evt) {
    // Item what perform event.
    JMenu menu = (JMenu) evt.getSource();

    // Get item name from actionCommand.
    String action = menu.getActionCommand();

    // Find method by item name.
    String methodName = action + "MenuSelected";
    Method method = null;
    try {
      method = menuHandler.getClass().getMethod(methodName, JMenu.class);
    } catch (NoSuchMethodException ex) {
      // method not declared
      return;
    }

    // Call ...MenuMenuSelected method.
    try {
      method.invoke(menuHandler, menu);
    } catch (IllegalAccessException ex) {
      throw new IncompatibleClassChangeError(
              "Error invoke method handler for main menu");
    } catch (InvocationTargetException ex) {
      LOGGER.log(Level.SEVERE, "Error execute method", ex);
      throw new IncompatibleClassChangeError(
              "Error invoke method handler for main menu");
    }
  }

  @Override
  public void menuCanceled(MenuEvent evt) {
  }

  @Override
  public void menuDeselected(MenuEvent evt) {
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    // Get item name from actionCommand.
    String action = evt.getActionCommand();

    //LOGGER.logRB("LOG_MENU_CLICK", action);
    // Find method by item name.
    String methodName = action + "ActionPerformed";
    Method method = null;
    try {
      method = menuHandler.getClass().getMethod(methodName);
    } catch (NoSuchMethodException ignore) {
      try {
        method = menuHandler.getClass()
                .getMethod(methodName, Integer.TYPE);
      } catch (NoSuchMethodException ex) {
        throw new IncompatibleClassChangeError(
                "Error invoke method handler for main menu: there is no method "
                        + methodName);
      }
    }
    // Call ...MenuItemActionPerformed method.
    Object[] args = method.getParameterTypes()
            .length == 0 ? null : new Object[]{evt.getModifiers()};
    try {
      method.invoke(menuHandler, args);
    } catch (IllegalAccessException ex) {
      throw new IncompatibleClassChangeError(
              "Error invoke method handler for main menu");
    } catch (InvocationTargetException ex) {
      LOGGER.log(Level.SEVERE, "Error execute method", ex);
      throw new IncompatibleClassChangeError(
              "Error invoke method handler for main menu");
    }
  }

  public final JMenu getMenuFile() {
    return menuFile;
  }

  public final JMenu getMenuEdit() {
    return menuEdit;
  }

  public final JMenu getMenuSettings() {
    return menuSettings;
  }

  public final JMenu getMenuHelp() {
    return menuHelp;
  }

  private void makeMenusComponents() {
    makeFileMenuComponents();
    makeEditMenuComponents();
    makeSettingsMenuComponents();
    makeHelpMenuComponents();
    setActionCommands();
    enableEditMenus(false);
  }

  private void makeFileMenuComponents() {
    menuFile = makeMenuComponent(MenuComponentType.MENU,
            null, null, "File", "MNU.FILE");
    menuItemFileOpen = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('O', KeyEvent.CTRL_MASK, false),
            getIcon("project_open.png"), "Open...", "MNI.FILE.OPEN");
    menuItemFileTextOpen = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('T', KeyEvent.CTRL_MASK, false),
            getIcon("project_open.png"), "Open Text...",
            "MNI.TEXTFILE.OPEN");
    menuItemFileSave = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('S', KeyEvent.CTRL_MASK, false),
            getIcon("filesave.png"), "Save", "MNI.FILE.SAVE");
    menuItemFileSave.setEnabled(false);
    menuItemFileSaveAs = makeMenuComponent(MenuComponentType.ITEM,
            null, getIcon("filesave.png"), "Save As...",
            "MNI.FILE.SAVEAS");
    menuItemFileSaveAs.setEnabled(false);
    menuItemFileClose = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('W', KeyEvent.CTRL_MASK, false),
            getIcon("fileclose.png"), "Close", "MNI.FILE.ABORT");
    menuItemFileClose.setEnabled(false);
    menuItemFileQuit = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('Q', KeyEvent.CTRL_MASK, false),
            getIcon("application-exit.png"), "Quit",
            "MNI.FILE.EXIT");
    menuFile.add(menuItemFileOpen);
    menuFile.addSeparator();
    menuFile.add(menuItemFileTextOpen);
    menuFile.addSeparator();
    menuFile.add(menuItemFileSave);
    menuFile.add(menuItemFileSaveAs);
    menuFile.addSeparator();
    menuFile.add(menuItemFileClose);
    if (!Platform.isMacOsx()) {
      menuFile.addSeparator();
      menuFile.add(menuItemFileQuit);
    }
  }

  private void makeEditMenuComponents() {
    menuEdit = makeMenuComponent(MenuComponentType.MENU, null, null,
	    "Edit", "MNU.EDIT");
    menuItemUndo = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "Undo", "MNI.EDIT.UNDO");
    menuItemRedo = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "Redo", "MNI.EDIT.REDO");
    menuItemOriginalDelete = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "OriginalDelete", "BTN.DELETE.ORIGINAL");
    menuItemOriginalJoin = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "OriginalJoin", "BTN.JOIN.ORIGINAL");
    menuItemOriginalSplit = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "OriginalSplit", "BTN.SPLIT.ORIGINAL" );
    menuItemTranslationDelete = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "TranslationDelete", "BTN.DELETE.TRANSLATION" );
    menuItemTranslationJoin = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "TranslationJoin", "BTN.JOIN.TRANSLATION" );
    menuItemTranslationSplit = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "TranslationSplit", "BTN.SPLIT.TRANSLATION" );
    menuItemRemoveBlankRows = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "RemoveBlankRow", "BTN.DELETE.BLANK.ROWS");
    menuItemTuSplit = makeMenuComponent(MenuComponentType.ITEM,
	    null, null, "TuSplit", "BTN.SPLIT.TU");
    menuEdit.add(menuItemUndo);
    menuEdit.add(menuItemRedo);
    menuEdit.add(menuItemOriginalDelete);
    menuEdit.add(menuItemOriginalJoin);
    menuEdit.add(menuItemOriginalSplit);
    menuEdit.add(menuItemTranslationDelete);
    menuEdit.add(menuItemTranslationJoin);
    menuEdit.add(menuItemTranslationSplit);
    menuEdit.add(menuItemRemoveBlankRows);
    menuEdit.add(menuItemTuSplit);
  }

  private void makeSettingsMenuComponents() {
    menuSettings = makeMenuComponent(MenuComponentType.MENU, null, null,
            "Settings", "MNU.SETTINGS");
    menuItemSettingsFonts = makeMenuComponent(MenuComponentType.ITEM,
            null, getIcon("fonts.png"), "Configure Fonts...",
            "MNI.SETTINGS.FONTS");
    menuItemSettingsFonts.setToolTipText(getString(
            "MNI.SETTINGS.FONTS.TOOLTIP"));
    menuSettings.add(menuItemSettingsFonts);
    if (!Platform.isMacOsx()) {
      menuItemLaf = makeMenuComponent(MenuComponentType.MENU, null, null,
              "Look and Feel", null);
      menuItemLafLiquid = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "Liquid", null);
      menuItemLafMetal = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "Metal", null);
      menuItemLafNimbus = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "Nimbus", null);
      menuItemLafSystem = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "System", null);
      menuItemLafLiquid.setMnemonic('L');
      menuItemLafMetal.setMnemonic('M');
      menuItemLafNimbus.setMnemonic('N');
      menuItemLafSystem.setMnemonic('Y');
      if (!Platform.isWindows()) {
        menuItemLafGtk = makeMenuComponent(MenuComponentType.ITEM, null, null,
                "Gtk", null);
        menuItemLafGtk.setMnemonic('G');
        menuItemLaf.add(menuItemLafGtk);
      }
      menuItemLaf.add(menuItemLafLiquid);
      menuItemLaf.add(menuItemLafMetal);
      menuItemLaf.add(menuItemLafNimbus);
      menuItemLaf.add(menuItemLafSystem);
      menuSettings.add(menuItemLaf);
    }
    menuSettings.add(menuItemLaf);
  }

  private void makeHelpMenuComponents() {
    menuHelp = makeMenuComponent(MenuComponentType.MENU, null, null,
            "Help", "MNU.HELP");
    menuItemHelpAbout = makeMenuComponent(MenuComponentType.ITEM, null,
            getIcon("icon-small.png"), "About", "MNI.HELP.ABOUT");
    if (!Platform.isMacOsx()) {
      menuHelp.addSeparator();
      menuHelp.add(menuItemHelpAbout);
    }
  }

  /**
   * Return a new menu component.
   *
   * <p>Can return subclasses of JMenuItem: including JMenu! Downcast return type
   * to as needed
   */
  private <T extends JMenuItem> T makeMenuComponent(
          final MenuComponentType menuComponentType, final KeyStroke ksShortcut,
          final ImageIcon icon, final String strText, final String strKey) {
    JMenuItem menuItem;
    assert strText != null;
    switch (menuComponentType) {
      case ITEM:
        menuItem = new JMenuItem();
        break;
      case CHECKBOX:
        menuItem = new JCheckBoxMenuItem();
        break;
      case MENU:
        menuItem = new JMenu();
        break;
      case RADIOBUTTON:
        menuItem = new JRadioButtonMenuItem();
        break;
      default:
        menuItem = new JMenuItem();
        break;
    }
    menuItem.setText(strText);
    if (ksShortcut != null) {
      menuItem.setAccelerator(ksShortcut);
    }
    if (!Platform.isMacOsx() && icon != null) {
      menuItem.setIcon(icon);
    }
    menuItem.addActionListener(this);
    if (strKey != null) {
      setLocalizedText(menuItem, getString(strKey));
    }
    @SuppressWarnings(value = "unchecked")
    T res = (T) menuItem;
    return res;
  }

    final void enableEditMenus( boolean enabled ) {
    menuItemRemoveBlankRows.setEnabled( enabled );
    menuItemTuSplit.setEnabled( enabled );
    menuItemOriginalJoin.setEnabled( enabled );
    menuItemOriginalDelete.setEnabled( enabled );
    menuItemOriginalSplit.setEnabled( enabled );
    menuItemTranslationJoin.setEnabled( enabled );
    menuItemTranslationDelete.setEnabled( enabled );
    menuItemTranslationSplit.setEnabled( enabled );
  }

  public final void setUndoEnabled( boolean enabled ) {
    menuItemUndo.setEnabled(enabled);
  }

  /**
   * Set 'actionCommand' for all menu items. TODO: change to key from resource
   * bundle values
   */
  protected void setActionCommands() {
    try {
      for (Field f : this.getClass().getDeclaredFields()) {
        if (JMenuItem.class.isAssignableFrom(f.getType())) {
          JMenuItem menuItem = (JMenuItem) f.get(this);
          menuItem.setActionCommand(f.getName());
        }
      }
    } catch (IllegalAccessException ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

}

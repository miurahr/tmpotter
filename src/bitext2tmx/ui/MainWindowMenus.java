/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/


package bitext2tmx.ui;

import static bitext2tmx.util.Localization.getString;
import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.util.Platform;
import bitext2tmx.util.RuntimePreferences;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 * UI menu class.
 *
 * @author Hiroshi Miura
 */
final class MainWindowMenus implements ActionListener, MenuListener {
  private static final Logger LOGGER =
          Logger.getLogger(MainWindowMenus.class.getName());
  
  /**
   * MainWindow instance.
   */
  protected final MainWindow mainWindow;

  /**
   * MainWindow menu handler instance.
   */
  protected final MainWindowMenuHandlers mainWindowMenuHandler;

  ImageIcon getIcon(final String iconName) {
    return Icons.getIcon(iconName);
  }

  /**
   * Used by makeMenuComponent to select componenet type.
   */
  public static enum MenuComponentType {
    CHECKBOX, ITEM, MENU, RADIOBUTTON
  }

  JMenuItem menuItemLafNimbus;
  JMenuItem menuItemHelpManual;
  JMenuItem menuItemFileSave;
  JMenuItem menuItemSettingsFonts;
  JMenuItem menuItemFileQuit;
  JMenuItem menuItemFileOpen;
  //  Settings menu
  JMenu menuSettings;
  JMenuItem menuItemHelpAbout;
  //  Look and Feel submenu
  JMenu menuLaf;
  JMenuItem menuItemFileClose;
  JMenuItem menuItemLafSystem;
  //  File menu
  JMenu menuItemFile;
  JMenuItem menuItemLafLiquid;
  JMenuItem menuItemFileSaveAs;
  //  Help menu
  JMenu menuHelp;
  JMenuItem menuLafMetal;
  JMenuItem menuItemLafGtk;
  JMenuItem menuItemFileTextOpen;
  
  /**
   * Menus initilizer/setter/getter.
   * 
   * @param mainWindow main window object
   * @param mainWindowMenuHandler menu handler object
   */
  public MainWindowMenus(final MainWindow mainWindow,
          final MainWindowMenuHandlers mainWindowMenuHandler) {
    this.mainWindow = mainWindow;
    this.mainWindowMenuHandler = mainWindowMenuHandler;
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
      method = mainWindowMenuHandler.getClass().getMethod(methodName, JMenu.class);
    } catch (NoSuchMethodException ex) {
      // method not declared
      return;
    }

    // Call ...MenuMenuSelected method.
    try {
      method.invoke(mainWindowMenuHandler, menu);
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
      method = mainWindowMenuHandler.getClass().getMethod(methodName);
    } catch (NoSuchMethodException ignore) {
      try {
        method = mainWindowMenuHandler.getClass()
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
      method.invoke(mainWindowMenuHandler, args);
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
    return menuItemFile;
  }
  
  public final JMenu getMenuSettings() {
    return menuSettings;
  }

  public final JMenu getMenuHelp() {
    return menuHelp;
  }

  private void makeMenusComponents() {
    menuItemFile = makeMenuComponent(MenuComponentType.MENU,
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
    menuSettings = makeMenuComponent(MenuComponentType.MENU, null, null,
            "Settings", "MNU.SETTINGS");
    menuItemSettingsFonts = makeMenuComponent(MenuComponentType.ITEM,
            null, getIcon("fonts.png"), "Configure Fonts...",
            "MNI.SETTINGS.FONTS");
    menuItemSettingsFonts.setToolTipText(getString(
            "MNI.SETTINGS.FONTS.TOOLTIP"));
    menuHelp = makeMenuComponent(MenuComponentType.MENU, null, null,
            "Help", "MNU.HELP");
    menuItemHelpAbout = makeMenuComponent(MenuComponentType.ITEM, null,
            getIcon("icon-small.png"), "About", "MNI.HELP.ABOUT");
    menuItemHelpManual = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
            getIcon("help-contents.png"), "Manual", "MNI.HELP.MANUAL");
    menuItemFile.add(menuItemFileOpen);
    menuItemFile.addSeparator();
    menuItemFile.add(menuItemFileTextOpen);
    menuItemFile.addSeparator();
    menuItemFile.add(menuItemFileSave);
    menuItemFile.add(menuItemFileSaveAs);
    menuItemFile.addSeparator();
    menuItemFile.add(menuItemFileClose);
    if (!Platform.isMacOsx()) {
      menuItemFile.addSeparator();
      menuItemFile.add(menuItemFileQuit);
    }
    menuSettings.add(menuItemSettingsFonts);
    if (!Platform.isMacOsx()) {
      menuLaf = makeMenuComponent(MenuComponentType.MENU, null, null,
              "Look and Feel", null);
      menuItemLafLiquid = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "Liquid", null);
      menuLafMetal = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "Metal", null);
      menuItemLafNimbus = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "Nimbus", null);
      menuItemLafSystem = makeMenuComponent(MenuComponentType.ITEM, null, null,
              "System", null);
      menuItemLafLiquid.setMnemonic('L');
      menuLafMetal.setMnemonic('M');
      menuItemLafNimbus.setMnemonic('N');
      menuItemLafSystem.setMnemonic('Y');
      if (!Platform.isWindows()) {
        menuItemLafGtk = makeMenuComponent(MenuComponentType.ITEM, null, null,
                "Gtk", null);
        menuItemLafGtk.setMnemonic('G');
        menuLaf.add(menuItemLafGtk);
      }
      menuLaf.add(menuItemLafLiquid);
      menuLaf.add(menuLafMetal);
      menuLaf.add(menuItemLafNimbus);
      menuLaf.add(menuItemLafSystem);
      menuSettings.add(menuLaf);
    }
    menuSettings.add(menuLaf);
    menuHelp.add(menuItemHelpManual);
    if (!Platform.isMacOsx()) {
      menuHelp.addSeparator();
      menuHelp.add(menuItemHelpAbout);
    }
    setActionCommands();
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

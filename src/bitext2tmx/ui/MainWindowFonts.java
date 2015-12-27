/**
 * ************************************************************************
 *
 * bitext2tmx - Bitext Aligner/TMX Editor
 *
 * Copyright (C) 2015 Hiroshi Miura
 *
 * This file is part of bitext2tmx.
 *
 * bitext2tmx is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * bitext2tmx is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * bitext2tmx. If not, see http://www.gnu.org/licenses/.
 *
 *************************************************************************
 */

package bitext2tmx.ui;

import bitext2tmx.util.Platform;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Font setup for main window.
 * 
 * @author Hiroshi Miura
 */
public class MainWindowFonts {
  
  private Font fontTable;
  private Font fontTableHeader;
  private Font fontSourceEditor;
  private Font fontTranslationEditor;
  private Font fontUserInterface;

  public MainWindowFonts(MainWindow mainWindow, MainWindowMenus mainWindowMenu) {
  }
  
  /**
   * User interface components font mutator.
   *
   * <p>Acts as delegate for
   * setUserInterfaceFont()
   *
   * @param font UI font to be set
   */
  private final void setUserInterfaceFonts(final Font font,
          MainWindowMenus mainWindowMenu) {
    mainWindowMenu.menuItemFile.setFont(font);
    mainWindowMenu.menuItemFileOpen.setFont(font);
    mainWindowMenu.menuItemFileTextOpen.setFont(font);
    mainWindowMenu.menuItemFileSave.setFont(font);
    mainWindowMenu.menuItemFileSaveAs.setFont(font);
    mainWindowMenu.menuItemFileClose.setFont(font);
    if (!Platform.isMacOsx()) {
      mainWindowMenu.menuItemFileQuit.setFont(font);
    }
    mainWindowMenu.menuSettings.setFont(font);
    mainWindowMenu.menuItemSettingsFonts.setFont(font);
    mainWindowMenu.menuCallbackSettingsLinebreak.setFont(font);
    if (!Platform.isMacOsx()) {
      mainWindowMenu.menuLaf.setFont(font);
      mainWindowMenu.menuItemLafLiquid.setFont(font);
      mainWindowMenu.menuLafMetal.setFont(font);
      mainWindowMenu.menuItemLafNimbus.setFont(font);
      mainWindowMenu.menuItemLafSystem.setFont(font);
      if (!Platform.isWindows()) {
        mainWindowMenu.menuItemLafGtk.setFont(font);
      }
    }
    mainWindowMenu.menuHelp.setFont(font);
    mainWindowMenu.menuItemHelpManual.setFont(font);
    if (!Platform.isMacOsx()) {
      mainWindowMenu.menuItemHelpAbout.setFont(font);
    }
  }

  /**
   * Font style accessor.
   *
   * @param strFontStyle font style string
   * @return int font style
   */
  public final int getFontStyle(final String strFontStyle) {
    final int iFontStyle;
    if (strFontStyle.equals("Bold+Italic")) {
      iFontStyle = Font.BOLD + Font.ITALIC;
    } else if (strFontStyle.equals("Italic")) {
      iFontStyle = Font.ITALIC;
    } else if (strFontStyle.equals("Bold")) {
      iFontStyle = Font.BOLD;
    } else if (strFontStyle.equals("Plain")) {
      iFontStyle = Font.PLAIN;
    } else {
      iFontStyle = Font.PLAIN;
    }
    return iFontStyle;
  }


  /**
   * Font style string accessor.
   *
   * @param font to retrieve
   * @return String style
   */
  public final String getFontStyleString(final Font font) {
    final String strFontStyle;
    if (font.isBold() && font.isItalic()) {
      strFontStyle = "Bold+Italic";
    } else if (font.isItalic()) {
      strFontStyle = "Italic";
    } else if (font.isBold()) {
      strFontStyle = "Bold";
    } else if (font.isPlain()) {
      strFontStyle = "Plain";
    } else {
      strFontStyle = "Plain";
    }
    return strFontStyle;
  }

  /**
   * Original editor font mutator.
   *
   * @param font set editor font to display
   */
  public final void setSourceEditorFont(final Font font, MainWindow mainWindow) {
    fontSourceEditor = font;
    if (fontSourceEditor == null) {
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;
      fontSourceEditor = new Font(strFontName, getFontStyle(strFontStyle),
              iFontSize);
    }
    mainWindow.editLeftSegment.setEditorFont(fontSourceEditor);
  }

  /**
   * User interface font accessor.
   *
   * @return Font
   */
  public final Font getUserInterfaceFont() {
    return fontUserInterface;
  }

  /**
   * Table header font accessor.
   *
   * @return Font
   */
  public final Font getTableHeaderFont() {
    return fontTableHeader;
  }

  /**
   * Translation editor font mutator.
   *
   * @param font to be set to Editor
   */
  public final void setTargetEditorFont(final Font font, MainWindow mainWindow) {
    fontTranslationEditor = font;
    if (fontTranslationEditor == null) {
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;
      fontTranslationEditor = new Font(strFontName, getFontStyle(strFontStyle),
              iFontSize);
    }
    mainWindow.editRightSegment.setEditorFont(fontTranslationEditor);
  }

  /**
   * Table font accessor.
   *
   * @return Font retrieve font for table
   */
  public final Font getTableFont() {
    return fontTable;
  }

  /**
   * Fonts accessor.
   *
   * @return Font[]
   */
  public final Font[] getFonts() {
    final Font[] afnt = {fontUserInterface, fontTable, fontTableHeader,
      fontSourceEditor, fontTranslationEditor};
    return afnt;
  }

  /**
   * Table font mutator.
   *
   * @param font to be set to table
   */
  public final void setTableFont(final Font font, MainWindow mainWindow) {
    fontTable = font;
    if (fontTable == null) {
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;
      fontTable = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }
    mainWindow.viewAlignments.setTableFont(fontTable);
  }

  /**
   * User interface font mutator.
   *
   * @param font UI font
   */
  public final void setUserInterfaceFont(final Font font,
          MainWindow mainWindow) {
    fontUserInterface = font;
    if (fontUserInterface == null) {
      final String strFontName = "Serif";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;
      fontUserInterface = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }
    setUserInterfaceFonts(fontUserInterface, mainWindow.mainWindowMenu);
  }

  /**
   * Original editor font accessor.
   *
   * @return font
   */
  public final Font getSourceEditorFont() {
    return fontSourceEditor;
  }

  /**
   * Translation editor font accessor.
   *
   * @return Font
   */
  public final Font getTargetEditorFont() {
    return fontTranslationEditor;
  }

  /**
   * Table header font mutator.
   *
   * @param font to be set
   */
  public final void setTableHeaderFont(final Font font) {
    fontTableHeader = font;
    if (fontTableHeader == null) {
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;
      fontTableHeader = new Font(strFontName, getFontStyle(strFontStyle),
              iFontSize);
    }
  }
  
}

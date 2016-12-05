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

import org.tmpotter.util.Platform;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Font setup for main window.
 *
 * @author Hiroshi Miura
 */
public class WindowFontManager {

  private Font fontTable;
  private Font fontTableHeader;
  private Font fontSourceEditor;
  private Font fontTranslationEditor;
  private Font uiFont;

  private final MainWindow mainWindow;

  /**
   * Constructor.
   *
   * @param mainWindow main frame owner
   */
  public WindowFontManager(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
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
    } else { // strFontStyle.equals("Plain") and others
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
    } else { // font.isPlain() and others
      strFontStyle = "Plain";
    }
    return strFontStyle;
  }

  /**
   * Original editor font mutator.
   *
   * @param font set editor font to display
   */
  public final void setSourceEditorFont(final Font font) {
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
  public final Font getUiFont() {
    return uiFont;
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
  public final void setTargetEditorFont(final Font font) {
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
    final Font[] afnt = {uiFont, fontTable, fontTableHeader,
      fontSourceEditor, fontTranslationEditor};
    return afnt;
  }

  /**
   * Table font mutator.
   *
   * @param font to be set to table
   */
  public final void setTableFont(final Font font) {
    fontTable = font;
    if (fontTable == null) {
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;
      fontTable = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }
    mainWindow.tmView.setTableFont(fontTable);
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

  /**
   * Font family names accessor.
   *
   * @return String[] font family names
   */
  public final String[] getFontFamilyNames() {
    GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return graphics.getAvailableFontFamilyNames();
  }

  /**
   * Fonts mutator Delegates actual setting of fonts to specific methods.
   *
   * <p>
   * Passing in null causes default values to be used - used at startup or for reset Passing in a
   * font causes all UI elements to be the same - used with the 'All' window area when selected in
   * the fonts dialog
   *
   * @param font to be configured
   */
  public final void setFonts(final Font font) {
    setTableFont(font);
    setTableHeaderFont(font);
    setSourceEditorFont(font);
    setTargetEditorFont(font);
    mainWindow.alignToolBar.setFonts(font);
  }

}

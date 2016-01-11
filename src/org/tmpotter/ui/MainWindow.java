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

import static org.tmpotter.util.Localization.getString;

import org.tmpotter.core.Document;
import org.tmpotter.core.SegmentChanges;
import org.tmpotter.util.Platform;
import org.tmpotter.util.Utilities;
import org.tmpotter.util.gui.AquaAdapter;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.tmpotter.core.Segment;
import static org.tmpotter.util.StringUtil.formatText;
import static org.tmpotter.util.StringUtil.restoreText;


/**
 * Main window class.
 * 
 */
@SuppressWarnings("serial")
public final class MainWindow extends JFrame implements ModelMediator, WindowListener {
  protected final ToolBar toolBar = new ToolBar();
  protected final SegmentEditor editLeftSegment = new SegmentEditor();
  protected final SegmentEditor editRightSegment = new SegmentEditor();
  protected final TmView tmView = new TmView();

  protected MenuHandler menuHandler = new MenuHandler(this);
  protected MainMenu mainMenu = new MainMenu(this);
  protected WindowFonts windowFonts = new WindowFonts(this);
  protected AppComponents uiComponents = new AppComponents(this);

  protected Document documentOriginal;
  protected Document documentTranslation;

  protected final ArrayList arrayListBitext = new ArrayList();;

  protected final ArrayList<SegmentChanges> arrayListChanges = new ArrayList<>();;
  protected final ArrayList arrayListLang = new ArrayList();;

  protected int topArrays;    //  =  0;
  protected int positionTextArea;  //  =  0;
  protected int identChanges = -1;
  protected int identLabel;  //  =  0;
  protected int identAnt;     //  =  0;

  protected String stringLangOriginal = "en";
  protected String stringLangTranslation = "en";
  protected String stringOriginal;
  protected String stringTranslation;

  protected File userHome = new File(System.getProperty("user.home"));
  protected File filePathOriginal;
  protected File filePathTranslation;

  private static final Logger LOG = Logger.getLogger(MainWindow.class.getName());


  /**
   * Main window class.
   * 
   */
  public MainWindow() {
    tmView.setModelMediator(this);
    toolBar.setModelMediator(this);
    editLeftSegment.setModelMediator(this);
    editRightSegment.setModelMediator(this);

    makeMenus();
    makeUi();
    setMacProxy();
    setCloseHandler();
    setFrameSize();
    setFonts();
  }

  protected ImageIcon getDesktopIcon(final String iconName) {
    if (Platform.isMacOsx()) {
      return (mainMenu.getIcon("desktop/osx/" + iconName));
    }
    return (mainMenu.getIcon("desktop/" + iconName));
  }

  private void makeUi() {
    uiComponents.makeUi();
  }

  private void setMacProxy() {
    //  Proxy callbacks from/to Mac OS X Aqua global menubar for Quit and About
    try {
      AquaAdapter.connect(menuHandler, "displayAbout", AquaAdapter.AquaEvent.ABOUT);
      AquaAdapter.connect(menuHandler, "quit", AquaAdapter.AquaEvent.QUIT);
    } catch (final NoClassDefFoundError e) {
      System.out.println(e);
    }
  }

  private void makeMenus() {
    uiComponents.menuBar.add(mainMenu.getMenuFile());
    uiComponents.menuBar.add(mainMenu.getMenuEdit());
    uiComponents.menuBar.add(mainMenu.getMenuSettings());
    uiComponents.menuBar.add(mainMenu.getMenuHelp());
    setJMenuBar(uiComponents.menuBar);
  }

  private void setCloseHandler() {
    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public final void windowClosing(final WindowEvent event) {
        menuHandler.quit();
      }
    });
  }

  private void setFrameSize() {
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension frameSize = this.getSize();

    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);
  }

  /**
   * Updates the changes adding a "join" change in the "undo" array and performs
   * the "join". (not sure about the translation)
   *
   * @param textAreaIzq :TRUE if the left text (source text) has to be joined
   */
  private void join(final boolean textAreaIzq) {
    if (identLabel != topArrays) {
      final SegmentChanges Changes = new SegmentChanges(0, positionTextArea,
              textAreaIzq, "", identLabel);
      arrayListChanges.add(identChanges, Changes);

      if (textAreaIzq) {
        Changes.setFrase(documentOriginal.get(identLabel));
      } else {
        Changes.setFrase(documentTranslation.get(identLabel));
      }

      if (textAreaIzq) {
        documentOriginal.join(identLabel);
      } else {
        documentTranslation.join(identLabel);
      } 
    }
  }

  /**
   * Delete text. 
   * 
   * <p>This function updates the changes adding a delete change
   * to the undo array and deletes
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be deleted
   */
  private void delete(final boolean textAreaIzq) {
    final SegmentChanges Changes = new SegmentChanges(1, positionTextArea,
            textAreaIzq, "", identLabel);
    arrayListChanges.add(identChanges, Changes);

    if (textAreaIzq) {
      Changes.setFrase(documentOriginal.get(identLabel));
    } else {
      Changes.setFrase(documentTranslation.get(identLabel));
    }

    if (textAreaIzq) {
      documentOriginal.delete(identLabel);
    } else {
      documentTranslation.delete(identLabel);
    }
  }

  /**
   * Function Split. 
   * 
   * <p>This function updates the changes adding a split to the undo
   * array and performs the splitting
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be split
   */
  private void split(final boolean textAreaIzq) {
    if (textAreaIzq) {
      if (positionTextArea >= documentOriginal.get(identLabel).length()) {
        positionTextArea = 0;
      }
    } else if (positionTextArea >= documentTranslation.get(identLabel).length()) {
      positionTextArea = 0;
    }
    final SegmentChanges Changes = new SegmentChanges(2, positionTextArea,
            textAreaIzq, "", identLabel);
    arrayListChanges.add(identChanges, Changes);
    if (textAreaIzq) {
      Changes.setFrase(documentOriginal.get(identLabel));
    } else {
      Changes.setFrase(documentTranslation.get(identLabel));
    }

    if (textAreaIzq) {
      documentOriginal.split(identLabel, Changes.getPosition());
    } else {
      documentTranslation.split(identLabel, Changes.getPosition());
    }
  }

  /**
   * Update the row in table with mods.
   * 
   * <p>This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  @Override
  public void updateTmView() {
    if (!documentOriginal.isEmpty()
            && !documentTranslation.isEmpty()) {
      matchArrays();
    }
    for (int cont = 0; cont < tmView.getRowCount(); cont++) {
      tmView.setModelValueAt("", cont, 0);
      tmView.setModelValueAt("", cont, 1);
      tmView.setModelValueAt("", cont, 2);
    }
    if ((tmView.getRowCount() > documentOriginal.size())
            && (documentOriginal.size() > 25)) {
      while (tmView.getRowCount() != documentOriginal.size()) {
        tmView.removeSegment(tmView.getRowCount() - 1);
        tmView.setPreferredSize(805, 15, -1);
      }
    } else if (tmView.getRowCount() < documentOriginal.size()) {
      while (tmView.getRowCount() != documentOriginal.size()) {
        tmView.addModelSegment(new Segment(null, null, null));
        tmView.setPreferredSize(805, 15, 1);
      }
    }
    for (int cont = 0; cont < documentOriginal.size(); cont++) {
      tmView.setModelValueAt(Integer.toString(cont + 1), cont, 0);
      tmView.setModelValueAt(documentOriginal.get(cont), cont, 1);
    }
    for (int cont = 0; cont < documentTranslation.size(); cont++) {
      tmView.setModelValueAt(documentTranslation.get(cont), cont, 2);
    }
    if (identLabel == topArrays) {
      tmView.setRowSelectionInterval(topArrays, topArrays);
    }
    tmView.repaint(100);
    editLeftSegment.setText(formatText(tmView.getValueAt(identLabel, 1).toString()));
    editRightSegment.setText(formatText(tmView.getValueAt(identLabel, 2).toString()));
    tmView.updateUI();
  }

  @Override
  public void onTableClicked() {
    positionTextArea = 0;
    if (identAnt < documentOriginal.size()) {
      documentOriginal.set(identAnt,
              restoreText(editLeftSegment.getText()));
      documentTranslation.set(identAnt,
              restoreText(editRightSegment.getText()));
    }
    editLeftSegment.setText(formatText(tmView.getValueAt(tmView.getSelectedRow(),
            1).toString()));
    editRightSegment.setText(formatText(tmView.getValueAt(tmView.getSelectedRow(),
            2).toString()));
    identLabel = tmView.getSelectedRow();
    identAnt = identLabel;
    if (identLabel == topArrays) {
      toolBar.setTranslationJoinEnabled(false);
      toolBar.setOriginalJoinEnabled(false);
    } else {
      toolBar.setTranslationJoinEnabled(true);
      toolBar.setOriginalJoinEnabled(true);
    }
    updateTmView();
  }

  @Override
  public void onTablePressed(final KeyEvent event) {
    int fila;
    if (tmView.getSelectedRow() != -1) {
      fila = tmView.getSelectedRow();
      positionTextArea = 0;
    } else {
      fila = 1;
    }
    if (fila < tmView.getRowCount() - 1) {
      if ((event.getKeyCode() == KeyEvent.VK_DOWN)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
        if (identAnt < documentOriginal.size()) {
          documentOriginal.set(identAnt,
                  restoreText(editLeftSegment.getText()));
          documentTranslation.set(identAnt,
                  restoreText(editRightSegment.getText()));
        }
        editLeftSegment.setText(formatText(tmView.getValueAt(fila + 1, 1)
                .toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(fila + 1, 2)
                .toString()));
        identLabel = fila + 1;
      } else if ((event.getKeyCode() == KeyEvent.VK_UP)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
        identLabel = fila - 1;
        if (fila == 0) {
          fila = 1;
          identLabel = 0;
        }
        if (identAnt < documentOriginal.size()) {
          documentOriginal.set(identAnt,
                  restoreText(editLeftSegment.getText()));
          documentTranslation.set(identAnt,
                  restoreText(editRightSegment.getText()));
        }
        editLeftSegment.setText(formatText(tmView.getValueAt(fila - 1, 1)
                .toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(fila - 1, 2)
                .toString()));
      }
      if (identLabel == topArrays) {
        toolBar.setTranslationJoinEnabled(false);
        toolBar.setOriginalJoinEnabled(false);
      } else {
        toolBar.setTranslationJoinEnabled(true);
        toolBar.setOriginalJoinEnabled(true);
      }
      identAnt = identLabel;
    }
    updateTmView();
  }

  
  /**
   * Join on Original.
   */
  @Override
  public final void onOriginalJoin() {
    identChanges++;
    join(true);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * Delete on original document.
   */
  @Override
  public final void onOriginalDelete() {
    identChanges++;
    delete(true);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * Split on original document.
   */
  @Override
  public final void onOriginalSplit() {
    identChanges++;
    split(true);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * join on translation document.
   */
  @Override
  public final void onTranslationJoin() {
    identChanges++;
    join(false);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * delete on translation document.
   */
  @Override
  public final void onTranslationDelete() {
    identChanges++;
    delete(false);
    updateTmView();
    toolBar.setUndoEnabled( true );
    mainMenu.setUndoEnabled(true);
  }

  /**
   * split on translation document.
   */
  @Override
  public final void onTranslationSplit() {
    identChanges++;
    split(false);
    updateTmView();
    toolBar.setUndoEnabled( true );
    mainMenu.setUndoEnabled(true);
  }

  //  Accessed by ControlView
  public void onUndo() {
    menuHandler.undoChanges();
    arrayListChanges.remove(identChanges);
    identChanges--;

    if (identChanges == -1) {
      toolBar.setUndoEnabled(false);
      mainMenu.setUndoEnabled(false);
    }
  }

  /**
   * Set position.
   * @param position indicate where in int
   */
  @Override
  public final void setTextAreaPosition(int position) {
    positionTextArea = position;
  }

  /**
   * remove blank rows in TMView.
   */
  @Override
  public final void onRemoveBlankRows() {
    int maxTamArrays = 0;
    int cont = 0;
    int lineasLimpiar = 0;
    final int[] numEliminadas = new int[1000];  // default = 1000 - why?
    int cont2 = 0;

    maxTamArrays = Utilities.largerSize(documentOriginal.size(), documentTranslation.size()) - 1;

    while (cont <= (maxTamArrays - lineasLimpiar)) {
      if ((documentOriginal.get(cont) == null 
              || documentOriginal.get(cont).equals(""))
            && (documentTranslation.get(cont) == null 
              || documentTranslation.get(cont).equals(""))) {
        lineasLimpiar++;
        numEliminadas[cont2] = cont + cont2;
        cont2++;
        documentOriginal.remove(cont);
        documentTranslation.remove(cont);
      } else {
        cont++;
      }
    }

    JOptionPane.showMessageDialog(this, getString("MSG.ERASED") + " "
            + lineasLimpiar + " " + getString("MSG.BLANK_ROWS"));

    if (lineasLimpiar > 0) {
      identChanges++;

      SegmentChanges changes = new SegmentChanges(3, 0, false, "", 0);
      arrayListChanges.add(identChanges, changes);
      changes.setNumEliminada(numEliminadas, lineasLimpiar);
      toolBar.setUndoEnabled(true);
      mainMenu.menuItemUndo.setEnabled(true);
      updateTmView();
    }
  }

  /**
   * Split on TU.
   */
  @Override
  public final void onTuSplit() {
    int izq;
    int cont;
    SegmentChanges changes;
    identChanges++;

    izq = tmView.getSelectedColumn();

    documentOriginal.add(documentOriginal.size(),
            documentOriginal.get(documentOriginal.size() - 1));
    documentTranslation.add(documentTranslation.size(),
            documentTranslation.get(documentTranslation.size() - 1));

    if (izq == 1) {
      // Columna izq.
      // Left column.
      changes = new SegmentChanges(4, 0, true, "", identLabel);

      for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
        documentTranslation.set(cont, documentTranslation.get(cont - 1));

        if (cont > (identLabel + 1)) {
          documentOriginal.set(cont, documentOriginal.get(cont - 1));
        } else {
          documentOriginal.set(cont, "");
        }
      }

      documentTranslation.set(identLabel, "");
    } else {
      changes = new SegmentChanges(4, 0, false, "", identLabel);

      for (cont = documentOriginal.size() - 1; cont > identLabel; cont--) {
        documentOriginal.set(cont, documentOriginal.get(cont - 1));

        if (cont > (identLabel + 1)) {
          documentTranslation.set(cont, documentTranslation.get(cont - 1));
        } else {
          documentTranslation.set(cont, "");
        }
      }

      documentOriginal.set(identLabel, "");
    }

    arrayListChanges.add(identChanges, changes);
    updateTmView();
    toolBar.buttonUndo.setEnabled(true);
    mainMenu.menuItemUndo.setEnabled(true);

  }

  /**
   * Fonts mutator Delegates actual setting of fonts to specific methods.
   *
   * <p> Passing in null causes default values to be used - used at startup or for
   * reset Passing in a font causes all UI elements to be the same - used with
   * the 'All' window area when selected in the fonts dialog
   *
   * @param font to be configured
   */
  public final void setFonts(final Font font) {
    windowFonts.setUiFont(font);
    windowFonts.setTableFont(font, this);
    windowFonts.setTableHeaderFont(font);
    windowFonts.setSourceEditorFont(font);
    windowFonts.setTargetEditorFont(font, this);
    toolBar.setFonts(font);
  }

  private void setFonts() {
    setFonts(null);
  }

  public final void setTableFont(final Font font) {
    windowFonts.setTableFont(font, this);
  }

  public final void setUserInterfaceFont(final Font font) {
    windowFonts.setUiFont(font);
  }

  public final void setTableHeaderFont(final Font font) {
    windowFonts.setTableHeaderFont(font);
  }

  public final void setSourceEditorFont(final Font font) {
    windowFonts.setSourceEditorFont(font);
  }

  public final void setTargetEditorFont(final Font font) {
    windowFonts.setTargetEditorFont(font, this);
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


  //  WindowListener Overrides
  @Override
  public final void windowActivated(final WindowEvent evt) {
  }

  @Override
  public final void windowClosed(final WindowEvent evt) {
  }

  @Override
  public final void windowClosing(final WindowEvent evt) {
    if (evt.getSource() == this) {
      menuHandler.menuItemFileQuitActionPerformed();
    }
  }

  @Override
  public final void windowDeactivated(final WindowEvent evt) {
  }

  @Override
  public final void windowDeiconified(final WindowEvent evt) {
  }

  @Override
  public final void windowIconified(final WindowEvent evt) {
  }

  @Override
  public final void windowOpened(final WindowEvent evt) {
  }

  /**
   * Function IgualarArrays: adds rows to the smallest array and deletes blank
   * rows.
   */
  void matchArrays() {
    boolean limpiar = true;
    while (documentOriginal.size() > documentTranslation.size()) {
      documentTranslation.add(documentTranslation.size(), "");
    }
    while (documentTranslation.size() > documentOriginal.size()) {
      documentOriginal.add(documentOriginal.size(), "");
    }
    while (limpiar) {
      if (documentOriginal.get(documentOriginal.size() - 1) == null
          || (documentOriginal.get(documentOriginal.size() - 1).equals(""))
          && (documentTranslation.get(documentTranslation.size() - 1) == null
          || documentTranslation.get(documentTranslation.size() - 1)
                  .equals(""))) {
        documentOriginal.remove(documentOriginal.size() - 1);
        documentTranslation.remove(documentTranslation.size() - 1);
      } else {
        limpiar = false;
      }
    }
    topArrays = documentOriginal.size() - 1;
    if (identLabel > (documentOriginal.size() - 1)) {
      identLabel = documentOriginal.size() - 1;
    }
  }

}

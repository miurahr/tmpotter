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
import static org.tmpotter.util.StringUtil.formatText;
import static org.tmpotter.util.StringUtil.restoreText;

import org.tmpotter.core.Segment;
import org.tmpotter.core.SegmentChanges;
import org.tmpotter.util.Platform;
import org.tmpotter.util.Utilities;
import org.tmpotter.util.gui.AquaAdapter;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


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

  protected MainMenu mainMenu = new MainMenu(this);
  protected WindowFontManager fontManager = new WindowFontManager(this);
  protected AppComponentsManager appComponentsManager = new AppComponentsManager(this);
  protected TmData tmData = new TmData();
  protected MenuHandler menuHandler;

  protected File userHome = new File(System.getProperty("user.home"));

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
    menuHandler = new MenuHandler(this, tmData);

    appComponentsManager.makeMenus(this);
    appComponentsManager.makeUi();
    setMacProxy();
    setCloseHandler();
    setMainFrameSize();
    fontManager.setFonts(null);
  }

  protected ImageIcon getDesktopIcon(final String iconName) {
    if (Platform.isMacOsx()) {
      return (mainMenu.getIcon("desktop/osx/" + iconName));
    }
    return (mainMenu.getIcon("desktop/" + iconName));
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

  private void setCloseHandler() {
    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public final void windowClosing(final WindowEvent event) {
        menuHandler.quit();
      }
    });
  }

  private void setMainFrameSize() {
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
   * Update the row in table with mods.
   * 
   * <p>This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  @Override
  public void updateTmView() {
    updateTmView2();
    editLeftSegment.setText(formatText(tmView.getValueAt(tmData.identLabel, 1).toString()));
    editRightSegment.setText(formatText(tmView.getValueAt(tmData.identLabel, 2).toString()));
  }

  private void updateTmView2() {
    if (!tmData.isSomeDocumentEmpty()) {
      tmData.matchArrays();
    }
    for (int cont = 0; cont < tmView.getRowCount(); cont++) {
      tmView.setModelValueAt("", cont, 0);
      tmView.setModelValueAt("", cont, 1);
      tmView.setModelValueAt("", cont, 2);
    }
    if ((tmView.getRowCount() > tmData.getDocumentOriginalSize())
            && (tmData.getDocumentOriginalSize() > 25)) {
      while (tmView.getRowCount() != tmData.getDocumentOriginalSize()) {
        tmView.removeSegment(tmView.getRowCount() - 1);
        tmView.setPreferredSize(805, 15, -1);
      }
    } else if (tmView.getRowCount() < tmData.getDocumentOriginalSize()) {
      while (tmView.getRowCount() != tmData.getDocumentOriginalSize()) {
        tmView.addModelSegment(new Segment(null, null, null));
        tmView.setPreferredSize(805, 15, 1);
      }
    }
    for (int cont = 0; cont < tmData.getDocumentOriginalSize(); cont++) {
      tmView.setModelValueAt(Integer.toString(cont + 1), cont, 0);
      tmView.setModelValueAt(tmData.getDocumentOriginal(cont), cont, 1);
    }
    for (int cont = 0; cont < tmData.getDocumentTranslationSize(); cont++) {
      tmView.setModelValueAt(tmData.getDocumentTranslation(cont), cont, 2);
    }
    if (tmData.isIdentTop()) {
      tmView.setRowSelectionInterval(tmData.topArrays, tmData.topArrays);
    }
    tmView.repaint(100);
    tmView.updateUI();
  }

  @Override
  public void onTableClicked() {
    tmData.positionTextArea = 0;
    if (tmData.identAnt < tmData.getDocumentOriginalSize()) {
      tmData.setOriginalDocumentAnt(restoreText(editLeftSegment.getText()));
      tmData.setTranslationDocumentAnt(restoreText(editRightSegment.getText()));
    }
    editLeftSegment.setText(formatText(tmView.getValueAt(tmView.getSelectedRow(),
            1).toString()));
    editRightSegment.setText(formatText(tmView.getValueAt(tmView.getSelectedRow(),
            2).toString()));
    tmData.setBothIdent( tmView.getSelectedRow());
    if (tmData.isIdentTop()) {
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
      tmData.positionTextArea = 0;
    } else {
      fila = 1;
    }
    if (fila < tmView.getRowCount() - 1) {
      if ((event.getKeyCode() == KeyEvent.VK_DOWN)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
        if (tmData.identAnt < tmData.documentOriginal.size()) {
          tmData.documentOriginal.set(tmData.identAnt,
                  restoreText(editLeftSegment.getText()));
          tmData.documentTranslation.set(tmData.identAnt,
                  restoreText(editRightSegment.getText()));
        }
        editLeftSegment.setText(formatText(tmView.getValueAt(fila + 1, 1)
                .toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(fila + 1, 2)
                .toString()));
        tmData.identLabel = fila + 1;
      } else if ((event.getKeyCode() == KeyEvent.VK_UP)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
        tmData.identLabel = fila - 1;
        if (fila == 0) {
          fila = 1;
          tmData.identLabel = 0;
        }
        if (tmData.identAnt < tmData.getDocumentOriginalSize()) {
          tmData.setOriginalDocumentAnt(restoreText(editLeftSegment.getText()));
          tmData.setTranslationDocumentAnt(restoreText(editRightSegment.getText()));
        }
        editLeftSegment.setText(formatText(tmView.getValueAt(fila - 1, 1)
                .toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(fila - 1, 2)
                .toString()));
      }
      if (tmData.identLabel == tmData.topArrays) {
        toolBar.setTranslationJoinEnabled(false);
        toolBar.setOriginalJoinEnabled(false);
      } else {
        toolBar.setTranslationJoinEnabled(true);
        toolBar.setOriginalJoinEnabled(true);
      }
      tmData.identAnt = tmData.identLabel;
    }
    updateTmView();
  }

  
  /**
   * Join on Original.
   */
  @Override
  public final void onOriginalJoin() {
    tmData.incrementChanges();
    tmData.join(true);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * Delete on original document.
   */
  @Override
  public final void onOriginalDelete() {
    tmData.incrementChanges();
    tmData.delete(true);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * Split on original document.
   */
  @Override
  public final void onOriginalSplit() {
    tmData.incrementChanges();
    tmData.split(true);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * join on translation document.
   */
  @Override
  public final void onTranslationJoin() {
    tmData.incrementChanges();
    tmData.join(false);
    updateTmView();
    toolBar.setUndoEnabled(true);
    mainMenu.setUndoEnabled(true);
  }

  /**
   * delete on translation document.
   */
  @Override
  public final void onTranslationDelete() {
    tmData.incrementChanges();
    tmData.delete(false);
    updateTmView();
    toolBar.setUndoEnabled( true );
    mainMenu.setUndoEnabled(true);
  }

  /**
   * split on translation document.
   */
  @Override
  public final void onTranslationSplit() {
    tmData.incrementChanges();
    tmData.split(false);
    updateTmView();
    toolBar.setUndoEnabled( true );
    mainMenu.setUndoEnabled(true);
  }

  //  Accessed by ControlView
  public void onUndo() {
    menuHandler.undoChanges();
    tmData.arrayListChanges.remove(tmData.getIdentChanges());
    int currentChange = tmData.decrementChanges();

    if (currentChange == -1) {
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
    tmData.positionTextArea = position;
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

    maxTamArrays = Utilities.largerSize(tmData.getDocumentOriginalSize(),
        tmData.getDocumentTranslationSize()) - 1;

    while (cont <= (maxTamArrays - lineasLimpiar)) {
      if ((tmData.getDocumentOriginal(cont) == null 
              || tmData.getDocumentOriginal(cont).equals(""))
            && (tmData.getDocumentTranslation(cont) == null 
              || tmData.getDocumentTranslation(cont).equals(""))) {
        lineasLimpiar++;
        numEliminadas[cont2] = cont + cont2;
        cont2++;
        tmData.documentOriginal.remove(cont);
        tmData.documentTranslation.remove(cont);
      } else {
        cont++;
      }
    }

    JOptionPane.showMessageDialog(this, getString("MSG.ERASED") + " "
            + lineasLimpiar + " " + getString("MSG.BLANK_ROWS"));

    if (lineasLimpiar > 0) {
      tmData.incrementChanges();

      SegmentChanges changes = new SegmentChanges(3, 0, false, "", 0);
      tmData.arrayListChanges.add(tmData.getIdentChanges(), changes);
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

    izq = tmView.getSelectedColumn();

    tmData.incrementChanges();
    tmData.documentOriginal.add(tmData.documentOriginal.size(),
            tmData.documentOriginal.get(tmData.documentOriginal.size() - 1));
    tmData.documentTranslation.add(tmData.documentTranslation.size(),
            tmData.documentTranslation.get(tmData.documentTranslation.size() - 1));

    if (izq == 1) {
      // Columna izq.
      // Left column.
      changes = new SegmentChanges(4, 0, true, "", tmData.identLabel);

      for (cont = tmData.documentTranslation.size() - 1; cont > tmData.identLabel; cont--) {
        tmData.setDocumentTranslation(cont, tmData.getDocumentTranslation(cont - 1));

        if (cont > (tmData.identLabel + 1)) {
          tmData.setDocumentOriginal(cont, tmData.getDocumentOriginal(cont - 1));
        } else {
          tmData.setDocumentOriginal(cont, "");
        }
      }

      tmData.documentTranslation.set(tmData.identLabel, "");
    } else {
      changes = new SegmentChanges(4, 0, false, "", tmData.identLabel);

      for (cont = tmData.documentOriginal.size() - 1; cont > tmData.identLabel; cont--) {
        tmData.documentOriginal.set(cont, tmData.documentOriginal.get(cont - 1));

        if (cont > (tmData.identLabel + 1)) {
          tmData.setDocumentTranslation(cont, tmData.getDocumentTranslation(cont - 1));
        } else {
          tmData.setDocumentTranslation(cont, "");
        }
      }

      tmData.setDocumentOriginal(tmData.identLabel, "");
    }

    tmData.arrayListChanges.add(tmData.getIdentChanges(), changes);
    updateTmView();
    toolBar.buttonUndo.setEnabled(true);
    mainMenu.menuItemUndo.setEnabled(true);
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
}

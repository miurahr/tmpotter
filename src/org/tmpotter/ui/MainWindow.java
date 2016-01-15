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

import org.tmpotter.core.Document;
import org.tmpotter.core.ProjectProperties;
import org.tmpotter.core.TextReader;
import org.tmpotter.core.TmxReader;
import org.tmpotter.core.SegmentChanges;
import org.tmpotter.util.AppConstants;
import org.tmpotter.util.Platform;
import org.tmpotter.util.RuntimePreferences;
import org.tmpotter.util.Utilities;
import org.tmpotter.util.gui.AquaAdapter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;


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
    menuHandler.setModelMediator(this);

    appComponentsManager.makeUi();
    setJMenuBar(appComponentsManager.menuBar);
    getContentPane().add(toolBar, BorderLayout.NORTH);
    getContentPane().add(appComponentsManager.msp);
    getContentPane().add(appComponentsManager.panelStatusBar, BorderLayout.SOUTH);
    setSize(new Dimension(1024, 768));
    setMinimumSize(new Dimension(640, 480));
    setTitle(AppConstants.getDisplayNameAndVersion());

    if (Platform.isMacOsx()) {
      setMacProxy();
    }
    setCloseHandler();
    setMainFrameSize();
    fontManager.setFonts(null);
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


  public void onOpenFile(File filePathOriginal,
      String stringLangOriginal, String stringLangTranslation) {
      tmData.filePathOriginal = filePathOriginal;
      tmData.filePathTranslation = tmData.filePathOriginal;
      tmData.stringLangOriginal = stringLangOriginal;
      tmData.stringLangTranslation = stringLangTranslation;
      tmView.buildDisplay();
      try {
        ProjectProperties prop = new ProjectProperties();
        prop.setSourceLanguage(tmData.stringLangOriginal);
        prop.setTargetLanguage(tmData.stringLangTranslation);
        TmxReader reader = new TmxReader(prop, tmData.filePathOriginal);
        tmData.documentOriginal =
                reader.getOriginalDocument(tmData.documentOriginal);
        tmData.documentTranslation = 
                reader.getTranslationDocument(tmData.documentTranslation);
      } catch (Exception ex) {
        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
      }
      initializeTmView();
      updateTmView();
      toolBar.enableButtons(true);
      mainMenu.enableEditMenus(true);
      toolBar.setUndoEnabled(false);
      mainMenu.menuItemFileSave.setEnabled(true);
      mainMenu.menuItemFileSaveAs.setEnabled(true);
      mainMenu.menuItemFileClose.setEnabled(true);
  }

  @Override
  public void setOriginalProperties(File filePath, String text, String lang, String encoding) {
    tmData.originalEncoding = encoding;
    tmData.filePathOriginal = filePath;
    tmData.stringOriginal = text;
    tmData.stringLangOriginal = lang;
  }

  @Override
  public void setTargetProperties(File filePath, String text, String lang, String encoding) {
    tmData.targetEncoding = encoding;
    tmData.filePathTranslation = filePath;
    tmData.stringTranslation = text;
    tmData.stringLangTranslation = lang;
  }



  @Override
  public void loadDocumentsFromText(String stringOriginal, String stringTarget) throws IOException {
    tmData.documentOriginal = new Document();
    tmData.documentTranslation = new Document();
    tmData.documentOriginal
        = TextReader.read(tmData.stringOriginal,
            tmData.stringLangOriginal, tmData.originalEncoding);
    tmData.documentTranslation
        = TextReader.read(tmData.stringTranslation,
            tmData.stringLangTranslation, tmData.targetEncoding);
    tmData.matchArrays();

  }

  /**
   * Initialize alignment view.
   *
   * <p>
   * Extracts from the TMX those lines having information which is useful for
   * alignment, and puts them in the corresponding ArrayList's The left part in
   * _alstOriginal corresponds to source text lines and the right part in
   * _alstTranslation corresponds to the target text lines. Initialize the table
   * with one line for each left and right line
   *
   */
  protected void initializeTmView() {
    TableColumn col;
    col = tmView.getColumnModel().getColumn(1);
    col.setHeaderValue(getString("TBL.HDR.COL.SOURCE")
            + tmData.filePathOriginal.getName());
    col = tmView.getColumnModel().getColumn(2);
    col.setHeaderValue(getString("TBL.HDR.COL.TARGET")
            + tmData.filePathTranslation.getName());
    tmView.setColumnHeaderView();
    updateTmView();
    tmData.topArrays = tmData.documentOriginal.size() - 1;
    tmData.indexCurrent = 0;
  }

  /**
   * Update the row in table with mods.
   * 
   * <p>This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  @Override
  public void updateTmView() {
    if (!tmData.isSomeDocumentEmpty()) {
      tmData.matchArrays();
    }
    tmView.clearAllView();
    tmView.adjustOriginalView(tmData.getDocumentOriginalSize());
    tmView.setViewData(tmData);
    if (tmData.isIdentTop()) {
      tmView.setRowSelectionInterval(tmData.topArrays, tmData.topArrays);
    }
    tmView.repaint(100);
    tmView.updateUI();
    editLeftSegment.setText(formatText(tmView.getValueAt(tmData.indexCurrent, 1).toString()));
    editRightSegment.setText(formatText(tmView.getValueAt(tmData.indexCurrent, 2).toString()));
  }

  @Override
  public void onTableClicked() {
    tmData.positionTextArea = 0;
    if (tmData.indexPrevious < tmData.getDocumentOriginalSize()) {
      tmData.setOriginalDocumentAnt(restoreText(editLeftSegment.getText()));
      tmData.setTranslationDocumentAnt(restoreText(editRightSegment.getText()));
    }
    editLeftSegment.setText(formatText(tmView.getValueAt(tmView.getSelectedRow(),
            1).toString()));
    editRightSegment.setText(formatText(tmView.getValueAt(tmView.getSelectedRow(),
            2).toString()));
    tmData.setBothIndex( tmView.getSelectedRow());
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
        if (tmData.indexPrevious < tmData.documentOriginal.size()) {
          tmData.setOriginalDocumentAnt(restoreText(editLeftSegment.getText()));
          tmData.setTranslationDocumentAnt(restoreText(editRightSegment.getText()));
        }
        editLeftSegment.setText(formatText(tmView.getValueAt(fila + 1, 1)
                .toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(fila + 1, 2)
                .toString()));
        tmData.indexCurrent = fila + 1;
      } else if ((event.getKeyCode() == KeyEvent.VK_UP)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
        tmData.indexCurrent = fila - 1;
        if (fila == 0) {
          fila = 1;
          tmData.indexCurrent = 0;
        }
        if (tmData.indexPrevious < tmData.getDocumentOriginalSize()) {
          tmData.setOriginalDocumentAnt(restoreText(editLeftSegment.getText()));
          tmData.setTranslationDocumentAnt(restoreText(editRightSegment.getText()));
        }
        editLeftSegment.setText(formatText(tmView.getValueAt(fila - 1, 1)
                .toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(fila - 1, 2)
                .toString()));
      }
      if (tmData.isIdentTop()) {
        toolBar.setTranslationJoinEnabled(false);
        toolBar.setOriginalJoinEnabled(false);
      } else {
        toolBar.setTranslationJoinEnabled(true);
        toolBar.setOriginalJoinEnabled(true);
      }
      tmData.indexPrevious = tmData.indexCurrent;
    }
    updateTmView();
  }

  /**
   * Join on Original.
   */
  @Override
  public final void onOriginalJoin() {
    tmData.incrementChanges();
    tmData.join(TmData.Side.ORIGINAL);
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
    tmData.delete(TmData.Side.ORIGINAL);
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
    tmData.split(TmData.Side.ORIGINAL);
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
    tmData.join(TmData.Side.TRANSLATION);
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
    tmData.delete(TmData.Side.TRANSLATION);
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
    tmData.split(TmData.Side.TRANSLATION);
    updateTmView();
    toolBar.setUndoEnabled( true );
    mainMenu.setUndoEnabled(true);
  }

  //  Accessed by ControlView
  @Override
  public void onUndo() {
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
    int cleanedLines = 0;
    final int[] numCleared = new int[1000];  // default = 1000 - why?
    int cont2 = 0;

    maxTamArrays = Utilities.largerSize(tmData.getDocumentOriginalSize(),
        tmData.getDocumentTranslationSize()) - 1;

    while (cont <= (maxTamArrays - cleanedLines)) {
      if ((tmData.getDocumentOriginal(cont) == null 
              || tmData.getDocumentOriginal(cont).equals(""))
            && (tmData.getDocumentTranslation(cont) == null 
              || tmData.getDocumentTranslation(cont).equals(""))) {
        cleanedLines++;
        numCleared[cont2] = cont + cont2;
        cont2++;
        tmData.documentOriginal.remove(cont);
        tmData.documentTranslation.remove(cont);
      } else {
        cont++;
      }
    }

    JOptionPane.showMessageDialog(this, getString("MSG.ERASED") + " "
            + cleanedLines + " " + getString("MSG.BLANK_ROWS"));

    if (cleanedLines > 0) {
      tmData.incrementChanges();

      SegmentChanges changes = new SegmentChanges(SegmentChanges.OperationKind.REMOVE,
          0, TmData.Side.TRANSLATION, "", 0);
      tmData.arrayListChanges.add(tmData.getIdentChanges(), changes);
      changes.setNumEliminada(numCleared, cleanedLines);
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
    tmData.tuSplit((tmView.getSelectedColumn()==1)?TmData.Side.ORIGINAL:TmData.Side.TRANSLATION);
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

  @Override
  public final void tmDataClear() {
    tmData.clear();
  }

  @Override
  public final void tmViewClear() {
    tmView.clear();
  }

  @Override
  public final void editSegmentClear() {
    editLeftSegment.setText("");
    editRightSegment.setText("");
  }

  @Override
  public final void setUndoEnabled(boolean enable) {
    toolBar.setUndoEnabled(enable);
    mainMenu.menuItemUndo.setEnabled(enable);
  }

  /**
   * Undo last change.
   *
   */
  @Override
  public void undoChanges() {
    SegmentChanges ultChanges;
    ultChanges = tmData.arrayListChanges.get(tmData.getIdentChanges());
    tmData.indexCurrent = ultChanges.getIdent_linea();
    SegmentChanges.OperationKind operationKind = ultChanges.getKind();
    tmData.setIdentAntAsLabel();
    switch (operationKind) {
      case JOIN:
        {
          tmData.undoJoin();
          break;
        }
      case DELETE:
        tmData.undoDelete();
        updateTmView();
        break;
      case SPLIT:
        {
          tmData.undoSplit();
          break;
        }
      case REMOVE:
        {
          tmData.undoRemove();
          break;
        }
      case TUSPLIT:
        {
          tmData.undoTuSplit(ultChanges.getSource());
          break;
        }
      default:
        break;
    }
    updateTmView();
  }
}

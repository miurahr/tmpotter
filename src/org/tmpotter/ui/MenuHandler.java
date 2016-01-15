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
import org.tmpotter.core.ProjectProperties;
import org.tmpotter.core.TextReader;
import org.tmpotter.core.TmxReader;
import org.tmpotter.core.TmxWriter;
import org.tmpotter.segmentation.Segmenter;
import org.tmpotter.ui.dialogs.About;
import org.tmpotter.ui.dialogs.Encodings;
import org.tmpotter.ui.dialogs.FontSelector;
import org.tmpotter.ui.dialogs.OpenTexts;
import org.tmpotter.ui.dialogs.OpenTmx;
import org.tmpotter.util.Preferences;
import org.tmpotter.util.RuntimePreferences;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;


/**
 * Action Handlers.
 *
 * @author Hiroshi Miura
 */
final class MenuHandler {

  private final MainWindow mainWindow;
  private ModelMediator modelMediator;
  private final TmData tmData;
  private File userHome = new File(System.getProperty("user.home"));
  
  public MenuHandler(final MainWindow mainWindow, TmData tmData) {
    this.mainWindow = mainWindow;
    this.tmData = tmData;
  }

  public void setModelMediator(ModelMediator mediator) {
    this.modelMediator = mediator;
  }

  // proxy for aqua
  public boolean displayAbout() {
    menuItemHelpAboutActionPerformed();
    return true;
  }
  
  public void menuItemHelpAboutActionPerformed() {
    new About(mainWindow).setVisible(true);
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  public void menuItemFileOpenActionPerformed() {
    final OpenTmx dlg = new OpenTmx(null, "", false);
    dlg.setPath(userHome);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (!dlg.isClosed()) {
      userHome = dlg.getPath();
      tmData.filePathOriginal = dlg.getFilePath();
      tmData.filePathTranslation = tmData.filePathOriginal;
      tmData.stringLangOriginal = dlg.getSourceLocale();
      tmData.stringLangTranslation = dlg.getTargetLocale();
      mainWindow.tmView.buildDisplay();
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
      initializeTmView(mainWindow);
      mainWindow.updateTmView();
      mainWindow.toolBar.enableButtons(true);
      mainWindow.mainMenu.enableEditMenus(true);
      mainWindow.toolBar.setUndoEnabled(false);
      mainWindow.mainMenu.menuItemFileSave.setEnabled(true);
      mainWindow.mainMenu.menuItemFileSaveAs.setEnabled(true);
      mainWindow.mainMenu.menuItemFileClose.setEnabled(true);
      dlg.dispose();
    }
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  public void menuItemFileTextOpenActionPerformed() {
    String originalEncoding;
    String translateEncoding;
    final OpenTexts dlg = new OpenTexts();
    dlg.setPath(userHome);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (!dlg.isClosed()) {
      userHome = dlg.getPath();
      originalEncoding = (String) dlg.getSourceLangEncComboBox().getSelectedItem();
      tmData.filePathOriginal = dlg.getSourcePath();
      tmData.stringOriginal = dlg.getSource();
      tmData.stringLangOriginal = dlg.getSourceLocale();
      mainWindow.tmView.buildDisplay();
      tmData.documentOriginal = new Document();
      tmData.documentTranslation = new Document();
      translateEncoding = (String) dlg.getTargetLangEncComboBox().getSelectedItem();
      tmData.filePathTranslation = dlg.getTargetPath();
      tmData.stringTranslation = dlg.getTarget();
      tmData.stringLangTranslation = dlg.getTargetLocale();
      Segmenter.srx = Preferences.getSrx();
      try {
        tmData.documentOriginal =
                TextReader.read(tmData.stringOriginal,
                        tmData.stringLangOriginal, originalEncoding);
        tmData.documentTranslation =
                TextReader.read(tmData.stringTranslation,
                        tmData.stringLangTranslation, translateEncoding);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(mainWindow, getString("MSG.ERROR"),
                getString("MSG.ERROR.FILE_READ"), JOptionPane.ERROR_MESSAGE);
        mainWindow.dispose();
      }
      tmData.matchArrays();
      initializeTmView(mainWindow);
      mainWindow.updateTmView();
      mainWindow.toolBar.enableButtons(true);
      mainWindow.mainMenu.enableEditMenus(true);
      mainWindow.toolBar.setUndoEnabled(false);
      mainWindow.mainMenu.menuItemFileSaveAs.setEnabled(true);
      mainWindow.mainMenu.menuItemFileClose.setEnabled(true);
      dlg.dispose();
    }
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
  protected void initializeTmView(MainWindow mainWindow) {
    TableColumn col;
    col = mainWindow.tmView.getColumnModel().getColumn(1);
    col.setHeaderValue(getString("TBL.HDR.COL.SOURCE")
            + tmData.filePathOriginal.getName());
    col = mainWindow.tmView.getColumnModel().getColumn(2);
    col.setHeaderValue(getString("TBL.HDR.COL.TARGET")
            + tmData.filePathTranslation.getName());
    mainWindow.tmView.setColumnHeaderView();
    mainWindow.updateTmView();
    tmData.topArrays = tmData.documentOriginal.size() - 1;
    tmData.indexCurrent = 0;
  }

  public void menuItemFileSaveAsActionPerformed() {
    saveBitext();
  }

  //  ToDo: implement proper functionality; not used currently
  public void menuItemFileSaveActionPerformed() {
    saveBitext();
  }

  /**
   * Necessary capabilities to store the bitext.
   *
   */
  private void saveBitext() {
    for (int cont = 0; cont < (tmData.documentOriginal.size() - 1); cont++) {
      if (tmData.documentOriginal.get(cont).equals("")
              && tmData.documentTranslation.get(cont).equals("")) {
        tmData.documentOriginal.remove(cont);
        tmData.documentTranslation.remove(cont);
      }
    }
    try {
      String outFileName = tmData.filePathOriginal.getName();
      String outFileNameBase = outFileName.substring(0,
              tmData.filePathOriginal.getName().length() - 4);
      boolean save = false;
      boolean cancel = false;
      userHome = new File(RuntimePreferences.getUserHome());
      File outFile = new File(outFileNameBase.concat(tmData
              .stringLangTranslation + ".tmx"));
      while (!save && !cancel) {
        final JFileChooser fc = new JFileChooser();
        boolean nameOfUser = false;
        while (!nameOfUser) {
          fc.setLocation(230, 300);
          fc.setCurrentDirectory(userHome);
          fc.setDialogTitle(getString("DLG.SAVEAS"));
          fc.setMultiSelectionEnabled(false);
          fc.setSelectedFile(outFile);
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          userHome = fc.getCurrentDirectory();
          int returnVal;
          returnVal = fc.showSaveDialog(mainWindow);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            returnVal = 1;
            outFile = fc.getSelectedFile();
            if (!outFile.getName().endsWith(".tmx")) {
              outFileName = outFile.getName().concat(".tmx");
              outFile = new File(outFileName);
            }
            nameOfUser = true;
          } else {
            nameOfUser = true;
            cancel = true;
          }
        }
        int selected;
        if (nameOfUser && !cancel) {
          if (outFile.exists()) {
            final Object[] options = {getString("BTN.SAVE"),
                    getString("BTN.CANCEL")};
            selected = JOptionPane.showOptionDialog(mainWindow,
                    getString("MSG.FILE_EXISTS"), getString("MSG.WARNING"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (selected == 0) {
              save = true;
            }
          } else {
            save = true;
          }
        }
      }
      String encoding = "UTF-8";
      if (save) {
        Encodings dlgEnc = new Encodings();
        dlgEnc.setVisible(true);
        if (!dlgEnc.isClosed()) {
          encoding = dlgEnc.getComboBoxEncoding();
          dlgEnc.dispose();
        }
      }
      TmxWriter.writeBitext(outFile, tmData.documentOriginal,
              tmData.stringLangOriginal, tmData.documentTranslation,
              tmData.stringLangTranslation, encoding);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(mainWindow,
              (String) tmData.arrayListLang.get(21),
              (String) tmData.arrayListLang.get(18),
              JOptionPane.ERROR_MESSAGE);
      mainWindow.dispose();
    }
  }

  /**
   * Actions to perform when closing alignment/editing session.
   *
   * <p>Leaves the text
   * as it was so that it can be processed later.
   */
  public void menuItemFileCloseActionPerformed() {
    clear();
    mainWindow.toolBar.enableButtons(false);
    mainWindow.mainMenu.enableEditMenus(false);
    mainWindow.mainMenu.menuItemFileSave.setEnabled(false);
    mainWindow.mainMenu.menuItemFileSaveAs.setEnabled(false);
    mainWindow.mainMenu.menuItemFileClose.setEnabled(false);
  }

  /**
   * clear empty segment.
   *
   * <p>Initialize values to start the validation of the following alignment
   */
  private void clear() {
    modelMediator.tmDataClear();
    modelMediator.setUndoEnabled(false);
    modelMediator.tmViewClear();
    modelMediator.editSegmentClear();
  }

  /**
   * Quit application.
   *
   * @return boolean - OS X Aqua integration only
   */
  public boolean quit() {
    menuItemFileQuitActionPerformed();
    return true;
  }
  
  public void menuItemFileQuitActionPerformed() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        mainWindow.dispose();
      }
    });
  }

  public void menuItemUndoActionPerformed() {
    modelMediator.undoChanges();
    modelMediator.onUndo();
  }

  public void menuItemRedoActionPerformed() {
    
  }

  public void menuItemOriginalDeleteActionPerformed() {
    modelMediator.onOriginalDelete();
  }

  public void menuItemOriginalJoinActionPerformed() {
    modelMediator.onOriginalJoin();
  }

  public void menuItemOriginalSplitActionPerformed() {
    modelMediator.onOriginalSplit();
  }

  public void menuItemTranslationDeleteActionPerformed() {
    modelMediator.onTranslationDelete();
  }

  public void menuItemTranslationJoinActionPerformed() {
    modelMediator.onTranslationJoin();
  }

  public void menuItemTranslationSplitActionPerformed() {
    modelMediator.onTranslationSplit();
  }

  public void menuItemRemoveBlankRowsActionPerformed() {
    modelMediator.onRemoveBlankRows();
  }

  public void menuItemTuSplitActionPerformed() {
    modelMediator.onTuSplit();
  }

  /**
   * Display fonts dialog.
   *
   * <p>Display the fonts dialog to allow selection of fonts for
   * origianl/translation tables/editors and main window
   */
  public void menuItemSettingsFontsActionPerformed() {
    FontSelector dlgFonts = new FontSelector(mainWindow, mainWindow.fontManager);
    dlgFonts.setVisible(true);
  }

  enum LnfType { GTK, LIQUID, METAL, NIMBUS, SYSTEM }

  public void menuItemLafGtkActionPerformed() {
    onChangeLnF(MenuHandler.LnfType.GTK);
  }

  public void menuItemLafLiquidActionPerformed() {
    onChangeLnF(MenuHandler.LnfType.LIQUID);
  }

  public void menuLafMetalActionPerformed() {
    onChangeLnF(MenuHandler.LnfType.METAL);
  }

  public void menuItemLafNimbusActionPerformed() {
    onChangeLnF(MenuHandler.LnfType.NIMBUS);
  }

  public void menuItemLafSystemActionPerformed() {
    onChangeLnF(MenuHandler.LnfType.SYSTEM);
  }

  private void onChangeLnF(LnfType type) {
    switch (type) {
      case GTK:
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
          SwingUtilities.updateComponentTreeUI(mainWindow);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        break;
      case LIQUID:
        try {
          UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
          com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(false);
          SwingUtilities.updateComponentTreeUI(mainWindow);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        break;
      case METAL:
        try {
          UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
          SwingUtilities.updateComponentTreeUI(mainWindow);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        //  Java 1.6 update 10+
        break;
      case NIMBUS:
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
          SwingUtilities.updateComponentTreeUI(mainWindow);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        break;
      case SYSTEM:
      default:
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          SwingUtilities.updateComponentTreeUI(mainWindow);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        break;
    }
  }
}

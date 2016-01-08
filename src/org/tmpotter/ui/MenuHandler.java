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
import org.tmpotter.core.SegmentChanges;
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

  public MenuHandler(final MainWindow mainWindow) {
    this.mainWindow = mainWindow;
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
   * Undo last change.
   *
   */
  void undoChanges() {
    String cad;
    SegmentChanges ultChanges = new SegmentChanges();
    int tam = 0;
    ultChanges = mainWindow.arrayListChanges.get(mainWindow.identChanges);
    mainWindow.identLabel = ultChanges.getIdent_linea();
    int operacion = ultChanges.getKind();
    int position;
    boolean izq = ultChanges.getSource();
    mainWindow.identAnt = mainWindow.identLabel;
    switch (operacion) {
      case 0: {
        final String cadaux = ultChanges.getFrase();
        if (izq) {
          cad = mainWindow.documentOriginal.get(mainWindow.identLabel);
          if (!cad.equals("")) {
            cad = cad.trim();
          }
          position = cad.indexOf(cadaux) + cadaux.length();
        } else {
          cad = mainWindow.documentTranslation.get(mainWindow.identLabel);
          if (!cad.equals("")) {
            cad = cad.trim();
          }
          position = cad.indexOf(cadaux) + cadaux.length();
        }
        if (ultChanges.getSource()) {
          mainWindow.documentOriginal.split(mainWindow.identLabel, position);
        } else {
          mainWindow.documentTranslation.split(mainWindow.identLabel, position);
        }
        mainWindow.updateTmView();
        break;
      }
      case 1:
        undoDelete();
        break;
      case 2: {
          // El complementario de Split es Unir
        // The complement of Split is Join
        int cont;
        cont = mainWindow.identLabel + 1;
        if (izq) {
          cad = ultChanges.getFrase();
          mainWindow.documentOriginal.set(mainWindow.identLabel, cad.trim());
          while (cont < mainWindow.topArrays) {
            mainWindow.documentOriginal.set(cont, mainWindow.documentOriginal.get(cont + 1));
            cont++;
          }
          mainWindow.documentOriginal.set(mainWindow.documentOriginal.size() - 1, "");
        } else {
          cad = ultChanges.getFrase();
          mainWindow.documentTranslation.set(mainWindow.identLabel, cad.trim());
          while (cont < mainWindow.topArrays) {
            mainWindow.documentTranslation.set(cont, mainWindow.documentTranslation.get(cont + 1));
            cont++;
          }
          mainWindow.documentTranslation.set(mainWindow.documentTranslation.size() - 1, "");
        }
        mainWindow.updateTmView();
        break;
      }
      case 3: {
        tam = ultChanges.getTam();
        int[] filasEliminadas;
        filasEliminadas = ultChanges.getNumEliminada();
        while (tam > 0) {
          mainWindow.documentTranslation.add(mainWindow.documentTranslation.size(), "");
          mainWindow.documentOriginal.add(mainWindow.documentOriginal.size(), "");
          mainWindow.topArrays = mainWindow.documentTranslation.size() - 1;
          tam--;
        }
        int cont2 = mainWindow.documentOriginal.size() - 1;
        tam = ultChanges.getTam();
        while (cont2 >= tam && tam > 0) {
          if (cont2 == filasEliminadas[tam - 1]) {
            mainWindow.documentTranslation.set(cont2, "");
            mainWindow.documentOriginal.set(cont2, "");
            tam--;
          } else {
            mainWindow.documentTranslation.set(cont2,
                    mainWindow.documentTranslation.get(cont2 - tam));
            mainWindow.documentOriginal.set(cont2,
                    mainWindow.documentOriginal.get(cont2 - tam));
          }
          cont2--;
        }
        mainWindow.updateTmView();
        break;
      }
      case 4: {
        if (izq) {
          mainWindow.documentTranslation.set(mainWindow.identLabel,
                  mainWindow.documentTranslation.get(mainWindow.identLabel + 1));
          mainWindow.documentOriginal.remove(mainWindow.identLabel + 1);
          mainWindow.documentTranslation.remove(mainWindow.identLabel + 1);
        } else {
          mainWindow.documentOriginal.set(mainWindow.identLabel,
                  mainWindow.documentOriginal.get(mainWindow.identLabel + 1));
          mainWindow.documentOriginal.remove(mainWindow.identLabel + 1);
          mainWindow.documentTranslation.remove(mainWindow.identLabel + 1);
        }
        mainWindow.updateTmView();
        break;
      }
      default:
        break;
    }
  }

  /**
   * Undoes the last delete.
   */
  private void undoDelete() {
    SegmentChanges ultChanges =
            mainWindow.arrayListChanges.get(mainWindow.identChanges);
    mainWindow.identLabel = ultChanges.getIdent_linea();
    boolean izq = ultChanges.getSource();
    
    final Document documentOriginal = mainWindow.documentOriginal;
    final Document documentTranslation = mainWindow.documentTranslation;
    final int identLabel = mainWindow.identLabel;
    
    if (izq) {
      if (mainWindow.identLabel == documentOriginal.size()) {
        documentOriginal.add(mainWindow.identLabel,
                ultChanges.getFrase());
        if (documentOriginal.size() != documentTranslation.size()) {
          documentTranslation.add(documentTranslation.size(), "");
        }
      } else {
        documentOriginal.add(documentOriginal.size(),
                documentOriginal.get(documentOriginal.size() - 1));
        for (int cont = documentOriginal.size() - 1; cont > identLabel; cont--) {
          documentOriginal.set(cont, documentOriginal.get(cont - 1));
        }
        documentOriginal.set(identLabel, ultChanges.getFrase());
      }
    } else {
      if (identLabel == documentTranslation.size()) {
        documentTranslation.add(identLabel, ultChanges.getFrase());
        if (documentOriginal.size() != documentTranslation.size()) {
          documentOriginal.add(documentOriginal.size(), "");
        }
      } else {
        int cont;
        documentTranslation.add(documentTranslation.size(),
                documentTranslation.get(documentTranslation.size() - 1));
        for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
          documentTranslation.set(cont, documentTranslation.get(cont - 1));
        }
        documentTranslation.set(identLabel, ultChanges.getFrase());
      }
    }
    mainWindow.updateTmView();
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  public void menuItemFileOpenActionPerformed() {
    final OpenTmx dlg = new OpenTmx(null, "", false);
    dlg.setPath(mainWindow.userHome);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (!dlg.isClosed()) {
      mainWindow.userHome = dlg.getPath();
      mainWindow.filePathOriginal = dlg.getFilePath();
      mainWindow.filePathTranslation = mainWindow.filePathOriginal;
      mainWindow.stringLangOriginal = dlg.getSourceLocale();
      mainWindow.stringLangTranslation = dlg.getTargetLocale();
      mainWindow.tmView.buildDisplay();
      try {
        ProjectProperties prop = new ProjectProperties();
        prop.setSourceLanguage(mainWindow.stringLangOriginal);
        prop.setTargetLanguage(mainWindow.stringLangTranslation);
        TmxReader reader = new TmxReader(prop, mainWindow.filePathOriginal);
        mainWindow.documentOriginal =
                reader.getOriginalDocument(mainWindow.documentOriginal);
        mainWindow.documentTranslation = 
                reader.getTranslationDocument(mainWindow.documentTranslation);
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
    dlg.setPath(mainWindow.userHome);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (!dlg.isClosed()) {
      mainWindow.userHome = dlg.getPath();
      originalEncoding = (String) dlg.getSourceLangEncComboBox().getSelectedItem();
      mainWindow.filePathOriginal = dlg.getSourcePath();
      mainWindow.stringOriginal = dlg.getSource();
      mainWindow.stringLangOriginal = dlg.getSourceLocale();
      mainWindow.tmView.buildDisplay();
      mainWindow.documentOriginal = new Document();
      mainWindow.documentTranslation = new Document();
      translateEncoding = (String) dlg.getTargetLangEncComboBox().getSelectedItem();
      mainWindow.filePathTranslation = dlg.getTargetPath();
      mainWindow.stringTranslation = dlg.getTarget();
      mainWindow.stringLangTranslation = dlg.getTargetLocale();
      Segmenter.srx = Preferences.getSrx();
      try {
        mainWindow.documentOriginal =
                TextReader.read(mainWindow.stringOriginal,
                        mainWindow.stringLangOriginal, originalEncoding);
        mainWindow.documentTranslation =
                TextReader.read(mainWindow.stringTranslation,
                        mainWindow.stringLangTranslation, translateEncoding);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(mainWindow, getString("MSG.ERROR"),
                getString("MSG.ERROR.FILE_READ"), JOptionPane.ERROR_MESSAGE);
        mainWindow.dispose();
      }
      mainWindow.matchArrays();
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
            + mainWindow.filePathOriginal.getName());
    col = mainWindow.tmView.getColumnModel().getColumn(2);
    col.setHeaderValue(getString("TBL.HDR.COL.TARGET")
            + mainWindow.filePathTranslation.getName());
    mainWindow.tmView.setColumnHeaderView();
    mainWindow.updateTmView();
    mainWindow.topArrays = mainWindow.documentOriginal.size() - 1;
    mainWindow.identLabel = 0;
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
    for (int cont = 0; cont < (mainWindow.documentOriginal.size() - 1); cont++) {
      if (mainWindow.documentOriginal.get(cont).equals("")
              && mainWindow.documentTranslation.get(cont).equals("")) {
        mainWindow.documentOriginal.remove(cont);
        mainWindow.documentTranslation.remove(cont);
      }
    }
    try {
      String outFileName = mainWindow.filePathOriginal.getName();
      String outFileNameBase = outFileName.substring(0,
              mainWindow.filePathOriginal.getName().length() - 4);
      boolean save = false;
      boolean cancel = false;
      mainWindow.userHome = new File(RuntimePreferences.getUserHome());
      File outFile = new File(outFileNameBase.concat(mainWindow
              .stringLangTranslation + ".tmx"));
      while (!save && !cancel) {
        final JFileChooser fc = new JFileChooser();
        boolean nameOfUser = false;
        while (!nameOfUser) {
          fc.setLocation(230, 300);
          fc.setCurrentDirectory(mainWindow.userHome);
          fc.setDialogTitle(getString("DLG.SAVEAS"));
          fc.setMultiSelectionEnabled(false);
          fc.setSelectedFile(outFile);
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          mainWindow.userHome = fc.getCurrentDirectory();
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
      TmxWriter.writeBitext(outFile, mainWindow.documentOriginal,
              mainWindow.stringLangOriginal, mainWindow.documentTranslation,
              mainWindow.stringLangTranslation, encoding);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(mainWindow,
              (String) mainWindow.arrayListLang.get(21),
              (String) mainWindow.arrayListLang.get(18),
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
    mainWindow.toolBar.setUndoEnabled(false);
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
    mainWindow.documentOriginal.clean();
    mainWindow.documentTranslation.clean();
    int cont = mainWindow.arrayListBitext.size() - 1;
    while (!mainWindow.arrayListBitext.isEmpty()) {
      mainWindow.arrayListBitext.remove(cont--);
    }
    cont = mainWindow.arrayListChanges.size() - 1;
    while (!mainWindow.arrayListChanges.isEmpty()) {
      mainWindow.arrayListChanges.remove(cont--);
    }
    mainWindow.identChanges = -1;
    mainWindow.identLabel = 0;
    mainWindow.identAnt = 0;
    mainWindow.filePathTranslation = null;
    mainWindow.filePathOriginal = null;
    mainWindow.toolBar.setUndoEnabled(false);
    mainWindow.tmView.clear();
    mainWindow.topArrays = 0;
    mainWindow.editLeftSegment.setText("");
    mainWindow.editRightSegment.setText("");
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
    mainWindow.onUndoCv();
  }

  public void menuItemRedoActionPerformed() {
  }

  public void menuItemOriginalDeleteActionPerformed() {
    mainWindow.onOriginalDelete();
  }

  public void menuItemOriginalJoinActionPerformed() {
    mainWindow.onOriginalJoin();
  }

  public void menuItemOriginalSplitActionPerformed() {
    mainWindow.onOriginalSplit();
  }

  public void menuItemTranslationDeleteActionPerformed() {
    mainWindow.onTranslationDelete();
  }

  public void menuItemTranslationJoinActionPerformed() {
    mainWindow.onTranslationJoin();
  }

  public void menuItemTranslationSplitActionPerformed() {
    mainWindow.onTranslationSplitCv();
  }

  /**
   * Display fonts dialog.
   *
   * <p>Display the fonts dialog to allow selection of fonts for
   * origianl/translation tables/editors and main window
   */
  public void menuItemSettingsFontsActionPerformed() {
    FontSelector dlgFonts = new FontSelector(mainWindow, mainWindow.windowFonts.getFonts());
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

  private final void onChangeLnF(LnfType type) {
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

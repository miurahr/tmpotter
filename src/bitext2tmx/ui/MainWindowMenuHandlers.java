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

import static bitext2tmx.util.Localization.getString;

import bitext2tmx.core.Document;
import bitext2tmx.core.DocumentSegmenter;
import bitext2tmx.core.ProjectProperties;
import bitext2tmx.core.TmxReader;
import bitext2tmx.core.TranslationAligner;
import bitext2tmx.engine.SegmentChanges;
import bitext2tmx.ui.dialogs.About;
import bitext2tmx.ui.dialogs.OpenTexts;
import bitext2tmx.ui.dialogs.OpenTmx;
import bitext2tmx.ui.help.Manual;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

/**
 * Action Handlers.
 *
 * @author Hiroshi Miura
 */
final class MainWindowMenuHandlers {

  private final MainWindow mainWindow;

  public MainWindowMenuHandlers(final MainWindow mainWindow) {
    this.mainWindow = mainWindow;
  }

  final void helpAboutMenuItemActionPerformed() {
    new About(mainWindow).setVisible(true);
  }

  void helpManualMenuItemActionPerformed() {
    final Manual dlg = new Manual();
    dlg.setVisible(true);
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
        mainWindow.updateAlignmentsView();
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
        mainWindow.updateAlignmentsView();
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
        mainWindow.updateAlignmentsView();
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
        mainWindow.updateAlignmentsView();
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
        int cont = documentTranslation.size() - 1;
        documentTranslation.add(documentTranslation.size(),
                documentTranslation.get(documentTranslation.size() - 1));
        for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
          documentTranslation.set(cont, documentTranslation.get(cont - 1));
        }
        documentTranslation.set(identLabel, ultChanges.getFrase());
      }
    }
    mainWindow.updateAlignmentsView();
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  void onOpenTmx() {
    final OpenTmx dlg = new OpenTmx(null, "", false);
    dlg.setPath(mainWindow.userHome);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (!dlg.isClosed()) {
      mainWindow.userHome = dlg.getPath();
      String originalEncoding = (String) dlg.getLangEncComboBox().getSelectedItem();
      mainWindow.filePathOriginal = dlg.getFilePath();
      mainWindow.filePathTranslation = mainWindow.filePathOriginal;
      mainWindow.stringLangOriginal = dlg.getSourceLocale();
      mainWindow.stringLangTranslation = dlg.getTargetLocale();
      mainWindow.viewAlignments.buildDisplay();
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
      initializeAlignmentsView(mainWindow);
      mainWindow.updateAlignmentsView();
      mainWindow.viewControls.enableButtons(true);
      mainWindow.viewControls.setUndoEnabled(false);
      mainWindow.mainWindowMenu.menuItemFileSaveAs.setEnabled(true);
      mainWindow.mainWindowMenu.menuItemFileClose.setEnabled(true);
      dlg.dispose();
    }
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  void onOpenText() {
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
      mainWindow.viewAlignments.buildDisplay();
      mainWindow.documentOriginal = new Document();
      mainWindow.documentTranslation = new Document();
      translateEncoding = (String) dlg.getTargetLangEncComboBox().getSelectedItem();
      mainWindow.filePathTranslation = dlg.getTargetPath();
      mainWindow.stringTranslation = dlg.getTarget();
      mainWindow.stringLangTranslation = dlg.getTargetLocale();
      try {
        mainWindow.documentOriginal =
                DocumentSegmenter.readDocument(mainWindow.stringOriginal,
                        mainWindow.stringLangOriginal, originalEncoding);
        mainWindow.documentTranslation =
                DocumentSegmenter.readDocument(mainWindow.stringTranslation,
                        mainWindow.stringLangTranslation, translateEncoding);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(mainWindow, getString("MSG.ERROR"),
                getString("MSG.ERROR.FILE.READ"), JOptionPane.ERROR_MESSAGE);
        mainWindow.dispose();
      }
      boolean res = TranslationAligner.align(mainWindow.documentOriginal,
              mainWindow.documentTranslation);
      if (res) {
        matchArrays(mainWindow);
        for (int cont = 0; cont < mainWindow.documentOriginal.size(); cont++) {
          if (mainWindow.documentOriginal.get(cont) == null
                  || (mainWindow.documentOriginal.get(cont).equals(""))
                  && (mainWindow.documentTranslation.get(cont) == null
                  || mainWindow.documentTranslation.get(cont).equals(""))) {
            mainWindow.documentOriginal.remove(cont);
            mainWindow.documentTranslation.remove(cont);
          }
        }
      }
      initializeAlignmentsView(mainWindow);
      mainWindow.updateAlignmentsView();
      mainWindow.viewControls.enableButtons(true);
      mainWindow.viewControls.setUndoEnabled(false);
      mainWindow.mainWindowMenu.menuItemFileSaveAs.setEnabled(true);
      mainWindow.mainWindowMenu.menuItemFileClose.setEnabled(true);
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
  protected void initializeAlignmentsView(MainWindow mainWindow) {
    TableColumn col;
    col = mainWindow.viewAlignments.getColumnModel().getColumn(1);
    col.setHeaderValue(getString("TBL.HDR.COL.SOURCE")
            + mainWindow.filePathOriginal.getName());
    col = mainWindow.viewAlignments.getColumnModel().getColumn(2);
    col.setHeaderValue(getString("TBL.HDR.COL.TARGET")
            + mainWindow.filePathTranslation.getName());
    mainWindow.viewAlignments.setColumnHeaderView();
    mainWindow.updateAlignmentsView();
    mainWindow.topArrays = mainWindow.documentOriginal.size() - 1;
    mainWindow.identLabel = 0;
  }

  /**
   * Function IgualarArrays: adds rows to the smallest array and deletes blank
   * rows.
   */
  void matchArrays(MainWindow mainWindow) {
    boolean limpiar = true;
    Document documentOriginal = mainWindow.documentOriginal;
    Document documentTranslation = mainWindow.documentTranslation;
    
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
          || documentTranslation.get(documentTranslation.size() - 1).equals(""))) {
        documentOriginal.remove(documentOriginal.size() - 1);
        documentTranslation.remove(documentTranslation.size() - 1);
      } else {
        limpiar = false;
      }
    }
    mainWindow.topArrays = documentOriginal.size() - 1;
    if (mainWindow.identLabel > (documentOriginal.size() - 1)) {
      mainWindow.identLabel = documentOriginal.size() - 1;
    }
  }

}

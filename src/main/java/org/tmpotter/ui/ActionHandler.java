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

import org.tmpotter.core.TmxWriter;
import org.tmpotter.segmentation.Segmenter;
import org.tmpotter.ui.dialogs.About;
import org.tmpotter.ui.dialogs.Encodings;
import org.tmpotter.ui.wizard.ImportWizard;
import org.tmpotter.ui.dialogs.OpenTmx;
import org.tmpotter.util.Preferences;
import org.tmpotter.util.RuntimePreferences;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import org.tmpotter.ui.wizard.ImportPreference;


/**
 * Action Handlers.
 *
 * @author Hiroshi Miura
 */
final class ActionHandler {

  private final MainWindow mainWindow;
  private ModelMediator modelMediator;
  private final TmData tmData;

  public ActionHandler(final MainWindow mainWindow, TmData tmData) {
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
    new About(mainWindow, true).setVisible(true);
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  public void menuItemFileOpenActionPerformed() {
    final OpenTmx dlg = new OpenTmx(mainWindow, true);
    dlg.setFilePath(RuntimePreferences.getUserHome());
    dlg.setModal(true);
    dlg.setVisible(true);
    if (!dlg.isClosed()) {
      RuntimePreferences.setUserHome(dlg.getFilePath());
      modelMediator.onOpenFile(dlg.getFilePath(), dlg.getSourceLocale(), dlg.getTargetLocale());
      dlg.dispose();
    }
  }

  /**
   * Open dialog to select the files we want to align/convert.
   *
   */
  public void menuItemFileImportActionPerformed() {
    final ImportWizard dlg = new ImportWizard(mainWindow, true);
    //dlg.setPath(RuntimePreferences.getUserHome());
    dlg.setModal(true);
    dlg.setVisible(true);
    dlg.dispose();
    if (dlg.isFinished()) {
	    doImport(dlg.getPref());
    }
  }

  private void doImport(ImportPreference pref) {
	  String filter = pref.getFilter(); // Now support "BiTextFilter" and "PoFilter"
	  RuntimePreferences.setUserHome(pref.getOriginalFilePath());
	  modelMediator.setOriginalProperties(pref.getOriginalFilePath(), pref.getOriginalLang(), pref.getEncoding());
	  modelMediator.setTargetProperties(pref.getTranslationFilePath(), pref.getTranslationLang(),pref.getEncoding());
	  mainWindow.tmView.buildDisplay();
	  Segmenter.setSrx(Preferences.getSrx());
	  try {
		  modelMediator.onImportFile(filter);
		  initializeTmView(mainWindow);
		  mainWindow.updateTmView();
		  mainWindow.toolBar.enableButtons(true);
		  mainWindow.enableEditMenus(true);
		  mainWindow.toolBar.setUndoEnabled(false);
		  mainWindow.enableMenuItemFileSaveAs(true);
		  mainWindow.enableMenuItemFileClose(true);
	  } catch (Exception ex) {
		  JOptionPane.showMessageDialog(mainWindow, getString("MSG.ERROR"),
		      getString("MSG.ERROR.FILE_READ"), JOptionPane.ERROR_MESSAGE);
	  }
  }

  /**
   * Initialize alignment view.
   *
   * <p>
   * Extracts from the TMX those lines having information which is useful for alignment, and puts
   * them in the corresponding ArrayList's The left part in _alstOriginal corresponds to source text
   * lines and the right part in _alstTranslation corresponds to the target text lines. Initialize
   * the table with one line for each left and right line
   *
   */
  protected void initializeTmView(MainWindow mainWindow) {
    TableColumn col;
    col = mainWindow.tmView.getColumnModel().getColumn(1);
    col.setHeaderValue(getString("TBL.HDR.COL.SOURCE")
        + mainWindow.prop.getFilePathOriginal().getName());
    col = mainWindow.tmView.getColumnModel().getColumn(2);
    col.setHeaderValue(getString("TBL.HDR.COL.TARGET")
        + mainWindow.prop.getFilePathTranslation().getName());
    mainWindow.tmView.setColumnHeaderView();
    mainWindow.updateTmView();
    tmData.topArrays = tmData.documentOriginal.size() - 1;
    tmData.indexCurrent = 0;
  }

  public void menuItemFileSaveAsActionPerformed() {
    saveProject();
  }

  //  ToDo: implement proper functionality; not used currently
  public void menuItemFileSaveActionPerformed() {
    saveProject();
  }

  /**
   * Necessary capabilities to store the bitext.
   *
   */
  private void saveProject() {
    for (int cont = 0; cont < (tmData.documentOriginal.size() - 1); cont++) {
      if (tmData.documentOriginal.get(cont).equals("")
          && tmData.documentTranslation.get(cont).equals("")) {
        tmData.documentOriginal.remove(cont);
        tmData.documentTranslation.remove(cont);
      }
    }
    try {
      String outFileName = mainWindow.prop.getFilePathOriginal().getName();
      String outFileNameBase = outFileName.substring(0,
          mainWindow.prop.getFilePathOriginal().getName().length() - 4);
      boolean save = false;
      boolean cancel = false;
      File outFile = new File(outFileNameBase.concat(tmData.stringLangTranslation + ".tmx"));
      while (!save && !cancel) {
        final JFileChooser fc = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter(
        	"TMX File", "tmx");
    	fc.setFileFilter(filter);
        boolean nameOfUser = false;
        while (!nameOfUser) {
          fc.setLocation(230, 300);
          fc.setCurrentDirectory(RuntimePreferences.getUserHome());
          fc.setDialogTitle(getString("DLG.SAVEAS"));
          fc.setMultiSelectionEnabled(false);
          fc.setSelectedFile(outFile);
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          RuntimePreferences.setUserHome(fc.getCurrentDirectory());
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
        Encodings dlgEnc = new Encodings(mainWindow, true);
        dlgEnc.setVisible(true);
        if (!dlgEnc.isClosed()) {
          encoding = dlgEnc.getComboBoxEncoding();
          dlgEnc.dispose();
        }
      }
      TmxWriter.writeTmxFileBody(outFile, tmData.documentOriginal,
          tmData.stringLangOriginal, tmData.documentTranslation,
          tmData.stringLangTranslation, encoding);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(mainWindow,
          tmData.stringLangOriginal, tmData.stringLangTranslation,
          JOptionPane.ERROR_MESSAGE);
      mainWindow.dispose();
    }
  }

  /**
   * Actions to perform when closing alignment/editing session.
   *
   * <p>
   * Leaves the text as it was so that it can be processed later.
   */
  public void menuItemFileCloseActionPerformed() {
    clear();
    mainWindow.toolBar.enableButtons(false);
    mainWindow.enableEditMenus(false);
    mainWindow.enableMenuItemFileSave(false);
    mainWindow.enableMenuItemFileSaveAs(false);
    mainWindow.enableMenuItemFileClose(false);
  }

  /**
   * clear empty segment.
   *
   * <p>
   * Initialize values to start the validation of the following alignment
   */
  private void clear() {
    modelMediator.tmDataClear();
    mainWindow.prop.clear();
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

  public void menuItemSettingsActionPerformed() {
    // FIXME: Implement me.
  }
}

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
import static bitext2tmx.util.StringUtil.formatText;
import static bitext2tmx.util.StringUtil.restoreText;

import bitext2tmx.core.Document;
import bitext2tmx.core.DocumentSegmenter;
import bitext2tmx.core.ProjectProperties;
import bitext2tmx.core.TmxReader;
import bitext2tmx.core.TmxWriter;
import bitext2tmx.core.TranslationAligner;
import bitext2tmx.engine.Segment;
import bitext2tmx.engine.SegmentChanges;
import bitext2tmx.ui.dialogs.Encodings;
import bitext2tmx.ui.dialogs.FontSelector;
import bitext2tmx.ui.dialogs.OpenTexts;
import bitext2tmx.ui.dialogs.OpenTmx;
import bitext2tmx.util.AppConstants;
import bitext2tmx.util.Platform;
import bitext2tmx.util.RuntimePreferences;
import bitext2tmx.util.Utilities;
import bitext2tmx.util.gui.AquaAdapter;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.ui.DockingUISettings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;


/**
 * Main window class,.
 * 
 */
@SuppressWarnings("serial")
public final class MainWindow extends JFrame implements ActionListener,
        WindowListener {
  protected final AlignmentsView viewAlignments = new AlignmentsView(this);
  protected final SegmentEditor editLeftSegment = new SegmentEditor(this);
  protected final SegmentEditor editRightSegment = new SegmentEditor(this);
  protected final ControlView viewControls = new ControlView(this);

  protected MainWindowMenuHandlers handler;
  protected MainWindowMenus mainWindowMenu;
  protected MainWindowFonts mainWindowFonts;

  protected DockingDesktop desktop;

  //  Menubar
  protected final JMenuBar menuBar = new JMenuBar();

  //  Statusbar
  protected JPanel panelStatusBar;
  protected JLabel labelStatusBar;

  protected Document documentOriginal;
  protected Document documentTranslation;

  private final ArrayList arrayListBitext;

  protected final ArrayList<SegmentChanges> arrayListChanges;
  private final ArrayList arrayListLang;

  protected int topArrays;    //  =  0;
  private int positionTextArea;  //  =  0;
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


  /**
   * Main window class.
   * 
   */
  public MainWindow() {
    this.arrayListBitext = new ArrayList();
    this.arrayListChanges = new ArrayList<>();
    this.arrayListLang = new ArrayList();

    handler = new MainWindowMenuHandlers(this);
    mainWindowMenu = new MainWindowMenus(this, handler);
    mainWindowFonts = new MainWindowFonts(this, mainWindowMenu);

    MainWindowUI.initDockingUi(this);
    makeMenus();
    MainWindowUI.makeUi(this);
    setWindowIcon();

    //  Proxy callbacks from/to Mac OS X Aqua global menubar for Quit and About
    try {
      AquaAdapter.connect(this, "displayAbout", AquaAdapter.AquaEvent.ABOUT);
      AquaAdapter.connect(this, "quit", AquaAdapter.AquaEvent.QUIT);
    } catch (final NoClassDefFoundError e) {
      System.out.println(e);
    }

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public final void windowClosing(final WindowEvent event) {
        quit();
      }
    });

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

    setFonts(null);

  }

  private static final Logger LOG = Logger.getLogger(MainWindow.class
          .getName());


  protected ImageIcon getDesktopIcon(final String iconName) {
    if (Platform.isMacOsx()) {
      return (mainWindowMenu.getIcon("desktop/osx/" + iconName));
    }

    return (mainWindowMenu.getIcon("desktop/" + iconName));
  }

  /**
   * Set root window icon.
   */
  private void setWindowIcon() {
    try {
      //setIconImage( Icons.getIcon( "icon-small.png" ).getImage() ); 
    } catch (final Exception e) {
      System.out.println("Error loading icon: " + e);
    }
  }


  private void makeMenus() {
    menuBar.add(mainWindowMenu.getMenuFile());
    menuBar.add(mainWindowMenu.getMenuSettings());
    menuBar.add(mainWindowMenu.getMenuHelp());
    setJMenuBar(menuBar);
  }

  /**
   * Quit application.
   *
   * @return boolean - OS X Aqua integration only
   */
  private boolean quit() {
    //  ToDo: check for unsaved changes first, save, then quit

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        dispose();
      }
    });

    return (true);
  }

  /**
   * Actions to perform when closing alignment/editing session.
   * 
   * <p>Leaves the text
   * as it was so that it can be processed later.
   */
  private void onClose() {
    clear();
    //BotonesFalse();
    viewControls.enableButtons(false);
    //  Done in _vwControls
    //_btnUndo.setEnabled( false );
    // Undo is separate
    viewControls.setUndoEnabled(false);
    mainWindowMenu.menuItemFileSave.setEnabled(false);
    mainWindowMenu.menuItemFileSaveAs.setEnabled(false);
    mainWindowMenu.menuItemFileClose.setEnabled(false);
  }

  //  ToDo: implement proper functionality; not used currently
  final void onSave() {
    saveBitext();
  }

  final void onSaveAs() {
    saveBitext();
  }

  /**
   * Necessary capabilities to store the bitext.
   *
   */
  private void saveBitext() {
    for (int cont = 0; cont < (documentOriginal.size() - 1); cont++) {
      if (documentOriginal.get(cont).equals("")
              && documentTranslation.get(cont).equals("")) {
        documentOriginal.remove(cont);
        documentTranslation.remove(cont);
      }
    }

    try {
      String outFileName = filePathOriginal.getName();
      String outFileNameBase = outFileName
              .substring(0, (filePathOriginal.getName().length() - 4));
      boolean save = false;
      boolean cancel = false;

      userHome = new File(RuntimePreferences.getUserHome());
      File outFile = new File(outFileNameBase.concat(stringLangTranslation
              + ".tmx"));

      while (!save && !cancel) {
        final JFileChooser fc = new JFileChooser();

        //  switch() on language removed from here -RM
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
          returnVal = fc.showSaveDialog(this);

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

            selected = JOptionPane.showOptionDialog(this,
                    getString("MSG.FILE.EXISTS"), getString("MSG.WARNING"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            //  Overwrite
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
        //  Ask for the encoding
        Encodings dlgEnc = new Encodings();

        dlgEnc.setVisible(true);

        if (!dlgEnc.isClosed()) {
          encoding = dlgEnc.getComboBoxEncoding();
          dlgEnc.dispose();
        }
      }

      TmxWriter.writeBitext(outFile,
              documentOriginal, stringLangOriginal,
              documentTranslation, stringLangTranslation,
              encoding);

    } catch (IOException ex) {
      JOptionPane
              .showMessageDialog(this, (String) arrayListLang.get(21),
                      (String) arrayListLang.get(18), JOptionPane.ERROR_MESSAGE);
      this.dispose();
    }
  }

  /**
   * clear empty segment.
   *
   * <p>Initialize values to start the validation of the following alignment
   */
  private void clear() {
    documentOriginal.clean();
    documentTranslation.clean();

    int cont = arrayListBitext.size() - 1;

    while (!arrayListBitext.isEmpty()) {
      arrayListBitext.remove(cont--);
    }

    cont = arrayListChanges.size() - 1;

    while (!arrayListChanges.isEmpty()) {
      arrayListChanges.remove(cont--);
    }

    identChanges = -1;
    identLabel = 0;
    identAnt = 0;
    filePathTranslation = null;
    filePathOriginal = null;
    viewControls.setUndoEnabled(false);
    viewAlignments.clear();
    topArrays = 0;

    editLeftSegment.setText("");
    editRightSegment.setText("");
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
      updateAlignmentsView();
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
    updateAlignmentsView();
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
    updateAlignmentsView();
  }

  /**
   * Update the row in table with mods.
   * 
   * <p>This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  protected void updateAlignmentsView() {
    if (!documentOriginal.isEmpty() && !documentTranslation.isEmpty()) {
      handler.matchArrays(this);
    }

    for (int cont = 0; cont < viewAlignments.getRowCount(); cont++) {
      viewAlignments.setModelValueAt("", cont, 0);
      viewAlignments.setModelValueAt("", cont, 1);
      viewAlignments.setModelValueAt("", cont, 2);
    }

    if ((viewAlignments.getRowCount() > documentOriginal.size())
            && (documentOriginal.size() > 25)) {
      while (viewAlignments.getRowCount() != documentOriginal.size()) {
        viewAlignments.removeSegment(viewAlignments.getRowCount() - 1);
        viewAlignments.setPreferredSize(805, 15, -1);
      }
    } else if (viewAlignments.getRowCount() < documentOriginal.size()) {
      while (viewAlignments.getRowCount() != documentOriginal.size()) {
        Segment nullSegment = new Segment(null, null, null);
        viewAlignments.addModelSegment(nullSegment);
        viewAlignments.setPreferredSize(805, 15, 1);
      }
    }

    for (int cont = 0; cont < documentOriginal.size(); cont++) {
      viewAlignments.setModelValueAt(Integer.toString(cont + 1), cont, 0);
      viewAlignments.setModelValueAt(documentOriginal.get(cont), cont, 1);
    }

    for (int cont = 0; cont < documentTranslation.size(); cont++) {
      viewAlignments.setModelValueAt(documentTranslation.get(cont), cont, 2);
    }

    if (identLabel == topArrays) {
      viewAlignments.setRowSelectionInterval(topArrays, topArrays);
    }

    viewAlignments.repaint();

    editLeftSegment.setText(formatText(viewAlignments.getValueAt(
            identLabel, 1).toString()));
    editRightSegment.setText(formatText(viewAlignments.getValueAt(
            identLabel, 2).toString()));
  }

  //  Accessed by ControlView
  final void onOriginalJoin() {
    identChanges++;
    join(true);
  }

  //  Accessed by ControlView
  final void onOriginalDelete() {
    identChanges++;
    delete(true);
  }

  //  Accessed by ControlView
  final void onOriginalSplit() {
    identChanges++;
    split(true);
  }

  //  Accessed by ControlView
  final void onTranslationJoin() {
    identChanges++;
    join(false);
  }

  //  Accessed by ControlView
  final void onTranslationDelete() {
    identChanges++;
    delete(false);
  }

  //  Accessed by ControlView
  final void onTranslationSplit() {
    identChanges++;
    split(false);
  }

  //  Accessed by ControlView
  final void onUndo() {
    handler.undoChanges();
    arrayListChanges.remove(identChanges);
    identChanges--;

    if (identChanges == -1) {
      viewControls.setUndoEnabled(false);
    }
  }

  //  Accessed by AlignmentsView
  final void onTableClicked() {
    // if (e.getClickCount() == 1 || e.getClickCount() == 2) {
    positionTextArea = 0;

    if (identAnt < documentOriginal.size()) {
      //  ToDo: replace with docking editors call
      documentOriginal.set(identAnt, restoreText(editLeftSegment.getText()));
      documentTranslation.set(identAnt, restoreText(editRightSegment.getText()));
    }

    editLeftSegment.setText(formatText(viewAlignments
            .getValueAt(viewAlignments.getSelectedRow(), 1).toString()));
    editRightSegment.setText(formatText(viewAlignments
            .getValueAt(viewAlignments.getSelectedRow(), 2).toString()));

    identLabel = viewAlignments.getSelectedRow();
    identAnt = identLabel;

    if (identLabel == topArrays) {
      viewControls.setTranslationJoinEnabled(false);
      viewControls.setOriginalJoinEnabled(false);
    } else {
      viewControls.setTranslationJoinEnabled(true);
      viewControls.setOriginalJoinEnabled(true);
    }
    // }

    updateAlignmentsView();
  }

  //  Accessed by AlignmentsView
  final void onTablePressed(final KeyEvent event) {
    int fila;

    if (viewAlignments.getSelectedRow() != -1) {
      fila = viewAlignments.getSelectedRow();
      positionTextArea = 0;
    } else {
      fila = 1;
    }

    if (fila < viewAlignments.getRowCount() - 1) {
      if ((event.getKeyCode() == KeyEvent.VK_DOWN)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
        if (identAnt < documentOriginal.size()) {
          documentOriginal.set(identAnt,
                  restoreText(editLeftSegment.getText()));
          documentTranslation.set(identAnt,
                  restoreText(editRightSegment.getText()));
        }

        editLeftSegment.setText(formatText(viewAlignments
                .getValueAt(fila + 1, 1).toString()));
        editRightSegment.setText(formatText(viewAlignments
                .getValueAt(fila + 1, 2).toString()));

        identLabel = fila + 1;
      } else if ((event.getKeyCode() == KeyEvent.VK_UP)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
        identLabel = fila - 1;

        if (fila == 0) {
          fila = 1;
          identLabel = 0;
        }

        if (identAnt < documentOriginal.size()) {
          documentOriginal.set(identAnt, restoreText(editLeftSegment.getText()));
          documentTranslation.set(identAnt, restoreText(editRightSegment.getText()));
        }

        editLeftSegment.setText(formatText(viewAlignments.getValueAt(fila - 1, 1).toString()));
        editRightSegment.setText(formatText(viewAlignments.getValueAt(fila - 1, 2).toString()));
      }

      if (identLabel == topArrays) {
        viewControls.setTranslationJoinEnabled(false);
        viewControls.setOriginalJoinEnabled(false);
      } else {
        viewControls.setTranslationJoinEnabled(true);
        viewControls.setOriginalJoinEnabled(true);
      }

      identAnt = identLabel;
    }

    updateAlignmentsView();
  }

  //  Accessed by SegmentEditor
  public final void setTextAreaPosition(int position) {
    positionTextArea = position;
  }

  //  Accessed by ControlView currently
  final void onRemoveBlankRows() {
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
            + lineasLimpiar + " " + getString("MSG.BLANK.ROWS"));

    if (lineasLimpiar > 0) {
      identChanges++;

      SegmentChanges changes = new SegmentChanges(3, 0, false, "", 0);
      arrayListChanges.add(identChanges, changes);
      changes.setNumEliminada(numEliminadas, lineasLimpiar);
      viewControls.setUndoEnabled(true);
      updateAlignmentsView();
    }
  }

  //  Accessed by ControlView currently
  final void onTuSplit() {
    int izq;
    int cont;
    SegmentChanges changes;
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    identChanges++;

    izq = viewAlignments.getSelectedColumn();

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
    updateAlignmentsView();
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
    mainWindowFonts.setUserInterfaceFont(font, this);
    mainWindowFonts.setTableFont(font, this);
    mainWindowFonts.setTableHeaderFont(font);
    mainWindowFonts.setSourceEditorFont(font, this);
    mainWindowFonts.setTargetEditorFont(font, this);
    viewControls.setFonts(font);
  }

  public final void setTableFont(final Font font) {
    mainWindowFonts.setTableFont(font, this);
  }

  public final void setUserInterfaceFont(final Font font) {
    mainWindowFonts.setUserInterfaceFont(font, this);
  }

  public final void setTableHeaderFont(final Font font) {
    mainWindowFonts.setTableHeaderFont(font);
  }

  public final void setSourceEditorFont(final Font font) {
    mainWindowFonts.setSourceEditorFont(font, this);
  }

  public final void setTargetEditorFont(final Font font) {
    mainWindowFonts.setTargetEditorFont(font, this);
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
   * Display fonts dialog.
   * 
   * <p>Display the fonts dialog to allow selection of fonts for
   * origianl/translation tables/editors and main window
   */
  private void displayFontSelector() {
    FontSelector dlgFonts = new FontSelector(this, mainWindowFonts.getFonts());
    dlgFonts.setVisible(true);
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
      quit();
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
   * class action listener implementation.
   *
   * @param action   gotten
   */
  @Override
  public final void actionPerformed(final ActionEvent action) {
    final Object actor = action.getSource();

    if (actor instanceof JMenuItem) {
      if (actor == mainWindowMenu.menuItemFileOpen) {
        handler.onOpenTmx();
      } else if (actor == mainWindowMenu.menuItemFileTextOpen) {
        handler.onOpenText();
      } else if (actor == mainWindowMenu.menuItemFileSave) {
        saveBitext();
      } else if (actor == mainWindowMenu.menuItemFileSaveAs) {
        saveBitext();
      } else if (actor == mainWindowMenu.menuItemFileClose) {
        onClose();
      } else if (actor == mainWindowMenu.menuItemFileQuit) {
        quit();
      } else if (actor == mainWindowMenu.menuItemSettingsFonts) {
        displayFontSelector();
        //  Only Linux, Solaris (UNIX?) with Gtk 2.2+
      } else if (actor == mainWindowMenu.menuItemLafGtk) {
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } else if (actor == mainWindowMenu.menuItemLafLiquid) {
        try {
          UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
          com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(false);
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        //  All platforms
      } else if (actor == mainWindowMenu.menuLafMetal) {
        try {
          UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
        //  Java 1.6 update 10+
      } else if (actor == mainWindowMenu.menuItemLafNimbus) {
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } else if (actor == mainWindowMenu.menuItemLafSystem) {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } else if (actor == mainWindowMenu.menuItemHelpManual) {
        handler.helpManualMenuItemActionPerformed();
      } else if (actor == mainWindowMenu.menuItemHelpAbout) {
        handler.helpAboutMenuItemActionPerformed();
      }
    }
  }

}
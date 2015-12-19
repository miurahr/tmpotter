/*
 #######################################################################
 #
 #  bitext2tmx - Bitext Aligner/TMX Editor
 #
 #  Copyright (C) 2005-2006 Susana Santos Antón
 #            (C) 2006-2009 Raymond: Martin et al
 #            (C) 2015 Hiroshi Miura
 #
 #  This program is free software; you can redistribute it and/or modify
 #  it under the terms of the GNU General Public License as published by
 #  the Free Software Foundation; either version 2 of the License, or
 #  (at your option) any later version.
 #
 #  This program is distributed in the hope that it will be useful,
 #  but WITHOUT ANY WARRANTY; without even the implied warranty of
 #  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 #  GNU General Public License for more details.
 #
 #  You should have received a copy of the GNU General Public License
 #  along with this program; if not, write to the Free Software
 #  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 #
 #######################################################################
 */
package bitext2tmx.ui;

import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.vlsolutions.swing.docking.ui.DockingUISettings;

import static org.openide.awt.Mnemonics.setLocalizedText;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;

import bitext2tmx.core.DocumentSegmenter;
import bitext2tmx.core.TranslationAligner;
import bitext2tmx.core.TMXReader;
import bitext2tmx.engine.Segment;
import bitext2tmx.engine.SegmentChanges;
import bitext2tmx.util.AppConstants;
import bitext2tmx.ui.dialogs.About;
import bitext2tmx.ui.dialogs.FontSelector;
import bitext2tmx.ui.dialogs.Encodings;
import bitext2tmx.ui.help.Manual;
import bitext2tmx.ui.dialogs.OpenTexts;
import bitext2tmx.core.Document;
import bitext2tmx.core.TMXWriter;
import bitext2tmx.util.RuntimePreferences;
import bitext2tmx.util.gui.AquaAdapter;
import bitext2tmx.util.Utilities;
import bitext2tmx.util.Platform;

import static bitext2tmx.util.Localization.getString;
import static bitext2tmx.util.StringUtil.formatText;
import static bitext2tmx.util.StringUtil.restoreText;

/**
 *
 */
final public class MainWindow extends JFrame implements ActionListener,
        WindowListener {

  final private static long serialVersionUID = -540065960678391862L;

  private DockingDesktop desktop;

  final private AlignmentsView viewAlignments = new AlignmentsView(this);
  final private SegmentEditor edLeftSegment = new SegmentEditor(this);
  final private SegmentEditor editRightSegment = new SegmentEditor(this);
  final private ControlView viewControls = new ControlView(this);

  //  Menubar
  final private JMenuBar _mbar = new JMenuBar();

  //  File menu
  private JMenu menuItemFile;
  private JMenuItem menuItemFileOpen;
  private JMenuItem menuItemFileSave;
  private JMenuItem menuItemFileSaveAs;
  private JMenuItem menuItemFileClose;
  private JMenuItem menuItemFileQuit;

  //  Settings menu
  private JMenu menuSettings;
  private JMenuItem menuItemSettingsFonts;
  private JMenuItem menuCallbackSettingsLinebreak;

  //  Look and Feel submenu
  private JMenu menuLaf;
  private JMenuItem menuItemLafGtk;
  private JMenuItem menuItemLafLiquid;
  private JMenuItem menuLafMetal;
  private JMenuItem menuItemLafNimbus;
  private JMenuItem menuItemLafSystem;

  //  Help menu
  private JMenu menuHelp;
  private JMenuItem menuItemHelpManual;
  private JMenuItem menuItemHelpAbout;

  //  Statusbar
  private JPanel panelStatusBar;
  private JLabel labelStatusBar;

  private Document documentOriginal;
  private Document documentTranslation;

  final private ArrayList arrayListBitext;

  final private ArrayList<SegmentChanges> arrayListChanges;
  final private ArrayList arrayListLang;

  private int topArrays;    //  =  0;
  private int positionTextArea;  //  =  0;
  private int identChanges = -1;
  private int identLabel;  //  =  0;
  private int _identAnt;     //  =  0;

  private String stringLangOriginal = "en";
  private String stringLangTranslation = "en";
  private String stringOriginal;
  private String stringTranslation;

  private File userHome = new File(System.getProperty("user.home"));
  private File filePathOriginal;
  private File filePathTranslation;

  private Font _fntUserInterface;
  private Font fontTableHeader;
  private Font fontTable;
  private Font _fntSourceEditor;
  private Font _fntTargetEditor;

  public MainWindow() {
    this.arrayListBitext = new ArrayList();
    this.arrayListChanges = new ArrayList<>();
    this.arrayListLang = new ArrayList();

    initDockingUI();
    makeUI();
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
      final public void windowClosing(final WindowEvent e) {
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
  private static final Logger LOG = Logger.getLogger(MainWindow.class.getName());

  private void initDockingUI() {
    DockingUISettings.getInstance().installUI();

    UIManager.put("DockViewTitleBar.titleFont", getUserInterfaceFont());

    UIManager.put("DockViewTitleBar.close", getDesktopIcon("close.png"));
    UIManager.put("DockViewTitleBar.close.rollover", getDesktopIcon("close_hovered.png"));
    UIManager.put("DockViewTitleBar.close.pressed", getDesktopIcon("close_pressed.png"));

    UIManager.put("DockViewTitleBar.hide", getDesktopIcon("min.png"));
    UIManager.put("DockViewTitleBar.hide.rollover", getDesktopIcon("min_hovered.png"));
    UIManager.put("DockViewTitleBar.hide.pressed", getDesktopIcon("min_pressed.png"));
    UIManager.put("DockViewTitleBar.maximize", getDesktopIcon("max.png"));
    UIManager.put("DockViewTitleBar.maximize.rollover", getDesktopIcon("max_hovered.png"));
    UIManager.put("DockViewTitleBar.maximize.pressed", getDesktopIcon("max_pressed.png"));

    UIManager.put("DockViewTitleBar.restore", getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.restore.rollover", getDesktopIcon("restore_hovered.png"));
    UIManager.put("DockViewTitleBar.restore.pressed", getDesktopIcon("restore_pressed.png"));

    UIManager.put("DockViewTitleBar.dock", getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.dock.rollover", getDesktopIcon("restore_hovered.png"));
    UIManager.put("DockViewTitleBar.dock.pressed", getDesktopIcon("restore_pressed.png"));

    UIManager.put("DockViewTitleBar.float", getDesktopIcon("shade.png"));
    UIManager.put("DockViewTitleBar.float.rollover", getDesktopIcon("shade_hovered.png"));
    UIManager.put("DockViewTitleBar.float.pressed", getDesktopIcon("shade_pressed.png"));

    UIManager.put("DockViewTitleBar.attach", getDesktopIcon("un_shade.png"));
    UIManager.put("DockViewTitleBar.attach.rollover", getDesktopIcon("un_shade_hovered.png"));
    UIManager.put("DockViewTitleBar.attach.pressed", getDesktopIcon("un_shade_pressed.png"));

    UIManager.put("DockViewTitleBar.menu.hide", getDesktopIcon("min.png"));
    UIManager.put("DockViewTitleBar.menu.maximize", getDesktopIcon("max.png"));
    UIManager.put("DockViewTitleBar.menu.restore", getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.menu.dock", getDesktopIcon("restore.png"));
    UIManager.put("DockViewTitleBar.menu.float", getDesktopIcon("shade.png"));
    UIManager.put("DockViewTitleBar.menu.attach", getDesktopIcon("un_shade.png"));
    UIManager.put("DockViewTitleBar.menu.close", getDesktopIcon("close.png"));

    UIManager.put("DockTabbedPane.close", getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.close.rollover", getDesktopIcon("close_hovered.png"));
    UIManager.put("DockTabbedPane.close.pressed", getDesktopIcon("close_pressed.png"));

    UIManager.put("DockTabbedPane.menu.close", getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.menu.hide", getDesktopIcon("shade.png"));
    UIManager.put("DockTabbedPane.menu.maximize", getDesktopIcon("max.png"));
    UIManager.put("DockTabbedPane.menu.float", getDesktopIcon("shade.png"));
    UIManager.put("DockTabbedPane.menu.closeAll", getDesktopIcon("close.png"));
    UIManager.put("DockTabbedPane.menu.closeAllOther", getDesktopIcon("close.png"));

    UIManager.put("DragControler.detachCursor", getDesktopIcon("shade.png").getImage());

    UIManager.put("DockViewTitleBar.closeButtonText", getString("VW.TITLEBAR.BTNCLOSE"));
    UIManager.put("DockViewTitleBar.minimizeButtonText", getString("VW.TITLEBAR.BTNMINIMIZE"));
    UIManager.put("DockViewTitleBar.maximizeButtonText", getString("VW.TITLEBAR.BTNMAXIMIZE"));
    UIManager.put("DockViewTitleBar.restoreButtonText", getString("VW.TITLEBAR.BTNRESTORE"));
    UIManager.put("DockViewTitleBar.floatButtonText", getString("VW.TITLEBAR.BTNFLOAT"));
    UIManager.put("DockViewTitleBar.attachButtonText", getString("VW.TITLEBAR.BTNATTACH"));

    UIManager.put("DockTabbedPane.closeButtonText", getString("TAB.BTNCLOSE"));
    UIManager.put("DockTabbedPane.minimizeButtonText", getString("TAB.BTNMINIMIZE"));
    UIManager.put("DockTabbedPane.restoreButtonText", getString("TAB.BTNRESTORE"));
    UIManager.put("DockTabbedPane.maximizeButtonText", getString("TAB.BTNMAXIMIZE"));
    UIManager.put("DockTabbedPane.floatButtonText", getString("TAB.BTNFLOAT"));

  }

  private ImageIcon getDesktopIcon(final String iconName) {
    if (Platform.isMacOSX()) {
      return (getIcon("desktop/osx/" + iconName));
    }

    return (getIcon("desktop/" + iconName));
  }

  private ImageIcon getIcon(final String iconName) {
    return Icons.getIcon(iconName);
  }

  /**
   * Set root window icon
   */
  private void setWindowIcon() {
    try {
      //setIconImage( Icons.getIcon( "icon-small.png" ).getImage() ); 
    } catch (final Exception e) {
      System.out.println("Error loading icon: " + e);
    }
  }

  private void makeUI() {
    makeMenus();

    labelStatusBar = new JLabel(" ");
    panelStatusBar = new JPanel();
    panelStatusBar.setLayout(new BoxLayout(panelStatusBar, BoxLayout.LINE_AXIS));
    panelStatusBar.add(Box.createRigidArea(new Dimension(10, 0)));
    panelStatusBar.add(labelStatusBar);

    desktop = new DockingDesktop();
    getContentPane().add(desktop, BorderLayout.CENTER);

    //  dock objects already exist at this point
    desktop.registerDockable(viewAlignments);
    desktop.registerDockable(edLeftSegment);
    desktop.registerDockable(editRightSegment);
    desktop.registerDockable(viewControls);

    DockKey keyAlignmentTable = viewAlignments.getDockKey();
    DockKey keyLeftSegment = edLeftSegment.getDockKey();
    DockKey keyRightSegment = editRightSegment.getDockKey();
    DockKey keySegmentButtons = viewControls.getDockKey();

    keyLeftSegment.setName(getString("VW.ORIGINAL.NAME"));
    keyLeftSegment.setTooltip(getString("VW.ORIGINAL.TOOLTIP"));
    keyRightSegment.setName(getString("VW.TRANSLATION.NAME"));
    keyRightSegment.setTooltip(getString("VW.TRANSLATION.TOOLTIP"));

    keyAlignmentTable.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    keyLeftSegment.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    keyRightSegment.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
    keySegmentButtons.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);

    keyAlignmentTable.setFloatEnabled(true);
    keyLeftSegment.setFloatEnabled(true);
    keyRightSegment.setFloatEnabled(true);
    keySegmentButtons.setFloatEnabled(true);

    keyAlignmentTable.setCloseEnabled(false);
    keyLeftSegment.setCloseEnabled(false);
    keyRightSegment.setCloseEnabled(false);
    keySegmentButtons.setCloseEnabled(false);

    keySegmentButtons.setResizeWeight(0.1f);

    desktop.addDockable(viewAlignments);

    desktop.split(viewAlignments, edLeftSegment, DockingConstants.SPLIT_BOTTOM);
    desktop.split(edLeftSegment, viewControls, DockingConstants.SPLIT_BOTTOM);
    desktop.split(edLeftSegment, editRightSegment, DockingConstants.SPLIT_RIGHT);

    this.setSize(new Dimension(800, 600));
    this.setMinimumSize(new Dimension(640, 480));

    setTitle(AppConstants.getDisplayNameAndVersion());
    getContentPane().add(panelStatusBar, BorderLayout.SOUTH);
  }

  /**
   * Used by makeMenuComponent to select componenet type
   */
  public static enum MenuComponentType {

    CHECKBOX, ITEM, MENU, RADIOBUTTON
  };

  /**
   * Return a new menu component
   *
   * Can return subclasses of JMenuItem: including JMenu! Downcast return type
   * to as needed
   */
  private <T extends JMenuItem> T makeMenuComponent(final MenuComponentType menuComponentType,
          final KeyStroke ksShortcut, final ImageIcon icon, final String strText,
          final String strKey) {
    JMenuItem menuItem;

    assert strText != null;

    switch (menuComponentType) {
      case ITEM:
        menuItem = new JMenuItem();
        break;
      case CHECKBOX:
        menuItem = new JCheckBoxMenuItem();
        break;
      case MENU:
        menuItem = new JMenu();
        break;
      case RADIOBUTTON:
        menuItem = new JRadioButtonMenuItem();
        break;
      default:
        menuItem = new JMenuItem();
        break;
    }

    //  Default text, when no localization available, testing
    menuItem.setText(strText);

    if (ksShortcut != null) {
      menuItem.setAccelerator(ksShortcut);
    }
    if (!Platform.isMacOSX() && icon != null) {
      menuItem.setIcon(icon);
    }

    menuItem.addActionListener(this);

    //  Localized text
    if (strKey != null) {
      setLocalizedText(menuItem, getString(strKey));
    }

    @SuppressWarnings("unchecked")
    T res = (T) menuItem;
    return (res);
  }

  private void makeMenus() {
    menuItemFile = makeMenuComponent(MenuComponentType.MENU, null, null, "File", "MNU.FILE");

    menuItemFileOpen = makeMenuComponent(MenuComponentType.ITEM, KeyStroke.
            getKeyStroke('O', KeyEvent.CTRL_MASK, false), getIcon("project_open.png"),
            "Open...", "MNI.FILE.OPEN");

    menuItemFileSave = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('S', KeyEvent.CTRL_MASK, false),
            getIcon("filesave.png"), "Save", "MNI.FILE.SAVE");
    menuItemFileSave.setEnabled(false);

    menuItemFileSaveAs = makeMenuComponent(MenuComponentType.ITEM, null,
            getIcon("filesave.png"), "Save As...", "MNI.FILE.SAVEAS");
    menuItemFileSaveAs.setEnabled(false);

    menuItemFileClose = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('W', KeyEvent.CTRL_MASK, false),
            getIcon("fileclose.png"), "Close", "MNI.FILE.ABORT");
    menuItemFileClose.setEnabled(false);

    menuItemFileQuit = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke('Q', KeyEvent.CTRL_MASK, false),
            getIcon("application-exit.png"), "Quit", "MNI.FILE.EXIT");

    menuSettings = makeMenuComponent(MenuComponentType.MENU, null, null,
            "Settings", "MNU.SETTINGS");

    menuCallbackSettingsLinebreak = makeMenuComponent(MenuComponentType.CHECKBOX, null, null,
            "Linebreaks", "MNI.SETTINGS.LINEBREAK");
    menuCallbackSettingsLinebreak.setToolTipText(getString("MNI.SETTINGS.LINEBREAK.TOOLTIP"));
    menuCallbackSettingsLinebreak.addChangeListener(new javax.swing.event.ChangeListener() {
      @Override
      final public void stateChanged(final ChangeEvent e) {
        RuntimePreferences.setSegmentByLineBreak(menuCallbackSettingsLinebreak.isSelected());
        onLinebreakToggle();
      }
    });

    menuItemSettingsFonts = makeMenuComponent(MenuComponentType.ITEM, null,
            getIcon("fonts.png"), "Configure Fonts...", "MNI.SETTINGS.FONTS");
    menuItemSettingsFonts.setToolTipText(getString("MNI.SETTINGS.FONTS.TOOLTIP"));

    menuHelp = makeMenuComponent(MenuComponentType.MENU, null, null,
            "Help", "MNU.HELP");

    menuItemHelpAbout = makeMenuComponent(MenuComponentType.ITEM, null,
            getIcon("icon-small.png"), "About", "MNI.HELP.ABOUT");

    menuItemHelpManual = makeMenuComponent(MenuComponentType.ITEM,
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
            getIcon("help-contents.png"), "Manual", "MNI.HELP.MANUAL");

    menuItemFile.add(menuItemFileOpen);
    menuItemFile.addSeparator();
    menuItemFile.add(menuItemFileSave);
    menuItemFile.add(menuItemFileSaveAs);
    menuItemFile.addSeparator();
    menuItemFile.add(menuItemFileClose);

    if (!Platform.isMacOSX()) {
      menuItemFile.addSeparator();
      menuItemFile.add(menuItemFileQuit);
    }

    menuSettings.add(menuCallbackSettingsLinebreak);
    menuSettings.add(menuItemSettingsFonts);

    if (!Platform.isMacOSX()) {
      menuLaf = makeMenuComponent(MenuComponentType.MENU, null, null,
              "Look and Feel", null);

      menuItemLafLiquid = makeMenuComponent(MenuComponentType.ITEM, null, null, "Liquid",
              null);

      menuLafMetal = makeMenuComponent(MenuComponentType.ITEM, null, null, "Metal",
              null);

      menuItemLafNimbus = makeMenuComponent(MenuComponentType.ITEM, null, null, "Nimbus",
              null);

      menuItemLafSystem = makeMenuComponent(MenuComponentType.ITEM, null, null, "System",
              null);

      menuItemLafLiquid.setMnemonic('L');
      menuLafMetal.setMnemonic('M');
      menuItemLafNimbus.setMnemonic('N');
      menuItemLafSystem.setMnemonic('Y');

      if (!Platform.isWindows()) {
        menuItemLafGtk = makeMenuComponent(MenuComponentType.ITEM, null, null, "Gtk",
                null);
        menuItemLafGtk.setMnemonic('G');
        menuLaf.add(menuItemLafGtk);
      }

      menuLaf.add(menuItemLafLiquid);
      menuLaf.add(menuLafMetal);
      menuLaf.add(menuItemLafNimbus);
      menuLaf.add(menuItemLafSystem);

      menuSettings.add(menuLaf);
    }

    menuSettings.add(menuLaf);

    menuHelp.add(menuItemHelpManual);

    if (!Platform.isMacOSX()) {
      menuHelp.addSeparator();
      menuHelp.add(menuItemHelpAbout);
    }

    _mbar.add(menuItemFile);
    _mbar.add(menuSettings);
    _mbar.add(menuHelp);

    setJMenuBar(_mbar);
  }

  /**
   * Quit application
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
   * Open dialog to select the files we want to align/convert
   *
   * Esta funci�n abre un cuadro de di�logo para seleccionar los ficheros que
   * queremos alinear.
   */
  private void onOpen() {
    String originalEncoding;
    String translateEncoding;

    final OpenTexts dlg = new OpenTexts();
    dlg.setPath(userHome);
    dlg.setModal(true);
    dlg.setVisible(true);

    if (!dlg.isClosed()) {
      userHome = dlg.getPath();
      originalEncoding = (String) dlg.getSourceLangEncComboBox().getSelectedItem();
      filePathOriginal = dlg.getSourcePath();
      stringOriginal = dlg.getSource();
      stringLangOriginal = dlg.getSourceLocale();
      viewAlignments.buildDisplay();
      documentOriginal = new Document();
      documentTranslation = new Document();

      if (dlg.getTypes() == 0) {
        //getSelectedItem()
        translateEncoding = (String) dlg.getTargetLangEncComboBox().getSelectedItem();
        filePathTranslation = dlg.getTargetPath();
        stringTranslation = dlg.getTarget();
        stringLangTranslation = dlg.getTargetLocale();

        try {
          documentOriginal = DocumentSegmenter.readDocument(stringOriginal, stringLangOriginal, originalEncoding);
          documentTranslation = DocumentSegmenter.readDocument(stringTranslation, stringLangTranslation, translateEncoding);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, getString("MSG.ERROR"),
                  getString("MSG.ERROR.FILE.READ"), JOptionPane.ERROR_MESSAGE);
          this.dispose();
        }

        boolean res = TranslationAligner.align(documentOriginal, documentTranslation);

        if (res) {
          matchArrays();

          for (int cont = 0; cont < documentOriginal.size(); cont++) {
            if (documentOriginal.get(cont) == null
                    || (documentOriginal.get(cont).equals(""))
                    && (documentTranslation.get(cont) == null
                    || documentTranslation.get(cont).equals(""))) {
              documentOriginal.remove(cont);
              documentTranslation.remove(cont);
            }
          }
        }
      } else {
        try {
          TMXReader.readTMX(filePathOriginal, originalEncoding,
                  documentOriginal, documentTranslation);
        } catch (Exception ex) {
          Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        filePathTranslation = filePathOriginal;
      }

      initializeAlignmentsView();
      updateAlignmentsView();
      viewControls.enableButtons(true);
      //_vwControls._btnUndo.setEnabled( false );
      viewControls.setUndoEnabled(false);
      menuItemFileSaveAs.setEnabled(true);
      menuItemFileClose.setEnabled(true);

      dlg.dispose();
    }
  }

  /**
   * Actions to perform when closing alignment/editing session. Leaves the text
   * as it was so that it can be processed later.
   *
   * Esta funci�n contiene la funcionalidad del bot�n abortar. Deja el texto
   * como estaba para volver a tratarlo m�s tarde.
   */
  private void onClose() {
    clear();
    //BotonesFalse();
    viewControls.enableButtons(false);
    //  Done in _vwControls
    //_btnUndo.setEnabled( false );
    // Undo is separate
    viewControls.setUndoEnabled(false);
    menuItemFileSave.setEnabled(false);
    menuItemFileSaveAs.setEnabled(false);
    menuItemFileClose.setEnabled(false);
  }

  //  ToDo: implement proper functionality; not used currently
  final void onSave() {
    saveBitext();
  }

  final void onSaveAs() {
    saveBitext();
  }

  final void displayAbout() {
    final About dlg = new About(this);
    dlg.setVisible(true);
  }

  /**
   * Necessary capabilities to store the bitext
   *
   */
  private void saveBitext() {
    for (int cont = 0; cont < (documentOriginal.size() - 1); cont++) {
      if (documentOriginal.get(cont).equals("") && documentTranslation.get(cont).equals("")) {
        documentOriginal.remove(cont);
        documentTranslation.remove(cont);
      }
    }

    try {
      String outFileName = filePathOriginal.getName();
      String outFileNameBase = outFileName.
              substring(0, (filePathOriginal.getName().length() - 4));
      boolean save = false;
      boolean cancel = false;

      userHome = new File(RuntimePreferences.getUserHome());
      File outFile = new File(outFileNameBase.concat(stringLangTranslation + ".tmx"));

      while (!save && !cancel) {
        final JFileChooser fc = new JFileChooser();

        //  switch() on language removed from here -RM
        boolean kNombre_Usuario = false;

        while (!kNombre_Usuario) {
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
            }

            kNombre_Usuario = true;
          } else {
            kNombre_Usuario = true;
            cancel = true;
          }
        }

        int n;
        if (kNombre_Usuario && !cancel) {
          if (outFile.exists()) {
            final Object[] options = {getString("BTN.SAVE"),
              getString("BTN.CANCEL")};

            n = JOptionPane.showOptionDialog(this, getString("MSG.FILE.EXISTS"), getString("MSG.WARNING"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            //  Overwrite
            if (n == 0) {
              save = true;
            }
          }
          else {
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

      TMXWriter.writeBitext(outFile,
              documentOriginal, stringLangOriginal,
              documentTranslation, stringLangTranslation,
              encoding);

    } catch (IOException ex) {
      JOptionPane.
              showMessageDialog(this, (String) arrayListLang.get(21),
                      (String) arrayListLang.get(18), JOptionPane.ERROR_MESSAGE);
      this.dispose();
    }
  }

  /**
   *
   * Initialize values to start the validation of the following alignment
   */
  private void clear() {
    int cont = documentOriginal.size() - 1;

    while (!documentOriginal.isEmpty()) {
      documentOriginal.remove(cont--);
    }

    cont = documentTranslation.size() - 1;

    while (!documentTranslation.isEmpty()) {
      documentTranslation.remove(cont--);
    }

    cont = arrayListBitext.size() - 1;

    while (!arrayListBitext.isEmpty()) {
      arrayListBitext.remove(cont--);
    }

    cont = arrayListChanges.size() - 1;

    while (!arrayListChanges.isEmpty()) {
      arrayListChanges.remove(cont--);
    }

    identChanges = -1;
    identLabel = 0;
    _identAnt = 0;
    filePathTranslation = null;
    filePathOriginal = null;
    viewControls.setUndoEnabled(false);
    viewAlignments.clear();
    topArrays = 0;

    edLeftSegment.setText("");
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
   * This function updates the changes adding a delete change
   * to the undo array and deletes
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be deleted
   */
  private void delete(final boolean textAreaIzq) {
    final SegmentChanges Changes = new SegmentChanges(1, positionTextArea, textAreaIzq,
            "", identLabel);
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
   * This function updates the changes adding a split to the undo
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
   * This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  private void updateAlignmentsView() {
    if (!documentOriginal.isEmpty() && !documentTranslation.isEmpty()) {
      matchArrays();
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
        Segment nSeg = new Segment(null, null, null);
        viewAlignments.addModelSegment(nSeg);
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

    edLeftSegment.setText(formatText(viewAlignments.getValueAt(
            identLabel, 1).toString()));
    editRightSegment.setText(formatText(viewAlignments.getValueAt(
            identLabel, 2).toString()));
  }

  /**
   * Function IgualarArrays: adds rows to the smallest array and deletes blank
   * rows.
   */
  private void matchArrays() {
    boolean limpiar = true;

    while (documentOriginal.size() > documentTranslation.size()) {
      documentTranslation.add(documentTranslation.size(), "");
    }

    while (documentTranslation.size() > documentOriginal.size()) {
      documentOriginal.add(documentOriginal.size(), "");
    }

    while (limpiar) {
      // Delete blank lines at the end
      if (documentOriginal.get(documentOriginal.size() - 1) == null
              || (documentOriginal.get(documentOriginal.size() - 1).equals(""))
              && (documentTranslation.get(documentTranslation.size() - 1) == null || documentTranslation.get(
                      documentTranslation.size() - 1).equals(""))) {
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
    undoChanges();
    arrayListChanges.remove(identChanges);
    identChanges--;

    if (identChanges == -1) {
      viewControls.setUndoEnabled(false);
    }
  }

  /**
   * Undoes the last delete
   */
  private void undoDelete() {
    SegmentChanges ultChanges = arrayListChanges.get(identChanges);

    identLabel = ultChanges.getIdent_linea();
    boolean izq = ultChanges.getSource();

    if (izq) {
      // Left part
      if (identLabel == documentOriginal.size()) {
        // Revert the deleting of the last line
        documentOriginal.add(identLabel, ultChanges.getFrase());

        if (documentOriginal.size() != documentTranslation.size()) {
          documentTranslation.add(documentTranslation.size(), "");
        }
      } else {
        // Intermediate deletions
        documentOriginal.add(documentOriginal.size(),
                documentOriginal.get(documentOriginal.size() - 1));
        for (int cont = documentOriginal.size() - 1; cont > identLabel; cont--) {
          documentOriginal.set(cont, documentOriginal.get(cont - 1));
        }

        documentOriginal.set(identLabel, ultChanges.getFrase());
      }
    } else {
      // Parte derecha
      // Right part
      if (identLabel == documentTranslation.size()) {
        documentTranslation.add(identLabel, ultChanges.getFrase());

        if (documentOriginal.size() != documentTranslation.size()) {
          documentOriginal.add(documentOriginal.size(), "");
        }
      } else {
        int cont = documentTranslation.size() - 1;
        // The source text had an empty string aligned: restore
        documentTranslation.add(documentTranslation.size(), documentTranslation.
                get(documentTranslation.size() - 1));

        for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
          documentTranslation.set(cont, documentTranslation.get(cont - 1));
        }

        documentTranslation.set(identLabel, ultChanges.getFrase());
      }
    }

    updateAlignmentsView();
  }

  /**
   * Undo last change
   *
   */
  private void undoChanges() {
    String cad;
    SegmentChanges ultChanges = new SegmentChanges();
    int tam = 0;

    ultChanges = arrayListChanges.get(identChanges);
    identLabel = ultChanges.getIdent_linea();
    int operacion = ultChanges.getTipo();
    int position;
    boolean izq = ultChanges.getSource();
    _identAnt = identLabel;

    switch (operacion) {
      //if( operacion == 0 )
      case 0: {
        // El complementario de Unir es Split
        // The complement of Join is Split
        final String cadaux = ultChanges.getFrase();

        if (izq) {
          cad = documentOriginal.get(identLabel);

          if (!cad.equals("")) {
            cad = cad.trim();
          }

          position = cad.indexOf(cadaux) + cadaux.length();
        } else {
          cad = documentTranslation.get(identLabel);

          if (!cad.equals("")) {
            cad = cad.trim();
          }

          position = cad.indexOf(cadaux) + cadaux.length();
        }

        if (ultChanges.getSource()) {
          documentOriginal.split(identLabel, position);
        } else {
          documentTranslation.split(identLabel, position);
        }
        updateAlignmentsView();
        break;
      }

      //else if( operacion == 1 )
      case 1:
        // El complementario de Borrar es Insertar
        // The complement of Delete is Insert
        undoDelete();
        break;

      //else if( operacion == 2 )
      case 2: {
        // El complementario de Split es Unir
        // The complement of Split is Join
        int cont;

        // modifyAlignments(ultCambio.getFuente(), 0,
        // 0,ultCambio.getEliminada_fila());
        cont = identLabel + 1;

        if (izq) {
          cad = ultChanges.getFrase();
          documentOriginal.set(identLabel, cad.trim());

          while (cont < topArrays) {
            documentOriginal.set(cont, documentOriginal.get(cont + 1));
            cont++;
          }

          documentOriginal.set(documentOriginal.size() - 1, "");
        } else {
          cad = ultChanges.getFrase();
          documentTranslation.set(identLabel, cad.trim());

          while (cont < topArrays) {
            documentTranslation.set(cont, documentTranslation.get(cont + 1));
            cont++;
          }

          documentTranslation.set(documentTranslation.size() - 1, "");
        }

        updateAlignmentsView();

        break;
      }

      //else if( operacion == 3 )
      case 3: {
        // Se eliminaron lineas enteras en blanco que hay que recuperar
        // Blank lines were deleted and have to be restored
        tam = ultChanges.getTam();
        int[] filasEliminadas;

        filasEliminadas = ultChanges.getNumEliminada();

        while (tam > 0) {
          // Se crean tantas filas como las que se eliminaron
          // Create as many files as those that were eliminated
          documentTranslation.add(documentTranslation.size(), "");
          documentOriginal.add(documentOriginal.size(), "");
          topArrays = documentTranslation.size() - 1;
          tam--;
        }

        int cont2 = documentOriginal.size() - 1;
        tam = ultChanges.getTam();

        while (cont2 >= tam && tam > 0) {
          // recorre los arrays para colocar las lineas en blanco donde
          // corresponden
          // moves across the arrays to place blank lines where needed
          if (cont2 == filasEliminadas[tam - 1]) {
            documentTranslation.set(cont2, "");
            documentOriginal.set(cont2, "");
            tam--;
          } else {
            documentTranslation.set(cont2, documentTranslation.get(cont2 - tam));
            documentOriginal.set(cont2, documentOriginal.get(cont2 - tam));
          }

          cont2--;
        }

        updateAlignmentsView();

        break;
      }

      //else if( operacion == 4 )
      case 4: {
        // Split TU
        if (izq) {
          documentTranslation.set(identLabel, documentTranslation.get(identLabel + 1));
          documentOriginal.remove(identLabel + 1);
          documentTranslation.remove(identLabel + 1);
        } else {
          documentOriginal.set(identLabel, documentOriginal.get(identLabel + 1));
          documentOriginal.remove(identLabel + 1);
          documentTranslation.remove(identLabel + 1);
        }

        updateAlignmentsView();
      }
    }//  switch()

  }

  //  Accessed by AlignmentsView
  final void onTableClicked() {
    // if (e.getClickCount() == 1 || e.getClickCount() == 2) {
    positionTextArea = 0;

    if (_identAnt < documentOriginal.size()) {
      //  ToDo: replace with docking editors call
      documentOriginal.set(_identAnt, restoreText(edLeftSegment.getText()));
      documentTranslation.set(_identAnt, restoreText(editRightSegment.getText()));
    }

    edLeftSegment.setText(formatText(viewAlignments.
            getValueAt(viewAlignments.getSelectedRow(), 1).toString()));
    editRightSegment.setText(formatText(viewAlignments.
            getValueAt(viewAlignments.getSelectedRow(), 2).toString()));

    identLabel = viewAlignments.getSelectedRow();
    _identAnt = identLabel;

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
  final void onTablePressed(final KeyEvent e) {
    int fila;

    if (viewAlignments.getSelectedRow() != -1) {
      fila = viewAlignments.getSelectedRow();
      positionTextArea = 0;
    } else {
      fila = 1;
    }

    if (fila < viewAlignments.getRowCount() - 1) {
      if ((e.getKeyCode() == KeyEvent.VK_DOWN)
              || (e.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
        if (_identAnt < documentOriginal.size()) {
          documentOriginal.set(_identAnt, restoreText(edLeftSegment.getText()));
          documentTranslation.set(_identAnt, restoreText(editRightSegment.getText()));
        }

        edLeftSegment.setText(formatText(viewAlignments.getValueAt(fila + 1, 1).toString()));
        editRightSegment.setText(formatText(viewAlignments.getValueAt(fila + 1, 2).toString()));

        identLabel = fila + 1;
      } else if ((e.getKeyCode() == KeyEvent.VK_UP)
              || (e.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
        identLabel = fila - 1;

        if (fila == 0) {
          fila = 1;
          identLabel = 0;
        }

        if (_identAnt < documentOriginal.size()) {
          documentOriginal.set(_identAnt, restoreText(edLeftSegment.getText()));
          documentTranslation.set(_identAnt, restoreText(editRightSegment.getText()));
        }

        edLeftSegment.setText(formatText(viewAlignments.getValueAt(fila - 1, 1).toString()));
        editRightSegment.setText(formatText(viewAlignments.getValueAt(fila - 1, 2).toString()));
      }

      if (identLabel == topArrays) {
        viewControls.setTranslationJoinEnabled(false);
        viewControls.setOriginalJoinEnabled(false);
      } else {
        viewControls.setTranslationJoinEnabled(true);
        viewControls.setOriginalJoinEnabled(true);
      }

      _identAnt = identLabel;
    }

    updateAlignmentsView();
  }

  //  Accessed by SegmentEditor
  final public void setTextAreaPosition(int iPos) {
    positionTextArea = iPos;
  }

  private void onLinebreakToggle() {

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
      if ((documentOriginal.get(cont) == null || documentOriginal.get(cont).equals(""))
              && (documentTranslation.get(cont) == null || documentTranslation.get(cont).equals(""))) {
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

      SegmentChanges Changes = new SegmentChanges(3, 0, false, "", 0);
      arrayListChanges.add(identChanges, Changes);
      Changes.setNumEliminada(numEliminadas, lineasLimpiar);
      viewControls.setUndoEnabled(true);
      updateAlignmentsView();
    }
  }

  //  Accessed by ControlView currently
  final void onTUSplit() {
    int izq;
    int cont;
    SegmentChanges Changes;
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
      Changes = new SegmentChanges(4, 0, true, "", identLabel);

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
      Changes = new SegmentChanges(4, 0, false, "", identLabel);

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

    arrayListChanges.add(identChanges, Changes);
    updateAlignmentsView();
  }

  private void displayManual() {
    final Manual dlg = new Manual();

    dlg.setVisible(true);
  }

  /////////////////////////////////////////////////////////////////////
  //  Fonts
  //
  //  Note: all this fonts stuff will be refactored into its own class
  //  later so it will be independent
  /**
   * Fonts mutator Delegates actual setting of fonts to specific methods
   *
   * @param font to be configured
   *
   * Passing in null causes default values to be used - used at startup or for
   * reset Passing in a font causes all UI elements to be the same - used with
   * the 'All' window area when selected in the fonts dialog
   */
  final public void setFonts(final Font font) {
    //  Delegate
    setUserInterfaceFont(font);
    setTableFont(font);
    setTableHeaderFont(font);
    setSourceEditorFont(font);
    setTargetEditorFont(font);

    //  ToDo: 
    //_vwAlignments.setFonts( font );
    //_edLeftSegment.setFonts( font );
    //_edRightSegment.setFonts( font );
    viewControls.setFonts(font);
  }

  /**
   * Fonts accessor
   *
   * @return Font[]
   */
  final public Font[] getFonts() {
    final Font[] afnt
            = {
              _fntUserInterface,
              fontTable,
              fontTableHeader,
              _fntSourceEditor,
              _fntTargetEditor
            };

    return (afnt);
  }

  /**
   * User interface font mutator
   *
   * @param font
   */
  final public void setUserInterfaceFont(final Font font) {
    _fntUserInterface = font;

    if (_fntUserInterface != null) {
      //  Write to user preferences goes here
      //  To be done -RM

    } //  Default font (e.g. At startup from prefs file or for reset)    
    else {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  And supply a fallback default
      final String strFontName = "Serif";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;

      //  Da font
      _fntUserInterface = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }

    //  Use delegate to set actual UI fonts
    setUserInterfaceFonts(_fntUserInterface);
  }

  /**
   * User interface components font mutator Acts as delegate for
   * setUserInterfaceFont()
   *
   * @param font
   */
  final public void setUserInterfaceFonts(final Font font) {
    //  File menu
    menuItemFile.setFont(font);
    menuItemFileOpen.setFont(font);
    menuItemFileSave.setFont(font);
    menuItemFileSaveAs.setFont(font);
    menuItemFileClose.setFont(font);

    if (!Platform.isMacOSX()) {
      menuItemFileQuit.setFont(font);
    }

    //  Settings menu
    menuSettings.setFont(font);
    menuItemSettingsFonts.setFont(font);
    menuCallbackSettingsLinebreak.setFont(font);

    if (!Platform.isMacOSX()) {
      menuLaf.setFont(font);
      menuItemLafLiquid.setFont(font);
      menuLafMetal.setFont(font);
      menuItemLafNimbus.setFont(font);
      menuItemLafSystem.setFont(font);

      if (!Platform.isWindows()) {
        menuItemLafGtk.setFont(font);
      }
    }

    //  Help menu
    menuHelp.setFont(font);
    menuItemHelpManual.setFont(font);

    if (!Platform.isMacOSX()) {
      menuItemHelpAbout.setFont(font);
    }
  }

  /**
   * User interface font accessor
   *
   * @return Font
   */
  final public Font getUserInterfaceFont() {
    return (_fntUserInterface);
  }

  /**
   * Table header font mutator
   *
   * @param Font
   */
  final public void setTableHeaderFont(final Font font) {
    fontTableHeader = font;

    if (fontTableHeader != null) {
      //  Write to user preferences goes here
      //  To be done -RM

    } //  Default font (e.g. At startup from prefs file or for reset)    
    else {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  And supply a fallback default
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;

      //  Da font
      fontTableHeader = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }

    //  Set it in the source table
    //_tbl.getTableHeader().setFont( _fntTableHeader );
    //_vwAlignments.getTableHeader().setFont( _fntTableHeader );
  }

  /**
   * Table header font accessor
   *
   * @return Font
   */
  final public Font getTableHeaderFont() {
    return (fontTableHeader);
  }

  /**
   * Table font mutator
   *
   * @param font
   */
  final public void setTableFont(final Font font) {
    fontTable = font;

    if (fontTable != null) {
      //  Write to user preferences goes here
      //  To be done -RM

    } //  Default font (e.g. At startup from prefs file or for reset)    
    else {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  Or supply a fallback default
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;

      //  Da font
      fontTable
              = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }

    //  Set it in the source table
    viewAlignments.setTableFont(fontTable);
  }

  /**
   * Table font accessor
   *
   * @return Font retrieve font for table
   */
  final public Font getTableFont() {
    return (fontTable);
  }

  /**
   * Original editor font mutator
   *
   * @param font set editor font to display
   */
  final public void setSourceEditorFont(final Font font) {
    _fntSourceEditor = font;

    if (_fntSourceEditor != null) {
      //  Write to user preferences goes here
      //  To be done -RM

    } //  Default font (e.g. At startup from prefs file or for reset)    
    else {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  Or supply a fallback default
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;

      //  Da font
      _fntSourceEditor
              = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }

    //  Set it in the source table
    edLeftSegment.setEditorFont(_fntSourceEditor);
  }

  /**
   * Original editor font accessor
   *
   * @return font
   */
  final public Font getSourceEditorFont() {
    return (_fntSourceEditor);
  }

  /**
   * Translation editor font mutator
   *
   * @param font
   */
  final public void setTargetEditorFont(final Font font) {
    _fntTargetEditor = font;

    if (_fntTargetEditor != null) {
      //  Write to user preferences goes here
      //  To be done -RM
    } //  Default font (e.g. At startup from prefs file or for reset)    
    else {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  Or supply a fallback default
      final String strFontName = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize = 11;

      //  Da font
      _fntTargetEditor
              = new Font(strFontName, getFontStyle(strFontStyle), iFontSize);
    }

    //  Set it in the target editor pane
    editRightSegment.setEditorFont(_fntTargetEditor);
  }

  /**
   * Translation editor font accessor
   *
   * @return Font
   */
  final public Font getTargetEditorFont() {
    return (_fntTargetEditor);
  }

  /**
   * Font family names accessor
   *
   * @return String[]
   */
  final public String[] getFontFamilyNames() {
    GraphicsEnvironment graphics
            = GraphicsEnvironment.getLocalGraphicsEnvironment();

    return (graphics.getAvailableFontFamilyNames());
  }

  /**
   * Font style string accessor
   *
   * @param font
   * @return String
   */
  final public String getFontStyleString(final Font font) {
    final String strFontStyle;// = "";

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

    return (strFontStyle);
  }

  /**
   * Font style accessor
   *
   * @param strFontStyle
   * @return int
   */
  final public int getFontStyle(final String strFontStyle) {
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

    return (iFontStyle);
  }

  /**
   * Display the fonts dialog to allow selection of fonts for
   * origianl/translation tables/editors and main window
   *
   * @param void
   * @return void
   */
  private void displayFontSelector() {
    FontSelector dlgFonts = new FontSelector(this, getFonts());
    dlgFonts.setVisible(true);
  }

  //  WindowListener Overrides
  @Override
  final public void windowActivated(final WindowEvent evt) {
  }

  @Override
  final public void windowClosed(final WindowEvent evt) {
  }

  @Override
  final public void windowClosing(final WindowEvent evt) {
    if (evt.getSource() == this) {
      quit();
    }
  }

  @Override
  final public void windowDeactivated(final WindowEvent evt) {
  }

  @Override
  final public void windowDeiconified(final WindowEvent evt) {
  }

  @Override
  final public void windowIconified(final WindowEvent evt) {
  }

  @Override
  final public void windowOpened(final WindowEvent evt) {
  }

  /**
   * class action listener implementation
   *
   * @param action
   */
  @Override
  final public void actionPerformed(final ActionEvent action) {
    final Object actor = action.getSource();

    if (actor instanceof JMenuItem) {
      if (actor == menuItemFileOpen) {
        onOpen();
      } else if (actor == menuItemFileSave) {
        saveBitext();
      } else if (actor == menuItemFileSaveAs) {
        saveBitext();
      } else if (actor == menuItemFileClose) {
        onClose();
      } else if (actor == menuItemFileQuit) {
        quit();
      } else if (actor == menuItemSettingsFonts) {
        displayFontSelector();
      } //  Only Linux, Solaris (UNIX?) with Gtk 2.2+
      else if (actor == menuItemLafGtk) {
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } else if (actor == menuItemLafLiquid) {
        try {
          UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
          com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(false);
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } //  All platforms
      else if (actor == menuLafMetal) {
        try {
          UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } //  Java 1.6 update 10+
      else if (actor == menuItemLafNimbus) {
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } else if (actor == menuItemLafSystem) {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI(this);
        } catch (final Exception e) {
          System.out.println(getString("OTP.LNF.INIT.ERROR"));
        }
      } else if (actor == menuItemHelpManual) {
        displayManual();
      } else if (actor == menuItemHelpAbout) {
        displayAbout();
      }
    }
  }

  /**
   *
   * Extracts from the TMX those lines having information which is useful for
   * alignment, and puts them in the corresponding ArrayList's The left part in
   * _alstOriginal corresponds to source text lines and the right part in
   * _alstTranslation corresponds to the target text lines. Initialize the table
   * with one line for each left and right line
   *
   * Esta funci�n extrae del Tmx las l�neas que contienen informaci�n util para
   * el alineamiento, y las mete en los ArrayList correspondientes. Parte izq.
   * en _alstOriginal corresponde a las l�neas del texto fuente, y parte dcha.
   * en _alstTranslation corresponde a las l�neas del texto meta. Inicializa la
   * tabla con una fila por cada l�nea izq y dcha.
   */
  private void initializeAlignmentsView() {
    TableColumn col;

    col = viewAlignments.getColumnModel().getColumn(1);
    col.setHeaderValue(getString("TBL.HDR.COL.SOURCE") + filePathOriginal.getName());

    col = viewAlignments.getColumnModel().getColumn(2);
    col.setHeaderValue(getString("TBL.HDR.COL.TARGET") + filePathTranslation.getName());

    viewAlignments.setColumnHeaderView();

    updateAlignmentsView();
    topArrays = documentOriginal.size() - 1;
    identLabel = 0;
  }

}// Bitext2tmxWindow{}
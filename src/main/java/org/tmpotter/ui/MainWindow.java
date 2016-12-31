/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
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

import static org.openide.awt.Mnemonics.setLocalizedText;
import static org.tmpotter.util.Localization.getString;
import static org.tmpotter.util.StringUtil.formatText;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.MultiSplitLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.core.ProjectProperties;
import org.tmpotter.core.TmData;
import org.tmpotter.filters.FilterManager;
import org.tmpotter.preferences.Preferences;
import org.tmpotter.util.AppConstants;
import org.tmpotter.util.Platform;
import org.tmpotter.util.gui.AquaAdapter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;


/**
 * Main Frame for Main window.
 *
 * @author Hiroshi Miura
 */
public class MainWindow extends javax.swing.JFrame implements ModelMediator, ActionListener, WindowListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);
    private final ActionHandler actionHandler;

    private final JToolBar toolBar = new JToolBar();

    final AlignToolBar alignToolBar;
    final EditToolBar editToolBar;
    final SegmentEditor editLeftSegment = new SegmentEditor();
    final SegmentEditor editRightSegment = new SegmentEditor();
    final TmView tmView;

    private final TmData tmData = new TmData();
    private final ProjectProperties prop = new ProjectProperties();
    private final FilterManager filterManager;

    private final JXMultiSplitPane msp;

    //  Statusbar
    private final JXStatusBar panelStatusBar;
    private final JXLabel labelStatusBar;
    private final JXLabel tableRows;


    /**
     * Creates new form MainFrame
     */
    public MainWindow() {
        initComponents();
        setActionCommands();
        filterManager = new FilterManager();
        actionHandler = new ActionHandler(this, tmData, filterManager);
        actionHandler.setModelMediator(this);
        tmView = new TmView(actionHandler);
        alignToolBar = new AlignToolBar(actionHandler);
        editToolBar = new EditToolBar(actionHandler);
        editLeftSegment.setModelMediator(this);
        editRightSegment.setModelMediator(this);
        labelStatusBar = new JXLabel(" ");
        panelStatusBar = new JXStatusBar();
        msp = new JXMultiSplitPane();
        tableRows = new JXLabel(" ");
        makeUi();
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(msp);
        getContentPane().add(panelStatusBar, BorderLayout.SOUTH);
        setTitle(AppConstants.getDisplayNameAndVersion());

        if (Platform.isMacOsx()) {
            setMacProxy();
        }
        setCloseHandler();
        setMainFrameSize();
    }

    private void makeUi() {
        // Make tool bar
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.PAGE_AXIS));
        toolBar.add(editToolBar);
        toolBar.add(alignToolBar);
        // Make Status Bar
        panelStatusBar.setLayout(new BoxLayout(panelStatusBar, BoxLayout.LINE_AXIS));
        panelStatusBar.add(Box.createRigidArea(new Dimension(10, 0)));
        panelStatusBar.add(labelStatusBar, BorderLayout.SOUTH);
        // Create a Multi Split Pane model
        LinkedList<MultiSplitLayout.Node> editChildren = new LinkedList<>();
        MultiSplitLayout.Leaf leaf1 = new MultiSplitLayout.Leaf("leftEdit");
        leaf1.setWeight(0.5f);
        MultiSplitLayout.Leaf leaf2 = new MultiSplitLayout.Leaf("rightEdit");
        leaf2.setWeight(0.5f);
        editChildren.add(leaf1);
        editChildren.add(new MultiSplitLayout.Divider());
        editChildren.add(leaf2);
        MultiSplitLayout.Split edit = new MultiSplitLayout.Split();
        edit.setRowLayout(true);
        edit.setChildren(editChildren);
        LinkedList<MultiSplitLayout.Node> rootChildren = new LinkedList<>();
        MultiSplitLayout.Leaf leaf3 = new MultiSplitLayout.Leaf("view");
        leaf3.setWeight(0.5f);
        rootChildren.add(edit);
        rootChildren.add(new MultiSplitLayout.Divider());
        rootChildren.add(leaf3);
        MultiSplitLayout.Split root = new MultiSplitLayout.Split();
        root.setRowLayout(false);
        root.setChildren(rootChildren);
        msp.getMultiSplitLayout().setModel(root);
        msp.getMultiSplitLayout().layoutByWeight(msp);
        // Arrange views
        msp.add(editLeftSegment, "leftEdit");
        msp.add(editRightSegment, "rightEdit");
        msp.add(tmView, "view");
    }

    /**
     * Set 'actionCommand' for all menu items.
     */
    private void setActionCommands() {
        try {
            for (Field f : this.getClass().getDeclaredFields()) {
                if (JMenuItem.class.isAssignableFrom(f.getType())) {
                    JMenuItem menuItem = (JMenuItem) f.get(this);
                    menuItem.setActionCommand(f.getName());
                    menuItem.addActionListener(this);
                }
            }
        } catch (IllegalAccessException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Updates status labels.
     */
    public void updateStatusBar() {
        tableRows.setText("" + tmView.getRowCount());
    }

	/**
     * Action handler for events.
     * @param evt event.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        // Get item name from actionCommand.
        String action = evt.getActionCommand();

        // Find method by item name.
        String methodName = action + "ActionPerformed";
        Method method;
        try {
            method = actionHandler.getClass().getMethod(methodName);
        } catch (NoSuchMethodException ignore) {
            try {
                method = actionHandler.getClass()
                    .getMethod(methodName, Integer.TYPE);
            } catch (NoSuchMethodException ex) {
                throw new IncompatibleClassChangeError(
                    "Error invoke method handler for main menu: there is no method "
                        + methodName);
            }
        }

        /* Call MenuItemActionPerformed method. */
        Object[] args = null;
        if (method.getParameterTypes().length != 0) {
            args = new Object[]{evt.getModifiers()};
        }
        try {
            method.invoke(actionHandler, args);
        } catch (IllegalAccessException ex) {
            throw new IncompatibleClassChangeError(
                "Error invoke method handler for main menu");
        } catch (InvocationTargetException ex) {
            LOGGER.info("Error execute method", ex);
            throw new IncompatibleClassChangeError(
                "Error invoke method handler for main menu");
        }
    }

    /**
     * Return project properties.
     * @return project properties.
     */
    @Override
    public final ProjectProperties getProjectProperties() {
        return prop;
    }

    /**
     * Clear project properties.
     */
    @Override
    public void clearProjectProperties() {
        prop.clear();
    }

    /**
     * Enables align tool bar.
     * @param val true if enable, otherwise false.
     */
    @Override
    public final void enableAlignToolBar(final boolean val) {
        alignToolBar.enableButtons(val);
    }

    /**
     * Enable save menu.
     * @param val true if enable, otherwise false.
     */
    @Override
    public final void enableMenuItemFileSave(final boolean val) {
        menuItemFileSave.setEnabled(val);
    }

    private void enableEditMenus(boolean enabled) {
        menuItemRemoveBlankRows.setEnabled(enabled);
        menuItemTuSplit.setEnabled(enabled);
        menuItemOriginalJoin.setEnabled(enabled);
        menuItemOriginalDelete.setEnabled(enabled);
        menuItemOriginalSplit.setEnabled(enabled);
        menuItemTranslationJoin.setEnabled(enabled);
        menuItemTranslationDelete.setEnabled(enabled);
        menuItemTranslationSplit.setEnabled(enabled);
    }

    /**
     * Undo button enables.
     * @param enabled true if enable, otherwise false.
     */
    @Override
    public final void setUndoEnabled(boolean enabled) {
        menuItemUndo.setEnabled(enabled);
    }

    /**
     * Enables edit buttons when open file.
     * @param val true when open file, false when close file.
     */
    public void enableButtonsOnOpenFile(boolean val) {
        alignToolBar.enableButtons(val);
        menuItemFileSave.setEnabled(val);
        menuItemFileSaveAs.setEnabled(val);
        menuItemFileClose.setEnabled(val);
        enableEditMenus(val);
        editToolBar.setUndoEnabled(false);
        editToolBar.setRedoEnabled(false);
        menuItemUndo.setEnabled(false);
        menuItemRedo.setEnabled(false);
    }

    private void setMacProxy() {
        //  Proxy callbacks from/to Mac OS X Aqua global menubar for Quit and About
        try {
            AquaAdapter.connect(actionHandler, "displayAbout", AquaAdapter.AquaEvent.ABOUT);
            AquaAdapter.connect(actionHandler, "quit", AquaAdapter.AquaEvent.QUIT);
        } catch (final NoClassDefFoundError ex) {
            System.out.println(ex);
        }
    }

    private void setCloseHandler() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public final void windowClosing(final WindowEvent event) {
                actionHandler.quit();
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

    public void setFilePathProject(File filePath) {
        prop.setFilePathProject(filePath);
    }

    public void setProjectName(final String name) {
        prop.setProjectName(name);
    }

    public String getProjectName() {
        return prop.getProjectName();
    }

    public void setSourceLanguage(String lang) {
        prop.setSourceLanguage(lang);
    }

    public void setTargetLanguage(String lang) {
        prop.setTargetLanguage(lang);
    }

    /**
     * Initialize alignment view.
     */
    public void initializeTmView() {
        TableColumn col;
        col = tmView.getColumnModel().getColumn(1);
        col.setHeaderValue(getString("TBL.HDR.COL.SOURCE"));
        col = tmView.getColumnModel().getColumn(2);
        col.setHeaderValue(getString("TBL.HDR.COL.TARGET"));
        tmView.setColumnHeaderView();
        updateTmView();
        tmData.setTopArrays(tmData.getDocumentOriginalSize() - 1);
        tmData.setIndexCurrent(0);
    }

    /**
     * Update the row in table with mods.
     * <p>
     * <p>
     * This function updates the rows in the table with the modifications performed, adds rows or
     * removes them.
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
            tmView.setRowSelectionInterval(tmData.getTopArrays(), tmData.getTopArrays());
        }
        tmView.repaint(100);
        tmView.updateUI();
        editLeftSegment.setText(formatText(tmView.getValueAt(tmData.getIndexCurrent(),
                1).toString()));
        editRightSegment.setText(formatText(tmView.getValueAt(tmData.getIndexCurrent(),
                2).toString()));
    }

    public final void setLeftEdit(String edit) {
        editLeftSegment.setText(edit);
    }

    public final void setRightEdit(String edit) {
        editRightSegment.setText(edit);
    }

    public String getLeftEdit() {
        return editLeftSegment.getText();
    }

    public String getRightEdit() {
        return editRightSegment.getText();
    }

    public String getLeftSegment(int index) {
        return tmView.getValueAt(index, 1)
            .toString();
    }

    public String getRightSegment(int index) {
        return tmView.getValueAt(index, 2)
            .toString();
    }

    public int getTmViewRows() {
        return tmView.getRowCount();
    }

    public int getTmViewSelectedRow() {
        return tmView.getSelectedRow();
    }

    public int getTmViewSelectedColumn() {
        return tmView.getSelectedColumn();
    }

    public void setJoinEnabled(boolean val) {
        alignToolBar.setTranslationJoinEnabled(val);
        alignToolBar.setOriginalJoinEnabled(val);
    }

    /**
     * Set position.
     *
     * @param position indicate where in int
     */
    public final void setTextAreaPosition(int position) {
        tmData.setPositionTextArea(position);
    }

    //  WindowListener Overrides
    public final void windowActivated(final WindowEvent evt) {
    }

    public final void windowClosed(final WindowEvent evt) {
    }

    public final void windowClosing(final WindowEvent evt) {
        if (evt.getSource() == this) {
            actionHandler.menuItemFileQuitActionPerformed();
        }
    }

    public final void windowDeactivated(final WindowEvent evt) {
    }

    public final void windowDeiconified(final WindowEvent evt) {
    }

    public final void windowIconified(final WindowEvent evt) {
    }

    public final void windowOpened(final WindowEvent evt) {
    }

    public final void tmDataClear() {
        tmData.clear();
    }

    public final void tmViewClear() {
        tmView.clear();
    }

    public final void editSegmentClear() {
        editLeftSegment.setText("");
        editRightSegment.setText("");
    }

    public final void buildDisplay() {
        tmView.buildDisplay();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemFileImport = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuItemFileOpen = new javax.swing.JMenuItem();
        menuItemFileSave = new javax.swing.JMenuItem();
        menuItemFileSaveAs = new javax.swing.JMenuItem();
        menuItemFileExport = new javax.swing.JMenuItem();
        menuItemFileClose = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuItemFileQuit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemUndo = new javax.swing.JMenuItem();
        menuItemRedo = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemCut = new javax.swing.JMenuItem(new DefaultEditorKit.CutAction());
        menuItemCopy = new javax.swing.JMenuItem(new DefaultEditorKit.CopyAction());
        menuItemPaste = new javax.swing.JMenuItem(new DefaultEditorKit.PasteAction());
        menuTu = new javax.swing.JMenu();
        menuItemTuSplit = new javax.swing.JMenuItem();
        menuItemRemoveBlankRows = new javax.swing.JMenuItem();
        sourceMenu = new javax.swing.JMenu();
        menuItemOriginalDelete = new javax.swing.JMenuItem();
        menuItemOriginalJoin = new javax.swing.JMenuItem();
        menuItemOriginalSplit = new javax.swing.JMenuItem();
        menuTranslation = new javax.swing.JMenu();
        menuItemTranslationDelete = new javax.swing.JMenuItem();
        menuItemTranslationJoin = new javax.swing.JMenuItem();
        menuItemTranslationSplit = new javax.swing.JMenuItem();
        menuOptions = new javax.swing.JMenu();
        menuItemSettings = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemHelpAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 480));

        menuFile.setText("File");
        setLocalizedText(menuFile, getString("MNU.FILE"));

        menuItemFileImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/filenew.png"))); // NOI18N
        menuItemFileImport.setText("New Import...");
        menuFile.add(menuItemFileImport);
        menuFile.add(jSeparator4);

        menuItemFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/fileopen.png"))); // NOI18N
        menuItemFileOpen.setText("Open");
        setLocalizedText(menuItemFileOpen, getString("MNI.FILE.OPEN"));
        menuFile.add(menuItemFileOpen);

        menuItemFileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/filesave.png"))); // NOI18N
        menuItemFileSave.setText("Save");
        setLocalizedText(menuItemFileSave, getString("MNI.FILE.SAVE"));
        menuFile.add(menuItemFileSave);

        menuItemFileSaveAs.setText("Save As");
        setLocalizedText(menuItemFileSaveAs, getString("MNI.FILE.SAVEAS"));
        menuFile.add(menuItemFileSaveAs);

        menuItemFileExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileExport.setText("Export TMX");
        menuFile.add(menuItemFileExport);

        menuItemFileClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/fileclose.png"))); // NOI18N
        menuItemFileClose.setText("Close");
        setLocalizedText(menuItemFileClose, getString("MNI.FILE.ABORT"));
        menuFile.add(menuItemFileClose);
        menuFile.add(jSeparator5);

        menuItemFileQuit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileQuit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/application-exit.png"))); // NOI18N
        menuItemFileQuit.setText("Quit");
        setLocalizedText(menuItemFileQuit, getString("MNI.FILE.EXIT"));
        menuFile.add(menuItemFileQuit);

        jMenuBar1.add(menuFile);

        menuEdit.setText("Edit");
        setLocalizedText(menuEdit, getString("MNU.EDIT"));

        menuItemUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        menuItemUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/undo.png"))); // NOI18N
        menuItemUndo.setText("Undo");
        menuEdit.add(menuItemUndo);

        menuItemRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        menuItemRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/redo.png"))); // NOI18N
        menuItemRedo.setText("Redo");
        menuEdit.add(menuItemRedo);
        menuEdit.add(jSeparator1);

        menuItemCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        menuItemCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/cut.png"))); // NOI18N
        menuItemCut.setText("Cut");
        menuEdit.add(menuItemCut);

        menuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        menuItemCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/editcopy.png"))); // NOI18N
        menuItemCopy.setText("Copy");
        menuEdit.add(menuItemCopy);

        menuItemPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        menuItemPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/editpaste.png"))); // NOI18N
        menuItemPaste.setText("Paste");
        menuEdit.add(menuItemPaste);

        jMenuBar1.add(menuEdit);

        menuTu.setText("TU");

        menuItemTuSplit.setText("Split Trans Unit");
        menuTu.add(menuItemTuSplit);

        menuItemRemoveBlankRows.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/tmpotter/ui/resources/eraser.png"))); // NOI18N
        menuItemRemoveBlankRows.setText("Clean blanck rows");
        menuTu.add(menuItemRemoveBlankRows);

        jMenuBar1.add(menuTu);

        sourceMenu.setText("Original");

        menuItemOriginalDelete.setText("Delete Original");
        sourceMenu.add(menuItemOriginalDelete);

        menuItemOriginalJoin.setText("Join Original");
        sourceMenu.add(menuItemOriginalJoin);

        menuItemOriginalSplit.setText("Split Original");
        sourceMenu.add(menuItemOriginalSplit);

        jMenuBar1.add(sourceMenu);

        menuTranslation.setText("Translation");

        menuItemTranslationDelete.setText("Delete Translation");
        menuTranslation.add(menuItemTranslationDelete);

        menuItemTranslationJoin.setText("Join Translation");
        menuTranslation.add(menuItemTranslationJoin);

        menuItemTranslationSplit.setText("Split Translation");
        menuTranslation.add(menuItemTranslationSplit);

        jMenuBar1.add(menuTranslation);

        menuOptions.setText("Options");
        setLocalizedText(menuOptions, getString("MNU.OPTIONS"));

        menuItemSettings.setText("Settings");
        setLocalizedText(menuItemSettings, getString("MNI.SETTINGS"));
        menuOptions.add(menuItemSettings);

        jMenuBar1.add(menuOptions);

        menuHelp.setText("Help");
        setLocalizedText(menuHelp, getString("MNU.HELP"));

        menuItemHelpAbout.setText("About");
        setLocalizedText(menuItemHelpAbout, getString("MNI.HELP.ABOUT"));
        menuItemHelpAbout.setActionCommand("");
        menuItemHelpAbout.setName(""); // NOI18N
        menuHelp.add(menuItemHelpAbout);

        jMenuBar1.add(menuHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

    /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemCopy;
    private javax.swing.JMenuItem menuItemCut;
    private javax.swing.JMenuItem menuItemFileClose;
    private javax.swing.JMenuItem menuItemFileExport;
    private javax.swing.JMenuItem menuItemFileImport;
    private javax.swing.JMenuItem menuItemFileOpen;
    private javax.swing.JMenuItem menuItemFileQuit;
    private javax.swing.JMenuItem menuItemFileSave;
    private javax.swing.JMenuItem menuItemFileSaveAs;
    private javax.swing.JMenuItem menuItemHelpAbout;
    private javax.swing.JMenuItem menuItemOriginalDelete;
    private javax.swing.JMenuItem menuItemOriginalJoin;
    private javax.swing.JMenuItem menuItemOriginalSplit;
    private javax.swing.JMenuItem menuItemPaste;
    private javax.swing.JMenuItem menuItemRedo;
    private javax.swing.JMenuItem menuItemRemoveBlankRows;
    private javax.swing.JMenuItem menuItemSettings;
    private javax.swing.JMenuItem menuItemTranslationDelete;
    private javax.swing.JMenuItem menuItemTranslationJoin;
    private javax.swing.JMenuItem menuItemTranslationSplit;
    private javax.swing.JMenuItem menuItemTuSplit;
    private javax.swing.JMenuItem menuItemUndo;
    private javax.swing.JMenu menuOptions;
    private javax.swing.JMenu menuTranslation;
    private javax.swing.JMenu menuTu;
    private javax.swing.JMenu sourceMenu;
    // End of variables declaration//GEN-END:variables
}

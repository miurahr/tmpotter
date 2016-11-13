/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import static org.openide.awt.Mnemonics.setLocalizedText;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.MultiSplitLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.core.Document;
import org.tmpotter.core.ProjectProperties;
import org.tmpotter.core.SegmentChanges;
import org.tmpotter.core.TmxReader;
import org.tmpotter.filters.FilterManager;
import org.tmpotter.util.AppConstants;
import static org.tmpotter.util.Localization.getString;
import org.tmpotter.util.Platform;
import static org.tmpotter.util.StringUtil.formatText;
import static org.tmpotter.util.StringUtil.restoreText;
import org.tmpotter.util.Utilities;
import org.tmpotter.util.gui.AquaAdapter;

/**
 * Main Frame for Main window.
 *
 * @author Hiroshi Miura
 */
public class MainWindow extends JFrame implements ModelMediator, WindowListener {

	private static final Logger LOGGER = LoggerFactory
		.getLogger(MainWindow.class);
	protected MenuHandler menuHandler;

	protected final ToolBar toolBar = new ToolBar();
	protected final SegmentEditor editLeftSegment = new SegmentEditor();
	protected final SegmentEditor editRightSegment = new SegmentEditor();
	protected final TmView tmView = new TmView();

	protected TmData tmData = new TmData();
	protected ProjectProperties prop = new ProjectProperties();
	protected FilterManager filterManager = new FilterManager();

	private JXMultiSplitPane msp;
	//  Statusbar
	protected JXStatusBar panelStatusBar;
	protected JXLabel labelStatusBar;
	private JXLabel tableRows;

	/**
	 * Creates new form MainFrame
	 */
	public MainWindow() {
		initComponents();
		setActionCommands();
		tmView.setModelMediator(this);
		toolBar.setModelMediator(this);
		editLeftSegment.setModelMediator(this);
		editRightSegment.setModelMediator(this);
		menuHandler = new MenuHandler(this, tmData);
		menuHandler.setModelMediator(this);
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

	protected void makeUi() {
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
	protected void setActionCommands() {
		try {
			for (Field f : this.getClass().getDeclaredFields()) {
				if (JMenuItem.class.isAssignableFrom(f.getType())) {
					JMenuItem menuItem = (JMenuItem) f.get(this);
					menuItem.setActionCommand(f.getName());
				}
			}
		} catch (IllegalAccessException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Updates status labels.
	 */
	protected void updateStatusBar() {
		tableRows.setText("" + tmView.getRowCount());
	}

	public final void enableEditMenus(boolean enabled) {
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
	 * Code for dispatching events from components to event handlers.
	 *
	 * @param evt event info
	 */
	public void menuSelected(MenuEvent evt) {
		// Item what perform event.
		JMenu menu = (JMenu) evt.getSource();

		// Get item name from actionCommand.
		String action = menu.getActionCommand();

		// Find method by item name.
		String methodName = action + "MenuSelected";
		Method method = null;
		try {
			method = this.getClass().getMethod(methodName, JMenu.class);
		} catch (NoSuchMethodException ex) {
			// method not declared
			return;
		}

		// Call ...MenuMenuSelected method.
		try {
			method.invoke(this, menu);
		} catch (IllegalAccessException ex) {
			throw new IncompatibleClassChangeError(
				"Error invoke method handler for main menu");
		} catch (InvocationTargetException ex) {
			LOGGER.info("Error execute method", ex);
			throw new IncompatibleClassChangeError(
				"Error invoke method handler for main menu");
		}
	}

	public void menuCanceled(MenuEvent evt) {
	}

	public void menuDeselected(MenuEvent evt) {
	}

	public final void setUndoEnabled(boolean enabled) {
		menuItemUndo.setEnabled(enabled);
	}

	public void actionPerformed(ActionEvent evt) {
		// Get item name from actionCommand.
		String action = evt.getActionCommand();

		//LOGGER.logRB("LOG_MENU_CLICK", action);
		// Find method by item name.
		String methodName = action + "ActionPerformed";
		Method method = null;
		try {
			method = menuHandler.getClass().getMethod(methodName);
		} catch (NoSuchMethodException ignore) {
			try {
				method = menuHandler.getClass()
					.getMethod(methodName, Integer.TYPE);
			} catch (NoSuchMethodException ex) {
				throw new IncompatibleClassChangeError(
					"Error invoke method handler for main menu: there is no method "
					+ methodName);
			}
		}
		// Call ...MenuItemActionPerformed method.
		Object[] args = method.getParameterTypes().length == 0 ? null : new Object[]{evt.getModifiers()};
		try {
			method.invoke(menuHandler, args);
		} catch (IllegalAccessException ex) {
			throw new IncompatibleClassChangeError(
				"Error invoke method handler for main menu");
		} catch (InvocationTargetException ex) {
			LOGGER.info("Error execute method", ex);
			throw new IncompatibleClassChangeError(
				"Error invoke method handler for main menu");
		}
	}

	public final JMenu getMenuFile() {
		return menuFile;
	}

	public final JMenu getMenuEdit() {
		return menuEdit;
	}

	public final JMenu getMenuOptions() {
		return menuOptions;
	}

	public final JMenu getMenuHelp() {
		return menuHelp;
	}

	public final void enableMenuItemFileSave(final boolean val) {
		menuItemFileSave.setEnabled(val);
	}

	public final void enableMenuItemFileSaveAs(final boolean val) {
		menuItemFileSaveAs.setEnabled(val);
	}

	public final void enableMenuItemFileClose(final boolean val) {
		menuItemFileClose.setEnabled(val);
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

	@Override
	public void onOpenFile(File filePathOriginal,
		String stringLangOriginal, String stringLangTranslation) {
		prop.setFilePathOriginal(filePathOriginal);
		prop.setFilePathTranslation(prop.getFilePathOriginal());
		prop.setSourceLanguage(stringLangOriginal);
		prop.setTargetLanguage(stringLangTranslation);
		tmView.buildDisplay();
		try {
			TmxReader reader = new TmxReader(prop);
			tmData.documentOriginal
				= reader.getOriginalDocument(tmData.documentOriginal);
			tmData.documentTranslation
				= reader.getTranslationDocument(tmData.documentTranslation);
		} catch (Exception ex) {
			LOGGER.info(ex.getMessage());
		}
		initializeTmView();
		updateTmView();
		toolBar.enableButtons(true);
		enableEditMenus(true);
		toolBar.setUndoEnabled(false);
		menuItemFileSave.setEnabled(true);
		menuItemFileSaveAs.setEnabled(true);
		menuItemFileClose.setEnabled(true);
	}

	@Override
	public void setOriginalProperties(File filePath, String text, String lang, String encoding) {
		prop.setOriginalEncoding(encoding);
		prop.setFilePathOriginal(filePath);
		tmData.stringOriginal = text;
		tmData.stringLangOriginal = lang;
	}

	@Override
	public void setTargetProperties(File filePath, String text, String lang, String encoding) {
		prop.setTranslationEncoding(encoding);
		prop.setFilePathTranslation(filePath);
		tmData.stringTranslation = text;
		tmData.stringLangTranslation = lang;
	}

	@Override
	public void onImportFile(String filterName) {
		tmData.documentOriginal = new Document();
		tmData.documentTranslation = new Document();
		filterManager.loadFile(prop,
			tmData.documentOriginal, tmData.documentTranslation, filterName);
		tmData.matchArrays();
	}

	/**
	 * Initialize alignment view.
	 *
	 * <p>
	 * Extracts from the TMX those lines having information which is useful
	 * for alignment, and puts them in the corresponding ArrayList's The
	 * left part in _alstOriginal corresponds to source text lines and the
	 * right part in _alstTranslation corresponds to the target text lines.
	 * Initialize the table with one line for each left and right line
	 *
	 */
	protected void initializeTmView() {
		TableColumn col;
		col = tmView.getColumnModel().getColumn(1);
		col.setHeaderValue(getString("TBL.HDR.COL.SOURCE")
			+ prop.getFilePathOriginal().getName());
		col = tmView.getColumnModel().getColumn(2);
		col.setHeaderValue(getString("TBL.HDR.COL.TARGET")
			+ prop.getFilePathTranslation().getName());
		tmView.setColumnHeaderView();
		updateTmView();
		tmData.topArrays = tmData.documentOriginal.size() - 1;
		tmData.indexCurrent = 0;
	}

	/**
	 * Update the row in table with mods.
	 *
	 * <p>
	 * This function updates the rows in the table with the modifications
	 * performed, adds rows or removes them.
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
		tmData.setBothIndex(tmView.getSelectedRow());
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
		setUndoEnabled(true);
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
		setUndoEnabled(true);
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
		setUndoEnabled(true);
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
		setUndoEnabled(true);
	}

	/**
	 * delete on translation document.
	 */
	@Override
	public final void onTranslationDelete() {
		tmData.incrementChanges();
		tmData.delete(TmData.Side.TRANSLATION);
		updateTmView();
		toolBar.setUndoEnabled(true);
		setUndoEnabled(true);
	}

	/**
	 * split on translation document.
	 */
	@Override
	public final void onTranslationSplit() {
		tmData.incrementChanges();
		tmData.split(TmData.Side.TRANSLATION);
		updateTmView();
		toolBar.setUndoEnabled(true);
		setUndoEnabled(true);
	}

	//  Accessed by ControlView
	@Override
	public void onUndo() {
		tmData.arrayListChanges.remove(tmData.getIdentChanges());
		int currentChange = tmData.decrementChanges();

		if (currentChange == -1) {
			toolBar.setUndoEnabled(false);
			setUndoEnabled(false);
		}
	}

	/**
	 * Set position.
	 *
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
			menuItemUndo.setEnabled(true);
			updateTmView();
		}
	}

	/**
	 * Split on TU.
	 */
	@Override
	public final void onTuSplit() {
		tmData.tuSplit((tmView.getSelectedColumn() == 1) ? TmData.Side.ORIGINAL
			: TmData.Side.TRANSLATION);
		updateTmView();
		toolBar.buttonUndo.setEnabled(true);
		menuItemUndo.setEnabled(true);
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
				tmData.undoJoin();
				break;
			case DELETE:
				tmData.undoDelete();
				updateTmView();
				break;
			case SPLIT:
				tmData.undoSplit();
				break;
			case REMOVE:
				tmData.undoRemove();
				break;
			case TUSPLIT:
				tmData.undoTuSplit(ultChanges.getSource());
				break;
			default:
				break;
		}
		updateTmView();
	}

	/**
	 * This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jMenuBar1 = new javax.swing.JMenuBar();
                menuFile = new javax.swing.JMenu();
                menuItemFileOpen = new javax.swing.JMenuItem();
                menuItemFileTextOpen = new javax.swing.JMenuItem();
                menuItemFileSave = new javax.swing.JMenuItem();
                menuItemFileSaveAs = new javax.swing.JMenuItem();
                menuItemFileClose = new javax.swing.JMenuItem();
                jSeparator4 = new javax.swing.JPopupMenu.Separator();
                menuItemFileImport = new javax.swing.JMenuItem();
                jSeparator5 = new javax.swing.JPopupMenu.Separator();
                menuItemFileQuit = new javax.swing.JMenuItem();
                menuEdit = new javax.swing.JMenu();
                menuItemUndo = new javax.swing.JMenuItem();
                menuItemRedo = new javax.swing.JMenuItem();
                jSeparator1 = new javax.swing.JPopupMenu.Separator();
                menuItemOriginalDelete = new javax.swing.JMenuItem();
                menuItemOriginalJoin = new javax.swing.JMenuItem();
                menuItemOriginalSplit = new javax.swing.JMenuItem();
                jSeparator2 = new javax.swing.JPopupMenu.Separator();
                menuItemTranslationDelete = new javax.swing.JMenuItem();
                menuItemTranslationJoin = new javax.swing.JMenuItem();
                menuItemTranslationSplit = new javax.swing.JMenuItem();
                jSeparator3 = new javax.swing.JPopupMenu.Separator();
                menuItemTuSplit = new javax.swing.JMenuItem();
                menuItemRemoveBlankRows = new javax.swing.JMenuItem();
                menuOptions = new javax.swing.JMenu();
                menuItemSettings = new javax.swing.JMenuItem();
                menuHelp = new javax.swing.JMenu();
                menuItemAbout = new javax.swing.JMenuItem();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setMinimumSize(new java.awt.Dimension(640, 480));

                menuFile.setText("File");

                menuItemFileOpen.setText("Open");
                setLocalizedText(menuItemFileOpen, getString("MNI.FILE.OPEN"));
                menuFile.add(menuItemFileOpen);

                menuItemFileTextOpen.setText("Open Text");
                menuFile.add(menuItemFileTextOpen);

                menuItemFileSave.setText("Save");
                menuFile.add(menuItemFileSave);

                menuItemFileSaveAs.setText("Save As");
                menuFile.add(menuItemFileSaveAs);

                menuItemFileClose.setText("Close");
                menuFile.add(menuItemFileClose);
                menuFile.add(jSeparator4);

                menuItemFileImport.setText("Import");
                menuFile.add(menuItemFileImport);
                menuFile.add(jSeparator5);

                menuItemFileQuit.setText("Quit");
                menuFile.add(menuItemFileQuit);

                jMenuBar1.add(menuFile);

                menuEdit.setText("Edit");

                menuItemUndo.setText("Undo");
                menuEdit.add(menuItemUndo);

                menuItemRedo.setText("Redo");
                menuEdit.add(menuItemRedo);
                menuEdit.add(jSeparator1);

                menuItemOriginalDelete.setText("Delete Original");
                menuEdit.add(menuItemOriginalDelete);

                menuItemOriginalJoin.setText("Join Original");
                menuEdit.add(menuItemOriginalJoin);

                menuItemOriginalSplit.setText("Split Original");
                menuEdit.add(menuItemOriginalSplit);
                menuEdit.add(jSeparator2);

                menuItemTranslationDelete.setText("Delete Translation");
                menuEdit.add(menuItemTranslationDelete);

                menuItemTranslationJoin.setText("Join Translation");
                menuEdit.add(menuItemTranslationJoin);

                menuItemTranslationSplit.setText("Split Translation");
                menuEdit.add(menuItemTranslationSplit);
                menuEdit.add(jSeparator3);

                menuItemTuSplit.setText("Split Trans Unit");
                menuEdit.add(menuItemTuSplit);

                menuItemRemoveBlankRows.setText("Clean blanck rows");
                menuEdit.add(menuItemRemoveBlankRows);

                jMenuBar1.add(menuEdit);

                menuOptions.setText("Options");

                menuItemSettings.setText("Settings");
                menuOptions.add(menuItemSettings);

                jMenuBar1.add(menuOptions);

                menuHelp.setText("Help");

                menuItemAbout.setText("About");
                menuHelp.add(menuItemAbout);

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
        private javax.swing.JPopupMenu.Separator jSeparator2;
        private javax.swing.JPopupMenu.Separator jSeparator3;
        private javax.swing.JPopupMenu.Separator jSeparator4;
        private javax.swing.JPopupMenu.Separator jSeparator5;
        protected javax.swing.JMenu menuEdit;
        protected javax.swing.JMenu menuFile;
        private javax.swing.JMenu menuHelp;
        private javax.swing.JMenuItem menuItemAbout;
        private javax.swing.JMenuItem menuItemFileClose;
        private javax.swing.JMenuItem menuItemFileImport;
        private javax.swing.JMenuItem menuItemFileOpen;
        private javax.swing.JMenuItem menuItemFileQuit;
        private javax.swing.JMenuItem menuItemFileSave;
        private javax.swing.JMenuItem menuItemFileSaveAs;
        private javax.swing.JMenuItem menuItemFileTextOpen;
        private javax.swing.JMenuItem menuItemOriginalDelete;
        private javax.swing.JMenuItem menuItemOriginalJoin;
        private javax.swing.JMenuItem menuItemOriginalSplit;
        private javax.swing.JMenuItem menuItemRedo;
        private javax.swing.JMenuItem menuItemRemoveBlankRows;
        private javax.swing.JMenuItem menuItemSettings;
        private javax.swing.JMenuItem menuItemTranslationDelete;
        private javax.swing.JMenuItem menuItemTranslationJoin;
        private javax.swing.JMenuItem menuItemTranslationSplit;
        private javax.swing.JMenuItem menuItemTuSplit;
        private javax.swing.JMenuItem menuItemUndo;
        protected javax.swing.JMenu menuOptions;
        // End of variables declaration//GEN-END:variables
}

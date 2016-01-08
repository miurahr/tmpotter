/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import org.tmpotter.core.BitextModel;
import org.tmpotter.core.Segment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 *   Alignment Table view for parallel texts.
 *
 */
@SuppressWarnings("serial")
final class TmView extends JXPanel {

  private final MainWindow mainWindow;

  BitextModel          bitextModel;
  JXTable              table;
  private JScrollPane  scrollPane;

  public TmView( final MainWindow parent ) {
    super();
    mainWindow = parent;
    setLayout( new BorderLayout() );
  }

  private void onTableClicked() {
    mainWindow.positionTextArea = 0;
    if (mainWindow.identAnt < mainWindow.documentOriginal.size()) {
      mainWindow.documentOriginal.set(mainWindow.identAnt,
              restoreText(mainWindow.editLeftSegment.getText()));
      mainWindow.documentTranslation.set(mainWindow.identAnt,
              restoreText(mainWindow.editRightSegment.getText()));
    }
    mainWindow.editLeftSegment.setText(formatText(getValueAt(getSelectedRow(),
            1).toString()));
    mainWindow.editRightSegment.setText(formatText(getValueAt(getSelectedRow(),
            2).toString()));
    mainWindow.identLabel = mainWindow.tmView.getSelectedRow();
    mainWindow.identAnt = mainWindow.identLabel;
    if (mainWindow.identLabel == mainWindow.topArrays) {
      mainWindow.toolBar.setTranslationJoinEnabled(false);
      mainWindow.toolBar.setOriginalJoinEnabled(false);
    } else {
      mainWindow.toolBar.setTranslationJoinEnabled(true);
      mainWindow.toolBar.setOriginalJoinEnabled(true);
    }
    updateView();
  }

  private void onTablePressed(final KeyEvent event) {
    int fila;
    if (mainWindow.tmView.getSelectedRow() != -1) {
      fila = mainWindow.tmView.getSelectedRow();
      mainWindow.positionTextArea = 0;
    } else {
      fila = 1;
    }
    if (fila < mainWindow.tmView.getRowCount() - 1) {
      if ((event.getKeyCode() == KeyEvent.VK_DOWN)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
        if (mainWindow.identAnt < mainWindow.documentOriginal.size()) {
          mainWindow.documentOriginal.set(mainWindow.identAnt,
                  restoreText(mainWindow.editLeftSegment.getText()));
          mainWindow.documentTranslation.set(mainWindow.identAnt,
                  restoreText(mainWindow.editRightSegment.getText()));
        }
        mainWindow.editLeftSegment.setText(formatText(getValueAt(fila + 1, 1)
                .toString()));
        mainWindow.editRightSegment.setText(formatText(getValueAt(fila + 1, 2)
                .toString()));
        mainWindow.identLabel = fila + 1;
      } else if ((event.getKeyCode() == KeyEvent.VK_UP)
              || (event.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
        mainWindow.identLabel = fila - 1;
        if (fila == 0) {
          fila = 1;
          mainWindow.identLabel = 0;
        }
        if (mainWindow.identAnt < mainWindow.documentOriginal.size()) {
          mainWindow.documentOriginal.set(mainWindow.identAnt,
                  restoreText(mainWindow.editLeftSegment.getText()));
          mainWindow.documentTranslation.set(mainWindow.identAnt,
                  restoreText(mainWindow.editRightSegment.getText()));
        }
        mainWindow.editLeftSegment.setText(formatText(getValueAt(fila - 1, 1)
                .toString()));
        mainWindow.editRightSegment.setText(formatText(getValueAt(fila - 1, 2)
                .toString()));
      }
      if (mainWindow.identLabel == mainWindow.topArrays) {
        mainWindow.toolBar.setTranslationJoinEnabled(false);
        mainWindow.toolBar.setOriginalJoinEnabled(false);
      } else {
        mainWindow.toolBar.setTranslationJoinEnabled(true);
        mainWindow.toolBar.setOriginalJoinEnabled(true);
      }
      mainWindow.identAnt = mainWindow.identLabel;
    }
    updateView();
  }

  public final void setTableFont( final Font font ) {
    if ( table != null ) {
      int size = font.getSize();

      if ( size <= 10 ) {
        table.setRowHeight( 10 );
      } else {
        table.setRowHeight( size );
      }

      table.setFont( font );
    }
  }

  public final TableColumnModel getColumnModel() {
    return ( table.getColumnModel() );
  }

  public final void setColumnHeaderView() {
    scrollPane.setColumnHeaderView(table.getTableHeader() );
  }

  public final void setRowSelectionInterval( final int row, final int len ) {
    if (row > 0 && len > 0) {
      table.setRowSelectionInterval( row, len );
    }
  }

  public final void setValueAt( final String str, final int row, final int column ) {
    table.setValueAt( str, row, column );
  }

  public final void setPreferredSize( final int width,
          final int height, final int offset ) {
    table.setPreferredSize(new Dimension( width, ( table
        .getRowCount() * height ) + offset ) );
  }

  public final int getRowCount() {
    return ( table.getRowCount() );
  }

  public final void setModelValueAt( final Object obj, int row, int column ) {
    bitextModel.setValueAt( obj, row, column );
  }

  public final void removeSegment( final int row ) {
    bitextModel.removeSegment( row );
    revalidate();
  }

  public final void addModelSegment( final Segment segment ) {
    bitextModel.addSegment( segment );
  }

  public final Object getValueAt( final int row, final int column ) {
    if (row < 0 && column < 0 ) {
      return null;
    }
    return ( table.getValueAt( row, column ) );
  }

  public final int getSelectedRow() {
    return ( table.getSelectedRow() );
  }

  public final int getSelectedColumn() {
    return ( table.getSelectedColumn() );
  }

  public final JTableHeader getTableHeader() {
    return ( table.getTableHeader() );
  }

  final void clear() {
    table.removeAll();
    table.revalidate();
    scrollPane.remove(table);
    scrollPane.revalidate();
    remove(scrollPane);
    revalidate();
    removeAll();
    repaint(100);
  }

  final void buildDisplay() {
    bitextModel = new BitextModel();
    table   = new JXTable( bitextModel );

    TableColumn column;

    //  Segment
    column = table.getColumnModel().getColumn( 0 );
    column.setPreferredWidth( 60 );
    column.setHeaderValue(table.getColumnName( 0 ) );

    //  Original
    column = table.getColumnModel().getColumn( 1 );
    column.setPreferredWidth( 600 );
    column.setHeaderValue(table.getColumnName( 1 ) );
    column.setCellEditor(table.getDefaultEditor(bitextModel.getColumnClass(1)));

    //  Translation
    column = table.getColumnModel().getColumn( 2 );
    column.setPreferredWidth( 600 );
    column.setHeaderValue(table.getColumnName( 2 ) );
    column.setCellEditor(table.getDefaultEditor(bitextModel.getColumnClass(2)));    

    table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    table.setAutoscrolls( true );

    // ToDo: set the start height according to the users font point size
    table.setRowHeight( 15 );
    table.setRowMargin( 2 );
    table.setRowSelectionAllowed( true );
    table.setColumnSelectionAllowed( false );
    table.setAutoCreateColumnsFromModel( false );
    table.setSelectionBackground( new Color( 220, 235, 250 ) );
    table.setShowGrid(true);
    table.setShowHorizontalLines(true);

    table.addKeyListener( new KeyAdapter() {
      @Override
      public final void keyPressed( final KeyEvent event ) {
        onTablePressed( event );
      }
    });

    table.addMouseListener( new MouseAdapter() {
      @Override
      public final void mouseClicked( final MouseEvent event ) {
        onTableClicked();
      }
    });

    scrollPane = new JScrollPane( table );
    scrollPane.setColumnHeader( null );
    scrollPane.setColumnHeaderView(table.getTableHeader() );

    add( scrollPane );
  }

  /**
   * Update the row in table with mods.
   *
   * <p>This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  protected void updateView() {
    if (!mainWindow.documentOriginal.isEmpty()
            && !mainWindow.documentTranslation.isEmpty()) {
      mainWindow.matchArrays();
    }
    for (int cont = 0; cont < mainWindow.tmView.getRowCount(); cont++) {
      setModelValueAt("", cont, 0);
      setModelValueAt("", cont, 1);
      setModelValueAt("", cont, 2);
    }
    if ((getRowCount() > mainWindow.documentOriginal.size())
            && (mainWindow.documentOriginal.size() > 25)) {
      while (getRowCount() != mainWindow.documentOriginal.size()) {
        removeSegment(getRowCount() - 1);
        setPreferredSize(805, 15, -1);
      }
    } else if (getRowCount() < mainWindow.documentOriginal.size()) {
      while (getRowCount() != mainWindow.documentOriginal.size()) {
        Segment nullSegment = new Segment(null, null, null);
        addModelSegment(nullSegment);
        setPreferredSize(805, 15, 1);
      }
    }
    for (int cont = 0; cont < mainWindow.documentOriginal.size(); cont++) {
      setModelValueAt(Integer.toString(cont + 1), cont, 0);
      setModelValueAt(mainWindow.documentOriginal.get(cont), cont, 1);
    }
    for (int cont = 0; cont < mainWindow.documentTranslation.size(); cont++) {
      setModelValueAt(mainWindow.documentTranslation.get(cont), cont, 2);
    }
    if (mainWindow.identLabel == mainWindow.topArrays) {
      setRowSelectionInterval(mainWindow.topArrays, mainWindow.topArrays);
    }
    repaint(100);
    mainWindow.editLeftSegment.setText(formatText(getValueAt(mainWindow.identLabel,
            1).toString()));
    mainWindow.editRightSegment.setText(formatText(getValueAt(mainWindow.identLabel,
            2).toString()));
  }
}

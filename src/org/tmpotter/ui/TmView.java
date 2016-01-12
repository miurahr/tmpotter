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

  private ModelMediator modelMediator;

  BitextModel          bitextModel;
  JXTable              table;
  private JScrollPane  scrollPane;

  public TmView() {
    super();
    setLayout( new BorderLayout() );
  }

  public void setModelMediator(ModelMediator mediator) {
    this.modelMediator = mediator;
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
        modelMediator.onTablePressed( event );
      }
    });

    table.addMouseListener( new MouseAdapter() {
      @Override
      public final void mouseClicked( final MouseEvent event ) {
        modelMediator.onTableClicked();
      }
    });

    scrollPane = new JScrollPane( table );
    scrollPane.setColumnHeader( null );
    scrollPane.setColumnHeaderView(table.getTableHeader() );

    add( scrollPane );
  }

  void clearAllView() {
    for (int i = 0; i < getRowCount(); i++) {
      setModelValueAt("", i, 0);
      setModelValueAt("", i, 1);
      setModelValueAt("", i, 2);
    }
  }

  void adjustOriginalView(int size) {
      if ((getRowCount() > size)
            && (size > 25)) {
      while (getRowCount() != size) {
        removeSegment(getRowCount() - 1);
        setPreferredSize(805, 15, -1);
      }
    } else if (getRowCount() < size) {
      while (getRowCount() != size) {
        addModelSegment(new Segment(null, null, null));
        setPreferredSize(805, 15, 1);
      }
    }
  }
  void setViewData(TmData tmData) {
    for (int cont = 0; cont < tmData.getDocumentOriginalSize(); cont++) {
      setModelValueAt(Integer.toString(cont + 1), cont, 0);
      setModelValueAt(tmData.getDocumentOriginal(cont), cont, 1);
    }
    for (int cont = 0; cont < tmData.getDocumentTranslationSize(); cont++) {
      setModelValueAt(tmData.getDocumentTranslation(cont), cont, 2);
    }
  }
}

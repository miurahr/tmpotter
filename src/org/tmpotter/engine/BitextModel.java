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

package org.tmpotter.engine;

import java.util.LinkedList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


/**
 * Model for bitext.
 *
 */
@SuppressWarnings("serial")
public final class BitextModel extends AbstractTableModel {
  private final LinkedList<Segment> data = new LinkedList<>();
  private final LinkedList<TableModelListener> tableModelListeners =
      new LinkedList<>();

  /**
   * column sizes.
   * 
   * @return column count sizes
   */
  @Override
  public final int getColumnCount() {
    return 3;
  }
  
  /**
   * row size.
   * 
   * @return row count sizes
   */
  @Override
  public final int getRowCount() {
    return data.size();
  }

  /**
   * get value from row,column.
   * 
   * @param row row number
   * @param column column number
   * @return object to represent by (row, column)
   */
  @Override
  public final Object getValueAt(final int row, final int column) {
  
    if ( getRowCount() < 1 ) {
      return null;
    }
    
    final Segment segment = data.get(row);

    switch (column) {
      case 0:
        return segment.getNum();
      case 1:
        return segment.getOriginal();
      case 2:
        return segment.getTranslation();
      default:
        return null;
    }
  }

  /**
   * remove segument on the row.
   * 
   * @param row to be removed
   */
  public final void removeSegment(final int row) {
    data.remove(row);
    fireTableRowsDeleted(row, row);

    fireTableModelEvent( new TableModelEvent( this, row, row,
        TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE ));
  }

  /**
   * Add segment.
   * 
   * @param segment to be added
   */
  public final void addSegment( final Segment segment ) {
    data.add( segment );

    fireTableModelEvent( new TableModelEvent( this, getRowCount() - 1, 
        getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT ) );
  }

  /**
   * Add listner to table.
   * 
   * @param listener to be added
   */
  @Override
  public final void addTableModelListener( final TableModelListener listener ) {
    tableModelListeners.add( listener );
  }

  /**
   * Get column class.
   * 
   * @param column to get
   * @return class
   */
  @Override
  public final Class getColumnClass( final int column ) {
    switch (column) {
      case 0:
        return String.class;
      case 1:
        return String.class;
      case 2:
        return String.class;
      default:
        return Object.class;
    }
  }

  /**
   * Retrive column name.
   * 
   * @param column to get
   * @return column name
   */
  @Override
  public final String getColumnName( final int column ) {
    switch ( column ) {
      case 0:
        return "";
      case 1:
        return "Original:";
      case 2:
        return "Translation:)";
      default:
        return null;
    }
  }

  /**
   * Check cell is editable or not.
   * 
   * @param row  to check
   * @param column to check
   * @return false
   */
  @Override
  public final boolean isCellEditable( final int row, final int column ) {
    if ( column == 1 || column == 2) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * remove listener from table model.
   * 
   * @param listener to be removed
   */
  @Override
  public final void removeTableModelListener( final TableModelListener listener ) {
    tableModelListeners.remove( listener );
  }

  /**
   * set value at row, column to obj.
   * 
   * @param obj to be set
   * @param row where to set
   * @param column  where to set
   */
  @Override
  public final void setValueAt( final Object obj, final int row,
          final int column ) {
    final Segment segment = data.get( row );
    
    switch ( column ) {
      case 0:
        segment.setNum( obj.toString() );
        break;
      case 1:
        segment.setOriginal( obj.toString() );
        break;
      case 2:
        segment.setTranslation( obj.toString() );
        break;
      default:
        break;
    }

    fireTableModelEvent( new TableModelEvent( this, row, row, column ) );
  }

  private void fireTableModelEvent( final TableModelEvent event ) {
    for ( TableModelListener listener : tableModelListeners ) {
      listener.tableChanged( event );
    }
  }
}
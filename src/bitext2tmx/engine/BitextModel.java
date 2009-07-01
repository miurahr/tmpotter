/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006 Susana Santos Antón
#            (C) 2006-2009 Raymond: Martin et al
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

package bitext2tmx.engine;

import javax.swing.table.*;
import javax.swing.event.*;
import java.util.LinkedList;



/**
 *
 */
final public class BitextModel extends AbstractTableModel
{
  private static final long serialVersionUID = 2627766162716454511L;

  final private LinkedList<Segment> _lstData = new LinkedList<Segment>();
  final private LinkedList<TableModelListener> _lstTableModelListeners =
    new LinkedList<TableModelListener>();

  final public int getColumnCount() { return( 3 ); }
  final public int getRowCount()    { return( _lstData.size() ); }

  final public Object getValueAt( final int iRow, final int iColumn )
  {
    final Segment segment = _lstData.get( iRow );

    switch( iColumn )
    {
      case 0:  return( segment.getNum() );
      case 1:  return( segment.getOriginal() );
      case 2:  return( segment.getTranslation() );
      default: return( null );
    }
  }

  final public void removeSegment( final int iRow )
  {
    _lstData.remove( iRow );
    fireTableRowsDeleted( iRow, iRow );

    fireTableModelEvent( new TableModelEvent( this, iRow, iRow,
      TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE ) );
  }

  final public void addSegment( final Segment segment )
  {
    _lstData.add( segment );

    fireTableModelEvent( new TableModelEvent( this, getRowCount() - 1, 
      getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT ) );
  }

  final public void addTableModelListener( final TableModelListener l )
  { _lstTableModelListeners.add( l ); }

  final public Class getColumnClass( final int iColumn )
  {
    switch( iColumn )
    {
      case 0:  return( String.class );
      case 1:  return( String.class );
      case 2:  return( String.class );
      default: return( Object.class );
    }
  }

  final public String getColumnName( final int iColumn )
  {
    switch( iColumn )
    {
      case 0:  return( "" );
      case 1:  return( "Original:" );  // ToDo: l10n
      case 2:  return( "Translation:" );
      default: return( null );
    }
  }

  final public boolean isCellEditable( final int iRow, final int iColumn )
  { return( false ); }

  final public void removeTableModelListener( final TableModelListener l )
  { _lstTableModelListeners.remove( l ); }

  final public void setValueAt( final Object obj, final int iRow,
    final int iColumn )
  {
    final Segment segment = _lstData.get( iRow );

    switch( iColumn )
    {
      case 0:  segment.setNum( obj.toString() );
      case 1:  segment.setOriginal( obj.toString() );
      case 2:  segment.setTranslation( obj.toString() );
      default: break;
    }

    fireTableModelEvent( new TableModelEvent( this, iRow, iRow, iColumn ) );
  }

  final private void fireTableModelEvent( final TableModelEvent event )
  {
    for( TableModelListener listener : _lstTableModelListeners )
      listener.tableChanged( event );
  }

}//  BitextModel{}



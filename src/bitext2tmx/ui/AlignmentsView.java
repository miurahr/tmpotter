/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009 Raymond: Martin et al
#            (C) 2005-2006 Susana Santos Antón
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

import static bitext2tmx.util.Localization.getString;

import bitext2tmx.engine.BitextModel;
import bitext2tmx.engine.Segment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 *   Alignment Table view for parallel texts.
 *
 */
@SuppressWarnings("serial")
final class AlignmentsView extends DockablePanel {

  private final MainWindow windowMain;

  BitextModel          bitextModel;
  JTable               table;
  private JScrollPane  scrollPane;

  public AlignmentsView( final MainWindow parent ) {
    super( "AlignmentTableView" );

    windowMain = parent;

    getDockKey().setName(getString( "VW.ALIGNMENTS.TITLE" ) );
    getDockKey().setTooltip(getString( "VW.ALIGNMENTS.TOOLTIP" ) );
    getDockKey().setCloseEnabled( true );
    getDockKey().setAutoHideEnabled( true );
    getDockKey().setResizeWeight( 1.0f );  // takes all resizing
    //getDockKey().setIcon( Icons.getIcon( "icon-small.png") );

    setLayout( new BorderLayout() );
  }

  private void onTableClicked() {
    windowMain.onTableClicked();
  }

  private void onTablePressed( final KeyEvent event ) {
    windowMain.onTablePressed( event );
  }

  public final void setFonts( final Font font ) {
    //_tpnOriginal.setFont( f );
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
    } else {
      // System.out.println( " table does not exist yet!" );
    }
  }

  public final TableColumnModel getColumnModel() {
    return ( table.getColumnModel() );
  }

  public final void setColumnHeaderView() {
    scrollPane.setColumnHeaderView(table.getTableHeader() );
  }

  public final void setRowSelectionInterval( final int row, final int len ) {
    table.setRowSelectionInterval( row, len );
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

  //  FixMe: called before table exists!
  public final void repaint() {
    //_tbl.repaint();
  }

  public final Object getValueAt( final int row, final int column ) {
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
    bitextModel = null;

    if ( scrollPane != null ) {
      scrollPane.removeAll();
      scrollPane = null;
    }

    if ( table != null ) {
      table = null;
    }

    removeAll();
    repaint();
    updateUI();
  }

  final void buildDisplay() {
    bitextModel = new BitextModel();
    table   = new JTable( bitextModel );

    TableColumn column;

    //  Segment
    column = table.getColumnModel().getColumn( 0 );
    column.setPreferredWidth( 60 );
    column.setHeaderValue(table.getColumnName( 0 ) );

    //  Original
    column = table.getColumnModel().getColumn( 1 );
    column.setPreferredWidth( 600 );
    column.setHeaderValue(table.getColumnName( 1 ) );

    //  Translation
    column = table.getColumnModel().getColumn( 2 );
    column.setPreferredWidth( 600 );
    column.setHeaderValue(table.getColumnName( 2 ) );

    table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    table.setAutoscrolls( true );

    // ToDo: set the start height according to the users font point size
    table.setRowHeight( 15 );
    table.setRowMargin( 2 );
    table.setRowSelectionAllowed( true );
    table.setColumnSelectionAllowed( false );
    table.setAutoCreateColumnsFromModel( false );
    table.setSelectionBackground( new Color( 220, 235, 250 ) );
    //  ToDo: make user configurable
    //_tbl.setShowGrid( false );
    //  ToDo: make user configurable
    table.setShowHorizontalLines( false );

    table.addKeyListener( new KeyAdapter() {
      public final void keyPressed( final KeyEvent event ) {
        onTablePressed( event );
      }
    });

    table.addMouseListener( new MouseAdapter() {
      public final void mouseClicked( final MouseEvent event ) {
        onTableClicked();
      }
    });

    scrollPane = new JScrollPane( table );
    scrollPane.setColumnHeader( null );
    //_scpnTable.setBounds( new Rectangle( 14, 0, 823, 394 ) );

    // Unnecessary? Container gets header automatically, I think?
    // this sets the scpn header from the table - won't scroll
    scrollPane.setColumnHeaderView(table.getTableHeader() );

    add( scrollPane );
    updateUI();
  }
}

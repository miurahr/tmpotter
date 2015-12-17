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

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;

import bitext2tmx.engine.BitextModel;
import bitext2tmx.engine.Segment;

import static bitext2tmx.util.Localization.getString;


/**
 *   Alignment Table view for parallel texts
 *
 */
final class AlignmentsView extends DockablePanel
{
  private static final long serialVersionUID = -9170260140474066213L;

  final private MainWindow _wndB2T;

  BitextModel          _model;
  JTable               _tbl;
  private JScrollPane  _scpn;

  public AlignmentsView( final MainWindow wndB2T )
  {
    super( "AlignmentTableView" );

    _wndB2T = wndB2T;

    getDockKey().setName(getString( "VW.ALIGNMENTS.TITLE" ) );
    getDockKey().setTooltip(getString( "VW.ALIGNMENTS.TOOLTIP" ) );
    getDockKey().setCloseEnabled( true );
    getDockKey().setAutoHideEnabled( true );
    getDockKey().setResizeWeight( 1.0f );  // takes all resizing
    getDockKey().setIcon( Bitext2TmxIcons.getIcon( "b2t-icon-small.png") );

    setLayout( new BorderLayout() );
  }

  final private void onTableClicked() { _wndB2T.onTableClicked(); }

  final private void onTablePressed( final KeyEvent e )
  { _wndB2T.onTablePressed( e ); }

  final public void setFonts( final Font f ) { //_tpnOriginal.setFont( f );
  }

  final public void setTableFont( final Font f )
  {
   if( _tbl != null )
   {
    int iSize = f.getSize();

    if( iSize <= 10 ) _tbl.setRowHeight( 10 );
    else _tbl.setRowHeight( iSize );

    _tbl.setFont( f );
   }
   else System.out.println( " _tbl does notexist yet!" );
  }

  final public TableColumnModel getColumnModel()
  { return( _tbl.getColumnModel() ); }

  final public void setColumnHeaderView()
  { _scpn.setColumnHeaderView( _tbl.getTableHeader() ); }

  final public void setRowSelectionInterval( final int i, final int j )
  { _tbl.setRowSelectionInterval( i, j ); }

  final public void setValueAt( final String str, final int i, final int j )
  { _tbl.setValueAt( str, i, j ); }

  final public void setPreferredSize( final int iWidth, final int iHeightMutliple, final int iOffset )
  {
    _tbl.setPreferredSize( new Dimension( iWidth, ( _tbl.
      getRowCount() * iHeightMutliple ) + iOffset ) );
  }

  final public int getRowCount() { return( _tbl.getRowCount() ); }

  final public void setModelValueAt( final Object obj, final int i, final int j )
  { _model.setValueAt( obj, i, j ); }

  final public void removeSegment( final int i )
  {
    _model.removeSegment( i );
    revalidate();
  }

  final public void addModelSegment( final Segment segment )
  { _model.addSegment( segment ); }

  //  FixMe: called before table exists!
  final public void repaint() { //_tbl.repaint();
  }

  final public Object getValueAt( final int iRow, final int iCol )
  { return( _tbl.getValueAt( iRow, iCol ) ); }

  final public int getSelectedRow() { return( _tbl.getSelectedRow() ); }

  final public int getSelectedColumn() { return( _tbl.getSelectedColumn() ); }

  final public JTableHeader getTableHeader() { return( _tbl.getTableHeader() ); }


  final void clear()
  {
   _model = null;

    if( _scpn != null )
    {
      _scpn.removeAll();
      _scpn = null;
    }

    if( _tbl != null ) _tbl = null;

    removeAll();
    repaint();
    updateUI();
  }

  final void buildDisplay()
  {
    _model = new BitextModel();
    _tbl   = new JTable( _model );

    TableColumn column = null;

    //  Segment
    column = _tbl.getColumnModel().getColumn( 0 );
    column.setPreferredWidth( 60 );
    column.setHeaderValue( _tbl.getColumnName( 0 ) );

    //  Original
    column = _tbl.getColumnModel().getColumn( 1 );
    column.setPreferredWidth( 600 );
    column.setHeaderValue( _tbl.getColumnName( 1 ) );

    //  Translation
    column = _tbl.getColumnModel().getColumn( 2 );
    column.setPreferredWidth( 600 );
    column.setHeaderValue( _tbl.getColumnName( 2 ) );

    _tbl.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    _tbl.setAutoscrolls( true );

    // ToDo: set the start height according to the users font point size
    _tbl.setRowHeight( 15 );
    _tbl.setRowMargin( 2 );
    _tbl.setRowSelectionAllowed( true );
    _tbl.setColumnSelectionAllowed( false );
    _tbl.setAutoCreateColumnsFromModel( false );
    _tbl.setSelectionBackground( new Color( 220, 235, 250 ) );
    //  ToDo: make user configurable
    //_tbl.setShowGrid( false );
    //  ToDo: make user configurable
    _tbl.setShowHorizontalLines( false );

    _tbl.addKeyListener( new KeyAdapter()
      { final public void keyPressed( final KeyEvent e )
        { onTablePressed( e ); } } );

    _tbl.addMouseListener( new MouseAdapter()
      { final public void mouseClicked( final MouseEvent e )
        { onTableClicked(); } } );

    _scpn = new JScrollPane( _tbl );
    _scpn.setColumnHeader( null );
    //_scpnTable.setBounds( new Rectangle( 14, 0, 823, 394 ) );

    // Unnecessary? Container gets header automatically, I think?
    // this sets the scpn header from the table - won't scroll
    _scpn.setColumnHeaderView( _tbl.getTableHeader() );

    add( _scpn );
    updateUI();
  }

}//  OriginalView{}



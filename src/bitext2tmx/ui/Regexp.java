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


package bitext2tmx.ui;

import java.awt.*;
import java.awt.Dimension;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import static bitext2tmx.util.Localization.*;


/**
 *
 */
final public class Regexp extends JDialog implements ActionListener
{
  private static final long serialVersionUID = 2154472900969488201L;

  final private JPanel       _pnl       = new JPanel();
  final private BorderLayout _blt       = new BorderLayout();
  final private JPanel       _pnl1      = new JPanel();
  final private JTable       _tbl       = new JTable( 10, 1 );
  final private JScrollPane  _scpn      = new JScrollPane();
  final private JButton      _btnOK     = new JButton();
  final private JButton      _btnCancel = new JButton();

  private String  _strRegexp = "";

/*
  public Regexp( final Frame frame, final String title, final boolean modal )
  {
    super( frame, title, modal );

    initialize();
  }
*/
  public Regexp()
  {
    initialize();
    //  No resize! -RM
    //this( null, "", false );
  }

  final private void initialize()
  {
    //TableColumn column = null;

    TableColumn column = _tbl.getColumnModel().getColumn( 0 );
    column.setPreferredWidth( 100 );
    column.setHeaderValue( l10n( "DLG.CONFIG.COL.HDR.VAL" ) );

    _btnOK.setBounds( new Rectangle( 78, 238, 96, 24 ) );
    _btnOK.setText( l10n( "BTN.OK" ) ); 
    _btnOK.addActionListener( this );

    _btnCancel.setBounds( new Rectangle( 216, 238, 96, 24 ) );
    _btnCancel.setSelected( false );
    _btnCancel.setText( l10n( "BTN.CANCEL" ) );
    _btnCancel.addActionListener( this );

    _pnl1.setBorder( BorderFactory.createLineBorder( Color.black ) );
    _pnl1.setLayout( null );

    _pnl1.add( _btnOK, null );
    _pnl1.add( _btnCancel, null );

    _scpn.setBounds( new Rectangle( 27, 36, 192, 178 ) );
    _scpn.getViewport().add( _tbl, null );
    _scpn.setColumnHeaderView( _tbl.getTableHeader() );

    _pnl1.add( _scpn, null );

    _pnl.setLayout( _blt );
    _pnl.add( _pnl1, BorderLayout.CENTER );

    getContentPane().add( _pnl );

    setModal( false );
    setTitle( l10n( "DLG.CONFIG.TITLE" ) );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds( ( screenSize.width - 500 ) / 2,
        ( screenSize.height - 300 ) / 2, 500, 300 );
  }

  final private void buildRegexp()
  {
    boolean bFirst = true;

    for( int cont=0; cont<_tbl.getRowCount(); cont++ )
      if( _tbl.getValueAt( cont, 0 ) != null )
      {
        if( !bFirst ) _strRegexp += "|";

        _strRegexp += _tbl.getValueAt( cont, 0 );
        bFirst = false;
      }
  }

  final public String getRegexp() { return( _strRegexp ); }
  final private void  onCancel()  { setVisible( false ); dispose(); }
  final private void  onOK()      { buildRegexp(); }

  final public void actionPerformed( final ActionEvent action )
  {
    final Object actor = action.getSource();

    if( actor instanceof JButton )
    {
      if( actor == _btnCancel )  onCancel();
      else if( actor == _btnOK ) onOK();
    }
  }

}//  Regexp{}



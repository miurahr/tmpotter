/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006  Susana Santos Antón
#            (C) 2005-2009  Raymond: Martin et al
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
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#######################################################################
*/


package bitext2tmx.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.util.AppConstants;
import static bitext2tmx.util.Localization.getString;


/**
* Ask encodings
*
* @author
*/
final public class Encodings extends JDialog implements ActionListener
{
  private static final long serialVersionUID = 9103389076759237131L;

  boolean _bClosed;

  final public boolean isClosed() { return( _bClosed ); }

  public Encodings( final Frame frame, final boolean modal )
  {
    super( frame, modal );

    initialize();
  }

  public Encodings()
  {
    //  No resize! -RM
    this( null, false );
  }

  private void initialize()
  {
    _pnl.setLayout( null );

    setLocalizedText( _btnOK, getString( "BTN.OK" ) );
    _btnOK.addActionListener( this );
    _btnOK.setBounds( new Rectangle( 72, 85, 115, 29 ) );

    _cbxEncoding.setSelectedIndex( 1 );
    _cbxEncoding.setBounds( new Rectangle( 60, 43, 145, 30 ) );

    _lblEncoding.setOpaque( false );
    _lblEncoding.setBounds( new Rectangle( 53, 15, 176, 27 ) );
    _lblEncoding.setText( getString( "LBL.ENCODING" ) );

    _pnl.setEnabled( true );

    addWindowListener( new WindowAdapter()
      { public void windowClosing( final WindowEvent evt ) { onClose(); } } );

    setModal( true );
    setResizable( false );
    setTitle( getString( "DLG.ENCODING.TITLE" ) );

    getContentPane().add( _pnl, BorderLayout.CENTER );

    _pnl.add( _lblEncoding, null );
    _pnl.add(_cbxEncoding, null );
    _pnl.add( _btnOK, null );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds( ( screenSize.width - 260 ) / 2,
        ( screenSize.height - 150 ) / 2, 260, 150 );
  }

  final public String getComboBoxEncoding()
  { return( _cbxEncoding.getSelectedItem().toString() ); }

  final void onOK() { setVisible( false ); }

  final void onClose()
  {
    _bClosed = true;
    setVisible( false );
    dispose();
  }

  final public void actionPerformed( final ActionEvent action )
  {
    final Object actor = action.getSource();

    if( actor instanceof JButton )
    {
      if( actor == _btnOK ) onOK();
    }
  }

  // Variables declaration
  final private JPanel    _pnl         = new JPanel();
  final private JButton   _btnOK       = new JButton();
  final private JLabel    _lblEncoding = new JLabel();
  final private JComboBox _cbxEncoding = new JComboBox( AppConstants.straEncodings );
  // End of variables declaration
}//  Encodings{}



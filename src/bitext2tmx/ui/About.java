/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2009  Raymond: Martin
#
#  Includes code: Copyright (C) 2002-2006 Keith Godfrey et al.
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


package bitext2tmx.ui;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import bitext2tmx.ui.Bitext2tmxWindow;

import static org.openide.awt.Mnemonics.*;

import static bitext2tmx.util.Constants.*;
import static bitext2tmx.util.Localization.*;
import static bitext2tmx.util.Utilities.*;


/**
 *  About Dialog
 *
 */
final public class About extends JDialog
{
  final private static long serialVersionUID = -7978170933387914555L;

  final private Bitext2tmxWindow  _wndB2T;

  final private JLabel   _lbl1       = new JLabel();
  final private JLabel   _lbl2       = new JLabel();
  final private JLabel   _lbl3       = new JLabel();
  final private JLabel   _lbl4       = new JLabel();
  final private JLabel   _lblIcon    = new JLabel();
  final private JButton  _btnClose   = new JButton();
  final private JPanel   _pnlButtons = new JPanel();

  //  ToDo: l10n, constants
  final private String  _strProduct     = APPNAME;
  final private String  _strDescription = "Bitext Aligner/Converter to TMX";
  final private String  _strCopyright   = "Copyright (C) 2005-2009,2015";
  final private String  _strAuthors     = "Hiroshi Miura, Susana Santos Antón, Raymond: Martin, et al.";

  public About( Bitext2tmxWindow wndB2T )
  {
    super( wndB2T, true );

    _wndB2T = wndB2T;

    initialize();
  }

  private void initialize()
  {
    setTitle( getString( "DLG.ABOUT.TITLE" ) );
    setModal( true );
    setResizable( false );

    addWindowListener( new WindowAdapter()
      { public void windowClosing( final WindowEvent evt ) { onClose(); } } );

    _lbl1.setText( _strProduct );
    _lbl2.setText( _strDescription );
    _lbl3.setText( _strCopyright );
    _lbl4.setText( _strAuthors );

    final GridBagConstraints gbc = new GridBagConstraints();

    getContentPane().setLayout( new GridBagLayout() );

    gbc.gridwidth = 1;
    gbc.gridx     = 0;
    gbc.gridy     = 0;
    gbc.fill      = GridBagConstraints.CENTER;
    gbc.weightx   = 1.0;
    gbc.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _lbl1, gbc );

    gbc.gridy = 1;
    getContentPane().add( _lbl2, gbc );

    gbc.gridy = 2;
    getContentPane().add( _lbl3, gbc );

    gbc.gridy = 3;
    getContentPane().add( _lbl4, gbc );

    _lblIcon.setIcon( getIcon( "b2t-icon-medium.png", this ) );
    _pnlButtons.add( _lblIcon, BorderLayout.WEST );

    gbc.gridy     = 5;
    gbc.anchor    = GridBagConstraints.SOUTHWEST;
    gbc.insets    = new Insets( 5, 25, 5, 10 );

    getContentPane().add( _lblIcon, gbc );

    setLocalizedText( _btnClose, getString( "BTN.CLOSE" ) );
    _btnClose.addActionListener( new ActionListener()
      { public void actionPerformed( final ActionEvent evt ) { onClose(); } } );

    gbc.anchor    = GridBagConstraints.CENTER;
    getContentPane().add( _btnClose, gbc );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds( ( screenSize.width - 400 ) / 2,
        ( screenSize.height - 200 ) / 2, 400, 200 );
  }

  final private void onClose()
  {
    setVisible( false );
    dispose();
  }

}//  About{}



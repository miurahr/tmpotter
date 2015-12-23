/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package bitext2tmx.ui.dialogs;

import static bitext2tmx.util.Localization.getString;
import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.util.AppConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
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


/**
* Ask encodings.
*
* @author 
*/
@SuppressWarnings("serial")
public final class Encodings extends JDialog implements ActionListener {

  boolean closed;

  public final boolean isClosed() {
    return ( closed );
  }

  /**
   * Encoding dialog.
   * 
   * @param frame frame to show
   * @param modal is modal?
   */
  public Encodings( final Frame frame, final boolean modal ) {
    super( frame, modal );

    initialize();
  }

  public Encodings() {
    //  No resize! -RM
    this( null, false );
  }

  private void initialize() {
    panel.setLayout( null );

    setLocalizedText( buttonOk, getString( "BTN.OK" ) );
    buttonOk.addActionListener( this );
    buttonOk.setBounds( new Rectangle( 72, 85, 115, 29 ) );

    comboEncoding.setSelectedIndex( 1 );
    comboEncoding.setBounds( new Rectangle( 60, 43, 145, 30 ) );

    labelEncoding.setOpaque( false );
    labelEncoding.setBounds( new Rectangle( 53, 15, 176, 27 ) );
    labelEncoding.setText( getString( "LBL.ENCODING" ) );

    panel.setEnabled( true );

    addWindowListener( new WindowAdapter() {
        public void windowClosing( final WindowEvent evt ) {
          onClose(); 
        } 
    } );

    setModal( true );
    setResizable( false );
    setTitle( getString( "DLG.ENCODING.TITLE" ) );

    getContentPane().add( panel, BorderLayout.CENTER );

    panel.add( labelEncoding, null );
    panel.add(comboEncoding, null );
    panel.add( buttonOk, null );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds( ( screenSize.width - 260 ) / 2,
        ( screenSize.height - 150 ) / 2, 260, 150 );
  }

  public final String getComboBoxEncoding() {
    return ( comboEncoding.getSelectedItem().toString() );
  }

  final void onOk() {
    setVisible( false );
  }

  final void onClose() {
    closed = true;
    setVisible( false );
    dispose();
  }

  /**
   * Action handler.
   * 
   * @param action event
   */
  @Override
  public final void actionPerformed( final ActionEvent action ) {
    final Object actor = action.getSource();

    if ( actor instanceof JButton ) {
      if ( actor == buttonOk ) {
        onOk();
      }
    }
  }

  // Variables declaration
  private final JPanel    panel         = new JPanel();
  private final JButton   buttonOk      = new JButton();
  private final JLabel    labelEncoding = new JLabel();
  private final JComboBox comboEncoding = new JComboBox( AppConstants.straEncodings );
  // End of variables declaration
}



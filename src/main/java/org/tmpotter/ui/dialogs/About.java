/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
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

package org.tmpotter.ui.dialogs;

import static org.openide.awt.Mnemonics.setLocalizedText;

import static org.tmpotter.util.Localization.getString;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextArea;

import org.tmpotter.ui.Icons;
import org.tmpotter.ui.MainWindow;
import org.tmpotter.util.AppConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;


/**
 *  About Dialog.
 * 
 *  @author Hiroshi Miura
 */
@SuppressWarnings("serial")
public final class About extends JDialog {

  public About( MainWindow mainWindow ) {
    this(mainWindow, true );
  }

  public About(MainWindow mainWindow, Boolean modal) {
    super(mainWindow, modal);
    initComponents();
  }

  @SuppressWarnings("unchecked")
  private void initComponents() {
    setTitle( getString( "DLG.ABOUT.TITLE" ) );
    setModal( true );
    setResizable( false );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( final WindowEvent evt ) {
        onClose(); 
      } 
    });

    label1.setText(AppConstants.getDisplayNameAndVersion() );
    label2.setText(AppConstants.getApplicationDescription() );
    label3.setText(AppConstants.COPYRIGHT + " " + AppConstants.AUTHORS );
    contributor.setText(AppConstants.CONTRIBUTORS);

    Font font = new Font("Free Serif", Font.BOLD,14);
    label1.setFont(font);
    label2.setFont(font);
    label3.setFont(font);

    Color cl = new Color(180,180,180,100);
    contributor.setBackground(cl);

    final GridBagConstraints gbc = new GridBagConstraints();

    getContentPane().setLayout( new GridBagLayout() );

    gbc.gridwidth = 1;
    gbc.gridx     = 0;
    gbc.gridy     = 0;
    gbc.fill      = GridBagConstraints.CENTER;
    gbc.weightx   = 1.0;
    gbc.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( label1, gbc );

    gbc.gridy = 1;
    getContentPane().add( label2, gbc );

    gbc.gridy = 2;
    getContentPane().add( label3, gbc );

    gbc.gridy = 3;
    getContentPane().add(contributor, gbc);

    labelIcon.setIcon( Icons.getIcon( "icon-medium.png" ) );
    panelButtons.add( labelIcon, BorderLayout.WEST );

    gbc.gridy     = 7;
    gbc.anchor    = GridBagConstraints.SOUTHWEST;
    gbc.insets    = new Insets( 5, 30, 5, 10 );

    getContentPane().add( labelIcon, gbc );

    setLocalizedText( buttonClose, getString( "BTN.CLOSE" ) );
    buttonClose.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( final ActionEvent evt ) {
        onClose(); 
      }
    });

    gbc.anchor    = GridBagConstraints.CENTER;
    getContentPane().add( buttonClose, gbc );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds( ( screenSize.width - 400 ) / 2,
        ( screenSize.height - 200 ) / 2, 400, 300 );
  }

  private void onClose() {
    setVisible( false );
    dispose();
  }

  // Variables declaration
  private final JXLabel label1 = new JXLabel();
  private final JXLabel label2 = new JXLabel();
  private final JXLabel label3 = new JXLabel();
  private final JXTextArea   contributor  = new JXTextArea();
  private final JXLabel labelIcon = new JXLabel();
  private final JXButton  buttonClose  = new JXButton();
  private final JXPanel   panelButtons = new JXPanel();
  // End of variables declaration
}
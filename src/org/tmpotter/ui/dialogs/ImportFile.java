/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
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
 * ************************************************************************/

package org.tmpotter.ui.dialogs;

import static org.openide.awt.Mnemonics.setLocalizedText;
import static org.tmpotter.util.Localization.getString;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import org.tmpotter.util.AppConstants;
import org.tmpotter.util.Localization;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * Import file dialog.
 *
 * @author miurahr
 */
@SuppressWarnings("serial")
public class ImportFile  extends JDialog implements ActionListener {
  private final JXPanel panel = new JXPanel();
  
  private final JXLabel labelEncoding = new JXLabel();

  private final JXLabel labelImportFile = new JXLabel();
  private final JTextField fieldImportFile = new JTextField();
  private final JXButton buttonImportFile = new JXButton();

  private final JXButton buttonOk = new JXButton();
  private final JXButton buttonCancel = new JXButton();

  private File filePath;
  private boolean closed;
  
  private String originalLang;
  private String translationLang;

  public final File getPath() {
    return ( filePath );
  }
  
  public final void setPath( final File filePath ) {
    this.filePath = filePath;
  }

  public final boolean isClosed() {
    return ( closed );
  }

  private final JXComboBox comboSourceLang = new JXComboBox();
  private final JXComboBox comboTranslationLang = new JXComboBox();
  private final JXLabel labelSourceLang = new JXLabel();
  private final JXLabel labelTranslationLang = new JXLabel();
  private final JComboBox comboOriginalEncoding    = new JComboBox( AppConstants
          .straEncodings );
  private final int numEncodings = AppConstants.straEncodings.length;

  public File userPathFile = new File( System.getProperty( "user.dir" ) );

  private String   originalDocFilename;
  
  private final String [] idiom = Localization.getLanguageList();
  
  /**
   * Constructor.
   * 
   * @param frame parent frame
   * @param title dialog title
   * @param modal is modal?
   */
  public ImportFile( final Frame frame, final String title,
          final boolean modal ) {
    super( frame, title, modal );

    initComponent();
  }
  
  private void initComponent() {
    
    panel.setLayout( null );

    labelImportFile.setText( getString( "LBL.IMPORT.FILE" ) );
    labelImportFile.setBounds( new Rectangle( 5, 10, 200, 15 ) );

    fieldImportFile.setText( "" );
    fieldImportFile.setBounds( new Rectangle( 5, 30, 300, 22 ) );

    setLocalizedText( buttonImportFile, getString( "BTN.BROWSE.FILE" ) );
    buttonImportFile.addActionListener( this );
    buttonImportFile.setBounds( new Rectangle( 310, 30, 100, 22 ) );

    setLocalizedText( buttonOk, getString( "BTN.OK" ) );
    buttonOk.addActionListener( this );
    buttonOk.setBounds( new Rectangle( 55, 115, 100, 22 ) );

    setLocalizedText( buttonCancel, getString( "BTN.CANCEL" ) );
    buttonCancel.addActionListener( this );
    buttonCancel.setBounds( new Rectangle( 160, 115, 100, 22 ) );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( final WindowEvent evt ) {
        onClose();
      }
    } );

    setModal( true );
    setResizable( false );
    setTitle( getString( "DLG.OPEN.TITLE" ) );
    getContentPane().setLayout( null );

    for (String item : idiom) {
      comboSourceLang.addItem(item);
      comboTranslationLang.addItem(item);
    }

    comboSourceLang.setToolTipText(getString( "CB.LANG.SOURCE.TOOLTIP"));
    comboSourceLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());
    comboSourceLang.setBounds(new Rectangle(5, 75, 100, 22));
    labelSourceLang.setText(getString("LBL.SOURCE.LANG"));
    labelSourceLang.setBounds(new Rectangle( 5, 55, 100, 16));

    comboTranslationLang.setToolTipText(getString( "CB.LANG.TARGET.TOOLTIP"));
    comboTranslationLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());
    comboTranslationLang.setBounds(new Rectangle(120, 75, 100, 22));
    labelTranslationLang.setText(getString("LBL.TARGET.LANG"));
    labelTranslationLang.setBounds(new Rectangle(120, 55, 100, 16));

    comboOriginalEncoding.removeItemAt(numEncodings - 1);
    comboOriginalEncoding.addItem(getString("ENCODING.DEFAULT"));
    comboOriginalEncoding.setToolTipText(getString("CB.ENCODING.TOOLTIP"));
    comboOriginalEncoding.setSelectedIndex( 0 );
    comboOriginalEncoding.setBounds(new Rectangle(250, 75, 100, 22));
    labelEncoding.setText(getString("LBL.ENCODING"));
    labelEncoding.setBounds(new Rectangle(250, 55, 100, 16));

    panel.setBounds(new Rectangle(-1, 0, 420, 180));

    panel.add(fieldImportFile, null);
    panel.add(buttonImportFile, null);

    panel.add(comboSourceLang, null);
    panel.add(comboTranslationLang, null);
    panel.add(comboOriginalEncoding, null);
    
    panel.add(buttonCancel, null);
    panel.add(buttonOk, null);

    panel.add(labelImportFile, null);
    panel.add(labelSourceLang, null);
    panel.add(labelTranslationLang, null);
    panel.add(labelEncoding, null);

    getContentPane().add(panel, null );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width - 420) / 2, (screenSize.height - 180) / 2, 420, 180);
  }

  public final File getFilePath() {
    return ( filePath );
  }

  public final String getSourceLocale() {
    return ( originalLang );
  }
  
  public final String getTargetLocale() {
    return ( translationLang ); 
  }
  
  public final JComboBox getLangEncComboBox() {
    return ( comboOriginalEncoding );
  }

  public final String getSource() {
    return ( originalDocFilename );
  }

  private void onImportFile() {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory( filePath );

    fc.setMultiSelectionEnabled( false );
    final int returnVal = fc.showOpenDialog(panel );
    filePath = fc.getCurrentDirectory();

    if ( returnVal == JFileChooser.APPROVE_OPTION ) {
      filePath = fc.getSelectedFile();
      if (fc.getName( filePath ).endsWith( ".po" ) && filePath.exists()) {
        originalDocFilename = fc.getName( filePath );
        fieldImportFile.setText(filePath.getPath());
      } else {
        JOptionPane.showMessageDialog(panel,
            getString( "MSG.ERROR.FILE_NOTFOUND" ),
            getString( "MSG.ERROR" ), JOptionPane.ERROR_MESSAGE );
        fieldImportFile.setText( "" );
      }
    }
  }

  private void onOk() {
    boolean errorImport = true;
    try {
      if ( fieldImportFile.getText() != null ) {
        final FileReader fr = new FileReader( fieldImportFile.getText() );
        fr.close();
        originalDocFilename = fieldImportFile.getText();
        filePath = new File( originalDocFilename );
        originalLang = Localization.getLanguageCode(comboSourceLang.getSelectedIndex());
        translationLang = Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
        errorImport = false;
        setVisible(false);
      }
    } catch ( final IOException ex ) {
      JOptionPane.showMessageDialog(panel, getString( "MSG.ERROR.FILE_NOTFOUND" ),
          getString( "MSG.ERROR" ), JOptionPane.ERROR_MESSAGE );
      fieldImportFile.setText("");
    }
    
    if (errorImport) {
      fieldImportFile.setText( "" );
    }
  }

  private void onCancel() {
    onClose();
  }

  private void onClose() {
    closed = true;
    setVisible( false );
    dispose();
  }

  /**
   * action handler.
   * 
   * @param action event
   */
  @Override
  public final void actionPerformed( final ActionEvent action ) {
    final Object actor = action.getSource();

    if ( actor instanceof JButton ) {
      if ( actor == buttonCancel ) {
        onCancel();
      } else if ( actor == buttonOk ) {
        onOk();
      } else if ( actor == buttonImportFile ) {
        onImportFile();
      }
    }
  }
  
  /**
   * set language code.
   * 
   * @param originalFlag indicate original or translation.
   */
  final void setLanguageCode( final boolean originalFlag ) {
    String strLang = originalFlag ? comboSourceLang.getSelectedItem()
            .toString() : comboTranslationLang.getSelectedItem().toString();
    
    if ( originalFlag ) {
      originalLang = strLang;
    } else {
      translationLang = strLang;
    }
  }
}

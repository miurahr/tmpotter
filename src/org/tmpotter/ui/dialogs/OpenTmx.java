/**
 * ************************************************************************
 *
 * tmpotter - Bitext Aligner/TMX Editor
 *
 * Copyright (C) 2015 Hiroshi Miura
 *
 *
 *  This file is part of tmpotter.
 *
 *  tmpotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  tmpotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with tmpotter.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package org.tmpotter.ui.dialogs;

import static org.openide.awt.Mnemonics.setLocalizedText;

import static org.tmpotter.util.Localization.getString;

import org.tmpotter.util.AppConstants;
import org.tmpotter.util.Localization;
import org.tmpotter.util.Utilities;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Open TMX file dialog.
 *
 * @author Hiroshi Miura
 */
@SuppressWarnings("serial")
public class OpenTmx extends JDialog implements ActionListener {
  private final JPanel  panel = new JPanel();

  private final JLabel      labelTmxFile  = new JLabel();
  private final JTextField  fieldTmxFile  = new JTextField();
  private final JButton     buttonTmxFile = new JButton();

  private final JButton  buttonOk     = new JButton();
  private final JButton  buttonCancel = new JButton();

  private final JLabel  labelEncoding            = new JLabel();

  private File     filePath;
  private boolean  closed;
  
  
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

  private final JComboBox  comboSourceLang    = new JComboBox();
  private final JComboBox  comboTranslationLang = new JComboBox();
  private final JLabel     labelSourceLang    = new JLabel();
  private final JLabel     labelTranslationLang = new JLabel();
  
  
  public File userPathFile = new File( System.getProperty( "user.dir" ) );

  private final JComboBox comboEncoding    = new JComboBox( AppConstants
          .straEncodings );
  private final int numEncodings = AppConstants.straEncodings.length;
  private String   originalDoc;
  
  private final String [] idiom = Localization.getLanguageList();
  
  /**
   * Constructor.
   * 
   * @param frame parent frame
   * @param title dialog title
   * @param modal is modal?
   */
  public OpenTmx( final Frame frame, final String title,
          final boolean modal ) {
    super( frame, title, modal );

    initComponent();
  }
  
  private void initComponent() {
    
    panel.setLayout( null );

    labelTmxFile.setText( getString( "LBL.TMX.FILE" ) );
    labelTmxFile.setBounds( new Rectangle( 5, 10, 200, 15 ) );

    fieldTmxFile.setText( "" );
    fieldTmxFile.setBounds( new Rectangle( 5, 30, 300, 22 ) );

    setLocalizedText( buttonTmxFile, getString( "BTN.BROWSE.TMX" ) );
    buttonTmxFile.addActionListener( this );
    buttonTmxFile.setBounds( new Rectangle( 310, 30, 100, 22 ) );

    setLocalizedText( buttonOk, getString( "BTN.OK" ) );
    buttonOk.addActionListener( this );
    //buttonOk.setBounds( new Rectangle( 205, 115, 100, 22 ) );
    buttonOk.setBounds( new Rectangle( 55, 115, 100, 22 ) );

    setLocalizedText( buttonCancel, getString( "BTN.CANCEL" ) );
    buttonCancel.addActionListener( this );
    //buttonCancel.setBounds( new Rectangle( 310, 115, 100, 22 ) );
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

    comboSourceLang.setToolTipText( getString( "CB.LANG.SOURCE.TOOLTIP" ) );
    comboSourceLang.setSelectedItem( Locale.getDefault().getDisplayLanguage() );
    //comboSourceLang.setBounds( new Rectangle( 420, 30, 100, 22 ) );
    comboSourceLang.setBounds( new Rectangle( 5, 75, 100, 22 ) );
    labelSourceLang.setText( getString( "LBL.SOURCE.LANG" ) );
    //labelSourceLang.setBounds( new Rectangle( 420, 10, 100, 16 ) );
    labelSourceLang.setBounds( new Rectangle( 5, 55, 100, 16 ) );

    comboEncoding.removeItemAt( numEncodings - 1 );
    comboEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    comboEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    comboEncoding.setSelectedIndex( 0 );
    //comboEncoding.setBounds( new Rectangle( 530, 30, 100, 22 ) );
    comboEncoding.setBounds( new Rectangle( 250, 75, 100, 22 )); 
        
    labelEncoding.setText( getString( "LBL.ENCODING" ) );
    //labelEncoding.setBounds( new Rectangle( 530, 10, 100, 16 ) );
    labelEncoding.setBounds(new Rectangle( 250, 55, 100, 16 ));

    comboTranslationLang.setToolTipText( getString( "CB.LANG.TARGET.TOOLTIP" ) );
    comboTranslationLang.setSelectedItem( Locale.getDefault().getDisplayLanguage() );
    //comboTranslationLang.setBounds( new Rectangle( 420, 75, 100, 22 ) );
    comboTranslationLang.setBounds(new Rectangle( 120, 75, 100, 22 ));
    labelTranslationLang.setText( getString( "LBL.TARGET.LANG" ) );
    //labelTranslationLang.setBounds( new Rectangle( 420, 55, 100, 16 ) );
    labelTranslationLang.setBounds( new Rectangle( 120, 55, 100, 16 ));
        
    panel.setBounds( new Rectangle( -1, 0, 420, 180 ) );

    panel.add( fieldTmxFile, null );
    panel.add( buttonTmxFile, null );

    panel.add(comboSourceLang, null );
    panel.add(comboTranslationLang, null );
    panel.add(comboEncoding, null );

    panel.add( buttonCancel, null );
    panel.add( buttonOk, null );

    panel.add( labelTmxFile, null);
    panel.add( labelEncoding, null);
    panel.add( labelSourceLang, null);
    panel.add( labelTranslationLang, null);

    getContentPane().add(panel, null );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds( ( screenSize.width - 420 ) / 2,
        ( screenSize.height - 180 ) / 2, 420, 180 );
  }
  
  public final JComboBox getLangEncComboBox() {
    return ( comboEncoding );
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
  
  private void onTmxFile() {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory( filePath );

    fc.setMultiSelectionEnabled( false );
    final int returnVal = fc.showOpenDialog(panel );
    filePath = fc.getCurrentDirectory();

    if ( returnVal == JFileChooser.APPROVE_OPTION ) {
      filePath = fc.getSelectedFile();

      if (fc.getName( filePath ).endsWith( ".tmx" )
          && filePath.exists()) {
        originalDoc = fc.getName( filePath );
        fieldTmxFile.setText(filePath.getPath());
        
      } else {
        JOptionPane.showMessageDialog(panel,
            getString( "MSG_ERROR_FILE_NOTFOUND" ),
            getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );
        fieldTmxFile.setText( "" );
      }
    }
  }

  private void onOk() {
    boolean errorTmx = true;
    try {
      if ( fieldTmxFile.getText() != null ) {
        final FileReader fr = new FileReader( fieldTmxFile.getText() );
        fr.close();
        originalDoc = fieldTmxFile.getText();
        filePath = new File( originalDoc );
        originalLang = Localization.getLanguageCode(comboSourceLang
                .getSelectedIndex());
        translationLang = Localization.getLanguageCode(comboTranslationLang
                .getSelectedIndex());
        errorTmx = false;
        setVisible(false);
      }
    } catch ( final IOException ex ) {
      JOptionPane.showMessageDialog(panel, getString( "MSG.ERROR.FILE.NOTFOUND" ),
          getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );
      fieldTmxFile.setText("");
    }
    
    if (errorTmx) {
      fieldTmxFile.setText( "" );
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
      } else if ( actor == buttonTmxFile ) {
        onTmxFile();
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

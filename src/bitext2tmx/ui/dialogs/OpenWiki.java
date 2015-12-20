/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006 Susana Santos Antón
#            (C) 2006-2009 Raymond: Martin et al
#                2015 Hiroshi Miura
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

package bitext2tmx.ui.dialogs;

import static bitext2tmx.util.Localization.getString;
import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.util.AppConstants;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * dialog to open wiki page.
 * 
 * @author Hiroshi Miura
 */
@SuppressWarnings("serial")
public final class OpenWiki extends JDialog implements ActionListener {

  private final JPanel  panel = new JPanel();

  private final JLabel      labelOriginal = new JLabel();
  private final JTextField  fieldOriginal = new JTextField();
  private final JButton     buttonOriginal = new JButton();

  private final JLabel      labelTranslation = new JLabel();
  private final JTextField  fieldTranslation = new JTextField();
  private final JButton     buttonTranslation = new JButton();

  private final JButton  buttonOk     = new JButton();
  private final JButton  buttonCancel = new JButton();

  private final JLabel  labelEncoding            = new JLabel();
  private final JLabel  labelOriginalEncoding    = new JLabel();
  private final JLabel  labelTranslationEncoding = new JLabel();

  private File     filePath;
  private File     originalFilePath;
  private File     translationFilePath;
  private String   stringOriginal;
  private String   stringTranslation;
  private boolean  closed;
  private int      type;  // = 0;  // 0: text (default); 1: tmx

  /**
   * get file path.
   * 
   * @return file
   */
  public final File getPath() {
    return ( filePath );
  }
  
  /**
   * set file path.
   * 
   * @param filePath path to set
   */
  public final void setPath( final File filePath ) {
    this.filePath = filePath;
  }

  public final File getSourcePath() {
    return ( originalFilePath );
  }
  
  public final File getTargetPath() {
    return ( translationFilePath );
  }

  public final boolean isClosed() {
    return ( closed );
  }

  public final String getSource() {
    return ( stringOriginal );
  }
  
  public final String getTarget() {
    return ( stringTranslation );
  }

  public final int getTypes() {
    return ( type );
  }

  private final JComboBox  comboOriginalLang    = new JComboBox();
  private final JComboBox  comboTranslationLang = new JComboBox();
  private final JLabel     labelOriginalLang    = new JLabel();
  private final JLabel     labelTranslationLang = new JLabel();

  private String originalLanguage;
  private String translationLanguage;

  public File userPath = new File( System.getProperty( "user.dir" ) );

  private final JComboBox comboOriginalEncoding    = new JComboBox( AppConstants.straEncodings );
  private final JComboBox comboTranslationEncoding = new JComboBox( AppConstants.straEncodings );
  private final int numEncodings = AppConstants.straEncodings.length;

  private final String [] idiomCa = {"Alemany","Angl�s","�rab","Catal��",
      "Txec","Core��",
      "Dan�s","Espanyol","Finland�s","Franc�s","Holand�s","Hongar�s",
      "Itali��","Japon�s","Noruec","Polon�s","Portugu�s","Rus","Suec",
      "Tailand�s","Xin�s"};

  private final String [] idiomEs = {"Alem�n","�rabe","Catal�n","Checo",
      "Chino","Coreano",
      "Dan�s","Espa�ol","Finland�s","Franc�s","Holand�s","H�ngaro","Ingl�s",
      "Italiano","Japon�s","Noruego","Polaco","Portugu�s","Ruso",
      "Sueco","Tailand�s"};

  private final String [] idiomIn = {"Arab","Catalan","Chinese","Czech",
      "Danish","Dutch","English",
      "Finnish","French","German","Hungarian","Italian","Japanese","Korean",
      "Norwegian","Polish","Portuguese","Russian","Spanish","Swedish","Thai"};

  public OpenWiki( final Frame frame, final String title, final boolean modal ) {
    super( frame, title, modal );
    initialize();
  }

  public OpenWiki() {
    this( null, "", false );
  }

  private final void initialize() { // throws Exception
    panel.setLayout( null );

    labelOriginal.setText( getString( "LBL.SOURCE.URL" ) );
    labelOriginal.setBounds( new Rectangle( 5, 10, 200, 15 ) );

    fieldOriginal.setText( "" );
    fieldOriginal.setBounds( new Rectangle( 5, 30, 300, 22 ) );

    labelTranslation.setText( getString( "LBL.TARGET.URL" ) );
    labelTranslation.setBounds( new Rectangle( 5, 55, 200, 15 ) );

    fieldTranslation.setText( "" );
    fieldTranslation.setBounds( new Rectangle( 5, 75, 300, 22 ) );

    setLocalizedText( buttonOk, getString( "BTN.OK" ) );
    buttonOk.addActionListener( this );
    buttonOk.setBounds( new Rectangle( 205, 115, 100, 22 ) );

    setLocalizedText( buttonCancel, getString( "BTN.CANCEL" ) );
    buttonCancel.addActionListener( this );
    buttonCancel.setBounds( new Rectangle( 310, 115, 100, 22 ) );

    addWindowListener( new WindowAdapter() {
        @Override
        public void windowClosing( final WindowEvent evt ) {
          onClose();
        }
    } );

    setModal( true );
    setResizable( false );
    setTitle( getString( "DLG.OPENWIKI.TITLE" ) );
    getContentPane().setLayout( null );

    comboOriginalLang.setToolTipText( getString( "CB.LANG.SOURCE.TOOLTIP" ) );
    comboOriginalLang.setSelectedItem( Locale.getDefault().getDisplayLanguage() );
    comboOriginalLang.setBounds( new Rectangle( 420, 30, 100, 22 ) );

    comboOriginalEncoding.removeItemAt( numEncodings - 1 );
    comboOriginalEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    comboOriginalEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    comboOriginalEncoding.setSelectedIndex( 0 );
    comboOriginalEncoding.setBounds( new Rectangle( 530, 30, 100, 22 ) );

    labelOriginalLang.setText( getString( "LBL.SOURCE.LANG" ) );
    labelOriginalLang.setBounds( new Rectangle( 420, 10, 100, 16 ) );

    labelOriginalEncoding.setText( getString( "LBL.ENCODING" ) );
    labelOriginalEncoding.setBounds( new Rectangle( 530, 10, 100, 16 ) );

    comboTranslationLang.setToolTipText( getString( "CB.LANG.TARGET.TOOLTIP" ) );
    comboTranslationLang.setSelectedItem( Locale.getDefault().getDisplayLanguage() );
    comboTranslationLang.setBounds( new Rectangle( 420, 75, 100, 22 ) );

    comboTranslationEncoding.removeItemAt( numEncodings - 1 );
    comboTranslationEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    comboTranslationEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    comboTranslationEncoding.setSelectedIndex( 0 );
    comboTranslationEncoding.setBounds( new Rectangle( 530, 75, 100, 22 ) );

    labelTranslationLang.setText( getString( "LBL.TARGET.LANG" ) );
    labelTranslationLang.setBounds( new Rectangle( 420, 55, 100, 16 ) );

    labelTranslationEncoding.setText( getString( "LBL.ENCODING" ) );
    labelTranslationEncoding.setBounds( new Rectangle( 530, 55, 100, 16 ) );

    panel.setBounds( new Rectangle( -1, 0, 640, 180 ) );

    panel.add( fieldTranslation, null );
    panel.add( fieldOriginal, null );

    panel.add( comboOriginalLang, null );
    panel.add( comboTranslationLang, null );
    panel.add( comboOriginalEncoding, null );
    panel.add( comboTranslationEncoding, null );

    panel.add( buttonCancel, null );
    panel.add( buttonOk, null );

    panel.add( labelOriginalLang, null );
    panel.add( labelOriginalEncoding, null );
    panel.add( labelTranslationEncoding, null );
    panel.add( labelOriginal, null );
    panel.add( labelTranslationLang, null );
    panel.add( labelTranslation, null );

    getContentPane().add( panel, null );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds( ( screenSize.width - 640 ) / 2,
        ( screenSize.height - 180 ) / 2, 640, 180 );
  }

  /**
   * get source locale.
   * 
   * @return locale
   */
  public final String getSourceLocale() {
    return ( originalLanguage );
  }
  
  /**
   * get translation locale.
   *
   * @return locale
   */
  public final String getTargetLocale() {
    return ( translationLanguage );
  }

  public final JComboBox getSourceLangEncComboBox() {
    return ( comboOriginalEncoding );
  }
  
  public final JComboBox getTargetLangEncComboBox() {
    return ( comboTranslationEncoding );
  }

  private void onOk() {
    boolean errorOrig = true;

    try {
      if ( fieldOriginal.getText() != null ) {
        //  FixMe: this is only used for the side-effect of throwing an exception
        //  should check for existence of file instead!!!
        final FileReader fr = new FileReader( fieldOriginal.getText() );
        fr.close();
        stringOriginal = fieldOriginal.getText();
        originalFilePath = new File( stringOriginal );
        errorOrig = false;
      }

      if ( fieldTranslation.getText() != null ) {
        //  FixMe: this is only used for the side-effect of throwing an exception
        final FileReader fr = new FileReader( fieldTranslation.getText() );
        fr.close();
        stringTranslation = fieldTranslation.getText();
        translationFilePath = new File( stringTranslation );
        setVisible( false );
      } else {
        setVisible( false );
      }
    } catch ( final IOException ex ) {
      JOptionPane.showMessageDialog( panel, getString( "MSG_ERROR_FILE_NOTFOUND" ),
          getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );
      if ( errorOrig ) {
        fieldOriginal.setText( "" );
      } else {
        fieldTranslation.setText( "" );
      }
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
   * Action handler.
   * 
   * @param action performed
   */
  @Override
  public final void actionPerformed( final ActionEvent action ) {
    final Object actor = action.getSource();
    if ( actor instanceof JButton ) {
      if ( actor == buttonCancel ) {
        onCancel();
      } else if ( actor == buttonOk ) {
        onOk();
      }
    }
  }
}



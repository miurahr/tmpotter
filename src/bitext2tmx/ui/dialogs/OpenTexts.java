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


@SuppressWarnings("serial")
public final class OpenTexts extends JDialog implements ActionListener {

  private final JPanel  panel = new JPanel();

  private final JLabel      labelOriginal  = new JLabel();
  private final JTextField  fieldOriginal  = new JTextField();
  private final JButton     buttonOriginal = new JButton();

  private final JLabel      labelTranslation  = new JLabel();
  private final JTextField  fieldTranslation  = new JTextField();
  private final JButton     buttonTranslation = new JButton();

  private final JButton  buttonOk     = new JButton();
  private final JButton  buttonCancel = new JButton();

  private final JLabel  labelEncoding            = new JLabel();
  private final JLabel  labelOriginalEncoding    = new JLabel();
  private final JLabel  labelTranslationEncoding = new JLabel();

  private File     filePath;
  private File     originalFilepath;
  private File     translationFilePath;
  private String   originalDoc;
  private String   translationDoc;
  private boolean  closed;

  public final File getPath() {
    return ( filePath );
  }
  
  public final void setPath( final File filePath ) {
    this.filePath = filePath;
  }

  public final File getSourcePath() {
    return ( originalFilepath );
  }
  
  public final File getTargetPath() {
    return ( translationFilePath );
  }

  public final boolean isClosed() {
    return ( closed );
  }

  public final String getSource() {
    return ( originalDoc );
  }
  
  public final String getTarget() {
    return ( translationDoc );
  }


  private final JComboBox  comboOriginalLang    = new JComboBox();
  private final JComboBox  comboTranslationLang = new JComboBox();
  private final JLabel     labelOriginalLang    = new JLabel();
  private final JLabel     labelTranslationLang = new JLabel();

  private String originalLang;
  private String translationLang;

  public File userPathFile = new File( System.getProperty( "user.dir" ) );

  private final JComboBox comboOriginalEncoding    = new JComboBox( AppConstants
          .straEncodings );
  private final JComboBox comboTranslationEncoding = new JComboBox( AppConstants
          .straEncodings );
  private final int numEncodings = AppConstants.straEncodings.length;

  private final String [] idiomCa = {"Alemany","Angl�s","�rab","Catal��",
      "Txec","Core��",
      "Dan�s","Espanyol","Finland�s","Franc�s","Holand�s","Hongar�s",
      "Itali��","Japon�s","Noruec","Polon�s","Portugu�s","Rus","Suec",
      "Tailand�s","Xin�s"};

  private final String [] idiomEs = {"Alem�n","�rabe","Catal�n","Checo",
      "Chino","Coreano",
      "Dan�s","Espa�ol","Finland�s","Franc�s","Holand�s","H�ngaro","Ingl�s",
      "Italiano","Japon�s","Noruego","Polaco","Portugu�s","Ruso","Sueco",
      "Tailand�s"};

  private final String [] idiomIn = {"Arab","Catalan","Chinese","Czech",
      "Danish","Dutch","English",
      "Finnish","French","German","Hungarian","Italian","Japanese","Korean",
      "Norwegian","Polish","Portuguese","Russian","Spanish","Swedish","Thai"};

  /**
   * Constructor.
   * 
   * @param frame parent frame
   * @param title dialog title
   * @param modal is modal?
   */
  public OpenTexts( final Frame frame, final String title,
          final boolean modal ) {
    super( frame, title, modal );

    initialize();
  }

  public OpenTexts() {
    this( null, "", false );
  }

  private void initialize() { // throws Exception
    //  ToDo: remove - use preferences
    optionsConfiguration();

    panel.setLayout( null );

    labelOriginal.setText( getString( "LBL.SOURCE.FILE" ) );
    labelOriginal.setBounds( new Rectangle( 5, 10, 200, 15 ) );

    fieldOriginal.setText( "" );
    fieldOriginal.setBounds( new Rectangle( 5, 30, 300, 22 ) );

    //_btnOriginal.setText( l10n( "BTN.BROWSE" ) );
    setLocalizedText( buttonOriginal, getString( "BTN.BROWSE.ORIGINAL" ) );
    buttonOriginal.addActionListener( this );
    buttonOriginal.setBounds( new Rectangle( 310, 30, 100, 22 ) );

    //_btnTranslation.setText( l10n( "BTN.BROWSE" ) );
    setLocalizedText( buttonTranslation, getString( "BTN.BROWSE.TRANSLATION" ) );
    buttonTranslation.addActionListener( this );
    buttonTranslation.setEnabled( false );
    buttonTranslation.setBounds( new Rectangle( 310, 75, 100, 22 ) );

    labelTranslation.setText( getString( "LBL.TARGET.FILE" ) );
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
      public void windowClosing( final WindowEvent evt ) {
        onClose();
      }
    } );

    setModal( true );
    setResizable( false );
    setTitle( getString( "DLG.OPEN.TITLE" ) );
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
    comboTranslationLang.setEnabled( false );

    comboTranslationEncoding.removeItemAt( numEncodings - 1 );
    comboTranslationEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    comboTranslationEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    comboTranslationEncoding.setSelectedIndex( 0 );
    comboTranslationEncoding.setBounds( new Rectangle( 530, 75, 100, 22 ) );
    comboTranslationEncoding.setEnabled( false );

    labelTranslation.setEnabled( false );

    labelTranslationLang.setText( getString( "LBL.TARGET.LANG" ) );
    labelTranslationLang.setBounds( new Rectangle( 420, 55, 100, 16 ) );
    labelTranslationLang.setEnabled( false );

    labelTranslationEncoding.setText( getString( "LBL.ENCODING" ) );
    labelTranslationEncoding.setBounds( new Rectangle( 530, 55, 100, 16 ) );

    panel.setBounds( new Rectangle( -1, 0, 640, 180 ) );

    panel.add( fieldTranslation, null );
    panel.add( fieldOriginal, null );
    panel.add( buttonOriginal, null );
    panel.add( buttonTranslation, null );

    panel.add(comboOriginalLang, null );
    panel.add(comboTranslationLang, null );
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

    getContentPane().add(panel, null );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds( ( screenSize.width - 640 ) / 2,
        ( screenSize.height - 180 ) / 2, 640, 180 );
  }

  /**
   *  Funci�n Opciones_Configuracion.
   *  Esta funci�n lee las opciones de configuraci�n del idioma.
   *  Por defecto: Ingl�s.
   *  kIDIOMA :0->Catal�n
   *           1->Espa�ol
   *           2->Ingl�s
   */
  final void optionsConfiguration() {
    //  All this should go out to resource bundles
    final String langIso = Locale.getDefault().getLanguage();

    if ( langIso.equals( "ca" ) ) {
      for ( int cont = 0; cont < idiomCa.length; cont++ ) {
        comboOriginalLang.addItem( idiomCa[cont] );
        comboTranslationLang.addItem( idiomCa[cont] );
      }
    } else if ( langIso.equals( "en" ) ) {
      for ( int cont = 0; cont < idiomIn.length; cont++ ) {
        comboOriginalLang.addItem( idiomIn[cont] );
        comboTranslationLang.addItem( idiomIn[cont] );
      }
    } else if ( langIso.equals( "es" ) ) {
      for ( int cont = 0; cont < idiomEs.length; cont++ ) {
        comboOriginalLang.addItem( idiomEs[cont] );
        comboTranslationLang.addItem( idiomEs[cont] );
      }
    } else {
      for ( int cont = 0; cont < idiomIn.length; cont++ ) {
        comboOriginalLang.addItem( idiomIn[cont] );
        comboTranslationLang.addItem( idiomIn[cont] );
      }
    }
  }

  public final String getSourceLocale() {
    return ( originalLang );
  }
  
  public final String getTargetLocale() {
    return ( translationLang ); 
  }

  public final JComboBox getSourceLangEncComboBox() {
    return ( comboOriginalEncoding );
  }
  
  public final JComboBox getTargetLangEncComboBox() {
    return ( comboTranslationEncoding );
  }

  private void onOriginal() {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory( filePath );

    fc.setMultiSelectionEnabled( false );
    final int returnVal = fc.showOpenDialog(panel );
    filePath = fc.getCurrentDirectory();

    if ( returnVal == JFileChooser.APPROVE_OPTION ) {
      originalFilepath = fc.getSelectedFile();

      if ( fc.getName( originalFilepath ).endsWith( ".txt" ) ) {
        if ( originalFilepath.exists() ) {
          originalDoc = fc.getName( originalFilepath );
          fieldOriginal.setText( originalFilepath.getPath() );
          labelTranslation.setEnabled( true );
          buttonTranslation.setEnabled( true );
          comboTranslationEncoding.setEnabled( true );
          labelTranslationLang.setEnabled( true );
          comboTranslationLang.setEnabled( true );
        } else {
          JOptionPane.showMessageDialog(panel,
              getString( "MSG_ERROR_FILE_NOTFOUND" ),
              getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );
          fieldOriginal.setText( "" );
        }
      }
      //  ToDo: remember filename by preferences
    }
  }

  private void onTranslation() {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory( filePath );

    fc.setMultiSelectionEnabled( false );
    final int returnVal = fc.showOpenDialog(panel );
    filePath = fc.getCurrentDirectory();

    if ( returnVal == JFileChooser.APPROVE_OPTION ) {
      translationFilePath = fc.getSelectedFile();

      if ( fc.getName( translationFilePath ).endsWith( ".txt" ) ) {
        if ( translationFilePath.exists() ) {
          translationDoc = fc.getName( translationFilePath );
          fieldTranslation.setText( translationFilePath.getPath() );
        } else {
          JOptionPane.showMessageDialog(panel,
                  getString( "MSG_ERROR_FILE_NOTFOUND" ),
                  getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );

          fieldTranslation.setText( "" );
        }
      }
      //  ToDo: remember filename by preferences
    }
  }

  private void onOk() {
    boolean errorOrig = true;

    try {
      if ( fieldOriginal.getText() != null ) {
        //  FixMe: this is only used for the side-effect of throwing an exception
        //  should check for existence of file instead!!!
        final FileReader fr = new FileReader( fieldOriginal.getText() );
        fr.close();
        originalDoc = fieldOriginal.getText();
        originalFilepath = new File( originalDoc );
        setLanguageCode( true );
        errorOrig = false;
      }
      if ( fieldTranslation.getText() != null ) {
        //  FixMe: this is only used for the side-effect of throwing an exception
        final FileReader fr = new FileReader( fieldTranslation.getText() );
        fr.close();
        translationDoc = fieldTranslation.getText();
        translationFilePath = new File( translationDoc );
        setLanguageCode( false );
        setVisible( false );
      }
    } catch ( final IOException ex ) {
      JOptionPane.showMessageDialog(panel, getString( "MSG_ERROR_FILE_NOTFOUND" ),
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
   * set language code.
   * 
   * @param originalFlag indicate original or translation.
   */
  final void setLanguageCode( final boolean originalFlag ) {
    String strLang;

    strLang = originalFlag ? comboOriginalLang.getSelectedItem().toString() :
      comboTranslationLang.getSelectedItem().toString();

    if ( strLang.equals( "Alemany" ) || strLang.equals( "Aleman" )
        || strLang.equals( "German" ) ) {
      if ( originalFlag ) {
        originalLang = "de";
      } else {
        translationLang = "de";
      }
    } else if ( strLang.equals( "�rab" ) || strLang.equals( "�rabe" )
        || strLang.equals( "Arab" ) ) {
      if ( originalFlag ) {
        originalLang = "ar";
      } else {
        translationLang = "ar";
      }
    } else if ( strLang.equals( "Catal�" ) || strLang.equals( "Catal�n" )
          || strLang.equals( "Catalan" ) ) {
      if ( originalFlag ) {
        originalLang = "ca";
      } else {
        translationLang = "ca";
      }
    } else if ( strLang.equals( "Espanyol" ) || strLang.equals( "Espa�ol" )
        || strLang.equals( "Spanish" ) ) {
      if ( originalFlag ) {
        originalLang = "es";
      } else {
        translationLang = "es";
      }
    } else if ( strLang.equals( "Angl�s" ) || strLang.equals( "Ingl�s" )
            || strLang.equals( "English" ) ) {
      if ( originalFlag ) {
        originalLang = "en";
      } else {
        translationLang = "en";
      }
    } else if ( strLang.equals( "Checo" ) || strLang.equals( "Checo" )
        || strLang.equals( "Czech" ) ) {
      if ( originalFlag ) {
        originalLang = "cs";
      } else {
        translationLang = "cs";
      }
    } else if ( strLang.equals( "Xin�s" ) || strLang.equals( "Chino" )
         || strLang.equals( "Chinese" ) ) {
      if ( originalFlag ) {
        originalLang = "zh";
      } else {
        translationLang = "zh";
      }
    } else if ( strLang.equals( "Core�" ) || strLang.equals( "Coreano" )
         ||  strLang.equals( "Korean" ) ) {
      if ( originalFlag ) {
        originalLang = "ko";
      } else {
        translationLang = "ko";
      }
    } else if ( strLang.equals( "Dan�s" ) || strLang.equals( "Dan�s" )
        || strLang.equals( "Danish" ) ) {
      if ( originalFlag ) {
        originalLang = "da";
      } else {
        translationLang = "da";
      }
    } else if ( strLang.equals( "Finland�s" ) || strLang.equals( "Finland�s" )
            || strLang.equals( "Finnish" ) ) {
      if ( originalFlag ) {
        originalLang = "fi";
      } else {
        translationLang = "fi";
      }
    } else if ( strLang.equals( "Franc�s" ) || strLang.equals( "Franc�s" )
            || strLang.equals( "French" ) ) {
      if ( originalFlag ) {
        originalLang = "fr";
      } else {
        translationLang = "fr";
      }
    } else if ( strLang.equals( "Holand�s" ) || strLang.equals( "Holand�s" )
            || strLang.equals( "Dutch" ) ) {
      if ( originalFlag ) {
        originalLang = "nl";
      } else  {
        translationLang = "nl";
      }
    } else if ( strLang.equals( "Hongar�s" ) || strLang.equals( "H�ngaro" )
            || strLang.equals( "Hungarian" ) ) {
      if ( originalFlag ) {
        originalLang = "hu";
      } else {
        translationLang = "hu";
      }
    } else if ( strLang.equals( "Itali�" ) || strLang.equals( "Italiano" )
            || strLang.equals( "Italian" ) ) {
      if ( originalFlag ) {
        originalLang = "it";
      } else {
        translationLang = "it";
      }
    } else if ( strLang.equals( "Japon�s" ) || strLang.equals( "Japon�s" )
        || strLang.equals( "Japanese" ) ) {
      if ( originalFlag ) {
        originalLang = "ja";
      } else {
        translationLang = "ja";
      }
    } else if ( strLang.equals( "Noruec" ) || strLang.equals( "Noruego" )
        || strLang.equals( "Norwegian" ) ) {
      if ( originalFlag ) {
        originalLang = "no";
      } else  {
        translationLang = "no";
      }
    } else if ( strLang.equals( "Polon�s" ) || strLang.equals( "Polaco" ) 
            || strLang.equals( "Polish" ) ) {
      if ( originalFlag ) {
        originalLang = "pl";
      } else {
        translationLang = "pl";
      }
    } else if ( strLang.equals( "Portugu�s" ) || strLang.equals( "Portugu�s" )
            || strLang.equals( "Portuguese" ) ) {
      if ( originalFlag ) {
        originalLang = "pt";
      } else {
        translationLang = "pt";
      }
    } else if ( strLang.equals( "Rus" ) || strLang.equals( "Ruso" )
        || strLang.equals( "Russian" ) ) {
      if ( originalFlag ) {
        originalLang = "ru";
      } else {
        translationLang = "ru";
      }
    } else if ( strLang.equals( "Suec" ) || strLang.equals( "Sueco" )     
        || strLang.equals( "Swedish" ) ) {
      if ( originalFlag ) {
        originalLang = "sv";
      } else    {
        translationLang = "sv";
      }
    } else if ( strLang.equals( "Tailand�s" ) 
            || strLang.equals( "Tailand�s" )
            || strLang.equals( "Thai" ) ) {
      if ( originalFlag ) {
        originalLang = "th";
      } else {
        translationLang = "th";
      }
    }
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
      } else if ( actor == buttonOriginal ) {
        onOriginal();
      } else if ( actor == buttonTranslation ) {
        onTranslation();
      }
    }
  }

}



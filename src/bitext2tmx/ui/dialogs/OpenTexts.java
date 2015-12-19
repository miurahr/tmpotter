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

package bitext2tmx.ui.dialogs;

import java.awt.Frame;
import java.awt.Dimension;
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

import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.util.AppConstants;
import static bitext2tmx.util.Localization.getString;


final public class OpenTexts extends JDialog implements ActionListener
{
  /**
   *
   */
  final private static long serialVersionUID = -4724307632488696250L;

  final private JPanel  _pnl = new JPanel();

  final private JLabel      _lblOriginal = new JLabel();
  final private JTextField  _tfdOriginal = new JTextField();
  final private JButton     _btnOriginal = new JButton();

  final private JLabel      _lblTranslation = new JLabel();
  final private JTextField  _tfdTranslation = new JTextField();
  final private JButton     _btnTranslation = new JButton();

  final private JButton  _btnOK     = new JButton();
  final private JButton  _btnCancel = new JButton();

  final private JLabel  _lblEncoding            = new JLabel();
  final private JLabel  _lblOriginalEncoding    = new JLabel();
  final private JLabel  _lblTranslationEncoding = new JLabel();

  private File     _fPath;
  private File     _fOriginalPath;
  private File     _fTranslationPath;
  private String   _strOriginal;
  private String   _strTranslation;
  private boolean  _bClosed;
  private int      _type;  // = 0;  // 0: text (default); 1: tmx

  final public File getPath() { return( _fPath ); }
  final public void setPath( final File fPath ) { _fPath = fPath; }

  final public File getSourcePath() { return( _fOriginalPath ); }
  final public File getTargetPath() { return( _fTranslationPath ); }

  final public boolean isClosed() { return( _bClosed ); }

  final public String getSource() { return( _strOriginal ); }
  final public String getTarget() { return( _strTranslation ); }

  final public int getTypes()  { return( _type ); }

  final private JComboBox  _cbxOriginalLang    = new JComboBox();
  final private JComboBox  _cbxTranslationLang = new JComboBox();
  final private JLabel     _lblOriginalLang    = new JLabel();
  final private JLabel     _lblTranslationLang = new JLabel();

  private String _strOriginalLang;
  private String _strTranslationLang;

  public File _fUserPath = new File( System.getProperty( "user.dir" ) );

  //private int kIDIOMA = 2;

  final private JComboBox _cbxOriginalEncoding    = new JComboBox( AppConstants.straEncodings );
  final private JComboBox _cbxTranslationEncoding = new JComboBox( AppConstants.straEncodings );
  final private int numEncodings = AppConstants.straEncodings.length;

  final private String [] IdiomasCa = {"Alemany","Angl�s","�rab","Catal��","Txec","Core��",
      "Dan�s","Espanyol","Finland�s","Franc�s","Holand�s","Hongar�s",
      "Itali��","Japon�s","Noruec","Polon�s","Portugu�s","Rus","Suec","Tailand�s","Xin�s"};

  final private String [] IdiomasEs = {"Alem�n","�rabe","Catal�n","Checo","Chino","Coreano",
      "Dan�s","Espa�ol","Finland�s","Franc�s","Holand�s","H�ngaro","Ingl�s",
      "Italiano","Japon�s","Noruego","Polaco","Portugu�s","Ruso","Sueco","Tailand�s"};

  final private String [] IdiomasIn = {"Arab","Catalan","Chinese","Czech","Danish","Dutch","English",
      "Finnish","French","German","Hungarian","Italian","Japanese","Korean",
      "Norwegian","Polish","Portuguese","Russian","Spanish","Swedish","Thai"};

  public OpenTexts( final Frame frame, final String title, final boolean modal )
  {
    super( frame, title, modal );

    initialize();
  }

  public OpenTexts() { this( null, "", false ); }

  final private void initialize()// throws Exception
  {
    //  ToDo: remove - use preferences
    Opciones_Configuracion();

    _pnl.setLayout( null );

    _lblOriginal.setText( getString( "LBL.SOURCE.FILE" ) );
    _lblOriginal.setBounds( new Rectangle( 5, 10, 200, 15 ) );

    _tfdOriginal.setText( "" );
    _tfdOriginal.setBounds( new Rectangle( 5, 30, 300, 22 ) );

    //_btnOriginal.setText( l10n( "BTN.BROWSE" ) );
    setLocalizedText( _btnOriginal, getString( "BTN.BROWSE.ORIGINAL" ) );
    _btnOriginal.addActionListener( this );
    _btnOriginal.setBounds( new Rectangle( 310, 30, 100, 22 ) );

    //_btnTranslation.setText( l10n( "BTN.BROWSE" ) );
    setLocalizedText( _btnTranslation, getString( "BTN.BROWSE.TRANSLATION" ) );
    _btnTranslation.addActionListener( this );
    _btnTranslation.setEnabled( false );
    _btnTranslation.setBounds( new Rectangle( 310, 75, 100, 22 ) );

    _lblTranslation.setText( getString( "LBL.TARGET.FILE" ) );
    _lblTranslation.setBounds( new Rectangle( 5, 55, 200, 15 ) );

    _tfdTranslation.setText( "" );
    _tfdTranslation.setBounds( new Rectangle( 5, 75, 300, 22 ) );

    setLocalizedText( _btnOK, getString( "BTN.OK" ) );
    _btnOK.addActionListener( this );
    _btnOK.setBounds( new Rectangle( 205, 115, 100, 22 ) );

    setLocalizedText( _btnCancel, getString( "BTN.CANCEL" ) );
    _btnCancel.addActionListener( this );
    _btnCancel.setBounds( new Rectangle( 310, 115, 100, 22 ) );

    addWindowListener( new WindowAdapter()
      { public void windowClosing( final WindowEvent evt ) { onClose(); } } );

    setModal( true );
    setResizable( false );
    setTitle( getString( "DLG.OPEN.TITLE" ) );
    getContentPane().setLayout( null );

    _cbxOriginalLang.setToolTipText( getString( "CB.LANG.SOURCE.TOOLTIP" ) );
    _cbxOriginalLang.setSelectedItem( Locale.getDefault().getDisplayLanguage() );
    _cbxOriginalLang.setBounds( new Rectangle( 420, 30, 100, 22 ) );

    _cbxOriginalEncoding.removeItemAt( numEncodings - 1 );
    _cbxOriginalEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    _cbxOriginalEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    _cbxOriginalEncoding.setSelectedIndex( 0 );
    _cbxOriginalEncoding.setBounds( new Rectangle( 530, 30, 100, 22 ) );

    _lblOriginalLang.setText( getString( "LBL.SOURCE.LANG" ) );
    _lblOriginalLang.setBounds( new Rectangle( 420, 10, 100, 16 ) );

    _lblOriginalEncoding.setText( getString( "LBL.ENCODING" ) );
    _lblOriginalEncoding.setBounds( new Rectangle( 530, 10, 100, 16 ) );

    _cbxTranslationLang.setToolTipText( getString( "CB.LANG.TARGET.TOOLTIP" ) );
    _cbxTranslationLang.setSelectedItem( Locale.getDefault().getDisplayLanguage() );
    _cbxTranslationLang.setBounds( new Rectangle( 420, 75, 100, 22 ) );
    _cbxTranslationLang.setEnabled( false );

    _cbxTranslationEncoding.removeItemAt( numEncodings - 1 );
    _cbxTranslationEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    _cbxTranslationEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    _cbxTranslationEncoding.setSelectedIndex( 0 );
    _cbxTranslationEncoding.setBounds( new Rectangle( 530, 75, 100, 22 ) );
    _cbxTranslationEncoding.setEnabled( false );

    _lblTranslation.setEnabled( false );

    _lblTranslationLang.setText( getString( "LBL.TARGET.LANG" ) );
    _lblTranslationLang.setBounds( new Rectangle( 420, 55, 100, 16 ) );
    _lblTranslationLang.setEnabled( false );

    _lblTranslationEncoding.setText( getString( "LBL.ENCODING" ) );
    _lblTranslationEncoding.setBounds( new Rectangle( 530, 55, 100, 16 ) );

    _pnl.setBounds( new Rectangle( -1, 0, 640, 180 ) );

    _pnl.add( _tfdTranslation, null );
    _pnl.add( _tfdOriginal, null );
    _pnl.add( _btnOriginal, null );
    _pnl.add( _btnTranslation, null );

    _pnl.add( _cbxOriginalLang, null );
    _pnl.add( _cbxTranslationLang, null );
    _pnl.add( _cbxOriginalEncoding, null );
    _pnl.add( _cbxTranslationEncoding, null );

    _pnl.add( _btnCancel, null );
    _pnl.add( _btnOK, null );

    _pnl.add( _lblOriginalLang, null );
    _pnl.add( _lblOriginalEncoding, null );
    _pnl.add( _lblTranslationEncoding, null );
    _pnl.add( _lblOriginal, null );
    _pnl.add( _lblTranslationLang, null );
    _pnl.add( _lblTranslation, null );

    getContentPane().add( _pnl, null );

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
  final void Opciones_Configuracion()
  {
    String linea = "";
    //  All this should go out to resource bundles
    final String strLangISO = Locale.getDefault().getLanguage();


    if( strLangISO.equals( "ca" ) )
      for( int cont = 0; cont < IdiomasCa.length; cont++ )
      {
        _cbxOriginalLang.addItem( IdiomasCa[cont] );
        _cbxTranslationLang.addItem( IdiomasCa[cont] );
      }
    else if ( strLangISO.equals( "en" ) )
      for( int cont = 0; cont < IdiomasIn.length; cont++ )
      {
        _cbxOriginalLang.addItem( IdiomasIn[cont] );
        _cbxTranslationLang.addItem( IdiomasIn[cont] );
      }
    else if ( strLangISO.equals( "es" ) )
      for( int cont = 0; cont < IdiomasEs.length; cont++ )
      {
        _cbxOriginalLang.addItem( IdiomasEs[cont] );
        _cbxTranslationLang.addItem( IdiomasEs[cont] );
      }
    else
      for( int cont = 0; cont < IdiomasIn.length; cont++ )
      {
        _cbxOriginalLang.addItem( IdiomasIn[cont] );
        _cbxTranslationLang.addItem( IdiomasIn[cont] );
      }
  }

  final public String getSourceLocale() { return( _strOriginalLang ); }
  final public String getTargetLocale() { return( _strTranslationLang ); }

  final public JComboBox getSourceLangEncComboBox() { return( _cbxOriginalEncoding ); }
  final public JComboBox getTargetLangEncComboBox() { return( _cbxTranslationEncoding ); }


  /**Bot�n Examinar... Fuente / Meta
   * Abre un cuadro de di�logo para seleccionar el fichero fuente o el fichero meta
   * @param e :contiene la acci�n del evento.
   */

  private void onOriginal()
  {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory( _fPath );

/*
    switch( kIDIOMA )
    {
      case 1:
        fc.setLocale( new Locale( "ca", "ES" ) );
        fc.setDialogTitle( "Obrir..." );
        break;
      case 2:
        fc.setLocale( new Locale( "es", "ES", "Traditional_WIN" ) );
        fc.setDialogTitle( "Abrir..." );
        break;
      case 3:  //  English -fall through to default -RM
      default:
        fc.setLocale( new Locale( "en", "US" ) );
        fc.setDialogTitle( "Open..." );
    }
*/
    fc.setMultiSelectionEnabled( false );
    final int returnVal = fc.showOpenDialog( _pnl );
    _fPath = fc.getCurrentDirectory();

    if( returnVal == JFileChooser.APPROVE_OPTION )
    {
      _fOriginalPath = fc.getSelectedFile();

      if( fc.getName( _fOriginalPath ).endsWith( ".txt" ) ||
        fc.getName( _fOriginalPath ).endsWith( ".tmx" ) )
      {
        if( _fOriginalPath.exists() )
        {
          _strOriginal = fc.getName( _fOriginalPath );
          _tfdOriginal.setText( _fOriginalPath.getPath() );

          if( fc.getName( _fOriginalPath ).endsWith( ".txt" ) )
          {
            _lblTranslation.setEnabled( true );
            _btnTranslation.setEnabled( true );
            _cbxTranslationEncoding.setEnabled( true );
            _lblTranslationLang.setEnabled( true );
            _cbxTranslationLang.setEnabled( true );

          }
          else _type = 1;  // TMX document
        }
        else
        {
          JOptionPane.showMessageDialog( _pnl, getString( "MSG_ERROR_FILE_NOTFOUND" ),
            getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );

          _tfdOriginal.setText( "" );
        }
      }
      /*
      //  ToDo: remove - replace with preferences
      try{
         final FileWriter wr = new FileWriter( _fUserPath + "/config.txt" );
         final BufferedWriter bw = new BufferedWriter( wr );
         final PrintWriter pw = new PrintWriter( bw );

         pw.println( "2" );
         pw.println( _fPath.getAbsolutePath() );
         pw.close();
       }
       catch( final IOException ex2 )
       {
         System.out.println( "ERROR al crear el config" );
         System.exit( 0 );
       }
       */
    }
  }

  private void onTranslation()
  {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory( _fPath );

/*
    switch( kIDIOMA )
    {
      case 1:
        fc.setLocale( new Locale( "ca", "ES" ) );
        fc.setDialogTitle( "Obrir..." );
        break;
      case 2:
        fc.setLocale( new Locale( "es", "ES", "Traditional_WIN" ) );
        fc.setDialogTitle( "Abrir..." );
        break;
      case 3:  //  English - fall through to default -RM
      default:
        fc.setLocale( new Locale( "en", "US" ) );
        fc.setDialogTitle( "Open..." );
    }
*/
    fc.setMultiSelectionEnabled( false );
    final int returnVal = fc.showOpenDialog( _pnl );
    _fPath = fc.getCurrentDirectory();

    if( returnVal == JFileChooser.APPROVE_OPTION )
    {
      _fTranslationPath = fc.getSelectedFile();

      if( fc.getName( _fTranslationPath ).endsWith( ".txt" ) )
      {
        if( _fTranslationPath.exists() )
        {
          _strTranslation = fc.getName( _fTranslationPath );
          _tfdTranslation.setText( _fTranslationPath.getPath() );
          _type = 0;  // plain-text
        }
        else
        {
          JOptionPane.showMessageDialog( _pnl, getString( "MSG_ERROR_FILE_NOTFOUND" ),
            getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );

          _tfdTranslation.setText( "" );
        }

      }
      /*
      //  ToDo: remove - replace with preferences
      try{
         final FileWriter     wr = new FileWriter( _fUserPath + "/config.txt" );
         final BufferedWriter bw = new BufferedWriter( wr );
         final PrintWriter    pw = new PrintWriter( bw );

         pw.println( "2" );
         pw.println(_fPath.getAbsolutePath() );
         pw.close();
       }
       catch( final IOException ioe )
       {
         System.out.println( "ERROR al crear el config" );
         System.exit( 0 );
       }
       */
     }
  }

  private void onOK()
  {
    boolean bErrorOrig = true;

    try
    {
      if( _tfdOriginal.getText() != null )
      {
        //  FixMe: this is only used for the side-effect of throwing an exception
        //  should check for existence of file instead!!!
        final FileReader fr = new FileReader( _tfdOriginal.getText() );
        fr.close();
        _strOriginal = _tfdOriginal.getText();
        _fOriginalPath = new File( _strOriginal );
        setLanguageCode( true );
        bErrorOrig = false;
      }

      if( _type == 0 )
      {
        if( _tfdTranslation.getText() != null )
        {
          //  FixMe: this is only used for the side-effect of throwing an exception
          final FileReader fr = new FileReader( _tfdTranslation.getText() );
          fr.close();
          _strTranslation = _tfdTranslation.getText();
          _fTranslationPath = new File( _strTranslation );
          setLanguageCode( false );
          setVisible( false );
        }
      }
      else setVisible( false );

    }
    catch( final IOException ex )
    {
      JOptionPane.showMessageDialog( _pnl, getString( "MSG_ERROR_FILE_NOTFOUND" ),
        getString( "MSG_ERROR" ), JOptionPane.ERROR_MESSAGE );

      if( bErrorOrig ) _tfdOriginal.setText( "" );
      else _tfdTranslation.setText( "" );
    }
  }

  private void onCancel() { onClose(); }

  private void onClose()
  {
    _bClosed = true;
    setVisible( false );
    dispose();
  }


  /**
   */
  final void setLanguageCode( final boolean bOriginal )
  {
    String strLang = "";

    strLang = bOriginal ? _cbxOriginalLang.getSelectedItem().toString() :
      _cbxTranslationLang.getSelectedItem().toString();

    if( strLang.equals( "Alemany" ) || strLang.equals( "Aleman" ) ||
      strLang.equals( "German" ) )
    {
      if( bOriginal )  _strOriginalLang = "de";
      else           _strTranslationLang = "de";
    }
    else if( strLang.equals( "�rab" ) || strLang.equals( "�rabe" ) ||
      strLang.equals( "Arab" ) )
    {
      if( bOriginal )  _strOriginalLang = "ar";
      else           _strTranslationLang = "ar";
    }
    else if( strLang.equals( "Catal�" ) || strLang.equals( "Catal�n" ) ||
        strLang.equals( "Catalan" ) )
    {
      if( bOriginal )  _strOriginalLang = "ca";
      else           _strTranslationLang = "ca";
    }
    else if( strLang.equals( "Espanyol" ) || strLang.equals( "Espa�ol" ) ||
      strLang.equals( "Spanish" ) )
    {
      if( bOriginal )  _strOriginalLang = "es";
      else           _strTranslationLang = "es";
    }
    else if( strLang.equals( "Angl�s" ) || strLang.equals( "Ingl�s" ) ||
      strLang.equals( "English" ) )
    {
      if( bOriginal )  _strOriginalLang = "en";
      else           _strTranslationLang = "en";
    }
    else if( strLang.equals( "Checo" ) || strLang.equals( "Checo" ) ||
      strLang.equals( "Czech" ) )
    {
      if( bOriginal )  _strOriginalLang = "cs";
      else           _strTranslationLang = "cs";
    }
    else if( strLang.equals( "Xin�s" ) || strLang.equals( "Chino" ) ||
      strLang.equals( "Chinese" ) )
    {
      if( bOriginal )  _strOriginalLang = "zh";
      else           _strTranslationLang = "zh";
    }
    else if( strLang.equals( "Core�" ) || strLang.equals( "Coreano" ) ||
      strLang.equals( "Korean" ) )
    {
      if( bOriginal )  _strOriginalLang = "ko";
      else           _strTranslationLang = "ko";
    }
    else if( strLang.equals( "Dan�s" ) || strLang.equals( "Dan�s" ) ||
      strLang.equals( "Danish" ) )
    {
      if( bOriginal )  _strOriginalLang = "da";
      else           _strTranslationLang = "da";
    }
    else if( strLang.equals( "Finland�s" ) || strLang.equals( "Finland�s" ) ||
      strLang.equals( "Finnish" ) )
    {
      if( bOriginal )  _strOriginalLang = "fi";
      else           _strTranslationLang = "fi";
    }
    else if( strLang.equals( "Franc�s" ) || strLang.equals( "Franc�s" ) ||
      strLang.equals( "French" ) )
    {
      if( bOriginal )  _strOriginalLang = "fr";
      else           _strTranslationLang = "fr";
    }
    else if( strLang.equals( "Holand�s" ) || strLang.equals( "Holand�s" ) ||
      strLang.equals( "Dutch" ) )
    {
      if( bOriginal )  _strOriginalLang = "nl";
      else           _strTranslationLang = "nl";
    }
    else if( strLang.equals( "Hongar�s" ) || strLang.equals( "H�ngaro" ) ||
      strLang.equals( "Hungarian" ) )
    {
      if( bOriginal )  _strOriginalLang = "hu";
      else           _strTranslationLang = "hu";
    }
    else if( strLang.equals( "Itali�" ) || strLang.equals( "Italiano" ) ||
      strLang.equals( "Italian" ) )
    {
      if( bOriginal )  _strOriginalLang = "it";
      else           _strTranslationLang = "it";
    }
    else if( strLang.equals( "Japon�s" ) || strLang.equals( "Japon�s" ) ||
      strLang.equals( "Japanese" ) )
    {
      if( bOriginal )  _strOriginalLang = "ja";
      else           _strTranslationLang = "ja";
    }
    else if( strLang.equals( "Noruec" ) || strLang.equals( "Noruego" ) ||
      strLang.equals( "Norwegian" ) )
    {
      if( bOriginal )  _strOriginalLang = "no";
      else           _strTranslationLang = "no";
    }
    else if( strLang.equals( "Polon�s" ) || strLang.equals( "Polaco" ) ||
      strLang.equals( "Polish" ) )
    {
      if( bOriginal )  _strOriginalLang = "pl";
      else           _strTranslationLang = "pl";
    }
    else if( strLang.equals( "Portugu�s" ) || strLang.equals( "Portugu�s" ) ||
      strLang.equals( "Portuguese" ) )
    {
      if( bOriginal )  _strOriginalLang = "pt";
      else           _strTranslationLang = "pt";
    }
    else if( strLang.equals( "Rus" ) || strLang.equals( "Ruso" ) ||
      strLang.equals( "Russian" ) )
    {
      if( bOriginal )  _strOriginalLang = "ru";
      else           _strTranslationLang = "ru";
    }
    else if( strLang.equals( "Suec" ) || strLang.equals( "Sueco" ) ||
      strLang.equals( "Swedish" ) )
    {
      if( bOriginal )  _strOriginalLang = "sv";
      else           _strTranslationLang = "sv";
    }
    else if( strLang.equals( "Tailand�s" ) || strLang.equals( "Tailand�s" ) ||
      strLang.equals( "Thai" ) )
    {
      if( bOriginal )  _strOriginalLang = "th";
      else           _strTranslationLang = "th";
    }
  }

  final public void actionPerformed( final ActionEvent action )
  {
    final Object actor = action.getSource();

    if( actor instanceof JButton )
    {
      if( actor == _btnCancel )            onCancel();
      else if( actor == _btnOK )           onOK();
      else if( actor == _btnOriginal )     onOriginal();
      else if( actor == _btnTranslation )  onTranslation();
    }
  }

}//  OpenTexts{}



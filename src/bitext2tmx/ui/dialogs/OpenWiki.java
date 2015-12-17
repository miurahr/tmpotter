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


final public class OpenWiki extends JDialog implements ActionListener
{
  /**
   *
   */

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

  public OpenWiki( final Frame frame, final String title, final boolean modal )
  {
    super( frame, title, modal );

    initialize();
  }

  public OpenWiki() { this( null, "", false ); }

  final private void initialize()// throws Exception
  {
    _pnl.setLayout( null );

    _lblOriginal.setText( getString( "LBL.SOURCE.URL" ) );
    _lblOriginal.setBounds( new Rectangle( 5, 10, 200, 15 ) );

    _tfdOriginal.setText( "" );
    _tfdOriginal.setBounds( new Rectangle( 5, 30, 300, 22 ) );

    _lblTranslation.setText( getString( "LBL.TARGET.URL" ) );
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
    setTitle( getString( "DLG.OPENWIKI.TITLE" ) );
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

    _cbxTranslationEncoding.removeItemAt( numEncodings - 1 );
    _cbxTranslationEncoding.addItem( getString( "ENCODING.DEFAULT" ) );
    _cbxTranslationEncoding.setToolTipText( getString( "CB.ENCODING.TOOLTIP" ) );
    _cbxTranslationEncoding.setSelectedIndex( 0 );
    _cbxTranslationEncoding.setBounds( new Rectangle( 530, 75, 100, 22 ) );

    _lblTranslationLang.setText( getString( "LBL.TARGET.LANG" ) );
    _lblTranslationLang.setBounds( new Rectangle( 420, 55, 100, 16 ) );

    _lblTranslationEncoding.setText( getString( "LBL.ENCODING" ) );
    _lblTranslationEncoding.setBounds( new Rectangle( 530, 55, 100, 16 ) );

    _pnl.setBounds( new Rectangle( -1, 0, 640, 180 ) );

    _pnl.add( _tfdTranslation, null );
    _pnl.add( _tfdOriginal, null );

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

  final public String getSourceLocale() { return( _strOriginalLang ); }
  final public String getTargetLocale() { return( _strTranslationLang ); }

  final public JComboBox getSourceLangEncComboBox() { return( _cbxOriginalEncoding ); }
  final public JComboBox getTargetLangEncComboBox() { return( _cbxTranslationEncoding ); }


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
        bErrorOrig = false;
      }

      if( _tfdTranslation.getText() != null )
      {
          //  FixMe: this is only used for the side-effect of throwing an exception
          final FileReader fr = new FileReader( _tfdTranslation.getText() );
          fr.close();
          _strTranslation = _tfdTranslation.getText();
          _fTranslationPath = new File( _strTranslation );
          setVisible( false );
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

  final public void actionPerformed( final ActionEvent action )
  {
    final Object actor = action.getSource();

    if( actor instanceof JButton )
    {
      if( actor == _btnCancel )            onCancel();
      else if( actor == _btnOK )           onOK();
    }
  }

}//  OpenTexts{}



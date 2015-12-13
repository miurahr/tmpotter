/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006  Susana Santos Anton
#            (C) 2005-2009 Raymond: Martin et al.
#
#  Includes code: Copyright (C) 2002-2005 Keith Godfrey et al.
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

import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

import static org.openide.awt.Mnemonics.*;

import static bitext2tmx.util.Localization.*;


/**
 *  Dialog to select the fonts of bitext2tmx
 *
 */
final public class FontSelector extends JDialog
{
  public FontSelector( final Bitext2tmxWindow wndB2T, final Font[] afnt )
  {
    super( wndB2T, true );

    _wndB2T = wndB2T;

    initialize();

    getRootPane().setDefaultButton( _btnClose );

    _fntDialog = afnt[0];

    //  Current font style
    setFontStyle( _fntDialog.getStyle() );

    //  Conditionals test must be done in order
    if( getFontStyle() == (Font.BOLD + Font.ITALIC) )
      _cboxFontStyle.setSelectedIndex( 3 );
    //  Italic alone is equivalent to Plain + Italic
    else if( getFontStyle() == Font.ITALIC )
      _cboxFontStyle.setSelectedIndex( 1 );
    else if( getFontStyle() ==  Font.BOLD )
      _cboxFontStyle.setSelectedIndex( 2 );
    else if( getFontStyle() == Font.PLAIN )
      _cboxFontStyle.setSelectedIndex( 0 );
    else
      _cboxFontStyle.setSelectedIndex( 0 );

    _fontComboBox.setSelectedItem( _fntDialog.getName() );

    _sizeSpinner.setValue( new Integer( _fntDialog.getSize() ) );

    setFonts( _fntDialog );
  }


  final private void initialize()
  {
    final GridBagConstraints    gridBagConstraints;

    _buttonPanel         = new JPanel();
    _fontComboBox        = new JComboBox( _wndB2T.getFontFamilyNames() );
    _fontLabel           = new JLabel();
    _sizeSpinner         = new JSpinner();
    _sizeLabel           = new JLabel();
    _previewTextArea     = new JTextArea();

    _btnApply            = new JButton();
    _btnClose            = new JButton();

    _lblFontStyle        = new JLabel();
    _lblFontDisplayArea  = new JLabel();

    _cboxFontStyle       = new JComboBox( _astrFontStyles );
    _cboxFontDisplayArea = new JComboBox( _astrFontWindowAreas );


    getContentPane().setLayout( new GridBagLayout() );

    setTitle( getString( "DLG.FONTS.TITLE" ) );

    setModal( false );

    addWindowListener( new WindowAdapter()
      { public void windowClosing( final WindowEvent evt )
        { DialogClose(); } } );


    //  Build Dialog Grid Layout

    //  Default Constraints
    gridBagConstraints           = new GridBagConstraints();
    gridBagConstraints.fill      = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridwidth = 1;


    //  Top row 0 - Font Label & Combobox

    _fontLabel.setText( getString( "DLG.FONTS.LBL.SOURCE.FONT" ) );

    gridBagConstraints.gridx  = 0;
    gridBagConstraints.gridy  = 0;
    gridBagConstraints.fill   = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _fontLabel, gridBagConstraints );


    _fontComboBox.setMaximumRowCount( 10 );

    _fontComboBox.addActionListener( new ActionListener()
      { public void actionPerformed( final ActionEvent evt )
        { fontComboBoxActionPerformed( evt ); } } );

    _fontComboBox.setBackground( new Color( 255, 255,255 ) );

    gridBagConstraints.gridx   = 1;
    gridBagConstraints.gridy   = 0;
    gridBagConstraints.fill    = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets  = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _fontComboBox, gridBagConstraints );


    //  Row 2 - Font Style
    _lblFontStyle.setText( getString( "DLG.FONTS.LBL.STYLE" ) ); 

    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.0;

    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _lblFontStyle, gridBagConstraints );


    _cboxFontStyle.addActionListener( new ActionListener()
      { public void actionPerformed( final ActionEvent evt )
        { FontStyleComboBoxEvent( evt ); } } );


    gridBagConstraints.gridx   = 1;
    gridBagConstraints.gridy   = 1;
    gridBagConstraints.anchor  = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets  = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _cboxFontStyle, gridBagConstraints );


    //  Row 3 - Size Label & Spinner
    _sizeLabel.setText( getString( "DLG.FONTS.LBL.FONTSIZE" ) );

    gridBagConstraints.gridx  = 0;
    gridBagConstraints.gridy  = 2;
    gridBagConstraints.fill   = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.0;
    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _sizeLabel, gridBagConstraints );


    _sizeSpinner.addChangeListener( new ChangeListener()
      { public void stateChanged( final ChangeEvent evt )
        { sizeSpinnerStateChanged( evt ); } } );

    gridBagConstraints.gridx  = 1;
    gridBagConstraints.gridy  = 2;
    gridBagConstraints.fill   = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );
    getContentPane().add( _sizeSpinner, gridBagConstraints ); 


    //  Row 3 - Font Display Area
    _lblFontDisplayArea = new JLabel();
    _lblFontDisplayArea.setText( getString( "DLG.FONTS.LBL.DISPLAY.AREA" ) );

    gridBagConstraints.gridx   = 0;
    gridBagConstraints.gridy   = 3;
    gridBagConstraints.weightx = 0.0;

    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _lblFontDisplayArea, gridBagConstraints );


    _cboxFontDisplayArea.addActionListener( new ActionListener()
       { public void actionPerformed( final ActionEvent evt )
         { FontDisplayAreaComboBoxEvent( evt ); } } );

    gridBagConstraints.gridx   = 1;
    gridBagConstraints.gridy   = 3;
    gridBagConstraints.anchor  = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets  = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _cboxFontDisplayArea, gridBagConstraints );


    //  Preview Text Area
    _bdrTextPreview = new TitledBorder( null, getString( "DLG.FONTS.SAMPLE.TEXT" ),
      TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
        _fontLabel.getFont() );

    //  White background
    _previewTextArea.setBackground( new Color( 255, 255, 255 ) );
    _previewTextArea.setEditable( false );
    _previewTextArea.setLineWrap( true );
    _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT" ) );
    _previewTextArea.setWrapStyleWord( true );
    _previewTextArea.setBorder( _bdrTextPreview );

    _previewTextArea.setPreferredSize( new Dimension( 100, 100 ) );

    gridBagConstraints.gridx     = 0;
    gridBagConstraints.gridy     = 4;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill      = GridBagConstraints.BOTH;
    gridBagConstraints.anchor    = GridBagConstraints.WEST;
    gridBagConstraints.weighty   = 1.0;
    gridBagConstraints.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _previewTextArea, gridBagConstraints );

    //  Botom row - Button Panel OK & Cancel
    _buttonPanel.setLayout(new FlowLayout( FlowLayout.RIGHT ) );

    setLocalizedText( _btnApply, getString( "BTN.APPLY" ) );
    _btnApply.addActionListener( new ActionListener()
      { public void actionPerformed( final ActionEvent action )
        { OnApplyButton( action ); } } );

    _buttonPanel.add( _btnApply );

    setLocalizedText( _btnClose, getString( "BTN.CLOSE" ) );
    _btnClose.addActionListener( new ActionListener()
      { public void actionPerformed( final ActionEvent evt )
        { CloseButtonEvent( evt ); } } );

    _buttonPanel.add( _btnClose );

    gridBagConstraints.gridx     = 0;
    gridBagConstraints.gridy     = 5;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill      = GridBagConstraints.BOTH;
    gridBagConstraints.anchor    = GridBagConstraints.SOUTHEAST;
    gridBagConstraints.weighty   = 0.0;
    gridBagConstraints.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( _buttonPanel, gridBagConstraints );

    pack();

    setSize( new Dimension( 300, 350 ) );

    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    java.awt.Dimension dialogSize = getSize();

    setLocation( ( screenSize.width - dialogSize.width )/2,
     ( screenSize.height - dialogSize.height )/2 );
  }


  /**  New Font, selected by the user */
  final public Font getSelectedFont()
  {
    return( new Font( _fontComboBox.getSelectedItem().toString(), 
      getFontStyle(), ( (Number)_sizeSpinner.getValue() ).intValue() ) );
  }

  final public void setFontStyle( final int iFontStyle )
  { _iFontStyle = iFontStyle; }

  final public void setFontStyle()
  {
    switch( _cboxFontStyle.getSelectedIndex() )
    {
      case 0:
        _iFontStyle = Font.PLAIN;
        break;
      case 1:
        _iFontStyle = Font.ITALIC;
        break;
      case 2:
        _iFontStyle = Font.BOLD;
        break;
      case 3:
        _iFontStyle = Font.BOLD + Font.ITALIC;
        break;
      default:
        _iFontStyle = Font.PLAIN;
    }
  }

  final public int getFontStyle() { return( _iFontStyle ); }

  final public void setFontDisplayArea()
  {
    //  Should set font area variable to use to set fonts
    switch( _cboxFontDisplayArea.getSelectedIndex() )
    {
      case 0:
        _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.TABLE" ) );
        break;
      case 1:
        _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.TABLE.HDR" ));
        break;
      case 2:
        _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.SOURCE.EDITOR" ) );
        break;
      case 3:
        _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.TARGET.EDITOR" ) );
        break;
      case 4:
        _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.OTHER" ) );
        break;
      default:
        _previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT" ) );
    }
  }

  private void setFonts( final Font font )
  {
    setFont( font );

    _fontLabel.setFont( font );

    //  Set the font so that the strings will all show up
    //  Specifically needed for non ISO-8859 languages
    //  e.g. Chinese, Japanese, ?
    //  Mostly needed on non-Windoze/non-Mac systems
    //  Should use _fntOther when it is set
    //  - this is non EditView stuff
    //  - font for other UI stuff should be set independent
    //    of components that work with "document" text
    _fontComboBox.setFont( font );

    _cboxFontStyle.setFont( font );
    _cboxFontDisplayArea.setFont( font );

    _sizeLabel.setFont( font );

    _lblFontStyle.setFont( font );
    _lblFontDisplayArea.setFont( font );

    _previewTextArea.setFont( font );

    _bdrTextPreview.setTitleFont( font );

    _btnApply.setFont( font );
    _btnClose.setFont( font );
  }

  final public void setTextPreviewFont( final Font font ) {}

  private void fontComboBoxActionPerformed( final ActionEvent evt )
  { _previewTextArea.setFont( getSelectedFont() ); }

  private void sizeSpinnerStateChanged( final ChangeEvent evt )
  { _previewTextArea.setFont( getSelectedFont() ); }

  private void FontStyleComboBoxEvent( final ActionEvent evt )
  {
    setFontStyle(); 
    _previewTextArea.setFont( getSelectedFont() );
  }

  private void FontDisplayAreaComboBoxEvent( final ActionEvent evt )
  { setFontDisplayArea(); }

  private void OnApplyButton( final ActionEvent action )
  {
    //  Should set font area variable to use to set fonts
    switch( _cboxFontDisplayArea.getSelectedIndex() )
    {
      //  Table
      case 0:
        _wndB2T.setTableFont( getSelectedFont() );
        break;
      //  Table Header
      case 1:
        _wndB2T.setTableHeaderFont( getSelectedFont() );
        break;
      //  Source Editor
      case 2:
        _wndB2T.setSourceEditorFont( getSelectedFont() );
        break;
      //  Target Editor
      case 3:
        _wndB2T.setTargetEditorFont( getSelectedFont() );
        break;
      //  Windows, Dialogs, Menus, etc.
      case 4:
        _wndB2T.setUserInterfaceFont( getSelectedFont() );
        setFonts( getSelectedFont() );
        break;
      //  All GUI components  
      case 5:
      default:
        _wndB2T.setFonts( getSelectedFont() );
        setFonts( getSelectedFont() );
    }
  }

  private void CloseButtonEvent( final ActionEvent evt )
  { DialogClose(); }

  private void DialogClose()
  {
    setVisible( false );
    dispose();
  }


  private Bitext2tmxWindow _wndB2T;

  private JPanel           _buttonPanel;
  private JComboBox        _fontComboBox;
  private JLabel           _fontLabel;
  private JTextArea        _previewTextArea;
  private TitledBorder     _bdrTextPreview;
  private JLabel           _sizeLabel;
  private JSpinner         _sizeSpinner;

  private JButton          _btnApply;
  private JButton          _btnClose;

  private int              _iFontStyle;

  private JLabel           _lblFontStyle; 
  private JLabel           _lblFontDisplayArea;

  private JComboBox        _cboxFontStyle;
  private JComboBox        _cboxFontDisplayArea;

  /** The current editor and viewer font, passed in */
  private Font _fntDialog;

  /** The current font for other UI elements, passed in */
  //private Font _fntOther;


  final private String[] _astrFontStyles =
  {
    getString( "FONT.STYLE.PLAIN" ),
    getString( "FONT.STYLE.ITALIC" ),
    getString( "FONT.STYLE.BOLD" ),
    getString( "FONT.STYLE.BOLD.ITALIC" )
  };


  final private String[] _astrFontWindowAreas =
  {
    getString( "DLG.FONTS.WND.AREA.TABLE" ),
    getString( "DLG.FONTS.WND.AREA.TABLE.HDR" ),
    getString( "DLG.FONTS.WND.AREA.SOURCE.EDITOR" ),
    getString( "DLG.FONTS.WND.AREA.TARGET.EDITOR" ),
    getString( "DLG.FONTS.WND.AREA.OTHER" ),
    getString( "DLG.FONTS.WND.AREA.ALL" ) 
  };

}//  DlgFonts{}


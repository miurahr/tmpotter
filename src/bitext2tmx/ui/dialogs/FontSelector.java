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

import bitext2tmx.ui.MainWindow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *  Dialog to select the fonts of bitext2tmx.
 *
 */
@SuppressWarnings("serial")
public final class FontSelector extends JDialog {

  /**
   * Show font selector.
   * 
   * @param wndB2T frame
   * @param afnt font list
   */
  public FontSelector( final MainWindow wndB2T, final Font[] afnt ) {
    super( wndB2T, true );

    windowMain = wndB2T;

    initialize();

    getRootPane().setDefaultButton( buttonClose );

    fontDialog = afnt[0];

    //  Current font style
    setFontStyle( fontDialog.getStyle() );

    //  Conditionals test must be done in order
    if ( getFontStyle() == (Font.BOLD + Font.ITALIC) ) {
      comboFontStyle.setSelectedIndex( 3 );
    //  Italic alone is equivalent to Plain + Italic
    } else if ( getFontStyle() == Font.ITALIC ) {
      comboFontStyle.setSelectedIndex( 1 );
    } else if ( getFontStyle() ==  Font.BOLD ) {
      comboFontStyle.setSelectedIndex( 2 );
    } else if ( getFontStyle() == Font.PLAIN ) {
      comboFontStyle.setSelectedIndex( 0 );
    } else {
      comboFontStyle.setSelectedIndex( 0 );
    }
    
    fontComboBox.setSelectedItem( fontDialog.getName() );

    sizeSpinner.setValue( fontDialog.getSize() );

    setFonts( fontDialog );
  }

  private void initialize() {
    final GridBagConstraints    gridBagConstraints;

    buttonPanel         = new JPanel();
    fontComboBox        = new JComboBox( windowMain.getFontFamilyNames() );
    fontLabel           = new JLabel();
    sizeSpinner         = new JSpinner();
    sizeLabel           = new JLabel();
    previewTextArea     = new JTextArea();

    buttonApply            = new JButton();
    buttonClose            = new JButton();

    labelFontStyle        = new JLabel();
    labelFontDisplayArea  = new JLabel();

    comboFontStyle       = new JComboBox( fontStyleList );
    comboFontDisplayArea = new JComboBox( fontWindowAreasList );


    getContentPane().setLayout( new GridBagLayout() );

    setTitle( getString( "DLG.FONTS.TITLE" ) );

    setModal( false );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( final WindowEvent evt ) {
          dialogClose();
      }
    } );


    //  Build Dialog Grid Layout

    //  Default Constraints
    gridBagConstraints           = new GridBagConstraints();
    gridBagConstraints.fill      = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridwidth = 1;


    //  Top row 0 - Font Label & Combobox

    fontLabel.setText( getString( "DLG.FONTS.LBL.SOURCE.FONT" ) );

    gridBagConstraints.gridx  = 0;
    gridBagConstraints.gridy  = 0;
    gridBagConstraints.fill   = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( fontLabel, gridBagConstraints );


    fontComboBox.setMaximumRowCount( 10 );

    fontComboBox.addActionListener( new ActionListener() {
        @Override
        public void actionPerformed( final ActionEvent evt ) {
          fontComboBoxActionPerformed( evt );
        }
    } );

    fontComboBox.setBackground( new Color( 255, 255,255 ) );

    gridBagConstraints.gridx   = 1;
    gridBagConstraints.gridy   = 0;
    gridBagConstraints.fill    = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets  = new Insets( 5, 10, 5, 10 );

    getContentPane().add( fontComboBox, gridBagConstraints );


    //  Row 2 - Font Style
    labelFontStyle.setText( getString( "DLG.FONTS.LBL.STYLE" ) ); 

    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.0;

    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( labelFontStyle, gridBagConstraints );


    comboFontStyle.addActionListener( new ActionListener() {
        @Override
        public void actionPerformed( final ActionEvent evt ) {
          fontStyleComboBoxEvent( evt );
        }
    } );


    gridBagConstraints.gridx   = 1;
    gridBagConstraints.gridy   = 1;
    gridBagConstraints.anchor  = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets  = new Insets( 5, 10, 5, 10 );

    getContentPane().add( comboFontStyle, gridBagConstraints );


    //  Row 3 - Size Label & Spinner
    sizeLabel.setText( getString( "DLG.FONTS.LBL.FONTSIZE" ) );

    gridBagConstraints.gridx  = 0;
    gridBagConstraints.gridy  = 2;
    gridBagConstraints.fill   = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.0;
    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( sizeLabel, gridBagConstraints );


    sizeSpinner.addChangeListener( new ChangeListener() {
        @Override
        public void stateChanged( final ChangeEvent evt ) {
          sizeSpinnerStateChanged( evt );
        }
    } );

    gridBagConstraints.gridx  = 1;
    gridBagConstraints.gridy  = 2;
    gridBagConstraints.fill   = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );
    getContentPane().add( sizeSpinner, gridBagConstraints ); 


    //  Row 3 - Font Display Area
    labelFontDisplayArea = new JLabel();
    labelFontDisplayArea.setText( getString( "DLG.FONTS.LBL.DISPLAY.AREA" ) );

    gridBagConstraints.gridx   = 0;
    gridBagConstraints.gridy   = 3;
    gridBagConstraints.weightx = 0.0;

    gridBagConstraints.insets = new Insets( 5, 10, 5, 10 );

    getContentPane().add( labelFontDisplayArea, gridBagConstraints );


    comboFontDisplayArea.addActionListener( new ActionListener() {
        @Override
        public void actionPerformed( final ActionEvent evt ) {
          fontDisplayAreaComboBoxEvent( evt );
        }
    } );

    gridBagConstraints.gridx   = 1;
    gridBagConstraints.gridy   = 3;
    gridBagConstraints.anchor  = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets  = new Insets( 5, 10, 5, 10 );

    getContentPane().add( comboFontDisplayArea, gridBagConstraints );


    //  Preview Text Area
    bdrTextPreview = new TitledBorder( null,
            getString( "DLG.FONTS.SAMPLE.TEXT" ),
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            fontLabel.getFont() );

    //  White background
    previewTextArea.setBackground( new Color( 255, 255, 255 ) );
    previewTextArea.setEditable( false );
    previewTextArea.setLineWrap( true );
    previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT" ) );
    previewTextArea.setWrapStyleWord( true );
    previewTextArea.setBorder( bdrTextPreview );

    previewTextArea.setPreferredSize( new Dimension( 100, 100 ) );

    gridBagConstraints.gridx     = 0;
    gridBagConstraints.gridy     = 4;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill      = GridBagConstraints.BOTH;
    gridBagConstraints.anchor    = GridBagConstraints.WEST;
    gridBagConstraints.weighty   = 1.0;
    gridBagConstraints.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( previewTextArea, gridBagConstraints );

    //  Botom row - Button Panel OK & Cancel
    buttonPanel.setLayout(new FlowLayout( FlowLayout.RIGHT ) );

    setLocalizedText( buttonApply, getString( "BTN.APPLY" ) );
    buttonApply.addActionListener( new ActionListener() {
        @Override
        public void actionPerformed( final ActionEvent action ) {
          onApplyButton( action );
        }
    } );

    buttonPanel.add( buttonApply );

    setLocalizedText( buttonClose, getString( "BTN.CLOSE" ) );
    buttonClose.addActionListener( new ActionListener() {
      public void actionPerformed( final ActionEvent evt ) {
        closeButtonEvent( evt );
      }
    } );

    buttonPanel.add( buttonClose );

    gridBagConstraints.gridx     = 0;
    gridBagConstraints.gridy     = 5;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill      = GridBagConstraints.BOTH;
    gridBagConstraints.anchor    = GridBagConstraints.SOUTHEAST;
    gridBagConstraints.weighty   = 0.0;
    gridBagConstraints.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( buttonPanel, gridBagConstraints );

    pack();

    setSize( new Dimension( 300, 350 ) );

    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    java.awt.Dimension dialogSize = getSize();

    setLocation( ( screenSize.width - dialogSize.width ) / 2,
        ( screenSize.height - dialogSize.height ) / 2 );
  }


  /** 
   * New Font, selected by the user.
   */
  public final Font getSelectedFont() {
    return ( new Font( fontComboBox.getSelectedItem().toString(), 
        getFontStyle(), ( (Number)sizeSpinner.getValue() ).intValue() ) );
  }

  public final void setFontStyle( final int fontStyle ) {
    this.fontStyle = fontStyle;
  }

  /**
   * set font style.
   * 
   * <p> such as PLAIN, ITALIC, BOLD...
   */
  public final void setFontStyle() {
    switch ( comboFontStyle.getSelectedIndex() ) {
      case 0:
        fontStyle = Font.PLAIN;
        break;
      case 1:
        fontStyle = Font.ITALIC;
        break;
      case 2:
        fontStyle = Font.BOLD;
        break;
      case 3:
        fontStyle = Font.BOLD + Font.ITALIC;
        break;
      default:
        fontStyle = Font.PLAIN;
    }
  }

  public final int getFontStyle() {
    return ( fontStyle );
  }

  /**
   * Set test on font display area by bundle.
   */
  public final void setFontDisplayArea() {
    //  Should set font area variable to use to set fonts
    switch ( comboFontDisplayArea.getSelectedIndex() ) {
      case 0:
        previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.TABLE" ) );
        break;
      case 1:
        previewTextArea.setText(
                getString( "DLG.FONTS.SAMPLE.TEXT.TABLE.HDR" ));
        break;
      case 2:
        previewTextArea.setText(
                getString( "DLG.FONTS.SAMPLE.TEXT.SOURCE.EDITOR" ) );
        break;
      case 3:
        previewTextArea.setText(
                getString( "DLG.FONTS.SAMPLE.TEXT.TARGET.EDITOR" ) );
        break;
      case 4:
        previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT.OTHER" ) );
        break;
      default:
        previewTextArea.setText( getString( "DLG.FONTS.SAMPLE.TEXT" ) );
        break;
    }
  }

  private void setFonts( final Font font ) {
    setFont( font );

    fontLabel.setFont( font );

    //  Set the font so that the strings will all show up
    //  Specifically needed for non ISO-8859 languages
    //  e.g. Chinese, Japanese, ?
    //  Mostly needed on non-Windoze/non-Mac systems
    //  Should use _fntOther when it is set
    //  - this is non EditView stuff
    //  - font for other UI stuff should be set independent
    //    of components that work with "document" text
    fontComboBox.setFont( font );

    comboFontStyle.setFont( font );
    comboFontDisplayArea.setFont( font );

    sizeLabel.setFont( font );

    labelFontStyle.setFont( font );
    labelFontDisplayArea.setFont( font );

    previewTextArea.setFont( font );

    bdrTextPreview.setTitleFont( font );

    buttonApply.setFont( font );
    buttonClose.setFont( font );
  }

  public final void setTextPreviewFont( final Font font ) {}

  private void fontComboBoxActionPerformed( final ActionEvent evt ) {
    previewTextArea.setFont( getSelectedFont() );
  }

  private void sizeSpinnerStateChanged( final ChangeEvent evt ) {
    previewTextArea.setFont( getSelectedFont() );
  }

  private void fontStyleComboBoxEvent( final ActionEvent evt ) {
    setFontStyle(); 
    previewTextArea.setFont( getSelectedFont() );
  }

  private void fontDisplayAreaComboBoxEvent( final ActionEvent evt ) {
    setFontDisplayArea();
  }

  private void onApplyButton( final ActionEvent action ) {
    //  Should set font area variable to use to set fonts
    switch ( comboFontDisplayArea.getSelectedIndex() ) {
      //  Table
      case 0:
        windowMain.setTableFont( getSelectedFont() );
        break;
      //  Table Header
      case 1:
        windowMain.setTableHeaderFont( getSelectedFont() );
        break;
      //  Source Editor
      case 2:
        windowMain.setSourceEditorFont( getSelectedFont() );
        break;
      //  Target Editor
      case 3:
        windowMain.setTargetEditorFont( getSelectedFont() );
        break;
      //  Windows, Dialogs, Menus, etc.
      case 4:
        windowMain.setUserInterfaceFont( getSelectedFont() );
        setFonts( getSelectedFont() );
        break;
      //  All GUI components  
      case 5:
      default:
        windowMain.setFonts( getSelectedFont() );
        setFonts( getSelectedFont() );
    }
  }

  private void closeButtonEvent( final ActionEvent evt ) {
    dialogClose();
  }

  private void dialogClose() {
    setVisible( false );
    dispose();
  }

  private final MainWindow windowMain;

  private JPanel           buttonPanel;
  private JComboBox        fontComboBox;
  private JLabel           fontLabel;
  private JTextArea        previewTextArea;
  private TitledBorder     bdrTextPreview;
  private JLabel           sizeLabel;
  private JSpinner         sizeSpinner;

  private JButton          buttonApply;
  private JButton          buttonClose;

  private int              fontStyle;

  private JLabel           labelFontStyle; 
  private JLabel           labelFontDisplayArea;

  private JComboBox        comboFontStyle;
  private JComboBox        comboFontDisplayArea;

  // The current editor and viewer font, passed in
  private final Font fontDialog;

  private final String[] fontStyleList = 
  {
    getString( "FONT.STYLE.PLAIN" ),
    getString( "FONT.STYLE.ITALIC" ),
    getString( "FONT.STYLE.BOLD" ),
    getString( "FONT.STYLE.BOLD.ITALIC" )
  };


  private final String[] fontWindowAreasList = 
  {
    getString( "DLG.FONTS.WND.AREA.TABLE" ),
    getString( "DLG.FONTS.WND.AREA.TABLE.HDR" ),
    getString( "DLG.FONTS.WND.AREA.SOURCE.EDITOR" ),
    getString( "DLG.FONTS.WND.AREA.TARGET.EDITOR" ),
    getString( "DLG.FONTS.WND.AREA.OTHER" ),
    getString( "DLG.FONTS.WND.AREA.ALL" ) 
  };

}
/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009  Raymond: Martin
#                2015 Hiroshi Miura
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 3 of the License, or
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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import static org.openide.awt.Mnemonics.setLocalizedText;

import static bitext2tmx.util.Localization.getString;


/**
 *   Alignment Table view for parallel texts
 *
 */
@SuppressWarnings("serial")
final class ControlView extends DockablePanel implements ActionListener
{
  final private MainWindow windowMain;

  final private JButton  buttonOriginalDelete    = new JButton();
  final private JButton  buttonOriginalJoin      = new JButton();
  final private JButton  buttonOriginalSplit     = new JButton();
  final private JButton  buttonTranslationDelete = new JButton();
  final private JButton  buttonTranslationJoin   = new JButton();
  final private JButton  buttonTranslationSplit  = new JButton();
  final private JButton  buttonRemoveBlankRows   = new JButton();
  final private JButton  buttonTUSplit           = new JButton();

  final private JButton  buttonUndo              = new JButton();

  final private JPanel panelButtons       = new JPanel( new GridLayout( 2, 1 ) );
  final private JPanel panelButtonsTop    = new JPanel( new GridLayout( 1, 2 ) );
  final private JPanel panelButtonsBottom = new JPanel();
  final private JPanel panelButtonsLeft   = new JPanel();
  final private JPanel panelButtonsRight  = new JPanel();

  public ControlView( final MainWindow windowMain )
  {
    super( "SegmentButtonsView" );

    this.windowMain = windowMain;

    getDockKey().setName(getString("CV_DOCK_TITLE"));
    getDockKey().setTooltip(getString("CV_DOCK_TOOLTIP"));
    getDockKey().setCloseEnabled(true);
    getDockKey().setAutoHideEnabled(true);
    getDockKey().setResizeWeight(1.0F);  // takes all resizing
    //getDockKey().setIcon( Icons.getIcon( "icon-small.png") );
    setLayout(new BorderLayout());

    setLocalizedText(buttonRemoveBlankRows, getString("BTN.DELETE.BLANK.ROWS"));
    buttonRemoveBlankRows.setToolTipText(getString("BTN.DELETE.BLANK.ROWS.TOOLTIP"));
    buttonRemoveBlankRows.addActionListener(this);
    buttonRemoveBlankRows.setEnabled(false);

    setLocalizedText(buttonTUSplit, getString("BTN.SPLIT.TU"));
    buttonTUSplit.setToolTipText(getString( "BTN.SPLIT.TU.TOOLTIP" ) );
    buttonTUSplit.addActionListener( this );
    buttonTUSplit.setEnabled( false );

    setLocalizedText(buttonTranslationJoin, getString( "BTN.JOIN.TRANSLATION" ) );
    buttonTranslationJoin.addActionListener( this );
    buttonTranslationJoin.setActionCommand(getString( "BTN.JOIN.TRANSLATION" ) );

    setLocalizedText(buttonOriginalJoin, getString( "BTN.JOIN.ORIGINAL" ) );
    buttonOriginalJoin.addActionListener( this );
    buttonOriginalJoin.setActionCommand(getString( "BTN.JOIN.ORIGINAL" ) );

    setLocalizedText(buttonTranslationDelete, getString( "BTN.DELETE.TRANSLATION" ) );
    buttonTranslationDelete.addActionListener( this );
    buttonTranslationDelete.setActionCommand(getString( "BTN.DELETE.TRANSLATION" ) );

    setLocalizedText(buttonOriginalDelete, getString( "BTN.DELETE.ORIGINAL" ) );
    buttonOriginalDelete.addActionListener( this );
    buttonOriginalDelete.setActionCommand(getString( "BTN.DELETE.ORIGINAL" ) );

    setLocalizedText(buttonTranslationSplit, getString( "BTN.SPLIT.TRANSLATION" ) );
    buttonTranslationSplit.addActionListener( this );
    buttonTranslationSplit.setActionCommand(getString( "BTN.SPLIT.TRANSLATION" ) );

    setLocalizedText(buttonOriginalSplit, getString( "BTN.SPLIT.ORIGINAL" ) );
    buttonOriginalSplit.addActionListener( this );
    buttonOriginalSplit.setActionCommand(getString( "BTN.SPLIT.ORIGINAL" ) );

    buttonUndo.setText(getString( "BTN.UNDO" ) );
    setLocalizedText(buttonUndo, getString( "BTN.UNDO" ) );
    buttonUndo.addActionListener( this );
    buttonUndo.setEnabled( false );

    enableButtons( false );

    panelButtonsBottom  .add( buttonUndo,              null );
    panelButtonsBottom  .add( buttonRemoveBlankRows,   null );
    panelButtonsBottom  .add( buttonTUSplit,           null );
    panelButtonsLeft    .add( buttonOriginalJoin,      null );
    panelButtonsLeft    .add(buttonOriginalDelete,    null );
    panelButtonsLeft    .add( buttonOriginalSplit,     null );
    panelButtonsRight   .add( buttonTranslationJoin,   null );
    panelButtonsRight   .add( buttonTranslationDelete, null );
    panelButtonsRight   .add( buttonTranslationSplit,  null );

    panelButtonsTop.add( panelButtonsLeft );
    panelButtonsTop.add( panelButtonsRight );

    panelButtons.add( panelButtonsBottom );
    panelButtons.add( panelButtonsTop );

    panelButtons.setMinimumSize( new Dimension( 480, 120 ) );

    add( panelButtons, BorderLayout.CENTER );
  }

  final void setFonts( final Font font )
  {
    buttonUndo               .setFont( font );
    buttonRemoveBlankRows    .setFont( font );
    buttonTUSplit            .setFont( font );
    buttonOriginalJoin       .setFont( font );
    buttonOriginalDelete     .setFont( font );
    buttonOriginalSplit      .setFont( font );
    buttonTranslationJoin    .setFont( font );
    buttonTranslationDelete  .setFont( font );
    buttonTranslationSplit   .setFont( font );
  }

  final void enableButtons( boolean bEnabled )
  {
    buttonRemoveBlankRows    .setEnabled( bEnabled );
    buttonTUSplit            .setEnabled( bEnabled );
    buttonOriginalJoin       .setEnabled( bEnabled );
    buttonOriginalDelete     .setEnabled( bEnabled );
    buttonOriginalSplit      .setEnabled( bEnabled );
    buttonTranslationJoin    .setEnabled( bEnabled );
    buttonTranslationDelete  .setEnabled( bEnabled );
    buttonTranslationSplit   .setEnabled( bEnabled );
  }

  final public void setUndoEnabled( boolean bEnabled )
  { buttonUndo.setEnabled( bEnabled ); }

  final public void setOriginalJoinEnabled( boolean bEnabled )
  { buttonOriginalJoin.setEnabled( bEnabled ); }

  final public void setTranslationJoinEnabled( boolean bEnabled )
  { buttonTranslationJoin.setEnabled( bEnabled ); }

  final public void updateText()
  {
    buttonUndo.setText(getString( "BTN.UNDO" ) );

    buttonRemoveBlankRows.setText(getString( "BTN.DELETE.BLANK.ROWS" ) );
    buttonRemoveBlankRows.setToolTipText(getString( "BTN.DELETE.BLANK.ROWS.TOOLTIP" ) );
    buttonTUSplit.setText(getString( "BTN.SPLIT.TU" ) );
    buttonTUSplit.setToolTipText(getString( "BTN.SPLIT.TU.TOOLTIP" ) );

    buttonOriginalJoin    .setActionCommand(getString( "BTN.JOIN" ) );
    buttonOriginalJoin    .setText(getString( "BTN.JOIN" ) );
    buttonOriginalDelete  .setActionCommand(getString( "BTN.DELETE" ) );
    buttonOriginalDelete  .setText(getString( "BTN.DELETE" ) );
    buttonOriginalSplit   .setActionCommand(getString( "BTN.SPLIT" ) );
    buttonOriginalSplit   .setText(getString( "BTN.SPLIT" ) );

    buttonTranslationJoin    .setActionCommand(getString( "BTN.JOIN" ) );
    buttonTranslationJoin    .setText(getString( "BTN.JOIN" ) );
    buttonTranslationDelete  .setText(getString( "BTN.DELETE" ) );
    buttonTranslationDelete  .setActionCommand(getString( "BTN.DELETE" ) );
    buttonTranslationSplit   .setText(getString( "BTN.SPLIT" ) );
    buttonTranslationSplit   .setActionCommand(getString( "BTN.SPLIT" ) );
  }

  private void onUndo()            { windowMain.onUndo(); }
  private void onRemoveBlankRows() { windowMain.onRemoveBlankRows(); }

  private void onTUSplit() {
    windowMain.onTUSplit();
    buttonUndo.setEnabled( true );
  }

  private void onOriginalJoin() {
    windowMain.onOriginalJoin();
    buttonUndo.setEnabled( true );
  }

  private void onOriginalDelete() {
    windowMain.onOriginalDelete();
    buttonUndo.setEnabled( true );
  }

  private void onOriginalSplit() {
    windowMain.onOriginalSplit();
    buttonUndo.setEnabled( true );
  }

  private void onTranslationJoin() {
    windowMain.onTranslationJoin();
    buttonUndo.setEnabled( true );
  }

  private void onTranslationDelete() {
    windowMain.onTranslationDelete();
    buttonUndo.setEnabled( true );
  }

  private void onTranslationSplit() {
    windowMain.onTranslationSplit();
    buttonUndo.setEnabled( true );
  }

  @Override
  final public void actionPerformed( final ActionEvent action ) {
    final Object actor = action.getSource();

    if( actor instanceof JButton )
    {
      if( actor == buttonOriginalDelete )          onOriginalDelete();
      else if( actor == buttonOriginalJoin )       onOriginalJoin();
      else if( actor == buttonOriginalSplit )      onOriginalSplit();

      else if( actor == buttonTranslationDelete )  onTranslationDelete();
      else if( actor == buttonTranslationJoin )    onTranslationJoin();
      else if( actor == buttonTranslationSplit )   onTranslationSplit();

      else if( actor == buttonRemoveBlankRows )    onRemoveBlankRows();
      else if( actor == buttonTUSplit )            onTUSplit();
      else if( actor == buttonUndo )               onUndo();
    }
  }

}//  ControlView{}



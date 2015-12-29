/**************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
 *  Copyright (C) 2015 Hiroshi Miura
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

package org.tmpotter.ui;

import static org.openide.awt.Mnemonics.setLocalizedText;
import static org.tmpotter.util.Localization.getString;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


/**
 *   Alignment Table view for parallel texts.
 *
 */
@SuppressWarnings("serial")
final class ControlView extends DockablePanel implements ActionListener {
  private final MainWindow mainWindow;

  private final JButton  buttonOriginalDelete    = new JButton();
  private final JButton  buttonOriginalJoin      = new JButton();
  private final JButton  buttonOriginalSplit     = new JButton();
  private final JButton  buttonTranslationDelete = new JButton();
  private final JButton  buttonTranslationJoin   = new JButton();
  private final JButton  buttonTranslationSplit  = new JButton();
  private final JButton  buttonRemoveBlankRows   = new JButton();
  private final JButton  buttonTuSplit           = new JButton();

  private final JButton  buttonUndo              = new JButton();

  private final JPanel panelButtons       = new JPanel( new GridLayout( 2, 1 ) );
  private final JPanel panelButtonsTop    = new JPanel( new GridLayout( 1, 2 ) );
  private final JPanel panelButtonsBottom = new JPanel();
  private final JPanel panelButtonsLeft   = new JPanel();
  private final JPanel panelButtonsRight  = new JPanel();

  public ControlView( final MainWindow windowMain ) {
    super( "SegmentButtonsView" );

    this.mainWindow = windowMain;

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

    setLocalizedText(buttonTuSplit, getString("BTN.SPLIT.TU"));
    buttonTuSplit.setToolTipText(getString( "BTN.SPLIT.TU.TOOLTIP" ) );
    buttonTuSplit.addActionListener( this );
    buttonTuSplit.setEnabled( false );

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
    panelButtonsBottom  .add( buttonTuSplit,           null );
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

  final void setFonts( final Font font ) {
    buttonUndo               .setFont( font );
    buttonRemoveBlankRows    .setFont( font );
    buttonTuSplit            .setFont( font );
    buttonOriginalJoin       .setFont( font );
    buttonOriginalDelete     .setFont( font );
    buttonOriginalSplit      .setFont( font );
    buttonTranslationJoin    .setFont( font );
    buttonTranslationDelete  .setFont( font );
    buttonTranslationSplit   .setFont( font );
  }

  final void enableButtons( boolean enabled ) {
    buttonRemoveBlankRows    .setEnabled( enabled );
    buttonTuSplit            .setEnabled( enabled );
    buttonOriginalJoin       .setEnabled( enabled );
    buttonOriginalDelete     .setEnabled( enabled );
    buttonOriginalSplit      .setEnabled( enabled );
    buttonTranslationJoin    .setEnabled( enabled );
    buttonTranslationDelete  .setEnabled( enabled );
    buttonTranslationSplit   .setEnabled( enabled );
  }

  public final void setUndoEnabled( boolean enabled ) {
    buttonUndo.setEnabled( enabled );
  }

  public final void setOriginalJoinEnabled( boolean enabled ) {
    buttonOriginalJoin.setEnabled( enabled );
  }

  public final void setTranslationJoinEnabled( boolean enabled ) {
    buttonTranslationJoin.setEnabled( enabled );
  }

  public final void updateText() {
    buttonUndo.setText(getString( "BTN.UNDO" ));

    buttonRemoveBlankRows.setText(getString( "BTN.DELETE.BLANK.ROWS" ) );
    buttonRemoveBlankRows.setToolTipText(getString( "BTN.DELETE.BLANK.ROWS.TOOLTIP" ) );
    buttonTuSplit.setText(getString( "BTN.SPLIT.TU" ) );
    buttonTuSplit.setToolTipText(getString( "BTN.SPLIT.TU.TOOLTIP" ) );

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

  private void onUndo()            {
    mainWindow.onUndoCv();
  }
  
  private void onRemoveBlankRows() {
    mainWindow.onRemoveBlankRows();
  }

  private void onTuSplit() {
    mainWindow.onTuSplit();
    buttonUndo.setEnabled( true );
  }

  private void onOriginalJoin() {
    mainWindow.onOriginalJoin();
    buttonUndo.setEnabled( true );
  }

  private void onOriginalDelete() {
    mainWindow.onOriginalDelete();
    buttonUndo.setEnabled( true );
  }

  private void onOriginalSplit() {
    mainWindow.onOriginalSplit();
    buttonUndo.setEnabled( true );
  }

  private void onTranslationJoin() {
    mainWindow.onTranslationJoin();
    buttonUndo.setEnabled( true );
  }

  private void onTranslationDelete() {
    mainWindow.onTranslationDelete();
    buttonUndo.setEnabled( true );
  }

  private void onTranslationSplit() {
    mainWindow.onTranslationSplitCv();
    buttonUndo.setEnabled( true );
  }

  @Override
  public final void actionPerformed( final ActionEvent action ) {
    final Object actor = action.getSource();

    if ( actor instanceof JButton ) {
      if ( actor == buttonOriginalDelete ) {
        onOriginalDelete();
      } else if ( actor == buttonOriginalJoin ) {
        onOriginalJoin();
      } else if ( actor == buttonOriginalSplit ) {
        onOriginalSplit();
      } else if ( actor == buttonTranslationDelete ) {
        onTranslationDelete();
      } else if ( actor == buttonTranslationJoin )  {
        onTranslationJoin();
      } else if ( actor == buttonTranslationSplit ) {
        onTranslationSplit();
      } else if ( actor == buttonRemoveBlankRows )  {
        onRemoveBlankRows();
      } else if ( actor == buttonTuSplit )     {
        onTuSplit();
      } else if ( actor == buttonUndo )     {
        onUndo();
      }
    }
  }
}

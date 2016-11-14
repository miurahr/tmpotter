/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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
 * *************************************************************************/
package org.tmpotter.ui;

import org.jdesktop.swingx.JXPanel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Segment editor.
 *
 */
@SuppressWarnings("serial")
class SegmentEditor extends JXPanel {

  private final JTextPane textPane = new JTextPane();
  private ModelMediator modelMediator;

  private static final Logger LOG = Logger.getLogger(SegmentEditor.class.getName());

  /**
   * Constructor.
   *
   * @param windowMain main window object.
   */
  public SegmentEditor() {
    super(false);

    textPane.addKeyListener(new KeyAdapter() {
      @Override
      public final void keyReleased(final KeyEvent evnet) {
        onKeyReleased();
      }
    });

    textPane.addMouseListener(new MouseAdapter() {
      @Override
      public final void mouseClicked(final MouseEvent event) {
        onClicked();
      }
    });

    setLayout(new GridBagLayout());

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridheight = 1;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.ipadx = 10;
    gbc.ipady = 10;
    gbc.insets = new Insets(1, 1, 1, 1);
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;

    final JScrollPane scpn = new JScrollPane(textPane);
    add(scpn, gbc);
  }

  public void setModelMediator(ModelMediator mediator) {
    this.modelMediator = mediator;
  }

  public final String getText() {
    return (textPane.getText());
  }

  public final void setText(final String strText) {
    textPane.setText(strText);
    textPane.setCaretPosition(0);
  }

  /**
   * Set Editor pane font.
   *
   * @param font to set
   */
  public final void setEditorFont(final Font font) {
    if (textPane != null) {
      textPane.setFont(font);
    } else {
      LOG.log(Level.INFO, "segment editor pane does not exist yet!");
    }
  }

  public final void reset() {
    textPane.setText("");
  }

  public final int getSelectionStart() {
    return (textPane.getSelectionStart());
  }

  private void onKeyReleased() {
    modelMediator.setTextAreaPosition(textPane.getSelectionStart());
  }

  private void onClicked() {
    modelMediator.setTextAreaPosition(textPane.getSelectionStart());
  }
}

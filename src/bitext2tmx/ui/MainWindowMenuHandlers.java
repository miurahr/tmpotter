/**
 * ************************************************************************
 *
 * bitext2tmx - Bitext Aligner/TMX Editor
 *
 * Copyright (C) 2015 Hiroshi Miura
 *
 * This file is part of bitext2tmx.
 *
 * bitext2tmx is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * bitext2tmx is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * bitext2tmx. If not, see http://www.gnu.org/licenses/.
 *
 *************************************************************************
 */

package bitext2tmx.ui;

import bitext2tmx.core.Document;
import bitext2tmx.ui.dialogs.About;
import bitext2tmx.ui.help.Manual;
import static bitext2tmx.util.StringUtil.formatText;
import static bitext2tmx.util.StringUtil.restoreText;
import java.awt.event.KeyEvent;

/**
 * Action Handlers.
 *
 * @author Hiroshi Miura
 */
final class MainWindowMenuHandlers {
  private final MainWindow mainWindow;
  private final AlignmentsView viewAlignments;
  private final SegmentEditor editLeftSegment;
  private final SegmentEditor editRightSegment;
  private final ControlView viewControls;
  
  public MainWindowMenuHandlers(final MainWindow mainWindow, final AlignmentsView viewAlignments, final SegmentEditor editLeftSegment, final SegmentEditor editRightSegment, ControlView viewControls) {
    this.mainWindow = mainWindow;
    this.viewAlignments = viewAlignments;
    this.editLeftSegment = editLeftSegment;
    this.editRightSegment = editRightSegment;
    this.viewControls = viewControls;
  }
  
  final void helpAboutMenuItemActionPerformed() {
    new About(mainWindow).setVisible(true);
  }

  void helpManualMenuItemActionPerformed() {
    final Manual dlg = new Manual();
    dlg.setVisible(true);
  }
  
  
}

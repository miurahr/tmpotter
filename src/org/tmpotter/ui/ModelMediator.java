/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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
 * ************************************************************************/

package org.tmpotter.ui;

import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Model mediator for TM view and segment editors.
 *
 * @author Hiroshi Miura
 */
public interface ModelMediator {

  void onOpenFile(File filePathOriginal, String stringLangOriginal, String stringLangTranslation);

  //for TMView
  void updateTmView();

  void onOriginalJoin();

  void onOriginalDelete();

  void onOriginalSplit();

  void onTranslationJoin();

  void onTranslationDelete();

  void onTranslationSplit();

  void onUndo();

  void onRemoveBlankRows();

  void onTuSplit();

  // Segment Editor
  void setTextAreaPosition(int position);

  void onTableClicked();

  void onTablePressed(final KeyEvent event);

  void tmDataClear();

  void tmViewClear();

  void editSegmentClear();

  void setUndoEnabled(boolean enable);

  void undoChanges();
}

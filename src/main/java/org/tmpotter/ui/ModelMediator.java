/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
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

import org.tmpotter.core.ProjectProperties;

import java.io.File;


/**
 * Model mediator for TM view and segment editors.
 *
 * @author Hiroshi Miura
 */
public interface ModelMediator {
  // Open/Import
  void setFilePathOriginal(File filePath);

  String getFileNameOriginal();

  void setFilePathTranslation(File filePath);

  String getFileNameTranslation();

  void setSourceLanguage(String lang);

  void setTargetLanguage(String lang);

  void setOriginalProperties(File filePath, String lang, String encoding);

  void setTargetProperties(File filePath, String lang, String encoding);

  // Props
  ProjectProperties getProjectProperties();

  void clearProjectProperties();

  // Menus
  void enableButtonsOnOpenFile(boolean val);

  // TMView
  void initializeTmView();

  void updateTmView();

  void buildDisplay();

  String getLeftSegment(int index);

  String getRightSegment(int index);

  int getTmViewRows();

  int getTmViewSelectedRow();

  int getTmViewSelectedColumn();

  // Segment Editor
  void setTextAreaPosition(int position);

  void tmDataClear();

  void tmViewClear();

  void editSegmentClear();

  void setUndoEnabled(boolean enable);

  void setLeftEdit(String edit);

  void setRightEdit(String edit);

  String getLeftEdit();

  String getRightEdit();

  // ToolBar
  void setJoinEnabled(boolean val);

  void enableAlignToolBar(boolean enable);

  void enableMenuItemFileSave(boolean val);

  // Status Bar
  void updateStatusBar();
}

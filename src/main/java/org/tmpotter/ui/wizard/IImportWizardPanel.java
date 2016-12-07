/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
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

package org.tmpotter.ui.wizard;

import javax.swing.JPanel;


/**
 * Interface for control wizard panels and buttons.
 * @author Hiroshi Miura
 */
public interface IImportWizardPanel {
  void init(ImportWizardController controller, ImportPreference pref);
	String getId();
	boolean isCombinedFormat();
	JPanel getPanel();
	String getName();
	String getDesc();
	String getNextFinishCommand();
	String getBackCommand();
	void updatePref();
}

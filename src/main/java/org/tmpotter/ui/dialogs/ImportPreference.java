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

package org.tmpotter.ui.dialogs;

import java.io.File;


/**
 *
 * @author miurahr
 */
public class ImportPreference {
	private File originalFilePath;
	private File translationFilePath;
	private String originalLang;
	private String translationLang;
	private String encoding;
	private String filter;
	private File currentPath;

	public File getOriginalFilePath() {
		return originalFilePath;
	}

	public void setOriginalFilePath(File originalFilePath) {
		this.originalFilePath = originalFilePath;
	}

	public File getTranslationFilePath() {
		return translationFilePath;
	}

	public void setTranslationFilePath(File translationFilePath) {
		this.translationFilePath = translationFilePath;
	}

	public String getOriginalLang() {
		return originalLang;
	}

	public void setOriginalLang(String originalLang) {
		this.originalLang = originalLang;
	}

	public String getTranslationLang() {
		return translationLang;
	}

	public void setTranslationLang(String translationLang) {
		this.translationLang = translationLang;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public File getCurrentPath() {
		if (currentPath != null) {
			return currentPath;		
		} else {
			return originalFilePath;
		}
	}
	
	public void setCurrentPath(File path) {
		currentPath = path;
	}
}

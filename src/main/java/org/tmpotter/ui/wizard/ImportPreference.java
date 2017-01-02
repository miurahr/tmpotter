/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016-2017 Hiroshi Miura
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

import java.io.File;
import java.net.URL;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

/**
 * Preference get by import wizard.
 *
 * @author Hiroshi Miura
 */
public class ImportPreference {
    private URI originalFileUri;
    private URI translationFileUri;
    private String originalLang;
    private String translationLang;
    private String sourceEncoding;
    private String translationEncoding;
    private String filter;
    private File currentPath;
    private Map<String, String> config = new TreeMap<>();

    public File getOriginalFilePath() {
        return new File(originalFileUri);
    }

    public URI getOriginalFileUri() {
        return originalFileUri;
    }

    public void setOriginalFilePath(File originalFilePath) {
        originalFileUri = originalFilePath.toURI();
    }

    public void setOriginalFilePath(URI uri) {
        originalFileUri = uri;
    }

    public File getTranslationFilePath() {
        return new File(translationFileUri);
    }

    public URI getTranslationFileUri() {
        return translationFileUri;
    }

    public void setTranslationFilePath(File translationFilePath) {
        translationFileUri = translationFilePath.toURI();
    }

    public void setTranslationFilePath(URI uri) {
        translationFileUri = uri;
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

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(String encoding) {
        this.sourceEncoding = encoding;
    }

    public String getTranslationEncoding() {
        return translationEncoding;
    }

    public void setTranslationEncoding(String translationEncoding) {
        this.translationEncoding = translationEncoding;
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
        } else if (originalFileUri != null) {
            return new File(originalFileUri);
        }
        return null;
    }

    public void setCurrentPath(File path) {
        currentPath = path;
    }

    public String getConfigValue(final String key) {
        return config.get(key);
    }

    public String getConfigValueOrDefault(final String key, final String defaultValue) {
        if (config.containsKey(key)) {
            return config.get(key);
        }
        return defaultValue;
    }

    public void setConfigValue(final String key, final String value) {
        config.put(key, value);
    }
}

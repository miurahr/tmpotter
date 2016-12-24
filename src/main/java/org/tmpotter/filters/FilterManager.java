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
 * ************************************************************************/

package org.tmpotter.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmpotter.core.Document;
import org.tmpotter.core.ProjectProperties;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.util.Localization;
import org.tmpotter.util.PluginUtils;

import java.io.File;
import java.util.List;


/**
 * Filter manager to handle multiple filters.
 *
 * @author Hiroshi Miura
 */
public class FilterManager {
    private final List<IFilter> filterList;

    private Document documentOriginal;
    private Document documentTranslation;

    /**
     * Constructor.
     */
    public FilterManager() {
        filterList = PluginUtils.getFilters();
    }

    /**
     * return filter instance according to its name.
     *
     * @param name to get
     * @return IFilter filter instance
     * @throws FilterNotFoundException when filter not found
     */
    public IFilter getFilterInstance(String name) throws FilterNotFoundException {
        for (IFilter filter : filterList) {
            String filterName = filter.getClass().getSimpleName();
            if (filterName.equals(name)) {
                return filter;
            }
        }
        throw new FilterNotFoundException("Filter " + name + " not found.");
    }

    /**
     * Load file from source file set to properties.
     *
     * @param prop           project properties
     * @param docOriginal    document to return
     * @param docTranslation document to return
     * @param filterName     filter to use
     */
    public void loadFile(ImportPreference pref, ProjectProperties prop,
                         Document docOriginal, Document docTranslation,
                         String filterName) {
        IFilter filter = null;
        this.documentOriginal = docOriginal;
        this.documentTranslation = docTranslation;

        prop.setSourceLanguage(pref.getOriginalLang());
        prop.setTargetLanguage(pref.getTranslationLang());
        FilterContext fc = new FilterContext(prop.getSourceLanguage(), prop.getTargetLanguage(),
                true);

        if (pref.getSourceEncoding().equals(Localization.getString("ENCODING.DEFAULT"))) {
            fc.setSourceEncoding(null);
        } else {
            fc.setSourceEncoding(pref.getSourceEncoding());
        }
        if (pref.getTranslationEncoding() == null
                || pref.getTranslationEncoding().equals(Localization
                .getString("ENCODING.DEFAULT"))) {
            fc.setTranslationEncoding(null);
        } else {
            fc.setTranslationEncoding(pref.getTranslationEncoding());
        }
        try {
            filter = getFilterInstance(filterName);
        } catch (Exception ex) {
            LOGGER.info("Filter error", ex);
        }
        if (filter != null) {
            try {
                filter.parseFile(pref.getOriginalFilePath(), pref.getTranslationFilePath(),
                        null, fc, new ParseCb());
            } catch (Exception ex) {
                LOGGER.info("Filter error", ex);
            }
        }
    }

    /**
     * Call saveFile interface of filter.
     *
     * @param outFile output filename.
     * @param prop property.
     * @param docOriginal original document
     * @param docTranslation translation document.
     * @param filterName filter name.
     * @throws Exception throw when error happened.
     */
    public void saveFile(File outFile, ProjectProperties prop,
                Document docOriginal, Document docTranslation, String filterName)
            throws Exception {
        IFilter filter = null;
        this.documentOriginal = docOriginal;
        this.documentTranslation = docTranslation;
        FilterContext fc = new FilterContext(prop.getSourceLanguage(),
                prop.getTargetLanguage(), true);
        try {
            filter = getFilterInstance(filterName);
        } catch (Exception ex) {
            LOGGER.info("Filter error", ex);
        }
        if (filter != null) {
            try {
                filter.saveFile(outFile, docOriginal, docTranslation, fc);
            } catch (Exception ex) {
                LOGGER.info("Filter error", ex);
            }
        }
    }


    public class ParseCb implements IParseCallback {
        @Override
        public void addEntry(String id, String source, String translation, boolean isFuzzy,
                             String comment, String path, IFilter filter) {
            if (source != null) {
                documentOriginal.add(source);
            }
            if (translation != null) {
                documentTranslation.add(translation);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterManager.class);
}

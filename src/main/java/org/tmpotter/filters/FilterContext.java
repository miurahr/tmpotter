/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file come from OmegaT.
 *
 *  Copyright (C) 2010 Alex Buloichik
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

package org.tmpotter.filters;

import org.tmpotter.core.ProjectProperties;
import org.tmpotter.util.Language;

/**
 * Context for filter calls.
 */
public class FilterContext {

    private final ProjectProperties props;
    private final Language sourceLang;
    private final Language targetLang;
    private String sourceEncoding;
    private String translateEncoding;

    /**
     * Constructor from existent procps.
     *
     * @param props Project Properties
     */
    public FilterContext(ProjectProperties props) {
        this.props = props;
        this.sourceLang = props.getSourceLanguage();
        this.targetLang = props.getTargetLanguage();
    }

    /**
     * Constructor.
     *
     * @param sourceLang                source langauge
     * @param targetLang                target language
     * @param sentenceSegmentingEnabled true when enables segmentation by sentence
     */
    public FilterContext(Language sourceLang, Language targetLang,
                         boolean sentenceSegmentingEnabled) {
        this.props = null;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }

    /**
     * Source language of project.
     *
     * @return source language
     */
    public Language getSourceLang() {
        return sourceLang;
    }

    /**
     * Target language of project.
     *
     * @return target language
     */
    public Language getTargetLang() {
        return targetLang;
    }

    /**
     * Source file encoding, but can be 'null'.
     *
     * @return encoding for file input
     */
    public String getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Set encoding for file input.
     *
     * @param inEncoding set character encoding for file input.
     */
    public void setSourceEncoding(String inEncoding) {
        this.sourceEncoding = inEncoding;
    }

    /**
     * Get Translation file encoding, but can be 'null'.
     *
     * @return charactor encoding for file output.
     */
    public String getTranslationEncoding() {
        return translateEncoding;
    }

    /**
     * Set file encoding for translation file.
     *
     * @param outEncoding encoding name
     */
    public void setTranslationEncoding(String outEncoding) {
        this.translateEncoding = outEncoding;
    }

    /**
     * get project properties.
     *
     * @return project properties
     */
    public ProjectProperties getProjectProperties() {
        return props;
    }
}

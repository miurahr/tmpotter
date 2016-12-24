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
 * *************************************************************************/

package org.tmpotter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmpotter.util.Language;
import org.tmpotter.util.TmxWriter2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * save to TMX file.
 *
 * @author miurahr
 */
public class TmxpWriter {

    /**
     * Write TMX file.
     *
     * @param pprop Project properties.
     * @param originalDocument    original document object
     * @param translationDocument translation document object
     * @throws Exception when file write error
     */
    public static void writeTmxp(final ProjectProperties pprop,
                                 Document originalDocument,
                                 Document translationDocument
    ) throws Exception {
        File outFile = pprop.getFilePathProject();
        Language sourceLanguage = pprop.getSourceLanguage();
        Language targetLanguage = pprop.getTargetLanguage();

        Map<String, String> headerProp = new TreeMap<>();
        headerProp.put("sourceLang", sourceLanguage.toString());
        headerProp.put("targetLang", targetLanguage.toString());

        TmxWriter2 wr = new TmxWriter2(outFile, sourceLanguage,
                targetLanguage, true, false, false,
                headerProp);
        try {
            HashMap<String, String> prop = new HashMap<>();
            for (int i = 0; i < originalDocument.size(); i++) {
                TmxEntry te = new TmxEntry();
                te.source = originalDocument.get(i);
                te.translation = translationDocument.get(i);
                wr.writeEntry(te.source, te.translation, te, prop);
            }
        } finally {
            wr.close();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TmxpWriter.class.getName());

}

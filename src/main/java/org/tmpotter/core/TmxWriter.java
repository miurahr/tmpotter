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


/**
 * save to TMX file.
 *
 * @author miurahr
 */
public class TmxWriter {

    /**
     * Write TMX file.
     *
     * @param outFile             filename for output
     * @param originalDocument    original document object
     * @param langOriginal        original document language
     * @param translationDocument translation document object
     * @param langTranslation     translation language
     * @throws Exception when file write error
     */
    public static void writeTmx(final File outFile,
                                Document originalDocument, String langOriginal,
                                Document translationDocument, String langTranslation
    ) throws Exception {
        Language sourceLanguage = new Language(langOriginal);
        Language targetLanguage = new Language(langTranslation);

        TmxWriter2 wr = new TmxWriter2(outFile, sourceLanguage,
            targetLanguage, true, false, false);
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TmxWriter.class.getName());

}

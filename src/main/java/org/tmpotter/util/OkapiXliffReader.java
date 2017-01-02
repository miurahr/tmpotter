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

package org.tmpotter.util;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.xliff.XLIFFFilter;
//import net.sf.okapi.common.Event;

import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Unit;
//import net.sf.okapi.lib.xliff2.reader.Event;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;


import java.io.File;

/**
 * XLIFF1 reader utility.
 * @author Hiroshi Miura
 */
public class OkapiXliffReader {
    /**
     * Read XLIFF version 2.0 file.
     * @param inFile input file.
     * @param callback callback for each entry.
     * @param parent parent object for callback.
     * @throws Exception when I/O error happened or XLIFF format error.
     */
    public static void readXliff2(File inFile, org.tmpotter.filters.IParseCallback callback,
            org.tmpotter.filters.IFilter parent) throws Exception {
        try (XLIFFReader reader = new XLIFFReader()) {
            reader.open(inFile);
            while (reader.hasNext()) {
                net.sf.okapi.lib.xliff2.reader.Event event = reader.next();
                if (event.isUnit()) {
                    Unit unit = event.getUnit();
                    for (Segment segment : unit.getSegments()) {
                        callback.addEntry(null, segment.getSource().getPlainText(),
                                segment.getTarget().getPlainText(), false,
                                "", null, parent);
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Read XLIFF version 1.1/1.4 file.
     * @param inFile input file.
     * @param sourceLang source langauage.
     * @param targetLang target translation language.
     * @param callback callback for entry.
     * @param parent parent object for callback.
     */
    public static void readXliff1(File inFile, String sourceLang, String targetLang,
                                  org.tmpotter.filters.IParseCallback callback,
                                  org.tmpotter.filters.IFilter parent) {
        IFilter filter = new XLIFFFilter();
        LocaleId sourceId;
        LocaleId targetId;

        if (!StringUtil.isEmpty(sourceLang)) {
            sourceId = new LocaleId(sourceLang);
        } else {
            sourceId = new LocaleId("en");
        }
        if (!StringUtil.isEmpty(targetLang)) {
            targetId = new LocaleId(targetLang);
        } else {
            targetId = new LocaleId("ja");
        }
        filter.open(new RawDocument(inFile.toURI(), "UTF-8", sourceId, targetId));
        while (filter.hasNext()) {
            net.sf.okapi.common.Event event = filter.next();
            if (event.isTextUnit()) {
                ITextUnit unit = event.getTextUnit();
                TextContainer source = unit.getSource();
                if (unit.hasTarget(targetId)) {
                    TextContainer target = unit.getTarget(targetId);
                    callback.addEntry(null, source.getCodedText(),
                            target.getCodedText(), false,
                            "", null, parent);
                }
            }
        }
    }
}

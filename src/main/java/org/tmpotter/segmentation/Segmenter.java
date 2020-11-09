/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2020 Hiroshi Miura
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

package org.tmpotter.segmentation;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.Range;
import net.sf.okapi.lib.segmentation.SRXDocument;
import org.tmpotter.util.Language;

import java.util.ArrayList;
import java.util.List;


public class Segmenter {
    private final ISegmenter segmenter;

    public Segmenter(Language lang) {
        SRXDocument doc = new SRXDocument();
        doc.setCascade(true);
        doc.loadRules(getClass().getResourceAsStream("defaultRules.srx"));
        segmenter = doc.compileLanguageRules(lang.getLocaleId(), null);
        segmenter.setOptions(false, false, true, false, false, true, true);
    }

    public List<String> segment(String content) {
        segmenter.computeSegments(content);
        List<Range> ranges = segmenter.getRanges();
        List<String> result = new ArrayList<>();
        ranges.forEach(elem -> result.add(content.substring(elem.start, elem.end)));
        return result;
    }
}

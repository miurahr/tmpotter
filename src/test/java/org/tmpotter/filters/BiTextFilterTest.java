/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  Copyright (C) 2008 Alex Buloichik
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

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import org.tmpotter.core.Alignment;
import org.tmpotter.util.Language;
import org.tmpotter.util.Localization;
import org.tmpotter.filters.bitext.BiTextFilter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * BiTextFilter test.
 *
 * @author Hiroshi Miura
 */
public class BiTextFilterTest extends TestFilterBase {

    @Test
    public void testLoad() throws Exception {
        Map<String, String> options = new TreeMap<>();
        context = new FilterContext(new Language("en"), new Language("ja"), true);
        context.setSourceEncoding("UTF-8");
        context.setTranslationEncoding("UTF-8");
        List<ParsedEntry> result = parse3(new BiTextFilter(),
                this.getClass().getResource("/text/src.txt").getFile(),
                this.getClass().getResource("/text/trans.txt").getFile(),
                options);
        assertEquals(result.get(0).source, "'''Android''' is a [[Linux]]-based operating system for mobile phones and other mobile devices.");
        assertEquals(result.get(0).translation, "'''Android'''は、携帯電話やモバイル機器の[[Linux]]ベースの操作環境です。");
    }
}

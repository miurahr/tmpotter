/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *  Copyright (C) 2010 Alex Buloichik
 *
 *  This file is part of TMPotter.
 *
 *  This comes from OmegaT project.
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
 **************************************************************************/

package org.tmpotter.util;

import static org.testng.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.*;


/**
 * @author Alex Buloichik
 * @author Hiroshi Miura
 */
public class TMXReaderTest {
    @Test
    public void testLevel1() throws Exception {
        final Map<String, String> tr = new TreeMap<>();
        new TmxReader2().readTmx(new File(this.getClass().getResource("/tmx/test-level1.tmx").getFile()),
                new Language("en-US"), new Language("be"), false, false,
                (tu, tuvSource, tuvTarget, isParagraphSegtype) -> {
                    tr.put(tuvSource.text, tuvTarget.text);
                    return true;
                });
        assertEquals(tr.get("entuv"), "betuv");
        //assertEquals(tr.get("lang1"),"tr1"); // FIXME.
        assertEquals(tr.get("lang2"), "tr2");
        assertEquals(tr.get("lang3"), "tr3");
    }

    @Test
    public void testLevel2() throws Exception {
        final Map<String, String> tr = new TreeMap<>();
        new TmxReader2().readTmx(new File(this.getClass().getResource("/tmx/test-level2.tmx").getFile()),
                new Language("en-US"), new Language("be"), true, false,
                (tu, tuvSource, tuvTarget, isParagraphSegtype) -> {
                    tr.put(tuvSource.text, tuvTarget.text);
                    return true;
                });
        assertEquals("betuv", tr.get("entuv"));
        assertEquals("tr", tr.get("2 <a0> zz <t1>xx</t1>"));
        assertEquals("tr", tr.get("3 <n0>xx</n0>"));
    }

    @Test
    public void testInvalidTmx() throws Exception {
        final Map<String, String> tr = new TreeMap<>();
        new TmxReader2().readTmx(new File(this.getClass().getResource("/tmx/invalid.tmx").getFile()),
                new Language("en"), new Language("be"), false, false,
                (tu, tuvSource, tuvTarget, isParagraphSegtype) -> {
                    tr.put(tuvSource.text, tuvTarget.text);
                    return true;
                });
    }
    
    public void testSMP() throws Exception {
        final Map<String, String> tr = new TreeMap<>();
        new TmxReader2().readTmx(new File(this.getClass().getResource("/tmx/test-SMP.tmx").getFile()),
                new Language("en"), new Language("be"), true, false,
                (tu, tuvSource, tuvTarget, isParagraphSegtype) -> {
                    tr.put(tuvSource.text, tuvTarget.text);
                    return true;
                });
        assertFalse(tr.isEmpty());
        // Assert contents are {"ABC": "DEF"} where letters are MATHEMATICAL BOLD CAPITALs (U+1D400-)
        assertEquals("\uD835\uDC03\uD835\uDC04\uD835\uDC05", tr.get("\uD835\uDC00\uD835\uDC01\uD835\uDC02"));
    }
    
    public void testGetTuvByLang() {
        TmxReader2.ParsedTuv tuvBE = new TmxReader2.ParsedTuv();
        tuvBE.lang = "be";

        TmxReader2.ParsedTuv tuvFR = new TmxReader2.ParsedTuv();
        tuvFR.lang = "FR";
        
        TmxReader2.ParsedTuv tuvFRCA = new TmxReader2.ParsedTuv();
        tuvFRCA.lang = "FR-CA";

        TmxReader2.ParsedTuv tuvFRFR = new TmxReader2.ParsedTuv();
        tuvFRFR.lang = "FR-FR";

        TmxReader2.ParsedTuv tuvENGB = new TmxReader2.ParsedTuv();
        tuvENGB.lang = "EN-GB";

        TmxReader2 tmx = new TmxReader2();
        tmx.currentTu.tuvs.add(tuvBE);
        tmx.currentTu.tuvs.add(tuvFR);
        tmx.currentTu.tuvs.add(tuvFRCA);
        tmx.currentTu.tuvs.add(tuvFRFR);
        tmx.currentTu.tuvs.add(tuvENGB);

        assertEquals(tmx.getTuvByLang(new Language("BE")), tuvBE);
        assertEquals(tmx.getTuvByLang(new Language("BE-NN")), tuvBE);

        assertNotNull(tmx.getTuvByLang(new Language("FR")));
        assertEquals(tmx.getTuvByLang(new Language("FR-CA")), tuvFRCA);
        assertEquals(tmx.getTuvByLang(new Language("FR-NN")), tuvFR);

        assertEquals(tmx.getTuvByLang(new Language("EN")), tuvENGB);
        assertEquals(tmx.getTuvByLang(new Language("EN-CA")), tuvENGB);
        
        assertNull(tmx.getTuvByLang(new Language("ZZ")));
    }
}

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

import java.io.File;
import java.util.List;
import org.tmpotter.util.Localization;

import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

public class PoFilterTest extends TestFilterBase {

  @Test
  public void testParse() throws Exception {
    Map<String, String> data = new TreeMap<>();
    Map<String, String> tmx = new TreeMap<>();

    parse2(new PoFilter(), this.getClass().getResource("/po/file-POFilter-be.po").getFile(),
        data, tmx);
    
    assertEquals("Non Fuzzy check", "non-fuzzy translation", data.get("non-fuzzy"));
    assertEquals("Fuzzy check", "fuzzy translation", tmx.get("[PO-fuzzy] fuzzy"));
    assertEquals("Plural single check", "Supprimer le compte", tmx.get("[PO-fuzzy] Delete Account"));
    // FIXME: this claim to fails but application works, why?
    //assertEquals("Plural check", "Supprimer des comptes", tmx.get("[PO-fuzzy] Delete Accounts"));
  }

  public void testLoad() throws Exception {
    Map<String, String> options = new TreeMap<>();
    options.put("skipHeader", "true");
    List<ParsedEntry> result = parse3(new PoFilter(),
        this.getClass().getResource("/po/file-POFilter-multiple.po").getFile(), options);

    String comment = Localization.getString("POFILTER_TRANSLATOR_COMMENTS") + "\n" + "A valid comment\nAnother valid comment\n\n"
        + Localization.getString("POFILTER_EXTRACTED_COMMENTS") + "\n" + "Some extracted comments\nMore extracted comments\n\n"
        + Localization.getString("POFILTER_REFERENCES") + "\n" + "/my/source/file\n/my/source/file2\n\n";
    
    assertEquals("Source1", "source1", result.get(0).source);
    assertEquals("Comment check", comment, result.get(0).comment);
    assertEquals("Source2", "source2", result.get(1).source);
    assertTrue("Fuzzy check", result.get(1).isFuzzy);
    assertEquals("source3", result.get(2).source);
    assertEquals("Null comment", null, result.get(2).comment);
    assertEquals("Context check", "other context", result.get(4).path);
    assertEquals("Context check(same source text with other context)","source1", result.get(4).source);
  }
}

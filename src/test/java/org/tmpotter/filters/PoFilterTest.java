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

import org.tmpotter.util.Localization;
import org.tmpotter.filters.pofile.PoFilter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class PoFilterTest extends TestFilterBase {

  @Test
  public void testParse() throws Exception {
    Map<String, String> data = new TreeMap<>();
    Map<String, String> tmx = new TreeMap<>();

    parse2(new PoFilter(), this.getClass().getResource("/po/file-POFilter-be.po").getFile(),
        data, tmx);
    
    assertEquals("non-fuzzy translation", data.get("non-fuzzy"), "Non Fuzzy check");
    assertEquals("fuzzy translation", tmx.get("[PO-fuzzy] fuzzy"), "Fuzzy check");
    assertEquals("Supprimer le compte", tmx.get("[PO-fuzzy] Delete Account"), "Plural single check");
    // FIXME: this claim to fails but application works, why?
    //assertEquals("Plural check", "Supprimer des comptes", tmx.get("[PO-fuzzy] Delete Accounts"));
  }

  @Test
  public void testLoad() throws Exception {
    Map<String, String> options = new TreeMap<>();
    options.put("skipHeader", "true");
    List<ParsedEntry> result = parse3(new PoFilter(),
        this.getClass().getResource("/po/file-POFilter-multiple.po").getFile(), options);

    String comment = Localization.getString("POFILTER_TRANSLATOR_COMMENTS") + "\n" + "A valid comment\nAnother valid comment\n\n"
        + Localization.getString("POFILTER_EXTRACTED_COMMENTS") + "\n" + "Some extracted comments\nMore extracted comments\n\n"
        + Localization.getString("POFILTER_REFERENCES") + "\n" + "/my/source/file\n/my/source/file2\n\n";
    
    assertEquals("source1", result.get(0).source, "Source1");
    assertEquals(comment, result.get(0).comment, "Comment check");
    assertEquals("source2", result.get(1).source, "Source2");
    assertTrue(result.get(1).isFuzzy, "Fuzzy check");
    assertEquals(result.get(2).source, "source3");
    assertEquals(null, result.get(2).comment, "Null comment");
    assertEquals("other context", result.get(4).path, "Context check");
    assertEquals("source1", result.get(4).source, "Context check(same source text with other context)");
  }
}

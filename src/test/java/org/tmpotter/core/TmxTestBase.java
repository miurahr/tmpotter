/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  This file come from OmegaT project
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
 **************************************************************************/

package org.tmpotter.core;

import static org.junit.Assert.*;

import org.tmpotter.util.AppConstants;

import org.custommonkey.xmlunit.XMLUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Hiroshi Miura
 */
public class TmxTestBase {
  static Pattern RE_SEG = Pattern.compile("(<seg>.+</seg>)");

  protected Set<String> readTmxSegments(File tmx) throws Exception {
    BufferedReader rd 
            = new BufferedReader(new InputStreamReader(new FileInputStream(tmx),
                    AppConstants.ENCODINGS_UTF8));
    String s;
    Set<String> entries = new TreeSet<String>();
    while ((s = rd.readLine()) != null) {
      Matcher m = RE_SEG.matcher(s);
      if (m.find()) {
        entries.add(m.group(1));
      }
    }
    rd.close();
    return entries;
  }

  protected void compareTmx(File orig, File created, int count) throws Exception {
    Set<String> tmxOrig = readTmxSegments(orig);
    Set<String> tmxCreated = readTmxSegments(created);
    assertEquals(count, tmxCreated.size());
    assertEquals(tmxOrig.size(), tmxCreated.size());

    List<String> listOrig = new ArrayList<>(tmxOrig);
    List<String> listCreated = new ArrayList<>(tmxCreated);
    for (int i = 0; i < listOrig.size(); i++) {
      XMLUnit.compareXML(listOrig.get(i), listCreated.get(i));
    }
  }
}

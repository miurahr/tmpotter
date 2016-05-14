/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
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
 **************************************************************************/

package org.tmpotter.segmentation;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tmpotter.util.Language;


/**
 *
 * @author Hiroshi Miura
 */
public class SegmenterTest {
  /**
   * Test of segment method, of class Segmenter.
   */
  @Test
  public void testSegment() {
    System.out.println("segment");
    Segmenter.srx = SRX.getDefault();
    Language lang = new Language("en");
    String paragraph = "'''Android''' is a [[Linux]]-based operating system for"
            + "mobile phones and other mobile devices.\n"
            + "For general information, see [[Wikipedia:Android_(operating_system"
            + ")|Wikipedia's page on Android]] "
            + "or [https://android.com/ android.com].";
    List<StringBuilder> spaces = new ArrayList<>();
    List<Rule> brules = new ArrayList<>();
    List<String> expResult = Arrays.asList(
            "'''Android''' is a [[Linux]]-based operating system for"
            + "mobile phones and other mobile devices.",
            "For general information, see [[Wikipedia:Android_(operating_system"
            + ")|Wikipedia's page on Android]] "
            + "or [https://android.com/ android.com].");
    List<String> result = Segmenter.segment(lang, paragraph, spaces, brules);
    assertEquals(expResult, result);
  }
}

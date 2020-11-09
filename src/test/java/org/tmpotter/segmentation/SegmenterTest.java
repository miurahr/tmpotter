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

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.SRXSegmenter;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tmpotter.util.Language;
import org.tmpotter.segmentation.Segmenter;


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
    Language lang = new Language("en");
    Segmenter segmenter = new Segmenter(lang);
    String paragraph = "'''Android''' is a [[Linux]]-based operating system for"
            + "mobile phones and other mobile devices.\n"
            + "For general information, see [[Wikipedia:Android_(operating_system"
            + ")|Wikipedia's page on Android]] "
            + "or [https://android.com/ android.com].";
    List<String> expResult = Arrays.asList(
            "'''Android''' is a [[Linux]]-based operating system for"
            + "mobile phones and other mobile devices.",
            "For general information, see [[Wikipedia:Android_(operating_system"
            + ")|Wikipedia's page on Android]] "
            + "or [https://android.com/ android.com].");
    List<String> result = segmenter.segment(paragraph);
    assertEquals(result.get(0), expResult.get(0));
  }

  /**
   * Test of segment method, of class Segmenter.
   */
  @Test
  public void testSegmentJa() {
    System.out.println("segment");
    Language lang = new Language("ja");
    Segmenter segmenter = new Segmenter(lang);
    String paragraph = "'''Android'''は、携帯電話やモバイル機器の[[Linux]]ベースの操作環境です。" +
            "全般的な情報については、 " +
            "[[Wikipedia:Android_(operating_system)|WikipediaのAndroidの項]] や" +
            " [https://android.com/ android.com]を参照してください。";
    List<String> expResult = Arrays.asList("'''Android'''は、携帯電話やモバイル機器の[[Linux]]ベースの操作環境です。",
            "全般的な情報については、 [[Wikipedia:Android_(operating_system)|WikipediaのAndroidの項]] や" +
            " [https://android.com/ android.com]を参照してください。");
    List<String> result = segmenter.segment(paragraph);
    assertEquals(result.get(0), expResult.get(0));
  }
}

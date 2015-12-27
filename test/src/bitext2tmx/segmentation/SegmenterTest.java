/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package bitext2tmx.segmentation;

import bitext2tmx.util.Language;
import bitext2tmx.util.Preferences;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miurahr
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
    List<StringBuilder> spaces = null;
    List<Rule> brules = null;
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

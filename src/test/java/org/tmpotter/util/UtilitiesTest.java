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

package org.tmpotter.util;

import java.io.File;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


/**
 *
 * @author miurahr
 */
public class UtilitiesTest {
  
  /**
   * Test of getFontNames method, of class Utilities.
   */
  @Test
  public void testGetFontNames() {
    System.out.println("getFontNames");
    String[] result = Utilities.getFontNames();
    assertNotNull(result);
  }

  /**
   * Test of joinString method, of class Utilities.
   */
  @Test
  public void testJoinString() {
    System.out.println("joinString");
    String separator = ",";
    String[] items = {"first","second","third"};
    String expResult = "first,second,third";
    String result = StringUtil.joinString(separator, items);
    assertEquals(expResult, result);
  }

  /**
   * Test of printUTF8 method, of class Utilities.
   */
  @Test
  public void testPrintUTF8() {
    System.out.println("printUTF8");
    String output = "test output";
    Utilities.printUtf8(output);
  }

  /**
   * Test of saveUTF8 method, of class Utilities.
   */
  @Test
  public void testSaveUTF8() {
    System.out.println("saveUTF8");
    String dir = this.getClass().getResource("/").getFile();
    String filename = "save_utf8_result.txt";
    String output = "\u3401\u3402";
    try {
      Utilities.saveUtf8(dir, filename, output);
      File target = new File(this.getClass().getResource("/save_utf8_result.txt").getFile());
      File expected = new File(this.getClass().getResource("/save_utf8_expected.txt").getFile()); 
      assertTrue(FileUtil.compareFile(target, expected));
      target.delete();
    } catch (Exception ex) {
      System.out.println(ex);
      fail();
    }
  }
  
  /**
   * Test of getConfigDir method, of class Utilities.
   */
  @Test
  public void testGetConfigDir() {
    System.out.println("getConfigDir");

    String expResult1 = ".tmpotter" + File.separator; // Unix
    String expResult2 = new File(".").getAbsolutePath() + File.separator; // no HOME env
    String expResult3 = "TMPotter" + File.separator; // Win/Mac
    String expResult4 = "TMPOTTER" + File.separator;
    String result = Utilities.getConfigDir();
    assertTrue(result.endsWith(expResult1) ||
               result.endsWith(expResult2) ||
               result.endsWith(expResult3) ||
               result.endsWith(expResult4));
  }

  /**
   * Test of installDir method, of class Utilities.
   */
  @Test
  public void testInstallDir() {
    System.out.println("installDir");
    String expResult = "tmpotter/build";
    String result = Utilities.installDir();
    assertTrue(result.endsWith(expResult));
  }

  /**
   * Test of largerSize method, of class Utilities.
   */
  @Test
  public void testLargerSize() {
    System.out.println("largerSize");
    int a = 1;
    int b = 2;
    int expResult = 2;
    int result = Utilities.largerSize(a, b);
    assertEquals(expResult, result);
  }
}

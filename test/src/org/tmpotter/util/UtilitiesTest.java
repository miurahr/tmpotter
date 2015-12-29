/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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
import junit.framework.TestCase;

/**
 *
 * @author miurahr
 */
public class UtilitiesTest extends TestCase {
  
  public UtilitiesTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of getFontNames method, of class Utilities.
   */
  public void testGetFontNames() {
    System.out.println("getFontNames");
    String[] result = Utilities.getFontNames();
    assertNotNull(result);
  }

  /**
   * Test of joinString method, of class Utilities.
   */
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
  public void testPrintUTF8() {
    System.out.println("printUTF8");
    String output = "test output";
    Utilities.printUtf8(output);
  }

  /**
   * Test of saveUTF8 method, of class Utilities.
   */
  public void testSaveUTF8() {
    System.out.println("saveUTF8");
    String dir = "test/data/";
    String filename = "save_utf8_result.txt";
    String output = "\u3401\u3402";
    String expectedFN = "save_utf8_expected.txt";
    try {
      Utilities.saveUtf8(dir, filename, output);
      File target = new File(dir+filename);
      File expected = new File(dir+expectedFN); 
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
  public void testGetConfigDir() {
    System.out.println("getConfigDir");
    String expResult = "/.tmpotter/";
    String result = Utilities.getConfigDir();
    assertTrue(result.endsWith(expResult));
  }

  /**
   * Test of installDir method, of class Utilities.
   */
  public void testInstallDir() {
    System.out.println("installDir");
    String expResult = "tmpotter/build";
    String result = Utilities.installDir();
    assertTrue(result.endsWith(expResult));
  }

  /**
   * Test of largerSize method, of class Utilities.
   */
  public void testLargerSize() {
    System.out.println("largerSize");
    int a = 1;
    int b = 2;
    int expResult = 2;
    int result = Utilities.largerSize(a, b);
    assertEquals(expResult, result);
  }
  
}

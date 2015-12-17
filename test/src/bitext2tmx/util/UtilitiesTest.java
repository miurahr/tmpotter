/*
 * Copyright (C) 2015 miurahr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bitext2tmx.util;

import java.io.BufferedWriter;
import java.io.OutputStream;
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
    String[] expResult = null;
    String[] result = Utilities.getFontNames();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getValidXMLChar method, of class Utilities.
   */
  public void testGetValidXMLChar() {
    System.out.println("getValidXMLChar");
    char c = ' ';
    String expResult = "";
    String result = Utilities.getValidXMLChar(c);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getValidXMLText method, of class Utilities.
   */
  public void testGetValidXMLText() {
    System.out.println("getValidXMLText");
    String plaintext = "";
    String expResult = "";
    String result = Utilities.getValidXMLText(plaintext);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isLinux method, of class Utilities.
   */
  public void testIsLinux() {
    System.out.println("isLinux");
    boolean expResult = false;
    boolean result = Utilities.isLinux();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isMacOSX method, of class Utilities.
   */
  public void testIsMacOSX() {
    System.out.println("isMacOSX");
    boolean expResult = false;
    boolean result = Utilities.isMacOSX();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isUnix method, of class Utilities.
   */
  public void testIsUnix() {
    System.out.println("isUnix");
    boolean expResult = false;
    boolean result = Utilities.isUnix();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isWindows method, of class Utilities.
   */
  public void testIsWindows() {
    System.out.println("isWindows");
    boolean expResult = false;
    boolean result = Utilities.isWindows();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of fixChars method, of class Utilities.
   */
  public void testFixChars() {
    System.out.println("fixChars");
    String str = "";
    String expResult = "";
    String result = Utilities.fixChars(str);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of joinString method, of class Utilities.
   */
  public void testJoinString() {
    System.out.println("joinString");
    String separator = "";
    String[] items = null;
    String expResult = "";
    String result = Utilities.joinString(separator, items);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of printUTF8 method, of class Utilities.
   */
  public void testPrintUTF8() {
    System.out.println("printUTF8");
    String output = "";
    Utilities.printUTF8(output);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of UTF8WriterBuilder method, of class Utilities.
   */
  public void testUTF8WriterBuilder() throws Exception {
    System.out.println("UTF8WriterBuilder");
    OutputStream out = null;
    BufferedWriter expResult = null;
    BufferedWriter result = Utilities.UTF8WriterBuilder(out);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of saveUTF8 method, of class Utilities.
   */
  public void testSaveUTF8() {
    System.out.println("saveUTF8");
    String dir = "";
    String filename = "";
    String output = "";
    Utilities.saveUTF8(dir, filename, output);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of format method, of class Utilities.
   */
  public void testFormat() {
    System.out.println("format");
    String str = "";
    Object[] arguments = null;
    String expResult = "";
    String result = Utilities.format(str, arguments);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isEmpty method, of class Utilities.
   */
  public void testIsEmpty() {
    System.out.println("isEmpty");
    String str = "";
    boolean expResult = false;
    boolean result = Utilities.isEmpty(str);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of removeXMLInvalidChars method, of class Utilities.
   */
  public void testRemoveXMLInvalidChars() {
    System.out.println("removeXMLInvalidChars");
    String str = "";
    String expResult = "";
    String result = Utilities.removeXMLInvalidChars(str);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isValidXMLChar method, of class Utilities.
   */
  public void testIsValidXMLChar() {
    System.out.println("isValidXMLChar");
    int codePoint = 0;
    boolean expResult = false;
    boolean result = Utilities.isValidXMLChar(codePoint);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of escapeXMLChars method, of class Utilities.
   */
  public void testEscapeXMLChars() {
    System.out.println("escapeXMLChars");
    int cp = 0;
    String expResult = "";
    String result = Utilities.escapeXMLChars(cp);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of makeValidXML method, of class Utilities.
   */
  public void testMakeValidXML() {
    System.out.println("makeValidXML");
    String plaintext = "";
    String expResult = "";
    String result = Utilities.makeValidXML(plaintext);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getConfigDir method, of class Utilities.
   */
  public void testGetConfigDir() {
    System.out.println("getConfigDir");
    String expResult = "";
    String result = Utilities.getConfigDir();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of installDir method, of class Utilities.
   */
  public void testInstallDir() {
    System.out.println("installDir");
    String expResult = "";
    String result = Utilities.installDir();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of cost method, of class Utilities.
   */
  public void testCost() {
    System.out.println("cost");
    float[][] mat = null;
    int i = 0;
    int j = 0;
    float[] v1 = null;
    float[] v2 = null;
    int d = 0;
    float expResult = 0.0F;
    float result = Utilities.cost(mat, i, j, v1, v2, d);
    assertEquals(expResult, result, 0.0);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of min3 method, of class Utilities.
   */
  public void testMin3() {
    System.out.println("min3");
    float a = 0.0F;
    float b = 0.0F;
    float c = 0.0F;
    float expResult = 0.0F;
    float result = Utilities.min3(a, b, c);
    assertEquals(expResult, result, 0.0);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of max3 method, of class Utilities.
   */
  public void testMax3() {
    System.out.println("max3");
    float a = 0.0F;
    float b = 0.0F;
    float c = 0.0F;
    float expResult = 0.0F;
    float result = Utilities.max3(a, b, c);
    assertEquals(expResult, result, 0.0);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of argmin3 method, of class Utilities.
   */
  public void testArgmin3() {
    System.out.println("argmin3");
    float a = 0.0F;
    float b = 0.0F;
    float c = 0.0F;
    int expResult = 0;
    int result = Utilities.argmin3(a, b, c);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of argmax3 method, of class Utilities.
   */
  public void testArgmax3() {
    System.out.println("argmax3");
    float a = 0.0F;
    float b = 0.0F;
    float c = 0.0F;
    int expResult = 0;
    int result = Utilities.argmax3(a, b, c);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isAlignedOKES method, of class Utilities.
   */
  public void testIsAlignedOKES() {
    System.out.println("isAlignedOKES");
    float[] v1 = null;
    float[] v2 = null;
    int i = 0;
    int j = 0;
    boolean expResult = false;
    boolean result = Utilities.isAlignedOKES(v1, v2, i, j);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isAlignedOKSE method, of class Utilities.
   */
  public void testIsAlignedOKSE() {
    System.out.println("isAlignedOKSE");
    float[] v1 = null;
    float[] v2 = null;
    int i = 0;
    int j = 0;
    boolean expResult = false;
    boolean result = Utilities.isAlignedOKSE(v1, v2, i, j);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of largerSize method, of class Utilities.
   */
  public void testLargerSize() {
    System.out.println("largerSize");
    int a = 0;
    int b = 0;
    int expResult = 0;
    int result = Utilities.largerSize(a, b);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}

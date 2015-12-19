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
package bitext2tmx.util.xml;

import junit.framework.TestCase;

/**
 *
 * @author miurahr
 */
public class XMLUtilTest extends TestCase {
  
  public XMLUtilTest(String testName) {
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
   * Test of getValidXMLText method, of class XMLUtil.
   */
  public void testGetValidXMLText() {
    System.out.println("getValidXMLText");
    String plaintext = "";
    String expResult = "";
    String result = XMLUtil.getValidXMLText(plaintext);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of removeXMLInvalidChars method, of class XMLUtil.
   */
  public void testRemoveXMLInvalidChars() {
    System.out.println("removeXMLInvalidChars");
    String str = "";
    String expResult = "";
    String result = XMLUtil.removeXMLInvalidChars(str);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isValidXMLChar method, of class XMLUtil.
   */
  public void testIsValidXMLChar() {
    assertFalse(XMLUtil.isValidXMLChar(0x01));
    assertTrue(XMLUtil.isValidXMLChar(0x09));
    assertTrue(XMLUtil.isValidXMLChar(0x0A));
    assertTrue(XMLUtil.isValidXMLChar(0x0D));

    assertTrue(XMLUtil.isValidXMLChar(0x21));
    assertFalse(XMLUtil.isValidXMLChar(0xD800));

    assertTrue(XMLUtil.isValidXMLChar(0xE000));
    assertFalse(XMLUtil.isValidXMLChar(0xFFFE));

    assertTrue(XMLUtil.isValidXMLChar(0x10000));
    assertFalse(XMLUtil.isValidXMLChar(0x110000));
  }

  /**
   * Test of escapeXMLChars method, of class XMLUtil.
   */
  public void testEscapeXMLChars() {
    System.out.println("escapeXMLChars");
    int cp = 0;
    String expResult = "";
    String result = XMLUtil.escapeXMLChars(cp);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of makeValidXML method, of class XMLUtil.
   */
  public void testMakeValidXML() {
    System.out.println("makeValidXML");
    String plaintext = "";
    String expResult = "";
    String result = XMLUtil.makeValidXML(plaintext);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of fixChars method, of class XMLUtil.
   */
  public void testFixChars() {
    System.out.println("fixChars");
    String str = "";
    String expResult = "";
    String result = XMLUtil.fixChars(str);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}

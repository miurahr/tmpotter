/**************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of tmpotter.
 *
 *  This file come from OmegaT project
 *
 *  Copyright (C) 2008 Alex Buloichik
 *
 *  tmpotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  tmpotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with tmpotter.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package org.tmpotter.util.xml;

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
    String plaintext = "hoge<tag>fuga";
    String expResult = "hoge&lt;tag&gt;fuga";
    String result = XMLUtil.getValidXMLText(plaintext);
    assertEquals(expResult, result);
  }

  /**
   * Test of removeXMLInvalidChars method, of class XMLUtil.
   */
  public void testRemoveXMLInvalidChars() {
    System.out.println("removeXMLInvalidChars");
    String str = "test\u000Btest";
    String expResult = "test test";
    String result = XMLUtil.removeXMLInvalidChars(str);
    assertEquals(expResult, result);
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

}

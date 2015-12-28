/* *************************************************************************
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
 * ************************************************************************/
package bitext2tmx.core;

import bitext2tmx.util.Language;
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
public class ProjectPropertiesTest {
  
  public ProjectPropertiesTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of getSourceLanguage method, of class ProjectProperties.
   */
  @Test
  public void testGetSourceLanguage() {
    System.out.println("getSourceLanguage");
    ProjectProperties instance = new ProjectProperties();
    Language expResult = null;
    Language result = instance.getSourceLanguage();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setSourceLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetSourceLanguage_Language() {
    System.out.println("setSourceLanguage");
    Language sourceLanguage = null;
    ProjectProperties instance = new ProjectProperties();
    instance.setSourceLanguage(sourceLanguage);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setSourceLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetSourceLanguage_String() {
    System.out.println("setSourceLanguage");
    String sourceLanguage = "";
    ProjectProperties instance = new ProjectProperties();
    instance.setSourceLanguage(sourceLanguage);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getTargetLanguage method, of class ProjectProperties.
   */
  @Test
  public void testGetTargetLanguage() {
    System.out.println("getTargetLanguage");
    ProjectProperties instance = new ProjectProperties();
    Language expResult = null;
    Language result = instance.getTargetLanguage();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setTargetLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetTargetLanguage_Language() {
    System.out.println("setTargetLanguage");
    Language targetLanguage = null;
    ProjectProperties instance = new ProjectProperties();
    instance.setTargetLanguage(targetLanguage);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setTargetLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetTargetLanguage_String() {
    System.out.println("setTargetLanguage");
    String targetLanguage = "";
    ProjectProperties instance = new ProjectProperties();
    instance.setTargetLanguage(targetLanguage);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of verifySingleLangCode method, of class ProjectProperties.
   */
  @Test
  public void testVerifySingleLangCode() {
    System.out.println("verifySingleLangCode");
    String code = "";
    boolean expResult = false;
    boolean result = ProjectProperties.verifySingleLangCode(code);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}

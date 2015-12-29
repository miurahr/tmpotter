/* *************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of tmpotter.
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
 * ************************************************************************/
package org.tmpotter.core;

import org.tmpotter.util.Language;
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
  public void testSetSourceLanguage_Language() {
    System.out.println("getSourceLanguage");
    ProjectProperties instance = new ProjectProperties();
    Language sourceLanguage = new Language("en");
    instance.setSourceLanguage(sourceLanguage);
    Language expResult = sourceLanguage;
    Language result = instance.getSourceLanguage();
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of setSourceLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetSourceLanguage_String() {
    System.out.println("setSourceLanguage");
    String sourceLanguage = "en";
    ProjectProperties instance = new ProjectProperties();
    instance.setSourceLanguage(sourceLanguage);
    Language expResult = new Language(sourceLanguage);
    Language result = instance.getSourceLanguage();
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of getTargetLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetTargetLanguage_Language() {
    System.out.println("getTargetLanguage");
    ProjectProperties instance = new ProjectProperties();
    Language targetLanguage = new Language("JA");
    instance.setTargetLanguage(targetLanguage);
    Language expResult = targetLanguage;
    Language result = instance.getTargetLanguage();
    assertEquals(expResult, result);
  }

  /**
   * Test of setTargetLanguage method, of class ProjectProperties.
   */
  @Test
  public void testSetTargetLanguage_String() {
    System.out.println("setTargetLanguage");
    String targetLanguage = "JA";
    ProjectProperties instance = new ProjectProperties();
    instance.setTargetLanguage(targetLanguage);
    Language expResult = new Language(targetLanguage);
    Language result = instance.getTargetLanguage();
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of verifySingleLangCode method, of class ProjectProperties.
   */
  @Test
  public void testVerifySingleLangCode_True() {
    System.out.println("verifySingleLangCode");
    String code = "EN";
    boolean result = ProjectProperties.verifySingleLangCode(code);
    assertTrue(result);
  }
  
}

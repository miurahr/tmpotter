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

import junit.framework.TestCase;

/**
 *
 * @author miurahr
 */
public class PreferencesTest extends TestCase {
  
  public PreferencesTest(String testName) {
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
   * Test of getPreference method, of class Preferences.
   */
  public void testGetPreference() {
    System.out.println("getPreference");
    String key = "";
    String expResult = "";
    String result = Preferences.getPreference(key);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of existsPreference method, of class Preferences.
   */
  public void testExistsPreference() {
    System.out.println("existsPreference");
    String key = "";
    boolean expResult = false;
    boolean result = Preferences.existsPreference(key);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isPreference method, of class Preferences.
   */
  public void testIsPreference() {
    System.out.println("isPreference");
    String key = "";
    boolean expResult = false;
    boolean result = Preferences.isPreference(key);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of isPreferenceDefault method, of class Preferences.
   */
  public void testIsPreferenceDefault() {
    System.out.println("isPreferenceDefault");
    String key = "";
    boolean defaultValue = false;
    boolean expResult = false;
    boolean result = Preferences.isPreferenceDefault(key, defaultValue);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getPreferenceDefault method, of class Preferences.
   */
  public void testGetPreferenceDefault_String_String() {
    System.out.println("getPreferenceDefault");
    String key = "";
    String defaultValue = "";
    String expResult = "";
    String result = Preferences.getPreferenceDefault(key, defaultValue);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getPreferenceDefault method, of class Preferences.
   */
  public void testGetPreferenceDefault_String_int() {
    System.out.println("getPreferenceDefault");
    String key = "";
    int defaultValue = 0;
    int expResult = 0;
    int result = Preferences.getPreferenceDefault(key, defaultValue);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setPreference method, of class Preferences.
   */
  public void testSetPreference_String_String() {
    System.out.println("setPreference");
    String name = "";
    String value = "";
    Preferences.setPreference(name, value);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setPreference method, of class Preferences.
   */
  public void testSetPreference_String_Enum() {
    System.out.println("setPreference");
    String name = "";
    Enum value = null;
    Preferences.setPreference(name, value);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setPreference method, of class Preferences.
   */
  public void testSetPreference_String_boolean() {
    System.out.println("setPreference");
    String name = "";
    boolean boolvalue = false;
    Preferences.setPreference(name, boolvalue);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of setPreference method, of class Preferences.
   */
  public void testSetPreference_String_int() {
    System.out.println("setPreference");
    String name = "";
    int intvalue = 0;
    Preferences.setPreference(name, intvalue);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of save method, of class Preferences.
   */
  public void testSave() {
    System.out.println("save");
    Preferences.save();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of doLoad method, of class Preferences.
   */
  public void testDoLoad() {
    System.out.println("doLoad");
    Preferences.doLoad();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}

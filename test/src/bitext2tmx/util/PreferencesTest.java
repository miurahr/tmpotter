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

package bitext2tmx.util;

import bitext2tmx.segmentation.SRX;
import java.io.File;
import java.io.PrintWriter;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
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
public class PreferencesTest {
  
  public PreferencesTest() {
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
   * Test of getPreference method, of class Preferences.
   */
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
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
  @Test
  public void testSave() {
    System.out.println("save");
    Preferences.save();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of doLoad method, of class Preferences.
   */
  @Test
  public void testDoLoad() {
    System.out.println("doLoad");
    Preferences.doLoad();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getSRX method, of class Preferences.
   */
  @Test
  public void testGetSRX() {
    System.out.println("getSRX");
    SRX result = Preferences.getSRX();
    assertNotNull(result);
  }

  /**
   * Test of setSRX method, of class Preferences.
   */
  @Test
  public void testSetSRX() {
    System.out.println("setSRX");
    SRX newSrx = SRX.getDefault();
    Preferences.setSRX(newSrx);
  }
  
  
   /**
   * Test that if an error is encountered when loading the preferences file, the
   * original file is backed up.
   * <p>
   * Note that this test can spuriously fail if run in a situation where the
   * Preferences class has already been initialized, for instance when running
   * the entire suite of tests in Eclipse. It behaves correctly when run
   * individually, or with ant.
   */
  public void testPreferencesBackup() throws Exception {
    File tmpDir = FileUtil.createTempDir();
    try {
      assertTrue(tmpDir.isDirectory());

      Utilities.setConfigDir(tmpDir.getAbsolutePath());

      File prefs = new File(tmpDir, Preferences.FILE_PREFERENCES);

      // Write anything that is malformed XML, to force a parsing error.
      PrintWriter out = new PrintWriter(prefs, "UTF-8");
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      out.println("<omegat>");
      out.println("<preference version=\"1.0\">");
      out.close();
      assertFalse(out.checkError());

      // Load bad prefs file.
      Preferences.doLoad();

            // The actual backup file will have a timestamp in the filename,
      // so we have to loop through looking for it.
      File backup = null;
      for (File f : tmpDir.listFiles()) {
        String name = f.getName();
        if (name.startsWith("omegat.prefs") && name.endsWith(".bak")) {
          backup = f;
          break;
        }
      }

      assertNotNull(backup);
      assertTrue(backup.isFile());

      FileUtil.compareFile(prefs, backup);
    } finally {
      assertTrue(FileUtil.deleteTree(tmpDir));
    }
  }
}

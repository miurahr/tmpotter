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

package org.tmpotter.preferences;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import org.tmpotter.util.FileUtil;
import org.tmpotter.util.Utilities;

/**
 *
 * @author miurahr
 */
public class PreferencesTest {
  private static File prefs;

  @BeforeClass
  public static void setUpClass() {
    File tmpDir = FileUtil.createTempDir();
    try {
      assertTrue(tmpDir.isDirectory());
      Utilities.setConfigDir(tmpDir.getAbsolutePath());
      prefs = new File(tmpDir, Preferences.FILE_PREFERENCES);
      // Write test data.
      PrintWriter out = new PrintWriter(prefs, "UTF-8");
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      out.println("<tmpotter>");
      out.println("<preference version=\"1.0\">");
      out.println("   <source_lang>EN-US</source_lang>");
      out.println("   <target_lang>JA</target_lang>");
      out.println("   <success>true</success>");
      out.println("   <screen_size>640</screen_size>");
      out.println("   <fail>false</fail>");
      out.println("</preference>");
      out.println("</tmpotter>");
      out.close();
      Preferences pref = Preferences.getPreferences();
      pref.doLoad();
    } catch (Exception ex) {
      // FIXME
    }
    Utilities.setConfigDir(null);
  }
  
  @AfterClass
  public static void tearDownClass() {
    // cleanup
    prefs.delete();
  }
  
  /**
   * Test of getPreference method, of class Preferences.
   */
  @Test
  public void testGetPreference() {
    System.out.println("getPreference");
    String key = "source_lang";
    String expResult = "EN-US";
    Preferences pref = Preferences.getPreferences();
    String result = pref.getPreference(key);
    assertEquals(expResult, result);
  }

  /**
   * Test of existsPreference method, of class Preferences.
   */
  @Test
  public void testExistsPreference_False() {
    System.out.println("existsPreference");
    String key = "hoge";
    boolean expResult = false;
    Preferences pref = Preferences.getPreferences();
    boolean result = pref.existsPreference(key);
    assertEquals(expResult, result);
  }

  /**
   * Test of existsPreference method, of class Preferences.
   */
  @Test
  public void testExistsPreference_True() {
    System.out.println("existsPreference");
    String key = "source_lang";
    boolean expResult = true;
    Preferences pref = Preferences.getPreferences();
    boolean result = pref.existsPreference(key);
    assertEquals(expResult, result);
  }
  /**
   * Test of isPreference method, of class Preferences.
   */
  @Test
  public void testIsPreference() {
    System.out.println("isPreference");
    String key = "success";
    Preferences pref = Preferences.getPreferences();
    boolean result = pref.isPreference(key);
    assertTrue(result);
  }

  /**
   * Test of isPreferenceDefault method, of class Preferences.
   */
  @Test
  public void testIsPreferenceDefault() {
    System.out.println("isPreferenceDefault");
    String key = "fuga";
    boolean defaultValue = false;
    Preferences pref = Preferences.getPreferences();
    boolean result = pref.isPreferenceDefault(key, defaultValue);
    assertFalse(result);
  }

  /**
   * Test of getPreferenceDefault method, of class Preferences.
   */
  @Test
  public void testGetPreferenceDefault_String_String() {
    System.out.println("getPreferenceDefault");
    String key = "foo";
    String defaultValue = "boo";
    String expResult = "boo";
    Preferences pref = Preferences.getPreferences();
    String result = pref.getPreferenceDefault(key, defaultValue);
    assertEquals(expResult, result);
  }

  /**
   * Test of getPreferenceDefault method, of class Preferences.
   */
  @Test
  public void testGetPreferenceDefault_String_int() {
    System.out.println("getPreferenceDefault");
    String key = "screen_size";
    int defaultValue = 1024;
    int expResult = 640;
    Preferences pref = Preferences.getPreferences();
    int result = pref.getPreferenceDefault(key, defaultValue);
    assertEquals(expResult, result);
  }

  /**
   * Test of setPreference method, of class Preferences.
   */
  @Test
  public void testSetPreference_String_String() {
    System.out.println("setPreference");
    String name = "new";
    String value = "one";
    Preferences pref = Preferences.getPreferences();
    pref.setPreference(name, value);
    String result = pref.getPreference(name);
    assertEquals(value, result);
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
      out.println("<tmpotter>");
      out.println("<preference version=\"1.0\">");
      out.close();
      assertFalse(out.checkError());

      // Load bad prefs file.
      Preferences pref = Preferences.getPreferences();
      pref.doLoad();

            // The actual backup file will have a timestamp in the filename,
      // so we have to loop through looking for it.
      File backup = null;
      for (File f : tmpDir.listFiles()) {
        String name = f.getName();
        if (name.startsWith("tmpotter.prefs") && name.endsWith(".bak")) {
          backup = f;
          break;
        }
      }

      assertNotNull(backup);
      assertTrue(backup.isFile());

      assertTrue(FileUtils.contentEquals(prefs, backup));
    } finally {
      assertTrue(FileUtil.deleteTree(tmpDir));
    }
  }
}

/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  This file come from OmegaT project
 * 
 * Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, and Henry Pijffers
 *              2007 Zoltan Bartko
 *              2008-2009 Didier Briel
 *              2010 Wildrich Fourie, Antonio Vilei, Didier Briel
 *              2011 John Moran, Didier Briel
 *              2012 Martin Fleurke, Wildrich Fourie, Didier Briel, Thomas Cordonnier,
 *                   Aaron Madlon-Kay
 *              2013 Aaron Madlon-Kay, Zoltan Bartko
 *              2014 Piotr Kulik, Aaron Madlon-Kay
 *              2015 Aaron Madlon-Kay, Yu Tang, Didier Briel
 *
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
 **************************************************************************/

package bitext2tmx.util;

import bitext2tmx.util.xml.XMLBlock;
import bitext2tmx.util.xml.XMLStreamReader;
import bitext2tmx.util.xml.XMLUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class to load and save preferences.
 *
 * @author Hiroshi Miura
 */
public class Preferences {
  public static final String FILE_PREFERENCES = "bitext2tmx" + ".prefs";

  // preference names
  public static final String SOURCE_LOCALE = "source_lang";
  public static final String TARGET_LOCALE = "target_lang";
  public static final String PROXY_USER = "proxy_user";
  public static final String PROXY_PASS = "proxy_pass";
          
  //Singleton
  static {
    m_loaded = false;
    m_preferenceMap = new HashMap<>(64);
    m_nameList = new ArrayList<>(32);
    m_valList = new ArrayList<>(32);
    m_changed = false;
    doLoad();
  }

  /**
   * Returns the defaultValue of some preference.
   *
   * <p>If the key is not found, returns the empty string.
   * 
   * @param key
   *            key of the key to look up, usually OConsts.PREF_...
   * @return preference defaultValue as a string
   */
  public static String getPreference(String key) {
    if (key == null || key.equals("")) {
      return "";
    }
    if (!m_loaded) {
      doLoad();
    }
    Integer num = m_preferenceMap.get(key);
    String val = "";
    if (num != null) {
      // mapping exists - recover defaultValue
      val = m_valList.get(num);
    }
    return val;
  }

  /**
   * Returns true if the preference exist.
   *
   * <p>If the key is not found return false
   * 
   * @param key
   *            key of the key to look up, usually OConsts.PREF_...
   * @return true if preferences exists
   */
  public static boolean existsPreference(String key) {
    boolean exists = false;
    if (key == null) {
      exists = false;
    }
    if (!m_loaded) {
      doLoad();
    }
    Integer num = m_preferenceMap.get(key);
    if (num != null) {
      exists = true;
    }
    return exists;
  }

  /**
   * Returns the boolean defaultValue of some preference.
   *
   * <p>Returns true if the preference exists and is equal to "true", false
   * otherwise (no such preference, or it's equal to "false", etc).
   * 
   * @param key
   *            preference key, usually OConsts.PREF_...
   * @return preference defaultValue as a boolean
   */
  public static boolean isPreference(String key) {
    return "true".equals(getPreference(key));
  }

  /**
   * Returns the boolean value of some preference.
   * 
   * <p>If the key is not found, returns the default value provided and sets the
   * preference to the default value.
   * 
   * @param key
   *            name of the key to look up, usually OConsts.PREF_...
   * @param defaultValue
   *            default value for the key
   * @return preference value as an boolean
   */
  public static boolean isPreferenceDefault(String key, boolean defaultValue) {
    String val = getPreference(key);
    if (StringUtil.isEmpty(val)) {
      setPreference(key, defaultValue);
      return defaultValue;
    }
    return "true".equals(val);
  }

  /**
   * Returns the value of some preference out of OmegaT's preferences file, if
   * it exists.
   * 
   * <p>If the key is not found, returns the default value provided and sets the
   * preference to the default value.
   * 
   * @param key
   *            name of the key to look up, usually OConsts.PREF_...
   * @param defaultValue
   *            default value for the key
   * @return preference value as a string
   */
  public static String getPreferenceDefault(String key, String defaultValue) {
    String val = getPreference(key);
    if (val.equals("")) {
      val = defaultValue;
      setPreference(key, defaultValue);
    }
    return val;
  }

  /**
   * Get preference value, if not defined returns default.
   * 
   * @param key preference key
   * @param defaultValue defualt value
   * @return preference value
   */
  public static int getPreferenceDefault(String key, int defaultValue) {
    String val = getPreferenceDefault(key, Integer.toString(defaultValue));
    int res = defaultValue;
    try {
      res = Integer.parseInt(val);
    } catch (NumberFormatException nfe) {
      // FIXME
    }
    return res;
  }

  /**
   * Sets the value of some preference.
   * 
   * @param name
   *            preference key name, usually Preferences.PREF_...
   * @param value
   *            preference value as a string
   */
  public static void setPreference(String name, String value) {
    m_changed = true;
    if (!StringUtil.isEmpty(name) && value != null) {
      if (!m_loaded) {
        doLoad();
      }
      Integer num = m_preferenceMap.get(name);
      if (num == null) {
        // defaultValue doesn't exist - add it
        num = m_valList.size();
        m_preferenceMap.put(name, num);
        m_valList.add(value);
        m_nameList.add(name);
      } else {
        // mapping exists - reset defaultValue to new
        m_valList.set(num, value);
      }
    }
  }

  /**
   * Sets the value of some preference.
   * 
   * @param name
   *            preference key name, usually Preferences.PREF_...
   * @param value
   *            preference value as enum
   */
  public static void setPreference(String name, Enum<?> value) {
    m_changed = true;
    if (!StringUtil.isEmpty(name) && value != null) {
      if (!m_loaded) {
        doLoad();
      }
      Integer num = m_preferenceMap.get(name);
      if (num == null) {
        // defaultValue doesn't exist - add it
        num = m_valList.size();
        m_preferenceMap.put(name, num);
        m_valList.add(value.name());
        m_nameList.add(name);
      } else {
        // mapping exists - reset defaultValue to new
        m_valList.set(num, value.name());
      }
    }
  }

  /**
   * Sets the boolean value of some preference.
   * 
   * @param name
   *            preference key name, usually Preferences.PREF_...
   * @param boolvalue
   *            preference defaultValue as a boolean
   */
  public static void setPreference(String name, boolean boolvalue) {
    setPreference(name, String.valueOf(boolvalue));
  }

  /**
   * Sets the int value of some preference.
   * 
   * @param name
   *            preference key name, usually Preferences.PREF_...
   * @param intvalue
   *            preference value as an integer
   */
  public static void setPreference(String name, int intvalue) {
    setPreference(name, String.valueOf(intvalue));
  }

  /**
   * Save preference.
   * 
   */
  public static void save() {
    try {
      if (m_changed) {
        doSave();
      }
    } catch (IOException e) {
      // FIXME
    }
  }

  /**
   * Loads the preferences from disk, from a location determined by {@link #getPreferencesFile()}.
   * This method is package-private for unit testing purposes. Otherwise it is only meant to be
   * called from the static initializer in this class. DO NOT CALL IT UNLESS YOU KNOW WHAT YOU'RE
   * DOING.
   */
  static void doLoad() {
    // mark as loaded - if the load fails, there's no use
    // trying again later
    m_loaded = true;

    XMLStreamReader xml = new XMLStreamReader();
    xml.killEmptyBlocks();

    File prefsFile = getPreferencesFile();

    try {
      if (prefsFile == null) {
        // If no prefs file is present, look inside JAR for defaults. Useful for e.g. Web Start.
        InputStream is = Preferences.class.getResourceAsStream(FILE_PREFERENCES);
        if (is != null) {
          xml.setStream(new BufferedReader(new InputStreamReader(is)));
          readXmlPrefs(xml);
        }
      } else {
        xml.setStream(prefsFile);
        readXmlPrefs(xml);
      }
    } catch (TranslationException te) {
      // error loading preference file - keep whatever was
      // loaded then return gracefully to calling function
      // print an error to the console as an FYI
      makeBackup(prefsFile);
    } catch (IndexOutOfBoundsException e3) {
      // error loading preference file - keep whatever was
      // loaded then return gracefully to calling function
      // print an error to the console as an FYI
      makeBackup(prefsFile);
    } catch (UnsupportedEncodingException e3) {
      // unsupported encoding - forget about it
      makeBackup(prefsFile);
    } catch (IOException e4) {
      // can't read file - forget about it and move on
      makeBackup(prefsFile);
    } finally {
      try {
        xml.close();
      } catch (IOException ex) {
        // FIXME
      }
    }
  }

  /**
   * Gets the prefs file to use. Looks in these places in this order:
   * <ol>
   * <li>omegat.prefs in config dir
   * <li>omegat.prefs in install dir (defaults supplied with local install)
   * </ol>
   */
  private static File getPreferencesFile() {
    File prefsFile = new File(Utilities.getConfigDir(), FILE_PREFERENCES);
    if (prefsFile.exists()) {
      return prefsFile;
    }
    // If user prefs don't exist, fall back to defaults (possibly) bundled with OmegaT.
    prefsFile = new File(Utilities.installDir(), FILE_PREFERENCES);
    if (prefsFile.exists()) {
      return prefsFile;
    }
    return null;
  }

  private static void readXmlPrefs(XMLStreamReader xml) throws TranslationException {
    String pref;
    String val;
    
    m_preferenceMap.clear();
    // advance to omegat tag
    if (xml.advanceToTag("bitext2tmx") == null) {
      return;
    }
    // advance to project tag
    XMLBlock blk;
    if ((blk = xml.advanceToTag("preference")) == null) {
      return;
    }
    String ver = blk.getAttribute("version");
    if (ver != null && !ver.equals("1.0")) {
      // unsupported preference file version - abort read
      return;
    }
    List<XMLBlock> lst = xml.closeBlock(blk);
    if (lst == null) {
      return;
    }
    for (int i = 0; i < lst.size(); i++) {
      blk = lst.get(i);
      if (blk.isClose()) {
        continue;
      }
      if (!blk.isTag()) {
        continue;
      }
      pref = blk.getTagName();
      blk = lst.get(++i);
      if (blk.isClose()) {
        // allow empty string as a preference value
        val = "";
      } else {
        val = blk.getText();
      }
      if (pref != null && val != null) {
        // valid match - record these
        m_preferenceMap.put(pref, m_valList.size());
        m_nameList.add(pref);
        m_valList.add(val);
      }
    }
  }

  private static void makeBackup(File file) {
    if (file == null || !file.isFile()) {
      return;
    }
    String timestamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
    File bakFile = new File(file.getAbsolutePath() + "." + timestamp + ".bak");
    try {
      LFileCopy.copy(file, bakFile);
    } catch (IOException ex) {
      // FIXME
    }
  }

  private static void doSave() throws IOException {
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
            Utilities.getConfigDir() + FILE_PREFERENCES), "UTF-8"));
    try {
      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
      out.write("<omegat>\n");
      out.write("  <preference version=\"1.0\">\n");

      for (int i = 0; i < m_nameList.size(); i++) {
        String name = m_nameList.get(i);
        String val = XMLUtil.makeValidXML(m_valList.get(i));
        out.write("    <" + name + ">");
        out.write(val);
        out.write("</" + name + ">\n");
      }
      out.write("  </preference>\n");
      out.write("</omegat>\n");
    } finally {
      out.close();
    }
    m_changed = false;
  }

  private static boolean m_loaded;
  private static boolean m_changed;

  // use a hash map for fast lookup of data
  // use array lists for orderly recovery of it for saving to disk
  private static List<String> m_nameList;
  private static List<String> m_valList;
  private static Map<String, Integer> m_preferenceMap;
}

/* *************************************************************************
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
 * *************************************************************************/

package org.tmpotter.util;


import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 *  Localization: localization functionality.
 */
public final class Localization {
  //  Init the bundle
  private static Control control = new Utf8ResourceBundleControl();
  private static ResourceBundle _bundle = ResourceBundle
          .getBundle("org/tmpotter/Bundle", control);

  private static final ArrayList<String> languageList = new ArrayList<>();
  private static final ArrayList<String> langId = new ArrayList<>();
  
  static {
    languageList.add(getString("language-name-arab"));
    langId.add("AR");
    languageList.add(getString("language-name-bulgarian"));
    langId.add("BG");
    languageList.add(getString("language-name-catalan"));
    langId.add("CA");
    languageList.add(getString("language-name-chinese"));
    langId.add("ZH");
    languageList.add(getString("language-name-czech"));
    langId.add("CZ");
    languageList.add(getString("language-name-danish"));
    langId.add("DA");
    languageList.add(getString("language-name-dutch"));
    langId.add("NL");
    languageList.add(getString("language-name-english"));
    langId.add("EN");
    languageList.add(getString("language-name-finnish"));
    langId.add("FI");
    languageList.add(getString("language-name-french"));
    langId.add("FR");
    languageList.add(getString("language-name-german"));
    langId.add("DE");
    languageList.add(getString("language-name-hungarian"));
    langId.add("HU");
    languageList.add(getString("language-name-italian"));
    langId.add("IT");
    languageList.add(getString("language-name-japanese"));
    langId.add("JA");
    languageList.add(getString("language-name-korean"));
    langId.add("KO");
    languageList.add(getString("language-name-norwegian"));
    langId.add("NB");
    languageList.add(getString("language-name-polish"));
    langId.add("PL");
    languageList.add(getString("language-name-portuguese"));
    langId.add("PT");
    languageList.add(getString("language-name-russian"));
    langId.add("RU");
    languageList.add(getString("language-name-spanish"));
    langId.add("ES");
    languageList.add(getString("language-name-swedish"));
    langId.add("SV");
    languageList.add(getString("language-name-thai"));
    langId.add("TH");
  }
 
  /**
   * Return language name list in local character.
   * 
   * @return String array of language name in local language.
   */
  public static String[] getLanguageList() {
    String[] list = new String[languageList.size()];
    return languageList.toArray(list);
  }
  
  public static String getLanguageCode(int index) {
    return langId.get(index);
  }
  
  /**
   * Returns resource bundle.
   *
   * @return bundle object
   */
  public static ResourceBundle getResourceBundle() {
    return _bundle;
  }

  /**
   *  Private constructor.
   * 
   *  @noinspection UNUSED_SYMBOL
   */
  private Localization() {}

  /**
   *  l10n: return localized string for given key.
   *
   * @param strKey String to retrieve resource
   * @return String translated string
   */
  public static final String getString( final String strKey ) {
    return ( _bundle.getString( strKey ) );
  }

  /**
   * Exception on localization.
   * 
   */
  @SuppressWarnings("serial")
  public static final class LocalizationException extends Exception {
    public LocalizationException() {
      super();
    }

    /**
     *  LocalizationException with message parameter.
     * 
     * @param msg exception message
     */
    public LocalizationException( final String msg ) {
      super( msg );
    }
  }
}

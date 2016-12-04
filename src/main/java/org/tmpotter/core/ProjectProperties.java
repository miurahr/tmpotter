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

package org.tmpotter.core;

import org.tmpotter.util.Language;
import org.tmpotter.preferences.Preferences;
import org.tmpotter.util.StringUtil;

import java.io.File;


/**
 * Storage for project properties.
 *
 */
public final class ProjectProperties {
  private File filePathOriginal;
  private File filePathTranslation;
  private String targetEncoding;
  private String originalEncoding;
  private Language sourceLanguage;
  private Language targetLanguage;

  /**
   * Default constructor to initialize fields.
   */
  public ProjectProperties() {
    String sourceLocale = Preferences.getPreference(Preferences.SOURCE_LOCALE);
    if (!StringUtil.isEmpty(sourceLocale)) {
      setSourceLanguage(sourceLocale);
    } else {
      setSourceLanguage("EN-US");
    }

    String targetLocale = Preferences.getPreference(Preferences.TARGET_LOCALE);
    if (!StringUtil.isEmpty(targetLocale)) {
      setTargetLanguage(targetLocale);
    } else {
      setTargetLanguage("EN-GB");
    }
  }

  public void clear() {
    filePathOriginal = null;
    filePathTranslation = null;
  }

  public void setFilePathOriginal(File filePath) {
    filePathOriginal = filePath;
  }

  public File getFilePathOriginal() {
    return filePathOriginal;
  }

  public void setFilePathTranslation(File filePath) {
    filePathTranslation = filePath;
  }

  public File getFilePathTranslation() {
    return filePathTranslation;
  }

  public void setOriginalEncoding(String encoding) {
    originalEncoding = encoding;
  }

  public String getOriginalEncoding() {
    return originalEncoding;
  }

  public void setTranslationEncoding(String encoding) {
    targetEncoding = encoding;
  }

  public String getTranslationEncoding() {
    return targetEncoding;
  }

  public Language getSourceLanguage() {
    return sourceLanguage;
  }

  /**
   * Sets The Source Language (language of the source files) of the Project.
   */
  public void setSourceLanguage(Language sourceLanguage) {
    this.sourceLanguage = sourceLanguage;
  }

  /**
   * Sets The Source Language (language of the source files) of the Project.
   */
  public void setSourceLanguage(String sourceLanguage) {
    this.sourceLanguage = new Language(sourceLanguage);
  }

  /**
   * Returns The Target Language (language of the translated files) of the
   * Project.
   */
  public Language getTargetLanguage() {
    return targetLanguage;
  }

  /**
   * Sets The Target Language (language of the translated files) of the Project.
   */
  public void setTargetLanguage(Language targetLanguage) {
    this.targetLanguage = targetLanguage;
  }

  /**
   * Sets The Target Language (language of the translated files) of the Project.
   */
  public void setTargetLanguage(String targetLanguage) {
    this.targetLanguage = new Language(targetLanguage);
  }

  /**
   * Verify the correctness of a language or country code.
   *
   * @param code A string containing a language or country code
   * @return <code>true</code> or <code>false</code>
   */
  private static boolean verifyLangCode(String code) {
    // Make sure all values are characters
    for (int cp, i = 0; i < code.length(); i += Character.charCount(cp)) {
      cp = code.codePointAt(i);
      if (!Character.isLetter(cp)) {
        return false;
      }
    }
    return !new Language(code).getDisplayName().isEmpty();
  }

  /**
   * Verifies whether the language code is OK.
   * 
   * @param code check string as language code
   * @return true when valid 
   */
  public static boolean verifySingleLangCode(String code) {
    int cpc = code.codePointCount(0, code.length());
    if (cpc == 2 || cpc == 3) {
      return verifyLangCode(code);
    } else if (cpc == 5 || cpc == 6) {
      int shift = 0;
      if (cpc == 6) {
        shift = 1;
      }
      int sepOffset = code.offsetByCodePoints(0, 2 + shift);
      int sep = code.codePointAt(sepOffset);
      return verifyLangCode(code.substring(0, sepOffset))
              && (sep == '-' || sep == '_')
              && verifyLangCode(code.substring(code.offsetByCodePoints(sepOffset, 1),
                              code.offsetByCodePoints(sepOffset, 3)));
    }
    return false;
  }

}

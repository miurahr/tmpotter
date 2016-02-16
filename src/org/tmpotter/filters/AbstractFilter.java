/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *                2006 Martin Wunderlich
 *                2011 Alex Buloichik, Didier Briel,
 *                2012 Guido Leenders
 *                2015 Aaron Madlon-Kay
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

package org.tmpotter.filters;

import org.tmpotter.util.Localization;
import org.tmpotter.util.TranslationException;

import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import org.tmpotter.util.EncodingDetector;

/**
 * The base class for all filters.
 * <p>
 * Each filter should extend this class or one of its descendants.
 * <p>
 * The process how the filter works is the following:
 * <ol>
 * <li>Source text is extracted.
 * <li>Tags are converted into shortcuts and these shortcuts are temporarily stored
 * <li>Source text with shortened tags is sent to core
 * </ol>
 *
 * @author Maxym Mykhalchuk
 * @author Martin Wunderlich
 * @author Alex Buloichik
 * @author Didier Briel
 * @author Guido Leenders
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 */
public abstract class AbstractFilter implements IFilter {

  /**
   * This value represents to the user that the encoding is determined by the filter itself. "In
   * code" the <code>null</code> is used to represent automatic encoding selection.
   */
  public static String ENCODING_AUTO_HUMAN = Localization.getString("ENCODING_AUTO");

  /**
   * The original filename (with extension).
   */
  public static final String TFP_FILENAME = "${filename}";
  /**
   * The original filename without extension.
   */
  public static final String TFP_NAMEONLY = "${nameOnly}";
  /**
   * The original file extension.
   */
  public static final String TFP_EXTENSION = "${extension}";
  /**
   * "xx_YY", locale code
   */
  public static final String TFP_TARGET_LOCALE = "${targetLocale}";
  /**
   * "XX-YY", the TMX/XML language code
   */
  public static final String TFP_TARGET_LANGUAGE = "${targetLanguage}";
  /**
   * language "XX" only
   */
  public static final String TFP_TARGET_LANG_CODE = "${targetLanguageCode}";
  /**
   * country "YY" only
   */
  public static final String TFP_TARGET_COUNTRY_CODE = "${targetCountryCode}";
  /**
   * Old spelling of the variable for country "YY" only
   */
  public static final String TFP_TARGET_COUTRY_CODE = "${targetCoutryCode}";
  /**
   * System time at generation time in various patterns.
   */
  public static final String TFP_TIMESTAMP_LA = "${timestamp-a}";
  public static final String TFP_TIMESTAMP_LD = "${timestamp-d}";
  public static final String TFP_TIMESTAMP_LDD = "${timestamp-dd}";
  public static final String TFP_TIMESTAMP_LH = "${timestamp-h}";
  public static final String TFP_TIMESTAMP_LHH = "${timestamp-hh}";
  public static final String TFP_TIMESTAMP_LM = "${timestamp-m}";
  public static final String TFP_TIMESTAMP_LMM = "${timestamp-mm}";
  public static final String TFP_TIMESTAMP_LS = "${timestamp-s}";
  public static final String TFP_TIMESTAMP_LSS = "${timestamp-ss}";
  public static final String TFP_TIMESTAMP_LYYYY = "${timestamp-yyyy}";
  public static final String TFP_TIMESTAMP_UD = "${timestamp-D}";
  public static final String TFP_TIMESTAMP_UEEE = "${timestamp-EEE}";
  public static final String TFP_TIMESTAMP_UEEEE = "${timestamp-EEEE}";
  public static final String TFP_TIMESTAMP_UH = "${timestamp-H}";
  public static final String TFP_TIMESTAMP_UHH = "${timestamp-HH}";
  public static final String TFP_TIMESTAMP_UM = "${timestamp-M}";
  public static final String TFP_TIMESTAMP_UMM = "${timestamp-MM}";
  public static final String TFP_TIMESTAMP_UMMM = "${timestamp-MMM}";
  /**
   * Workstation properties.
   */
  public static final String TFP_SYSTEM_OS_NAME = "${system-os-name}";
  public static final String TFP_SYSTEM_OS_VERSION = "${system-os-version}";
  public static final String TFP_SYSTEM_OS_ARCH = "${system-os-arch}";
  public static final String TFP_SYSTEM_USER_NAME = "${system-user-name}";
  public static final String TFP_SYSTEM_HOST_NAME = "${system-host-name}";
  /**
   * File properties.
   */
  public static final String TFP_FILE_SOURCE_ENCODING = "${file-source-encoding}";
  public static final String TFP_FILE_TARGET_ENCODING = "${file-target-encoding}";
  public static final String TFP_FILE_FILTER_NAME = "${file-filter-name}";
  /**
   * Microsoft.
   */
  public static final String TFP_TARGET_LOCALE_LCID = "${targetLocaleLCID}";

  protected String inEncodingLastParsedFile;

  /**
   * All target filename patterns.
   */
  public static final String[] TARGET_FILENAME_PATTERNS = new String[]{
    TFP_FILENAME,
    TFP_NAMEONLY,
    TFP_EXTENSION,
    TFP_TARGET_LOCALE,
    TFP_TARGET_LOCALE_LCID,
    TFP_TARGET_LANGUAGE,
    TFP_TARGET_LANG_CODE,
    TFP_TARGET_COUNTRY_CODE,
    TFP_TIMESTAMP_LA,
    TFP_TIMESTAMP_LD,
    TFP_TIMESTAMP_LDD,
    TFP_TIMESTAMP_LH,
    TFP_TIMESTAMP_LHH,
    TFP_TIMESTAMP_LM,
    TFP_TIMESTAMP_LMM,
    TFP_TIMESTAMP_LS,
    TFP_TIMESTAMP_LSS,
    TFP_TIMESTAMP_LYYYY,
    TFP_TIMESTAMP_UD,
    TFP_TIMESTAMP_UEEE,
    TFP_TIMESTAMP_UEEEE,
    TFP_TIMESTAMP_UH,
    TFP_TIMESTAMP_UHH,
    TFP_TIMESTAMP_UM,
    TFP_TIMESTAMP_UMM,
    TFP_TIMESTAMP_UMMM,
    TFP_SYSTEM_OS_NAME,
    TFP_SYSTEM_OS_VERSION,
    TFP_SYSTEM_OS_ARCH,
    TFP_SYSTEM_USER_NAME,
    TFP_SYSTEM_HOST_NAME,
    TFP_FILE_SOURCE_ENCODING,
    TFP_FILE_TARGET_ENCODING,
    TFP_FILE_FILTER_NAME
  };

  /**
   * Callback for parse.
   */
  protected IParseCallback entryParseCallback;

  /**
   * Callback for align.
   */
  protected IAlignCallback entryAlignCallback;

  /**
   * Options for processing time.
   */
  protected Map<String, String> processOptions;

  /**
   * The default output filename pattern.
   * <p>
   * It is equal to "${filename}", which means that the name of the translated file should be the
   * same as the name of the input file.
   */
  public static String TARGET_DEFAULT = TFP_FILENAME;

  /**
   * Human-readable name of the File Format this filter supports.
   *
   * @return File format name
   */
  @Override
  public abstract String getFileFormatName();

  /**
   * Whether source encoding can be varied by the user.
   * <p>
   * True means that OmegaT should handle all the encoding mess.
   * <p>
   * Return false to state that your filter doesn't need encoding management provided by OmegaT,
   * because it either autodetects the encoding based on file contents (like HTML filter does) or
   * the encoding is fixed (like in OpenOffice files).
   *
   * @return whether source encoding can be changed by the user
   */
  @Override
  public abstract boolean isSourceEncodingVariable();

  /**
   * Whether target encoding can be varied by the user.
   * <p>
   * True means that OmegaT should handle all the encoding mess.
   * <p>
   * Return false to state that your filter doesn't need encoding management provided by OmegaT,
   * because the encoding is fixed (like in OpenOffice files), or for some other reason.
   *
   * @return whether target encoding can be changed by the user
   */
  @Override
  public abstract boolean isTargetEncodingVariable();

  /**
   * Returns whether the file is supported by the filter,
   * given the reader with file's contents.
   * There exists a version of this method that takes file and encoding
   * {@link #isFileSupported(File,String)}. You should override only one of the two.
   * <p>
   * By default returns true, because this method should be overriden only by filters that
   * differentiate input files not by extensions, but by file's content.
   * <p>
   * For example, DocBook files have .xml extension, as possibly many other XML files, so the filter
   * should check a DTD of the document.
   *
   * @param reader The reader of the source file
   * @return Does the filter support the file
   */
  protected boolean isFileSupported(BufferedReader reader) {
    return true;
  }

  /**
   * Returns whether the file is supported by the filter, given the file and possible file's
   * encoding ( <code>null</code> encoding means autodetect). Default implementation creates a
   * reader and calls {@link #isFileSupported(BufferedReader)}. You should override only one of the
   * two.
   * <p>
   * For example, DocBook files have .xml extension, as possibly many other XML files, so the filter
   * should check a DTD of the document.
   *
   * @param inFile Source file.
   * @param fc Filter context.
   * @return Does the filter support the file.
   */
  @Override
  public boolean isFileSupported(File inFile, Map<String, String> config, FilterContext fc) {
    BufferedReader reader = null;
    try {
      reader = createReader(inFile, fc.getInEncoding());
      return isFileSupported(reader);
    } catch (IOException e) {
      return false;
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception e) {
        // ignore it
      }
    }
  }

  /**
   * Define fuzzy mark prefix for source which will be stored in TM. It's 'fuzzy' by default, but
   * each filter can redefine it.
   *
   * @return fuzzy mark prefix
   */
  @Override
  public String getFuzzyMark() {
    return "fuzzy";
  }

  /**
   * Returns the hint displayed while the user edits the filter, and when she adds/edits the
   * instance of this filter. The hint may be any string, preferably in a non-geek language.
   *
   * @return The hint for editing the filter in a non-geek language.
   */
  @Override
  public String getHint() {
    return "";
  }

  /**
   * Creates a reader of an input file.
   *
   * @param inFile The source file.
   * @param inEncoding Encoding of the input file, if the filter supports it. Otherwise null.
   * @return The reader for the source file
   * @throws UnsupportedEncodingException Thrown if JVM doesn't support the specified inEncoding
   * @throws IOException If any I/O Error occurs upon reader creation
   */
  protected BufferedReader createReader(File inFile, String inEncoding)
      throws UnsupportedEncodingException, IOException {
    InputStreamReader isr;
    if (inEncoding == null) {
      isr = new InputStreamReader(new FileInputStream(inFile));
    } else {
      isr = new InputStreamReader(new FileInputStream(inFile), inEncoding);
    }
    return new BufferedReader(isr);
  }

  /**
   * Processes a single file given a reader. Generally this method should read strings from the
   * input reader. In order to let core know what strings are translatable, filter should call
   * {@link #processEntry(String)} method.
   * <p>
   * If you need more control over processed files, override
   * {@link #processFile(File, File, FilterContext)} instead.
   *
   * @param inFile Reader of the source file. It's the result of calling
   * {@link #createReader(File,String)}.
   * @throws TranslationException Should be thrown when processed file has any format defects.
   * @throws IOException In case of any I/O error.
   */
  protected abstract void processFile(BufferedReader inFile, FilterContext fc) throws IOException,
      TranslationException;

  /**
   * Processes a single file given an input. This method can be used to create a filter that works
   * with the source/target files directly, rather than using BufferedReader.
   * <p>
   * Generally this method should read strings from the input reader and write them to the output
   * reader. In order to let OmegaT know what strings are translatable and to get thair translation,
   * filter should call {@link #processEntry(String)} method.
   * <p>
   * If you override this method and do all the processing here, you should simply implement
   * {@link #processFile(BufferedReader)} with a stub.
   *
   * @param inFile The source file.
   * @param fc Filter context.
   * @returns List of processed files (each element of type {@link File}) or null if the filter can
   * not/did not process multiple files.
   *
   * @throws IOException In case of any I/O error.
   * @throws TranslationException Should be thrown when processed file has any format defects.
   */
  protected void processFile(File inFile, FilterContext fc) throws IOException,
      TranslationException {
    String encoding = getInputEncoding(fc, inFile);
    BufferedReader reader = createReader(inFile, encoding);
    inEncodingLastParsedFile = encoding == null ? Charset.defaultCharset().name() : encoding;

    try {
      processFile(reader, fc);
    } finally {
      reader.close();
    }
  }

  /**
   * Get the input encoding. If it's not set in the FilterContext (setting is "&lt;auto&gt;") and
   * the filter allows ({@link #isSourceEncodingVariable()}), try to detect it. The result may be
   * null.
   *
   * @param fc
   * @param inFile
   * @return
   * @throws IOException
   */
  protected String getInputEncoding(FilterContext fc, File inFile) throws IOException {
    String encoding = fc.getInEncoding();
    if (encoding == null && isSourceEncodingVariable()) {
      encoding = EncodingDetector.detectEncoding(inFile);
    }
    return encoding;
  }

  @Override
  public final void parseFile(File inFile, Map<String, String> config, FilterContext fc,
      IParseCallback callback) throws Exception {
    entryParseCallback = callback;
    entryAlignCallback = null;
    processOptions = config;

    try {
      processFile(inFile, fc);
    } finally {
      entryParseCallback = null;
      processOptions = null;
    }
  }

  @Override
  public final void alignFile(File inFile, File outFile, Map<String, String> config,
      FilterContext fc, IAlignCallback callback) throws Exception {
    entryParseCallback = null;
    entryAlignCallback = callback;
    processOptions = config;

    BufferedReader readerIn = createReader(inFile, fc.getInEncoding());
    BufferedReader readerOut = createReader(outFile, fc.getOutEncoding());

    try {
      alignFile(readerIn, readerOut, fc);
    } finally {
      readerIn.close();
      readerOut.close();
    }
  }

  /**
   * Align source file against translated file.
   *
   * @param sourceFile source file
   * @param translatedFile translated file
   * @param fc
   * @throws java.lang.Exception
   */
  protected void alignFile(BufferedReader sourceFile, BufferedReader translatedFile,
      FilterContext fc) throws Exception {
  }

  /**
   * Call this method to:
   * <ul>
   * <li>Instruct OmegaT what source strings are translatable.
   * <li>Get the translation of each source string.
   * </ul>
   *
   * @param entry Translatable source string
   * @return Translation of the source string. If there's no translation, returns the source string
   * itself.
   */
  protected final String processEntry(String entry) {
    return processEntry(entry, null);
  }

  /**
   * Call this method to:
   * <ul>
   * <li>Instruct OmegaT what source strings are translatable.
   * <li>Get the translation of each source string.
   * </ul>
   *
   * @param entry Translatable source string
   * @param comment comment on the source string in the source file (if available)
   * @return Translation of the source string. If there's no translation, returns the source string
   * itself.
   */
  protected final String processEntry(String entry, String comment) {
    entryParseCallback.addEntry(null, entry, null, false, comment, null, this);
    return entry;
  }

  /**
   * Set both callbacks. Used for child XML filters only.
   *
   * @param parseCallback
   */
  public void setCallback(IParseCallback parseCallback) {
    this.entryParseCallback = parseCallback;
  }

}

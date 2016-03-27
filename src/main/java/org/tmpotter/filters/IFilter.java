/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from OmegaT.
 *
 *  Copyright (C) 2009-2010 Alex Buloichik
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

package org.tmpotter.filters;

import java.io.File;
import java.util.Map;

/**
 * Interface for filters declaration.
 *
 * @author Alex Buloichik
 * @author Hiroshi Miura
 */
public interface IFilter {

  /**
   * Human-readable name of the File Format this filter supports.
   *
   * @return File format name
   */
  String getFileFormatName();

  /**
   * Returns the hint displayed while the user edits the filter, and when she adds/edits
   * the instance of this filter. The hint may be any string, preferably in a non-geek language.
   *
   * @return The hint for editing the filter in a non-geek language.
   */
  String getHint();

  /**
   * Whether source encoding can be varied by the user.
   * <p>
   * True means that OmegaT should handle all the encoding mess.
   * <p>
   * Return false to state that your filter doesn't need encoding management provided by core,
   * because it either autodetects the encoding based on file contents (like HTML filter does) or
   * the encoding is fixed (like in OpenOffice files).
   *
   * @return whether source encoding can be changed by the user
   */
  boolean isSourceEncodingVariable();

  /**
   * Whether target encoding can be varied by the user.
   * <p>
   * True means that OmegaT should handle all the encoding mess.
   * <p>
   * Return false to state that your filter doesn't need encoding management provided by core,
   * because the encoding is fixed (like in OpenOffice files), or for some other reason.
   *
   * @return whether target encoding can be changed by the user
   */
  boolean isTargetEncodingVariable();

  /**
   * Define fuzzy mark prefix for source which will be stored in TM. It's 'fuzzy' by default, but
   * each filter can redefine it.
   *
   * @return fuzzy mark prefix
   */
  String getFuzzyMark();

  /**
   * File format has both source and translated text?
   * 
   * @return  boolean true if format is combined such as TMX.
   */
  boolean isCombinedFileFormat();

  /**
   * Returns whether the file is supported by the filter, given the file and possible file's
   * encoding ( <code>null</code> encoding means autodetect).
   * <p>
   * For example, DocBook files have .xml extension, as possibly many other XML files, so the filter
   * should check a DTD of the document.
   *
   * @param inFile Source file.
   * @param config filter's configuration options
   * @param context processing context
   * @return Does the filter support the file.
   */
  boolean isFileSupported(File inFile, Map<String, String> config, FilterContext context);

  /**
   * Parse single file.
   *
   * @param inFile file to parse
   * @param config filter's configuration options
   * @param context processing context
   * @param callback callback for parsed data
   * @throws Exception when file I/O error
   */
  void parseFile(File inFile, Map<String, String> config, FilterContext context,
      IParseCallback callback) throws Exception;

  /**
   * Parse source and translated files.
   *
   * @param sourceFile source file
   * @param translateFile translated file
   * @param config filter's configuration options
   * @param context processing context
   * @param callback callback for store aligned data
   * @throws Exception when file I/O error
   */
  void parseFile(File sourceFile, File translateFile, Map<String, String> config,
      FilterContext context, IParseCallback callback) throws Exception;

}

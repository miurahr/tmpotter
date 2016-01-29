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

package org.tmpotter.filters;

import org.tmpotter.core.Document;
import org.tmpotter.util.Language;

import java.io.InputStreamReader;


/**
 * Import filter interface.
 * 
 * @author Hiroshi Miura
 */
public interface IImportFilter {
    
  /**
   * Human-readable name of the File Format this filter supports.
   * 
   * @return File format name
   */
  String getFileFormatName();

  /**
   * File format has both source and translated text?
   * 
   * @return  boolean true if format is combined such as TMX.
   */
  boolean isCombinedFileFormat();

  /**
   * Read combined document.
   * 
   * @throws Exception while read.
   */
  void read(InputStreamReader isr, Language originalLang, Language translationLang)
          throws Exception;

  /**
   * Read document.
   * 
   * @param isr InputStreamReader of target.
   * @param lang language of target stream.
   * @return Document result by read.
   * @throws Exception while read.
   */
  Document read(InputStreamReader isr, Language lang) throws Exception;

  /**
   * Get resulted original document.
   *
   * @return document result
   */
  Document getOriginalDocument();

  /**
   * Get resulted target document.
   *
   * @return document result
   */
  Document getTranslationDocument();

}

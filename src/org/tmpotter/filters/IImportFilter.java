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

import java.io.File;

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
   * Read source and translated files.
   * 
   * @param sourceFile
   *            source file
   * @param targetFile
   *            translated file
   * @throws Exception while file read.
   */
  void load(File sourceFile, File targetFile) throws Exception;

  /**
   * Read document.
   * 
   * @param sourceFile
   *            source file
   * @param targetFile
   *            translated file
   * @throws Exception while file read.
   */
  void load( File filePath, String sourceEncode ) throws Exception;

}

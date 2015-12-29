/**************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of tmpotter.
 *
 *  tmpotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  tmpotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with tmpotter.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package org.tmpotter.filters;

import java.io.File;

/**
 * Bi-Text loader.
 * 
 * <p> TBD
 * 
 * @author Hiroshi Miura
 */
public class TextHandler implements IImportFilter {
  @Override
  public boolean isCombinedFileFormat() {
    return false;
  }

  @Override
  public String getFileFormatName() {
    return "bi-text";
  }
  
  @Override
  public void load(File sourceFile, File targetFile) throws Exception {

  }
  
  @Override
  public void load(File sourceFile, String encoding ) throws Exception {
    
  }
}

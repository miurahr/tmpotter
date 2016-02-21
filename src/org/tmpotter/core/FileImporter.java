/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
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
 * ************************************************************************/

package org.tmpotter.core;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.tmpotter.filters.FilterContext;
import org.tmpotter.filters.IFilter;
import org.tmpotter.filters.PoFilter;

/**
 *
 * @author Hiroshi Miura
 */
public class FileImporter {
    
  private final String name;
  private FilterContext fc;
  
  /**
   * Import file.
   * 
   * @param prop property.
   * @throws Exception when read I/O error.
   */
  public FileImporter(ProjectProperties prop)
          throws Exception {
    this.name = prop.getFilePathOriginal().getName();
    fc=new FilterContext(prop);
  }

  public void poReader(File file) throws Exception {
    Map<String,String> config = new HashMap<>();
    IFilter reader = new PoFilter();
    reader.parseFile(file, config, fc, null);
  }

  public String getName() {
    return name;
  }
  
  /**
   * Return original as document.
   *
   * @param doc Document to be stored
   * @return document
   */
  public Document getOriginalDocument(Document doc) {
    if (doc == null) {
      doc = new Document();
    } else {
      doc.clean();
    }
    // fill in doc
    return doc;
  }
  
  /**
   * Return translation as Document.
   * 
   * @param doc Document to be stored.
   * @return document
   */
  public Document getTranslationDocument(Document doc) {
    if (doc == null) {
      doc = new Document();
    } else {
      doc.clean();
    }
    // fill in doc
    return doc;
  }
}

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

package org.tmpotter.filters;

import org.tmpotter.core.Document;
import org.tmpotter.core.ProjectProperties;
import org.tmpotter.util.Localization;
import org.tmpotter.util.PluginUtils;

import java.io.File;
import java.util.List;


/**
 * Filter manager to handle multiple filters.
 *
 * @author Hiroshi Miura
 */
public class FilterManager {
  private final List<IFilter> filterList;

  private Document documentOriginal;
  private Document documentTranslation;
  
  /**
   * Constructor.
   */
  public FilterManager() {
    filterList = PluginUtils.getFilters();
  }
  
  /**
   * return filter instance according to its name.
   * 
   * @param name to get
   * @return IFilter filter instance
   * @throws FilterNotFoundException when filter not found
   */
  public IFilter getFilterInstance(String name) throws FilterNotFoundException {
    for (IFilter filter: filterList) {
      String fileterName = filter.getClass().getSimpleName();
      if (fileterName.equals(name)) {
        return filter;
      }
    }
    throw new FilterNotFoundException("Filter " + name + " not found.");
  }
  
  /**
   * Load file from source file set to properties.
   * 
   * @param prop project properties
   * @param docOriginal document to return
   * @param docTranslation document to return
   * @param filterName filter to use
   */
  public void loadFile(ProjectProperties prop, Document docOriginal, Document docTranslation,
      String filterName) {
    IFilter filter = null;
    this.documentOriginal = docOriginal;
    this.documentTranslation = docTranslation;

    FilterContext fc = new FilterContext(prop.getSourceLanguage(), prop.getTargetLanguage(), true);
    File inFile = prop.getFilePathOriginal();
    File outFile = prop.getFilePathTranslation();
    if (prop.getOriginalEncoding().equals(Localization.getString("ENCODING.DEFAULT"))) {
      fc.setInEncoding(null);
    } else {
      fc.setInEncoding(prop.getOriginalEncoding());
    }
    try {
      filter = getFilterInstance(filterName);
    } catch (Exception ex) {
      System.out.println(ex);
    }
    if (filter != null) {
      try {
        filter.parseFile(inFile, outFile, null, fc, new ParseCb());
      } catch (Exception ex) {
        System.out.println(ex);
      }
    }
  }
  
  public class ParseCb implements IParseCallback {
    @Override
    public void addEntry(String id, String source, String translation, boolean isFuzzy,
        String comment, String path, IFilter filter) {
      if (source != null) {
        documentOriginal.add(source);
      }
      if (translation != null) {
        documentTranslation.add(translation);
      } 
    }
  }

}

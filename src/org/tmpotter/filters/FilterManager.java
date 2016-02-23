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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Filter manager to handle multiple filters.
 *
 * @author Hiroshi Miura
 */
public class FilterManager {
  private final List<IFilter> filterList;

  /**
   * Constructor.
   */
  public FilterManager() {
    filterList = new ArrayList<>();
    filterList.add(new BiTextFilter());
    filterList.add(new PoFilter());
  }
  
  /**
   * return filter instance according to its name.
   * 
   * @param name to get
   * @return IFilter filter instance
   * @throws org.tmpotter.filters.FilterNotFoundException when filter not found
   */
  public IFilter getFilterInstance(String name) throws FilterNotFoundException {
    for (IFilter filter: filterList) {
      String fName = filter.getClass().getSimpleName();
      if (fName.equals(name)) {
        return filter;
      }
    }
    throw new FilterNotFoundException("Filter "+name+" not found.");
  }
  
  public void loadFile(ProjectProperties prop, Document docOriginal, Document docTranslation,
      String filterName) {
    IFilter filter = null;

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
        if (filter.isCombinedFileFormat()) {
          filter.alignFile(inFile, outFile, null, fc, new AlignCb(docOriginal, docTranslation));
        } else {
          filter.parseFile(inFile, null, fc, new ParseCb(docOriginal, docTranslation));
          filter.alignFile(inFile, outFile, null, fc, new AlignCb(docOriginal, docTranslation));
        }
      } catch (Exception ex) {
        System.out.println(ex);
      }
    }
  }
  
  public class ParseCb implements IParseCallback {
    private final Document documentOriginal;
    private final Document documentTranslation;
    
    public ParseCb(Document orig, Document trans) {
      this.documentOriginal = orig;
      this.documentTranslation = trans;
    }

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
  
  /**
   * Align callback method.
   * Add sentences to tmData. Add also empty string.
   */
  public class AlignCb implements IAlignCallback {
    private final Document documentOriginal;
    private final Document documentTranslation;
    
    public AlignCb(Document orig, Document trans) {
      this.documentOriginal = orig;
      this.documentTranslation = trans;
    }

    @Override
    public void addTranslation(String id, String source, String translation, boolean isFuzzy,
        String comment, IFilter filter) {
      if (source != null) {
        documentOriginal.add(source);
      }
      if (translation != null) {
        documentTranslation.add(translation);
      } 
    }
  }

}

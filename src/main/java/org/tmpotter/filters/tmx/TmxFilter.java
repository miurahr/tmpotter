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
 * *************************************************************************/

package org.tmpotter.filters.tmx;

import java.io.File;
import java.util.Map;

import org.tmpotter.filters.FilterContext;
import org.tmpotter.filters.IFilter;
import org.tmpotter.filters.IParseCallback;
import org.tmpotter.util.TmxReader2;

/**
 *
 * @author miurahr
 */
public class TmxFilter implements IFilter {
  private TmxFilter self;

  public TmxFilter () {
	  self = this;
  }
	
  public String getFileFormatName() {
	  return "TMX";
  }

  public String getHint() {
	  return "TMX file.";
  }
  
  public boolean isSourceEncodingVariable() {
	  return false;
  }

  public boolean isTargetEncodingVariable() {
	  return false;
  }

  public String getFuzzyMark() {
	return "";
  }

  public boolean isCombinedFileFormat() {
	  return true;
  }

  public boolean isFileSupported(File inFile, Map<String, String> config, FilterContext context) {
	  if (inFile.getName().endsWith(".tmx")) {
		  return true;
	  }
	  return false;
  }

  public void parseFile(File inFile, Map<String, String> config, FilterContext fc,
      IParseCallback callback) throws Exception {
    TmxReader2.LoadCallback callbackLoader =  new TmxReader2.LoadCallback() {
      @Override
      public boolean onEntry(TmxReader2.ParsedTu tu,
              TmxReader2.ParsedTuv tuvSource,
              TmxReader2.ParsedTuv tuvTranslation,
              boolean isParagraphType) {
        if (tuvSource == null) {
          return false;
        }
        if (tuvTranslation != null) {
          addTuv(tu, tuvSource, tuvTranslation);
        } else {
          for (TmxReader2.ParsedTuv tuv : tu.tuvs) {
            if (tuv != tuvSource) {
              addTuv(tu, tuvSource, tuv);
            }
          }
        }
        return true;
      }
    
      private void addTuv(TmxReader2.ParsedTu tu,
              TmxReader2.ParsedTuv tuvSource,
              TmxReader2.ParsedTuv tuvTranslation) {

        /*
        changer = StringUtil.nvl(tuvTranslation.changeid,
                tuvTranslation.creationid, tu.changeid, tu.creationid);
        changeDate = StringUtil.nvlLong(tuvTranslation.changedate,
                tuvTranslation.creationdate, tu.changedate, tu.creationdate);
        creator = StringUtil.nvl(tuvTranslation.creationid, tu.creationid);
        creationDate = StringUtil.nvlLong(tuvTranslation.creationdate,
                tu.creationdate);
        */
        
        callback.addEntry(null, tuvSource.text, tuvTranslation.text, false,
	    tu.note, null, self);
      }
    };

    TmxReader2 reader = new TmxReader2();
    reader.readTmx(inFile, fc.getSourceLang(),
      fc.getTargetLang(), false, false, callbackLoader);
	  
  }

  public void parseFile(File sourceFile, File translateFile, Map<String, String> config,
      FilterContext fc, IParseCallback callback) throws Exception {
	  parseFile(sourceFile, config, fc, callback);
  }
}

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

import org.tmpotter.filters.FilterContext;
import org.tmpotter.filters.IFilter;
import org.tmpotter.filters.IParseCallback;
import org.tmpotter.util.TmxReader2;

import java.io.File;
import java.util.Map;
import org.tmpotter.core.Document;
import org.tmpotter.core.TmxWriter;


/**
 * TMX import filter.
 * @author Hiroshi Miura
 */
public class TmxFilter implements IFilter {
    private TmxFilter self;

    /**
     * Constructor.
     */
    public TmxFilter() {
        self = this;
    }

    /**
     * Return file format name 'TMX'.
     * @return format name  'TMX'.
     */
    public String getFileFormatName() {
        return "TMX";
    }

    /**
     * Return hint string.
     * @return hint string.
     */
    public String getHint() {
        return "TMX file.";
    }

    /**
     * TMX filter read UTF-8 file and not encoding variable.
     * @return false.
     */
    public boolean isSourceEncodingVariable() {
        return false;
    }

    /**
     * TMX filter read UTF-8 file and not encoding variable.
     * @return
     */
    public boolean isTargetEncodingVariable() {
        return false;
    }

    /**
     * Return fuzzy mark.
     * @return mark.
     */
    public String getFuzzyMark() {
        return "";
    }

    /**
     * TMX is combined format.
     * @return true
     */
    public boolean isCombinedFileFormat() {
        return true;
    }

    /**
     * Check the file is supported with this filter.
     * @param inFile  Source file.
     * @param config  filter's configuration options
     * @param context processing context
     * @return
     */
    public boolean isFileSupported(File inFile, Map<String, String> config, FilterContext context) {
        if (inFile.getName().endsWith(".tmx")) {
            return true;
        }
        return false;
    }

    /**
     * Parse file.
     * @param inFile   file to parse
     * @param config   filter's configuration options
     * @param fc filter context.
     * @param callback callback for parsed data
     * @throws Exception when error is happened.
     */
    public void parseFile(File inFile, Map<String, String> config, FilterContext fc,
                          IParseCallback callback) throws Exception {
        TmxReader2.LoadCallback callbackLoader = new TmxReader2.LoadCallback() {
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

    /**
     * Parse file.
     * @param sourceFile    source file
     * @param translateFile translated file
     * @param config        filter's configuration options
     * @param fc    filter context.
     * @param callback      callback for store aligned data
     * @throws Exception when error is happened.
     */
    @Override
    public void parseFile(File sourceFile, File translateFile, Map<String, String> config,
                          FilterContext fc, IParseCallback callback) throws Exception {
        parseFile(sourceFile, config, fc, callback);
    }
    
    @Override
    public boolean isSaveSupported() {
        return true;
    }
    
    @Override
    public void saveFile(File outFile, Document docOriginal, Document docTranslation, FilterContext fc) {
	    try {
		    TmxWriter.writeTmx(outFile, docOriginal,
			    fc.getSourceLang().toString(), docTranslation,
                            fc.getTargetLang().toString());
	    } catch (Exception ex) {
		    System.err.println(ex.getMessage());
	    }
    }
}

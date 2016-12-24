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

package org.tmpotter.core;

import org.apache.commons.io.FilenameUtils;
import org.tmpotter.util.Language;
import org.tmpotter.util.StringUtil;
import org.tmpotter.util.TmxReader2;

import java.util.ArrayList;
import java.util.List;


/**
 * Tmpotter Project file(tmpx) reader class.
 *
 * @author Hiroshi Miura
 */
public class TmpxReader {
    private TmxReader2.ParsedHeader header;
    private final List<TmxEntry> entries;

    /**
     * Read TMX file.
     *
     * @param prop property.
     * @throws Exception when read I/O error.
     */
    public TmpxReader(ProjectProperties prop)
        throws Exception {
        entries = new ArrayList<>();

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

                TmxEntry te = new TmxEntry();
                te.source = tuvSource.text;
                te.translation = tuvTranslation.text;
                te.changer = StringUtil.nvl(tuvTranslation.changeid,
                    tuvTranslation.creationid, tu.changeid, tu.creationid);
                te.changeDate = StringUtil.nvlLong(tuvTranslation.changedate,
                    tuvTranslation.creationdate, tu.changedate, tu.creationdate);
                te.creator = StringUtil.nvl(tuvTranslation.creationid, tu.creationid);
                te.creationDate = StringUtil.nvlLong(tuvTranslation.creationdate,
                    tu.creationdate);
                te.note = tu.note;

                entries.add(te);
            }
        };

        Language defaultSource = new Language("EN-US");
        Language defaultTarget = new Language("EN-GB");

        TmxReader2 reader = new TmxReader2();
        reader.readTmx(prop.getFilePathProject(), defaultSource, defaultTarget,
                false, false, callbackLoader);
        this.header = reader.getParsedHeader();
        // TODO: sanity check for security.
        // Don't respect a TMX srclang property but accept a custom property.
        // see TmxpWriter class.
        prop.setSourceLanguage(header.props.get("sourceLang"));
        prop.setTargetLanguage(header.props.get("targetLang"));
        // Project Name.
        prop.setProjectName(FilenameUtils.removeExtension(prop.getFilePathProject().getName()));
    }

    public void loadDocument(TmData tmData) {
        tmData.loadDocument(getOriginalDocument(tmData.getDocumentOriginal()),
                getTranslationDocument(tmData.getDocumentTranslation()));
    }

    /**
     * Return original as document.
     *
     * @param doc Document to be stored
     * @return document
     */
    private Document getOriginalDocument(Document doc) {
        if (doc == null) {
            doc = new Document();
        } else {
            doc.clean();
        }
        for (TmxEntry te : entries) {
            doc.add(te.source);
        }
        return doc;
    }

    /**
     * Return translation as Document.
     *
     * @param doc Document to be stored.
     * @return document
     */
    private Document getTranslationDocument(Document doc) {
        if (doc == null) {
            doc = new Document();
        } else {
            doc.clean();
        }
        for (TmxEntry te : entries) {
            doc.add(te.translation);
        }
        return doc;
    }

}

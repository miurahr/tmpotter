/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from OmegaT.
 *
 *  Copyright (C) 2008 Alex Buloichik
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


/**
 * Callback for parse files.
 *
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public interface IParseCallback {

    /**
     * Read entry from source file.
     *
     * @param id          ID in source file, or null if ID not supported by format
     * @param source      source entry text
     * @param translation exist translation text
     * @param isFuzzy     true if translation is fuzzy
     * @param comment     comment for entry, if format supports it
     * @param path        path of segment
     * @param filter      filter which produces entry
     */
    void addEntry(String id, String source, String translation, boolean isFuzzy, String comment,
                  String path, IFilter filter);

}

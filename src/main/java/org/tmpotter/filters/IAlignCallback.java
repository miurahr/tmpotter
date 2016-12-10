/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file come from OmegaT.
 *
 *  Copyright (C) 2009 Alex Buloichik
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
 * Callback for align files in filter.
 *
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public interface IAlignCallback {

    /**
     * New found aligned data.
     *
     * @param id          entry id
     * @param source      source text
     * @param translation translated text
     * @param isFuzzy     true if translation is fuzzy
     * @param path        path
     * @param filter      filter which produces entry
     */
    void addTranslation(String id, String source, String translation, boolean isFuzzy, String path,
                        IFilter filter);
}

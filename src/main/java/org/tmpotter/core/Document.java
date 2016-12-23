/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
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

import java.util.ArrayList;
import java.util.List;


/**
 * Document hold single text segments in Array.
 *
 * @author miurahr
 */
@SuppressWarnings("serial")
public class Document {

    private final ArrayList<String> documentSegments;

    public Document() {
        documentSegments = new ArrayList<>();
    }

    public Document(List<String> sentences) {
        documentSegments = new ArrayList<>(sentences);
    }

    public boolean isEmpty() {
        return documentSegments.isEmpty();
    }

    public int size() {
        return documentSegments.size();
    }

    public String remove(int index) {
        return documentSegments.remove(index);
    }

    /**
     * Add text segment at index.
     *
     * @param index   index to add
     * @param element text segment
     */
    public void add(int index, String element) {
        documentSegments.add(index, element);
    }

    /**
     * Add text segment at last of segments.
     *
     * @param element text to add
     */
    public void add(String element) {
        documentSegments.add(element);
    }

    public String get(int index) {
        return documentSegments.get(index);
    }

    public void set(int index, String content) {
        documentSegments.set(index, content);
    }

    public void duplicate(int index) {
        documentSegments.add(index + 1, documentSegments.get(index));
    }

    public void duplicateLast() {
        int index = documentSegments.size();
        documentSegments.add(index, documentSegments.get(index - 1));
    }

    /**
     * Get last segment.
     *
     * @return segument
     */
    public String getLast() {
        if (documentSegments.size() > 0) {
            return documentSegments.get(documentSegments.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Remove last segment.
     */
    public void removeLast() {
        if (documentSegments.size() > 0) {
            documentSegments.remove(documentSegments.size() - 1);
        }
    }

    /**
     * Padding by pad string.
     *
     * @param pad    String to pad
     * @param length of padding
     */
    public void padding(String pad, int length) {
        for (int i = 0; i < length; i++) {
            documentSegments.add(pad);
        }
    }

    /**
     * Clean up all strings.
     */
    public void clean() {
        int cont = documentSegments.size() - 1;
        while (!documentSegments.isEmpty()) {
            documentSegments.remove(cont--);
        }
    }

    /**
     * Perform alignments:join.
     * <p>
     * <p>joins the selected row with the following.
     *
     * @param index join index and index+1
     */
    public void join(final int index) {
        String cad;

        cad = documentSegments.get(index);
        cad = cad.concat(" ");
        cad = cad.concat(documentSegments.get(index + 1));
        documentSegments.set(index, cad.trim());
        int length = documentSegments.size() - 1;
        for (int i = index + 1; i < length; i++) {
            documentSegments.set(i, documentSegments.get(i + 1));
        }
        documentSegments.set(length, "");
    }

    /**
     * Perform alignments: delete.
     * <p>
     * <p>deletes the selected row
     *
     * @param index to be deleted
     */
    public void delete(final int index) {
        int length = documentSegments.size() - 1;
        for (int i = index; i < length; i++) {
            documentSegments.set(i, documentSegments.get(i + 1));
        }
        documentSegments.set(length, "");
    }

    /**
     * Perform alignments: split.
     * <p>
     * <p>splits the selected row at the given position creating two
     * rows.
     *
     * @param index    join index and index+1
     * @param position position at which the split is performed
     */
    public void split(final int index, final int position) {
        int length = documentSegments.size() - 1;
        assert length >= index;
        if (length == index || !documentSegments.get(length).equals("")) {
            documentSegments.add(length + 1, documentSegments.get(length));
            length++;
        }
        for (int i = length; i > (index + 1); i--) {
            documentSegments.set(i, documentSegments.get(i - 1));
        }
        String cad = documentSegments.get(index);
        if (position == 0) {
            documentSegments.set(index, "");
        } else {
            documentSegments.set(index, cad.substring(0, position).trim());
        }
        documentSegments.set(index + 1, cad.substring(position).trim());
    }

}

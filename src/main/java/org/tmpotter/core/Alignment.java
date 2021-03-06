/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015, 2020 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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


/**
 * Segment class.
 * @author Hiroshi Miura
 */
/*
  This class is not strictly correct

  There should be separate segments for the original and translation
  this is really an alignment, it should contain the two segments
  or it is a row in the table
*/
public class Alignment {
    private String num;
    private String original;
    private String translation;

    /**
     * Constructor.
     *
     * @param num         row number
     * @param original    original pharase
     * @param translation translation pharase
     */
    public Alignment(final String num, final String original,
                     final String translation) {
        this.num = num;
        this.original = original;
        this.translation = translation;
    }

    /**
     * Accessor to num.
     *
     * @return num
     */
    public String getNum() {
        return num;
    }

    /**
     * get original string.
     *
     * @return original string in segument.
     */
    public String getOriginal() {
        return original;
    }

    /**
     * get translation string.
     *
     * @return translation string in segument.
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * set number of segment.
     *
     * @param num string to set
     */
    public void setNum(final String num) {
        this.num = num;
    }

    /**
     * set original string.
     *
     * @param original to set
     */
    public void setOriginal(final String original) {
        this.original = original;
    }

    /**
     * set translation string.
     *
     * @param translation to set
     */
    public void setTranslation(final String translation) {
        this.translation = translation;
    }
}
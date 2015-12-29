/**
 * ************************************************************************
 * tmpotter - Bitext Aligner/TMX Editor.
 * 
 *  Copyright (C) 2005-2009  Raymond: Martin
 *            (C) 2015 Hiroshi Miura
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *
 *
 *  This file is part of tmpotter.
 *
 *  tmpotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  tmpotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with tmpotter.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package org.tmpotter.filters;

/**
 * Element of the translatable entry. Can be a tag or a piece of text.
 *
 * @author Maxym Mykhalchuk
 */
public interface Element {

  /**
   * Returns shortcut string representation of the element. E.g. for
   * &lt;strong&gt; tag should return &lt;s3&gt;.
   */
  String toShortcut();

  /**
   * Returns shorcuts like '\b_i0_\b' for statistics calculation.
   */
  String toSafeCalcShortcut();

  /**
   * Returns long XML-encoded representation of the element for storing in TMX.
   * E.g. for &lt;strong&gt; tag should return &lt;bpt
   * i="3"&gt;&amp;lt;strong&amp;gt;&lt;/bpt&gt;.
   */
  String toTmx();

  /**
   * Returns the element in its original form as it was in original document.
   * E.g. for &lt;strong&gt; tag should return &lt;bpt
   * i="3"&gt;&amp;lt;strong&amp;gt;&lt;/bpt&gt;.
   */
  String toOriginal();
}

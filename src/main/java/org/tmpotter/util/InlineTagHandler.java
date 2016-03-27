/* ************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor.
 *
 *  Copyright (C) 2005-2009  Raymond: Martin
 *            (C) 2015 Hiroshi Miura
 *
 *  Copyright (C) 2013 Alex Buloichik, Aaron Madlon-Kay
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

package org.tmpotter.util;

import org.tmpotter.filters.Tag;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class handles inline tags.
 * 
 * <p>i.e. helps to replace all tags into
 * shortcuts. It handles bpt,ept,it tags numeration.
 *
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Aaron Madlon-Kay
 */
public class InlineTagHandler {

  /**
   * map of 'i' attributes to tag numbers.
   */
  Map<String, Integer> pairTags = new TreeMap<>();
  Map<String, ArrayDeque<Integer>> pairedOtherTags = new TreeMap<>();
  Map<String, Integer> shortcutLetters = new TreeMap<>();
  String currentI;
  String currentPos;
  int tagIndex;
  int otherTagShortcutLetter;

  /**
   * Reset stored info for process new part of XML.
   */
  public void reset() {
    pairTags.clear();
    pairedOtherTags.clear();
    currentI = null;
    currentPos = null;
    tagIndex = 0;
  }

  /**
   * Handle "bpt" tag start for TMX. OmegaT internal tag number will be based
   * off the x attr (if provided).
   *
   * @param attrI TMX i attribute value
   * @param attrX TMX x attribute value (can be null)
   */
  public void startBpt(String attrI, String attrX) {
    if (attrI == null) {
      throw new RuntimeException("Wrong index in inline tag");
    }
    currentI = attrI;
    int index = tagIndex++;
    try {
            // If a value for the @x attr was provided, base the tag
      // number off of it for matching purposes.
      // Subtract 1 because OmegaT 0-indexes tags, while TMX
      // seems to start at 1 (though the spec only says it must be
      // unique for each <bpt> in the segment, so we clip to 0
      // to prevent negative tag numbers).
      index = Math.max(0, Integer.parseInt(attrX) - 1);
    } catch (Exception ex) {
      // Ignore
    }
    pairTags.put(currentI, index);
  }

  /**
   * Handle "bpt" tag start. Identifier will be first non-null attribute in
   * provided attributes. OmegaT internal tag number will be its index in the
   * list of tags in the segment (starting with 0).
   *
   * @param attributeValues attributes to identify pairs
   */
  public void startBpt(String... attributeValues) {
    currentI = nvl(attributeValues);
    pairTags.put(currentI, tagIndex++);
  }

  /**
   * Store shortcut letter for current 'i' value.
   *
   * @param letter letter to store
   */
  public void setTagShortcutLetter(int letter) {
    if (letter != 0) {
      shortcutLetters.put(currentI, letter);
    }
  }

  /**
   * Get stored shortcut letter for current 'i' value.
   *
   * @return letter
   */
  public int getTagShortcutLetter() {
    Integer currentINum = shortcutLetters.get(currentI);
    return currentINum != null ? currentINum : 0;
  }

  /**
   * Store shortcut letter for current other tag.
   *
   * @param letter letter to store
   */
  public void setOtherTagShortcutLetter(int letter) {
    otherTagShortcutLetter = letter;
  }

  /**
   * Get stored shortcut letter for current other tag.
   *
   * @return letter
   */
  public int getOtherTagShortcutLetter() {
    return otherTagShortcutLetter;
  }

  /**
   * Handle "ept" tag start.
   *
   * @param attributeValues attributes to identify pairs
   */
  public void startEpt(String... attributeValues) {
    currentI = nvl(attributeValues);
  }

  /**
   * Handle other tag start.
   */
  public void startOther() {
    currentI = null;
  }

  /**
   * Handle "bpt" tag end.
   *
   * @return shortcut index
   */
  public Integer endBpt() {
    return pairTags.get(currentI);
  }

  /**
   * Handle "ept" tag end.
   *
   * @return shortcut index
   */
  public Integer endEpt() {
    return pairTags.get(currentI);
  }

  /**
   * Handle other tag end.
   *
   * @return shortcut index
   */
  public int endOther() {
    int result = tagIndex;
    tagIndex++;
    return result;
  }

  /**
   * Handle paired tag end.
   *
   * @return shortcut index
   */
  public int paired(String tagName, Tag.Type tagType) {
    int result;
    switch (tagType) {
      case BEGIN:
        result = tagIndex;
        cacheTagIndex(tagName, result);
        tagIndex++;
        return result;
      case END:
        Integer index = getCachedTagIndex(tagName);
        if (index == null) {
          index = tagIndex;
          tagIndex++;
        }
        return index;
      case ALONE:
        result = tagIndex;
        tagIndex++;
        return result;
      default:
        throw new RuntimeException("Impossible tag type");
    }
  }

  private void cacheTagIndex(String tagName, int result) {
    ArrayDeque<Integer> cache = pairedOtherTags.get(tagName);
    if (cache == null) {
      cache = new ArrayDeque<Integer>();
      pairedOtherTags.put(tagName, cache);
    }
    cache.addFirst(result);
  }

  private Integer getCachedTagIndex(String tagName) {
    ArrayDeque<Integer> cache = pairedOtherTags.get(tagName);
    if (cache == null) {
      return null;
    }
    return cache.pollFirst();
  }

  /**
   * Remember current begin/end mark of "it" tag.
   */
  public void setCurrentPos(String currentPos) {
    this.currentPos = currentPos;
  }

  /**
   * Returns current begin/end mark of "it" tag.
   */
  public String getCurrentPos() {
    return currentPos;
  }

  private String nvl(String... attributeValues) {
    String result = StringUtil.nvl(attributeValues);
    if (result == null) {
      throw new RuntimeException("Wrong index in inline tag");
    }
    return result;
  }
}

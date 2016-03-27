/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
 *
 *  Part of this come from OmegaT.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
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

import org.tmpotter.util.AppConstants;

/**
 * A tag in a source text.
 *
 * @author Maxym Mykhalchuk
 */
public abstract class Tag implements Element {

  /**
   * Type tag.
   * 
   * BEGIN: begin of a paired tag
   * END: end of a paired tag
   * ALONE standalone tag
   */
  public enum Type { BEGIN, END, ALONE }

  private String tag;

  /**
   * Returns this tag.
   */
  public String getTag() {
    return tag;
  }

  private String shortcut;

  /**
   * Returns the short form of this tag, most often -- the first letter.
   */
  public String getShortcut() {
    if (shortcut != null) {
      return shortcut;
    } else {
      return String.valueOf(Character.toChars(getTag().codePointAt(0)));
    }
  }

  private Type type;

  /**
   * Returns type of this tag.
   */
  public Type getType() {
    return type;
  }

  /**
   * Sets type of this tag.
   */
  public void setType(Type type) {
    this.type = type;
  }

  private Attributes attributes;

  /**
   * Returns tag's attributes.
   */
  public Attributes getAttributes() {
    return attributes;
  }

  /**
   * Returns attribute value by name.
   */
  public String getAttribute(String name) {
    for (Attribute a : attributes.list) {
      if (name.equals(a.getName())) {
        return a.getValue();
      }
    }
    return null;
  }

  /**
   * Attributes of correspondent start tag.
   */
  private Attributes startAttributes;

  /**
   * Returns tag's attributes.
   */
  public Attributes getStartAttributes() {
    return startAttributes;
  }

  public void setStartAttributes(Attributes startAttributes) {
    this.startAttributes = startAttributes;
  }

  private int index;

  /**
   * Returns the index of this tag in the entry.
   */
  public int getIndex() {
    return index;
  }

  /**
   * Sets the index of the tag in the entry for proper shortcutization. E.g. if
   * called for &lt;strong&gt; tag with shortcut=3, {@link #toShortcut()} will
   * return &lt;s3&gt; and {@link #toTmx()} will return &lt;bpt
   * i="3"&gt;&amp;lt;strong&amp;gt;&lt;/bpt&gt;.
   */
  public void setIndex(int shortcut) {
    this.index = shortcut;
  }

  /**
   * Creates a new instance of Tag.
   */
  public Tag(String tag, String shortcut, Type type, Attributes attributes) {
    this.tag = tag;
    this.shortcut = shortcut;
    this.type = type;
    this.attributes = attributes;
  }

  /**
   * Returns long XML-encoded representation of the tag to store in TMX.
   * 
   * <p>This implementation encloses {@link #toPartialTmx()} in &lt;bpt&gt;, &lt;ept&gt;
   * or &lt;ph&gt;. Can be overriden in ancestors if needed, but most probably
   * you won't ever need to override this method, and override
   * {@link #toPartialTmx()} instead. E.g. for &lt;strong&gt; tag should return
   * &lt;bpt i="3"&gt;&amp;lt;strong&amp;gt;&lt;/bpt&gt;.
   */
  public String toTmx() {
    String tmxtag;
    switch (getType()) {
      case BEGIN:
        tmxtag = "bpt";
        break;
      case END:
        tmxtag = "ept";
        break;
      case ALONE:
        tmxtag = "ph";
        break;
      default:
        throw new RuntimeException("Shouldn't hapen!");
    }

    StringBuilder buf = new StringBuilder();

    buf.append("<");
    buf.append(tmxtag);
    buf.append(" i=\"");
    buf.append(getIndex());
    buf.append("\">");

    buf.append(toPartialTmx());

    buf.append("</");
    buf.append(tmxtag);
    buf.append(">");

    return buf.toString();
  }

  /**
   * Returns short XML-encoded representation of the tag to store in TMX,
   * without enclosing &lt;bpt&gt;, &lt;ept&gt; or &lt;ph&gt;.
   * 
   * <p>Can be overriden
   * in ancestors if needed. E.g. for &lt;strong&gt; tag should return
   * &amp;lt;strong&amp;gt;
   */
  protected String toPartialTmx() {
    StringBuilder buf = new StringBuilder();

    buf.append("&amp;lt;");
    if (Type.END == getType()) {
      buf.append("/");
    }
    buf.append(getTag());
    buf.append(getAttributes().toString());
    if (Type.ALONE == getType()) {
      buf.append("/");
    }
    buf.append("&amp;gt;");

    return buf.toString();
  }

  /**
   * Returns shortcut string representation of the element.
   * 
   * <p>E.g. for &lt;strong&gt; tag should return &lt;s3&gt;.
   */
  public String toShortcut() {
    StringBuilder buf = new StringBuilder();

    buf.append('<');
    if (Type.END == getType()) {
      buf.append('/');
    }
    buf.append(getShortcut());
    buf.append(getIndex());
    if (Type.ALONE == getType()) {
      buf.append('/');
    }
    buf.append('>');

    return buf.toString();
  }

  @Override
  public String toSafeCalcShortcut() {
    return AppConstants.TAG_REPLACEMENT_CHAR
            + getShortcut().replace('<', '_').replace('>', '_')
            + AppConstants.TAG_REPLACEMENT_CHAR;
  }

  /**
   * Returns the tag in its original form as it was in original document.
   * 
   * <p>Must be overriden by ancestors. E.g. for &lt;strong&gt; tag should return
   * &lt;bpt i="3"&gt;&amp;lt;strong&amp;gt;&lt;/bpt&gt;.
   */
  public abstract String toOriginal();
}

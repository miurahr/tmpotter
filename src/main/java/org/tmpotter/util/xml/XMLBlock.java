/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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

package org.tmpotter.util.xml;

import java.util.ArrayList;
import java.util.List;

/*
 * XML Block is either a tag (with optional attributes), or a string
 *
 * @author Keith Godfrey
 */
public class XMLBlock {
    public XMLBlock() {
        reset();
    }

    private void reset() {
        m_text = "";
        m_isClose = false;
        m_isStandalone = false;
        m_isComment = false;
        m_isTag = false;
        m_typeChar = 0;
        m_hasText = false;
        m_shortcut = "";

        if (m_attrList != null)
            m_attrList.clear();
    }

    // ////////////////////////////////////////////////
    // initialization methods

    public void setAttribute(String attribute, String value) {
        XMLAttribute attr = new XMLAttribute(attribute, value);
        setAttribute(attr);
    }

    private void setAttribute(XMLAttribute attr) {
        if (m_attrList == null) {
            m_attrList = new ArrayList<XMLAttribute>(8);
        }

        // assume that this attribute doesn't exist already
        m_attrList.add(attr);
    }

    public void setText(String text) {
        setTag(false);
        m_text = text;

        // block considered text if it has length=1 and includes non ws
        m_hasText = false;
        if (text.codePointCount(0, text.length()) == 1) {
            int cp = text.codePointAt(0);
            if (cp != 9 && cp != 10 && cp != 13 && cp != ' ') {
                m_hasText = true;
            }
        } else {
            m_hasText = true;
        }
    }

    public void setTypeChar(char c) {
        m_typeChar = c;
    }

    public void setShortcut(String shortcut) {
        m_shortcut = shortcut;
    }

    public String getShortcut() {
        if (m_shortcut != null && !m_shortcut.equals("")) {
            if (m_isClose) {
                return "/" + m_shortcut;
            } else if (m_isComment) {
                return "!comment";
            }
        }
        return m_shortcut;
    }

    /** Sets that this block is a closing tag. */
    public void setCloseFlag() {
        m_isClose = true;
        m_isStandalone = false;
    }

    /** Sets that this block is a stand-alone tag. */
    public void setStandaloneFlag() {
        m_isStandalone = true;
        m_isClose = false;
    }

    public void setComment() {
        m_isTag = true;
        setTypeChar('!');
        m_isComment = true;
        m_isClose = false;
        m_isStandalone = false;
    }

    public void setTagName(String name) {
        setTag(true);
        m_text = name;
    }

    private void setTag(boolean isTag) {
        m_isTag = isTag;
    }

    // ///////////////////////////////////////////////
    // data retrieval functions

    /** Whether this block is a chunk of text (not a tag). */
    public boolean hasText() {
        return m_hasText;
    }

    /** Whether this block is a tag. */
    public boolean isTag() {
        return m_isTag;
    }

    /** Whether this block is a standalone tag. */
    public boolean isStandalone() {
        return m_isStandalone;
    }

    /** Whether this is a closing tag. */
    public boolean isClose() {
        return m_isClose;
    }

    /** Whether this block is a comment. */
    public boolean isComment() {
        return m_isComment;
    }

    /**
     * Returns the block as text - either raw text if not a tag, or the tag and
     * attributes in text form if it is
     */
    public String getText() {
        if (m_typeChar == '?') {
            // write < + [/ +] tagname + attributes + [/ +] >
            StringBuilder tag = new StringBuilder("<?").append(m_text);
            if (m_attrList != null) {
                for (XMLAttribute attr : m_attrList) {
                    // add attribute/value pair
                    tag.append(" ").append(attr.name).append("=\"").append(attr.value).append("\"");
                }
            }
            tag.append("?>");
            return tag.toString();
        } else if (m_typeChar == '!') {
            StringBuilder tag = new StringBuilder("<!");
            if (m_text.equals("CDATA")) {
                tag.append("[").append(m_text).append("[");
            } else if (m_text.equals("]]")) {
                tag = new StringBuilder("]]>");
            } else if (m_isComment) {
                tag.append("-- ");
                tag.append(m_text);
                tag.append(" -->");
            } else {
                tag.append(m_text).append(" ");
                if (m_attrList != null) {
                    if (!m_attrList.isEmpty()) {
                        tag.append(m_attrList.get(0).name);
                    }
                }
                tag.append('>');
            }
            return tag.toString();
        } else if (isTag()) {
            // write < + [/ +] tagname + attributes + [/ +] >
            StringBuilder tag = new StringBuilder("<");
            if (m_isClose) {
                tag.append('/');
            }
            tag.append(m_text);
            if (m_attrList != null) {
                for (XMLAttribute attr : m_attrList) {
                    // add attribute/value pair
                    tag.append(" ").append(attr.name).append("=\"").append(attr.value).append("\"");
                }
            }
            if (m_isStandalone) {
                tag.append(" /");
            }
            tag.append('>');
            return tag.toString();
        } else {
            return m_text;
        }
    }

    public String getTagName() {
        if (isTag()) {
            return m_text;
        } else {
            return "";
        }
    }

    public int numAttributes() {
        if (m_attrList == null) {
            return 0;
        } else {
            return m_attrList.size();
        }
    }

    public XMLAttribute getAttribute(int n) {
        if (n < 0 || !isTag() || m_attrList == null || n > m_attrList.size()) {
            return null;
        } else {
            return m_attrList.get(n);
        }
    }

    public String getAttribute(String name) {
        if (!isTag() || m_attrList == null) {
            return null;
        }
        XMLAttribute attr = null;

        for (int i = 0; i < m_attrList.size(); i++) {
            attr = m_attrList.get(i);
            if (attr.name.equals(name)) {
                break;
            } else {
                attr = null;
            }
        }
        if (attr == null) {
            return null;
        } else {
            return attr.value;
        }
    }

    private String m_text; // tagname if tag; text if not
    private String m_shortcut; // user display for tag
    private boolean m_isClose;
    private boolean m_isComment;
    private boolean m_isStandalone;
    private boolean m_isTag;
    private boolean m_hasText;
    private char m_typeChar;
    private List<XMLAttribute> m_attrList;

    /** Returns a string representation for debugging purposes mainly. */
    public String toString() {
        return getText();
    }

    /** holds the shortcut number of this tag. */
    private int shortcutNumber;

    /** What's the shortcut number of this tag. */
    public int getShortcutNumber() {
        return this.shortcutNumber;
    }

    /** Sets the shortcut number of this tag. */
    public void setShortcutNumber(int shortcutNumber) {
        this.shortcutNumber = shortcutNumber;
    }
}

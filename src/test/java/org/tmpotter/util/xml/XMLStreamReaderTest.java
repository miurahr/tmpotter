/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *
 *  This file come from OmegaT project
 *
 *  Copyright (C) 2015 Aaron Madlon-Kay
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
 **************************************************************************/

package org.tmpotter.util.xml;

import java.io.File;
import java.util.List;

import org.tmpotter.util.TranslationException;

import static org.testng.Assert.*;
import org.testng.annotations.Test;


/**
 * Test the XML reader used to read OmegaT preference files.
 * 
 * @author Aaron Madlon-Kay
 */
public class XMLStreamReaderTest  {
    
    public void testLoadXML() throws Exception {
        XMLStreamReader xml = new XMLStreamReader();
        xml.killEmptyBlocks();
        
        xml.setStream(new File(this.getClass().getResource("/xml/test.xml").getFile()));
        
        XMLBlock blk;
        List<XMLBlock> lst;
        
        assertNotNull(xml.advanceToTag("root"));
        assertNotNull(blk = xml.advanceToTag("body"));
        assertEquals("foo", blk.getAttribute("attr"));
        assertNotNull(lst = xml.closeBlock(blk));
        
        assertEquals(25, lst.size());
        
        assertOpenTag(lst.get(0), "ascii");
        assertText(lst.get(1), "bar");
        assertCloseTag(lst.get(2), "ascii");
        
        assertOpenTag(lst.get(3), "bmp");
        assertText(lst.get(4), "\u2603"); // SNOWMAN
        assertCloseTag(lst.get(5), "bmp");
        
        assertOpenTag(lst.get(6), "dec");
        assertText(lst.get(7), "\u2603"); // SNOWMAN
        assertCloseTag(lst.get(8), "dec");
        
        assertOpenTag(lst.get(9), "hex");
        assertText(lst.get(10), "\u2603"); // SNOWMAN
        assertCloseTag(lst.get(11), "hex");
        
        assertOpenTag(lst.get(12), "astral");
        assertText(lst.get(13), "\uD83C\uDCBF"); // PLAYING CARD RED JOKER
        assertCloseTag(lst.get(14), "astral");
        
        assertOpenTag(lst.get(15), "a-dec");
        assertText(lst.get(16), "\uD83C\uDCBF"); // PLAYING CARD RED JOKER
        assertCloseTag(lst.get(17), "a-dec");
        
        assertOpenTag(lst.get(18), "a-hex");
        assertText(lst.get(19), "\uD83C\uDCBF"); // PLAYING CARD RED JOKER
        assertCloseTag(lst.get(20), "a-hex");
        
        assertOpenTag(lst.get(21), "named");
        assertText(lst.get(22), "&<>'\"");
        assertCloseTag(lst.get(23), "named");
        
        assertStandaloneTag(lst.get(24), "standalone");
        
        xml.close();
    }
    
    public void testBadEntity() throws Exception {
        XMLStreamReader xml = new XMLStreamReader();
        xml.killEmptyBlocks();
        
        XMLBlock blk;
        
        xml.setStream(new File(this.getClass().getResource("/xml/test-badDecimalEntity.xml").getFile()));

        assertNotNull(xml.advanceToTag("root"));
        assertNotNull(blk = xml.advanceToTag("body"));
        try {
            assertNotNull(xml.closeBlock(blk));
            fail("XML parsing should fail on bad decimal entity");
        } catch (TranslationException ex) {
        }
        
        xml.setStream(new File(this.getClass().getResource("/xml/test-badHexEntity.xml").getFile()));

        assertNotNull(xml.advanceToTag("root"));
        assertNotNull(blk = xml.advanceToTag("body"));
        try {
            assertNotNull(xml.closeBlock(blk));
            fail("XML parsing should fail on bad hex entity");
        } catch (TranslationException ex) {
        }
    }
    
    private void assertOpenTag(XMLBlock block, String name) {
        assertTrue(block.isTag());
        assertEquals(name, block.getTagName());
        assertFalse(block.isClose());
        assertFalse(block.isStandalone());
    }
    
    private void assertText(XMLBlock block, String text) {
        assertFalse(block.isTag());
        assertEquals(text, block.getText());
    }
    
    private void assertCloseTag(XMLBlock block, String text) {
        assertTrue(block.isTag());
        assertEquals(text, block.getTagName());
        assertTrue(block.isClose());
    }
    
    private void assertStandaloneTag(XMLBlock block, String name) {
        assertTrue(block.isTag());
        assertEquals(name, block.getTagName());
        assertTrue(block.isStandalone());
    }
}

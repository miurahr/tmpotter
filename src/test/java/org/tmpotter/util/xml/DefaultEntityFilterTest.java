/**************************************************************************
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
 **************************************************************************/

package org.tmpotter.util.xml;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.tmpotter.util.xml.DefaultEntityFilter;


/**
 *
 * @author miurahr
 */
public class DefaultEntityFilterTest {
  
  /**
   * Test of convertToEntity method, of class DefaultEntityFilter.
   */
  @Test
  public void testConvertToEntity() {
    System.out.println("convertToEntity");
    int cp = 32;
    DefaultEntityFilter instance = new DefaultEntityFilter();
    String expResult = " ";
    String result = instance.convertToEntity(cp);
    assertEquals(expResult, result);
  }

  /**
   * Test of convertToSymbol method, of class DefaultEntityFilter.
   */
  @Test
  public void testConvertToSymbol() {
    System.out.println("convertToSymbol");
    String escapeSequence = "Pi";
    DefaultEntityFilter instance = new DefaultEntityFilter();
    int expResult = 928;
    int result = instance.convertToSymbol(escapeSequence);
    assertEquals(expResult, result);
  }
}

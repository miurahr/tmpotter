/* *************************************************************************
 *
 *  tmpotter - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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
 * ************************************************************************/

package org.tmpotter.util;

import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author miurahr
 */
public class DefaultEntityFilterTest extends TestCase {
  
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

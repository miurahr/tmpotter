/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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

package org.tmpotter.util;

import junit.framework.TestCase;

/**
 *
 * @author miurahr
 */
public class RuntimePreferencesTest extends TestCase {

  /**
   * Test of setUserHome method, of class RuntimePreferences.
   */
  public void testSetUserHome() {
    System.out.println("setUserHome");
    String home = "hoge";
    RuntimePreferences.setUserHome(home);
    String result = RuntimePreferences.getUserHome();
    assertEquals(home, result);
  }

}

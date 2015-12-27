/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  This file come from OmegaT project
 *
 *  Copyright (C) 2008 Alex Buloichik
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package bitext2tmx.util;

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

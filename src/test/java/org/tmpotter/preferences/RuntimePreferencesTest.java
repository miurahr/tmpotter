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

package org.tmpotter.preferences;

import org.tmpotter.preferences.RuntimePreferences;
import java.io.File;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author miurahr
 */
public class RuntimePreferencesTest {

  /**
   * Test of setUserHome method, of class RuntimePreferences.
   */
  @Test
  public void testSetUserHome() {
    System.out.println("setUserHome");
    File home = new File("/tmp");
    RuntimePreferences.setUserHome(home);
    File result = RuntimePreferences.getUserHome();
    assertEquals(home, result);
  }

}

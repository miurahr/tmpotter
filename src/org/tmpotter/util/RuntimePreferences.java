/**************************************************************************
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
 **************************************************************************/

package org.tmpotter.util;

/**
 * Preference that remember only run time.
 * 
 * @author Hiroshi Miura
 */
public class RuntimePreferences {
  private static String userHome = "/";
  
  /**
   * Set user home directory.
   * 
   * @param home user home directory
   */
  public static void setUserHome(String home) {
    userHome = home;
  }
  
  /**
   * Get user home directory.
   * 
   * @return string user home
   */
  public static String getUserHome() {
    return userHome;
  }
}

/*
 * Copyright (C) 2015 miurahr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bitext2tmx.util;

/**
 *
 * @author Hiroshi Miura
 */
public class RuntimePreferences {
  private static boolean segmentByLineBreak;
  private static String userHome = null;
  
  public static boolean isSegmentByLineBreak()
  {
    return segmentByLineBreak;
  }
  
  public static void setSegmentByLineBreak(boolean segmentRule)
  {
    segmentByLineBreak = segmentRule;
  }
  
  public static void setUserHome(String home)
  {
    userHome = home;
  }
  
  public static String getUserHome()
  {
    return userHome;
  }
}

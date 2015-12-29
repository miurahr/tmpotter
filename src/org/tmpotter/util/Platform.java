/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from OmegaT project
 * 
 *  Copyright (C) 2007 - Zoltan Bartko
 *                2011 Alex Buloichik
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

package org.tmpotter.util;

/**
 * A class to retrieve some platform information.
 * 
 * @author: Zoltan Bartko bartkozoltan@bartkozoltan.com
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public final class Platform {
  public enum OsType {
    // os.arch=amd64, os.name=Linux, os.version=3.0.0-12-generic
    LINUX64,
    // os.arch=i386, os.name=Linux, os.version=3.0.0-12-generic
    LINUX32,
    // os.arch=x86_64, os.name=Mac OS X, os.version=10.6.8
    MAC64,
    // os.arch=i386, os.name=Mac OS X, os.version=10.6.8
    MAC32,
    // os.arch=amd64, os.name=Windows 7, os.version=6.1
    WIN64,
    // os.arch=x86, os.name=Windows 7, os.version=6.1
    WIN32,
    // other unix
    UNIX,
    // unknown system
    OTHER
  }

  private static OsType osType = OsType.OTHER;

  static {
    String osName = System.getProperty("os.name");
    if (osName != null && System.getProperty("os.arch") != null) {
      boolean is64 = is64Bit();
      if (osName.startsWith("Linux")) {
        osType = is64 ? OsType.LINUX64 : OsType.LINUX32;
      } else if (osName.contains("OS X")) {
        osType = is64 ? OsType.MAC64 : OsType.MAC32;
      } else if (osName.startsWith("Windows")) {
        osType = is64 ? OsType.WIN64 : OsType.WIN32;
      } else if ( osName.contains( "AIX" )
               || osName.equals( "Digital Unix" )
               || osName.equals( "FreeBSD" )
               || osName.equals( "HP UX" )
               || osName.equals( "Irix" )
               || osName.equals( "MPE/iX" )
               || osName.equals( "Solaris" )
               || osName.equals( "SunOS" ) ) {
        osType = OsType.UNIX;
      }
    }
  }

  private Platform() {
  }

  public static OsType getOsType() {
    return osType;
  }

  public static final boolean isWebStart() {
    return System.getProperty("javawebstart.version") != null;
  }

  /**
   * Returns true if running on Mac OS X.
   */
  public static boolean isMacOsx() {
    OsType os = getOsType();
    return os == OsType.MAC32 || os == OsType.MAC64;
  }

  /**
   * Returns true if running on Windows.
   */
  public static boolean isWindows() {
    OsType os = getOsType();
    return os == OsType.WIN32 || os == OsType.WIN64;
  }

  /**
   * Returns true if the JVM (NOT the OS) is 64-bit.
   */
  public static boolean is64Bit() {
    String osArch = System.getProperty("os.arch");
    if (osArch != null) {
      return osArch.contains("64");
    }
    return false;
  }

  /**  
   * Ask on Unix type OS.
   * 
   * @return true if platform is on Unix
   */
  public static boolean isUnix() {
    OsType os = getOsType();
    return os == OsType.UNIX;
  }

  /**
   * Returns true if running on Linux.
   * 
   */
  public static boolean isLinux() {
    OsType os = getOsType();
    return os == OsType.LINUX32 || os == OsType.LINUX64;
  }
}

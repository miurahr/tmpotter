/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015-2016 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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

import org.tmpotter.util.Platform.OsType;

import java.awt.GraphicsEnvironment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Utility functions.
 */
public class Utilities {
    /* Variables */
    private static String configDirectory = null;

    /* Constants */
    private static final String WINDOWS_CONFIG_DIR = "\\TMPotter\\";
    private static final String UNIX_CONFIG_DIR = "/.tmpotter/";
    private static final String OSX_CONFIG_DIR = "/Library/Preferences/TMPotter/";
    private static final String OTHER_CONFIG_DIR = "TMPOTTER/";

    private static final Logger LOG = Logger.getLogger(Utilities.class.getName());

    /**
     * Return names of all font families available.
     *
     * @return font name
     */
    public static final String[] getFontNames() {
        final GraphicsEnvironment graphics =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        return (graphics.getAvailableFontFamilyNames());
    }


    /**
     * Print UTF-8 text to stdout (useful for debugging)
     *
     * @param output The UTF-8 format string to be printed.
     */
    public static void printUtf8(String output) {
        try {
            BufferedWriter out = utf8WriterBuilder(System.out);
            out.write(output);

            out.flush();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Error", e);
        }
    }

    /**
     * Creates new BufferedWriter configured for UTF-8 output and connects it to
     * an OutputStream
     *
     * @param out OutputStream object
     * @return BufferWriter object with UTF-8 configuration
     * @throws java.lang.Exception Outputstream to connect to.
     */
    public static BufferedWriter utf8WriterBuilder(OutputStream out) throws Exception {
        return new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    }

    /**
     * Save UTF-8 format data to file.
     *
     * @param dir      directory to write to.
     * @param filename filename of file to write.
     * @param output   UTF-8 format text to write
     */
    public static void saveUtf8(String dir, String filename, String output) {
        try {
            // Page name can contain invalid characters, see [1878113]
            // Contributed by Anatoly Techtonik
            filename = filename.replaceAll("[\\\\/:\\*\\?\\\"\\|\\<\\>]", "_");
            File path = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(path);
            BufferedWriter out = utf8WriterBuilder(fos);
            out.write(output);
            out.close();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "utf8 save exception", e);
        }
    }

    /**
     * Returns the location of the configuration directory, depending on the
     * user's platform. Also creates the configuration directory, if necessary.
     * If any problems occur while the location of the configuration directory
     * is being determined, an empty string will be returned, resulting in the
     * current working directory being used.
     * <p>
     * <ul><li>Windows XP: &lt;Documents and Settings>\&lt;User name>\Application Data\TMPotter
     * <li>Windows Vista: User\&lt;User name>\AppData\Roaming
     * <li>Linux: ~/.tmpotter
     * <li>Solaris/SunOS: ~/.tmpotter
     * <li>FreeBSD: ~/.tmpotter
     * <li>Mac OS X: ~/Library/Preferences/TMPOtter
     * <li>Other: User home directory
     * </ul>
     *
     * @return The full path of the directory containing the OmegaT
     * configuration files, including trailing path separator.
     * @author Henry Pijffers (henry.pijffers@saxnot.com)
     */
    public static String getConfigDir() {
        // if the configuration directory has already been determined, return it
        if (configDirectory != null) {
            return configDirectory;
        }

        Platform.OsType os = Platform.getOsType(); // name of operating system
        String home; // user home directory

        // get os and user home properties
        try {
            // get the user's home directory
            home = System.getProperty("user.home");
        } catch (SecurityException e) {
            // access to the os/user home properties is restricted,
            // the location of the config dir cannot be determined,
            // set the config dir to the current working dir
            configDirectory = new File(".").getAbsolutePath() + File.separator;
            return configDirectory;
        }

        // if os or user home is null or empty, we cannot reliably determine
        // the config dir, so we use the current working dir (= empty string)
        if (os == null || StringUtil.isEmpty(home)) {
            // set the config dir to the current working dir
            configDirectory = new File(".").getAbsolutePath() + File.separator;
            return configDirectory;
        }

        // check for Windows versions
        if (os == OsType.WIN32 || os == OsType.WIN64) {
            String appData = null;

            // We do not use %APPDATA%
            // Trying first Vista/7, because "Application Data" exists also as virtual folder,
            // so we would not be able to differentiate with 2000/XP otherwise
            File appDataFile = new File(home, "AppData\\Roaming");
            if (appDataFile.exists()) {
                appData = appDataFile.getAbsolutePath();
            } else {
                // Trying to locate "Application Data" for 2000 and XP
                // C:\Documents and Settings\<User>\Application Data
                appDataFile = new File(home, "Application Data");
                if (appDataFile.exists()) {
                    appData = appDataFile.getAbsolutePath();
                }
            }

            if (!StringUtil.isEmpty(appData)) {
                // if a valid application data dir has been found,
                // append an OmegaT subdir to it
                configDirectory = appData + WINDOWS_CONFIG_DIR;
            } else {
                // otherwise set the config dir to the user's home directory,
                // usually
                // C:\Documents and Settings\<User>\TMPotter
                configDirectory = home + WINDOWS_CONFIG_DIR;
            }
        } else if (os == OsType.LINUX32 || os == OsType.LINUX64 || os == OsType.OTHER) {
            // Check for UNIX varieties
            // Solaris is generally detected as SunOS
            // set the config dir to the user's home dir + "/.tmpotter/", so it's
            // hidden
            configDirectory = home + UNIX_CONFIG_DIR;
        } else if (Platform.isMacOsx()) {
            // check for Mac OS X
            // set the config dir to the user's home dir +
            // "/Library/Preferences/TMPotter/"
            configDirectory = home + OSX_CONFIG_DIR;
        } else {
            // other OSes / default
            configDirectory = home + OTHER_CONFIG_DIR;
            // use the user's home directory by default
        }

        // create the path to the configuration dir, if necessary
        if (!configDirectory.isEmpty()) {
            try {
                // check if the dir exists
                File dir = new File(configDirectory);
                if (!dir.exists()) {
                    // create the dir
                    boolean created = dir.mkdirs();

                    // if the dir could not be created,
                    // set the config dir to the current working dir
                    if (!created) {
                        configDirectory = new File(".").getAbsolutePath() + File.separator;
                    }
                }
            } catch (SecurityException e) {
                // the system doesn't want us to write where we want to write
                // reset the config dir to the current working dir
                configDirectory = new File(".").getAbsolutePath() + File.separator;
            }
        }

        // we should have a correct, existing config dir now
        return configDirectory;
    }

    /**
     * Set the config dir path. This method is for unit testing purposes only. DO
     * NOT CALL IT UNLESS YOU KNOW WHAT YOU'RE DOING.
     */
    public static void setConfigDir(String path) {
        configDirectory = path;
    }

    // Caching install dir
    private static String strInstallDir = null;

    /**
     * Returns Application installation directory.
     *
     * @return install directory as String
     */
    public static String installDir() {
        if (strInstallDir == null) {
            String cp = System.getProperty("java.class.path");
            // See if we are running from a JAR
            String path = extractClasspathElement(cp, File.separator
                + AppConstants.APPLICATION_JAR);

            if (path == null) {
                // We're not running from a JAR; probably debug mode (in IDE, etc.)
                path = extractClasspathElement(cp, AppConstants.DEBUG_CLASSPATH);
            }

            // WTF?!! Falling back to current directory
            if (path == null) {
                path = ".";
            }

            // Cache the absolute path
            strInstallDir = new File(path).getAbsolutePath();
        }
        return strInstallDir;
    }

    /**
     * Extracts an element of a class path.
     *
     * @param fullcp           the classpath
     * @param posInsideElement position inside a class path string, that fits inside some
     *                         classpath element.
     */
    private static String classPathElement(String fullcp, int posInsideElement) {
        // semicolon before the path to the Jar
        int semicolon1 = fullcp.lastIndexOf(File.pathSeparatorChar, posInsideElement);
        // semicolon after the path to the Jar
        int semicolon2 = fullcp.indexOf(File.pathSeparatorChar, posInsideElement);
        if (semicolon1 < 0) {
            semicolon1 = -1;
        }
        if (semicolon2 < 0) {
            semicolon2 = fullcp.length();
        }
        return fullcp.substring(semicolon1 + 1, semicolon2);
    }

    /**
     * Extract classpath element that ends with <code>ending</code> from
     * the full classpath <code>cp</code>, if present. If not present, returns
     * null.
     */
    private static String extractClasspathElement(String cp, String ending) {
        try {
            int pos = cp.indexOf(ending);
            if (pos >= 0) {
                String path = classPathElement(cp, pos);
                return path.substring(0, path.indexOf(ending));
            }
        } catch (Exception e) {
            // should never happen, but just in case ;-)
        }
        return null;
    }

    public static int largerSize(int num1, int num2) {
        return (num1 < num2) ? num2 : num1;
    }

    private Utilities() {
        // static utility constructer
    }
}
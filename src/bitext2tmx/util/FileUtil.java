/**
 * ************************************************************************
 *
 * bitext2tmx - Bitext Aligner/TMX Editor.
 *
 * Copyright (C) 2005-2009 Raymond: Martin
 *           (C) 2015 Hiroshi Miura
 *
 * Copyright (C) 2008 Alex Buloichik
 *               2009 Didier Briel,  2012 Alex Buloichik, Didier Briel
 *               2014 Alex Buloichik, Aaron Madlon-Kay
 *
 *  This file is part of bitext2tmx.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Files processing utilities.
 *
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 */
public class FileUtil {

  public static String LINE_SEPARATOR = System.getProperty("line.separator");
  public static long RENAME_RETRY_TIMEOUT = 3000;
  private static final Logger LOG = Logger.getLogger(FileUtil.class.getName());

  /**
   * Renames file, with checking errors and 3 seconds retry against external
   * programs (like antivirus or TortoiseSVN) locking.
   */
  public static void rename(File from, File to) throws IOException {
    if (!from.exists()) {
      throw new IOException("Source file to rename (" + from + ") doesn't exist");
    }
    if (to.exists()) {
      throw new IOException("Target file to rename (" + to + ") already exists");
    }
    long bfor = System.currentTimeMillis();
    while (!from.renameTo(to)) {
      long end = System.currentTimeMillis();
      if (end - bfor > RENAME_RETRY_TIMEOUT) {
        throw new IOException("Error renaming " + from + " to " + to);
      }
    }
  }

  /**
   * Read file as UTF-8 text.
   */
  public static String readTextFile(File file) throws IOException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(
            new FileInputStream(file), AppConstants.ENCODINGS_UTF8));

    try {
      StringWriter out = new StringWriter();
      LFileCopy.copy(rd, out);
      return out.toString();
    } finally {
      rd.close();
    }
  }

  /**
   * Write text in file using UTF-8.
   */
  public static void writeTextFile(File file, String text) throws IOException {
    Writer wr = new OutputStreamWriter(new FileOutputStream(file), AppConstants.ENCODINGS_UTF8);
    try {
      wr.write(text);
    } finally {
      wr.close();
    }
  }

  /**
   * Find files in subdirectories.
   *
   * @param dir directory to start find
   * @param filter filter for found files
   * @return list of filtered found files
   */
  public static List<File> findFiles(final File dir, final FileFilter filter) {
    final List<File> result = new ArrayList<File>();
    Set<String> knownDirs = new HashSet<String>();
    findFiles(dir, filter, result, knownDirs);
    return result;
  }

  /**
   * Internal find method, which calls himself recursively.
   *
   * @param dir directory to start find
   * @param filter filter for found files
   * @param result list of filtered found files
   */
  private static void findFiles(final File dir, final FileFilter filter, final List<File> result,
          final Set<String> knownDirs) {
    String currDir;
    try {
      // check for recursive
      currDir = dir.getCanonicalPath();
      if (!knownDirs.add(currDir)) {
        return;
      }
    } catch (IOException ex) {
      LOG.log(Level.WARNING, "Exception", ex);
      return;
    }
    File[] list = dir.listFiles();
    if (list != null) {
      for (File f : list) {
        if (f.isDirectory()) {
          findFiles(f, filter, result, knownDirs);
        } else {
          if (filter.accept(f)) {
            result.add(f);
          }
        }
      }
    }
  }

  /**
   * Compute relative path of file.
   *
   * @param rootDir root directory
   * @param file file path
   * @return path string
   * @throws java.io.IOException when file is not found
   */
  public static String computeRelativePath(File rootDir, File file) throws IOException {
    String rootAbs = rootDir.getAbsolutePath().replace('\\', '/') + '/';
    String fileAbs = file.getAbsolutePath().replace('\\', '/');

    switch (Platform.getOsType()) {
      case WIN32:
      case WIN64:
        if (!fileAbs.toUpperCase().startsWith(rootAbs.toUpperCase())) {
          throw new IOException("File '" + file + "' is not under dir '" + rootDir + "'");
        }
        break;
      default:
        if (!fileAbs.startsWith(rootAbs)) {
          throw new IOException("File '" + file + "' is not under dir '" + rootDir + "'");
        }
        break;
    }
    return fileAbs.substring(rootAbs.length());
  }

  /**
   * Recursively delete a directory and all of its contents.
   *
   * @param dir The directory to delete
   */
  public static boolean deleteTree(File dir) {
    if (!dir.exists()) {
      return false;
    }
    if (dir.isDirectory()) {
      for (File file : dir.listFiles()) {
        if (file.isFile()) {
          file.delete();
        } else if (file.isDirectory()) {
          deleteTree(file);
        }
      }
    }
    return dir.delete();
  }

  public interface ICollisionCallback {

    public boolean isCanceled();

    public boolean shouldReplace(File file, int thisFile, int totalFiles);
  }

  /**
   * Copy a collection of files to a destination. Recursively copies contents of
   * directories while preserving relative paths. Provide an
   * {@link ICollisionCallback} to determine what to do with files with
   * conflicting names; they will be overwritten if the callback is null.
   *
   * @param destination Directory to copy to
   * @param toCopy Files to copy
   * @param onCollision Callback that determines what to do in case files with
   *        the same name already exist
   * @throws IOException when destination is exist or other reason
   */
  public static void copyFilesTo(File destination, File[] toCopy,
          ICollisionCallback onCollision) throws IOException {
    if (destination.exists() && !destination.isDirectory()) {
      throw new IOException("Copy-to destination exists and is not a directory.");
    }
    Map<File, File> collisions = copyFilesTo(destination, toCopy, (File) null);
    if (collisions.isEmpty()) {
      return;
    }
    List<File> toReplace = new ArrayList<>();
    List<File> toDelete = new ArrayList<>();
    int count = 0;
    for (Entry<File, File> e : collisions.entrySet()) {
      if (onCollision != null && onCollision.isCanceled()) {
        break;
      }
      if (onCollision == null || onCollision.shouldReplace(e.getValue(),
              count, collisions.size())) {
        toReplace.add(e.getKey());
        toDelete.add(e.getValue());
      }
      count++;
    }
    if (onCollision == null || !onCollision.isCanceled()) {
      for (File file : toDelete) {
        deleteTree(file);
      }
      copyFilesTo(destination, toReplace.toArray(new File[toReplace.size()]),
              (File) null);
    }
  }

  private static Map<File, File> copyFilesTo(File destination, File[] toCopy,
          File root) throws IOException {
    Map<File, File> collisions = new LinkedHashMap<>();
    for (File file : toCopy) {
      if (destination.getPath().startsWith(file.getPath())) {
        // Trying to copy something into its own subtree
        continue;
      }
      File thisRoot = root == null ? file.getParentFile() : root;
      String filePath = file.getPath();
      String relPath = filePath.substring(thisRoot.getPath().length(), filePath.length());
      File dest = new File(destination, relPath);
      if (file.equals(dest)) {
        // Trying to copy file to itself. Skip.
        continue;
      }
      if (dest.exists()) {
        collisions.put(file, dest);
        continue;
      }
      if (file.isDirectory()) {
        copyFilesTo(destination, file.listFiles(), thisRoot);
      } else {
        LFileCopy.copy(file, dest);
      }
    }
    return collisions;
  }

  /**
   * This method is taken from
   * <a href="https://code.google.com/p/guava-libraries/">Google Guava</a>,
   * which is licenced under the Apache License 2.0.
   *
   * <p>
   * Atomically creates a new directory somewhere beneath the system's temporary
   * directory (as defined by the {@code java.io.tmpdir} system property), and
   * returns its name.
   *
   * <p>
   * Use this method instead of {@link File#createTempFile(String, String)} when
   * you wish to create a directory, not a regular file. A common pitfall is to
   * call {@code createTempFile}, delete the file and create a directory in its
   * place, but this leads a race condition which can be exploited to create
   * security vulnerabilities, especially when executable files are to be
   * written into the directory.
   *
   * <p>
   * This method assumes that the temporary volume is writable, has free inodes
   * and free blocks, and that it will not be called thousands of times per
   * second.
   *
   * @return the newly-created directory
   * @throws IllegalStateException if the directory could not be created
   */
  public static File createTempDir() {
    File baseDir = new File(System.getProperty("java.io.tmpdir"));
    String baseName = System.currentTimeMillis() + "-";

    for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
      File tempDir = new File(baseDir, baseName + counter);
      if (tempDir.mkdir()) {
        return tempDir;
      }
    }
    throw new IllegalStateException("Failed to create directory within "
            + TEMP_DIR_ATTEMPTS + " attempts (tried "
            + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
  }

  private static int TEMP_DIR_ATTEMPTS = 10000;
}

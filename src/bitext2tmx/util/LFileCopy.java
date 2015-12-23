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
 *  Copyright (C) 2007 - Zoltan Bartko
 *              2011 Alex Buloichik
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

import static bitext2tmx.util.Localization.getString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


/**
 * Utility class for copying untranslatable files.
 * 
 * @author Keith Godfrey
 * @author Kim Bruning
 * @author Maxym Mykhalchuk
 */
public class LFileCopy {
  private static int BUFSIZE = 1024;

  /** Copies one file. Creates directories on the path to dest if necessary. */
  public static void copy(File src, File dest) throws IOException {
    if (!src.exists()) {
      throw new IOException(StringUtil
                .format(getString("LFC_ERROR_FILE_DOESNT_EXIST"),
                src.getAbsolutePath()));
    }
    FileInputStream fis = new FileInputStream(src);
    dest.getParentFile().mkdirs();
    FileOutputStream fos = new FileOutputStream(dest);
    byte[] ba = new byte[BUFSIZE];
    int readBytes;
    while ((readBytes = fis.read(ba)) > 0) {
      fos.write(ba, 0, readBytes);
    }
    fis.close();
    fos.close();
  }

  /** 
   * Stores a file from input stream.
   * 
   * <p>Input stream is not closed.
   * 
   * @param src input
   * @param dest output
   * @throws java.io.IOException if stream has error.
   */
  public static void copy(InputStream src, File dest) throws IOException {
    dest.getParentFile().mkdirs();
    FileOutputStream fos = new FileOutputStream(dest);
    byte[] ba = new byte[BUFSIZE];
    int readBytes;
    while ((readBytes = src.read(ba)) > 0) {
      fos.write(ba, 0, readBytes);
    }
    fos.close();
  }

  /**
   * Transfers all the input stream to the output stream.
   * 
   * <p>Input and output
   * streams are not closed.
   * @param src input
   * @param dest output
   * @throws java.io.IOException if input, output has error
   */
  public static void copy(InputStream src, OutputStream dest) throws IOException {
    byte[] ba = new byte[BUFSIZE];
    int readBytes;
    while ((readBytes = src.read(ba)) > 0) {
      dest.write(ba, 0, readBytes);
    }
  }

  /**
   * Transfers all data from reader to writer.
   * 
   * <p>Reader and writer are not
   * closed.
   */
  public static void copy(Reader src, Writer dest) throws IOException {
    char[] ba = new char[BUFSIZE];
    int readChars;
    while ((readChars = src.read(ba)) > 0) {
      dest.write(ba, 0, readChars);
    }
  }

  /**
   * Loads contents of a file into output stream. Output stream is not closed.
   */
  public static void copy(File src, OutputStream dest) throws IOException {
    if (!src.exists()) {
      throw new IOException(StringUtil
              .format(getString("LFC_ERROR_FILE_DOESNT_EXIST"),
              src.getAbsolutePath()));
    }
    FileInputStream fis = new FileInputStream(src);
    byte[] ba = new byte[BUFSIZE];
    int readBytes;
    while ((readBytes = fis.read(ba)) > 0) {
      dest.write(ba, 0, readBytes);
    }
    fis.close();
  }

}

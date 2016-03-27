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

package org.tmpotter.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author miurahr
 */
public class FileUtilTest extends TestCase {
  private static File tmpDir;
  private static File target;
  
  @Before
  @Override
  public void setUp() throws FileNotFoundException, UnsupportedEncodingException {
    tmpDir = FileUtil.createTempDir();
    try {
      assertTrue(tmpDir.isDirectory());
      target = new File(tmpDir, "FileUtilTest");
      PrintWriter out = new PrintWriter(target, "UTF-8");
      out.println("hoge");
      out.println("fuga");
      out.close();
    } catch (FileNotFoundException | UnsupportedEncodingException ex) {
      throw ex;
    }
  }
  
  @After
  @Override
  public void tearDown() {
    target.delete();
  }

  /**
   * Test of rename method, of class FileUtil.
   */
  @Test
  public void testRename() throws Exception {
    System.out.println("rename");
    File from = target;
    File to = new File(tmpDir, "hoge");
    FileUtil.rename(from, to);
  }

  /**
   * Test of readTextFile method, of class FileUtil.
   */
  @Test
  public void testReadTextFile() throws Exception {
    System.out.println("readTextFile");
    File file = target;
    String expResult = "hoge\nfuga\n";
    String result = FileUtil.readTextFile(file);
    assertEquals(expResult, result);
  }

  /**
   * Test of writeTextFile method, of class FileUtil.
   */
  @Test
  public void testWriteTextFile() throws Exception {
    System.out.println("writeTextFile");
    File file = target;
    String text = "foo";
    //File expected = new File("FIXME");
    FileUtil.writeTextFile(file, text);
    //FileUtil.compareFile(target, expected);
  }

}

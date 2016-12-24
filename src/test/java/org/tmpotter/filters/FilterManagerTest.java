/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
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
 * ************************************************************************/

package org.tmpotter.filters;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.File;

import org.tmpotter.core.Document;
import org.tmpotter.core.ProjectProperties;


/**
 *
 * @author miurahr
 */
public class FilterManagerTest {
  
  /**
   * Test of getFilterInstance method, of class FilterManager.
   */
  @Test
  public void testGetFilterInstance() {
    IFilter result = null;
    System.out.println("getFilterInstance");
    FilterManager manager = new FilterManager();
    try {
      result = manager.getFilterInstance("BiTextFilter");
    } catch (Exception ex) {
      fail("Filter not found.");
    }
    if (result != null) {
      assertTrue(result.getFileFormatName().equals("bi-text"));
    }
  }

  /**
   * Test of loadFile method, of class FilterManager.
   */
  @Test
  public void testLoadFile() {
    System.out.println("loadFile");
    ProjectProperties prop = new ProjectProperties();
    prop.setEncoding("UTF-8");
    prop.setTranslationEncoding("UTF-8");
    prop.setSourceLanguage("en");
    prop.setTargetLanguage("ja");
    prop.setFilePathOriginal(new File(this.getClass().getResource("/text/src.txt").getFile()));
    prop.setFilePathTranslation(new File(this.getClass().getResource("/text/trans.txt").getFile()));
    Document docOriginal = new Document();
    Document docTranslation = new Document();
    String filterName = "BiTextFilter";
    FilterManager instance = new FilterManager();
    instance.loadFile(prop, docOriginal, docTranslation, filterName);
    assertEquals(2, docOriginal.size());
    assertEquals(2, docTranslation.size());
  }
}

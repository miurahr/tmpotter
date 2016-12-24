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
import org.tmpotter.ui.wizard.ImportPreference;


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
    ImportPreference pref = new ImportPreference();
    prop.setSourceLanguage("en");
    prop.setTargetLanguage("ja");
    pref.setOriginalFilePath(new File(this.getClass().getResource("/text/src.txt").getFile()));
    pref.setTranslationFilePath(new File(this.getClass().getResource("/text/trans.txt").getFile()));
    pref.setEncoding("UTF-8");
    Document docOriginal = new Document();
    Document docTranslation = new Document();
    String filterName = "BiTextFilter";
    FilterManager instance = new FilterManager();
    instance.loadFile(pref, prop, docOriginal, docTranslation, filterName);
    assertEquals(docOriginal.size(), 2);
    assertEquals(docTranslation.size(), 2);
  }
}

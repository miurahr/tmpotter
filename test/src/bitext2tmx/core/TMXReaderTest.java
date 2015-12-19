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
package bitext2tmx.core;

import java.io.File;
import junit.framework.TestCase;

/**
 *
 * @author miurahr
 */
public class TMXReaderTest extends TestCase {
  
  public TMXReaderTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of readTMX method, of class TMXReader.
   */
  public void testReadTMX() {
    System.out.println("readTMX");
    File fPathOriginal = null;
    String encodeing = "";
    String langOriginal = "";
    String langTranslation = "";
    Document _alstOriginal = null;
    Document _alstTranslation = null;
    TMXReader.readTMX(fPathOriginal, encodeing, _alstOriginal, _alstTranslation);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}

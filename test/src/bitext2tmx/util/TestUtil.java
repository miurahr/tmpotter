/* *************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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
 * ************************************************************************/

package bitext2tmx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author miurahr
 */
public class TestUtil {
  public static boolean compareFile(File target, File expected)
          throws Exception {
    String line;
    FileInputStream fin;
    BufferedReader in;

    fin = new FileInputStream(target);
    in = new BufferedReader(new InputStreamReader(fin));
    StringBuilder tsb = new StringBuilder();
    while ((line = in.readLine()) != null ) {
       tsb.append(line);
    }
    FileInputStream expectedIn = new FileInputStream(expected);
    BufferedReader expectedInput = new BufferedReader(new InputStreamReader(expectedIn));
    StringBuilder esb = new StringBuilder();
    while ((line = expectedInput.readLine()) != null ) {
       esb.append(line);
    }
    return esb.toString().equals(tsb.toString());
  }
}

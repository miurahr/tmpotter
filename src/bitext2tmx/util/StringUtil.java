/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009 Raymond: Martin et al
#                2015 Hiroshi Miura
#
#  Includes code: Copyright (C) 2002-2006 Keith Godfrey et al.
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#######################################################################
#
# 
# This file is stolen from OmegaT project.
# and distributed under the GPLv3, or later.
#
#######################################################################
*/

/** Original copyright notices **/
/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2007 Kim Bruning
               2010 Alex Buloichik, Didier Briel, Rashid Umarov
               2011 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package bitext2tmx.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * String Utilities
 * 
 * @author Hiroshi Miura
 */
public class StringUtil {
    /**
     * ~inverse of String.split() refactor note: In future releases, this might
     * best be moved to a different file
     */
    public static String joinString(String separator, String[] items) {
        if (items.length < 1)
            return "";
        StringBuilder joined = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            joined.append(items[i]);
            if (i != items.length - 1)
                joined.append(separator);
        }
        return joined.toString();
    }

    /**
     * Print UTF-8 text to stdout (useful for debugging)
     * 
     * @param output
     *            The UTF-8 format string to be printed.
     */
    public static void printUTF8(String output) {
        try {
            BufferedWriter out = UTF8WriterBuilder(System.out);
            out.write(output);

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new BufferedWriter configured for UTF-8 output and connects it to
     * an OutputStream
     * 
     * @param out
     *            Outputstream to connect to.
     */
    public static BufferedWriter UTF8WriterBuilder(OutputStream out) throws Exception {
        return new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    }

    /**
     * Save UTF-8 format data to file.
     * 
     * @param dir
     *            directory to write to.
     * @param filename
     *            filename of file to write.
     * @param output
     *            UTF-8 format text to write
     */
    public static void saveUTF8(String dir, String filename, String output) {
        try {
            // Page name can contain invalid characters, see [1878113]
            // Contributed by Anatoly Techtonik
            filename = filename.replaceAll("[\\\\/:\\*\\?\\\"\\|\\<\\>]", "_");
            File path = new File(dir, filename);
            FileOutputStream f = new FileOutputStream(path);
            BufferedWriter out = UTF8WriterBuilder(f);
            out.write(output);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

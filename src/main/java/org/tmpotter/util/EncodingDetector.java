/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from OmegaT project
 * 
 *  Copyright (C) 2015 Aaron Madlon-Kay
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

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncodingDetector {

    /**
     * Detect the encoding of the supplied file. Convenience method for
     * {@link #detectEncoding(java.io.InputStream)}.
     */
    public static String detectEncoding(File inFile) throws IOException {
        FileInputStream stream = new FileInputStream(inFile);
        try {
            return detectEncoding(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Detect the encoding of the supplied file.
     *
     * @see <a href="https://code.google.com/p/juniversalchardet/">Original</a>
     * @see <a href="https://github.com/amake/juniversalchardet">Fork</a>
     */
    public static String detectEncoding(InputStream stream) throws IOException {
        UniversalDetector detector = new UniversalDetector(null);

        byte[] buffer = new byte[4096];
        int read;
        while ((read = stream.read(buffer)) > 0 && !detector.isDone()) {
            detector.handleData(buffer, 0, read);
        }

        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        return encoding;
    }

    /**
     * Detect the encoding of the supplied file. If detection fails, return the supplied default
     * encoding.
     */
    public static String detectEncodingDefault(File inFile, String defaultEncoding) {
        String detected = null;
        try {
            detected = detectEncoding(inFile);
        } catch (IOException ex) {
            // Ignore
        }
        return detected == null ? defaultEncoding : detected;
    }
}

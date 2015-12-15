/*
 * Copyright (C) 2015 Hiroshi Miura
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
package bitext2tmx.filters;

import java.io.File;

/**
 *
 * @author Hiroshi Miura
 */
public interface IFilter {
    
    /**
     * Human-readable name of the File Format this filter supports.
     * 
     * @return File format name
     */
    String getFileFormatName();
    
    /**
     * File format has both source and translated text?
     * 
     * @return  boolean true if format is combined such as TMX.
     */
    boolean isCombinedFileFormat();
    
    /**
     * Read source and translated files
     * 
     * @param sourceFile
     *            source file
     * @param targetFile
     *            translated file
     * @throws Exception
     */
    void load(File sourceFile, File targetFile) throws Exception;
    
    /**
     * Read document
     * 
     * @param sourceFile
     *            source file
     * @param targetFile
     *            translated file
     * @throws Exception
     */
    void load( File fPath, String sourceEncode ) throws Exception;
    
}

/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *                2006 Martin Wunderlich
 *                2011 Alex Buloichik, Didier Briel,
 *                2012 Guido Leenders
 *                2015 Aaron Madlon-Kay
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

package org.tmpotter.filters;

import org.tmpotter.exceptions.TranslationException;
import org.tmpotter.util.EncodingDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;


/**
 * The base class for all filters.
 * <p>
 * Each filter should extend this class or one of its descendants.
 * <p>
 * The process how the filter works is the following:
 * <ol>
 * <li>Source text is extracted.
 * <li>Tags are converted into shortcuts and these shortcuts are temporarily stored
 * <li>Source text with shortened tags is sent to core
 * </ol>
 *
 * @author Maxym Mykhalchuk
 * @author Martin Wunderlich
 * @author Alex Buloichik
 * @author Didier Briel
 * @author Guido Leenders
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 */
public abstract class AbstractFilter implements IFilter {

    protected String inEncodingLastParsedFile;

    /**
     * Callback for parse.
     */
    protected IParseCallback entryParseCallback;

    /**
     * Options for processing time.
     */
    protected Map<String, String> processOptions;

    /**
     * Human-readable name of the File Format this filter supports.
     *
     * @return File format name
     */
    @Override
    public abstract String getFileFormatName();

    /**
     * Whether source encoding can be varied by the user.
     * <p>
     * True means that OmegaT should handle all the encoding mess.
     * <p>
     * Return false to state that your filter doesn't need encoding management provided by OmegaT,
     * because it either autodetects the encoding based on file contents (like HTML filter does) or
     * the encoding is fixed (like in OpenOffice files).
     *
     * @return whether source encoding can be changed by the user
     */
    @Override
    public abstract boolean isSourceEncodingVariable();

    /**
     * Whether target encoding can be varied by the user.
     * <p>
     * True means that OmegaT should handle all the encoding mess.
     * <p>
     * Return false to state that your filter doesn't need encoding management provided by OmegaT,
     * because the encoding is fixed (like in OpenOffice files), or for some other reason.
     *
     * @return whether target encoding can be changed by the user
     */
    @Override
    public abstract boolean isTargetEncodingVariable();

    /**
     * Returns whether the file is supported by the filter,
     * given the reader with file's contents.
     * There exists a version of this method that takes file and encoding
     * {@link #isFileSupported(File, String)}. You should override only one of the two.
     * <p>
     * By default returns true, because this method should be overriden only by filters that
     * differentiate input files not by extensions, but by file's content.
     * <p>
     * For example, DocBook files have .xml extension, as possibly many other XML files,
     * so the filter should check a DTD of the document.
     *
     * @param reader The reader of the source file
     * @return Does the filter support the file
     */
    protected boolean isFileSupported(BufferedReader reader) {
        return true;
    }

    /**
     * Returns whether the file is supported by the filter, given the file and possible file's
     * encoding ( <code>null</code> encoding means autodetect). Default implementation creates a
     * reader and calls {@link #isFileSupported(BufferedReader)}. You should override only one of
     * the two.
     * <p>
     * For example, DocBook files have .xml extension, as possibly many other XML files,
     * so the filter should check a DTD of the document.
     *
     * @param inFile Source file.
     * @param fc     Filter context.
     * @return Does the filter support the file.
     */
    @Override
    public boolean isFileSupported(File inFile, Map<String, String> config, FilterContext fc) {
        BufferedReader reader = null;
        try {
            reader = createReader(inFile, fc.getSourceEncoding());
            return isFileSupported(reader);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                // ignore it
            }
        }
    }

    @Override
    public boolean isCombinedFileFormat() {
        return false;
    }

    /**
     * Define fuzzy mark prefix for source which will be stored in TM. It's 'fuzzy' by default, but
     * each filter can redefine it.
     *
     * @return fuzzy mark prefix
     */
    @Override
    public String getFuzzyMark() {
        return "fuzzy";
    }

    /**
     * Returns the hint displayed while the user edits the filter, and when she adds/edits the
     * instance of this filter. The hint may be any string, preferably in a non-geek language.
     *
     * @return The hint for editing the filter in a non-geek language.
     */
    @Override
    public String getHint() {
        return "";
    }

    /**
     * Creates a reader of an input file.
     *
     * @param inFile     The source file.
     * @param inEncoding Encoding of the input file. null means filter default encoding.
     * @return The reader for the source file
     * @throws UnsupportedEncodingException Thrown if JVM doesn't support the specified inEncoding
     * @throws IOException                  If any I/O Error occurs upon reader creation
     */
    protected BufferedReader createReader(File inFile, String inEncoding)
            throws UnsupportedEncodingException, IOException {
        InputStreamReader isr;
        if (inEncoding == null) {
            inEncoding = "UTF-8";
        }
        isr = new InputStreamReader(new FileInputStream(inFile), inEncoding);
        return new BufferedReader(isr);
    }

    /**
     * Processes a single file given a reader. Generally this method should read strings from the
     * input reader.
     * <p>
     * If you need more control over processed files, override
     * {@link #processFile(File, File, FilterContext)} instead.
     *
     * @param inFile Reader of the source file. It's the result of calling
     *               {@link #createReader(File, String)}.
     * @throws IOException          In case of any I/O error.
     * @throws TranslationException Should be thrown when processed file has any format defects.
     */
    protected abstract void processFile(BufferedReader inFile, FilterContext fc) throws IOException,
            TranslationException;

    /**
     * Process source and translated file.
     *
     * @param sourceFile     source file buffer reader
     * @param translatedFile translated file buffer reader
     * @param fc             filter context for file load
     * @throws java.io.IOException                          In case of any I/O error.
     * @throws org.tmpotter.exceptions.TranslationException Should be thrown when processed file has
     *     any format defects.
     */
    protected abstract void processFile(BufferedReader sourceFile, BufferedReader translatedFile,
                                        FilterContext fc) throws IOException, TranslationException;

    /**
     * Processes a single file given an input. This method can be used to create a filter that works
     * with the source/target files directly, rather than using BufferedReader.
     * <p>
     * Generally this method should read strings from the input reader and write them to the output
     * reader.
     * <p>
     * If you override this method and do all the processing here, you should simply implement
     * {@link #processFile(BufferedReader)} with a stub.
     *
     * @param inFile The source file.
     * @param fc     Filter context.
     * @throws IOException          In case of any I/O error.
     * @throws TranslationException Should be thrown when processed file has any format defects.
     * @returns List of processed files (each element of type {@link File}) or null if the filter
     *     can not/did not process multiple files.
     */
    protected void processFile(File inFile, FilterContext fc) throws IOException,
            TranslationException {
        String encoding = getSourceEncoding(fc, inFile);
        BufferedReader reader = createReader(inFile, encoding);
        inEncodingLastParsedFile = encoding == null ? Charset.defaultCharset().name() : encoding;

        try {
            processFile(reader, fc);
        } finally {
            reader.close();
        }
    }

    /**
     * Processes twe files given an input. This method can be used to create a filter that works
     * with the source/target files directly, rather than using BufferedReader.
     * Generally this method should read strings from the input reader.
     * <p>
     * If you override this method and do all the processing here, you should simply implement
     * {@link #processFile(BufferedReader, BufferedReader)} with a stub.
     *
     * @param sourceFile    The source file.
     * @param translateFile The translation file.
     * @param fc            Filter context.
     * @throws IOException          In case of any I/O error.
     * @throws TranslationException Should be thrown when processed file has any format defects.
     * @returns List of processed files (each element of type {@link File}) or null if the filter
     *     can not/did not process multiple files.
     */
    protected void processFile(File sourceFile, File translateFile, FilterContext fc)
            throws IOException, TranslationException {
        String sourceEncoding = getSourceEncoding(fc, sourceFile);
        BufferedReader sourceReader = createReader(sourceFile, sourceEncoding);
        inEncodingLastParsedFile = sourceEncoding == null ? Charset.defaultCharset().name()
            : sourceEncoding;
        String translateEncoding = getTranslateEncoding(fc, translateFile);
        BufferedReader translateReader = createReader(translateFile, translateEncoding);

        try {
            processFile(sourceReader, translateReader, fc);
        } finally {
            sourceReader.close();
            translateReader.close();
        }
    }

    /**
     * Get the input encoding. If it's not set in the FilterContext (setting is "&lt;auto&gt;") and
     * the filter allows ({@link #isSourceEncodingVariable()}), try to detect it. The result may be
     * null.
     *
     * @param fc     Filter context that has encoding
     * @param inFile target file
     * @return Encoding name
     * @throws IOException throw when Encoding detection fails
     */
    protected String getSourceEncoding(FilterContext fc, File inFile) throws IOException {
        String encoding = fc.getSourceEncoding();
        if (encoding == null && isSourceEncodingVariable()) {
            encoding = EncodingDetector.detectEncoding(inFile);
        }
        return encoding;
    }

    protected String getTranslateEncoding(FilterContext fc, File inFile) throws IOException {
        String encoding = fc.getTranslationEncoding();
        if (encoding == null && isTargetEncodingVariable()) {
            encoding = EncodingDetector.detectEncoding(inFile);
        }
        return encoding;
    }

    @Override
    public final void parseFile(File inFile, Map<String, String> config, FilterContext fc,
                                IParseCallback callback) throws Exception {
        entryParseCallback = callback;
        processOptions = config;

        try {
            processFile(inFile, fc);
        } finally {
            entryParseCallback = null;
            processOptions = null;
        }
    }

    @Override
    public final void parseFile(File inFile, File outFile, Map<String, String> config,
                                FilterContext fc, IParseCallback callback) throws Exception {
        entryParseCallback = callback;
        processOptions = config;

        try {
            processFile(inFile, outFile, fc);
        } finally {
            entryParseCallback = null;
            processOptions = null;
        }
    }

    /**
     * Set both callbacks. Used for child XML filters only.
     *
     * @param parseCallback to be set.
     */
    public void setCallback(IParseCallback parseCallback) {
        this.entryParseCallback = parseCallback;
    }

}

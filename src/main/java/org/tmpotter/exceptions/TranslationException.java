/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from OmegaT project
 * 
 *  Copyright (C) 2007 - Zoltan Bartko
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

package org.tmpotter.exceptions;

/**
 * TranslationException may be thrown while parsing/writing out the file.
 * 
 * <p>Note that a filter may also throw IOException in case of any I/O errors.
 * 
 * @author Maxym Mykhalchuk
 */
@SuppressWarnings("serial")
public class TranslationException extends Exception {
  /**
   * Constructs an instance of <code>TranslationException</code> with the
   * specified detail message.
   * 
   * @param msg
   *            the detail message.
   */
  public TranslationException(String msg) {
    super(msg);
  }

  /**
   * Constructs an instance of <code>TranslationException</code> with the
   * specified detail message and cause.
   * 
   * @param msg
   *            the detail message.
   * @param cause
   *            cause the cause
   */
  public TranslationException(String msg, Throwable cause) {
    super(msg, cause);
  }
}


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

/**
 * FilterNotFoundException is a checked exception that may be thrown by FilterManager
 * when get a filter instance.
 *
 * @author Hiroshi Miura
 */
@SuppressWarnings("serial")
public class FilterNotFoundException extends Exception {

    /**
     * Constructs an instance of <code>FilterNotFoundException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public FilterNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>FilterNotFoundException</code> with the specified detail
     * message and cause.
     *
     * @param msg   the detail message.
     * @param cause cause the cause
     */
    public FilterNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs an instance of <code>FilterNotFoundException</code> with the specified cause.
     *
     * @param cause cause the cause
     */
    public FilterNotFoundException(Throwable cause) {
        super(cause);
    }
}

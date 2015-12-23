/**************************************************************************
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
 **************************************************************************/

package bitext2tmx.util;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *  Localization: localization functionality.
 */
public final class Localization {
  //  Init the bundle
  private static ResourceBundle _bundle = ResourceBundle.getBundle("bitext2tmx/Bundle");

  /**
   * Returns resource bundle.
   */
  public static ResourceBundle getResourceBundle() {
    return _bundle;
  }

  /**
   *  Private constructor.
   * 
   *  @noinspection UNUSED_SYMBOL
   */
  private Localization() {}

  /**
   *  l10n: return localized string for given key.
   *
   *  @param  String key
   *  @return String translated string
   */
  public static final String getString( final String strKey ) {
    return ( _bundle.getString( strKey ) );
  }

  /**
   * Exception on localization.
   * 
   */
  @SuppressWarnings("serial")
  public static final class LocalizationException extends Exception {
    public LocalizationException() {
      super();
    }

    /**
     *  LocalizationException with message parameter.
     * 
     * @param msg exception message
     */
    public LocalizationException( final String msg ) {
      super( msg );
    }
  }
}
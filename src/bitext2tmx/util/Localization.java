/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2006-2009 Raymond: Martin et al.
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
*/

package bitext2tmx.util;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 *  Localization: localization functionality
 */
final public class Localization
{
  //  Resource bundle property file prefix
  //  Path(base name) to these files is determined from the classpath
  //  set for the VM. This is set externally at the moment.
  //  Could be set programmatically. -RM
  final private static String _strPropertyPrefix = "bitext2tmx-rb";

  private static Locale _locCurrent = Locale.getDefault();

  //  Init the bundle
  private static ResourceBundle _rbDefault =
    getResourceBundle( Locale.getDefault() );

  //  Resource Bundle for a locale
  //  Only used at startup or everytime lang changes?
  //  Does setDefault superceded this ?
  final public static ResourceBundle getResourceBundle( final Locale loc )
  { return( ResourceBundle.getBundle( _strPropertyPrefix, loc ) ); }

  final public static void updateLocalization()
  {
    //System.out.println( "refreshLocalization" );
    //_rbDefault = getResourceBundle( Locale.getDefault() );
    _rbDefault = getResourceBundle( _locCurrent );
  }


  //  Set the default locale
  //  Call globally to change language/locale?
  //  Should result in re-initialization of UI
  final public static void setLocale( final Locale loc )
  {
    //System.out.println( "setLocale" );
    Locale.setDefault( loc );

    _locCurrent = loc;
  }

  final public static Locale getLocale()
  {
    //System.out.println( "setLocale" );
    return( _locCurrent );
  }

  final public static void printLocale()
  { System.out.println( _rbDefault.getLocale().getDisplayName() ); }

  /**  Language ID, the current one - for later use */
  private static String _strLanguageId = null;

  /**
   *  Private constructor
   *  @noinspection UNUSED_SYMBOL
   */
  private Localization() {}

  /**
   *  getCurrentLanguageId: Current language ID accessor
   *  @param  void
   *  @return String
   */
  final public static String getCurrentLanguageId()
  { return( _strLanguageId ); }

  /**
   *  setCurrentLanguageId: Current language ID mutator
   *
   *  @param  String
   *  @return void
   */
  final public static void setCurrentLanguageId( final String strLanguageId )
    throws LocalizationException
  {
    if( strLanguageId != null ) _strLanguageId = strLanguageId;
    else
      throw new LocalizationException( "Language ID cannot be null" );
  }

  /**
   *  l10n: return localized string for given key
   *
   *  @param  String
   *  @return String
   */
  final public static String l10n( final String strKey )
  { return( _rbDefault.getString( strKey ) ); }


  final static public class LocalizationException extends Exception
  {
    public LocalizationException() { super(); }

    /**
    *  LocalizationException with message parameter
    *  @param  String
    *  @return void
    */
    public LocalizationException( final String msg ) { super( msg ); }
  }

}//  Localization{}



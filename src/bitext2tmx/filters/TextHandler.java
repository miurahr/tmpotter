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

package bitext2tmx.filters;

import java.io.File;

/**
 * Bi-Text loader.
 * 
 * <p> TBD
 * 
 * @author Hiroshi Miura
 */
public class TextHandler implements IImportFilter {
  @Override
  public boolean isCombinedFileFormat() {
    return false;
  }

  @Override
  public String getFileFormatName() {
    return "bi-text";
  }
  
  @Override
  public void load(File fSourceFile, File fTargetFile) throws Exception {
    /*      while( _alstOriginal.size() > _alstTranslation.size() )
        _alstTranslation.add( _alstTranslation.size(), "" );

      while( _alstTranslation.size() > _alstOriginal.size() )
        _alstOriginal.add( _alstOriginal.size(), "" );
      while( limpiar )
      {
        if( ( ( _alstOriginal.get( _alstOriginal.size() - 1 ) == null )
           || ( _alstOriginal
            .get( _alstOriginal.size() - 1 ).equals( "" ) ) )
            && ( ( _alstTranslation.get( _alstTranslation.size() - 1 ) == null )
          || ( _alstTranslation
                .get( _alstTranslation.size() - 1 ).equals( "" ) ) ) )
        {
          _alstOriginal.remove( _alstOriginal.size() - 1 );
          _alstTranslation.remove( _alstTranslation.size() - 1 );
        }
        else limpiar = false;
      }
  */
  }
  
  @Override
  public void load(File sourceFile, String encoding ) throws Exception {
    
  }
}

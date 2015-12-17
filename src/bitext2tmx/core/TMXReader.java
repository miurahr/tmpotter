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
package bitext2tmx.core;

import static bitext2tmx.util.Localization.getString;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author miurahr
 */
public class TMXReader {
  
  private static String  _strTMXEnc;
  private static String  _strLangOriginal    = "en";
  private static String  _strLangTranslation = "en";
  

  //  FixMe: only reads TMX 1.1-1.2
  //  ToDo: need to read TMX 1.1-1.4
 /**
  *  Read in TMX
  */
  public static void readTMX( final File fPathOriginal, final String encodeing,
                                       String langOriginal, String langTranslation,
                             Document _alstOriginal, Document _alstTranslation)
  {
    ArrayList<String> _alstBitext = new ArrayList<>();
    
    try
    {
      final FileInputStream fis = new FileInputStream( fPathOriginal );
      final InputStreamReader isr;

      if( encodeing.equals( getString( "ENCODING.DEFAULT" ) ) )
        isr = new InputStreamReader( fis );
      else if(encodeing.equals( "UTF-8" ) )
        isr = new InputStreamReader( fis, "UTF-8" );
      else
        isr = new InputStreamReader(fis, "ISO-8859-1");

      final BufferedReader br = new BufferedReader( isr );
      String linea;
      int cont   = 0;
      int indice = 0;
      int aux    = 0;
      boolean pI      = false;
      boolean idioma1 = false;

      while( ( linea = br.readLine() ) != null ) 
      {
        linea = linea.trim();
        _alstBitext.add( cont, linea );
        cont++;

        //Si no tengo los idiomas
        if( !idioma1 )
        {
          if( linea.indexOf( "<tu tuid" ) != -1 ) aux = 0;
          else if( linea.indexOf( "</tu>" ) != -1 )
          {
            if( aux == 2 ) idioma1 = true;
            else aux = 0;
          }
          else if( linea.indexOf( "<tuv" ) != -1)
          {
            if( aux == 0 )
            {
              indice = linea.indexOf( "lang=" );
              _strLangOriginal = linea.substring( indice + 6, indice + 8 );
              aux++;
            }
            else
            {
              indice = linea.indexOf( "lang=" );
              _strLangTranslation = linea.substring( indice + 6, indice + 8 );
              aux++;
            }
          }
        }
      }

      idioma1 = false;
      cont = 0;
      aux  = 0;

      while( cont < _alstBitext.size() )
      {
        linea = (String)_alstBitext.get( cont );

        while( linea.indexOf( "  " ) > -1 )
        {
          linea = linea.substring( 0, linea.indexOf( "  " ) ) +
            linea.substring( linea.indexOf( "  " ) + 1 );
        }

        if (linea.indexOf("<tuv") != -1)
        {
          if( linea.indexOf( _strLangOriginal ) != -1 )     pI = true;
          else if( linea.indexOf( _strLangTranslation ) != -1) pI = false;
        }
        else if( linea.indexOf( "<seg>" ) != -1 )
        {
          linea = removeTag( linea, "seg" );

          while( linea.indexOf( "  " ) > -1 )
          {
            linea = linea.substring( 0, linea.indexOf( "  " ) ) +
              linea.substring( linea.indexOf( "  " ) + 1 );
          }

          if( pI )
          {
            _alstOriginal.add( aux, linea );
            _alstTranslation.add( aux, "" );
            idioma1 = true;
            aux++;
          }
          else
          {
            if( !idioma1 )
            {
              _alstOriginal.add( aux, "" );
              _alstTranslation.add( aux, linea );
              aux++;
            }
            else _alstTranslation.set( _alstTranslation.size()-1, linea );
          }
        }

        cont++;
      }

      br.close();
    }
    catch( final java.io.IOException ex ) { System.out.println( ex ); }

  }


  /**
   *  Remove tag
   *
   *  @param linea : la cadena que hay que retocar,
   *  @param etiqueta : que queremos quitar
   *  @return cad devuelve la cadena sin la etiqueta
   */
  private static String removeTag( final String linea, final String etiqueta )
  {
    String cad = "";
    final String etiquetaCerrada = "</" + etiqueta + ">";

    if( !linea.contains(etiquetaCerrada) ) {
      // FIXME
    } else {
      cad = linea.substring( etiqueta.length() + 2, linea.indexOf( etiquetaCerrada ) );
    }
    return( cad );
  }


}

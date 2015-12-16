
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
package bitext2tmx.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import bitext2tmx.util.RuntimePreferences;

import static bitext2tmx.util.Localization.getString;

/**
 *
 * @author miurahr
 */
public class Document {

  private static final long serialVersionUID = -4962008112386443862L;

  final private ArrayList<String>  documentSegments = new ArrayList<>();
  final private String _strOriginal;

  public Document(String strOriginal) {
    _strOriginal = strOriginal;
  }

  public boolean isEmpty(){
    return documentSegments.isEmpty();
  }

  public int size(){
    return documentSegments.size();
  }

  public String remove(int index){
    return documentSegments.remove(index);
  }

  public void add(int index, String ele){
    documentSegments.add(index, ele);
  }

  public void add(String ele){
    documentSegments.add(ele);
  }

  public String get(int index){
    return  documentSegments.get(index);
  }

  public void set(int index, String content){
    documentSegments.set(index, content);
  }

  /**
   *  Reads in document to string with the original or translation text
   *  so it can be segmented
   *
   * @param strEncoding
   */
  public void readDocument( final String strEncoding )
  {
    final StringBuilder sb = new StringBuilder();
    String doc = "";
    boolean limpiar = true;
    String linea = "";

    try
    {
      final FileInputStream fis;

      fis = new FileInputStream( _strOriginal );
      
      final InputStreamReader isr;
      final BufferedReader br;

      if( strEncoding.equals(  getString( "ENCODING.DEFAULT" ) ) )
        isr = new InputStreamReader( fis );
      else
        isr = new InputStreamReader( fis, strEncoding );

      br = new BufferedReader( isr );

      while( ( linea = br.readLine() ) != null )
      {
        linea = linea.trim();

        if( !linea.equals( "" ) )
        {
          linea = linea + "\n";
          sb.append( linea );
        }
        else
          if( !sb.equals( "" ) )
          {
            sb.append( "\n" );
          }
      }

      doc = sb.toString();

      if( RuntimePreferences.isSegmentByLineBreak() )
        segmentWithBreak( doc );
      else segmentWithoutBreak( doc );

    }
    catch( final java.io.IOException ex )
    {

    }
  }


  /**
   *
   *  Segments texts according to the programmed rules considering
   *  that a newline is not a segmentation boundary (two newlines
   *  are however)
   *
   *  @param document
   *        :string containing the whole text
   *
   */
  private void segmentWithoutBreak( final String document )
  {
    String result = "";
    char car      = ' ';
    char carAnt   = ' ';
    int cont = 0;
    boolean kpunto = false;
    boolean kcar   = false;

    for( int i = 0; i < document.length(); i++ )
    {
      car = document.charAt( i );

      //  This code is repeated in various places -> new method -RM
      if( car == '\n' || car == '\t' ) result = result + ' ';
      else result = result + car;

      if( car == ' ' )
      {
        if( carAnt == '.' || carAnt == ';' || carAnt == ':' || carAnt == '?' ||
          carAnt == '!' )
        {
          if( !result.equals( "" ) )
          {
            documentSegments.add( cont, result.trim() );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar  = false;
        }
        else if( carAnt == '"' && kpunto )
        {
          if( !result.equals( "" ) )
          {
             documentSegments.add( cont, result );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
        else if( kpunto && kcar )
        {
          if( !result.equals( "" ) )
          {
             documentSegments.add( cont, result );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
      }
      else if( car == '\n' && ( carAnt == '\n' || carAnt == '.' ) )
      {
        if( !result.equals( "" ) )
        {
           documentSegments.add( cont, result.trim() );
        }

        cont++;
        car = ' ';
        carAnt = ' ';
        result = "";
        kpunto = false;
        kcar = false;
      }
      else if( car == '.' ) kpunto = true;
      /*
       * else if(car >= '0' && car <= '9' && kpunto){ kpunto = false; }
       */
      else kcar = true;

      carAnt = car;
    }

    if( !result.equals( "" ) )
    {
       documentSegments.add( cont, result );
    }
  }

  private void segmentWithBreak( final String document)
  {
    String result = "";
    char car      = ' ';
    char carAnt   = ' ';
    int cont = 0;
    boolean kpunto = false;
    boolean kcar   = false;

    for( int i = 0; i < document.length(); i++ )
    {
      car = document.charAt( i );

      if( car == '\n' || car == '\t' ) result = result + ' ';
      else result = result + car;

      if( car == '\n' )
      {
        if( !result.equals( "" ) )
        {
          documentSegments.add( cont, result.trim() );
        }

        cont++;
        car    = ' ';
        carAnt = ' ';
        result = "";
        kpunto = false;
        kcar   = false;
      }
      else if( car == ' ' )
      {
        if( carAnt == '.' || carAnt == ';' || carAnt == ':' || carAnt == '?' ||
          carAnt == '!' )
        {
          if( !result.equals( "" ) )
          {
            documentSegments.add( cont, result.trim() );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
        else if( carAnt == '"' && kpunto )
        {
          if( !result.equals( "" ) )
          {
             documentSegments.add( cont, result.trim() );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
        else if( kpunto && kcar )
        {
          if( !result.equals( "" ) )
          {
            documentSegments.add( cont, result.trim() );
          }

          cont++;
          car    = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar   = false;
        }
      }
      else if( car == '.' ) kpunto = true;
      else kcar = true;

      carAnt = car;
    }

    if( !result.equals( "" ) )
    {
      documentSegments.add( cont, result.trim() );
    }
  }

}

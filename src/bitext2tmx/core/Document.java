
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

  public Document() {
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
   * Perform alignments:join.
   *
   * joins the selected row with the following.
   *
   * @param index: join index and index+1
   */
  public void join(final int index) {
    String cad;
    int length = documentSegments.size() - 1;
    cad = documentSegments.get(index);
    cad = cad.concat(" ");
    cad = cad.concat(documentSegments.get(index + 1));
    documentSegments.set(index, cad.trim());
    for (int i = index + 1; i < length; i++) {
      documentSegments.set(i, documentSegments.get(i + 1));
    }
    documentSegments.set(length, "");
  }
  
  /**
   * Perform alignments: delete.
   *
   * deletes the selected row
   *
   * @param index: to be deleted
   */
  public void delete(final int index) {
    int length = documentSegments.size() -1;
    for (int i = index ; i < length; i++) {
      documentSegments.set(i, documentSegments.get(i + 1));
    }
    documentSegments.set(length, "");
  }

  /**
   * Perform alignments: split.
   *
   * splits the selected row at the given position creating two
   * rows.
   *
   * @param index: join index and index+1
   * @param position: position at which the split is performed
   */
  public void split(final int index, final int position) {
    String cad;
    int length = documentSegments.size() - 1;
    assert length >= index;
    if (length == index ||
        !documentSegments.get(length).equals("")) {
      documentSegments.add(length + 1, documentSegments.get(length));
      length++;
    }
    for (int i = length; i > (index+1); i--) {
      documentSegments.set(i, documentSegments.get(i-1));
    }
    cad = documentSegments.get(index);
    if (position == 0) {
      documentSegments.set(index, "");
    } else {
      documentSegments.set(index, cad.substring(0, position).trim());
    }
    documentSegments.set(index+1, cad.substring(position).trim());
  }

  public void readDocument( String original, String encoding){
    readDocument(original, null, encoding);
  }

  /**
   *  Reads in document to string with the original or translation text
   *  so it can be segmented
   *
   * @param strEncoding
   */
  public void readDocument( String _strOriginal, String language, String strEncoding )
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

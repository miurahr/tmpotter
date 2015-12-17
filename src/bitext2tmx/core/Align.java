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

import java.util.ArrayList;

import bitext2tmx.util.Utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miurahr
 */
public class Align {
  
  private static final Logger LOG = Logger.getLogger(Align.class.getName());
    
  private Align(){
  }

  public static boolean align(Document orig, Document trans)
  {
    Document _alstOriginal = orig;
    Document _alstTranslation = trans;
    String _ult_recorridoinv;

    try
    {
      final int tamF   = _alstOriginal.size();
      final int tamM   = _alstTranslation.size();
      final float[] v1          = new float[tamF];
      final float[] v2          = new float[tamM];
      final float[][] votacion  = new float[tamF + 1][tamM + 1];
      final float[][] temporal  = new float[tamF + 1][tamM + 1];
      final float[][] resultado = new float[tamF + 1][tamM + 1];
      float ganancia_x = 5;
      float ganancia_s = 1;
      float ganancia_e = 1;
      int limite_d = 2;
      int cont  = 0;
      int cont2 = 0;
      final float DBL_MAX = 999999999;

      _ult_recorridoinv = "";

      // Inicializar los vectores con el tama�o de cada segmento.
      // Inicializar el vector resultado y votaciones a cero
      //
      // Initialize vectors with the size of each segment
      // Initialize the result vector and votes to zero
      for( cont = 0; cont < tamF; cont++ )
        v1[cont] = _alstOriginal.get( cont ).toString().length();

      for( cont = 0; cont < tamM; cont++ )
        v2[cont] = _alstTranslation.get( cont ).toString().length();

      for( cont = 0; cont <= tamF; cont++ )
      //{
        for( cont2 = 0; cont2 <= tamM; cont2++ )
        {
          votacion[tamF][tamM] = 0;
          resultado[tamF][tamM] = 0;
        }
      //}

      // Inicializar la primera columna y la primera fila de la matriz temporal
      // a ceros.
      // Initialize the first column and the first row of the temporary array
      // with zeros
      for( cont = 0; cont <= tamF; cont++ ) temporal[cont][0] = DBL_MAX;
      for( cont = 0; cont <= tamM; cont++ ) temporal[0][cont] = DBL_MAX;

      temporal[0][0] = 0;

      for( int d = 1; d <= limite_d; d++ )
        for( int i = 1; i <= tamF; i++ )
          for( int j = 1; j <= tamM; j++ )
            temporal[i][j] = Utilities.cost( temporal, i, j, v1, v2, d );

      // actualizaci�n de la matriz resultado
      // updating the result array
      votacion[tamF][tamM] += ganancia_x;
      int i = tamF;
      int j = tamM;

      while( i > 1 && j > 1 )
      {
        switch( Utilities.argmin3( temporal[i - 1][j - 1], temporal[i - 1][j],
            temporal[i][j - 1] ) )
        {
          case 1:
          {
            votacion[i - 1][j - 1] += ganancia_x;
            i--;
            j--;
            break;
          }
          case 2:
          {
            votacion[i - 1][j] += ganancia_s;
            i--;
            break;
          }
          case 3:
          {
            votacion[i][j - 1] += ganancia_e;
            j--;
            break;
          }
        }//  switch()
      }//  while()

      // c�lculo del camino con m�xima ganancia
      // computing the maximum-gain path
      for( i = 1; i <= tamF; i++ )
        for( j = 1; j <= tamM; j++ )
          resultado[i][j] = Utilities.max3( resultado[i - 1][j - 1], resultado[i - 1][j],
              resultado[i][j - 1] )
              + votacion[i][j];

      i = tamF;
      j = tamM;

      while( i > 1 && j > 1 )
      {
        switch( Utilities.argmax3( resultado[i - 1][j - 1], resultado[i - 1][j],
            resultado[i][j - 1] ) )
        {
          case 1:
          {
            _ult_recorridoinv += 'x';
            j--;
            i--;
            break;
          }
          case 2:
          {
            _ult_recorridoinv += 's';
            i--;
            break;
          }
          case 3:
          {
            _ult_recorridoinv += 'e';
            j--;
            break;
          }
        }//  switch()
      }//  while()

      // simplificaci�n de _ult_recorridoinv
      // simplification of _ult_recorridoinv
      char estado = 'x';
      String almacenamiento = "";
      i = tamF - 1;
      j = tamM - 1;

      for( cont = 0; cont < _ult_recorridoinv.length(); cont++ )
      {
        switch( _ult_recorridoinv.charAt( cont ) )
        {
          case 's':
          {
            i--;

            if( estado == 'e' && Utilities.isAlignedOKSE( v1, v2, i, j ) )
            {
              char[] almchar = almacenamiento.toCharArray();
              almchar[almacenamiento.length() - 1] = 'x';
              almacenamiento = new String( almchar );
              estado = 'x';
            }
            else
            {
              almacenamiento = almacenamiento + 's';
              estado = 's';
            }

            break;
          }
          case 'e':
          {
            j--;

            if( estado == 's' && Utilities.isAlignedOKES( v1, v2, i, j ) )
            {
              char[] almchar = almacenamiento.toCharArray();
              almchar[almacenamiento.length() - 1] = 'x';
              almacenamiento = new String( almchar );
              estado = 'x';
            }
            else
            {
              almacenamiento = almacenamiento + 'e';
              estado = 'e';
            }

            break;
          }
          case 'x':
          {
            i--;
            estado = 'x';
            almacenamiento = almacenamiento + 'x';

            break;
          }
        }//  switch()
      }//  for()

      _ult_recorridoinv = almacenamiento;

      int f1 = 1;
      int f2 = 1;
      final ArrayList<String> Source = new ArrayList<>();
      final ArrayList<String> Target   = new ArrayList<>();

      Source.add( _alstOriginal.get( 0 ) );
      Target.add( _alstTranslation.get( 0 ) );

      for( i = _ult_recorridoinv.length() - 1; i >= 0; i-- )
      {
        switch( _ult_recorridoinv.charAt( i ) )
        {
          case 'x':
          {
            Source.add( _alstOriginal.get( f1 ) );
            Target.add( _alstTranslation.get( f2 ) );
            f1++;
            f2++;

            break;
          }
          case 's':
          {
            Source.add( _alstOriginal.get( f1 ) );
            Target.add( "" );
            f1++;

            break;
          }
          case 'e':
          {
            Source.add( "" );
            Target.add( _alstTranslation.get( f2 ) );
            f2++;

            break;
          }
        }//  switch()
      }//  for()

      while( !_alstOriginal.isEmpty() )    _alstOriginal.remove( 0 );
      while( !_alstTranslation.isEmpty() ) _alstTranslation.remove( 0 );

      for( cont = 0; cont < Source.size(); cont++ )
        _alstOriginal.add(Source.get( cont ) );

      for( cont = 0; cont < Target.size(); cont++ )
        _alstTranslation.add(Target.get( cont ) );

      return( true );
    }
    

    //  FixMe: this should never happen if the program is designed properly
    //  It is very bad practice to have to catch OutOfMemoryError inside
    //  an app like this. A little pre-calculation/estimate of required memory
    //  from file sizes or related could subvert this altogether.
    catch( final java.lang.OutOfMemoryError ex )
    {
      LOG.log(Level.WARNING, "Oops", ex);
      return( false );
    }
  }
}

/*
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 * Copyright (C) 2005-2006 Susana Santos Antón
 *           (C) 2006-2009 Raymond: Martin et al
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

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Document aligner
 * 
 * @author Hiroshi Miura
 */
public class Aligner {
  
  private static final Logger LOG = Logger.getLogger(Aligner.class.getName());

  private Aligner(){
  }

  public static boolean align(Document originalDocument, Document translationDocument)
  {
    String _ult_recorridoinv;

    try
    {
      final int tamF   = originalDocument.size();
      final int tamM   = translationDocument.size();
      final float[] v1          = new float[tamF];
      final float[] v2          = new float[tamM];
      final float[][] vote  = new float[tamF + 1][tamM + 1];
      final float[][] temporary  = new float[tamF + 1][tamM + 1];
      final float[][] result = new float[tamF + 1][tamM + 1];
      float gainX = 5;
      float gainS = 1;
      float gainE = 1;
      int limitD = 2;
      int cont  = 0;
      int cont2 = 0;
      final float DBL_MAX = 999999999;

      _ult_recorridoinv = "";

      // Initialize vectors with the size of each segment
      // Initialize the result vector and votes to zero
      for( cont = 0; cont < tamF; cont++ )
        v1[cont] = originalDocument.get( cont ).length();

      for( cont = 0; cont < tamM; cont++ )
        v2[cont] = translationDocument.get( cont ).length();

      for( cont = 0; cont <= tamF; cont++ ) {
        for( cont2 = 0; cont2 <= tamM; cont2++ ) {
          vote[tamF][tamM] = 0;
          result[tamF][tamM] = 0;
        }
      }

      // Initialize the first column and the first row of the temporary array
      // with zeros
      for( cont = 0; cont <= tamF; cont++ ) temporary[cont][0] = DBL_MAX;
      for( cont = 0; cont <= tamM; cont++ ) temporary[0][cont] = DBL_MAX;

      temporary[0][0] = 0;

      for( int d = 1; d <= limitD; d++ )
        for( int i = 1; i <= tamF; i++ )
          for( int j = 1; j <= tamM; j++ )
            temporary[i][j] = cost(temporary, i, j, v1, v2, d );

      // updating the result array
      vote[tamF][tamM] += gainX;
      int i = tamF;
      int j = tamM;

      while( i > 1 && j > 1 )
      {
        switch( argmin3(temporary[i - 1][j - 1], temporary[i - 1][j],
            temporary[i][j - 1] ) )
        {
          case 1:
          {
            vote[i - 1][j - 1] += gainX;
            i--;
            j--;
            break;
          }
          case 2:
          {
            vote[i - 1][j] += gainS;
            i--;
            break;
          }
          case 3:
          {
            vote[i][j - 1] += gainE;
            j--;
            break;
          }
        }
      }

      // computing the maximum-gain path
      for( i = 1; i <= tamF; i++ )
        for( j = 1; j <= tamM; j++ )
          result[i][j] = max3(result[i - 1][j - 1], result[i - 1][j],
              result[i][j - 1] )
              + vote[i][j];

      i = tamF;
      j = tamM;

      while( i > 1 && j > 1 )
      {
        switch( argmax3(result[i - 1][j - 1], result[i - 1][j],
            result[i][j - 1] ) )
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
        }
      }

      // simplification of _ult_recorridoinv
      char been = 'x';
      String storage = "";
      i = tamF - 1;
      j = tamM - 1;

      for( cont = 0; cont < _ult_recorridoinv.length(); cont++ )
      {
        switch( _ult_recorridoinv.charAt( cont ) )
        {
          case 's':
          {
            i--;

            if( been == 'e' && isAlignedOKSE( v1, v2, i, j ) )
            {
              char[] storageChar = storage.toCharArray();
              storageChar[storage.length() - 1] = 'x';
              storage = new String( storageChar );
              been = 'x';
            }
            else
            {
              storage = storage + 's';
              been = 's';
            }

            break;
          }
          case 'e':
            j--;
            if(been == 's' && isAlignedOKES(v1, v2, i, j)) {
              char[] storageChar = storage.toCharArray();
              storageChar[storage.length() - 1] = 'x';
              storage = new String( storageChar );
              been = 'x';
            }
            else
            {
              storage = storage + 'e';
              been = 'e';
            }
            break;

          case 'x':
            i--;
            been = 'x';
            storage = storage + 'x';
            break;

          default:
            break;
        }
      }

      _ult_recorridoinv = storage;

      int f1 = 1;
      int f2 = 1;
      final ArrayList<String> Source = new ArrayList<>();
      final ArrayList<String> Target   = new ArrayList<>();

      Source.add(originalDocument.get( 0 ) );
      Target.add(translationDocument.get( 0 ) );

      for( i = _ult_recorridoinv.length() - 1; i >= 0; i-- ) {
        switch( _ult_recorridoinv.charAt( i ) ) {
          case 'x':
            Source.add(originalDocument.get( f1 ) );
            Target.add(translationDocument.get( f2 ) );
            f1++;
            f2++;
            break;
          
          case 's':
            Source.add(originalDocument.get( f1 ) );
            Target.add( "" );
            f1++;
            break;
          
          case 'e':
            Source.add( "" );
            Target.add(translationDocument.get( f2 ) );
            f2++;
            break;
            
          default:
            break;
        }
      }

      while( !originalDocument.isEmpty() )    originalDocument.remove( 0 );
      while( !translationDocument.isEmpty() ) translationDocument.remove( 0 );

      for( cont = 0; cont < Source.size(); cont++ )
        originalDocument.add(Source.get( cont ) );

      for( cont = 0; cont < Target.size(); cont++ )
        translationDocument.add(Target.get( cont ) );

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
  
  private static int argmin3(float a, final float b, final float c) {
    int iArgMin3 = 0;
    if (b < a) {
      a = b;
      ++iArgMin3;
    }
    if (c < a) {
      return 3;
    }
    return ++iArgMin3;
  }

  private static float max3(float a, final float b, final float c) {
    if (b > a) {
      a = b;
    }
    if (c > a) {
      return c;
    }
    return a;
  }

  private static int argmax3(float a, final float b, final float c) {
    int iArgMax3 = 0;
    if (b > a) {
      a = b;
      ++iArgMax3;
    }
    if (c > a) {
      return 3;
    }
    return ++iArgMax3;
  }

  private static float min3(float a, final float b, final float c) {
    if (b < a) {
      a = b;
    }
    if (c < a) {
      return c;
    }
    return a;
  }

  private static float cost(final float[][] mat, final int i, final int j, final float[] v1, final float[] v2, final int d) {
    final float b2 = Math.abs(v1[i - 1] - v2[j - 1]);
    return min3(mat[i - 1][j] + v1[i - 1] / d, mat[i - 1][j - 1] + b2, mat[i][j - 1] + v2[j - 1] / d);
  }

  private static boolean isAlignedOKSE(final float[] v1, final float[] v2, final int i, final int j) {
    return Math.abs(v1[i] - v2[j - 1]) < Math.abs(v1[i] - v2[j]);
  }

  private static boolean isAlignedOKES(final float[] v1, final float[] v2, final int i, final int j) {
    return Math.abs(v1[i - 1] - v2[j]) < Math.abs(v1[i] - v2[j]);
  }

}

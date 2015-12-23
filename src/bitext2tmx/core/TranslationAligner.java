/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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

package bitext2tmx.core;

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Document aligner.
 * 
 * @author Hiroshi Miura
 */
public class TranslationAligner {
  
  private static final Logger LOG = Logger.getLogger(TranslationAligner.class.getName());
  private static final float DBL_MAX = 999999999;

  private TranslationAligner(){
  }

  /**
   * align bitext documents automaticaly.
   * 
   * @param originalDocument  source document
   * @param translationDocument translation document
   * @return true if success
   */
  public static boolean align(Document originalDocument,
          Document translationDocument) {
    String ultRecov;

    try {
      final int tamF   = originalDocument.size();
      final int tamM   = translationDocument.size();
      final float[] v1          = new float[tamF];
      final float[] v2          = new float[tamM];
      final float[][] vote  = new float[tamF + 1][tamM + 1];
      final float[][] costArray  = new float[tamF + 1][tamM + 1];
      final float[][] result = new float[tamF + 1][tamM + 1];
      final float gainX = 5;
      final float gainS = 1;
      final float gainE = 1;
      final int limitD = 2;
      int cont  = 0;
      int cont2 = 0;

      ultRecov = "";

      // Initialize vectors with the size of each segment
      // Initialize the result vector and votes to zero
      for (cont = 0; cont < tamF; cont++) {
        v1[cont] = originalDocument.get(cont).length();
      }
      for (cont = 0; cont < tamM; cont++) {
        v2[cont] = translationDocument.get(cont).length();
      }

      for (cont = 0; cont <= tamF; cont++) {
        for (cont2 = 0; cont2 <= tamM; cont2++) {
          vote[tamF][tamM] = 0;
          result[tamF][tamM] = 0;
        }
      }

      // Initialize the first column and the first row of the temporary array
      // with zeros
      for (cont = 0; cont <= tamF; cont++) {
        costArray[cont][0] = DBL_MAX;
      }
      for (cont = 0; cont <= tamM; cont++) {
        costArray[0][cont] = DBL_MAX;
      }

      costArray[0][0] = 0;

      for (int d = 1; d <= limitD; d++) {
        for (int i = 1; i <= tamF; i++) {
          for (int j = 1; j <= tamM; j++) {
            costArray[i][j] = cost(costArray, i, j, v1, v2, d );
          }
        }
      }
      
      // updating the result array
      vote[tamF][tamM] += gainX;
      for (int i = tamF, j = tamM; i > 1 && j > 1;) {
        switch (argmin3(costArray[i - 1][j - 1], costArray[i - 1][j],
            costArray[i][j - 1])) {
          case 1:
            vote[i - 1][j - 1] += gainX;
            i--;
            j--;
            break;
          
          case 2:
            vote[i - 1][j] += gainS;
            i--;
            break;
          
          case 3:
            vote[i][j - 1] += gainE;
            j--;
            break;
          
          default:
            break;
        }
      }

      // computing the maximum-gain path
      for (int i = 1; i <= tamF; i++) {
        for (int j = 1; j <= tamM; j++) {
          result[i][j] = max3(result[i - 1][j - 1], result[i - 1][j],
              result[i][j - 1] )
              + vote[i][j];
        }
      }

      for (int i = tamF, j = tamM; i > 1 && j > 1;) {
        switch (argmax3(result[i - 1][j - 1], result[i - 1][j],
            result[i][j - 1])) {
          case 1:
            ultRecov += 'x';
            j--;
            i--;
            break;
          
          case 2:
            ultRecov += 's';
            i--;
            break;
          
          case 3:
            ultRecov += 'e';
            j--;
            break;

          default:
            break;
        }
      }

      // simplification of _ult_recorridoinv
      char been = 'x';
      String storage = "";
      int i1 = tamF - 1;
      int i2 = tamM - 1;

      for (cont = 0; cont < ultRecov.length(); cont++) {
        switch (ultRecov.charAt(cont)) {
          case 's':
            i1--;
            if (been == 'e' && isAlignedOkSe(v1, v2, i1, i2)) {
              char[] storageChar = storage.toCharArray();
              storageChar[storage.length() - 1] = 'x';
              storage = new String(storageChar);
              been = 'x';
            } else {
              storage = storage + 's';
              been = 's';
            }
            break;

          case 'e':
            i2--;
            if (been == 's' && isAlignedOkEs(v1, v2, i1, i2)) {
              char[] storageChar = storage.toCharArray();
              storageChar[storage.length() - 1] = 'x';
              storage = new String( storageChar );
              been = 'x';
            } else {
              storage = storage + 'e';
              been = 'e';
            }
            break;

          case 'x':
            i1--;
            been = 'x';
            storage = storage + 'x';
            break;

          default:
            break;
        }
      }

      ultRecov = storage;

      int f1 = 1;
      int f2 = 1;
      final ArrayList<String> Source = new ArrayList<>();
      final ArrayList<String> Target   = new ArrayList<>();

      Source.add(originalDocument.get( 0 ) );
      Target.add(translationDocument.get( 0 ) );

      for (i1 = ultRecov.length() - 1; i1 >= 0; i1--) {
        switch (ultRecov.charAt(i1)) {
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

      while (!originalDocument.isEmpty()) {
        originalDocument.remove(0);
      }
      while (!translationDocument.isEmpty()) {
        translationDocument.remove(0);
      }

      for (cont = 0; cont < Source.size(); cont++) {
        originalDocument.add(Source.get(cont));
      }

      for (cont = 0; cont < Target.size(); cont++) {
        translationDocument.add(Target.get(cont));
      }

      return true;
    //  FixMe: this should never happen if the program is designed properly
    //  It is very bad practice to have to catch OutOfMemoryError inside
    //  an app like this. A little pre-calculation/estimate of required memory
    //  from file sizes or related could subvert this altogether.
    } catch (OutOfMemoryError ex) {
      LOG.log(Level.WARNING, "Oops", ex);
      return false;
    }
  }
  
  private static int argmin3(float na, final float nb, final float nc) {
    int argMin3 = 0;
    if (nb < na) {
      na = nb;
      ++argMin3;
    }
    if (nc < na) {
      return 3;
    }
    return ++argMin3;
  }

  private static float max3(float na, final float nb, final float nc) {
    if (nb > na) {
      na = nb;
    }
    if (nc > na) {
      return nc;
    }
    return na;
  }

  private static int argmax3(float na, final float nb, final float nc) {
    int argMax3 = 0;
    if (nb > na) {
      na = nb;
      ++argMax3;
    }
    if (nc > na) {
      return 3;
    }
    return ++argMax3;
  }

  private static float min3(float na, final float nb, final float nc) {
    if (nb < na) {
      na = nb;
    }
    if (nc < na) {
      return nc;
    }
    return na;
  }

  private static float cost(final float[][] mat, final int i1, final int i2,
          final float[] v1, final float[] v2, final int dem) {
    final float b2 = Math.abs(v1[i1 - 1] - v2[i2 - 1]);
    return min3(mat[i1 - 1][i2] + v1[i1 - 1] / dem, mat[i1 - 1][i2 - 1] + b2,
            mat[i1][i2 - 1] + v2[i2 - 1] / dem);
  }

  private static boolean isAlignedOkSe(final float[] v1, final float[] v2,
          final int i1, final int i2) {
    return Math.abs(v1[i1] - v2[i2 - 1]) 
            < Math.abs(v1[i1] - v2[i2]);
  }

  private static boolean isAlignedOkEs(final float[] v1, final float[] v2,
          final int i1, final int i2) {
    return Math.abs(v1[i1 - 1] - v2[i2]) < Math.abs(v1[i1] - v2[i2]);
  }
}

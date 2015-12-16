/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006 Susana Santos Antón
#            (C) 2006-2009 Raymond: Martin et al
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

package bitext2tmx.engine;


/**
 *  Clase que contiene los elementos constitutivos de un cambio realizado
 *  sobre la alineaci�n del bitexto
 */
public class SegmentChanges
{
  int tipo;
  int pos;
  boolean fuente;
  String frase;
  int ident_linea;
  int [] numEliminada;

  //  constructores
  public SegmentChanges()
  {
    tipo        = 0;
    pos         = 0;
    fuente      = false;
    frase       = "";
    ident_linea = -1;
  }

  public SegmentChanges( int ktipo, int kpos, boolean kfuente, 
    String kfrase, int kident_linea ) 
  {
    tipo        = ktipo;
    pos         = kpos;
    fuente      = kfuente;
    frase       = kfrase;
    ident_linea = kident_linea;
  }

  //  m�todos
  public int     getTipo()         { return( tipo );  }
  public int     getPosition()          { return( pos );   }
  public String  getFrase()        { return( frase ); }
  public int     getIdent_linea()  { return( ident_linea );  }
  public boolean getSource()       { return( fuente ); }
  public int[]   getNumEliminada() { return( numEliminada ); }

  public int getTam()
  {
    int tam = numEliminada.length;

    return( tam );
  }

  public void setTipo( int ktipo )         { tipo   = ktipo;   }
  public void setPos( int kpos )           { pos    = kpos;    }
  public void setFuente( boolean kFuente ) { fuente = kFuente; }
  public void setFrase( String kfrase )    { frase  = kfrase;  }

  public void setIdent_linea( int kident_linea ) 
  { ident_linea = kident_linea; }

  public void setNumEliminada( int[] keliminadas, int tam )
  {
    numEliminada = new int[tam];

    for( int cont=0; cont<tam; cont++ )
     numEliminada[cont] = keliminadas[cont];
  }

}//  SegmentChanges{}




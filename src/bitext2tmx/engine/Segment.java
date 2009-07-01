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
*
*/
/*
  This class is not strictly correct

  There should be separate segments for the original and translation
  this is really an alignment, it should contain the two segments
  
  or it is a row in the table
*/
final public class Segment
{
  private String _strNum;
  private String _strOriginal;
  private String _strTranslation;

  public Segment( final String strNum, final String strOriginal, final String strTranslation )
  {
    _strNum         = strNum;
    _strOriginal    = strOriginal;
    _strTranslation = strTranslation;
  }

  final public String getNum()         { return( _strNum ); }
  final public String getOriginal()    { return( _strOriginal ); }
  final public String getTranslation() { return( _strTranslation ); }

  final public void setNum( final String strNum )
  { _strNum = strNum; }

  final public void setOriginal( final String strOriginal )
  { _strOriginal = strOriginal; }

  final public void setTranslation( final String strTranslation )
  { _strTranslation = strTranslation; }

}//  Segment{}



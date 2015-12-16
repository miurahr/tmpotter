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

import static bitext2tmx.util.Utilities.getValidXMLText;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author miurahr
 */
public class TMXWriter {
  
  /**
   *  Esta funcion recompone el Tmx a partir de las posibles modificaciones
   *  efectuadas y lo deja en el directorio correspondiente segun los cambios
   *  realizados.
   * @param fNombre
   * @param _alstOriginal
   * @throws java.io.IOException
   */
  public void writeBitext( final File fNombre, ArrayList<String> _alstOriginal ) throws IOException
  {
    int cont = 0;
    final FileOutputStream fw;
    final OutputStreamWriter osw;
    final BufferedWriter bw;
    final PrintWriter pw;
    int max = 0;

    try
    {
      fw = new FileOutputStream( fNombre );
      //osw = new OutputStreamWriter(fw,cod_TMX);_strTMXEnc
      osw = new OutputStreamWriter( fw, _strTMXEnc );
      bw = new BufferedWriter( osw );
      pw = new PrintWriter( bw );

      max = largersizeSegments();
      //pw.println("<?xml version=\"1.0\" encoding=\"" + cod_TMX + "\"?>"); //poner el encoding
      pw.println( "<?xml version=\"1.0\" encoding=\"" + _strTMXEnc + "\"?>" ); //poner
      pw.println( "<tmx version=\"1.4\">" );
      //pw.println( "  <header creationtool=\"Bitext2tmx\" " +
                  //"creationtoolversion=\"1.0\" segtype=\"sentence\" o-tmf=\"Bitext2tmx\" " +
                  //"adminlang=\"en\" srclang=\"" +
                  //_strLangOriginal.toLowerCase() + "\" " +
                  //"datatype=\"PlainText\"  o-encoding=\"" + _strTMXEnc + "\">" );

      //pw.println(  );
      //pw.println(  );
      pw.println( "  <header" );
      pw.println( "    creationtool=\"Bitext2tmx\"" );
      pw.println( "    creationtoolversion=\"1.0\"" );
      pw.println( "    segtype=\"sentence\"" );
      pw.println( "    o-tmf=\"Bitext2tmx\""  );
      pw.println( "    adminlang=\"en\"" );
      pw.println( "    srclang=\"" + _strLangOriginal.toLowerCase() + "\"" );
      pw.println( "    datatype=\"PlainText\"" );
      pw.println( "    o-encoding=\"" + _strTMXEnc + "\"" );
      pw.println( "  >" );

      pw.println( "  </header>" );
      pw.println( "  <body>" );

      while( cont <= max )
      {
        if( !( _alstOriginal.get( cont ).equals( "" ) ) &&
            !( _alstTranslation.get( cont ).equals( "" ) ) )
        {
          pw.println( "  <tu tuid=\"" + ( cont ) + "\" datatype=\"Text\">" );

          if( max >= cont )
          {
            if( !( _alstOriginal.get( cont ).equals( "" ) ) )
            {
              pw.println( "    <tuv xml:lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
              //pw.println("      <seg>" + _alstOriginal.get( cont ) + "</seg>" );
              pw.println( "      <seg>" +
               getValidXMLText( (String)_alstOriginal.get( cont ) ) +
                "</seg>" );
              pw.println( "    </tuv>");
            }
            else
            {
              pw.println( "    <tuv xml:lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
              pw.println( "      <seg>  </seg>");
              pw.println( "    </tuv>");
            }
          }

          if( max >= cont )
          {
            if( !( _alstTranslation.get( cont ).equals( "" ) ) )
            {
              pw.println( "    <tuv xml:lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
              //pw.println("      <seg>" + _alstTranslation.get( cont ) + "</seg>");
              pw.println( "      <seg>" +
                getValidXMLText( (String)_alstTranslation.get( cont ) ) +
                 "</seg>" );
              pw.println( "    </tuv>");
            }
            else
            {
              pw.println("    <tuv xml:lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
              pw.println("      <seg>  </seg>");
              pw.println("    </tuv>");
            }
          }

          pw.println( "  </tu>" );
        }
        //Para que no hayan unidades de traducci?n con elementos vac?os.
        cont++;
      }

      pw.println( "  </body>" );
      pw.println( "</tmx>" );
      pw.close();
    }
    catch( final IOException ex )
    {
      throw ex;
    }
  }

}

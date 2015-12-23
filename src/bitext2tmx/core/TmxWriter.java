/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
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

import static bitext2tmx.util.xml.XMLUtil.getValidXMLText;

import bitext2tmx.util.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * save to TMX file.
 * 
 * @author miurahr
 */
public class TmxWriter {

  /**
   *
   * @param outFileName file name to be written
   * @param originalDocument Document to be written as source
   * @param langOriginal  language tag for source
   * @param translationDocument Document to be written as translation
   * @param langTranslation language tag for translation
   * @param encoding character encoding to be written to TMX
   * @throws java.io.IOException may happen while writing file
   */
  public static void writeBitext( final File outFileName,
          Document originalDocument, String langOriginal,
          Document translationDocument, String langTranslation,
          String encoding) throws IOException {
    int cont = 0;
    final FileOutputStream fw;
    final OutputStreamWriter osw;
    final BufferedWriter bw;
    final PrintWriter pw;
    int max = 0;

    String tmxEncoding = encoding;
    try {
      fw = new FileOutputStream(outFileName);
      osw = new OutputStreamWriter(fw, tmxEncoding);
      bw = new BufferedWriter( osw );
      pw = new PrintWriter( bw );

      max = Utilities.largerSize(originalDocument.size(),
              translationDocument.size()) - 1 ;
      pw.println("<?xml version=\"1.0\" encoding=\"" + tmxEncoding + "\"?>");
      pw.println("<tmx version=\"1.4\">" );
      pw.println( "  <header" );
      pw.println( "    creationtool=\"Bitext2tmx\"" );
      pw.println( "    creationtoolversion=\"1.0\"" );
      pw.println( "    segtype=\"sentence\"" );
      pw.println( "    o-tmf=\"Bitext2tmx\""  );
      pw.println( "    adminlang=\"en\"" );
      pw.println( "    srclang=\"" + langOriginal.toLowerCase() + "\"" );
      pw.println( "    datatype=\"PlainText\"" );
      pw.println( "    o-encoding=\"" + tmxEncoding + "\"" );
      pw.println( "  >" );

      pw.println( "  </header>" );
      pw.println("  <body>" );

      while (cont <= max) {
        if (!( originalDocument.get( cont ).equals( "" ))
            && !( translationDocument.get( cont ).equals( "" ) ) ) {
          pw.println("  <tu tuid=\"" + ( cont ) + "\" datatype=\"Text\">" );

          if (max >= cont) {
            if (!( originalDocument.get( cont ).equals( "" ))) {
              pw.println("    <tuv xml:lang=\"" + langOriginal.toLowerCase() + "\">" );
              pw.println("      <seg>"
                  + getValidXMLText( (String)originalDocument.get(cont))
                  + "</seg>");
              pw.println("    </tuv>");
            } else {
              pw.println("    <tuv xml:lang=\"" + langOriginal.toLowerCase() + "\">" );
              pw.println("      <seg>  </seg>");
              pw.println("    </tuv>");
            }
          }

          if (max >= cont) {
            if (!( translationDocument.get( cont ).equals( "" ))) {
              pw.println("    <tuv xml:lang=\""
                      + langTranslation.toLowerCase() + "\">" );
              pw.println("      <seg>"
                      + getValidXMLText(translationDocument.get(cont)) 
                      + "</seg>");
              pw.println("    </tuv>");
            } else {
              pw.println("    <tuv xml:lang=\"" + langTranslation.toLowerCase()
                      + "\">" );
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
    
    } catch (IOException ex) {
      LOG.log(Level.WARNING, "IO error", ex);
    }
  }

  private static final Logger LOG = Logger.getLogger(TmxWriter.class.getName());

}

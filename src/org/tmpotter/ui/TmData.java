/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * ************************************************************************/

package org.tmpotter.ui;

import org.tmpotter.core.Document;
import org.tmpotter.core.SegmentChanges;

import java.io.File;
import java.util.ArrayList;


/**
 * TM data holder.
 *
 * @author Hiroshi Miura
 */
public class TmData {

  protected int topArrays; //  =  0;
  protected final ArrayList<SegmentChanges> arrayListChanges = new ArrayList<>();
  private int identChanges = -1;
  protected int identLabel; //  =  0;
  protected int identAnt; //  =  0;
  protected int positionTextArea; //  =  0;
  protected Document documentOriginal;
  protected Document documentTranslation;
  protected String stringLangOriginal = "en";
  protected String stringLangTranslation = "en";
  protected String stringOriginal;
  protected String stringTranslation;
  protected final ArrayList arrayListBitext = new ArrayList();
  protected final ArrayList arrayListLang = new ArrayList();
  protected File filePathTranslation;
  protected File filePathOriginal;

  /**
   * Updates the changes adding a "join" change in the "undo" array and performs
   * the "join". (not sure about the translation)
   *
   * @param textAreaIzq :TRUE if the left text (source text) has to be joined
   */
  void join(final boolean textAreaIzq) {
    if (identLabel != topArrays) {
      final SegmentChanges Changes = new SegmentChanges(0, positionTextArea, textAreaIzq, "",
          identLabel);
      arrayListChanges.add(identChanges, Changes);
      if (textAreaIzq) {
        Changes.setFrase(documentOriginal.get(identLabel));
      } else {
        Changes.setFrase(documentTranslation.get(identLabel));
      }
      if (textAreaIzq) {
        documentOriginal.join(identLabel);
      } else {
        documentTranslation.join(identLabel);
      }
    }
  }

  /**
   * Delete text.
   *
   * <p>This function updates the changes adding a delete change
   * to the undo array and deletes
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be deleted
   */
  void delete(final boolean textAreaIzq) {
    final SegmentChanges Changes = new SegmentChanges(1, positionTextArea, textAreaIzq, "",
        identLabel);
    arrayListChanges.add(identChanges, Changes);
    if (textAreaIzq) {
      Changes.setFrase(documentOriginal.get(identLabel));
    } else {
      Changes.setFrase(documentTranslation.get(identLabel));
    }
    if (textAreaIzq) {
      documentOriginal.delete(identLabel);
    } else {
      documentTranslation.delete(identLabel);
    }
  }

  /**
   * Function Split.
   *
   * <p>This function updates the changes adding a split to the undo
   * array and performs the splitting
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be split
   */
  void split(final boolean textAreaIzq) {
    if (textAreaIzq) {
      if (positionTextArea >= documentOriginal.get(identLabel).length()) {
        positionTextArea = 0;
      }
    } else if (positionTextArea >= documentTranslation.get(identLabel).length()) {
      positionTextArea = 0;
    }
    final SegmentChanges Changes = new SegmentChanges(2, positionTextArea, textAreaIzq, "",
        identLabel);
    arrayListChanges.add(identChanges, Changes);
    if (textAreaIzq) {
      Changes.setFrase(documentOriginal.get(identLabel));
    } else {
      Changes.setFrase(documentTranslation.get(identLabel));
    }
    if (textAreaIzq) {
      documentOriginal.split(identLabel, Changes.getPosition());
    } else {
      documentTranslation.split(identLabel, Changes.getPosition());
    }
  }

  /**
   * Function IgualarArrays: adds rows to the smallest array and deletes blank
   * rows.
   */
  void matchArrays() {
    boolean limpiar = true;
    while (documentOriginal.size() > documentTranslation.size()) {
      documentTranslation.add(documentTranslation.size(), "");
    }
    while (documentTranslation.size() > documentOriginal.size()) {
      documentOriginal.add(documentOriginal.size(), "");
    }
    while (limpiar) {
      if (documentOriginal.get(documentOriginal.size() - 1) == null
          || (documentOriginal.get(documentOriginal.size() - 1).equals(""))
          && (documentTranslation.get(documentTranslation.size() - 1) == null
          || documentTranslation.get(documentTranslation.size() - 1).equals(""))) {
        documentOriginal.remove(documentOriginal.size() - 1);
        documentTranslation.remove(documentTranslation.size() - 1);
      } else {
        limpiar = false;
      }
    }
    topArrays = documentOriginal.size() - 1;
    if (identLabel > (documentOriginal.size() - 1)) {
      identLabel = documentOriginal.size() - 1;
    }
  }

  protected int getIdentChanges() {
    return identChanges;
  }

  protected int incrementChanges() {
    return identChanges++;
  }

  protected int decrementChanges() {
    return identChanges--;
  }

  protected String getDocumentTranslation(int cont) {
    return documentTranslation.get(cont);
  }

  protected void removeDocumentTranslation(int cont) {
    documentTranslation.remove(cont);
  }

  protected void setDocumentTranslation(int cont, String text) {
    documentTranslation.set(cont, text);
  }

  protected int getDocumentTranslationSize() {
    return documentTranslation.size();
  }

  protected String getDocumentOriginal(int cont) {
    return documentOriginal.get(cont);
  }

  protected void removeDocumentOriginal(int cont) {
    documentOriginal.remove(cont);
  }

  protected void setDocumentOriginal(int cont, String text) {
    documentOriginal.set(cont, text);
  }

  protected int getDocumentOriginalSize() {
    return documentOriginal.size();
  }

  protected boolean isIdentTop() {
    return (identLabel == topArrays);
  }
  
  protected boolean isSomeDocumentEmpty() {
    return (documentOriginal.isEmpty() || documentTranslation.isEmpty());
  }

  protected void setBothIdent(int ident) {
    identLabel = ident;
    identAnt = identLabel;
  }

  protected void setIdentAntAsLabel() {
    identAnt = identLabel;
  }

  protected void setOriginalDocumentAnt(String text) {
    documentOriginal.set(identAnt, text);
  }

  protected void setTranslationDocumentAnt(String text) {
    documentTranslation.set(identAnt, text);
  }
  
  protected void clear() {
    documentOriginal.clean();
    documentTranslation.clean();
    int cont = arrayListBitext.size() - 1;
    while (!arrayListBitext.isEmpty()) {
      arrayListBitext.remove(cont--);
    }
    cont = arrayListChanges.size() - 1;
    while (!arrayListChanges.isEmpty()) {
      arrayListChanges.remove(cont--);
    }
    identChanges = -1;
    identLabel = 0;
    identAnt = 0;
    filePathTranslation = null;
    filePathOriginal = null;
    topArrays = 0;
  }
}

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
      final SegmentChanges Changes = new SegmentChanges(SegmentChanges.OperationKind.JOIN,
          positionTextArea, textAreaIzq, "", identLabel);
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
    final SegmentChanges Changes = new SegmentChanges(SegmentChanges.OperationKind.DELETE,
        positionTextArea, textAreaIzq, "", identLabel);
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
    final SegmentChanges Changes = new SegmentChanges(SegmentChanges.OperationKind.SPLIT,
        positionTextArea, textAreaIzq, "", identLabel);
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

  void tuSplit(int izq) {
    int cont;
    SegmentChanges changes;

    incrementChanges();
    documentOriginal.add(getDocumentOriginalSize(),
            getDocumentOriginal(getDocumentOriginalSize() - 1));
    documentTranslation.add(documentTranslation.size(),
            getDocumentTranslation(getDocumentTranslationSize() - 1));

    if (izq == 1) {
      // Left column.
      changes = new SegmentChanges(SegmentChanges.OperationKind.TUSPLIT,
          0, true, "", identLabel);

      for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
        setDocumentTranslation(cont, getDocumentTranslation(cont - 1));

        if (cont > (identLabel + 1)) {
          setDocumentOriginal(cont, getDocumentOriginal(cont - 1));
        } else {
          setDocumentOriginal(cont, "");
        }
      }

      documentTranslation.set(identLabel, "");
    } else {
      changes = new SegmentChanges(SegmentChanges.OperationKind.TUSPLIT,
          0, false, "", identLabel);

      for (cont = documentOriginal.size() - 1; cont > identLabel; cont--) {
        documentOriginal.set(cont, documentOriginal.get(cont - 1));

        if (cont > (identLabel + 1)) {
          setDocumentTranslation(cont, getDocumentTranslation(cont - 1));
        } else {
          setDocumentTranslation(cont, "");
        }
      }

      setDocumentOriginal(identLabel, "");
    }

    arrayListChanges.add(getIdentChanges(), changes);
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

  void undoJoin() {
    String cad;
    SegmentChanges ultChanges;
    ultChanges = arrayListChanges.get(getIdentChanges());
    identLabel = ultChanges.getIdent_linea();
    int position;
    boolean izq = ultChanges.getSource();
    final String cadaux = ultChanges.getFrase();

    if (izq) {
      cad = getDocumentOriginal(identLabel);
      if (!cad.equals("")) {
        cad = cad.trim();
      }
      position = cad.indexOf(cadaux) + cadaux.length();
    } else {
      cad = getDocumentTranslation(identLabel);
      if (!cad.equals("")) {
        cad = cad.trim();
      }
      position = cad.indexOf(cadaux) + cadaux.length();
    }
    if (ultChanges.getSource()) {
      documentOriginal.split(identLabel, position);
    } else {
      documentTranslation.split(identLabel, position);
    }
  }

  /**
   * Undoes the last delete.
   */
  void undoDelete() {
    SegmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
    identLabel = ultChanges.getIdent_linea();
    boolean izq = ultChanges.getSource();
    if (izq) {
      if (identLabel == documentOriginal.size()) {
        documentOriginal.add(identLabel, ultChanges.getFrase());
        if (documentOriginal.size() != documentTranslation.size()) {
          documentTranslation.add(documentTranslation.size(), "");
        }
      } else {
        documentOriginal.add(documentOriginal.size(), documentOriginal.get(documentOriginal.size() - 1));
        for (int cont = documentOriginal.size() - 1; cont > identLabel; cont--) {
          documentOriginal.set(cont, documentOriginal.get(cont - 1));
        }
        documentOriginal.set(identLabel, ultChanges.getFrase());
      }
    } else {
      if (identLabel == documentTranslation.size()) {
        documentTranslation.add(identLabel, ultChanges.getFrase());
        if (documentOriginal.size() != documentTranslation.size()) {
          documentOriginal.add(documentOriginal.size(), "");
        }
      } else {
        int cont;
        documentTranslation.add(documentTranslation.size(),
            documentTranslation.get(documentTranslation.size() - 1));
        for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
          documentTranslation.set(cont, documentTranslation.get(cont - 1));
        }
        documentTranslation.set(identLabel, ultChanges.getFrase());
      }
    }
  }

  void undoSplit() {
    // The complement of Split is Join
    String cad;
    int cont;
    cont = identLabel + 1;
    SegmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
    boolean izq = ultChanges.getSource();
    if (izq) {
      cad = ultChanges.getFrase();
      documentOriginal.set(identLabel, cad.trim());
      while (cont < topArrays) {
        documentOriginal.set(cont, documentOriginal.get(cont + 1));
        cont++;
      }
      documentOriginal.set(documentOriginal.size() - 1, "");
    } else {
      cad = ultChanges.getFrase();
      documentTranslation.set(identLabel, cad.trim());
      while (cont < topArrays) {
        documentTranslation.set(cont, documentTranslation.get(cont + 1));
        cont++;
      }
      documentTranslation.set(documentTranslation.size() - 1, "");
    }
  }

  void undoRemove() {
    SegmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
    int tam = ultChanges.getTam();
    int[] filasEliminadas;
    filasEliminadas = ultChanges.getNumEliminada();
    while (tam > 0) {
      documentTranslation.add(documentTranslation.size(), "");
      documentOriginal.add(documentOriginal.size(), "");
      topArrays = documentTranslation.size() - 1;
      tam--;
    }
    int cont2 = documentOriginal.size() - 1;
    tam = ultChanges.getTam();
    while (cont2 >= tam && tam > 0) {
      if (cont2 == filasEliminadas[tam - 1]) {
        documentTranslation.set(cont2, "");
        documentOriginal.set(cont2, "");
        tam--;
      } else {
        documentTranslation.set(cont2, documentTranslation.get(cont2 - tam));
        documentOriginal.set(cont2, documentOriginal.get(cont2 - tam));
      }
      cont2--;
    }
  }

  void undoTuSplit(boolean izq) {
    if (izq) {
      documentTranslation.set(identLabel,
          documentTranslation.get(identLabel + 1));
      documentOriginal.remove(identLabel + 1);
      documentTranslation.remove(identLabel + 1);
    } else {
      documentOriginal.set(identLabel,
          documentOriginal.get(identLabel + 1));
      documentOriginal.remove(identLabel + 1);
      documentTranslation.remove(identLabel + 1);
    }
  }
}

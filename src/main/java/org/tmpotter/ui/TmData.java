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

import java.util.ArrayList;


/**
 * TM data holder.
 *
 * @author Hiroshi Miura
 */
public class TmData {

    protected int topArrays; //  =  0;
    protected final ArrayList<SegmentChanges> arrayListChanges = new ArrayList<>();
    private int indexChanges = -1;
    protected int indexCurrent; //  =  0;
    protected int indexPrevious; //  =  0;
    protected int positionTextArea; //  =  0;
    protected Document documentOriginal;
    protected Document documentTranslation;

    /**
     * Updates the changes adding a "join" change in the "undo" array and performs the "join". (not
     * sure about the translation)
     *
     * @param textAreaIzq :TRUE if the left text (source text) has to be joined
     */
    void join(Side textAreaIzq) {
        if (indexCurrent != topArrays) {
            final SegmentChanges Changes = new SegmentChanges(SegmentChanges.OperationKind.JOIN,
                positionTextArea, textAreaIzq, "", indexCurrent);
            arrayListChanges.add(indexChanges, Changes);
            if (textAreaIzq == Side.ORIGINAL) {
                Changes.setFrase(documentOriginal.get(indexCurrent));
            } else {
                Changes.setFrase(documentTranslation.get(indexCurrent));
            }
            if (textAreaIzq == Side.ORIGINAL) {
                documentOriginal.join(indexCurrent);
            } else {
                documentTranslation.join(indexCurrent);
            }
        }
    }

    /**
     * Delete text.
     * <p>
     * <p>
     * This function updates the changes adding a delete change to the undo array and deletes
     *
     * @param textAreaIzq :TRUE if the left hand (source text) has to be deleted
     */
    void delete(Side textAreaIzq) {
        final SegmentChanges Changes = new SegmentChanges(SegmentChanges.OperationKind.DELETE,
            positionTextArea, textAreaIzq, "", indexCurrent);
        arrayListChanges.add(indexChanges, Changes);
        if (textAreaIzq == Side.ORIGINAL) {
            Changes.setFrase(documentOriginal.get(indexCurrent));
        } else {
            Changes.setFrase(documentTranslation.get(indexCurrent));
        }
        if (textAreaIzq == Side.ORIGINAL) {
            documentOriginal.delete(indexCurrent);
        } else {
            documentTranslation.delete(indexCurrent);
        }
    }

    /**
     * Function Split.
     * <p>
     * <p>
     * This function updates the changes adding a split to the undo array and performs the splitting
     *
     * @param textAreaIzq :TRUE if the left hand (source text) has to be split
     */
    void split(Side textAreaIzq) {
        if (textAreaIzq == Side.ORIGINAL) {
            if (positionTextArea >= documentOriginal.get(indexCurrent).length()) {
                positionTextArea = 0;
            }
        } else if (positionTextArea >= documentTranslation.get(indexCurrent).length()) {
            positionTextArea = 0;
        }
        final SegmentChanges Changes = new SegmentChanges(SegmentChanges.OperationKind.SPLIT,
            positionTextArea, textAreaIzq, "", indexCurrent);
        arrayListChanges.add(indexChanges, Changes);
        if (textAreaIzq == Side.ORIGINAL) {
            Changes.setFrase(documentOriginal.get(indexCurrent));
        } else {
            Changes.setFrase(documentTranslation.get(indexCurrent));
        }
        if (textAreaIzq == Side.ORIGINAL) {
            documentOriginal.split(indexCurrent, Changes.getPosition());
        } else {
            documentTranslation.split(indexCurrent, Changes.getPosition());
        }
    }

    void tuSplit(Side side) {
        int cont;
        SegmentChanges changes;

        incrementChanges();
        documentOriginal.duplicateLast();
        documentTranslation.duplicateLast();

        if (side == Side.ORIGINAL) {
            // Left column.
            changes = new SegmentChanges(SegmentChanges.OperationKind.TUSPLIT,
                0, Side.ORIGINAL, "", indexCurrent);

            for (cont = documentTranslation.size() - 1; cont > indexCurrent; cont--) {
                documentTranslation.set(cont, getDocumentTranslation(cont - 1));

                if (cont > (indexCurrent + 1)) {
                    setDocumentOriginal(cont, getDocumentOriginal(cont - 1));
                } else {
                    setDocumentOriginal(cont, "");
                }
            }

            documentTranslation.set(indexCurrent, "");
        } else {
            changes = new SegmentChanges(SegmentChanges.OperationKind.TUSPLIT,
                0, Side.TRANSLATION, "", indexCurrent);

            for (cont = documentOriginal.size() - 1; cont > indexCurrent; cont--) {
                documentOriginal.set(cont, documentOriginal.get(cont - 1));

                if (cont > (indexCurrent + 1)) {
                    setDocumentTranslation(cont, getDocumentTranslation(cont - 1));
                } else {
                    setDocumentTranslation(cont, "");
                }
            }

            setDocumentOriginal(indexCurrent, "");
        }

        arrayListChanges.add(getIdentChanges(), changes);
    }

    /**
     * Function IgualarArrays: adds rows to the smallest array and deletes blank rows.
     */
    void matchArrays() {
        int origLen = documentOriginal.size();
        int transLen = documentTranslation.size();
        if (origLen > transLen) {
            documentTranslation.padding("", origLen - transLen);
        } else if (transLen > origLen) {
            documentOriginal.padding("", transLen - origLen);
        } else {
            // same length, do nothing
        }

        while (documentOriginal.getLast().equals("") && documentTranslation.getLast().equals("")) {
            documentOriginal.removeLast();
            documentTranslation.removeLast();
        }
        topArrays = documentOriginal.size() - 1;
        if (indexCurrent > topArrays) {
            indexCurrent = topArrays;
        }
    }

    protected int getIdentChanges() {
        return indexChanges;
    }

    protected int incrementChanges() {
        return indexChanges++;
    }

    protected int decrementChanges() {
        return indexChanges--;
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
        return (indexCurrent == topArrays);
    }

    protected boolean isSomeDocumentEmpty() {
        return (documentOriginal.isEmpty() || documentTranslation.isEmpty());
    }

    protected void setBothIndex(int ident) {
        indexCurrent = ident;
        indexPrevious = indexCurrent;
    }

    protected void setIdentAntAsLabel() {
        indexPrevious = indexCurrent;
    }

    protected void setOriginalDocumentAnt(String text) {
        documentOriginal.set(indexPrevious, text);
    }

    protected void setTranslationDocumentAnt(String text) {
        documentTranslation.set(indexPrevious, text);
    }

    protected void clear() {
        documentOriginal.clean();
        documentTranslation.clean();
        int cont = arrayListChanges.size() - 1;
        while (!arrayListChanges.isEmpty()) {
            arrayListChanges.remove(cont--);
        }
        indexChanges = -1;
        indexCurrent = 0;
        indexPrevious = 0;
        topArrays = 0;
    }

    void undoJoin() {
        String cad;
        SegmentChanges ultChanges;
        ultChanges = arrayListChanges.get(getIdentChanges());
        indexCurrent = ultChanges.getIdent_linea();
        int position;
        final String cadaux = ultChanges.getFrase();

        if (ultChanges.getSource() == Side.ORIGINAL) {
            cad = getDocumentOriginal(indexCurrent);
            if (!cad.equals("")) {
                cad = cad.trim();
            }
            position = cad.indexOf(cadaux) + cadaux.length();
        } else {
            cad = getDocumentTranslation(indexCurrent);
            if (!cad.equals("")) {
                cad = cad.trim();
            }
            position = cad.indexOf(cadaux) + cadaux.length();
        }
        if (ultChanges.getSource() == Side.ORIGINAL) {
            documentOriginal.split(indexCurrent, position);
        } else {
            documentTranslation.split(indexCurrent, position);
        }
    }

    /**
     * Undoes the last delete.
     */
    void undoDelete() {
        SegmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
        indexCurrent = ultChanges.getIdent_linea();
        if (ultChanges.getSource() == Side.ORIGINAL) {
            documentOriginal.add(indexCurrent, ultChanges.getFrase());
            if (documentOriginal.size() != documentTranslation.size()) {
                documentTranslation.add("");
            }
        } else {
            documentTranslation.add(indexCurrent, ultChanges.getFrase());
            if (documentOriginal.size() != documentTranslation.size()) {
                documentOriginal.add("");
            }
        }
    }

    void undoSplit() {
        // The complement of Split is Join
        String cad;
        int cont;
        cont = indexCurrent + 1;
        SegmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
        if (ultChanges.getSource() == Side.ORIGINAL) {
            cad = ultChanges.getFrase();
            documentOriginal.set(indexCurrent, cad.trim());
            while (cont < topArrays) {
                documentOriginal.set(cont, documentOriginal.get(cont + 1));
                cont++;
            }
            documentOriginal.set(documentOriginal.size() - 1, "");
        } else {
            cad = ultChanges.getFrase();
            documentTranslation.set(indexCurrent, cad.trim());
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
        while (tam > 0) {
            documentTranslation.add(documentTranslation.size(), "");
            documentOriginal.add(documentOriginal.size(), "");
            topArrays = documentTranslation.size() - 1;
            tam--;
        }
        int cont2 = documentOriginal.size() - 1;
        tam = ultChanges.getTam();
        while (cont2 >= tam && tam > 0) {
            if (cont2 == ultChanges.getNumEliminada(tam - 1)) {
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

    void undoTuSplit(Side izq) {
        if (izq == Side.ORIGINAL) {
            documentTranslation.set(indexCurrent,
                documentTranslation.get(indexCurrent + 1));
            documentOriginal.remove(indexCurrent + 1);
            documentTranslation.remove(indexCurrent + 1);
        } else {
            documentOriginal.set(indexCurrent,
                documentOriginal.get(indexCurrent + 1));
            documentOriginal.remove(indexCurrent + 1);
            documentTranslation.remove(indexCurrent + 1);
        }
    }

    public enum Side {
        ORIGINAL, TRANSLATION
    }
}

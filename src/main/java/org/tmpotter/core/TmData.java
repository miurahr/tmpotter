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

package org.tmpotter.core;

import java.util.ArrayList;


/**
 * TM data holder.
 *
 * @author Hiroshi Miura
 */
public class TmData {

    private int topArrays; //  =  0;
    private final ArrayList<AlignmentChanges> arrayListChanges = new ArrayList<>();
    private int indexChanges = -1;
    private int indexCurrent; //  =  0;
    private int indexPrevious; //  =  0;
    private int positionTextArea; //  =  0;
    private Document documentOriginal;
    private Document documentTranslation;

    public void newDocuments(){
        this.documentTranslation = new Document();
        this.documentOriginal = new Document();
    }

    /**
     * Updates the changes adding a "join" change in the "undo" array and performs the "join". (not
     * sure about the translation)
     *
     * @param textAreaIzq :TRUE if the left text (source text) has to be joined
     */
    public void join(Side textAreaIzq) {
        if (indexCurrent != topArrays) {
            final AlignmentChanges Changes = new AlignmentChanges(
                    AlignmentChanges.OperationKind.JOIN,
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
    public void delete(Side textAreaIzq) {
        final AlignmentChanges Changes = new AlignmentChanges(AlignmentChanges.OperationKind.DELETE,
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
    public void split(Side textAreaIzq) {
        if (textAreaIzq == Side.ORIGINAL) {
            if (positionTextArea >= documentOriginal.get(indexCurrent).length()) {
                positionTextArea = 0;
            }
        } else if (positionTextArea >= documentTranslation.get(indexCurrent).length()) {
            positionTextArea = 0;
        }
        final AlignmentChanges Changes = new AlignmentChanges(AlignmentChanges.OperationKind.SPLIT,
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

    /**
     * Split Translation-Unit for specified side.
     * @param side side to be split.
     */
    public void tuSplit(Side side) {
        int cont;
        AlignmentChanges changes;

        incrementChanges();
        documentOriginal.duplicateLast();
        documentTranslation.duplicateLast();

        if (side == Side.ORIGINAL) {
            // Left column.
            changes = new AlignmentChanges(AlignmentChanges.OperationKind.TUSPLIT,
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
            changes = new AlignmentChanges(AlignmentChanges.OperationKind.TUSPLIT,
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
    public void matchArrays() {
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

    public int getIdentChanges() {
        return indexChanges;
    }

    public int incrementChanges() {
        return indexChanges++;
    }

    public int decrementChanges() {
        return indexChanges--;
    }

    public Document getDocumentTranslation() {
        return documentTranslation;
    }

    public String getDocumentTranslation(int cont) {
        return documentTranslation.get(cont);
    }

    public void removeDocumentTranslation(int cont) {
        documentTranslation.remove(cont);
    }

    public void setDocumentTranslation(int cont, String text) {
        documentTranslation.set(cont, text);
    }

    public void loadDocument(Document documentOriginal, Document documentTranslation) {
        this.documentOriginal = documentOriginal;
        this.documentTranslation = documentTranslation;
    }

    public boolean isDocumentTranslationEmpty(int cont) {
        return (documentTranslation.get(cont) == null || documentTranslation.get(cont).equals(""));
    }

    public int getDocumentTranslationSize() {
        return documentTranslation.size();
    }

    public String getDocumentOriginal(int cont) {
        return documentOriginal.get(cont);
    }

    public void removeDocumentOriginal(int cont) {
        documentOriginal.remove(cont);
    }

    public void setDocumentOriginal(int cont, String text) {
        documentOriginal.set(cont, text);
    }

    public int getDocumentOriginalSize() {
        return documentOriginal.size();
    }

    public Document getDocumentOriginal() {
        return documentOriginal;
    }

    public boolean isDocumentOriginalEmpty(int cont) {
        return (documentOriginal.get(cont) == null || documentOriginal.get(cont).equals(""));
    }

    public boolean isIdentTop() {
        return (indexCurrent == topArrays);
    }

    public boolean isSomeDocumentEmpty() {
        return (documentOriginal.isEmpty() || documentTranslation.isEmpty());
    }

    public void setBothIndex(int ident) {
        indexCurrent = ident;
        indexPrevious = indexCurrent;
    }

    public void setIndexCurrent(int indexCurrent) {
        this.indexCurrent = indexCurrent;
    }

    public void setIndexPrevious(int indexPrevious) {
        this.indexPrevious = indexPrevious;
    }

    public int getIndexCurrent() {
        return indexCurrent;
    }

    public int getIndexPrevious() {
        return indexPrevious;
    }

    public void setIdentAntAsLabel() {
        indexPrevious = indexCurrent;
    }

    public void setOriginalDocumentAnt(String text) {
        documentOriginal.set(indexPrevious, text);
    }

    public void setTranslationDocumentAnt(String text) {
        documentTranslation.set(indexPrevious, text);
    }

    public int getTopArrays() {
        return topArrays;
    }

    public void setTopArrays(int topArrays) {
        this.topArrays = topArrays;
    }

    public void removeArrayListChanges(int cont) {
        arrayListChanges.remove(cont);
    }

    public AlignmentChanges getArrayListChanges(int cont) {
        return arrayListChanges.get(cont);
    }

    public void addArrayListChanges(int cont, AlignmentChanges changes) {
        arrayListChanges.add(cont, changes);
    }

    public int getPositionTextArea() {
        return positionTextArea;
    }

    public void setPositionTextArea(int positionTextArea) {
        this.positionTextArea = positionTextArea;
    }


    public void clear() {
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

    public void undoJoin() {
        String cad;
        AlignmentChanges ultChanges;
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
    public void undoDelete() {
        AlignmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
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

    public void undoSplit() {
        // The complement of Split is Join
        String cad;
        int cont;
        cont = indexCurrent + 1;
        AlignmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
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

    public void undoRemove() {
        AlignmentChanges ultChanges = arrayListChanges.get(getIdentChanges());
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

    public void undoTuSplit(Side izq) {
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

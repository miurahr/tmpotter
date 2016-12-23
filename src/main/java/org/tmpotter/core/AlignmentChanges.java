/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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
 * *************************************************************************/

package org.tmpotter.core;


/**
 * Action when alignment changing.
 *
 * @author Hiroshi Miura
 */
public class AlignmentChanges {
    OperationKind kind;
    int pos;
    TmData.Side source;
    String phrase;
    int indexLine;
    int[] numEliminada;


    /**
     * Constructor.
     */
    public AlignmentChanges() {
        kind = OperationKind.JOIN;
        pos = 0;
        source = TmData.Side.TRANSLATION;
        phrase = "";
        indexLine = -1;
    }

    /**
     * Constructor.
     *
     * @param kind     tipo
     * @param position pos
     * @param source   fuente
     * @param phrase   frase
     * @param index    ident line
     */
    public AlignmentChanges(OperationKind kind, int position, TmData.Side source,
                            String phrase, int index) {
        this.kind = kind;
        this.pos = position;
        this.source = source;
        this.phrase = phrase;
        this.indexLine = index;
    }

    /**
     * Accessor for kind.
     *
     * @return kind
     */
    public OperationKind getKind() {
        return (kind);
    }

    /**
     * Accessor for position.
     *
     * @return position
     */
    public int getPosition() {
        return (pos);
    }

    /**
     * Accessor for frase.
     *
     * @return frase
     */
    public String getFrase() {
        return (phrase);
    }

    /**
     * Accessor for ident line.
     *
     * @return ident
     */
    public int getIdent_linea() {
        return (indexLine);
    }

    /**
     * Accessor for source/translation indicator.
     *
     * @return source
     */
    public TmData.Side getSource() {
        return (source);
    }

    /**
     * Accessor for num eliminada.
     *
     * @param index index to get line number.
     * @return numEliminada
     */
    public int getNumEliminada(final int index) {
        assert (index >= 0);
        assert (index < numEliminada.length);
        return (numEliminada[index]);
    }

    /**
     * Accessor for TAM.
     *
     * @return length of numEliminada
     */
    public int getTam() {
        int tam = numEliminada.length;

        return (tam);
    }

    /**
     * Setter for OperationKind.
     *
     * @param kind to set
     */
    public void setKind(OperationKind kind) {
        this.kind = kind;
    }

    /**
     * Setter for position.
     *
     * @param kpos to set
     */
    public void setPos(int kpos) {
        pos = kpos;
    }

    /**
     * Setter for fuente.
     *
     * @param source to set boolean
     */
    public void setSource(TmData.Side source) {
        this.source = source;
    }

    /**
     * Setter for frase.
     *
     * @param kfrase string to set
     */
    public void setFrase(String kfrase) {
        phrase = kfrase;
    }

    /**
     * Setter for line ident.
     *
     * @param index to set
     */
    public void setIdent_linea(int index) {
        indexLine = index;
    }

    /**
     * Setter for num of Eliminada.
     *
     * @param keliminadas eliminada
     * @param tam         number of array
     */
    public void setNumEliminada(int[] keliminadas, int tam) {
        numEliminada = new int[tam];
        System.arraycopy(keliminadas, 0, numEliminada, 0, tam);
    }

    public enum OperationKind {
        JOIN, SPLIT, DELETE, REMOVE, TUSPLIT
    }
}
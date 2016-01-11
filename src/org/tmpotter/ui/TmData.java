/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
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

import java.io.File;
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
  protected int identChanges = -1;
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

}

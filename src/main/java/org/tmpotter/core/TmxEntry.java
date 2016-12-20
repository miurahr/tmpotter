/* ************************************************************************
 *
 * TMPotter - Bi-text Aligner/TMX Editor
 *
 * Copyright (C) 2015 Hiroshi Miura
 *
 * This file is imported from OmegaT.
 * 
 * Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *               2012 Guido Leenders, Didier Briel
 *               2013 Aaron Madlon-Kay, Yu Tang
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

import java.util.Map;


/**
 * Class for prepare TMXEntry content before save unchangeable copy in the
 * ProjectTMX. We can't use just parameters in the setTranslation() method since
 * count of parameters is too much. Structure of this class is almost the save
 * like TMXEntry.
 * <p>
 * Instead, we will set all parameters into this class, then ProjectTMX will
 * convert in into TMXEntry than save internally.
 *
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Guido Leenders
 * @author Aaron Madlon-Kay
 */
public class TmxEntry {

    public String source;
    public String translation;
    public String changer;
    public long changeDate;
    public String creator;
    public long creationDate;
    public String note;
    public Map<String, String> otherProperties;

    public TmxEntry() {
    }

    /**
     * TMX entry class.
     *
     * @param ent Entry for base
     */
    public TmxEntry(TmxEntry ent) {
        source = ent.source;
        translation = ent.translation;
        changer = ent.changer;
        changeDate = ent.changeDate;
        creator = ent.creator;
        creationDate = ent.creationDate;
        note = ent.note;
        otherProperties = ent.otherProperties;
    }

    /**
     * get properties value.
     *
     * @param propType propertiy type
     * @return value
     */
    public String getPropValue(String propType) {
        if (otherProperties == null) {
            return null;
        }
        for (Map.Entry<String, String> kv: otherProperties.entrySet()) {
            if (propType.equals(kv.getKey())) {
                return kv.getValue();
            }
        }
        return null;
    }

    /**
     * Ask property type has value.
     *
     * @param propType  property type to ask
     * @param propValue property value to be equal
     * @return true  property of the type has the value
     */
    public boolean hasPropValue(String propType, String propValue) {
        if (otherProperties == null) {
            return false;
        }
        for (Map.Entry<String, String> kv: otherProperties.entrySet()) {
            if (propType.equals(kv.getKey())) {
                if (propValue == null) {
                    return true;
                }
                if (propValue.equals(kv.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
}

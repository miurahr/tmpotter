/* *************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  Part of this come from OmegaT.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
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
 * *************************************************************************/

package bitext2tmx.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of Tag's attritutes.
 * 
 * @author Maxym Mykhalchuk
 */
public class Attributes {
  List<Attribute> list = new ArrayList<Attribute>();

  /**
   * Number of attributes.
   */
  public int size() {
    return list.size();
  }

  /**
   * Adds an attribute to the list.
   */
  public void add(Attribute attr) {
    list.add(attr);
  }

  /**
   * Gets one of the attributes from the list.
   */
  public Attribute get(int index) {
    return list.get(index);
  }

  /**
   * Find attribute value by name.
   *
   * @param attrName attribute name, case insensitive
   * @return attribute value, or null if not found
   */
  public String getValueByName(String attrName) {
    for (int i = 0; i < list.size(); i++) {
      Attribute oneAttribute = list.get(i);
      if (attrName.equalsIgnoreCase(oneAttribute.getName())) {
        return oneAttribute.getValue();
      }
    }
    return null;
  }

  /**
   * Returns a string representation of the list of attributes. ' name1="value1"
   * name2="value2" ...'
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (Attribute attr : list) {
      buf.append(' ');
      buf.append(attr.toString());
    }
    return buf.toString();
  }

}

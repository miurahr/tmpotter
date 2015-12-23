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

package bitext2tmx.ui;

import bitext2tmx.util.ResourceUtil;

import java.awt.Image;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Icon resource accessor.
 * 
 * @author Hiroshi Miura
 */
public class Icons {
  
  public static final Map<String, Image> ICONS = new HashMap<String, Image>();
  static final String RESOURCES = "/bitext2tmx/ui/resources/";

  static {
    try {
      ICONS.put("icon-small.png", ResourceUtil.getImage(RESOURCES + "icon-small.png"));
      ICONS.put("icon-medium.png", ResourceUtil.getImage(RESOURCES + "icon-medium.png"));
      ICONS.put("icon-large.png", ResourceUtil.getImage(RESOURCES + "icon-large.png"));
      ICONS.put("splash.png", ResourceUtil.getImage(RESOURCES + "splash.png"));
      ICONS.put("icon-broken.png", ResourceUtil.getImage(RESOURCES + "icon-broken.png"));
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Accessor for Icon image.
   * 
   * @param iconName to retrieve
   * @return icon image
   */
  public static final ImageIcon getIcon( final String iconName ) {
    if (!ICONS.containsKey(iconName)) {
      Image image;
      try {
        image = ResourceUtil.getImage(RESOURCES + iconName );
      } catch (FileNotFoundException ex) {
        image = ICONS.get("icon-broken.png");
      }
      return new ImageIcon(image);
    }
    return new ImageIcon(ICONS.get(iconName));
  }
}

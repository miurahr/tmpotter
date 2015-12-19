/*
 * Copyright (C) 2015 Hiroshi Miura
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bitext2tmx.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 *
 * @author Hiroshi Miura
 */
public class ResourceUtil {
  /**
   * Load icon.
   * 
   * @param resourceName
   *            resource name
   * @return Image
   *            An image retrieved by resource name
   * @throws FileNotFoundException 
   */
    public static Image getImage(final String resourceName) throws FileNotFoundException {
        URL resourceURL = ResourceUtil.class.getResource(resourceName);
        if (resourceURL == null) {
            throw new FileNotFoundException(resourceName);
        }
        return Toolkit.getDefaultToolkit().getImage(resourceURL);
    }
}

/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
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
package org.tmpotter.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow {

    /**
     * show splash screen.
     */
    public SplashScreen() {
        ImageIcon image = null;
        JLabel label;

        try {
            label = new JLabel(Icons.getIcon("splash.png"));

        } catch (Exception ex) {
            label = new JLabel("Error: unable to load image!");
        }

        getContentPane().add(label);
        pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int locationX = (int) (dim.getWidth() - getWidth()) / 2;
        int localtionY = (int) (dim.getHeight() - getHeight()) / 2;
        setLocation(locationX, localtionY);
    }

    public void display() {
        pack();
        setVisible(true);
    }

    public void remove() {
        setVisible(false);
        dispose();
    }
}

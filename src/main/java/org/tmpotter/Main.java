/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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
 * *************************************************************************/

package org.tmpotter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.ui.Icons;
import org.tmpotter.ui.MainWindow;
import org.tmpotter.ui.SplashScreen;
import org.tmpotter.util.Platform;
import org.tmpotter.util.gui.AquaAdapter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * Main class.
 */
public class Main {
    /**
     * Main constructor.
     */
    public Main() {
        setLnF();
        displaySplash();

        final MainWindow windowMain = new MainWindow();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                windowMain.setVisible(true);
            }
        });
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * main method.
     *
     * @param straArgs command line argument
     */
    public static void main(String[] straArgs) {
        new Main();
    }

    /**
     * Set the Swing Look and Feel.
     */
    private void setLnF() {
        try {
            if (Platform.isMacOsx()) {
                System.setProperty("apple.awt.graphics.UseQuartz", "true");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "tmpotter");
                //  ToDo: create (OS X) dock icon
                AquaAdapter.setDockIconImage(Icons.getIcon("icon-large.png").getImage());
            }
            // Workaround for JDK bug 6389282
            // it should be called before setLookAndFeel() for GTK LookandFeel
            UIManager.getInstalledLookAndFeels();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("swing.aatext", "true");

        } catch (final ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | UnsupportedLookAndFeelException cnfe) {
            //LOGGER.INFO(Main", "setLnF", "MW.ERROR.LOOK_AND_FEEL_EXCEPTION", "", cnfe);
        }
    }

    private void displaySplash() {
        new Thread() {
            @Override
            public void run() {
                final SplashScreen splash = new SplashScreen();
                splash.display();

                try {
                    sleep(5000);
                } catch (InterruptedException ie) {
                    LOGGER.info("Splash try to be Interrupted.");
                }

                splash.remove();
            }
        }.start();
    }

}
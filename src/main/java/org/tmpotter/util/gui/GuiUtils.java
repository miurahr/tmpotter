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
 * *************************************************************************/

package org.tmpotter.util.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Swing GUI utility.
 * <p>
 *     It installs Property change listener for text field.
 *     see http://stackoverflow.com/a/27190162
 * </p>
 * @author Hiroshi Miura
 */
public class GuiUtils {
    /**
     * Installs a listener to receive notification when the text of any
     * {@code JTextComponent} is changed. Internally, it installs a
     * {@link DocumentListener} on the text component's {@link Document},
     * and a {@link PropertyChangeListener} on the text component to detect
     * if the {@code Document} itself is replaced.
     *
     * @param text           any text component, such as a {@link JTextField}
     *                       or {@link JTextArea}
     * @param changeListener a listener to receieve {@link ChangeEvent}s
     *                       when the text is changed; the source object for the events
     *                       will be the text component
     * @throws NullPointerException if either parameter is null
     */
    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {

            private int lastChange = 0;
            private int lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent evt) {
                changedUpdate(evt);
            }

            @Override
            public void removeUpdate(DocumentEvent evt) {
                changedUpdate(evt);
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };

        text.addPropertyChangeListener("document", (PropertyChangeEvent evt) -> {
            Document d1 = (Document) evt.getOldValue();
            Document d2 = (Document) evt.getNewValue();
            if (d1 != null) {
                d1.removeDocumentListener(dl);
            }
            if (d2 != null) {
                d2.addDocumentListener(dl);
            }
            dl.changedUpdate(null);
        });
        Document doc = text.getDocument();
        if (doc != null) {
            doc.addDocumentListener(dl);
        }
    }
}

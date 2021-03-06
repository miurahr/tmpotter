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

package org.tmpotter.ui;

import static org.openide.awt.Mnemonics.setLocalizedText;
import static org.tmpotter.util.Localization.getString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;


/**
 * Align Toolbar.
 *
 * @author Hiroshi Miura
 */
public class AlignToolBar extends javax.swing.JPanel implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlignToolBar.class);
    private ActionHandler actionHandler;

    /**
     * Creates new form AlignToolBar.
     */
    public AlignToolBar(ActionHandler handler) {
        initComponents();
        this.actionHandler = handler;
        toolBar.setFloatable(false);
        setActionCommands();
    }

    /**
     * Set 'actionCommand' for all menu items.
     */
    protected void setActionCommands() {
        try {
            for (Field f : this.getClass().getDeclaredFields()) {
                if (JButton.class.isAssignableFrom(f.getType())) {
                    JButton button = (JButton) f.get(this);
                    button.setActionCommand(f.getName());
                    button.addActionListener(this);
                }
            }
        } catch (IllegalAccessException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Action handler for toobar.
     * @param evt event emitted.
     */
    public void actionPerformed(ActionEvent evt) {
        // Get item name from actionCommand.
        String action = evt.getActionCommand();

        // Find method by item name.
        String methodName = action + "ActionPerformed";
        Method method = null;
        try {
            method = actionHandler.getClass().getMethod(methodName);
        } catch (NoSuchMethodException ignore) {
            try {
                method = actionHandler.getClass()
                    .getMethod(methodName, Integer.TYPE);
            } catch (NoSuchMethodException ex) {
                throw new IncompatibleClassChangeError(
                    "Error invoke method handler for main menu: there is no method "
                        + methodName);
            }
        }
        // Call ...MenuItemActionPerformed method.
        Object[] args = method.getParameterTypes().length == 0 ? null : new Object[]{evt
                .getModifiers()};
        try {
            method.invoke(actionHandler, args);
        } catch (IllegalAccessException ex) {
            throw new IncompatibleClassChangeError(
                "Error invoke method handler for main menu");
        } catch (InvocationTargetException ex) {
            LOGGER.info("Error execute method", ex);
            throw new IncompatibleClassChangeError(
                "Error invoke method handler for main menu");
        }
    }

    final void setFonts(final Font font) {
        buttonRemoveBlankRows.setFont(font);
        buttonTUSplit.setFont(font);
        buttonOriginalJoin.setFont(font);
        buttonOriginalDelete.setFont(font);
        buttonOriginalSplit.setFont(font);
        buttonTranslationJoin.setFont(font);
        buttonTranslationDelete.setFont(font);
        buttonTranslationSplit.setFont(font);
    }

    final void enableButtons(boolean enabled) {
        buttonRemoveBlankRows.setEnabled(enabled);
        buttonTUSplit.setEnabled(enabled);
        buttonOriginalJoin.setEnabled(enabled);
        buttonOriginalDelete.setEnabled(enabled);
        buttonOriginalSplit.setEnabled(enabled);
        buttonTranslationJoin.setEnabled(enabled);
        buttonTranslationDelete.setEnabled(enabled);
        buttonTranslationSplit.setEnabled(enabled);
    }

    public final void setOriginalJoinEnabled(boolean enabled) {
        buttonOriginalJoin.setEnabled(enabled);
    }

    public final void setTranslationJoinEnabled(boolean enabled) {
        buttonTranslationJoin.setEnabled(enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        panelOriginal = new javax.swing.JPanel();
        buttonOriginalDelete = new javax.swing.JButton();
        buttonOriginalJoin = new javax.swing.JButton();
        buttonOriginalSplit = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0,
                0), new java.awt.Dimension(32767, 0));
        panelTranslateUnit = new javax.swing.JPanel();
        buttonRemoveBlankRows = new javax.swing.JButton();
        buttonTUSplit = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0,
                0), new java.awt.Dimension(32767, 0));
        panelTranslation = new javax.swing.JPanel();
        buttonTranslationDelete = new javax.swing.JButton();
        buttonTranslationJoin = new javax.swing.JButton();
        buttonTranslationSplit = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(32767, 92));
        setMinimumSize(new java.awt.Dimension(750, 64));
        setPreferredSize(new java.awt.Dimension(750, 64));

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setMaximumSize(new java.awt.Dimension(1000, 94));
        toolBar.setMinimumSize(new java.awt.Dimension(600, 48));
        toolBar.setPreferredSize(new java.awt.Dimension(600, 48));

        panelOriginal.setBorder(javax.swing.BorderFactory.createTitledBorder("Original"));
        panelOriginal.setMaximumSize(new java.awt.Dimension(500, 90));
        panelOriginal.setMinimumSize(new java.awt.Dimension(250, 60));
        panelOriginal.setPreferredSize(new java.awt.Dimension(250, 60));

        buttonOriginalDelete.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/tmpotter/ui/resources/eraser.png"))); // NOI18N
        buttonOriginalDelete.setText("Delete");
        setLocalizedText(buttonOriginalDelete, getString("BTN.DELETE.ORIGINAL"));

        buttonOriginalJoin.setText("Join");
        setLocalizedText(buttonOriginalJoin, getString("BTN.JOIN.ORIGINAL"));

        buttonOriginalSplit.setText("Split");
        setLocalizedText(buttonOriginalSplit, getString("BTN.SPLIT.ORIGINAL"));
        javax.swing.GroupLayout panelOriginalLayout = new javax.swing.GroupLayout(panelOriginal);
        panelOriginal.setLayout(panelOriginalLayout);
        panelOriginalLayout.setHorizontalGroup(
                panelOriginalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOriginalLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(buttonOriginalDelete)
                    .addGap(12, 12, 12)
                    .addComponent(buttonOriginalJoin)
                    .addGap(12, 12, 12)
                    .addComponent(buttonOriginalSplit)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelOriginalLayout.setVerticalGroup(
                panelOriginalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOriginalLayout.createSequentialGroup()
                    .addGroup(panelOriginalLayout.createParallelGroup(javax.swing.GroupLayout
                            .Alignment.CENTER)
                        .addComponent(buttonOriginalDelete)
                        .addComponent(buttonOriginalJoin)
                        .addComponent(buttonOriginalSplit))
                    .addGap(0, 8, Short.MAX_VALUE))
        );

        toolBar.add(panelOriginal);
        toolBar.add(filler2);

        panelTranslateUnit.setBorder(javax.swing.BorderFactory.createTitledBorder("TU"));
        panelTranslateUnit.setMaximumSize(new java.awt.Dimension(500, 90));
        panelTranslateUnit.setMinimumSize(new java.awt.Dimension(250, 60));
        panelTranslateUnit.setPreferredSize(new java.awt.Dimension(250, 60));

        buttonRemoveBlankRows.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/tmpotter/ui/resources/eraser.png"))); // NOI18N
        buttonRemoveBlankRows.setText("Delete Blank Rows");
        setLocalizedText(buttonRemoveBlankRows, getString("BTN.DELETE.BLANK.ROWS"));

        buttonTUSplit.setText("Split");
        setLocalizedText(buttonTUSplit, getString("BTN.SPLIT.TU"));

        javax.swing.GroupLayout panelTranslateUnitLayout = new javax.swing
                .GroupLayout(panelTranslateUnit);
        panelTranslateUnit.setLayout(panelTranslateUnitLayout);
        panelTranslateUnitLayout.setHorizontalGroup(
                panelTranslateUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment
                        .LEADING)
                .addGroup(panelTranslateUnitLayout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(buttonRemoveBlankRows)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonTUSplit)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTranslateUnitLayout.setVerticalGroup(
                panelTranslateUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment
                        .LEADING)
                .addGroup(panelTranslateUnitLayout.createSequentialGroup()
                    .addGroup(panelTranslateUnitLayout.createParallelGroup(
                            javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(buttonRemoveBlankRows)
                        .addComponent(buttonTUSplit))
                    .addGap(0, 8, Short.MAX_VALUE))
        );

        toolBar.add(panelTranslateUnit);
        panelTranslateUnit.getAccessibleContext().setAccessibleName("Translation");

        toolBar.add(filler1);

        panelTranslation.setBorder(javax.swing.BorderFactory.createTitledBorder("Translation"));
        panelTranslation.setMaximumSize(new java.awt.Dimension(500, 90));
        panelTranslation.setMinimumSize(new java.awt.Dimension(250, 60));
        panelTranslation.setPreferredSize(new java.awt.Dimension(250, 60));

        buttonTranslationDelete.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/org/tmpotter/ui/resources/eraser.png"))); // NOI18N
        buttonTranslationDelete.setText("Delete");
        setLocalizedText(buttonTranslationDelete, getString("BTN.DELETE.TRANSLATION"));

        buttonTranslationJoin.setText("Join");
        setLocalizedText(buttonTranslationJoin, getString("BTN.JOIN.TRANSLATION"));

        buttonTranslationSplit.setText("Split");
        setLocalizedText(buttonTranslationSplit, getString("BTN.SPLIT.TRANSLATION"));

        javax.swing.GroupLayout panelTranslationLayout = new javax.swing
                .GroupLayout(panelTranslation);
        panelTranslation.setLayout(panelTranslationLayout);
        panelTranslationLayout.setHorizontalGroup(
                panelTranslationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment
                        .LEADING)
                .addGroup(panelTranslationLayout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(buttonTranslationDelete)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonTranslationJoin)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonTranslationSplit)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTranslationLayout.setVerticalGroup(
                panelTranslationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment
                        .LEADING)
                .addGroup(panelTranslationLayout.createSequentialGroup()
                    .addGroup(panelTranslationLayout.createParallelGroup(javax.swing.GroupLayout
                            .Alignment.CENTER)
                        .addComponent(buttonTranslationSplit)
                        .addComponent(buttonTranslationJoin)
                        .addComponent(buttonTranslationDelete))
                    .addGap(0, 8, Short.MAX_VALUE))
        );

        toolBar.add(panelTranslation);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 750,
                            Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 64,
                             Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonOriginalDelete;
    private javax.swing.JButton buttonOriginalJoin;
    private javax.swing.JButton buttonOriginalSplit;
    private javax.swing.JButton buttonRemoveBlankRows;
    private javax.swing.JButton buttonTUSplit;
    private javax.swing.JButton buttonTranslationDelete;
    private javax.swing.JButton buttonTranslationJoin;
    private javax.swing.JButton buttonTranslationSplit;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel panelOriginal;
    private javax.swing.JPanel panelTranslateUnit;
    private javax.swing.JPanel panelTranslation;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}

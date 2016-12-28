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

package org.tmpotter.ui.wizard;

import org.tmpotter.util.PluginUtils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/**
 * @author Hiroshi Miura
 */
public class ImportWizardSelectTypePanel extends JPanel implements IImportWizardPanel {
    public static final String id = "selecttype";
    private ImportWizardController wizardController;
    private List<JRadioButton> buttons = new ArrayList<>();
    private List<JLabel> labels = new ArrayList<>();

    /**
     * Creates new form ImportWizardSelectTypePanel
     */
    public ImportWizardSelectTypePanel() {
        initComponents();
        setOptions();
    }

    // dummy methods.
    public void init(final ImportWizardController controller, ImportPreference pref) {}

    public void onShow() {}

    public String getId() {
        return id;
    }

    public boolean isCombinedFormat() {
        return true;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public String getNextFinishCommand() {
        return getSelection();
    }

    @Override
    public String getBackCommand() {
        return null;
    }

    public String getSelection() {
        if (buttonGroup.getSelection() != null) {
            return buttonGroup.getSelection().getActionCommand();
        }
        return "";
    }

    @Override
    public void updatePref() {
    }

    private void setOptions() {
        // count panels.
        int combined = 0;
        int bitext = 0;
        List<IImportWizardPanel> panels = PluginUtils.getWizards();
        for (IImportWizardPanel panel : panels) {
            if (panel.isCombinedFormat()) {
                combined++;
            } else {
                bitext++;
            }
        }
        // set place holder size.
        panelBiText.setLayout(new java.awt.GridLayout(2, bitext));
        panelSingle.setLayout(new java.awt.GridLayout(2, combined));
        // create radioButtons and labels.
        for (IImportWizardPanel panel : panels) {
            JLabel label = new JLabel();
            label.setText(panel.getDesc());
            JRadioButton button = new JRadioButton();
            button.setText(panel.getName());
            button.setActionCommand(panel.getId());
            if (panel.isCombinedFormat()) {
                panelSingle.add(button);
                panelSingle.add(label);
            } else {
                panelBiText.add(button);
                panelBiText.add(label);
            }
            buttonGroup.add(button);
            buttons.add(button);
            labels.add(label);
        }
        add(panelBiText);
        add(panelSingle);
    }

    private void initComponents() {
        buttonGroup = new javax.swing.ButtonGroup();
        panelBiText = new javax.swing.JPanel();
        panelSingle = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelBiText.setBorder(javax.swing.BorderFactory.createTitledBorder("Bi-Text imports"));
        panelBiText.setLayout(new java.awt.GridLayout(1, 0));

        panelSingle.setBorder(javax.swing.BorderFactory.createTitledBorder("Single file import"));
        panelSingle.setLayout(new java.awt.GridLayout(2, 2));
    }

    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel panelBiText;
    private javax.swing.JPanel panelSingle;

}

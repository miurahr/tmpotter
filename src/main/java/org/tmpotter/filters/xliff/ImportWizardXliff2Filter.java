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

package org.tmpotter.filters.xliff;

import static org.tmpotter.util.Localization.getString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.ui.wizard.IImportWizardPanel;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.ui.wizard.ImportWizardController;
import org.tmpotter.ui.wizard.ImportWizardSelectTypePanel;
import org.tmpotter.util.Localization;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * Import wizard panel for XLIFF2 filter.
 * @author Hiroshi Miura
 */
public class ImportWizardXliff2Filter extends javax.swing.JPanel implements IImportWizardPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportWizardXliff2Filter.class
            .getName());
    public static final String id = "Xliff2Filter";
    static final String XLIFF_VERSION_CONFIG = "Version";
    static final String XLIFF_VERSION_CONFIG_VERSION2 = "2";
    static final String XLIFF_VERSION_CONFIG_VERSION1 = "1";
    private ImportWizardController wizardController;
    private ImportPreference pref;
    private File originalFile;
    private final String[] idiom = Localization.getLanguageList();
  
    /**
     * Creates new form ImportWizardXliff2Filter.
     */
    public ImportWizardXliff2Filter() {
    }

    public void init(final ImportWizardController controller, ImportPreference pref) {
        wizardController = controller;
        this.pref = pref;
        initComponents();
        jRadioButton1.setActionCommand(XLIFF_VERSION_CONFIG_VERSION1);
        jRadioButton2.setActionCommand(XLIFF_VERSION_CONFIG_VERSION2);
    }

    public void onShow() {
    }

    public String getId() {
        return id;
    }

    public boolean isCombinedFormat() {
        return false;
    }

    public javax.swing.JPanel getPanel() {
        return this;
    }

    public String getName() {
        return "XLIFF";
    }

    public String getDesc() {
        return "XLIFF import.";
    }

    public String getBackCommand() {
        return ImportWizardSelectTypePanel.id;
    }

    public String getNextFinishCommand() {
        return "finish";
    }

    public String getSelection() {
        if (buttonGroup.getSelection() != null) {
            return buttonGroup.getSelection().getActionCommand();
        }
        return XLIFF_VERSION_CONFIG_VERSION1; // Default version 1.1,1.4
    }
    
    public final String getSourceLocale() {
        return Localization.getLanguageCode(comboSourceLang.getSelectedIndex());
    }

    public final String getTargetLocale() {
        return Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
    }

    public void updatePref() {
        pref.setFilter("Xliff2Filter");
        pref.setSourceEncoding("UTF-8");
        pref.setTranslationEncoding("UTF-8");
        pref.setOriginalFilePath(originalFile);
        pref.setTranslationFilePath(originalFile);
        pref.setOriginalLang(getSourceLocale());
        pref.setTranslationLang(getTargetLocale());
        pref.setConfigValue(XLIFF_VERSION_CONFIG, getSelection());
    }

    public void onImportFile(final IImportWizardPanel panel) {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "XLIFF File", "xlf", "xliff");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        final int returnVal = fc.showOpenDialog(this);
        pref.setCurrentPath(fc.getCurrentDirectory());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File filePath = fc.getSelectedFile();
            if ((fc.getName(filePath).endsWith(".xlf") || fc.getName(filePath).endsWith(".xliff"))
                    && filePath.exists()) {
                originalFile = filePath;
                jTextField1.setText(filePath.getPath());
                wizardController.setButtonNextEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    getString("MSG.ERROR.FILE_NOTFOUND"),
                    getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                buttonGroup = new javax.swing.ButtonGroup();
                jLabel1 = new javax.swing.JLabel();
                jTextField1 = new javax.swing.JTextField();
                jButton1 = new javax.swing.JButton();
                jRadioButton1 = new javax.swing.JRadioButton();
                jRadioButton2 = new javax.swing.JRadioButton();
                jLabel2 = new javax.swing.JLabel();
                comboSourceLang = new javax.swing.JComboBox<>();
                comboTranslationLang = new javax.swing.JComboBox<>();
                jLabel3 = new javax.swing.JLabel();
                jLabel4 = new javax.swing.JLabel();

                jLabel1.setText("XLIFF file import...");

                jTextField1.setText("Select file...");

                jButton1.setText("Select");
                jButton1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton1ActionPerformed(evt);
                        }
                });

                buttonGroup.add(jRadioButton1);
                jRadioButton1.setSelected(true);
                jRadioButton1.setText("XLIFF Version2");

                buttonGroup.add(jRadioButton2);
                jRadioButton2.setText("XLIFF Version1.1,1.4");

                jLabel2.setText("XLIFF version 1.x need to specify locales");

                comboSourceLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));

                comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));

                jLabel3.setText("Source language");

                jLabel4.setText("Translation language");

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jRadioButton2)
                                        .addComponent(jRadioButton1)
                                        .addComponent(jLabel1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                        .addGap(29, 29, 29)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(jLabel2)
                                                                .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                        .addGap(69, 69, 69)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel4)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(jLabel3)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addContainerGap(24, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jLabel1)
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1))
                                .addGap(26, 26, 26)
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                .addContainerGap(23, Short.MAX_VALUE))
                );
        }// </editor-fold>//GEN-END:initComponents

        private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        onImportFile(this);
        }//GEN-LAST:event_jButton1ActionPerformed


        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.ButtonGroup buttonGroup;
        private javax.swing.JComboBox<String> comboSourceLang;
        private javax.swing.JComboBox<String> comboTranslationLang;
        private javax.swing.JButton jButton1;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JRadioButton jRadioButton1;
        private javax.swing.JRadioButton jRadioButton2;
        private javax.swing.JTextField jTextField1;
        // End of variables declaration//GEN-END:variables
}

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

package org.tmpotter.filters.bitext;

import static org.openide.awt.Mnemonics.setLocalizedText;
import static org.tmpotter.util.Localization.getString;

import org.tmpotter.ui.wizard.IImportWizardPanel;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.ui.wizard.ImportWizardController;
import org.tmpotter.ui.wizard.ImportWizardSelectTypePanel;
import org.tmpotter.util.AppConstants;
import org.tmpotter.util.Localization;

import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * Import Wizard Bi-Text filter panel.
 *
 * @author Hiroshi Miura
 */
public class ImportWizardBiTextFile extends javax.swing.JPanel implements IImportWizardPanel {
    public static final String id = "BiTextFilter";

    private final String[] idiom = Localization.getLanguageList();
    private ImportWizardController wizardController;
    private ImportPreference pref;

    /**
     * Creates new form ImportWizardBiTextFile.
     */
    public ImportWizardBiTextFile() {
    }

    /**
     * Initialize Bitext Filter wizard ui.
     * {@link IImportWizardPanel#init(ImportWizardController, ImportPreference)}
     * @param controller controller.
     * @param preference preference to update.
     */
    public void init(final ImportWizardController controller,
                     final ImportPreference preference) {
        wizardController = controller;
        pref = preference;
        initComponents();
    }

    public void onShow() {
    }

    /**
     * Return ID of BiText filter.
     * {@link IImportWizardPanel#getId()}
     * @return filter id.
     */
    public String getId() {
        return id;
    }

    /**
     * This reads two text files.
     * {@link IImportWizardPanel#isCombinedFormat()}
     * @return false.
     */
    public boolean isCombinedFormat() {
        return false;
    }

    /**
     * {@link IImportWizardPanel#getPanel()}
     * @return this panel.
     */
    public JPanel getPanel() {
        return this;
    }

    /**
     * Return name of bitext filter.
     * {@link IImportWizardPanel#getName()}
     * @return name.
     */
    public String getName() {
        return "Bi-text file";
    }

    /**
     * Return description of bitext filter.
     * {@link IImportWizardPanel#getDesc()}
     * @return description.
     */
    public String getDesc() {
        return "Bi-text file.";
    }

    /**
     * Update preference from fields.
     * {@link IImportWizardPanel#updatePref()}
     */
    public void updatePref() {
        pref.setCurrentPath(new File(getOriginalFile()));
        pref.setOriginalFilePath(new File(getOriginalFile()));
        pref.setTranslationFilePath(new File(getTranslationFile()));
        pref.setOriginalLang(getSourceLocale());
        pref.setTranslationLang(getTargetLocale());
        pref.setSourceEncoding(getOriginalEncoding());
        pref.setTranslationEncoding(getTranslationEncoding());
        pref.setFilter(id);
    }

    /**
     * Return source language.
     * @return source locale.
     */
    public final String getSourceLocale() {
        return Localization.getLanguageCode(comboOriginalLang.getSelectedIndex());
    }

    /**
     * Set default source locale.
     * @param locale source language.
     */
    public final void setSourceLocale(String locale) {
        //String[] codes = Localization.getLanguageList();
        comboOriginalLang.setSelectedItem(locale);
        // FIXME
    }

    /**
     * Return target language.
     * @return target locale.
     */
    public final String getTargetLocale() {
        return Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
    }

    /**
     * Set default target locale.
     * @param locale target language.
     */
    public final void setTargetLocale(String locale) {
        //String[] codes = Localization.getLanguageList();
        comboTranslationLang.setSelectedItem(locale);
        // FIXME
    }

    /**
     * Return character encoding.
     * @return  character encoding.
     */
    public final String getOriginalEncoding() {
        return (String) comboOriginalEncoding.getSelectedItem();
    }

    /**
     * set default character encoding for source text.
     * @param encoding encoding.
     */
    public final void setOriginalEncoding(String encoding) {
        comboOriginalEncoding.setSelectedItem(encoding);
    }

    /**
     * Return character encoding.
     * @return encoding.
     */
    public final String getTranslationEncoding() {
        return (String) comboTranslationEncoding.getSelectedItem();
    }

    /**
     * Set default encoding for translation text.
     * @param encoding encoding.
     */
    public final void setTranslationEncoding(String encoding) {
        comboTranslationEncoding.setSelectedItem(encoding);
    }

    /**
     * Return original file path.
     * @return file path.
     */
    public final String getOriginalFile() {
        return fieldOriginal.getText();
    }

    /**
     * Set default original file path.
     * @param file file path.
     */
    public final void setOriginalFile(String file) {
        fieldOriginal.setText(file);
    }

    /**
     * Return translation file path.
     * @return file path.
     */
    public final String getTranslationFile() {
        return fieldTranslation.getText();
    }

    /**
     * Set default translation file path.
     * @param file file path.
     */
    public void setTranslationFile(String file) {
        fieldTranslation.setText(file);
    }

    /**
     * Return panel id for back button.
     * @return id of type selection panel.
     */
    public String getBackCommand() {
        return ImportWizardSelectTypePanel.id;
    }

    /**
     * Return next/finish button command.
     * @return "finish"
     */
    public String getNextFinishCommand() {
        return "finish";
    }

    /**
     * Action for original file selection.
     * @param panel this wizard panel.
     */
    public void onOriginal(final IImportWizardPanel panel) {
        final JFileChooser fc = new JFileChooser();
        File current = pref.getCurrentPath();
        if (current != null) {
            fc.setCurrentDirectory(current);
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text File", "txt", "utf8");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        final int returnVal = fc.showOpenDialog(this);
        pref.setCurrentPath(fc.getCurrentDirectory());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File originalFilepath = fc.getSelectedFile();
            if (fc.getName(originalFilepath).endsWith(".txt")
                    || fc.getName(originalFilepath).endsWith(".utf8")) {
                if (originalFilepath.exists()) {
                    setOriginalFile(originalFilepath.getPath());
                    pref.setOriginalFilePath(originalFilepath);
                    wizardController.setButtonNextEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            getString("MSG.ERROR.FILE_NOTFOUND"),
                            getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
                    setOriginalFile("");
                }
            }
            //  ToDo: remember filename by preferences
        }
    }

    /**
     * Action for translation file selection.
     * @param panel this panel.
     */
    public void onTranslation(final IImportWizardPanel panel) {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(pref.getCurrentPath());
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text File", "txt", "utf8");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        final int returnVal = fc.showOpenDialog(this);
        pref.setCurrentPath(fc.getCurrentDirectory());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File translationFilePath = fc.getSelectedFile();
            if (fc.getName(translationFilePath).endsWith(".txt")
                    || fc.getName(translationFilePath).endsWith(".utf8")) {
                if (translationFilePath.exists()) {
                    setTranslationFile(translationFilePath.getPath());
                    pref.setTranslationFilePath(translationFilePath);
                    wizardController.setButtonNextEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            getString("MSG.ERROR.FILE_NOTFOUND"),
                            getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
                    setTranslationFile("");
                }
            }
            //  ToDo: remember filename by preferences
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelOriginal = new javax.swing.JLabel();
        fieldOriginal = new javax.swing.JTextField();
        buttonOriginal = new javax.swing.JButton();
        labelOriginalLang = new javax.swing.JLabel();
        comboOriginalLang = new javax.swing.JComboBox<>();
        labelOriginalEncoding = new javax.swing.JLabel();
        comboOriginalEncoding = new javax.swing.JComboBox<>();
        labelTranslation = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        fieldTranslation = new javax.swing.JTextField();
        buttonTranslation = new javax.swing.JButton();
        labelTranslationLang = new javax.swing.JLabel();
        comboTranslationLang = new javax.swing.JComboBox<>();
        labelTranslationEncoding = new javax.swing.JLabel();
        comboTranslationEncoding = new javax.swing.JComboBox<>();

        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(400, 300));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/tmpotter/Bundle"); // NOI18N
        labelOriginal.setText(bundle.getString("LBL.SOURCE.FILE")); // NOI18N

        buttonOriginal.setText("Select File");
        buttonOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOriginalActionPerformed(evt);
            }
        });

        labelOriginalLang.setText(bundle.getString("LBL.SOURCE.LANG")); // NOI18N

        comboOriginalLang.setModel(new javax.swing.DefaultComboBoxModel(idiom));
        comboOriginalLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());

        labelOriginalEncoding.setText(bundle.getString("LBL.ENCODING")); // NOI18N

        comboOriginalEncoding.setModel(new javax.swing.DefaultComboBoxModel(AppConstants.straEncodings.toArray()));
        comboOriginalEncoding.setToolTipText(bundle.getString("LBL.ENCODING.TOOLTIP")); // NOI18N

        labelTranslation.setText(bundle.getString("LBL.TARGET.FILE")); // NOI18N

        buttonTranslation.setText("Select File");
        setLocalizedText(buttonTranslation, getString("BTN.BROWSE.TRANSLATION"));
        buttonTranslation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTranslationActionPerformed(evt);
            }
        });

        labelTranslationLang.setText(bundle.getString("LBL.TARGET.LANG")); // NOI18N

        comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel(idiom));
        comboTranslationLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());

        labelTranslationEncoding.setText(bundle.getString("LBL.ENCODING")); // NOI18N
        labelTranslationEncoding.setToolTipText(bundle.getString("LBL.ENCODING.TOOLTIP")); // NOI18N

        comboTranslationEncoding.setModel(new javax.swing.DefaultComboBoxModel(AppConstants.straEncodings.toArray()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labelTranslation)
                        .addComponent(labelOriginal)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(labelTranslationLang)
                                    .addGap(28, 28, 28)
                                    .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(32, 32, 32)
                                    .addComponent(labelTranslationEncoding))
                                .addComponent(fieldTranslation))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(comboTranslationEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonTranslation)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(fieldOriginal)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(labelOriginalLang)
                                    .addGap(58, 58, 58)
                                    .addComponent(comboOriginalLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(31, 31, 31)
                                    .addComponent(labelOriginalEncoding)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(comboOriginalEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonOriginal)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(labelOriginal)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fieldOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonOriginal))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelOriginalLang)
                        .addComponent(comboOriginalLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelOriginalEncoding)
                        .addComponent(comboOriginalEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(35, 35, 35)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(labelTranslation)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fieldTranslation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonTranslation))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelTranslationLang)
                        .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTranslationEncoding)
                        .addComponent(comboTranslationEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(43, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonOriginalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOriginalActionPerformed
        onOriginal(this);
    }//GEN-LAST:event_buttonOriginalActionPerformed

    private void buttonTranslationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTranslationActionPerformed
        onTranslation(this);
    }//GEN-LAST:event_buttonTranslationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonOriginal;
    private javax.swing.JButton buttonTranslation;
    private javax.swing.JComboBox<String> comboOriginalEncoding;
    private javax.swing.JComboBox<String> comboOriginalLang;
    private javax.swing.JComboBox<String> comboTranslationEncoding;
    private javax.swing.JComboBox<String> comboTranslationLang;
    private javax.swing.JTextField fieldOriginal;
    private javax.swing.JTextField fieldTranslation;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel labelOriginal;
    private javax.swing.JLabel labelOriginalEncoding;
    private javax.swing.JLabel labelOriginalLang;
    private javax.swing.JLabel labelTranslation;
    private javax.swing.JLabel labelTranslationEncoding;
    private javax.swing.JLabel labelTranslationLang;
    // End of variables declaration//GEN-END:variables
}

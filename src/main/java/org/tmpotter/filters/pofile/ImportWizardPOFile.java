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

package org.tmpotter.filters.pofile;


import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tmpotter.ui.wizard.IImportWizardPanel;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.ui.wizard.ImportWizardController;
import org.tmpotter.ui.wizard.ImportWizardSelectTypePanel;
import org.tmpotter.util.AppConstants;

import static org.openide.awt.Mnemonics.setLocalizedText;
import org.tmpotter.util.Localization;

import static org.tmpotter.util.Localization.getString;


/**
 * ImportWizard PO File dialog card.
 *
 * @author Hiroshi Miura
 */
public class ImportWizardPOFile extends javax.swing.JPanel implements IImportWizardPanel {
	public static final String id = "PoFilter";

	private final String[] idiom = Localization.getLanguageList();
	private ImportWizardController wizardController;
	private ImportPreference pref;

	/**
	 * Creates new form ImportWizardPOFile
	 */
	public ImportWizardPOFile() {
	}

	public void init(final ImportWizardController controller,
	    ImportPreference pref) {
		wizardController = controller;
		this.pref = pref;
		initComponents();
		wizardController.setButtonNextEnabled(false);
	}
	
	public String getId() {
		return id;
	}

	public boolean isCombinedFormat() {
		return true;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getName() {
		return "PO file";
	}

	public String getDesc() {
		return "PO file.";
	}
	public final File getPath() {
		return new File(fieldImportFile.getText());
	}

	public final void setPath(final File filePath) {
		fieldImportFile.setText(filePath.getName());
	}

	public final String getSourceLocale() {
		return Localization.getLanguageCode(comboSourceLang.getSelectedIndex());
	}
	
	public final void setSourceLocale(String locale) {
		String[] codes = Localization.getLanguageList();
		comboSourceLang.setSelectedItem(locale);
		// FIXME
	}

	public final String getTargetLocale() {
		return  Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
	}
	
	public final void setTargetLocale(String locale) {
		String[] codes = Localization.getLanguageList();
		comboTranslationLang.setSelectedItem(locale);
		// FIXME
	}

	public final String getOriginalEncoding() {
		return (String) comboEncoding.getSelectedItem();
	}

	public final void setOriginalEncoding(String encoding) {
		comboEncoding.setSelectedItem(encoding);
	}

	public final String getOriginalFile() {
		return fieldImportFile.getText();
	}
	
	public final void setOriginalFile(String file) {
		fieldImportFile.setText(file);
	}

	public final String getTranslationFile() {
		return null;
	}
	
	public void setTranslationFile(String file) {
	}
	
	public final String getTranslationEncoding() {
		return null;
	}
	
	public void setTranslationEncoding(String file) {
	}
	
	public String getBackCommand() {
		return ImportWizardSelectTypePanel.id;
	}

	public String getNextFinishCommand() {
		return "finish";
	}

	public void updatePref() {
		pref.setFilter(id);
		pref.setOriginalFilePath(new File(getOriginalFile()));
		pref.setTranslationFilePath(new File(getOriginalFile()));
		pref.setEncoding(getOriginalEncoding());
		pref.setOriginalLang(getSourceLocale());
		pref.setTranslationLang(getTargetLocale());
	}

	public void onImportFile(final IImportWizardPanel panel) {
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(pref.getCurrentPath());
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		    "PO File", "po");
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(false);
		final int returnVal = fc.showOpenDialog(this);
		pref.setCurrentPath(fc.getCurrentDirectory());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File filePath = fc.getSelectedFile();
			if (fc.getName(filePath).endsWith(".po") && filePath.exists()) {
				setOriginalFile(filePath.getPath());
				pref.setOriginalFilePath(filePath);
				wizardController.setButtonNextEnabled(true);
			} else {
				JOptionPane.showMessageDialog(this,
				    getString("MSG.ERROR.FILE_NOTFOUND"),
				    getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
				setOriginalFile("");
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

                labelImportFile = new javax.swing.JLabel();
                fieldImportFile = new javax.swing.JTextField();
                labelEncoding = new javax.swing.JLabel();
                comboEncoding = new javax.swing.JComboBox<>();
                labelSourceLanguage = new javax.swing.JLabel();
                comboSourceLang = new javax.swing.JComboBox<>();
                labelTargetLanguage = new javax.swing.JLabel();
                comboTranslationLang = new javax.swing.JComboBox<>();
                buttonImportFile = new javax.swing.JButton();
                jLabel1 = new javax.swing.JLabel();

                setMaximumSize(new java.awt.Dimension(800, 600));
                setMinimumSize(new java.awt.Dimension(400, 300));
                setPreferredSize(new java.awt.Dimension(400, 300));

                labelImportFile.setText(getString("LBL.IMPORT.FILE"
                ));

                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/tmpotter/Bundle"); // NOI18N
                labelEncoding.setText(bundle.getString("LBL.ENCODING")); // NOI18N

                comboEncoding.setModel(new javax.swing.DefaultComboBoxModel(AppConstants.straEncodings.toArray()));

                labelSourceLanguage.setText(bundle.getString("LBL.SOURCE.LANG")); // NOI18N

                comboSourceLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));
                comboSourceLang.setToolTipText(getString("CB.LANG.SOURCE.TOOLTIP"));

                labelTargetLanguage.setText(bundle.getString("LBL.TARGET.LANG")); // NOI18N

                comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));

                buttonImportFile.setText("Select");
                buttonImportFile.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                buttonImportFileActionPerformed(evt);
                        }
                });

                jLabel1.setText("Load PO File...");

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(labelImportFile))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(41, 41, 41)
                                                .addComponent(fieldImportFile, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonImportFile))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(25, 25, 25)
                                                .addComponent(jLabel1))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labelTargetLanguage)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(labelEncoding)
                                                                        .addComponent(labelSourceLanguage))
                                                                .addGap(62, 62, 62)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(comboTranslationLang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(comboSourceLang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(comboEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addContainerGap(53, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel1)
                                .addGap(27, 27, 27)
                                .addComponent(labelImportFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(fieldImportFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonImportFile))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelEncoding)
                                        .addComponent(comboEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelSourceLanguage))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelTargetLanguage)
                                        .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(64, Short.MAX_VALUE))
                );
        }// </editor-fold>//GEN-END:initComponents

        private void buttonImportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportFileActionPerformed
		onImportFile(this);
        }//GEN-LAST:event_buttonImportFileActionPerformed


        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton buttonImportFile;
        private javax.swing.JComboBox<String> comboEncoding;
        private javax.swing.JComboBox<String> comboSourceLang;
        private javax.swing.JComboBox<String> comboTranslationLang;
        private javax.swing.JTextField fieldImportFile;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel labelEncoding;
        private javax.swing.JLabel labelImportFile;
        private javax.swing.JLabel labelSourceLanguage;
        private javax.swing.JLabel labelTargetLanguage;
        // End of variables declaration//GEN-END:variables
}

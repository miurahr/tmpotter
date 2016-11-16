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

package org.tmpotter.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tmpotter.util.AppConstants;

import static org.openide.awt.Mnemonics.setLocalizedText;
import org.tmpotter.util.Localization;
import static org.tmpotter.util.Localization.getString;

/**
 *
 * @author Hiroshi Miura
 */
public class ImportFile extends javax.swing.JDialog implements ActionListener {

  /**
   * Creates new form ImportFile.
   */
  public ImportFile(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    addListener();
 }
  private String originalDocFilename;

  private boolean closed;
  private String originalLang;
  private String translationLang;
  private final String [] idiom = Localization.getLanguageList();

  private File filePath;

  public File userPathFile = new File(System.getProperty("user.dir"));

  public final File getPath() {
    return filePath;
  }

  public final void setPath(final File filePath) {
    this.filePath = filePath;
  }

  public final boolean isClosed() {
    return closed;
  }

  public final String getSourceLocale() {
    return originalLang;
  }

  public final String getTargetLocale() {
    return translationLang;
  }

  public final JComboBox getLangEncComboBox() {
    return comboEncoding;
  }

  public final String getSource() {
    return originalDocFilename;
  }

  private void onImportFile() {
    final JFileChooser fc = new JFileChooser();
    if (filePath == null) {
        filePath = userPathFile;
    }
    fc.setCurrentDirectory(filePath);
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "PO File", "po");
    fc.setFileFilter(filter);
    fc.setMultiSelectionEnabled(false);
    final int returnVal = fc.showOpenDialog(panel);
    filePath = fc.getCurrentDirectory();

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      filePath = fc.getSelectedFile();
      if (fc.getName(filePath).endsWith(".po") && filePath.exists()) {
        originalDocFilename = fc.getName(filePath);
        fieldImportFile.setText(filePath.getPath());
      } else {
        JOptionPane.showMessageDialog(panel,
            getString("MSG.ERROR.FILE_NOTFOUND"),
            getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
        fieldImportFile.setText("");
      }
    }
  }

  private void onOk() {
    closed = true;
    try {
      if (fieldImportFile.getText() != null) {
        final FileInputStream fr = new FileInputStream(fieldImportFile.getText());
        fr.close();
        originalDocFilename = fieldImportFile.getText();
        filePath = new File(originalDocFilename);
        originalLang = Localization.getLanguageCode(comboSourceLang.getSelectedIndex());
        translationLang = Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
        setVisible(false);
        closed = false;
      }
    } catch (final IOException ex) {
      JOptionPane.showMessageDialog(panel, getString("MSG.ERROR.FILE_NOTFOUND"),
          getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
      fieldImportFile.setText("");
    }
    dispose();
  }

  private void onCancel() {
    closed = true;
    dispose();
  }

  /**
   * action handler.
   *
   * @param action event
   */
  @Override
  public final void actionPerformed(final ActionEvent action) {
    final Object actor = action.getSource();

    if (actor instanceof JButton) {
      if (actor == buttonCancel) {
        onCancel();
      } else if (actor == buttonOk) {
        onOk();
      } else if (actor == buttonImportFile) {
        onImportFile();
      }
    }
  }

  /**
   * set language code.
   *
   * @param originalFlag indicate original or translation.
   */
  final void setLanguageCode(final boolean originalFlag) {
    String strLang = originalFlag ? comboSourceLang.getSelectedItem()
        .toString() : comboTranslationLang.getSelectedItem().toString();

    if (originalFlag) {
      originalLang = strLang;
    } else {
      translationLang = strLang;
    }
  }

  private void addListener() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent evt) {
        onCancel();
      }
    }); 
  }
 

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                panel = new javax.swing.JPanel();
                buttonOk = new javax.swing.JButton();
                buttonCancel = new javax.swing.JButton();
                labelImportFile = new javax.swing.JLabel();
                fieldImportFile = new javax.swing.JTextField();
                labelEncoding = new javax.swing.JLabel();
                comboEncoding = new javax.swing.JComboBox<>();
                labelSourceLanguage = new javax.swing.JLabel();
                comboSourceLang = new javax.swing.JComboBox<>();
                labelTargetLanguage = new javax.swing.JLabel();
                comboTranslationLang = new javax.swing.JComboBox<>();
                buttonImportFile = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/tmpotter/Bundle"); // NOI18N
                setTitle(bundle.getString("DLG.OPEN.TITLE")); // NOI18N

                buttonOk.setText("OK");
                setLocalizedText(buttonOk, getString("BTN.OK"));
                buttonOk.addActionListener(this);

                buttonCancel.setText("Cancel");
                setLocalizedText(buttonCancel, getString("BTN.CANCEL"));
                buttonCancel.addActionListener(this);

                javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
                panel.setLayout(panelLayout);
                panelLayout.setHorizontalGroup(
                        panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel)
                                .addGap(17, 17, 17))
                );
                panelLayout.setVerticalGroup(
                        panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonOk)
                                        .addComponent(buttonCancel))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                labelImportFile.setText(getString("LBL.IMPORT.FILE"
                ));

                labelEncoding.setText(bundle.getString("LBL.ENCODING")); // NOI18N

                comboEncoding.setModel(new javax.swing.DefaultComboBoxModel(AppConstants.straEncodings.toArray()));

                labelSourceLanguage.setText(bundle.getString("LBL.SOURCE.LANG")); // NOI18N

                comboSourceLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));
                comboSourceLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());
                comboSourceLang.setToolTipText(getString("CB.LANG.SOURCE.TOOLTIP"));

                labelTargetLanguage.setText(bundle.getString("LBL.TARGET.LANG")); // NOI18N

                comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));
                comboTranslationLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());
                comboTranslationLang.setToolTipText(getString("CB.LANG.TARGET.TOOLTIP"));

                setLocalizedText(buttonImportFile, getString("BTN.BROWSE.FILE"));
                buttonImportFile.setText("Select");
                buttonImportFile.addActionListener(this);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelTargetLanguage)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labelImportFile)
                                                        .addComponent(labelEncoding)
                                                        .addComponent(labelSourceLanguage))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(62, 62, 62)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(comboTranslationLang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(comboSourceLang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(comboEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(18, 18, 18)
                                                                .addComponent(fieldImportFile, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(buttonImportFile)))))
                                .addContainerGap(72, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelImportFile)
                                        .addComponent(fieldImportFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonImportFile))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(ImportFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(ImportFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(ImportFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(ImportFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        ImportFile dialog = new ImportFile(new javax.swing.JFrame(), true);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
          @Override
          public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
          }
        });
        dialog.setVisible(true);
      }
    });
  }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton buttonCancel;
        private javax.swing.JButton buttonImportFile;
        private javax.swing.JButton buttonOk;
        private javax.swing.JComboBox<String> comboEncoding;
        private javax.swing.JComboBox<String> comboSourceLang;
        private javax.swing.JComboBox<String> comboTranslationLang;
        private javax.swing.JTextField fieldImportFile;
        private javax.swing.JLabel labelEncoding;
        private javax.swing.JLabel labelImportFile;
        private javax.swing.JLabel labelSourceLanguage;
        private javax.swing.JLabel labelTargetLanguage;
        private javax.swing.JPanel panel;
        // End of variables declaration//GEN-END:variables
}

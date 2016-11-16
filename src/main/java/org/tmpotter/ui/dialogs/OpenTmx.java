/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015-2016 Hiroshi Miura
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.tmpotter.util.Localization;

import static org.openide.awt.Mnemonics.setLocalizedText;
import static org.tmpotter.util.Localization.getString;


/**
 * Open TMX file dialog.
 * @author Hiroshi Miura
 */
@SuppressWarnings("serial")
public class OpenTmx extends javax.swing.JDialog implements ActionListener {

  private File filePath;
  private boolean closed;
  private String originalDoc;
  private final String[] idiom = Localization.getLanguageList();
  private String originalLang;
  private String translationLang;

  public File userPathFile = new File(System.getProperty("user.dir"));

  /**
   * Creates new form OpenTmx
   */
  public OpenTmx(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    addListener();
  }

  private void addListener() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent evt) {
        onClose();
      }
    });
  }

  public final File getFilePath() {
    return (filePath);
  }

  public final void setFilePath(final File filePath) {
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

  private void onTmxFile() {
    final JFileChooser fc = new JFileChooser();
if (filePath == null) {
        filePath = userPathFile;
    }
    fc.setCurrentDirectory(filePath);
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "TMX File", "tmx");
    fc.setFileFilter(filter);
    fc.setMultiSelectionEnabled(false);
    final int returnVal = fc.showOpenDialog(jPanel1);
    filePath = fc.getCurrentDirectory();

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      filePath = fc.getSelectedFile();

      if (fc.getName(filePath).endsWith(".tmx")
          && filePath.exists()) {
        originalDoc = fc.getName(filePath);
        fieldOpenTmxFile.setText(filePath.getPath());

      } else {
        JOptionPane.showMessageDialog(jPanel1,
            getString("MSG.ERROR.FILE_NOTFOUND"),
            getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
        fieldOpenTmxFile.setText("");
      }
    }
  }

  private void onOk() {
    boolean errorTmx = true;
    try {
      if (fieldOpenTmxFile.getText() != null) {
        final FileInputStream fr = new FileInputStream(fieldOpenTmxFile.getText());
        fr.close();
        originalDoc = fieldOpenTmxFile.getText();
        filePath = new File(originalDoc);
        originalLang = Localization.getLanguageCode(comboSourceLang
            .getSelectedIndex());
        translationLang = Localization.getLanguageCode(comboTranslationLang
            .getSelectedIndex());
        errorTmx = false;
        setVisible(false);
      }
    } catch (final IOException ex) {
      JOptionPane.showMessageDialog(jPanel1, getString("MSG.ERROR.FILE_NOTFOUND"),
          getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
      fieldOpenTmxFile.setText("");
    }

    if (errorTmx) {
      fieldOpenTmxFile.setText("");
    }
  }

  private void onCancel() {
    onClose();
  }

  private void onClose() {
    closed = true;
    setVisible(false);
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
      } else if (actor == buttonSelectFile) {
        onTmxFile();
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

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jPanel1 = new javax.swing.JPanel();
                buttonOk = new javax.swing.JButton();
                buttonCancel = new javax.swing.JButton();
                jPanel2 = new javax.swing.JPanel();
                labelTmxFile = new javax.swing.JLabel();
                fieldOpenTmxFile = new javax.swing.JTextField();
                buttonSelectFile = new javax.swing.JButton();
                labelSourceLang = new javax.swing.JLabel();
                labelTranslationLang = new javax.swing.JLabel();
                comboSourceLang = new javax.swing.JComboBox<>();
                comboTranslationLang = new javax.swing.JComboBox<>();

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/tmpotter/Bundle"); // NOI18N
                setTitle(bundle.getString("DLG.OPEN.TMX.TITLE")); // NOI18N

                buttonOk.setText("OK");
                setLocalizedText(buttonOk, getString("BTN.OK"));
                buttonOk.addActionListener(this);

                buttonCancel.setText("Cancel");
                setLocalizedText(buttonCancel, getString("BTN.CANCEL"));
                buttonCancel.addActionListener(this);

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel)
                                .addGap(16, 16, 16))
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(22, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonOk)
                                        .addComponent(buttonCancel))
                                .addGap(15, 15, 15))
                );

                labelTmxFile.setText(bundle.getString("LBL.TMX.FILE")); // NOI18N

                buttonSelectFile.setText("Select File");
                setLocalizedText(buttonSelectFile, getString("BTN.BROWSE.TMX"));
                buttonSelectFile.addActionListener(this);

                labelSourceLang.setText(bundle.getString("LBL.SOURCE.LANG")); // NOI18N

                labelTranslationLang.setText(bundle.getString("LBL.TARGET.LANG")); // NOI18N

                comboSourceLang.setModel(new javax.swing.DefaultComboBoxModel(idiom));
                comboSourceLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());
                comboSourceLang.setToolTipText(bundle.getString("CB.LANG.SOURCE.TOOLTIP")); // NOI18N

                comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel(idiom));
                comboTranslationLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());
                comboTranslationLang.setToolTipText(bundle.getString("CB.LANG.TARGET.TOOLTIP")); // NOI18N

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(labelTranslationLang)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(labelSourceLang)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(labelTmxFile)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(fieldOpenTmxFile, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelectFile)
                                .addContainerGap(15, Short.MAX_VALUE))
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelTmxFile)
                                        .addComponent(fieldOpenTmxFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonSelectFile))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelSourceLang)
                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelTranslationLang)
                                        .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
      java.util.logging.Logger.getLogger(OpenTmx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(OpenTmx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(OpenTmx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(OpenTmx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        OpenTmx dialog = new OpenTmx(new javax.swing.JFrame(), true);
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
        private javax.swing.JButton buttonOk;
        private javax.swing.JButton buttonSelectFile;
        private javax.swing.JComboBox<String> comboSourceLang;
        private javax.swing.JComboBox<String> comboTranslationLang;
        private javax.swing.JTextField fieldOpenTmxFile;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JLabel labelSourceLang;
        private javax.swing.JLabel labelTmxFile;
        private javax.swing.JLabel labelTranslationLang;
        // End of variables declaration//GEN-END:variables
}

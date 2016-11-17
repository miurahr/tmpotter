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
import java.io.File;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import static org.openide.awt.Mnemonics.setLocalizedText;
import org.tmpotter.util.AppConstants;
import org.tmpotter.util.Localization;
import static org.tmpotter.util.Localization.getString;

/**
 *
 * @author miurahr
 */
public class OpenTexts extends javax.swing.JDialog implements ActionListener {

  /**
   * Creates new form OpenTexts
   */
  public OpenTexts(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  private File filePath;
  private File originalFilepath;
  private File translationFilePath;
  private String originalDocFilename;
  private String translationDoc;
  private boolean closed;

  public final File getPath() {
    return filePath;
  }

  public final void setPath(final File filePath) {
    this.filePath = filePath;
  }

  public final File getSourcePath() {
    return originalFilepath;
  }

  public final File getTargetPath() {
    return translationFilePath;
  }

  public final boolean isClosed() {
    return closed;
  }

  public final String getSource() {
    return originalDocFilename;
  }

  public final String getTarget() {
    return translationDoc;
  }

  private String originalLang;
  private String translationLang;

  public File userPathFile = new File(System.getProperty("user.dir"));

  private final int numEncodings = AppConstants.straEncodings.size();

  private final String[] idiom = Localization.getLanguageList();

  
  public final String getSourceLocale() {
    return ( originalLang );
  }
  
  public final String getTargetLocale() {
    return ( translationLang ); 
  }

  public final JComboBox getSourceLangEncComboBox() {
    return comboOriginalEncoding;
  }
  
  public final JComboBox getTargetLangEncComboBox() {
    return comboTranslationEncoding;
  }

  private void onOriginal() {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(filePath);
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Text File", "txt", "utf8");
    fc.setFileFilter(filter);
    fc.setMultiSelectionEnabled(false);
    final int returnVal = fc.showOpenDialog(panel);
    filePath = fc.getCurrentDirectory();

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      originalFilepath = fc.getSelectedFile();

      if (fc.getName(originalFilepath).endsWith(".txt")||
	  fc.getName(originalFilepath).endsWith(".utf8")) {
        if (originalFilepath.exists()) {
          originalDocFilename = fc.getName(originalFilepath);
          fieldOriginal.setText(originalFilepath.getPath());
        } else {
          JOptionPane.showMessageDialog(panel,
              getString("MSG.ERROR.FILE_NOTFOUND"),
              getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
          fieldOriginal.setText("");
        }
      }
      //  ToDo: remember filename by preferences
    }
  }

  private void onTranslation() {
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(filePath);
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Text File", "txt", "utf8");
    fc.setFileFilter(filter);
    fc.setMultiSelectionEnabled(false);
    final int returnVal = fc.showOpenDialog(panel);
    filePath = fc.getCurrentDirectory();

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      translationFilePath = fc.getSelectedFile();

      if (fc.getName(translationFilePath).endsWith(".txt")||
	  fc.getName(translationFilePath).endsWith(".utf8")) {
        if (translationFilePath.exists()) {
          translationDoc = fc.getName(translationFilePath);
          fieldTranslation.setText(translationFilePath.getPath());
        } else {
          JOptionPane.showMessageDialog(panel,
                  getString("MSG.ERROR.FILE_NOTFOUND"),
                  getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);

          fieldTranslation.setText("");
        }
      }
      //  ToDo: remember filename by preferences
    }
  }

  private void onOk() {

    if (fieldOriginal.getText() != null) {
      originalDocFilename = fieldOriginal.getText();
      originalFilepath = new File(originalDocFilename);
      if (!originalFilepath.exists()) {
        showFileNotFoundDlg();
        fieldOriginal.setText("");
      }
      setLanguageCode(true);
    }
    if (fieldTranslation.getText() != null) {
      translationDoc = fieldTranslation.getText();
      translationFilePath = new File(translationDoc);
      if (!translationFilePath.exists()) {
        showFileNotFoundDlg();
        fieldTranslation.setText("");
      }
      setLanguageCode(false);
      setVisible(false);
    }
  }

  private void showFileNotFoundDlg() {
    JOptionPane.showMessageDialog(panel, getString("MSG.ERROR.FILE_NOTFOUND"),
          getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE );
  }

  private void onCancel() {
    onClose();
  }

  private void onClose() {
    closed = true;
    setVisible( false );
    dispose();
  }

  /**
   * set language code.
   * 
   * @param originalFlag indicate original or translation.
   */
  final void setLanguageCode( final boolean originalFlag ) {

    if (originalFlag) {
      originalLang = Localization.getLanguageCode(comboOriginalLang.getSelectedIndex());
    } else {
      translationLang = Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
    }
  }

  /**
   * action handler.
   * 
   * @param action event
   */
  @Override
  public final void actionPerformed( final ActionEvent action ) {
    final Object actor = action.getSource();

    if ( actor instanceof JButton ) {
      if ( actor == buttonCancel ) {
        onCancel();
      } else if ( actor == buttonOk ) {
        onOk();
      } else if ( actor == buttonOriginal ) {
        onOriginal();
      } else if ( actor == buttonTranslation ) {
        onTranslation();
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

                panel = new javax.swing.JPanel();
                buttonOk = new javax.swing.JButton();
                buttonCancel = new javax.swing.JButton();
                jSeparator1 = new javax.swing.JSeparator();
                jPanel2 = new javax.swing.JPanel();
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
                                .addContainerGap(375, Short.MAX_VALUE)
                                .addComponent(buttonOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel)
                                .addGap(16, 16, 16))
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator1))
                );
                panelLayout.setVerticalGroup(
                        panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonOk)
                                        .addComponent(buttonCancel))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                getContentPane().add(panel, java.awt.BorderLayout.CENTER);

                labelOriginal.setText(bundle.getString("LBL.SOURCE.FILE")); // NOI18N

                buttonOriginal.setText("Select File");
                setLocalizedText(buttonOriginal, getString("BTN.BROWSE.ORIGINAL"));
                buttonOriginal.addActionListener(this);

                labelOriginalLang.setText(bundle.getString("LBL.SOURCE.LANG")); // NOI18N

                comboOriginalLang.setModel(new javax.swing.DefaultComboBoxModel(idiom));
                comboOriginalLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());

                labelOriginalEncoding.setText(bundle.getString("LBL.ENCODING")); // NOI18N

                comboOriginalEncoding.setModel(new javax.swing.DefaultComboBoxModel(AppConstants.straEncodings.toArray()));
                comboOriginalEncoding.setToolTipText(bundle.getString("LBL.ENCODING.TOOLTIP")); // NOI18N

                labelTranslation.setText(bundle.getString("LBL.TARGET.FILE")); // NOI18N

                buttonTranslation.setText("Select File");
                setLocalizedText(buttonTranslation, getString("BTN.BROWSE.TRANSLATION"));
                buttonTranslation.addActionListener(this);

                labelTranslationLang.setText(bundle.getString("LBL.TARGET.LANG")); // NOI18N

                comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel(idiom));
                comboTranslationLang.setSelectedItem(Locale.getDefault().getDisplayLanguage());

                labelTranslationEncoding.setText(bundle.getString("LBL.ENCODING")); // NOI18N
                labelTranslationEncoding.setToolTipText(bundle.getString("LBL.ENCODING.TOOLTIP")); // NOI18N

                comboTranslationEncoding.setModel(new javax.swing.DefaultComboBoxModel(AppConstants.straEncodings.toArray()));

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(labelTranslationLang)
                                                .addGap(28, 28, 28)
                                                .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(32, 32, 32)
                                                .addComponent(labelTranslationEncoding)
                                                .addGap(18, 18, 18)
                                                .addComponent(comboTranslationEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(labelTranslation)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(fieldTranslation, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonTranslation))
                                        .addComponent(labelOriginal)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(labelOriginalLang)
                                                .addGap(58, 58, 58)
                                                .addComponent(comboOriginalLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(31, 31, 31)
                                                .addComponent(labelOriginalEncoding)
                                                .addGap(18, 18, 18)
                                                .addComponent(comboOriginalEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(fieldOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonOriginal)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(labelOriginal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(fieldOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonOriginal))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelOriginalLang)
                                        .addComponent(comboOriginalLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelOriginalEncoding)
                                        .addComponent(comboOriginalEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTranslation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(fieldTranslation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonTranslation))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelTranslationLang)
                                        .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelTranslationEncoding)
                                        .addComponent(comboTranslationEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(22, Short.MAX_VALUE))
                );

                getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

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
      java.util.logging.Logger.getLogger(OpenTexts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(OpenTexts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(OpenTexts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(OpenTexts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        OpenTexts dialog = new OpenTexts(new javax.swing.JFrame(), true);
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
        private javax.swing.JButton buttonOriginal;
        private javax.swing.JButton buttonTranslation;
        private javax.swing.JComboBox<String> comboOriginalEncoding;
        private javax.swing.JComboBox<String> comboOriginalLang;
        private javax.swing.JComboBox<String> comboTranslationEncoding;
        private javax.swing.JComboBox<String> comboTranslationLang;
        private javax.swing.JTextField fieldOriginal;
        private javax.swing.JTextField fieldTranslation;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JSeparator jSeparator1;
        private javax.swing.JSeparator jSeparator2;
        private javax.swing.JLabel labelOriginal;
        private javax.swing.JLabel labelOriginalEncoding;
        private javax.swing.JLabel labelOriginalLang;
        private javax.swing.JLabel labelTranslation;
        private javax.swing.JLabel labelTranslationEncoding;
        private javax.swing.JLabel labelTranslationLang;
        private javax.swing.JPanel panel;
        // End of variables declaration//GEN-END:variables
}

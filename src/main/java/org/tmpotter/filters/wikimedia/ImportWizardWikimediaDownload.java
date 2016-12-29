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

package org.tmpotter.filters.wikimedia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.ui.wizard.IImportWizardPanel;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.ui.wizard.ImportWizardController;
import org.tmpotter.util.DownloadWorker;
import org.tmpotter.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;


/**
 * Wikimedia download progress pane.
 *
 * @author Hiroshi Miura
 */
public class ImportWizardWikimediaDownload extends javax.swing.JPanel implements IImportWizardPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportWizardWikimediaDownload.class
            .getName());
    private ImportWizardController wizardController;
    private ImportPreference pref;
    private URI sourceUri;
    private URI translationUri;
    private File originalFilePath;
    private File translationFilePath;

    public static final String id = "WikimediaDownload";

    /**
     * Creates new form ImportWizardWikimediaDownload
     */
    public ImportWizardWikimediaDownload() {
    }

    public void init(final ImportWizardController controller, ImportPreference pref) {
        wizardController = controller;
        this.pref = pref;
        initComponents();
    }

    public void onShow() {
        wizardController.setButtonNextEnabled(false);
        downloadButton.setEnabled(true);
        sourceUri = pref.getOriginalFileUri();
        sourceUrlLabel.setText(sourceUri.toString());
        translationUri = pref.getTranslationFileUri();
        translationUrlLabel.setText(translationUri.toString());
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
        return "Wikimedia download";
    }

    public String getDesc() {
        return "Wikimeida download.";
    }

    public String getBackCommand() {
        return ImportWizardWikimedia.id;
    }

    public String getNextFinishCommand() {
        return "finish";
    }

    public void updatePref() {
        pref.setFilter("BiTextFilter");
        pref.setOriginalFilePath(originalFilePath);
        pref.setTranslationFilePath(translationFilePath);
    }

    /**
     * Does wikiread.
     */
    public void doWikiDownload() {
        File tempDirectory;
        wizardController.setButtonBackEnabled(false);
        downloadButton.setEnabled(false);
        originalFilePath = null;
        translationFilePath = null;
        try {
            tempDirectory = createTempDirectory();
            tempDirectory.deleteOnExit();
            DownloadWorker wk = new DownloadWorker(tempDirectory, sourceUri,
                    new DownloadWorker.IDownloadCallback() {
                        @Override
                        public synchronized void getResult(final File result) {
                            originalFilePath = result;
                            if (translationFilePath != null) {
                                wizardController.setButtonNextEnabled(true);
                                wizardController.setButtonBackEnabled(true);
                            }
                        }
                        @Override
                        public void progress(final Long len) {
                            sourceProgressLabel.setText(Utilities.humanReadableByteCount(len, true));
                        }
                    });
            wk.execute();
            DownloadWorker wk2 = new DownloadWorker(tempDirectory, translationUri,
                     new DownloadWorker.IDownloadCallback() {
                        @Override
                        public synchronized void getResult(final File result) {
                            translationFilePath = result;
                            if (originalFilePath != null) {
                                wizardController.setButtonNextEnabled(true);
                                wizardController.setButtonBackEnabled(true);
                            }
                        }
                        @Override
                        public void progress(final Long len) {
                            translationProgressLabel.setText(Utilities.humanReadableByteCount(len,
                                    true));
                        }
                    });
            wk2.execute();
        } catch (IOException ex) {
            LOGGER.info("File I/O error.", ex);
        }
    }

    private static File createTempDirectory() throws IOException {
        final File temp;
        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        return (temp);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jLabel1 = new javax.swing.JLabel();
                sourceUrlLabel = new javax.swing.JLabel();
                translationUrlLabel = new javax.swing.JLabel();
                downloadButton = new javax.swing.JButton();
                jLabel2 = new javax.swing.JLabel();
                sourceProgressLabel = new javax.swing.JLabel();
                translationProgressLabel = new javax.swing.JLabel();

                jLabel1.setText("Source:");

                sourceUrlLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                sourceUrlLabel.setText("Source URL");

                translationUrlLabel.setText("Translation URL");

                downloadButton.setText("Start Download");
                downloadButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                downloadButtonActionPerformed(evt);
                        }
                });

                jLabel2.setText("Translation:");

                sourceProgressLabel.setText("0.0kB");

                translationProgressLabel.setText("0.0kB");

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(35, 35, 35)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(translationProgressLabel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(translationUrlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(sourceProgressLabel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(sourceUrlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(134, 134, 134)
                                                .addComponent(downloadButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel2))))
                                .addContainerGap(65, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(jLabel1)
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(sourceUrlLabel)
                                        .addComponent(sourceProgressLabel))
                                .addGap(27, 27, 27)
                                .addComponent(jLabel2)
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(translationUrlLabel)
                                        .addComponent(translationProgressLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                                .addComponent(downloadButton)
                                .addGap(32, 32, 32))
                );
        }// </editor-fold>//GEN-END:initComponents

        private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
            doWikiDownload();
        }//GEN-LAST:event_downloadButtonActionPerformed


        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton downloadButton;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel sourceProgressLabel;
        private javax.swing.JLabel sourceUrlLabel;
        private javax.swing.JLabel translationProgressLabel;
        private javax.swing.JLabel translationUrlLabel;
        // End of variables declaration//GEN-END:variables
}

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

import java.awt.CardLayout;
import java.util.Map;
import java.util.TreeMap;

import static org.openide.awt.Mnemonics.setLocalizedText;


/**
 * Import Wizard dialog.
 *
 * @author Hiroshi Miura
 */
public class ImportWizard extends javax.swing.JDialog {
    private final ImportWizardController wizardController;
    private final ImportPreference pref;
    private final Map<String, IImportWizardPanel> wizardPanels = new TreeMap<>();
    private String currentPanel;

    /**
     * Creates new form ImportWizard
     * @param parent parent gui parts.
     * @param modal set true if wizard is modal, otherwise false.
     */
    public ImportWizard(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        pref = new ImportPreference();
        wizardController = new ImportWizardController(this);
        wizardController.onStartup();
    }

    /**
     * Register wizard panel.
     * @param id panel unique id.
     * @param panel panel object.
     */
    public void registerWizardPanel(String id, IImportWizardPanel panel) {
        panelCard.add(panel.getPanel(), id);
        wizardPanels.put(id, panel);
    }

    /**
     * Show specified panel on wizard.
     * @param id panel id to show, which should be registered with
     *     {@link ImportWizard#registerWizardPanel(String, IImportWizardPanel)}
     */
    public void showPanel(String id) {
        CardLayout cl = (CardLayout) (panelCard.getLayout());
        cl.show(panelCard, id);
        currentPanel = id;
        wizardPanels.get(id).onShow();
        setButtonNextLabel();
   }

    /**
     * Enable/disable back button.
     * @param b true when enable back button, otherwise false.
     */
    public void setButtonBackEnabled(boolean b) {
        buttonBack.setEnabled(b);
    }

    /**
     * Return back button command.
     * @return back button command.
     */
    String getButtonBackCommand() {
        return wizardPanels.get(currentPanel).getBackCommand();
    }

    /**
     * Retrun next/finish button command.
     * @return panel id or "finish"
     */
    String getButtonNextCommand() {
        return wizardPanels.get(currentPanel).getNextFinishCommand();
    }

    /**
     * Enable/disable next/finish button.
     * @param b true when enable button, otherwise false.
     */
    void setButtonNextEnabled(boolean b) {
        buttonNextFinish.setEnabled(b);
    }

    void setButtonNextLabel() {
        if (currentPanel == null) {
            setLocalizedText(buttonNextFinish, "Next");
        } else if ("finish".equals(getButtonNextCommand())) {
            setLocalizedText(buttonNextFinish, "Finish");
        } else {
            setLocalizedText(buttonNextFinish, "Next");
        }
    }

    public ImportPreference getPref() {
        return pref;
    }

    public void updatePref() {
        wizardPanels.get(currentPanel).updatePref();
    }

    public boolean isFinished() {
        return wizardController.isFinished();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelWizard = new javax.swing.JPanel();
        panelButtons = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 50), new java.awt.Dimension(0, 50), new java.awt.Dimension(32767, 50));
        buttonBack = new javax.swing.JButton();
        buttonNextFinish = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        panelCard = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);

        panelButtons.setPreferredSize(new java.awt.Dimension(400, 100));
        panelButtons.setLayout(new javax.swing.BoxLayout(panelButtons, javax.swing.BoxLayout.LINE_AXIS));
        panelButtons.add(filler2);

        buttonBack.setText("Back");
        buttonBack.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        buttonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackActionPerformed(evt);
            }
        });
        panelButtons.add(buttonBack);

        buttonNextFinish.setText("Next");
        buttonNextFinish.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        buttonNextFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextFinishActionPerformed(evt);
            }
        });
        panelButtons.add(buttonNextFinish);

        buttonCancel.setText("Cancel");
        buttonCancel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel);

        panelCard.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        panelCard.setMaximumSize(new java.awt.Dimension(800, 600));
        panelCard.setMinimumSize(new java.awt.Dimension(400, 300));
        panelCard.setPreferredSize(new java.awt.Dimension(400, 300));
        panelCard.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout panelWizardLayout = new javax.swing.GroupLayout(panelWizard);
        panelWizard.setLayout(panelWizardLayout);
        panelWizardLayout.setHorizontalGroup(
            panelWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelWizardLayout.createSequentialGroup()
                    .addGroup(panelWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(panelCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                    .addGap(0, 0, Short.MAX_VALUE))
                .addComponent(jSeparator1)
        );
        panelWizardLayout.setVerticalGroup(
            panelWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelWizardLayout.createSequentialGroup()
                    .addComponent(panelCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );

        getContentPane().add(panelWizard, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackActionPerformed
        // TODO add your handling code here:
        wizardController.onBack();
    }//GEN-LAST:event_buttonBackActionPerformed

    private void buttonNextFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextFinishActionPerformed
        wizardController.onNextFinish();
    }//GEN-LAST:event_buttonNextFinishActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        // TODO add your handling code here:
        wizardController.onCancel();
    }//GEN-LAST:event_buttonCancelActionPerformed


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
            java.util.logging.Logger.getLogger(ImportWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ImportWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ImportWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImportWizard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

		/* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ImportWizard dialog = new ImportWizard(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton buttonBack;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonNextFinish;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelCard;
    private javax.swing.JPanel panelWizard;
    // End of variables declaration//GEN-END:variables
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package scalereader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author ali
 */
public class ArticlePanel extends javax.swing.JPanel {

    /**
     * Creates new form ArticlePanel
     */
    public ArticlePanel(BufferedImage img,String text,String prix) {
        initComponents();
        setVisible(true);
        title.setText(text);
        ImageIcon icon = new ImageIcon(img);
        Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        image.setIcon(scaledIcon);
        this.prix.setText(prix+" DH");
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        image = new javax.swing.JLabel();
        prix = new javax.swing.JLabel();
        title = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        prix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        prix.setForeground(new java.awt.Color(255, 102, 0));
        prix.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prix.setText("jLabel3");

        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prix, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(prix)
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel image;
    private javax.swing.JLabel prix;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}

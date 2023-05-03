/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author ali
 */
public class RowLabel extends JLabel {
    private final BufferedImage img;

    public RowLabel(BufferedImage img) {
        this.img = img;
        setOpaque(false); // make label transparent
        addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
        // your onclick code here
            System.out.println(img);
        }
            });
    }

    public ImageIcon getRowIMG() {
        ImageIcon imageIcon = new ImageIcon(img);
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        return new ImageIcon(newimg);
    }
    
    
}


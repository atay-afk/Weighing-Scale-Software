/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Beans;

import java.awt.image.BufferedImage;

/**
 *
 * @author ali
 */
public class Famille {
    private BufferedImage img;
    private int id;
    private String nomFam;

    public BufferedImage getImg() {
        return img;
    }

    public int getId() {
        return id;
    }

    public String getNomFam() {
        return nomFam;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNomFam(String nomFam) {
        this.nomFam = nomFam;
    }
    
    
}

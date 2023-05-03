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
public class Article {
    private String designation;
    private double quantite=1;
    private double prix;
    private String pesable;
    private String id;
    private int famille;
    private BufferedImage img;

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }
   
    
    public double calculPrixTotal(){
       return quantite*prix;
    }

    public String getPesable() {
        return pesable;
    }

    public void setPesable(String pesable) {
        this.pesable = pesable;
    }

    
    public void setPrix(double prixUn) {
        this.prix = prixUn;
    }


    public void setId(String id) {
        this.id = id;
    }


    public double getPrix() {
        return prix;
    }



    public String getId() {
        return id;
    }

    
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

   

    public void setFamille(int famille) {
        this.famille = famille;
    }

    public String getDesignation() {
        return designation;
    }

    public double getQuantite() {
        return quantite;
    }

    

    public int getFamille() {
        return famille;
    }
    
    
    
}

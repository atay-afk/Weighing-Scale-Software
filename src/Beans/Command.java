/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Beans;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author ali
 */
public class Command {
    private String barcode;
    private String designation;
    private double quantite=1;
    private String pesable;
    private double prix;
    private Timestamp date=Timestamp.valueOf(LocalDateTime.now());

    public String getPesable() {
        return pesable;
    }

    public void setPesable(String pesable) {
        this.pesable = pesable;
    }

    
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public static double getPrixTotal(List<Command> list){
        return list.stream().mapToDouble(e -> e.getPrix()).sum();
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }


    public void setPrix(double prix) {
        this.prix= prix;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDesignation() {
        return designation;
    }

    public double getQuantite() {
        return quantite;
    }

    public double getPrix() {
        return prix;
    }

    public Timestamp getDate() {
        return date;
    }
    
    
}

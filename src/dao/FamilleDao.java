/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Famille;
import java.util.List;

/**
 *
 * @author ali
 */
public interface FamilleDao {
   List<Famille> listerFamilles();
    void addFamille(Famille fam);
    void deleteFamille(int id);
    void editFamille(int id,Famille fam); 
}

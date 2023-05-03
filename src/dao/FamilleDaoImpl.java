/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Famille;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ali
 */
public class FamilleDaoImpl implements FamilleDao{
    private DaoFactory daoFactory;

    public FamilleDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public List<Famille> listerFamilles() {
        List<Famille> familles = new ArrayList<Famille>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT id,nom_fam,image FROM famille;");

            while (resultat.next()) {
                Famille famille = new Famille();
                famille.setId(resultat.getInt("id"));
                famille.setNomFam(resultat.getString("nom_fam"));
                byte[] imageData = resultat.getBytes("image");

                // Create an InputStream from the byte array
                InputStream inputStream = new ByteArrayInputStream(imageData);
                try {
                    famille.setImg(ImageIO.read(inputStream));
                } catch (IOException ex) {
                    Logger.getLogger(ArticleDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
               

                familles.add(famille);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return familles;
    }

    @Override
    public void addFamille(Famille fam) {
         try {
            Connection connexion = daoFactory.getConnection();
            PreparedStatement stmt = connexion.prepareStatement("INSERT INTO famille(nom_fam,image) VALUES(?,?);");
            byte[] imageBytes = imageToByteArray(fam.getImg(), "png");
            stmt.setString(1, fam.getNomFam());
            stmt.setBytes(2, imageBytes);            
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public byte[] imageToByteArray(BufferedImage image, String format) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, format, baos);
    return baos.toByteArray();
    }

    @Override
    public void deleteFamille(int id) {
        Connection connexion = null;
        Statement statement = null;
        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            statement.executeUpdate("DELETE FROM famille WHERE id="+id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editFamille(int id, Famille fam) {
    Connection connexion = null;
    PreparedStatement statement = null;
    try {
        connexion = daoFactory.getConnection();
        statement = connexion.prepareStatement("UPDATE famille SET nom_fam=?, image=? WHERE id=?");
        statement.setString(1, fam.getNomFam());
        byte[] imageBytes = imageToByteArray(fam.getImg(), "png");
        statement.setBytes(2, imageBytes);
        statement.setInt(3, id);
        statement.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    
    
}

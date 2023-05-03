/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Command;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ali
 */
public class CommandDaoImpl implements CommandDao{

    private DaoFactory daoFactory;
    
    CommandDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    @Override
    public void ajouter(Command command) {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("INSERT INTO historique(designation,quantite,prix,date,barcode,pesable) VALUES(?, ?, ?, ?, ?,?);");
            preparedStatement.setString(1, command.getDesignation());
            preparedStatement.setDouble(2, command.getQuantite());
            preparedStatement.setDouble(3, command.getPrix());
            preparedStatement.setTimestamp(4, command.getDate());
            preparedStatement.setString(5, command.getBarcode());
            preparedStatement.setString(6, command.getPesable());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Command> getCommands() {
        List<Command> commandes = new ArrayList<>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT barcode,designation,quantite,prix,date,pesable FROM historique order by date asc");

            while (resultat.next()) {
                Command cmd = new Command();
                cmd.setBarcode(resultat.getString("barcode"));
                cmd.setDesignation(resultat.getString("designation"));
                cmd.setPrix(resultat.getDouble("prix"));
                cmd.setQuantite(resultat.getDouble("quantite"));
                cmd.setDate(resultat.getTimestamp("date"));
                cmd.setPesable(resultat.getString("pesable"));

                commandes.add(cmd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
}

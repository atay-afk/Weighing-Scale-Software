/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ali
 */
public class DaoFactory {
    
    
    public static String error="";
    

    public Connection connectionclass() throws ClassNotFoundException, SQLException {
        Connection connection=null;
        String ConnectionURL;
        Class.forName("org.sqlite.JDBC");
        //ConnectionURL= "jdbc:jtds:sqlserver://"+ip+";databaseName="+dbname+";user="+user+";password="+pwd+"; ";
        ConnectionURL = "jdbc:sqlite:src\\db\\scaleDB.db";

        connection= DriverManager.getConnection(ConnectionURL);

        return connection;

    }
  
    public static DaoFactory getInstance() {
        try {
        Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {

        }

        DaoFactory instance = new DaoFactory();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        String url= "jdbc:sqlite:src\\db\\scaleDB.db";

        return DriverManager.getConnection(url);
    }

    // Récupération du Dao
    public ArticleDao getArticleDao() {
        return new ArticleDaoImpl(this);
    }
    
    public CommandDao getCommandDao() {
        return new CommandDaoImpl(this);
    }
    
    public UserDao getUserDao() {
        return new UserDaoImpl(this);
    }
    
    public FamilleDao getFamilleDao() {
        return new FamilleDaoImpl(this);
    }

}

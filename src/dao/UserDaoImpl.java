/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Users;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.ResultSetMetaData;


/**
 *
 * @author ali
 */
public class UserDaoImpl implements UserDao{

    private DaoFactory daoFactory;

    public UserDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    @Override
    public boolean connexionSucceeded(String login, String pwd, String fonction) {
        List<Users> co = listerUsers().stream()
                    .filter(e -> e.getLogin().equals(login))
                    .filter(e -> e.getPwd().equals(pwd))
                    .filter(e -> e.getFonction().equals(fonction))
                    .collect(Collectors.toList());
        if(co.isEmpty())
            return false;
        else
            return true;
       
    }
    
    
    @Override
    public void exportUser(Component c) {
    // Show a file chooser dialog to select the Excel file
        JFileChooser chooser = new JFileChooser();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet rs = null;
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
         
        
        try {
            connexion = daoFactory.getConnection();
             stmt = connexion.createStatement();
             rs = stmt.executeQuery("SELECT * FROM users");

                // Write the data to the Excel file
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Data");
                int rowNum = 0;
                while (rs.next()) {
                    if (rowNum == 0) {
                        // Create the header row
                        Row headerRow = sheet.createRow(rowNum++);
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Cell headerCell = headerRow.createCell(i - 1);
                            headerCell.setCellValue(columnName);
                        }
                    }

                // Create a new row and add the data from the ResultSet
                    Row row = sheet.createRow(rowNum++);
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);
                        Cell cell = row.createCell(i - 1);
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }

                // Write the workbook to the file
                    FileOutputStream fos = new FileOutputStream(file);
                    workbook.write(fos);
                

            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    

    @Override
    public List<Users> listerUsers() {
        List<Users> users = new ArrayList<Users>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT nom,prenom,login,pwd,fonction FROM users;");

            while (resultat.next()) {
                Users user = new Users();
                user.setNom(resultat.getString("nom"));
                user.setPrenom(resultat.getString("prenom"));
                user.setLogin(resultat.getString("login"));
                user.setPwd(resultat.getString("pwd"));
                user.setFonction(resultat.getString("fonction"));
               

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void addUser(Users user) {
        Connection connexion = null;
        Statement statement = null;
        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            statement.executeUpdate("INSERT INTO users(nom,prenom,login,pwd,fonction) VALUES('"+user.getNom()+"','"+user.getPrenom()
                    +"','"+user.getLogin()+"','"+user.getPwd()+"','"+user.getFonction()+"');");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(String login) {
        Connection connexion = null;
        Statement statement = null;
        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            statement.executeUpdate("DELETE FROM users WHERE login='"+login+"'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editUser(String login, Users user) {
        Connection connexion = null;
        Statement statement = null;
        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            statement.executeUpdate("UPDATE users set nom='"+user.getNom()+"',prenom='"+user.getPrenom()
                    +"',pwd='"+user.getPwd()+"',fonction='"+user.getPwd()+"' where login='"+login+"'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Users connectedUser(String login, String pwd, String fonction) {
       return listerUsers().stream()
                    .filter(e -> e.getLogin().equals(login))
                    .filter(e -> e.getPwd().equals(pwd))
                    .filter(e -> e.getFonction().equals(fonction)).findFirst()
                    .orElse(null);
    }
    
}

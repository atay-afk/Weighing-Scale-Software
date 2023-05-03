
package dao;

import Beans.Article;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ali
 */
public class ArticleDaoImpl implements ArticleDao{
    
      private DaoFactory daoFactory;

    ArticleDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    
     @Override
    public void exportArticle(Component c) {
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
             rs = stmt.executeQuery("SELECT id,designation,prixUn,prixKg,famille FROM articles");

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
    public List<Article> listerArticles() {
        List<Article> articles = new ArrayList<Article>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT id,designation,prix,pesable,famille,image FROM articles;");

            while (resultat.next()) {
                String designation = resultat.getString("designation");

                Article article = new Article();
                article.setId(resultat.getString("id"));
                article.setDesignation(designation);
                article.setPrix(resultat.getDouble("prix"));
                article.setPesable(resultat.getString("pesable"));
                article.setFamille(resultat.getInt("famille"));
                byte[] imageData = resultat.getBytes("image");

                // Create an InputStream from the byte array
                InputStream inputStream = new ByteArrayInputStream(imageData);
                try {
                    article.setImg(ImageIO.read(inputStream));
                } catch (IOException ex) {
                    Logger.getLogger(ArticleDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    @Override
    public void addArticle(Article art) {
        Connection connexion = null;
        Statement statement = null;
        try {
            connexion = daoFactory.getConnection();
            PreparedStatement stmt = connexion.prepareStatement("INSERT INTO articles(designation,prix,pesable,famille,image)"
                    + " VALUES (?,?,?,?,?)");
            byte[] imageBytes = imageToByteArray(art.getImg(), "png");
            stmt.setString(1, art.getDesignation());
            stmt.setDouble(2, art.getPrix());
            stmt.setString(3, art.getPesable());
            stmt.setInt(4, art.getFamille());
            stmt.setBytes(5, imageBytes);
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
    public void deleteArticle(String id) {
        Connection connexion = null;
        Statement statement = null;
        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            statement.executeUpdate("DELETE FROM articles WHERE id='"+id+"'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
        @Override
    public void editArticle(String id, Article art) {
    Connection connexion = null;
    PreparedStatement stmt = null;
    try {
        connexion = daoFactory.getConnection();
        stmt = connexion.prepareStatement("UPDATE articles set designation=?,prix=?,pesable=?,image=?,famille=? where id=?");
        byte[] imageBytes = imageToByteArray(art.getImg(), "png");
            stmt.setString(1, art.getDesignation());
            stmt.setDouble(2, art.getPrix());
            stmt.setString(3, art.getPesable());
            stmt.setInt(5, art.getFamille());
            stmt.setBytes(4, imageBytes);
            stmt.setString(6, id);
            stmt.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (stmt != null) {
            try {
                stmt.close();
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

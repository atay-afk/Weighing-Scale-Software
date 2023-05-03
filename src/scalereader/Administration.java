/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package scalereader;

import Beans.Article;
import Beans.Famille;
import Beans.Users;
import dao.ArticleDao;
import dao.DaoFactory;
import dao.FamilleDao;
import dao.UserDao;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tools.RowLabel;
import tools.TableActionCellEditor;
import tools.TableActionCellRender;
import tools.TableActionEvent;

/**
 *
 * @author ali
 */
public class Administration extends javax.swing.JFrame {

    DaoFactory daoFactory = DaoFactory.getInstance();
    UserDao userdao=daoFactory.getUserDao();
    FamilleDao familledao=daoFactory.getFamilleDao();
    ArticleDao articledao=daoFactory.getArticleDao();
    DefaultTableModel modeluser,modelfam,modelart;
    Article article=new Article();
    Famille fam=new Famille();
    String f;
    
    public Administration() {
        initComponents();
        this.setLocationRelativeTo(null);
        nomAdmin.setText(Users.getInstance().getNom());
        prenomAdmin.setText(Users.getInstance().getPrenom());
        img.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        img1.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        
        // insert rows user table
        modeluser=(DefaultTableModel) userTab.getModel();
        for(Users user:userdao.listerUsers()){
        modeluser.insertRow(modeluser.getRowCount(),new Object[]{
                user.getNom(),user.getPrenom(),user.getLogin(),user.getPwd(),user.getFonction()
            });
        }
        // edit and delete user
        TableActionEvent eventuser=new TableActionEvent(){
            @Override
            public void onEdit(int row){
                Users user=new Users();
                user.setNom((String)userTab.getModel().getValueAt(row, 0));
                user.setPrenom((String)userTab.getModel().getValueAt(row, 1));
                user.setPwd((String)userTab.getModel().getValueAt(row, 3));
                user.setFonction((String)userTab.getModel().getValueAt(row, 4));
                userdao.editUser((String)userTab.getModel().getValueAt(row, 2), user);
                modeluser.fireTableDataChanged();
            }
    
            public void onDelete(int row){
                userdao.deleteUser((String)userTab.getModel().getValueAt(row, 2));
                ((DefaultTableModel) userTab.getModel()).removeRow(row);
                userTab.revalidate();
                userTab.repaint(); 
            }

            @Override
            public void onUpload(int row) {
            }
        };
        TableActionCellRender table=new TableActionCellRender();
        table.setI(1);
        TableActionCellEditor table_=new TableActionCellEditor(eventuser);
        table_.setI(1);
        userTab.getColumnModel().getColumn(5).setCellRenderer(table);
        userTab.getColumnModel().getColumn(5).setCellEditor(table_);
        
        // filtrer la table
        TableRowSorter<DefaultTableModel> sorter1 = new TableRowSorter<>(modeluser);
        userTab.setRowSorter(sorter1);

        // create a text field for the filter
        filterField1.setToolTipText("Filter");

        // add a listener to the filter field to update the filter when the text changes
        filterField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }

            private void updateFilter() {
                String text = filterField1.getText();
                if (text.trim().length() == 0) {
                    sorter1.setRowFilter(null);
                } else {
                    sorter1.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        
        
        
        // GESTION FAMILLES
        modelfam=(DefaultTableModel) famTab.getModel();
       
        // Set the column index for which the cell renderer will be set

TableColumn column = famTab.getColumnModel().getColumn(2);

// Set the cell renderer for the column
column.setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // If the value is a RowLabel, return the JLabel with the appropriate text
        if (value instanceof RowLabel) {
            RowLabel rowLabel = (RowLabel) value;
            JLabel label = new JLabel();
            label.setIcon(rowLabel.getRowIMG());
            return label;
        }
        // Otherwise, return the default renderer
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
});
// Add rows to the table
for(Famille fam:familledao.listerFamilles()){
    RowLabel rowLabel = new RowLabel(fam.getImg());
    modelfam.addRow(new Object[] { 
        fam.getId(),fam.getNomFam(), rowLabel 
    });
}


        // edit and delete famille
        TableActionEvent eventfam=new TableActionEvent(){
            @Override
            public void onEdit(int row){
                Famille famille=new Famille();
                RowLabel oldRowLabel = (RowLabel) modelfam.getValueAt(row, 2);
                famille.setNomFam((String)famTab.getModel().getValueAt(row, 1));
                famille.setImg(convertICONToBufferedIMG((ImageIcon)oldRowLabel.getIcon()));
                familledao.editFamille((int)famTab.getModel().getValueAt(row, 0), famille);
                modelfam.fireTableDataChanged();
            }
    
            public void onDelete(int row){
                familledao.deleteFamille((int)famTab.getModel().getValueAt(row, 0));
                ((DefaultTableModel) famTab.getModel()).removeRow(row);
                famTab.revalidate();
                famTab.repaint(); 
            }

            @Override
            public void onUpload(int row) {
                 ImageIcon scaledIcon=uploadIMG(); 
                // Get the existing RowLabel from the third row
                RowLabel oldRowLabel = (RowLabel) modelfam.getValueAt(row, 2);
                RowLabel newRowLabel = new RowLabel(convertICONToBufferedIMG(scaledIcon));

                // Set the new RowLabel in the third row
                modelfam.setValueAt(newRowLabel, row, 2);

                // Remove the old RowLabel from the table
                oldRowLabel.removeAll();
            }
        };
        famTab.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        famTab.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(eventfam));
        
                // filtrer la table
        TableRowSorter<DefaultTableModel> sorter3 = new TableRowSorter<>(modelfam);
        famTab.setRowSorter(sorter3);

        // create a text field for the filter
        filterField3.setToolTipText("Filter");

        // add a listener to the filter field to update the filter when the text changes
        filterField3.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }

            private void updateFilter() {
                String text = filterField3.getText();
                if (text.trim().length() == 0) {
                    sorter3.setRowFilter(null);
                } else {
                    sorter3.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        // GESTION DES ARTICLES
        modelart=(DefaultTableModel) artTab.getModel();
        // Set the column index for which the cell renderer will be set

TableColumn column1 = artTab.getColumnModel().getColumn(5);
        
        // Set the cell renderer for the column
column1.setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // If the value is a RowLabel, return the JLabel with the appropriate text
        if (value instanceof RowLabel) {
            RowLabel rowLabel = (RowLabel) value;
            JLabel label = new JLabel();
            label.setIcon(rowLabel.getRowIMG());
            
            return label;
        }
        // Otherwise, return the default renderer
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
});
// Add rows to the table
for(Article art:articledao.listerArticles()){
    RowLabel rowLabel = new RowLabel(art.getImg());
    modelart.addRow(new Object[] { 
           art.getId(),art.getDesignation(),art.getPrix(),art.getPesable(),art.getFamille(), rowLabel     
    });
}

        // edit and delete famille
        TableActionEvent eventart=new TableActionEvent(){
            @Override
            public void onEdit(int row){
                Article article=new Article();
                RowLabel oldRowLabel = (RowLabel) modelart.getValueAt(row, 5);
                article.setDesignation((String)artTab.getModel().getValueAt(row, 1));
                article.setPrix((double)artTab.getModel().getValueAt(row, 2));
                article.setPesable((String)artTab.getModel().getValueAt(row, 3));
                article.setImg(convertICONToBufferedIMG((ImageIcon)oldRowLabel.getIcon()));
                article.setFamille((int)artTab.getModel().getValueAt(row, 4));
                articledao.editArticle((String)artTab.getModel().getValueAt(row, 0), article);
                modelart.fireTableDataChanged();
            }
    
            public void onDelete(int row){
                articledao.deleteArticle((String)artTab.getModel().getValueAt(row, 0));
                ((DefaultTableModel) artTab.getModel()).removeRow(row);
                artTab.revalidate();
                artTab.repaint(); 
            }

            @Override
            public void onUpload(int row) {
                ImageIcon scaledIcon=uploadIMG(); 
                // Get the existing RowLabel from the third row
                RowLabel oldRowLabel = (RowLabel) modelart.getValueAt(row, 5);
                RowLabel newRowLabel = new RowLabel(convertICONToBufferedIMG(scaledIcon));

                // Set the new RowLabel in the third row
                modelart.setValueAt(newRowLabel, row, 5);

                // Remove the old RowLabel from the table
                oldRowLabel.removeAll();
            }
        };
        artTab.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        artTab.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(eventart));
 
                        // filtrer la table
        TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<>(modelart);
        artTab.setRowSorter(sorter2);

        // create a text field for the filter
        filterField2.setToolTipText("Filter");

        // add a listener to the filter field to update the filter when the text changes
        filterField2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }

            private void updateFilter() {
                String text = filterField2.getText();
                if (text.trim().length() == 0) {
                    sorter2.setRowFilter(null);
                } else {
                    sorter2.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
    }
    
    private byte[] convertIMGToByte(ImageIcon icon){
        BufferedImage bufferedImage = new BufferedImage(
        icon.getIconWidth(),
        icon.getIconHeight(),
        BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
                ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    private BufferedImage  convertICONToBufferedIMG(ImageIcon icon){
        Image img = icon.getImage();
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

    // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

    // Draw the image onto the buffered image
        Graphics2D g2d = bimage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bimage;
    }
    
    private File getExcelFile(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        File file = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }
        return file;
    }

    private ImageIcon uploadIMG(){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Image image = ImageIO.read(selectedFile);
                ImageIcon icon = new ImageIcon(image);
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                return scaledIcon;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        profil = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        deconnexion = new javax.swing.JButton();
        nomAdmin = new javax.swing.JLabel();
        prenomAdmin = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        editUser = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        nom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        prenom = new javax.swing.JTextField();
        login = new javax.swing.JTextField();
        pwd = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ajouterUser = new javax.swing.JButton();
        combobox = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        userTab = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        filterField1 = new javax.swing.JTextField();
        importUserExcel = new javax.swing.JButton();
        exportUserExcel = new javax.swing.JButton();
        editArt = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        designation = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        prix = new javax.swing.JTextField();
        famille = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ajouterArticle = new javax.swing.JButton();
        img = new javax.swing.JLabel();
        upload = new javax.swing.JButton();
        pesable = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        artTab = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        filterField2 = new javax.swing.JTextField();
        importArticleExcel = new javax.swing.JButton();
        exportArticleExcel = new javax.swing.JButton();
        EditFam = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        designation1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ajouterFamille = new javax.swing.JButton();
        img1 = new javax.swing.JLabel();
        upload1 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        famTab = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        filterField3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ADMINISTRATION");

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        profil.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel1.setText("NOM");

        jLabel11.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel11.setText("PRENOM");

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel12.setText("FONCTION");

        deconnexion.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        deconnexion.setText("DECONNEXION");
        deconnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deconnexionActionPerformed(evt);
            }
        });

        nomAdmin.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        nomAdmin.setForeground(new java.awt.Color(255, 102, 0));
        nomAdmin.setText("jLabel13");

        prenomAdmin.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        prenomAdmin.setForeground(new java.awt.Color(255, 102, 0));
        prenomAdmin.setText("jLabel14");

        jLabel15.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 102, 0));
        jLabel15.setText("ADMINISTRATEUR");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(47, 47, 47)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(prenomAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nomAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(106, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deconnexion, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nomAdmin))
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(prenomAdmin))
                .addGap(37, 37, 37)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel15))
                .addGap(41, 41, 41)
                .addComponent(deconnexion, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(111, Short.MAX_VALUE))
        );

        profil.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 500, 410));

        jTabbedPane1.addTab("PROFIL", profil);

        editUser.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBorder(null);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Nom");

        jLabel3.setText("Prenom");

        jLabel4.setText("Login");

        jLabel5.setText("Mot de passe");

        ajouterUser.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        ajouterUser.setText("Ajouter");
        ajouterUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterUserActionPerformed(evt);
            }
        });

        combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrateur", "Vendeur" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(nom, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(prenom, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(137, 137, 137)
                        .addComponent(jLabel5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pwd, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ajouterUser, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nom)
                    .addComponent(prenom)
                    .addComponent(ajouterUser, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(login)
                    .addComponent(pwd)
                    .addComponent(combobox, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 950, 90));

        userTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOM", "PRENOM", "NOM D'UTILISATEUR", "MOT DE PASSE", "FONCTION", "ACTION"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTab.setRowHeight(40);
        jScrollPane2.setViewportView(userTab);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 960, 300));

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel16.setText("Filtrer les utilisateurs");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 330, 30));

        filterField1.setForeground(new java.awt.Color(204, 204, 204));
        filterField1.setToolTipText("filtrer les utilisateurs");
        filterField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterField1ActionPerformed(evt);
            }
        });
        jPanel1.add(filterField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 320, 50));

        importUserExcel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        importUserExcel.setText("IMPORTER ");
        importUserExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importUserExcelActionPerformed(evt);
            }
        });
        jPanel1.add(importUserExcel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 160, 50));

        exportUserExcel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        exportUserExcel.setText("EXPORTER");
        exportUserExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportUserExcelActionPerformed(evt);
            }
        });
        jPanel1.add(exportUserExcel, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 160, 140, 50));

        jScrollPane1.setViewportView(jPanel1);

        editUser.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 660));

        jTabbedPane1.addTab("GESTION DES UTILISATEURS ", editUser);

        editArt.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane3.setBorder(null);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Designation");

        jLabel7.setText("Prix ");

        jLabel8.setText("Pesable");

        jLabel9.setText("Famille");

        ajouterArticle.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        ajouterArticle.setText("Ajouter");
        ajouterArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterArticleActionPerformed(evt);
            }
        });

        upload.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        upload.setText("<html><center>Telecharger <br>IMG");
        upload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadActionPerformed(evt);
            }
        });

        pesable.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KILOGRAMME", "UNITE" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 115, Short.MAX_VALUE))
                    .addComponent(designation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prix, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(pesable, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(famille, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(img, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(upload, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ajouterArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(upload, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ajouterArticle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(14, 14, 14)
                                        .addComponent(designation, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel7))
                                        .addGap(14, 14, 14)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(famille, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                                            .addComponent(prix)
                                            .addComponent(pesable)))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(img, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)))
                .addContainerGap())
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 980, 140));

        artTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DESIGNATION", "PRIX", "PESABLE", "FAMILLE", "IMAGE", "ACTION"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        artTab.setRowHeight(40);
        jScrollPane4.setViewportView(artTab);
        if (artTab.getColumnModel().getColumnCount() > 0) {
            artTab.getColumnModel().getColumn(0).setResizable(false);
            artTab.getColumnModel().getColumn(0).setPreferredWidth(5);
            artTab.getColumnModel().getColumn(1).setResizable(false);
            artTab.getColumnModel().getColumn(1).setPreferredWidth(200);
            artTab.getColumnModel().getColumn(2).setResizable(false);
            artTab.getColumnModel().getColumn(2).setPreferredWidth(10);
            artTab.getColumnModel().getColumn(3).setResizable(false);
            artTab.getColumnModel().getColumn(3).setPreferredWidth(10);
            artTab.getColumnModel().getColumn(4).setResizable(false);
            artTab.getColumnModel().getColumn(4).setPreferredWidth(20);
        }

        jPanel3.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 980, 360));

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel14.setText("Filtrer les articles");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 330, 30));

        filterField2.setForeground(new java.awt.Color(204, 204, 204));
        filterField2.setToolTipText("filtrer les articles");
        filterField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterField2ActionPerformed(evt);
            }
        });
        jPanel3.add(filterField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, 320, 50));

        importArticleExcel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        importArticleExcel.setText("IMPORTER");
        importArticleExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importArticleExcelActionPerformed(evt);
            }
        });
        jPanel3.add(importArticleExcel, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 190, 170, 50));

        exportArticleExcel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        exportArticleExcel.setText("EXPORTER");
        exportArticleExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportArticleExcelActionPerformed(evt);
            }
        });
        jPanel3.add(exportArticleExcel, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 190, 150, 50));

        jScrollPane3.setViewportView(jPanel3);

        editArt.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 660));

        jTabbedPane1.addTab("GESTION DES ARTICLES", editArt);

        EditFam.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane5.setBorder(null);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        designation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                designation1ActionPerformed(evt);
            }
        });

        jLabel10.setText("Nom Famille");

        ajouterFamille.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        ajouterFamille.setText("Ajouter");
        ajouterFamille.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterFamilleActionPerformed(evt);
            }
        });

        upload1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        upload1.setText("<html><center>Telecharger <br>IMG");
        upload1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upload1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(designation1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65)
                .addComponent(img1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addGap(59, 59, 59)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(upload1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ajouterFamille, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(110, 110, 110))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(upload1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ajouterFamille, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                    .addComponent(img1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(designation1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 130));

        famTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOM FAMILLE", "IMAGE", "ACTION"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        famTab.setRowHeight(40);
        jScrollPane6.setViewportView(famTab);
        if (famTab.getColumnModel().getColumnCount() > 0) {
            famTab.getColumnModel().getColumn(2).setResizable(false);
            famTab.getColumnModel().getColumn(2).setPreferredWidth(50);
            famTab.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanel5.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 860, 340));

        jLabel13.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel13.setText("Filtrer les familles");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 330, 30));

        filterField3.setForeground(new java.awt.Color(204, 204, 204));
        filterField3.setToolTipText("filtrer les familles");
        filterField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterField3ActionPerformed(evt);
            }
        });
        jPanel5.add(filterField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 320, 50));

        jScrollPane5.setViewportView(jPanel5);

        EditFam.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 5, 950, 660));

        jTabbedPane1.addTab("GESTION DES FAMILLES", EditFam);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1029, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void uploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadActionPerformed
        ImageIcon scaledIcon=uploadIMG();    
        img.setIcon(scaledIcon);
        article.setImg(convertICONToBufferedIMG(scaledIcon));
          

    }//GEN-LAST:event_uploadActionPerformed

    private void upload1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upload1ActionPerformed
                ImageIcon scaledIcon=uploadIMG(); 
                img1.setIcon(scaledIcon);
                fam.setImg(convertICONToBufferedIMG(scaledIcon));
        
    }//GEN-LAST:event_upload1ActionPerformed

    private void ajouterUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterUserActionPerformed
        if(((String) combobox.getSelectedItem()).equals("Administrateur"))
            f="admin";
        else
            f="vendeur";
        Users user=new Users();
        user.setNom(nom.getText());
        user.setPrenom(prenom.getText());
        user.setLogin(login.getText());
        user.setPwd(pwd.getText());
        user.setFonction(f);
        userdao.addUser(user);
        new Administration().setVisible(true);
        dispose();
    }//GEN-LAST:event_ajouterUserActionPerformed

    private void ajouterArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterArticleActionPerformed
        
        article.setDesignation(designation.getText());
        article.setPrix(Double.parseDouble(prix.getText()));
         if(((String) pesable.getSelectedItem()).equals("UNITE"))
            article.setPesable("unite");
        else
            article.setPesable("kg");
        article.setFamille(Integer.parseInt(famille.getText()));
        articledao.addArticle(article);
     /*   RowLabel rowLabel = new RowLabel(article.getImg());
        modelart.addRow(new Object[] { 
        article.getId(),article.getDesignation(),article.getPrixKg(),article.getPrixUn(),article.getFamille(), rowLabel 
        });
        artTab.repaint();*/
     new Administration().setVisible(true);
        dispose();
    }//GEN-LAST:event_ajouterArticleActionPerformed

    private void ajouterFamilleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterFamilleActionPerformed
       
        fam.setNomFam(designation1.getText());
        familledao.addFamille(fam);
        new Administration().setVisible(true);
        dispose();
    }//GEN-LAST:event_ajouterFamilleActionPerformed

    private void designation1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_designation1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_designation1ActionPerformed

    private void deconnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deconnexionActionPerformed
        new Login().setVisible(true);
        dispose();
    }//GEN-LAST:event_deconnexionActionPerformed

    private void filterField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterField3ActionPerformed

    private void filterField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterField2ActionPerformed

    private void filterField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterField1ActionPerformed

    private void importUserExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importUserExcelActionPerformed
         // Read the data from the selected file
            try (Workbook workbook = new XSSFWorkbook(getExcelFile())) {
                 Sheet sheet = workbook.getSheetAt(0);
                int rowIndex = 0;
                for (Row row : sheet) {
                    if (rowIndex == 0) { // skip the first row
                        rowIndex++;
                        continue;
                    }
                    Object[] rowData = new Object[5];
                    int i = 0;
                    for (Cell cell : row) {
                        rowData[i++] = cell.getStringCellValue();
                    }
                    modeluser.addRow(rowData);
                    Users user=new Users();
                    user.setNom((String) rowData[0]);
                    user.setPrenom((String) rowData[1]);
                    user.setLogin((String) rowData[2]);
                    user.setPwd((String) rowData[3]);
                    user.setFonction((String) rowData[4]);
                    userdao.addUser(user);
                    rowIndex++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }//GEN-LAST:event_importUserExcelActionPerformed

    private void importArticleExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importArticleExcelActionPerformed
             // Read the data from the selected file
            try (Workbook workbook = new XSSFWorkbook(getExcelFile())) {
                 Sheet sheet = workbook.getSheetAt(0);
                int rowIndex = 0;
                for (Row row : sheet) {
                    if (rowIndex == 0) { // skip the first row
                        rowIndex++;
                        continue;
                    }
                    Object[] rowData = new Object[5];
                    int i = 0;
                    for (Cell cell : row) {
                        if(i==0)
                        rowData[i++] = cell.getStringCellValue();
                        else
                        rowData[i++] = cell.getNumericCellValue();  
                    }
                    Article article=new Article();
                    article.setDesignation((String) rowData[0]);
                    article.setPrix((double) rowData[1]);
                    article.setPesable((String) rowData[2]);
                    article.setFamille((int)((double) rowData[3]));
                    article.setImg(ImageIO.read(new File("src/images/photo.png")));
                    articledao.addArticle(article);
                    rowIndex++;
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            new Administration().setVisible(true);
            dispose();
    }//GEN-LAST:event_importArticleExcelActionPerformed

    private void exportUserExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportUserExcelActionPerformed
            userdao.exportUser(this);
    }//GEN-LAST:event_exportUserExcelActionPerformed

    private void exportArticleExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportArticleExcelActionPerformed
            articledao.exportArticle(this);
    }//GEN-LAST:event_exportArticleExcelActionPerformed

   
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Administration().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel EditFam;
    private javax.swing.JButton ajouterArticle;
    private javax.swing.JButton ajouterFamille;
    private javax.swing.JButton ajouterUser;
    private javax.swing.JTable artTab;
    private javax.swing.JComboBox<String> combobox;
    private javax.swing.JButton deconnexion;
    private javax.swing.JTextField designation;
    private javax.swing.JTextField designation1;
    private javax.swing.JPanel editArt;
    private javax.swing.JPanel editUser;
    private javax.swing.JButton exportArticleExcel;
    private javax.swing.JButton exportUserExcel;
    private javax.swing.JTable famTab;
    private javax.swing.JTextField famille;
    private javax.swing.JTextField filterField1;
    private javax.swing.JTextField filterField2;
    private javax.swing.JTextField filterField3;
    private javax.swing.JLabel img;
    private javax.swing.JLabel img1;
    private javax.swing.JButton importArticleExcel;
    private javax.swing.JButton importUserExcel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField login;
    private javax.swing.JTextField nom;
    private javax.swing.JLabel nomAdmin;
    private javax.swing.JComboBox<String> pesable;
    private javax.swing.JTextField prenom;
    private javax.swing.JLabel prenomAdmin;
    private javax.swing.JTextField prix;
    private javax.swing.JPanel profil;
    private javax.swing.JTextField pwd;
    private javax.swing.JButton upload;
    private javax.swing.JButton upload1;
    private javax.swing.JTable userTab;
    // End of variables declaration//GEN-END:variables
}



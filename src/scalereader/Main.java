
package scalereader;

import tools.Scale;
import Beans.Article;
import Beans.Command;
import Beans.Famille;
import com.fazecast.jSerialComm.SerialPort;
import com.formdev.flatlaf.FlatDarculaLaf;
import dao.ArticleDao;
import dao.CommandDao;
import dao.DaoFactory;
import dao.FamilleDao;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import static tools.Scale.IS1INFO;
import static tools.Scale.MainDll;
import static tools.Scale.SENSORINFO;



/**
 *
 * @author User
 */
public class Main extends javax.swing.JFrame {

    DefaultTableModel model;
    List<Command> commands=new ArrayList<>();
    Article article;
    DaoFactory daoFactory = DaoFactory.getInstance();
    ArticleDao articleDao = daoFactory.getArticleDao();
    CommandDao commandDao = daoFactory.getCommandDao();
    FamilleDao familledao = daoFactory.getFamilleDao();
    
    public Main() {
        ExecutorService executor = Executors.newCachedThreadPool();       
        executor.submit(() -> {
            initComponents();
            model=(DefaultTableModel) jTable.getModel();
            this.setLocationRelativeTo(null);
            // Step 2: Create the CardLayout object and set it as the layout for the cards panel
            CardLayout cardLayout = (CardLayout) pnlCards.getLayout();
            for(Famille fam:familledao.listerFamilles()){               
                FamillePanel famillepanel=new FamillePanel(fam.getImg(),setTextButton(fam.getNomFam()));
                JPanel panel = new JPanel(new GridLayout(3, 3));
                
                // create the tabbed pane
                JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
                int i=0,a=1;
                for(Article articles:articleDao.listerArticles()){
                    if(articles.getFamille()==fam.getId()){
                        i++;
                            ArticlePanel articlepanel=new ArticlePanel(articles.getImg(),
                                    setArticleTitle(articles.getDesignation())
                                    ,String.valueOf(articles.getPrix()));
                            articlepanel.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                        // Perform your action here
                                    jNomArticle.setText(articles.getDesignation());
                                    article=articles;
                                    }
                            });
                            panel.add(articlepanel);  
                            
                            if(i%9==0){
                                tabbedPane.addTab("Page "+a, panel);
                                a++;
                                panel = new JPanel(new GridLayout(3, 3));
                            }
                        }
                }
                // Fill the empty cells with null components
                for (int j = panel.getComponentCount(); j < 9; j++) {
                    JLabel l=new JLabel();
                    l.setVisible(false);
                    panel.add(l);
                }
                tabbedPane.addTab("Page "+a, panel);
                String titre="Panel "+fam.getId();
                pnlCards.add(tabbedPane, titre);
                famillepanel.addMouseListener(new MouseAdapter() {
                     @Override
                    public void mouseClicked(MouseEvent e) {
                // Perform your action here
                        cardLayout.show(pnlCards, titre);
                        }
                    });
                jFamille.add(famillepanel);
            }
         }
    ); //inline
        executor.submit(() -> {
            MainDll.__SetCallBackFunc("SENSORINFO",SENSORINFO);
		MainDll.__SetCallBackFunc("IS1INFO",IS1INFO);
                if (MainDll.__Open("COM1", 9600));
                else
                    JOptionPane.showMessageDialog(null, "Port Non Ouvert", "Alert", JOptionPane.WARNING_MESSAGE);
                Scale.setTextField(jWeight);   
		while (true){
			MainDll.__DoEvents();
		}
        });
        executor.shutdown();    
            
    }
    
    public String setTextButton(String s){
        return "<html><center>"+s.replace(" ", "<br>");
    }
    
    public String setArticleTitle(String s){
        String input = s;
        String output = input.replaceAll("(.{10})\\s(.*)", "$1<br>$2");
        return "<html><center>"+output;

    }
    
    public ImageIcon setImage(String link){
            ImageIcon imageIcon = new ImageIcon(getClass().getResource(link)); // load the image to a imageIcon
            Image image = imageIcon.getImage(); // transform it 
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            return new ImageIcon(newimg);  // transform it back
    }
    public ImageIcon setImage(BufferedImage img){
            ImageIcon imageIcon = new ImageIcon(img);
            Image image = imageIcon.getImage(); // transform it 
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            return new ImageIcon(newimg);  // transform it back
    }
  /*  
    public void getWeight() {
    // ... code to open and configure the serial port ...
        SerialPort port = SerialPort.getCommPort("COM1"); // or whatever port name you're using
        port.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
        if (port.openPort()) {
            System.out.println("Port opened successfully");
        } else {
            System.err.println("Failed to open port");
           
        }
    SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
        @Override
        protected Void doInBackground() throws Exception {
            byte[] buffer = new byte[1024];
            int numRead;
            while (!isCancelled()) {
                numRead = port.readBytes(buffer, buffer.length);
                String data = new String(buffer, 0, numRead);

                String example = data.substring(data.lastIndexOf("S") + 1);
                String[] split = example.split("k");
                String value = split[0];
                publish(value); // send the value to the process() method
            }

            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String latestValue = chunks.get(chunks.size() - 1);
            jWeight.setText(latestValue);
            double poids=Double.parseDouble(latestValue) - Double.parseDouble(jTextTAR.getText());
            jTextPoids.setText(String.valueOf(poids));
        }
    };

    worker.execute(); // start the background thread
}
*/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        poidsbrut = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jNomArticle = new javax.swing.JTextField();
        jWeight = new javax.swing.JTextField();
        jFamille = new javax.swing.JPanel();
        pnlCards = new javax.swing.JPanel();
        jZero = new javax.swing.JButton();
        jTAR = new javax.swing.JButton();
        jHistory = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton0 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jQuantity = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jImprimer = new javax.swing.JButton();
        jSave = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        deconnexion = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        prixtotal = new javax.swing.JTextField();
        deleteArticle = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MENU PRINCIPAL");

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        poidsbrut.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        poidsbrut.setForeground(new java.awt.Color(255, 102, 0));
        poidsbrut.setText("Poids (Kg)");
        jPanel3.add(poidsbrut, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 102, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("ARTICLE ");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 180, 27));

        jNomArticle.setEditable(false);
        jNomArticle.setBackground(new java.awt.Color(255, 255, 255));
        jNomArticle.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jNomArticle.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel3.add(jNomArticle, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 470, 50));

        jWeight.setEditable(false);
        jWeight.setBackground(new java.awt.Color(255, 255, 255));
        jWeight.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jWeight.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jWeight.setText("0.00");
        jWeight.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jWeight.setEnabled(false);
        jWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWeightActionPerformed(evt);
            }
        });
        jPanel3.add(jWeight, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 250, 50));

        jFamille.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jFamille.setLayout(new java.awt.GridLayout(4, 1, 2, 2));
        jPanel3.add(jFamille, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 170, 380));
        jFamille.getAccessibleContext().setAccessibleName("Famille");

        pnlCards.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlCards.setAutoscrolls(true);
        pnlCards.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlCards.setLayout(new java.awt.CardLayout());
        jPanel3.add(pnlCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 200, 640, 380));

        jZero.setBackground(new java.awt.Color(255, 153, 0));
        jZero.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jZero.setForeground(new java.awt.Color(255, 255, 255));
        jZero.setText("ZERO");
        jZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jZeroActionPerformed(evt);
            }
        });
        jPanel3.add(jZero, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 80, 140, 50));

        jTAR.setBackground(new java.awt.Color(255, 153, 0));
        jTAR.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTAR.setForeground(new java.awt.Color(255, 255, 255));
        jTAR.setText("TAR");
        jTAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTARActionPerformed(evt);
            }
        });
        jPanel3.add(jTAR, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 20, 140, 50));

        jHistory.setBackground(new java.awt.Color(255, 204, 102));
        jHistory.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jHistory.setForeground(new java.awt.Color(255, 255, 255));
        jHistory.setText("HISTORIQUE");
        jHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHistoryActionPerformed(evt);
            }
        });
        jPanel3.add(jHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 470, 140, 40));

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton7.setBackground(new java.awt.Color(204, 204, 204));
        jButton7.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("<html><bold>7</bold>");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(204, 204, 204));
        jButton8.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("8");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(204, 204, 204));
        jButton9.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("9");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(204, 204, 204));
        jButton4.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(204, 204, 204));
        jButton6.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("6");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(204, 204, 204));
        jButton5.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton0.setBackground(new java.awt.Color(204, 204, 204));
        jButton0.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton0.setForeground(new java.awt.Color(255, 255, 255));
        jButton0.setText("0");
        jButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton0ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(204, 204, 204));
        jButton2.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(204, 204, 204));
        jButton3.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(204, 204, 204));
        jButton14.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton14.setForeground(new java.awt.Color(255, 255, 255));
        jButton14.setText("EFFACER");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jQuantity.setBackground(new java.awt.Color(255, 255, 255));
        jQuantity.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jQuantity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jQuantityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton0, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton0, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 290, -1, 280));

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Article", "Quantite/Poids", "Prix Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        }

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 10, 450, 194));

        jImprimer.setBackground(new java.awt.Color(255, 204, 51));
        jImprimer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jImprimer.setForeground(new java.awt.Color(255, 255, 255));
        jImprimer.setText("IMPRIMER");
        jImprimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImprimerActionPerformed(evt);
            }
        });
        jPanel3.add(jImprimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 410, 140, 40));

        jSave.setBackground(new java.awt.Color(255, 153, 0));
        jSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jSave.setForeground(new java.awt.Color(255, 255, 255));
        jSave.setText("ENREGISTRER");
        jSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSaveActionPerformed(evt);
            }
        });
        jPanel3.add(jSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 350, 140, 40));

        jButton10.setBackground(new java.awt.Color(255, 102, 0));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("VALIDER");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 290, 140, 40));

        deconnexion.setBackground(new java.awt.Color(255, 0, 0));
        deconnexion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deconnexion.setForeground(new java.awt.Color(255, 255, 255));
        deconnexion.setText("DECONNEXION");
        deconnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deconnexionActionPerformed(evt);
            }
        });
        jPanel3.add(deconnexion, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 530, 140, 40));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 102, 0));
        jLabel3.setText("PRIX TOTAL");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 230, 140, 40));

        prixtotal.setEditable(false);
        prixtotal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        prixtotal.setForeground(new java.awt.Color(255, 102, 0));
        prixtotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prixtotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prixtotalActionPerformed(evt);
            }
        });
        jPanel3.add(prixtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 220, 260, 50));

        deleteArticle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deleteArticle.setText("<html><center>SUPPRIMER<br>ARTICLE");
        deleteArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteArticleActionPerformed(evt);
            }
        });
        jPanel3.add(deleteArticle, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 130, 150, 60));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1334, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        jQuantity.setText(jQuantity.getText()+"7");                                           
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        jQuantity.setText(jQuantity.getText()+"9");                                           
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jQuantity.setText(jQuantity.getText()+"4");                                           
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jQuantity.setText(jQuantity.getText()+"6");                                           
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        jQuantity.setText(jQuantity.getText()+"5");                                           
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jQuantity.setText(jQuantity.getText()+"1");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton0ActionPerformed
        // TODO add your handling code here:
        jQuantity.setText(jQuantity.getText()+"0");
    }//GEN-LAST:event_jButton0ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jQuantity.setText(jQuantity.getText()+"2");    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jQuantity.setText(jQuantity.getText()+"3");                                           
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        jQuantity.setText("");                                           
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jQuantityActionPerformed

    private void jSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSaveActionPerformed
        if(commands.isEmpty()){
            JOptionPane.showMessageDialog(null, "Aucune commande entree", "Alert", JOptionPane.WARNING_MESSAGE);
        }
        else{
            String timeStamp=new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
            for(Command a:commands){
                a.setBarcode(timeStamp);
                //commandDao.ajouter(a);
            }
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();
            model.setRowCount(0);
            commands.clear();
            JOptionPane.showMessageDialog(null, "Votre commande est enregistree", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jSaveActionPerformed

    private void jImprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jImprimerActionPerformed
         if(commands.isEmpty()){
            JOptionPane.showMessageDialog(null, "Aucune commande entree", "Alert", JOptionPane.WARNING_MESSAGE);
        }
         else{
            try {
    // Load the report design
    JasperDesign jdesign = JRXmlLoader.load("src\\jasperreports\\report1.jrxml");

    // Set the query for the report
    JRDesignQuery updateQuery = new JRDesignQuery();
    updateQuery.setText("select * from historique");// where barcode='"+commands.get(0).getBarcode()+"'");
    jdesign.setQuery(updateQuery);

    // Compile the report
    JasperReport jreport = JasperCompileManager.compileReport(jdesign);

    // Fill the report with data
    JasperPrint jprint = JasperFillManager.fillReport(jreport, null, daoFactory.getConnection());

    // Send the report to the printer
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
    PrintService printService = null;
    for (PrintService service : services) {
        if (service.getName().equalsIgnoreCase("Microsoft Print to PDF")) {
            printService = service;
            break;
        }
    }
    if (printService == null) {
throw new RuntimeException("Printer not found");
    }

    printerJob.setPrintService(printService);
    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
    MediaSizeName mediaSizeName = MediaSize.findMedia(4, 4, MediaPrintableArea.INCH);
    printRequestAttributeSet.add(mediaSizeName);
    printRequestAttributeSet.add(new Copies(1));
    JRPrintServiceExporter exporter = new JRPrintServiceExporter();
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jprint);
    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService);
    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
    exporter.exportReport();
} catch (   PrinterException | RuntimeException | SQLException | JRException e) {
    e.printStackTrace();
}

    }//GEN-LAST:event_jImprimerActionPerformed
    }
    private void jHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHistoryActionPerformed
        HistoriqueUI historic=new HistoriqueUI();
        historic.setVisible(true);
    }//GEN-LAST:event_jHistoryActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        jQuantity.setText(jQuantity.getText()+"8");  
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWeightActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jWeightActionPerformed

    private void jTARActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTARActionPerformed
            if(MainDll.__Tare("1234"));
            else
                    JOptionPane.showMessageDialog(null, "TAR ECHEC !", "Alert", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jTARActionPerformed

    private void jZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jZeroActionPerformed
            if( MainDll.__Zero("1234"));
            else
                    JOptionPane.showMessageDialog(null, "ZERO ECHEC !", "Alert", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jZeroActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
            if(article==null){
            JOptionPane.showMessageDialog(null, "Choississez une article !", "Alert", JOptionPane.WARNING_MESSAGE);
        }
        else{
            Command command=new Command();
            
            command.setPesable(article.getPesable());
            command.setDesignation(jNomArticle.getText());
            boolean articleExists = false; // Flag to indicate if the article exists in the JTable

            if(!article.getPesable().equals("kg")){
                if(jQuantity.getText().length()>0){               
                    command.setQuantite(Integer.parseInt(jQuantity.getText()));
                }
                command.setPrix(command.getQuantite()*article.getPrix());
            }else{
                if(jQuantity.getText().length()>0)               
                    command.setQuantite(Double.parseDouble(jQuantity.getText()));
                else
                    command.setQuantite(Double.parseDouble(jWeight.getText()));
                
                command.setPrix(command.getQuantite()*article.getPrix());  
            }
            
            // Assuming that you have already populated the JTable with data

            // Iterate through all the rows of the JTable
            for (int i = 0; i < jTable.getRowCount(); i++) {
            // Check if the article exists in this row
                if (jTable.getValueAt(i, 0).equals(command.getDesignation())) {
            // Get the existing quantity and price values of the row
                    double existingQuantity = (int) jTable.getValueAt(i, 1);
            // Update the quantity and price values with the new values
                    double updatedQuantity = existingQuantity + command.getQuantite();
                    commands.get(i).setQuantite(updatedQuantity);
                    double updatedPrice = updatedQuantity*article.getPrix();
                    commands.get(i).setPrix(updatedPrice);
            // Set the new values in the JTable
                    if(!article.getPesable().equals("kg"))
                        jTable.setValueAt((int)updatedQuantity, i, 1);
                    else
                        jTable.setValueAt((double)updatedQuantity, i, 1);
                    jTable.setValueAt(updatedPrice, i, 2);
                            articleExists = true; // Set the flag to true since the article exists
            // Exit the loop since the article has been updated
                    break;
                }
            }
               if (!articleExists){
                    if(!article.getPesable().equals("kg"))
                        model.insertRow(model.getRowCount(),new Object[]{
                            command.getDesignation(),(int)command.getQuantite(),command.getPrix()
                        });
                    else
                        model.insertRow(model.getRowCount(),new Object[]{
                            command.getDesignation(),(double)command.getQuantite(),command.getPrix()
                        });
                    commands.add(command);
                }
        jQuantity.setText("");
        prixtotal.setText(String.format("%.2f", commands.stream().mapToDouble(e -> e.getPrix()).sum()));
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void deconnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deconnexionActionPerformed
        new Login().setVisible(true);
        dispose();
    }//GEN-LAST:event_deconnexionActionPerformed

    private void prixtotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prixtotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prixtotalActionPerformed

    private void deleteArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteArticleActionPerformed
        // Assuming that the JTable is already populated with data and a row is selected

        // Get the selected row index
        int selectedRowIndex = jTable.getSelectedRow();

        // Check if a row is actually selected
        if (selectedRowIndex != -1) {
    // Remove the selected row from the JTable
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();
            model.removeRow(selectedRowIndex);
            commands.remove(selectedRowIndex);
            prixtotal.setText(String.format("%.2f", commands.stream().mapToDouble(e -> e.getPrix()).sum()));

        }

    }//GEN-LAST:event_deleteArticleActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatDarculaLaf.registerCustomDefaultsSource("style");
        FlatDarculaLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deconnexion;
    private javax.swing.JButton deleteArticle;
    private javax.swing.JButton jButton0;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jFamille;
    private javax.swing.JButton jHistory;
    private javax.swing.JButton jImprimer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jNomArticle;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jQuantity;
    private javax.swing.JButton jSave;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jTAR;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jWeight;
    private javax.swing.JButton jZero;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JLabel poidsbrut;
    private javax.swing.JTextField prixtotal;
    // End of variables declaration//GEN-END:variables
}

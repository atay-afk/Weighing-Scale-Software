/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author ali
 */
public class Print {
    
    public static void print(JPanel panel){
        PrinterJob printjob= PrinterJob.getPrinterJob();
        printjob.setJobName("Ticket");
        Printable a=new Printable() {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum) throws PrinterException {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D graphics2D = (Graphics2D) pg;
                graphics2D.translate(pf.getImageableX() * 2, pf.getImageableY() * 2);
                graphics2D.scale(0.7,0.7);
                panel.print(graphics2D);
                
                return Printable.PAGE_EXISTS;
            }};

        PrintService[] printServices;
        PrintService printService;
 
        String printerName = "Microsoft Print to PDF"; //PROZT230

        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(printerName, null));
        printServices = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);


        try {
            printService = printServices[0];
            printjob.setPrintService(printService); // Try setting the printer you want
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null,"Error: No printer named '" + printerName + "', using default printer.");
            
        } catch (PrinterException exception) {
            JOptionPane.showMessageDialog(null,"Printing error: " + exception);
        }

        try {
                printjob.setPrintable(a,getPageFormat(printjob));
            printjob.setCopies(1); // preciser le nombre
            printjob.print(); // Actual print command

        } catch (PrinterException exception) {
            JOptionPane.showMessageDialog(null,"Printing error: " + exception);
        }


        //
    }

    public static PageFormat getPageFormat(PrinterJob pj)
    {

        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();
        paper.setSize(cm_to_pp(30),cm_to_pp(2));
        paper.setImageableArea(2,2,cm_to_pp(30),cm_to_pp(18));

        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);

        return pf;
    }

    protected static double cm_to_pp(double cm)
    {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch)
    {
        return inch * 72d;
    }
}

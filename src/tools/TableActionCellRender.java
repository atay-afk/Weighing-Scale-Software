/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author ali
 */
public class TableActionCellRender extends DefaultTableCellRenderer{
    int i=0;
    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean b1n,boolean b1n1,int i,int i1){
        Component com=super.getTableCellRendererComponent(jtable,o,b1n,b1n1,i,i1);
        
        PanelAction action=new PanelAction();
        if(this.i!=0)
        action.setUpload();

        return action;
    }

    public void setI(int i) {
        this.i = i;
    }
}

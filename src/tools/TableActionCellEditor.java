/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *
 * @author ali
 */
public class TableActionCellEditor extends DefaultCellEditor{
    private TableActionEvent event;
    int i=0;
    
    public TableActionCellEditor(TableActionEvent event){
        super(new JCheckBox());
        this.event=event;
    }
    public void setI(int i) {
        this.i = i;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable jtable,Object o,boolean b,int row,int clomun){
        PanelAction action=new PanelAction();
        if(this.i!=0)
        action.setUpload();

        action.initEvent(event, row);
        action.setBackground(jtable.getSelectionBackground());
        return action;
    }
}

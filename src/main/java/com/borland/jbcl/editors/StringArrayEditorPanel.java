/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.jbcl.editors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

public class StringArrayEditorPanel extends JPanel implements ActionListener, ListSelectionListener
{
  JTable   list   = new JTable();
  JScrollPane listPane = new JScrollPane();
  DefaultTableModel model = new DefaultTableModel();
  JButton add    = new JButton();
  JButton remove = new JButton();
  JButton clear  = new JButton();
  JButton up     = new JButton();
  JButton down   = new JButton();
  JLabel spacer = new JLabel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  public StringArrayEditorPanel() {
    super();
    jbInit();

    checkButtons();
  }
 private void jbInit() {
    this.setLayout(gridBagLayout2);
    DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
    TableColumn column = new TableColumn();
    column.setPreferredWidth(120);
    column.setModelIndex(0);
    column.setHeaderValue("");
    model.addColumn("column1"); 
    columnModel.addColumn(column);
    //list.setShowRollover(true);
    //list.setAutoInsert(false);
    list.setAutoCreateColumnsFromModel(false);
    list.setModel(model);
    list.setColumnModel(columnModel);
    list.getSelectionModel().addListSelectionListener(this);
    up.setText(Res._MoveUp);     
    up.addActionListener(this);
    down.setText(Res._MoveDown);     
    down.addActionListener(this);
    add.setText(Res._Add);     
    add.addActionListener(this);
    remove.setText(Res._Remove);     
    remove.addActionListener(this);
    clear.setText(Res._Clear);     
    clear.addActionListener(this);

    this.add(up, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(down, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    this.add(clear, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    this.add(remove, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    this.add(add, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    this.add(spacer, new GridBagConstraints(1, 2, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    this.add(listPane, new GridBagConstraints(0, 0, 1, 6, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), -300, -200));

    listPane.getViewport().add(list,null);
 }
 void setItems(String[] sa){
    list.getSelectionModel().removeListSelectionListener( this);
    if (sa != null) {
      int size = model.getRowCount();
      for (int i = 0; i < size;i++)
        model.removeRow(0);
      for (int i = 0; i < sa.length;i++)
        model.addRow(new String[] {sa[i]});
    }
    list.getSelectionModel().addListSelectionListener( this);
    checkButtons();
 }
  public void actionPerformed(ActionEvent e) {
    if (list.isEditing()){
         TableCellEditor editor = list.getCellEditor();
         editor.stopCellEditing();
    }
    Object src = e.getSource();
    int sf = list.getSelectedRow();
    int count = model.getRowCount();
    if (src == up) {
      if (count > 1 && sf > 0) {
        Object above = model.getValueAt(sf - 1,0);
        Object below = model.getValueAt(sf,0);
        model.setValueAt(above,sf,0);
        model.setValueAt(below,sf-1,0);
        list.setRowSelectionInterval(sf - 1,sf - 1);
      }
    }
    else if (src == down) {
      if (count > 1 && sf < count - 1) {
        Object below = model.getValueAt(sf + 1,0);
        Object above = model.getValueAt(sf ,0);
        model.setValueAt(below,sf,0);
        model.setValueAt(above,sf + 1,0);
        list.setRowSelectionInterval(sf + 1,sf + 1);
      }
    }
    else if (src == add) {
      //list.safeEndEdit(true);
      model.addRow(new String[] {java.text.MessageFormat.format(Res._ItemX, new Object[] {String.valueOf(count + 1)})});     
      list.setRowSelectionInterval(model.getRowCount() - 1,model.getRowCount() - 1);
      checkButtons();
    }
    else if (src == remove) {
      model.removeRow(sf);
      checkButtons();
    }
    else if (src == clear) {
      int rowCount = model.getRowCount();
      for (int i=0;i < rowCount;i++)
        model.removeRow(0);
      checkButtons();
    }
  }

  public Insets getInsets() {
    return new Insets(10, 10, 5, 10);
  }

  void checkButtons() {
    int count = model.getRowCount();
    int sf = list.getSelectedRow();
    if (count > 1) {
      if (sf == 0) {
        up.setEnabled(false);
        down.setEnabled(true);
      }
      else if (sf == count - 1) {
        up.setEnabled(true);
        down.setEnabled(false);
      }
      else {
        up.setEnabled(true);
        down.setEnabled(true);
      }
    }
    else {
      up.setEnabled(false);
      down.setEnabled(false);
    }
    if (count > 0) {
      remove.setEnabled(true);
      clear.setEnabled(true);
    }
    else {
      remove.setEnabled(false);
      clear.setEnabled(false);
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    checkButtons();
  }
}

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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class String2DArrayEditorPanel extends JComponent implements ActionListener,ListSelectionListener
{
  JTable   grid   = new JTable();
  JScrollPane    gridPane = new JScrollPane();
  JPanel         butPan = new JPanel();
  JPanel         rowPan = new JPanel();
  JPanel         colPan = new JPanel();
  JButton addRow = new JButton();
  JButton remRow = new JButton();
  JButton addCol = new JButton();
  JButton remCol = new JButton();
  JButton clear  = new JButton();
  JLabel         label  = new JLabel();

  public String2DArrayEditorPanel() {
    super();
    JPanel panel = new JPanel();
    //panel.setLayout(new com.borland.jbcl.layout.VerticalFlowLayout());
    panel.add(butPan);
    butPan.setLayout(new GridLayout(0, 1, 10, 10));
    butPan.add(addRow);
    butPan.add(remRow);
    butPan.add(clear);
    butPan.add(addCol);
    butPan.add(remCol);
    butPan.add(rowPan);
    butPan.add(colPan);
    this.setLayout(new BorderLayout(10, 10));
    gridPane.getViewport().add(grid, null);
    this.add(gridPane, BorderLayout.CENTER);
    this.add(panel, BorderLayout.EAST);
    this.add(label, BorderLayout.NORTH);

    addRow.setText(Res._AddRow);     
    addRow.addActionListener(this);
    remRow.setText(Res._RemoveRow);     
    remRow.addActionListener(this);
    clear.setText(Res._Clear);     
    clear.addActionListener(this);
    addCol.setText(Res._AddColumn);     
    addCol.addActionListener(this);
    remCol.setText(Res._RemoveColumn);     
    remCol.addActionListener(this);
    label.setText(Res._GridDirections);     
    grid.setAutoCreateColumnsFromModel(false);
    grid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    grid.getSelectionModel().addListSelectionListener(this);

    //grid.setAutoAppend(true);   // users want to add rows from keyboard alone
  }

  public void actionPerformed(ActionEvent e) {
    Object src = e. getSource();
    if (grid.isEditing()){
         TableCellEditor editor = grid.getCellEditor();
         editor.stopCellEditing();
         //grid.removeEditor();
      }
    int row = grid.getSelectedRow();
    int column = grid.getSelectedColumn();
    if (src == addRow) {

      ((DefaultTableModel)grid.getModel()).addRow(new String[grid.getColumnCount()] );
    }
    else if (src == remRow && row >= 0) {
      int rows = grid.getRowCount();
      if (rows > 1) {
        ((DefaultTableModel)grid.getModel()).removeRow(row);
        if (row == rows-1)
          row--;
        grid.getSelectionModel().setSelectionInterval(row,row);
      }
    }
    else if (src == clear) {
      ((DefaultTableModel)grid.getModel()).setNumRows(1);
      grid.setColumnModel(new DefaultTableColumnModel( ));
      TableColumn tableColumn = new TableColumn();
      tableColumn.setModelIndex(0);
      tableColumn.setPreferredWidth(75);
      tableColumn.setHeaderValue("");
      grid.addColumn(tableColumn);
      grid.getSelectionModel().setSelectionInterval(row-1,row-1);
    }
    else if (src == addCol) {
      int count = grid.getModel().getColumnCount();
      ((DefaultTableModel)grid.getModel()).addColumn("column" + (count + 1));
      TableColumn tableColumn = new TableColumn();
      tableColumn.setModelIndex(count);
      tableColumn.setPreferredWidth(75);
      tableColumn.setHeaderValue("");
      grid.addColumn(tableColumn);
    }
    else if (src == remCol && column >= 0) {
      int cols = grid.getColumnCount();
      if (cols > 1) {

        grid.removeColumn(grid.getColumnModel().getColumn(column));

        if (column == cols-1)
          column--;
        grid.setColumnSelectionInterval(column,column);
      }
    }

  }
  public void setItems(String[][] items) {
     int rowCount = 1;
     int colCount = 1;
     if (items != null && items.length> 0){
       rowCount = items.length;
       if (items[0] != null && items[0].length > 0)
         colCount = items[0].length;
     }

     String[] columns = new String[colCount];
     DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
     grid.setColumnModel(columnModel);
     for (int i=0;i<colCount;i++) {
       columns[i] = "column" + (i + 1); 
       TableColumn column = new TableColumn();
       column.setModelIndex(i);
       column.setHeaderValue("");
       column.setPreferredWidth(75);
       columnModel.addColumn(column);
     }

     grid.setModel(new DefaultTableModel(items,columns));

  }
  public String[][] getItems() {
    TableModel model = grid.getModel();
    int rowCount = model.getRowCount();
    TableColumnModel columnModel = grid.getColumnModel();
    int colCount = columnModel.getColumnCount();
    String[][] items = new String[rowCount][colCount];


    for (int col =0; col < colCount;col++){
      TableColumn tableColumn = columnModel.getColumn(col);
      int dataCol = tableColumn.getModelIndex();
      for (int row = 0; row < rowCount;row++)

        items[row][col] = (String)model.getValueAt(row,dataCol);
    }
    return items;
  }
  public Insets getInsets() {
    return new Insets(10, 10, 5, 10);
  }
  public void valueChanged(ListSelectionEvent e) {
    boolean delOK = grid.  getSelectedRow() >= 0  && grid.getSelectedColumn() >=0;
      remCol.setEnabled(delOK);
      remRow.setEnabled(delOK);
  }
}

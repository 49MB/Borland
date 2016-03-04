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
package com.borland.jbcl.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jb.util.EventMulticaster;

/**
 * BasicMatrixContainer is a basic implementation of the WritableMatrixModel interface.
 */
public class BasicMatrixContainer implements WritableMatrixModel, Serializable
{
  private static final long serialVersionUID = 200L;

  public BasicMatrixContainer() {}

  public BasicMatrixContainer(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
    items = new Object[rows][columns];
  }

  public BasicMatrixContainer(Object[][] newItems) {
    rows = newItems.length;
    columns = rows > 0 ? newItems[0].length : 0;
    items = newItems;
  }

  public Object[][] getItems() { return items; }
  public void setItems(Object[][] newItems) {
    rows = newItems.length;
    columns = rows > 0 ? newItems[0].length : 0;
    items = newItems;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
  }

  // MatrixModel implementation

  public Object get(int row, int column) {
    if (row >= 0 && row < rows && column >= 0 && column < columns)
      return items[row][column];
    return null;
  }

  public MatrixLocation find(Object data) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < items[r].length; c++) {
        Object itemData = items[r][c];
        if (itemData == data || itemData != null && itemData.equals(data))
          return new MatrixLocation(r,c);
      }
    }
    return null;
  }

  public int getRowCount() { return rows; }
  public void setRowCount(int rowCount) {
    if (!variableRows || rowCount == rows)
      throw new IllegalStateException(Res._NoVariableRows);     
    Object[][] newItems = new Object[rowCount][columns];
    int min = rowCount < rows ? rowCount : rows;
    for (int r = 0; r < min; r++)
      System.arraycopy(items[r], 0, newItems[r], 0, items[r].length);
    rows = rowCount;
    items = newItems;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
  }

  public int getColumnCount() { return columns; }
  public void setColumnCount(int columnCount) {
    if (!variableColumns || columnCount == columns)
      throw new IllegalStateException(Res._NoVariableColumns);     
    Object[][] newItems = new Object[rows][columnCount];
    int min = columnCount < columns ? columnCount : columns;
    for (int r = 0; r < rows; r++)
      System.arraycopy(items[r], 0, newItems[r], 0, min);
    columns = columnCount;
    items = newItems;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
  }

  public void addModelListener(MatrixModelListener listener) { modelListeners.add(listener); }
  public void removeModelListener(MatrixModelListener listener) { modelListeners.remove(listener); }

  // WritableMatrixModel implementation

  public boolean canSet(int row, int column, boolean startEdit) {
    return (row >= 0 && row < rows && column >= 0 && column < columns);
  }

  public void set(int row, int column, Object data) {
    if (row >= 0 && row < rows && column >= 0 && column < columns) {
      items[row][column] = data;
      processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.ITEM_CHANGED, new MatrixLocation(row, column)));
    }
  }

  public void touched(int row, int column) {
    if (row >= 0 && row < rows && column >= 0 && column < columns)
      processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.ITEM_TOUCHED, new MatrixLocation(row, column)));
  }

  public boolean isVariableRows() { return variableRows; }
  public void setVariableRows(boolean variable) { variableRows = variable; }

  public void addRow() {
    if (!variableRows)
      throw new IllegalStateException(Res._NoVariableRows);     
    Object[][] newItems = new Object[rows + 1][columns];
    for (int r = 0; r < rows; r++)
      System.arraycopy(items[r], 0, newItems[r], 0, items[r].length);
    items = newItems;
    rows++;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.ROW_ADDED, new MatrixLocation(rows - 1, 0)));
  }

  public void addRow(int aheadOf) {
    if (!variableRows)
      throw new IllegalStateException(Res._NoVariableRows);     
    Object[][] newItems = new Object[rows + 1][columns];
    if (aheadOf == 0)
      for (int r = 0; r < rows; r++)
        System.arraycopy(items[r], 0, newItems[r+1], 0, items[r].length);
    else if (aheadOf >= 0 && aheadOf < rows) {
      for (int r = 0; r < aheadOf; r++)
        System.arraycopy(items[r], 0, newItems[r], 0, items[r].length);
      for (int r = aheadOf; r < rows; r++)
        System.arraycopy(items[r], 0, newItems[r+1], 0, items[r].length);
    }
    else
      return;
    items = newItems;
    rows++;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.ROW_ADDED, new MatrixLocation(aheadOf, 0)));
  }

  public void removeRow(int row) {
    if (!variableRows)
      throw new IllegalStateException(Res._NoVariableRows);     
    Object[][] newItems = new Object[rows - 1][columns];
    if (row == 0)
      for (int r = 0; r < rows - 1; r++)
        System.arraycopy(items[r+1], 0, newItems[r], 0, items[r+1].length);
    else if (row >= 0 && row < rows) {
      for (int r = 0; r < row; r++)
        System.arraycopy(items[r], 0, newItems[r], 0, items[r].length);
      for (int r = row; r < rows - 1; r++)
        System.arraycopy(items[r+1], 0, newItems[r], 0, items[r+1].length);
    }
    else
      return;
    items = newItems;
    rows--;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.ROW_REMOVED, new MatrixLocation(row, 0)));
  }

  public void removeAllRows() {
    if (!variableRows)
      throw new IllegalStateException(Res._NoVariableRows);     
    Object[][] newItems = new Object[0][columns];
    items = newItems;
    rows = 0;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
  }

  public boolean isVariableColumns() { return variableColumns; }
  public void setVariableColumns(boolean variable) { variableColumns = variable; }

  public void addColumn() {
    if (!variableColumns)
      throw new IllegalStateException(Res._NoVariableColumns);     
    Object[][] newItems = new Object[rows][columns + 1];
    for (int r = 0; r < rows; r++)
      newItems[r] = appendColumn(items[r]);
    items = newItems;
    columns++;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.COLUMN_ADDED, new MatrixLocation(0, columns - 1)));
  }

  public void addColumn(int aheadOf) {
    if (!variableColumns)
      throw new IllegalStateException(Res._NoVariableColumns);     
    if (aheadOf >= 0 && aheadOf < columns) {
      Object[][] newItems = new Object[rows][columns + 1];
      for (int r = 0; r < rows; r++)
        newItems[r] = insertColumn(items[r], aheadOf);
      items = newItems;
      columns++;
      processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.COLUMN_ADDED, new MatrixLocation(0, aheadOf)));
    }
  }

  public void removeColumn(int column) {
    if (!variableColumns)
      throw new IllegalStateException(Res._NoVariableColumns);     
    if (column >= 0 && column < columns) {
      Object[][] newItems = new Object[rows][columns - 1];
      for (int r = 0; r < rows; r++)
        newItems[r] = deleteColumn(items[r], column);
      items = newItems;
      columns--;
      processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.COLUMN_REMOVED, new MatrixLocation(0, column)));
    }
  }

  public void removeAllColumns() {
    if (!variableColumns)
      throw new IllegalStateException(Res._NoVariableColumns);     
    Object[][] newItems = new Object[rows][0];
    items = newItems;
    columns = 0;
    processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
  }

  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
    }
  }

  // Matrix Model Events

  protected void processModelEvent(MatrixModelEvent e) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(e);
  }

  private Object[] insertColumn(Object[] row, int col) {
    Object[] newRow = new Object[row.length + 1];
    if (col == 0)
      System.arraycopy(row, 0, newRow, 1, row.length);
    else if (col == row.length) {
      System.arraycopy(row, 0, newRow, 0, row.length);
    }
    else {
      System.arraycopy(row, 0, newRow, 0, col);
      System.arraycopy(row, col, newRow, col + 1, row.length - col);
    }
    return newRow;
  }

  private Object[] appendColumn(Object[] row) {
    Object[] newRow = new Object[row.length + 1];
    System.arraycopy(row, 0, newRow, 0, row.length);
    return newRow;
  }

  private Object[] deleteColumn(Object[] row, int col) {
    Object[] newRow = new Object[row.length - 1];
    if (col == 0)
      System.arraycopy(row, 1, newRow, 0, row.length - 1);
    else if (col == newRow.length) {
      System.arraycopy(row, 0, newRow, 0, newRow.length);
    }
    else {
      System.arraycopy(row, 0, newRow, 0, col);
      System.arraycopy(row, col + 1, newRow, col, newRow.length - col);
    }
    return newRow;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Object[][] sItems = new Object[rows][columns];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        if (items[r][c] instanceof Serializable)
          sItems[r][c] = items[r][c];
      }
    }
    s.writeObject(sItems);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    items = (Object[][])s.readObject();
  }

  private transient Object[][] items = new Object[0][0];
  private int rows;
  private int columns;
  private boolean variableRows = true;
  private boolean variableColumns = true;
  private transient EventMulticaster modelListeners = new EventMulticaster();
  private boolean events = true;
}

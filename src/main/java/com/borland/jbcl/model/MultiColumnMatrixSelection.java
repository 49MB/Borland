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

import java.io.Serializable;
import java.util.Vector;

import com.borland.jb.util.EventMulticaster;

/**
 * MultiColumnMatrixSelection implements WritableMatrixSelection allowing multiple
 * columns to be selected at a time.
 */
public class MultiColumnMatrixSelection implements WritableMatrixSelection, Serializable
{
  public MultiColumnMatrixSelection() {}
  public MultiColumnMatrixSelection(int maxRows) { rows = maxRows; }
  public MultiColumnMatrixSelection(int maxRows, int[] columns) {
    rows  = maxRows;
    for (int i = 0; i < columns.length; i++) {
      Integer integer = new Integer(columns[i]);
      if (!cellColumns.contains(integer))
        cellColumns.addElement(integer);
    }
  }
  public MultiColumnMatrixSelection(int maxRows, MatrixLocation[] selection) {
    rows  = maxRows;
    for (int i = 0; i < selection.length; i++) {
      Integer integer = new Integer(selection[i].column);
      if (!cellColumns.contains(integer))
        cellColumns.addElement(integer);
    }
  }

  public int getMaxRows() { return rows; }
  public void setMaxRows(int maxRows) {
    rows = maxRows;
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // MatrixSelection Implementation

  public boolean contains(MatrixLocation location) {
    return contains(location.row, location.column);
  }
  public boolean contains(int row, int column) {
    Integer integer = new Integer(column);
    return cellColumns.contains(integer);
  }

  public int getCount() {
    return cellColumns.size() * rows;
  }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[cellColumns.size() * rows];
    int i = 0;
    for (int c = 0; c < cellColumns.size() ; c++)
      for (int r = 0; r < rows; r++)
        contents[i++] = new MatrixLocation(r, ((Integer)cellColumns.elementAt(c)).intValue());
    return contents;
  }

  public void addSelectionListener(MatrixSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(MatrixSelectionListener listener) {
    selectionListeners.remove(listener);
  }

  // WritableMatrixSelection Implementation

  public void set(MatrixLocation[] locations) {
    cellColumns.removeAllElements();
    add(locations);
  }

  public void add(MatrixLocation location) {
    add(location.row, location.column);
  }

  public void add(int row, int column) {
    Integer integer = new Integer(column);
    if (!cellColumns.contains(integer)) {
      cellColumns.addElement(integer);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(MatrixLocation[] locations) {
    boolean changed = false;
    for (int i = 0; i < locations.length; i++) {
      Integer integer = new Integer(locations[i].column);
      if (!cellColumns.contains(integer)) {
        changed = true;
        cellColumns.addElement(integer);
      }
    }
    if (changed) {
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void addRange(MatrixLocation begin, MatrixLocation end) {
    addRange(begin.row, begin.column, end.row, end.column);
  }

  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    int loCol = beginColumn < endColumn ? beginColumn : endColumn;
    int hiCol = beginColumn > endColumn ? beginColumn : endColumn;
    boolean changed = false;
    for (int c = loCol; c <= hiCol; c++) {
      Integer integer = new Integer(c);
      if (!cellColumns.contains(integer)) {
        changed = true;
        cellColumns.addElement(integer);
      }
    }
    if (changed) {
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation location) {
    remove(location.row, location.column);
  }

  public void remove(int row, int column) {
    Integer integer = new Integer(column);
    if (cellColumns.contains(integer)) {
      cellColumns.removeElement(integer);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation[] locations) {
    boolean changed = false;
    for (int i = 0; i < locations.length; i++) {
      Integer integer = new Integer(locations[i].column);
      if (cellColumns.contains(integer)) {
        changed = true;
        cellColumns.removeElement(integer);
      }
    }
    if (changed) {
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void removeRange(MatrixLocation begin, MatrixLocation end) {
    removeRange(begin.row, begin.column, end.row, end.column);
  }

  public void removeRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    int loCol = beginColumn < endColumn ? beginColumn : endColumn;
    int hiCol = beginColumn > endColumn ? beginColumn : endColumn;
    boolean changed = false;
    for (int c = loCol; c <= hiCol; c++) {
      Integer integer = new Integer(c);
      if (cellColumns.contains(integer)) {
        changed = true;
        cellColumns.removeElement(integer);
      }
    }
    if (changed) {
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void removeAll() {
    if (cellColumns.size() > 0) {
      cellColumns.removeAllElements();
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void enableSelectionEvents(boolean enable) {
    if (events = enable)
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // Matrix Selection Events

  protected void processSelectionEvent(MatrixSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

  private Vector cellColumns = new Vector();
  private int rows;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

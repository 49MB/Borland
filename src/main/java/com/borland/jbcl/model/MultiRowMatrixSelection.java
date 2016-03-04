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
 * MultiRowMatrixSelection implements WritableMatrixSelection allowing multiple
 * entire rows to be selected at a time.
 */
public class MultiRowMatrixSelection implements WritableMatrixSelection, Serializable
{
  public MultiRowMatrixSelection() {}
  public MultiRowMatrixSelection(int maxColumns) { columns = maxColumns; }
  public MultiRowMatrixSelection(int maxColumns, int[] rows) {
    columns  = maxColumns;
    for (int i = 0; i < rows.length; i++) {
      Integer integer = new Integer(rows[i]);
      if (!cellRows.contains(integer))
        cellRows.addElement(integer);
    }
  }
  public MultiRowMatrixSelection(int maxColumns, MatrixLocation[] selection) {
    columns  = maxColumns;
    for (int i = 0; i < selection.length; i++) {
      Integer integer = new Integer(selection[i].row);
      if (!cellRows.contains(integer))
        cellRows.addElement(integer);
    }
  }

  public int getMaxColumns() { return columns; }
  public void setMaxColumns(int maxColumns) {
    columns = maxColumns;
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // MatrixSelection Implementation

  public boolean contains(MatrixLocation location) {
    return contains(location.row, location.column);
  }
  public boolean contains(int row, int column) {
    Integer integer = new Integer(row);
    return cellRows.contains(integer);
  }

  public int getCount() {
    return cellRows.size() * columns;
  }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[cellRows.size() * columns];
    int i = 0;
    for (int r = 0; r < cellRows.size() ; r++)
      for (int c = 0; c < columns; c++)
        contents[i++] = new MatrixLocation(((Integer)cellRows.elementAt(r)).intValue(), c);
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
    cellRows.removeAllElements();
    add(locations);
  }

  public void add(MatrixLocation location) {
    add(location.row, location.column);
  }

  public void add(int row, int column) {
    Integer integer = new Integer(row);
    if (!cellRows.contains(integer)) {
      cellRows.addElement(integer);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(MatrixLocation[] locations) {
    boolean changed = false;
    for (int i = 0; i < locations.length; i++) {
      Integer integer = new Integer(locations[i].row);
      if (!cellRows.contains(integer)) {
        changed = true;
        cellRows.addElement(integer);
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
    int loRow = beginRow < endRow ? beginRow : endRow;
    int hiRow = beginRow > endRow ? beginRow : endRow;
    boolean changed = false;
    for (int r = loRow; r <= hiRow; r++) {
      Integer integer = new Integer(r);
      if (!cellRows.contains(integer)) {
        changed = true;
        cellRows.addElement(integer);
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
    Integer integer = new Integer(row);
    if (cellRows.contains(integer)) {
      cellRows.removeElement(integer);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation[] locations) {
    boolean changed = false;
    for (int i = 0; i < locations.length; i++) {
      Integer integer = new Integer(locations[i].row);
      if (cellRows.contains(integer)) {
        changed = true;
        cellRows.removeElement(integer);
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
    int loRow = beginRow < endRow ? beginRow : endRow;
    int hiRow = beginRow > endRow ? beginRow : endRow;
    boolean changed = false;
    for (int r = loRow; r <= hiRow; r++) {
      Integer integer = new Integer(r);
      if (cellRows.contains(integer)) {
        changed = true;
        cellRows.removeElement(integer);
      }
    }
    if (changed) {
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void removeAll() {
    if (cellRows.size() > 0) {
      cellRows.removeAllElements();
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

  private Vector cellRows = new Vector();
  private int columns;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

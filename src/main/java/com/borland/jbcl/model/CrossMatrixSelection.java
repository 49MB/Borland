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

import com.borland.jb.util.EventMulticaster;

/**
 * CrossMatrixSelection implements WritableMatrixSelection allowing a single
 * row and column to be selected at a time.  (Crosshairs)
 */
public class CrossMatrixSelection implements WritableMatrixSelection, Serializable
{
  public CrossMatrixSelection() {}
  public CrossMatrixSelection(int maxRows, int maxColumns) {
    rows    = maxRows;
    columns = maxColumns;
  }
  public CrossMatrixSelection(int maxRows, int maxColumns, MatrixLocation focus) {
    this(maxRows, maxColumns);
    cell = focus;
    checkValid();
  }

  public int getMaxRows() { return rows; }
  public void setMaxRows(int maxRows) {
    rows = maxRows;
    checkValid();
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public int getMaxColumns() { return columns; }
  public void setMaxColumns(int maxColumns) {
    columns = maxColumns;
    checkValid();
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  private void checkValid() {
    valid = rows > 0 && columns > 0 && cell != null;
  }

  // MatrixSelection Implementation

  public boolean contains(MatrixLocation location) {
    return valid ? location.row == cell.row || location.column == cell.column : false;
  }
  public boolean contains(int row, int column) {
    return valid ? row == cell.row || column == cell.column : false;
  }

  public int getCount() {
    return valid ? rows + columns - 1 : 0;
  }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[valid ? rows + columns - 1 : 0];
    if (valid) {
      int i = 0;
      for (int r = 0 ; r < rows ; r++)
        contents[i++] = new MatrixLocation(r, cell.column);
      for (int c = 0 ; c < columns ; c++) {
        if (c != cell.column)
          contents[i++] = new MatrixLocation(cell.row, c);
      }
    }
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
    add(locations);
  }

  public void add(MatrixLocation location) {
    if (location != null && !location.equals(cell)) {
      cell = location;
      checkValid();
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(int row, int column) {
    if (row != cell.row || column != cell.column) {
      cell = new MatrixLocation(row, column);
      checkValid();
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(MatrixLocation[] locations) {
    if (locations.length > 0) {
      if (!locations[0].equals(cell)) {
        cell = locations[0];
        checkValid();
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void addRange(MatrixLocation begin, MatrixLocation end) {
    if (end != null && !end.equals(cell)) {
      cell = end;
      checkValid();
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    if (endRow != cell.row || endColumn != cell.column) {
      cell = new MatrixLocation(endRow, endColumn);
      checkValid();
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation location) {
    if (valid && location != null && location.equals(cell)) {
      cell = null;
      valid = false;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(int row, int column) {
    if (valid && row == cell.row && column == cell.column) {
      cell = null;
      valid = false;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation[] locations) {
    if (valid && locations.length > 0) {
      for (int i = 0 ; i < locations.length ; i++ ) {
        if (locations[i] != null && locations[i].equals(cell)) {
          cell = null;
          valid = false;
          processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
          return;
        }
      }
    }
  }

  public void removeRange(MatrixLocation begin, MatrixLocation end) {
    removeRange(begin.row, begin.column, end.row, end.column);
  }

  public void removeRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    if (valid) {
      if ((cell.row >= beginRow && cell.row <= endRow) ||
          (cell.column >= beginColumn && cell.column <= endColumn)) {
        cell = null;
        valid = false;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void removeAll() {
    if (valid) {
      cell = null;
      valid = false;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void enableSelectionEvents(boolean enable) {
    if (events = enable)
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  protected void processSelectionEvent(MatrixSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

  private MatrixLocation   cell;
  private int              rows;
  private int              columns;
  private boolean          valid;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

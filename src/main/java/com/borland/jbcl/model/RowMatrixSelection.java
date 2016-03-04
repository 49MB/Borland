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
 * RowMatrixSelection implements WritableMatrixSelection allowing a single
 * row to be selected at a time.
 */
public class RowMatrixSelection implements WritableMatrixSelection, Serializable
{
  public RowMatrixSelection() {}
  public RowMatrixSelection(int maxColumns) { columns = maxColumns; }
  public RowMatrixSelection(int maxColumns, int row) {
    columns = maxColumns;
    cellRow = row;
  }

  public int getMaxColumns() { return columns; }
  public void setMaxColumns(int maxColumns) {
    columns = maxColumns;
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // MatrixSelection Implementation

  public boolean contains(MatrixLocation location) {
    return cellRow != -1 ? location.row == cellRow : false;
  }
  public boolean contains(int row, int column) {
    return cellRow != -1 ? row == cellRow : false;
  }

  public int getCount() { return cellRow != -1 ? columns : 0; }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[cellRow != -1 ? columns : 0];
    if (cellRow != -1)
      for (int i = 0 ; i < contents.length ; i++) {
        contents[i] = new MatrixLocation(cellRow, i);
    }
    return contents;
  }

  public void addSelectionListener(MatrixSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(MatrixSelectionListener listener) {
    selectionListeners.remove(listener);
  }

//  public void enableSelectionEvents(boolean enable) { events = enable; }

  // WritableMatrixSelection Implementation

  public void set(MatrixLocation[] locations) {
    add(locations);
  }

  public void add(MatrixLocation location) {
    if (location.row != cellRow) {
      cellRow = location.row;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(int row, int column) {
    if (row != cellRow) {
      cellRow = row;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(MatrixLocation[] locations) {
    if (locations.length > 0) {
      if (locations[0].row != cellRow) {
        cellRow = locations[0].row;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void addRange(MatrixLocation begin, MatrixLocation end) {
    if (end.row != cellRow) {
      cellRow = end.row;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    if (cellRow != endRow) {
      cellRow = endRow;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation location) {
    if (cellRow != -1) {
      if (location.row == cellRow) {
        cellRow = -1;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void remove(int row, int column) {
    if (cellRow != -1) {
      if (row == cellRow) {
        cellRow = -1;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void remove(MatrixLocation[] locations) {
    if (cellRow != -1) {
      for (int i = 0 ; i < locations.length ; i++ ) {
        if (locations[i].row == cellRow) {
          cellRow = -1;
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
    if (cellRow != -1) {
      if (cellRow >= beginRow && cellRow <= endRow) {
        cellRow = -1;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void removeAll() {
    if (cellRow != -1) {
      cellRow = -1;
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

  private int cellRow = -1;
  private int columns;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

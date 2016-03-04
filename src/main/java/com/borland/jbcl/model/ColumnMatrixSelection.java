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
 * ColumnMatrixSelection implements WritableMatrixSelection allowing a single
 * column to be selected at a time.
 */
public class ColumnMatrixSelection implements WritableMatrixSelection, Serializable
{
  public ColumnMatrixSelection() {}
  public ColumnMatrixSelection(int maxRows) { rows = maxRows; }
  public ColumnMatrixSelection(int maxRows, int column) {
    rows = maxRows;
    cellColumn = column;
  }

  public int getMaxRows() { return rows; }
  public void setMaxRows(int maxRows) {
    rows = maxRows;
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // MatrixSelection Implementation

  public boolean contains(MatrixLocation location) {
    return cellColumn != -1 ? location.column == cellColumn : false;
  }
  public boolean contains(int row, int column) {
    return cellColumn != -1 ? column == cellColumn : false;
  }

  public int getCount() { return cellColumn != -1 ? rows : 0; }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[cellColumn != -1 ? rows : 0];
    if (cellColumn != -1)
      for (int i = 0 ; i < rows ; i++)
        contents[i] = new MatrixLocation(i, cellColumn);
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
    if (location.column != cellColumn) {
      cellColumn = location.column;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(int row, int column) {
    if (column != cellColumn) {
      cellColumn = column;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(MatrixLocation[] locations) {
    if (locations.length > 0) {
      if (locations[0].column != cellColumn) {
        cellColumn = locations[0].column;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void addRange(MatrixLocation begin, MatrixLocation end) {
    if (end.column != cellColumn) {
      cellColumn = end.column;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    if (cellColumn != endColumn) {
      cellColumn = endColumn;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation location) {
    if (cellColumn != -1) {
      if (location.column == cellColumn) {
        cellColumn = -1;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void remove(int row, int column) {
    if (cellColumn != -1) {
      if (column == cellColumn) {
        cellColumn = -1;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void remove(MatrixLocation[] locations) {
    if (cellColumn != -1) {
      for (int i = 0 ; i < locations.length ; i++ ) {
        if (locations[i].column == cellColumn) {
          cellColumn = -1;
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
    if (cellColumn != -1) {
      if (cellColumn >= beginColumn && cellColumn <= endColumn) {
        cellColumn = -1;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void removeAll() {
    if (cellColumn != -1) {
      cellColumn = -1;
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

  private int cellColumn = -1;
  private int rows;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

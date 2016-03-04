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
 * SingleMatrixSelection implements WritableMatrixSelection allowing a single
 * selection at a time.
 */
public class SingleMatrixSelection implements WritableMatrixSelection, Serializable
{
  public SingleMatrixSelection() {}
  public SingleMatrixSelection(MatrixLocation location) { cell = location; }

  // MatrixSelection Implementation

  public boolean contains(MatrixLocation location) {
    return location.equals(cell);
  }
  public boolean contains(int row, int column) {
    if (cell != null)
      return cell.row == row && cell.column == column;
    else
      return false;
  }

  public int getCount() { return cell != null ? 1 : 0; }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[cell != null ? 1 : 0];
    if (cell != null)
      contents[0] = cell;
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
    if (!location.equals(cell)) {
      cell = new MatrixLocation(location);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(int row, int column) {
    if (cell != null) {
      if (cell.row != row || cell.column != column) {
        cell.row    = row;
        cell.column = column;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
    else {
      cell = new MatrixLocation(row, column);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(MatrixLocation[] locations) {
    if (locations.length > 0) {
      if (!locations[0].equals(cell)) {
        cell = new MatrixLocation(locations[0]);
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void addRange(MatrixLocation begin, MatrixLocation end) {
    if (!end.equals(cell)) {
      cell = new MatrixLocation(end);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    if (cell != null) {
      if (cell.row != endRow || cell.column != endColumn) {
        cell.row    = endRow;
        cell.column = endColumn;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
    else {
      cell = new MatrixLocation(endRow, endColumn);
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(MatrixLocation location) {
    if (location.equals(cell)) {
      cell = null;
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(int row, int column) {
    if (cell != null) {
      if (cell.row == row && cell.column == column) {
        cell = null;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void remove(MatrixLocation[] locations) {
    if (cell != null) {
      for (int i = 0 ; i < locations.length ; i++ ) {
        if (locations[i].equals(cell)) {
          cell = null;
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
    if (cell != null) {
      if (cell.row    >= beginRow    && cell.row    <= endRow &&
          cell.column >= beginColumn && cell.column <= endColumn) {
        cell = null;
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void removeAll() {
    if (cell != null) {
      cell = null;
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

  public final String toString() {
    String cn = getClass().getName();
    return cn.substring(cn.lastIndexOf('.')+1) +  "[" + paramString() + "]"; 
  }

  protected String paramString() {
    return "cell=" + cell; 
  }

  private MatrixLocation cell;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

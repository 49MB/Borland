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

/**
 * BasicMatrixSelection implements a basic WritableMatrixSelection providing multiselect support
 */
public class BasicMatrixSelection extends BasicSelection implements WritableMatrixSelection, Serializable
{
  public BasicMatrixSelection() {}
  public BasicMatrixSelection(MatrixLocation[] newSet) { set(newSet); }

  // MatrixSelection implementation

  public boolean contains(MatrixLocation location) { return array.contains(location); }
  public boolean contains(int row, int column) { return contains(new MatrixLocation(row, column)); }

  public MatrixLocation[] getAll() {
    MatrixLocation[] contents = new MatrixLocation[getCount()];
    array.copyInto(contents);
    return contents;
  }

  public void addSelectionListener(MatrixSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(MatrixSelectionListener listener) {
    selectionListeners.remove(listener);
  }

  // WritableMatrixSelection implementation

  public void set(MatrixLocation[] locations) {
    doRemoveAll();
    for (int i = 0 ; i < locations.length ; i++)
      array.addElement(locations[i]);
    processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void add(MatrixLocation location) {
    if (doAdd(location))
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.ITEM_ADDED, location));
  }

  public void add(int row, int column) {
    if (doAdd(new MatrixLocation(row, column)))
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.ITEM_ADDED, new MatrixLocation(row, column)));
  }

  public void add(MatrixLocation[] locations) {
    boolean changed = false;
    for (int i = 0 ; i < locations.length ; i++)
      changed |= doAdd(locations[i]);
    if (changed)
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void addRange(MatrixLocation begin, MatrixLocation end) {
    addRange(begin.row, begin.column, end.row, end.column);
  }

  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    int loRow = beginRow < endRow ? beginRow : endRow;
    int hiRow = beginRow > endRow ? beginRow : endRow;
    int loCol = beginColumn < endColumn ? beginColumn : endColumn;
    int hiCol = beginColumn > endColumn ? beginColumn : endColumn;
    boolean changed = false;
    for (int row = loRow ; row <= hiRow ; row++)
      for (int column = loCol ; column <= hiCol ; column++)
        changed |= doAdd(new MatrixLocation(row, column));
    if (changed)
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.RANGE_ADDED,
                                                     new MatrixLocation(loRow, loCol),
                                                     new MatrixLocation(hiRow, hiCol)));
  }

  public void remove(MatrixLocation location) {
    if (doRemove(location))
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.ITEM_REMOVED, location));
  }

  public void remove(int row, int column) {
    if (doRemove(new MatrixLocation(row, column)))
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.ITEM_REMOVED, new MatrixLocation(row, column)));
  }

  public void remove(MatrixLocation[] locations) {
    boolean changed = false;
    for (int i = 0 ; i < locations.length ; i++ )
      changed |= doRemove(locations[i]);
    if (changed)
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void removeRange(MatrixLocation begin, MatrixLocation end) {
    removeRange(begin.row, begin.column, end.row, end.column);
  }

  public void removeRange(int beginRow, int beginColumn, int endRow, int endColumn) {
    int loRow = beginRow < endRow ? beginRow : endRow;
    int hiRow = beginRow > endRow ? beginRow : endRow;
    int loCol = beginColumn < endColumn ? beginColumn : endColumn;
    int hiCol = beginColumn > endColumn ? beginColumn : endColumn;
    boolean changed = false;
    for (int row = loRow ; row <= hiRow ; row++)
      for (int col = loCol ; col <= hiCol ; col++)
        changed |= doRemove(new MatrixLocation(row, col));
    if (changed)
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.RANGE_REMOVED,
                                                     new MatrixLocation(loRow, loCol),
                                                     new MatrixLocation(hiRow, hiCol)));
  }

  public void removeAll() {
    if (doRemoveAll())
      processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
  }

  public void enableSelectionEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (events)
        processSelectionEvent(new MatrixSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  // Matrix Selection Events

  protected void processSelectionEvent(MatrixSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

}

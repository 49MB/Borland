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
 * AbstractMatrixModel is an abstract implementation of the MatrixModel interface.
 * It provides the event management for a MatrixModel implementor.  To create a fully functional
 * read-only MatrixModel, subclass AbstractMatrixModel, and implement these methods:<BR>
 * <UL>
 *  <LI>public Object get(int row, int column)
 *  <LI>public int getRowCount()
 *  <LI>public int getColumnCount()
 * </UL>
 * To create a fully functional read/write MatrixModel (WritableMatrixModel), be sure to add
 * 'implements WritableMatrixModel' to your class definition, and add the following methods:<BR>
 * <UL>
 *  <LI>public void set(int row, int column, Object data)
 *  <LI>public void addRow()
 *  <LI>public void addRow(int aheadOf)
 *  <LI>public void removeRow(int row)
 *  <LI>public void removeAllRows()
 *  <LI>public void addColumn()
 *  <LI>public void addColumn(int aheadOf)
 *  <LI>public void removeColumn(int column)
 *  <LI>public void removeAllColumns()
 * </UL>
 */
public abstract class AbstractMatrixModel implements MatrixModel, Serializable
{
  public AbstractMatrixModel() {}

  /**
   * By default, the AbstractMatrixModel cannot find items.  To add find functionality,
   * override this method and search for the passed data object in your matrix data structure.
   */
  public MatrixLocation find(Object data) {
    return null;
  }

  /**
   * By default, the AbstractMatrixModel simply notifies listeners when an item is touched.
   */
  public void touched(int row, int column) {
    fireItemTouched(row, column);
  }

  /**
   * By default, the AbstractMatrixModel allows setting of all items (if it is used as
   * a WritableMatrixModel.  To restrict setting of items, override this method, and return
   * false when applicable.
   */
  public boolean canSet(int row, int column, boolean startEdit) {
    return true;
  }

  /**
   * By default, the AbstractMatrixModel allows adding/removing of rows (if it is used as
   * a WritableMatrixModel.  To restrict adding/removing of rows, override this method, and
   * return false when applicable.
   */
  public boolean isVariableRows() {
    return true;
  }

  /**
   * By default, the AbstractMatrixModel allows adding/removing of columns (if it is used as
   * a WritableMatrixModel.  To restrict adding/removing of columns, override this method, and
   * return false when applicable.
   */
  public boolean isVariableColumns() {
    return true;
  }

  // Matrix Model Events

  public void addModelListener(MatrixModelListener listener) { modelListeners.add(listener); }
  public void removeModelListener(MatrixModelListener listener) { modelListeners.remove(listener); }

  /**
   * By default, the AbstractMatrixModel will turn events on and off, and fire a structure changed
   * event when they are turned back on.  To change this behavior, override this method.
   */
  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        fireStructureChanged();
    }
  }

  /**
   * Fire this event when the contents of the entire matrix have changed - but the row count and
   * column count have not changed.
   */
  protected void fireContentChanged() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.CONTENT_CHANGED));
  }

  /**
   * Fire this event when the entire matrix has changed - including changes to row and column
   * counts.  This notifies the listeners to re-analyze the model.
   */
  protected void fireStructureChanged() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.STRUCTURE_CHANGED));
  }

  /**
   * Fire this event when an item in the matrix has changed.
   */
  protected void fireItemChanged(int row, int column) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.ITEM_CHANGED, new MatrixLocation(row, column)));
  }

  /**
   * Fire this event when an item in the matrix has been touched.
   */
  protected void fireItemTouched(int row, int column) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.ITEM_TOUCHED, new MatrixLocation(row, column)));
  }

  /**
   * Fire this event when an entire row in the matrix has changed.
   */
  protected void fireRowChanged(int row) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.ROW_CHANGED, new MatrixLocation(row, -1)));
  }

  /**
   * Fire this event when a new row is added to the matrix.
   */
  protected void fireRowAdded(int row) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.ROW_ADDED, new MatrixLocation(row, -1)));
  }

  /**
   * Fire this event when a row is removed from the matrix.
   */
  protected void fireRowRemoved(int row) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.ROW_REMOVED, new MatrixLocation(row, -1)));
  }

  /**
   * Fire this event when an entire column in the matrix has changed.
   */
  protected void fireColumnChanged(int column) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.COLUMN_CHANGED, new MatrixLocation(-1, column)));
  }

  /**
   * Fire this event when a new column is added to the matrix.
   */
  protected void fireColumnAdded(int column) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.COLUMN_ADDED, new MatrixLocation(-1, column)));
  }

  /**
   * Fire this event when a column is removed from the matrix.
   */
  protected void fireColumnRemoved(int column) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new MatrixModelEvent(this, MatrixModelEvent.COLUMN_REMOVED, new MatrixLocation(-1, column)));
  }

  private transient EventMulticaster modelListeners = new EventMulticaster();
  private boolean events = true;
}

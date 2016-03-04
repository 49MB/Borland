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

/**
 * The WritableMatrixModel interface supplies read/write access
 * to two-dimensional (matrix) data.
 */
public interface WritableMatrixModel extends MatrixModel
{
  /**
   * Returns true if model can set data object at row and column.
   * @param row The row address of the data object.
   * @param column The column address of the data object.
   * @param startEdit Indicates intent to start editing.
   * @return True if the data object can be modified, false if not.
   */
  public boolean canSet(int row, int column, boolean startEdit);

  /**
   * Sets data object at row and column.
   * @param row The row address of the data object to set.
   * @param column The column address of the data object to set.
   * @param data The new data object to put into the container.
   */
  public void set(int row, int column, Object data);

  /**
   * Notify all model listeners that the data object at the
   * specified row and column was touched.
   * @param row The row address of the data object that was touched.
   * @param column The column address of the data object that was touched.
   */
  public void touched(int row, int column);

  /**
   * Returns true if model can insert or remove entire rows.
   * @return True if this container supports row inserting/removing.
   */
  public boolean isVariableRows();

  /**
   * Adds a new row after last row.
   */
  public void addRow();

  /**
   * Adds a new row before specified row.
   * @param aheadOf The row address to insert the row before.
   */
  public void addRow(int aheadOf);

  /**
   * Removes a row.
   * @param row The row to remove.
   */
  public void removeRow(int row);

  /**
   * Removes all rows.
   */
  public void removeAllRows();

  /**
   * Returns true if model can insert or remove entire columns.
   * @return True if this container supports column inserting/removing.
   */
  public boolean isVariableColumns();

  /**
   * Adds a new column after last column.
   */
  public void addColumn();

  /**
   * Adds a new column before specified column.
   * @param aheadOf The column address to insert the column before.
   */
  public void addColumn(int aheadOf);

  /**
   * Removes a column.
   * @param column The column to remove.
   */
  public void removeColumn(int column);

  /**
   * Removes all columns.
   */
  public void removeAllColumns();

  /**
   * Enables / disables event broadcasting.
   * @param enable If false, events are disabled.
   * If true, events are enabled, and a STRUCTURE_CHANGED
   * event is fired to all model listeners.
   */
  public void enableModelEvents(boolean enable);
}

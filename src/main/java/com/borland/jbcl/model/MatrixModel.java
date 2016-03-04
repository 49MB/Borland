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
 * The MatrixModel interface supplies read access to two-dimensional (matrix) data.
 */
public interface MatrixModel
{
  /**
   * Returns data object at row and column.
   * @param row The row address of the data object to get.
   * @param column The column address of the data object to get.
   * @return The requested data object.
   */
  public Object get(int row, int column);

  /**
   * Returns the storage location of the passed data object.
   * @param data The data object to search this container for.
   * @return The address of the requested data object, or null if not found.
   */
  public MatrixLocation find(Object data);

  /**
   * Returns count of rows.
   * @return The number of rows in this container.
   */
  public int getRowCount();

  /**
   * Returns count of columns.
   * @return The number of columns in this container.
   */
  public int getColumnCount();

  /**
   * Adds a model event listener to this model.
   * @param listener The MatrixModelListener to add.
   */
  public void addModelListener(MatrixModelListener listener);

  /**
   * Removes a model event listener from this model.
   * @param listener The MatrixModelListener to remove.
   */
  public void removeModelListener(MatrixModelListener listener);
}

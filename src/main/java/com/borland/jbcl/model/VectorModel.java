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
 * The VectorModel interface supplies read access to one-dimensional (vector) data.
 */
public interface VectorModel
{
  /**
   * Returns data object at index.
   * @param index The zero based index of the object to retrieve.
   * @return The data object requested.
   */
  public Object get(int index);

  /**
   * Returns count of data objects.
   * @return The count of data objects in this container.
   */
  public int getCount();

  /**
   * Returns the storage index of the passed data object.
   * @param data The data object to search for in this container.
   * @return The zero based index of the object (if found). If not, it returns -1.
   */
  public int find(Object data);

  /**
   * Adds a model event listener to this model.
   * @param listener The VectorModelListener to add.
   */
  public void addModelListener(VectorModelListener listener);

  /**
   * Removes a model event listener from this model.
   * @param listener The VectorModelListener to remove.
   */
  public void removeModelListener(VectorModelListener listener);
}

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
 * The WritableVectorModel interface supplies read/write access
 * to one-dimensional (vector) data.
 */
public interface WritableVectorModel extends VectorModel
{
  /**
   * Returns true if model can set data object at index.
   * @param index The zero based index to test.
   * @param startEdit Indicates intent to start editing.
   * @return True if the data object can be modified, false if not.
   */
  public boolean canSet(int index, boolean startEditing);

  /**
   * Sets data object at index.
   * @param index The storage index to set.
   * @param data The data object to store.
   */
  public void set(int index, Object data);

  /**
   * Notify all model listeners that the data object at the
   * passed index was touched.
   * @param index The storage index that was touched
   */
  public void touched(int index);

  /**
   * Returns true if model can insert (or remove) data objects.
   * @return True if this container supports adding/removing of items, false if not.
   */
  public boolean isVariableSize();

  /**
   * Adds a new data object after last index.
   * @param data The data object to store.
   */
  public void addItem(Object data);

  /**
   * Adds a new data object before specified index.
   * @param aheadOf The storage index to insert data item before.
   * @param data The data object to store.
   */
  public void addItem(int aheadOf, Object data);

  /**
   * Removes a data object at index.
   * @param index The storage index to remove.
   */
  public void remove(int index);

  /**
   * Removes all data objects from this container.
   */
  public void removeAll();

  /**
   * Enables / disables event broadcasting.
   * @param enable If false, events are disabled.
   * If true, events are enabled, and a STRUCTURE_CHANGED
   * event is fired to all model listeners.
   */
  public void enableModelEvents(boolean enable);
}

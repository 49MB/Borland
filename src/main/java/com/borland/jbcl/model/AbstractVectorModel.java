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
 * AbstractVectorModel is an abstract implementation of the VectorModel interface.
 * It provides the event management for a VectorModel implementor.  To create a fully functional
 * read-only VectorModel, subclass AbstractVectorModel, and implement these methods:<BR>
 * <UL>
 *  <LI>public Object get(int index)
 *  <LI>public int getCount()
 * </UL>
 * To create a fully functional read/write VectorModel (WritableVectorModel), be sure to add
 * 'implements WritableVectorModel' to your class definition, and add the following methods:<BR>
 * <UL>
 *  <LI>public void set(int index, Object data)
 *  <LI>public void addItem()
 *  <LI>public void addItem(int aheadOf)
 *  <LI>public void remove(int index)
 *  <LI>public void removeAll()
 * </UL>
 */
public abstract class AbstractVectorModel implements VectorModel, Serializable
{
  public AbstractVectorModel() {}

  /**
   * By default, the AbstractVectorModel cannot find items.  To add find functionality,
   * override this method and search for the passed data object in your Vector data structure.
   */
  public int find(Object data) {
    return -1;
  }

  /**
   * By default, the AbstractVectorModel simply notifies listeners when an item is touched.
   */
  public void touched(int index) {
    fireItemTouched(index);
  }

  /**
   * By default, the AbstractVectorModel allows setting of all items (if it is used as
   * a WritableVectorModel.  To restrict setting of items, override this method, and return
   * false when applicable.
   */
  public boolean canSet(int index, boolean startEdit) {
    return true;
  }

  /**
   * By default, the AbstractVectorModel allows adding/removing of items (if it is used as
   * a WritableVectorModel.  To restrict adding/removing of items, override this method, and
   * return false when applicable.
   */
  public boolean isVariableSize() {
    return true;
  }

  // Vector Model Events

  public void addModelListener(VectorModelListener listener) { modelListeners.add(listener); }
  public void removeModelListener(VectorModelListener listener) { modelListeners.remove(listener); }

  /**
   * By default, the AbstractVectorModel will turn events on and off, and fire a structure changed
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
   * Fire this event when the contents of the entire Vector have changed - but total count of
   * items has not changed.
   */
  protected void fireContentChanged() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new VectorModelEvent(this, VectorModelEvent.CONTENT_CHANGED));
  }

  /**
   * Fire this event when the entire Vector has changed - including change to total item count.
   */
  protected void fireStructureChanged() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new VectorModelEvent(this, VectorModelEvent.STRUCTURE_CHANGED));
  }

  /**
   * Fire this event when an item in the Vector has changed.
   */
  protected void fireItemChanged(int index) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new VectorModelEvent(this, VectorModelEvent.ITEM_CHANGED, index));
  }

  /**
   * Fire this event when an item in the Vector has been touched.
   */
  protected void fireItemTouched(int index) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new VectorModelEvent(this, VectorModelEvent.ITEM_TOUCHED, index));
  }

  /**
   * Fire this event when a new item is added to the Vector.
   */
  protected void fireItemAdded(int index) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new VectorModelEvent(this, VectorModelEvent.ITEM_ADDED, index));
  }

  /**
   * Fire this event when a item is removed from the Vector.
   */
  protected void fireItemRemoved(int index) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new VectorModelEvent(this, VectorModelEvent.ITEM_REMOVED, index));
  }

  private transient EventMulticaster modelListeners = new EventMulticaster();
  private boolean events = true;
}

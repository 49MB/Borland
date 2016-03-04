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
 * AbstractGraphModel is an abstract implementation of the GraphModel interface.
 * It provides the event management for a GraphModel implementor. To create a
 * fully functional read-only GraphModel, subclass AbstractGraphModel, and
 * implement these methods:<BR>
 * <UL>
 * <LI>public GraphLocation getRoot()
 * <LI>public Object get(GraphLocation node)
 * </UL>
 * To create a fully functional read/write GraphModel (WritableGraphModel), be
 * sure to add 'implements WritableGraphModel' to your class definition, and add
 * the following methods:<BR>
 * <UL>
 * <LI>public GraphLocation setRoot(Object data)
 * <LI>public void set(GraphLocation node, Object data)
 * <LI>public GraphLocation addChild(GraphLocation parent, Object data)
 * <LI>public GraphLocation addChild(GraphLocation parent, GraphLocation
 * aheadOf, Object data)
 * <LI>public void removeChildren(GraphLocation parent)
 * <LI>public void remove(GraphLocation node)
 * <LI>public void removeAll()
 * </UL>
 */

@SuppressWarnings("serial")
public abstract class AbstractGraphModel implements GraphModel, Serializable {
  public AbstractGraphModel() {
  }
  
  /**
   * By default, the AbstractGraphModel cannot find items. To add find
   * functionality, override this method and search for the passed data object
   * in your graph data structure.
   */
  public GraphLocation find(Object data) {
    return null;
  }
  
  /**
   * By default, the AbstractGraphModel simply notifies listeners when an item
   * is touched.
   */
  public void touched(GraphLocation node) {
    fireItemTouched(node);
  }
  
  /**
   * By default, the AbstractGraphModel allows setting of all items (if it is
   * used as a WritableGraphModel. To restrict setting of items, override this
   * method, and return false when applicable.
   */
  public boolean canSet(GraphLocation node) {
    return true;
  }
  
  /**
   * By default, the AbstractGraphModel allows adding/removing of items (if it
   * is used as a WritableGraphModel. To restrict adding/removing of items,
   * override this method, and return false when applicable.
   */
  public boolean isVariableSize() {
    return true;
  }
  
  // Graph Model Events
  
  public void addModelListener(GraphModelListener listener) {
    modelListeners.add(listener);
  }
  
  public void removeModelListener(GraphModelListener listener) {
    modelListeners.remove(listener);
  }
  
  /**
   * By default, the AbstractGraphModel will turn events on and off, and fire a
   * structure changed event when they are turned back on. To change this
   * behavior, override this method.
   */
  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        fireStructureChanged();
    }
  }
  
  /**
   * Fire this event when the contents of the entire graph have changed - but
   * the item count has not changed.
   */
  protected void fireContentChanged() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.CONTENT_CHANGED));
  }
  
  /**
   * Fire this event when the entire graph has changed - including changes to
   * the node count. This notifies the listeners to re-analyze the model.
   */
  protected void fireStructureChanged() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.STRUCTURE_CHANGED));
  }
  
  /**
   * Fire this event when an item in the graph has changed.
   */
  protected void fireItemChanged(GraphLocation node) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.ITEM_CHANGED, node));
  }
  
  /**
   * Fire this event when an item in the graph has been touched.
   */
  protected void fireItemTouched(GraphLocation node) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.ITEM_TOUCHED, node));
  }
  
  /**
   * Fire this event when a new item is added to the graph.
   */
  protected void fireNodeAdded(GraphLocation node) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.NODE_ADDED, node));
  }
  
  /**
   * Fire this event when a item is removed from the graph.
   */
  protected void fireNodeRemoved(GraphLocation node) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.NODE_REMOVED, node));
  }
  
  /**
   * Fire this event when an entire item in the graph has been replaced.
   */
  protected void fireNodeReplaced(GraphLocation node) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(new GraphModelEvent(this,
          GraphModelEvent.NODE_REPLACED, node));
  }
  
  private transient EventMulticaster modelListeners = new EventMulticaster();
  private boolean events = true;
}

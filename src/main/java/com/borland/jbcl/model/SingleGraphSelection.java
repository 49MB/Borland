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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jb.util.EventMulticaster;

/**
 * SingleGraphSelection implements WritableGraphSelection allowing 1 item to be selected.
 */
public class SingleGraphSelection implements WritableGraphSelection, Serializable
{
  private static final long serialVersionUID = 200L;

  public SingleGraphSelection() {}
  public SingleGraphSelection(GraphLocation newItem) { item = newItem; }

  // GraphSelection Implementation

  public boolean contains(GraphLocation location) { return location.equals(item); }

  public int getCount() { return item != null ? 1 : 0; }

  public GraphLocation[] getAll() {
    GraphLocation[] contents = new GraphLocation[item != null ? 1 : 0];
    if (item != null)
      contents[0] = item;
    return contents;
  }

  public void addSelectionListener(GraphSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(GraphSelectionListener listener) {
    selectionListeners.remove(listener);
  }

//  public void enableSelectionEvents(boolean enable) { events = enable; }

  // WritableGraphSelection Implementation

  public void set(GraphLocation[] locations) {
    add(locations);
  }

  public void add(GraphLocation location) {
    if (!location.equals(item)) {
      item = location;
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(GraphLocation[] locations) {
    if (locations.length > 0) {
      if (!locations[0].equals(item)) {
        item = locations[0];
        processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void remove(GraphLocation location) {
    if (location.equals(item)) {
      item = null;
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void remove(GraphLocation[] locations) {
    for (int i = 0 ; i < locations.length ; i++ )
      if (locations[i].equals(item)) {
        item = null;
        processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
        return;
      }
  }

  public void removeAll() {
    if (item != null) {
      item = null;
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void enableSelectionEvents(boolean enable) {
    if (events = enable)
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // Graph Selection Events

  protected void processSelectionEvent(GraphSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

  public final String toString() {
    String cn = getClass().getName();
    return cn.substring(cn.lastIndexOf('.')+1) +  "[" + paramString() + "]";
  }

  protected String paramString() {
    return "item=" + item; 
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(item instanceof Serializable ? item : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof GraphLocation)
      item = (GraphLocation)data;
  }

  private transient GraphLocation item;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

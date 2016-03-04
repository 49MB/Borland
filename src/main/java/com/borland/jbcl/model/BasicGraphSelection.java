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
 * BasicGraphSelection implements a basic WritableGraphSelection providing multiselect support
 */
public class BasicGraphSelection extends BasicSelection implements WritableGraphSelection, Serializable
{
  public BasicGraphSelection() {}
  public BasicGraphSelection(GraphLocation[] newSet) { set(newSet); }

  // GraphSelection implementation

  public boolean contains(GraphLocation location) { return location != null && array.contains(location); }

  public GraphLocation[] getAll() {
    GraphLocation[] contents = new GraphLocation[getCount()];
    array.copyInto(contents);
    return contents;
  }

  public void addSelectionListener(GraphSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(GraphSelectionListener listener) {
    selectionListeners.remove(listener);
  }

  // WritableGraphSelection implementation

  public void set(GraphLocation[] locations) {
    doRemoveAll();
    for (int i = 0 ; i < locations.length ; i++)
      array.addElement(locations[i]);
    processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void add(GraphLocation location) {
    if (doAdd(location))
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.ITEM_ADDED, location));
  }

  public void add(GraphLocation[] locations) {
    boolean changed = false;
    for (int i = 0 ; i < locations.length ; i++)
      changed |= doAdd(locations[i]);
    if (changed)
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void remove(GraphLocation location) {
    if (doRemove(location))
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.ITEM_REMOVED, location));
  }

  public void remove(GraphLocation[] locations) {
    boolean changed = false;
    for (int i = 0 ; i < locations.length ; i++ )
      changed |= doRemove(locations[i]);
    if (changed)
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void removeAll() {
    if (doRemoveAll())
      processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
  }

  public void enableSelectionEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (events)
        processSelectionEvent(new GraphSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  // Graph Selection Events

  protected void processSelectionEvent(GraphSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

}

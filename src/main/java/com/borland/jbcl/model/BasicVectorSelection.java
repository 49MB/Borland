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
 * BasicVectorSelection implements a basic WritableVectorSelection providing multiselect support
 */
public class BasicVectorSelection extends BasicSelection implements WritableVectorSelection, Serializable
{
  public BasicVectorSelection() {}
  public BasicVectorSelection(int[] newSet) { set(newSet); }

  // VectorSelection implementation

  public boolean contains(int location) { return array.contains(new Integer(location)); }

  public int[] getAll() {
    int[] contents = new int[array.size()];
    for (int i = 0 ; i < array.size() ; i++)
      contents[i] = ((Integer)array.elementAt(i)).intValue();
    return contents;
  }

  public void addSelectionListener(VectorSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(VectorSelectionListener listener) {
    selectionListeners.remove(listener);
  }

  // WritableVectorSelection implementation

  public void set(int[] locations) {
    doRemoveAll();
    for (int i = 0 ; i < locations.length ; i++)
      array.addElement(new Integer(locations[i])); // direct array add to skip contains() check
    processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void add(int location) {
    if (doAdd(new Integer(location)))
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.ITEM_ADDED, location));
  }

  public void add(int[] locations) {
    boolean changed = false;
    for (int i = 0 ; i < locations.length ; i++)
      changed |= doAdd(new Integer(locations[i]));
    if (changed)
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void addRange(int begin, int end) {
    int lo = begin < end ? begin : end;
    int hi = begin > end ? begin : end;
    boolean changed = false;
    for (int i = lo ; i <= hi ; i++)
      changed |= doAdd(new Integer(i));
    if (changed)
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.RANGE_ADDED, begin, end));
  }

  public void remove(int location) {
    if (doRemove(new Integer(location)))
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.ITEM_REMOVED, location));
  }

  public void remove(int[] locations) {
    boolean changed = false;
    for (int i = 0 ; i < locations.length ; i++)
      changed |= doRemove(new Integer(locations[i]));
    if (changed)
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  public void removeRange(int begin, int end) {
    int lo = begin < end ? begin : end;
    int hi = begin > end ? begin : end;
    boolean changed = false;
    for (int i = lo ; i <= hi ; i++)
      changed |= doRemove(new Integer(i));
    if (changed)
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.RANGE_REMOVED, begin, end));
  }

  public void removeAll() {
    if (doRemoveAll())
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
  }

  public void enableSelectionEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (events)
        processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  //  Vector Selection Events

  protected void processSelectionEvent(VectorSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

}

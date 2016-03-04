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
 * SingleVectorSelection implements WritableVectorSelection allowing only 1
 * item to be selected.
 */
public class SingleVectorSelection implements WritableVectorSelection, Serializable
{
  public SingleVectorSelection() {}
  public SingleVectorSelection(int location) { index = location; }

  // VectorSelection Implemenation

  public boolean contains(int location) {
    return location == index;
  }

  public int getCount() {
    return index != -1 ? 1 : 0;
  }

  public int[] getAll() {
    int[] contents = new int[index != -1 ? 1 : 0];
    if (index != -1)
      contents[0] = index;
    return contents;
  }

  public void addSelectionListener(VectorSelectionListener listener) {
    selectionListeners.add(listener);
  }

  public void removeSelectionListener(VectorSelectionListener listener) {
    selectionListeners.remove(listener);
  }

//  public void enableSelectionEvents(boolean enable) { events = enable; }

  // WritableVectorSelection Implemenation

  public void set(int[] locations) {
    add(locations);
  }

  public void add(int location) {
    if (index != location) {
      index = location;
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void add(int[] locations) {
    if (locations.length > 0) {
      if (index != locations[0]) {
        index = locations[0];
        processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
      }
    }
  }

  public void addRange(int begin, int end) {
    if (index != end) {
      index = end;
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
    }
  }

  public void remove(int location) {
    if (index == location) {
      index = -1;
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void remove(int[] locations) {
    for (int i = 0 ; i < locations.length ; i++) {
      if (index == locations[i]) {
        index = -1;
        processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
        return;
      }
    }
  }

  public void removeRange(int begin, int end) {
    if (index >= begin || index <= end) {
      index = -1;
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void removeAll() {
    if (index != -1) {
      index = -1;
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CLEARED));
    }
  }

  public void enableSelectionEvents(boolean enable) {
    if (events = enable)
      processSelectionEvent(new VectorSelectionEvent(this, SelectionEvent.SELECTION_CHANGED));
  }

  // Vector Selection Events

  protected void processSelectionEvent(VectorSelectionEvent e) {
    if (events && selectionListeners.hasListeners())
      selectionListeners.dispatch(e);
  }

  public final String toString() {
    String cn = getClass().getName();
    return cn.substring(cn.lastIndexOf('.')+1) +  "[" + paramString() + "]"; 
  }

  protected String paramString() {
    return "index=" + index; 
  }

  private int index = -1;
  private transient EventMulticaster selectionListeners = new EventMulticaster();
  private boolean events = true;
}

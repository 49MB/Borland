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
package com.borland.jbcl.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.EventMulticaster;

/**
 *
 */
public class FocusMulticaster implements FocusListener
{
  // FocusListener Implementation

  public void focusGained(FocusEvent e) {
    if (listeners != null)
      dispatch(e);
  }

  public void focusLost(FocusEvent e) {
    if (listeners != null)
      dispatch(e);
  }

  /**
   * High speed test to see if any listeners present.
   */
  public final boolean hasListeners() {
    return listeners != null;
  }

  /**
   * High speed dispatcher that does not need to be synchronized.
   */
  public final void dispatch(FocusEvent e) {
    // Synchronized not needed becuase all updates are made to a "copy"
    // of listeners.  Assumes reference assignment is atomic.
    FocusListener[] listenersCopy = this.listeners;

    // Once I have a local copy of the list, don't have to worry about threads
    // adding/deleting from this list since they will make a copy of the list,
    // not modify the list.
    if (listenersCopy != null) {
      int count = listenersCopy.length;
      for (int index = 0; index < count; ++index) {
        //long time = System.currentTimeMillis();
        Diagnostic.trace(EventMulticaster.class, "->dispatch e=" + e + " => " + listenersCopy[index]); 
        switch (e.getID()) {
          case FocusEvent.FOCUS_GAINED:
            listenersCopy[index].focusGained(e);
            break;
          case FocusEvent.FOCUS_LOST:
            listenersCopy[index].focusLost(e);
            break;
        }
        //Diagnostic.trace(EventMulticaster.class, "  dispatch took " + (System.currentTimeMillis()-time) + "ms");
      }
    }
  }

  /**
   * Simple list management that avoids synchronized/functional interface of Vector.  Key to this
   * implementation is that all changes are made to a "copy" of the original list.  This allows
   * for non-synchronized access of listners when event dispatch is called.
   */
  public int find(FocusListener listener) {
    if (listeners != null ) {
      for (int index = 0; index < listeners.length; ++index)
        if (listeners[index] == listener)
          return index;
    }
    return -1;
  }

  /**
   *
   */
  public synchronized final void add(FocusListener listener) {
    if (find(listener) < 0) {
      FocusListener[] newListeners;

      if (listeners == null)
        newListeners = new FocusListener[1];
      else {
        newListeners = new FocusListener[listeners.length+1];
        System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
      }

      newListeners[newListeners.length-1] = listener;
      listeners = newListeners;  // Assumed atomic.
    }
  }

  /**
   *
   */
  public synchronized final void remove(FocusListener listener) {
    int index = find(listener);
    if (index > -1) {
      // Important: hasListeners() expects listeners too be null if there are no listeners.
      if (listeners.length == 1)
        listeners = null;
      else {
        FocusListener[] newListeners = new FocusListener[listeners.length-1];
        System.arraycopy(listeners, 0, newListeners, 0, index);

        if (index < newListeners.length)
          System.arraycopy(listeners, index+1, newListeners, index, newListeners.length-index);

        listeners = newListeners;  // Assumed atomic.
      }
    }
  }

  private transient FocusListener[] listeners;
}

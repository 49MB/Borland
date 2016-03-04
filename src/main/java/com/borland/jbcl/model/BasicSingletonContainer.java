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
 * The BasicSingletonContainer is a basic implementation of WritableSingletonModel
 */
public class BasicSingletonContainer implements WritableSingletonModel, Serializable
{
  private static final long serialVersionUID = 200L;

  public BasicSingletonContainer() {}
  public BasicSingletonContainer(Object data) {
    this.item = data;
  }

  // SingletonModel implementation

  public Object get() { return item; }

  public Object getCopy() {
    Object newObject = new Object();
    newObject = item;
    return newObject;
  }

  public void addModelListener(SingletonModelListener listener) {
    modelListeners.add(listener);
  }

  public void removeModelListener(SingletonModelListener listener) {
    modelListeners.remove(listener);
  }

  // WritableSingletonModel implementation

  public boolean canSet(boolean startEdit) { return true; }

  public void set(Object data) {
    this.item = data;
    processModelEvent(new SingletonModelEvent(this));
  }

  public void touched() {
    processModelEvent(new SingletonModelEvent(this));
  }

  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        processModelEvent(new SingletonModelEvent(this));
    }
  }

  // Singleton Model Events

  protected void processModelEvent(SingletonModelEvent e) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(e);
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(item instanceof Serializable ? item : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    item = s.readObject();
  }

  private transient Object item;
  private transient EventMulticaster modelListeners = new EventMulticaster();
  private boolean events = true;
}

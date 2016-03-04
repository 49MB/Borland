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
import java.util.Enumeration;
import java.util.Vector;

import com.borland.jb.util.EventMulticaster;

/**
 * The BasicVectorContainer is a basic implementation of WritableVectorModel
 * that uses a Vector to store data.
 */
public class BasicVectorContainer implements WritableVectorModel, Serializable {
  private static final long serialVersionUID = 200L;
  
  public BasicVectorContainer() {
    array = new Vector<Object>();
  }
  
  public BasicVectorContainer(int size) {
    array = new Vector<Object>(size);
  }
  
  public BasicVectorContainer(int size, Object object) {
    array = new Vector<Object>(size);
    for (int i = 0; i < size; i++) {
      array.addElement(object);
    }
  }
  
  public BasicVectorContainer(Object[] newArray) {
    array = new Vector<Object>();
    synchronized (newArray) {
      for (int i = 0, count = newArray.length; i < count; i++) {
        array.addElement(newArray[i]);
      }
    }
  }
  
  public Object[] getItems() {
    Object[] items = new Object[array.size()];
    array.copyInto(items);
    return items;
  }
  
  public void setItems(Object[] newItems) {
    array = new Vector<Object>();
    synchronized (newItems) {
      for (int i = 0, count = newItems.length; i < count; i++) {
        array.addElement(newItems[i]);
      }
    }
    processModelEvent(new VectorModelEvent(this,
        VectorModelEvent.STRUCTURE_CHANGED));
  }
  
  // VectorModel implementation
  
  public Object get(int index) {
    if (index >= 0 && array.size() > index)
      return array.elementAt(index);
    return null;
  }
  
  public int getCount() {
    return array.size();
  }
  
  public int find(Object data) {
    return array.indexOf(data);
  }
  
  public void setCount(int count) {
    int size = array.size();
    if (!variableSize || count == size)
      return;
    else if (count > size)
      for (int i = size; i < count; i++)
        array.addElement(null);
    else
      for (int i = size; i > count; i--)
        array.removeElementAt(i - 1);
  }
  
  public void addModelListener(VectorModelListener listener) {
    modelListeners.add(listener);
  }
  
  public void removeModelListener(VectorModelListener listener) {
    modelListeners.remove(listener);
  }
  
  // WritableVectorModel implementation
  
  public boolean canSet(int index, boolean startEdit) {
    return index < array.size();
  }
  
  public void set(int index, Object object) {
    array.setElementAt(object, index);
    processModelEvent(new VectorModelEvent(this, VectorModelEvent.ITEM_CHANGED,
        index));
  }
  
  public void touched(int index) {
    if (index < array.size())
      processModelEvent(new VectorModelEvent(this,
          VectorModelEvent.ITEM_TOUCHED, index));
  }
  
  public boolean isVariableSize() {
    return variableSize;
  }
  
  public void setVariableSize(boolean variable) {
    variableSize = variable;
  }
  
  public void addItem(Object object) {
    if (!variableSize)
      return;
    array.addElement(object);
    processModelEvent(new VectorModelEvent(this, VectorModelEvent.ITEM_ADDED,
        array.size()));
  }
  
  public void addItem(int aheadOf, Object object) {
    if (!variableSize)
      return;
    array.insertElementAt(object, aheadOf);
    processModelEvent(new VectorModelEvent(this, VectorModelEvent.ITEM_ADDED,
        aheadOf));
  }
  
  public void remove(int index) {
    if (!variableSize)
      return;
    array.removeElementAt(index);
    processModelEvent(new VectorModelEvent(this, VectorModelEvent.ITEM_REMOVED,
        index));
  }
  
  public void removeAll() {
    if (!variableSize)
      return;
    if (array.size() > 0) {
      array.removeAllElements();
      processModelEvent(new VectorModelEvent(this,
          VectorModelEvent.STRUCTURE_CHANGED));
    }
  }
  
  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        processModelEvent(new VectorModelEvent(this,
            VectorModelEvent.STRUCTURE_CHANGED));
    }
  }
  
  // Vector Model Events
  
  protected void processModelEvent(VectorModelEvent e) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(e);
  }
  
  public Enumeration<Object> begin() {
    return array.elements();
  }
  
  public Vector<Object> getVector() {
    return array;
  }
  
  // Serialization support
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Vector<Object> newArray = new Vector<Object>();
    for (int i = 0; i < array.size(); i++) {
      if (array.elementAt(i) instanceof Serializable)
        newArray.addElement(array.elementAt(i));
    }
    s.writeObject(newArray);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    array = (Vector<Object>) s.readObject();
  }
  
  private transient Vector<Object> array;
  private boolean variableSize = true;
  private transient EventMulticaster modelListeners = new EventMulticaster(); // ignore
                                                                              // for
                                                                              // serialization
  private boolean events = true;
}

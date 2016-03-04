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
import java.util.Vector;

import com.borland.jb.util.EventMulticaster;

/**
 * BasicSelection provides a base class for the specific dimensional selection
 * sets providing multiselect support.
 */
public abstract class BasicSelection implements Serializable {
  private static final long serialVersionUID = 200L;
  
  public int getCount() {
    return array.size();
  }
  
  /**
   *
   */
  protected boolean doAdd(Object location) {
    if (!array.contains(location)) {
      array.addElement(location);
      return true;
    }
    return false;
  }
  
  /**
   *
   */
  protected boolean doRemove(Object location) {
    int index = array.indexOf(location);
    if (index >= 0) {
      array.removeElementAt(index);
      return true;
    }
    return false;
  }
  
  /**
   *
   */
  protected boolean doRemoveAll() {
    if (array.size() > 0) {
      array.removeAllElements();
      return true;
    }
    return false;
  }
  
  public final String toString() {
    String cn = getClass().getName();
    return cn.substring(cn.lastIndexOf('.') + 1) + "[" + paramString() + "]";
  }
  
  protected String paramString() {
    return "size=" + array.size();
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
  
  protected transient Vector<Object> array = new Vector<Object>();
  protected transient EventMulticaster selectionListeners = new EventMulticaster(); // ignore
                                                                                    // for
                                                                                    // serialization
  protected boolean events = true;
}

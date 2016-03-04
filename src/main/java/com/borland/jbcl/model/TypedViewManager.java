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
//------------------------------------------------------------------------------
package com.borland.jbcl.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class TypedViewManager implements SingletonViewManager,
    VectorViewManager, MatrixViewManager, GraphViewManager, Serializable {
  private static final long serialVersionUID = 200L;
  
  // Registration. Passing null as the itemClass registers the default
  // painter/editor
  //
  public boolean add(Class itemClass, ItemPainter painter, ItemEditor editor) {
    if (painter != null) {
      if (itemClass == null)
        defaultPainter = painter;
      else
        painters.put(itemClass, painter);
    }
    if (editor != null) {
      if (itemClass == null)
        defaultEditor = editor;
      else
        editors.put(itemClass, editor);
    }
    return true;
  }
  
  // SingletonViewManager interface
  
  public ItemPainter getPainter(Object data, int state) {
    try {
      ItemPainter painter = (ItemPainter) painters.get(data.getClass());
      if (painter != null)
        return painter;
      for (Enumeration<Class<?>> i = painters.keys(); i.hasMoreElements();) {
        Class c = (Class) i.nextElement();
        if (c.isInstance(data))
          return (ItemPainter) painters.get(c);
      }
      return defaultPainter;
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }
  
  public ItemEditor getEditor(Object data, int state) {
    try {
      ItemEditor editor = (ItemEditor) editors.get(data.getClass());
      if (editor != null)
        return editor;
      for (Enumeration<Class<?>> i = editors.keys(); i.hasMoreElements();) {
        Class c = (Class) i.nextElement();
        if (c.isInstance(data))
          return (ItemEditor) editors.get(c);
      }
      return defaultEditor;
    } catch (Exception e) {
      System.err.println(e);
    }
    return null;
  }
  
  // VectorViewManager interface
  
  public ItemPainter getPainter(int index, Object data, int state) {
    return getPainter(data, state);
  }
  
  public ItemEditor getEditor(int index, Object data, int state) {
    return getEditor(data, state);
  }
  
  // MatrixViewManager interface
  
  public ItemPainter getPainter(int row, int col, Object data, int state) {
    return getPainter(data, state);
  }
  
  public ItemEditor getEditor(int row, int col, Object data, int state) {
    return getEditor(data, state);
  }
  
  // GraphViewManager interface
  
  public ItemPainter getPainter(GraphLocation node, Object data, int state) {
    return getPainter(data, state);
  }
  
  public ItemEditor getEditor(GraphLocation node, Object data, int state) {
    return getEditor(data, state);
  }
  
  // internals
  
  // Serialization support
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable<String, Object> hash = new Hashtable<String, Object>(8);
    if (defaultPainter instanceof Serializable)
      hash.put("dp", defaultPainter);
    if (defaultEditor instanceof Serializable)
      hash.put("de", defaultEditor);
    Enumeration<Class<?>> pe = painters.keys();
    while (pe.hasMoreElements()) {
      Object key = pe.nextElement();
      if (!(painters.get(key) instanceof Serializable))
        painters.remove(key);
    }
    Enumeration<Class<?>> ee = editors.keys();
    while (ee.hasMoreElements()) {
      Object key = ee.nextElement();
      if (!(editors.get(key) instanceof Serializable))
        editors.remove(key);
    }
    s.writeObject(hash);
    s.writeObject(painters);
    s.writeObject(editors);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Hashtable<?, ?> hash = (Hashtable<?, ?>) s.readObject();
    Object data = hash.get("dp");
    if (data instanceof ItemPainter)
      defaultPainter = (ItemPainter) data;
    data = hash.get("de");
    if (data instanceof ItemEditor)
      defaultEditor = (ItemEditor) data;
    painters = (Hashtable<Class<?>, ItemPainter>) s.readObject();
    editors = (Hashtable<Class<?>, ItemEditor>) s.readObject();
  }
  
  transient ItemPainter defaultPainter;
  transient ItemEditor defaultEditor;
  transient Hashtable<Class<?>, ItemPainter> painters = new Hashtable<Class<?>, ItemPainter>();
  transient Hashtable<Class<?>, ItemEditor> editors = new Hashtable<Class<?>, ItemEditor>();
}

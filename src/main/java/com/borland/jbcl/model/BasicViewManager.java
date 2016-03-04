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
//-------------------------------------------------------------------------------------------------
package com.borland.jbcl.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * The BasicViewManager provides a basic implementation of SingletonViewManager, VectorViewManager,
 * MatrixViewManager, and GraphViewManager.  It will always return the same ItemPainter and ItemEditor
 * regardless of the switching information passed in the getPainter(...) and getEditor(...) methods.
 */
public class BasicViewManager
  implements SingletonViewManager, VectorViewManager, MatrixViewManager, GraphViewManager, Serializable
{
  private static final long serialVersionUID = 200L;

  public BasicViewManager() {}
  public BasicViewManager(ItemPainter painter) {
    this.painter = painter;
  }
  public BasicViewManager(ItemPainter painter, ItemEditor editor) {
    this.painter = painter;
    this.editor = editor;
  }

  public void setPainter(ItemPainter p) {
    painter = p;
  }
  public ItemPainter getPainter() {
    return painter;
  }

  public void setEditor(ItemEditor e) {
    editor = e;
  }
  public ItemEditor getEditor() {
    return editor;
  }

  // SingletonViewManger

  public ItemPainter getPainter(Object data, int state) { return painter; }
  public ItemEditor getEditor(Object data, int state) { return editor; }

  // VectorViewManger

  public ItemPainter getPainter(int index, Object data, int state) { return painter; }
  public ItemEditor getEditor(int index, Object data, int state) { return editor; }

  // MatrixViewManger

  public ItemPainter getPainter(int row, int col, Object data, int state) { return painter; }
  public ItemEditor getEditor(int row, int col, Object data, int state) { return editor; }

  // GraphViewManger

  public ItemPainter getPainter(GraphLocation node, Object data, int state) { return painter; }
  public ItemEditor getEditor(GraphLocation node, Object data, int state) { return editor; }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (painter instanceof Serializable)
      hash.put("p", painter); 
    if (editor instanceof Serializable)
      hash.put("e", editor); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("p"); 
    if (data instanceof ItemPainter)
      painter = (ItemPainter)data;
    data = hash.get("e"); 
    if (data instanceof ItemEditor)
      editor = (ItemEditor)data;
  }

  protected transient ItemPainter painter;
  protected transient ItemEditor editor;
}

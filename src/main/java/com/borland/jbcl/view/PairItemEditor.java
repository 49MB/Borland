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
package com.borland.jbcl.view;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ToggleItemEditor;
import com.borland.jbcl.util.Pair;

/**
 * This ItemEditor implementation pulls the first or second object from
 * a Pair object to pass on to a nested ItemEditor
 */
public class PairItemEditor implements ToggleItemEditor, Serializable
{
  private static final long serialVersionUID = 200L;

  /**
   * Constructs a PairItemEditor using the nested 'editor', and pulling
   * the 'item' (1=first, 2=second) from the Pair object.
   */
  public PairItemEditor(ItemEditor editor, int pairItem) {
    this.editor = editor;
    this.pairItem = pairItem;
    if (this.pairItem < 1 || this.pairItem > 2)
      this.pairItem = 1;
  }

  // ItemEditor Implementation

  public Object getValue() {
    if (editor != null && pair != null) {
      Object data = editor.getValue();
      if (pairItem == 1)
        pair.first = data;
      else
        pair.second = data;
      return pair;
    }
    else if (editor != null)
      return editor.getValue();
    else
      return pair;
  }

  public Component getComponent() {
    if (editor != null)
      return editor.getComponent();
    else
      return null;
  }

  public boolean isToggle(Object data, Rectangle rect, ItemEditSite site) {
    if (editor != null && editor instanceof ToggleItemEditor)
      return ((ToggleItemEditor)editor).isToggle(data, rect, site);
    else
      return false;
  }

  public void startEdit(Object data, Rectangle bounds, ItemEditSite editSite) {
    if (data instanceof Pair) {
      this.pair = (Pair)data;
      Object pairData = null;
      if (pairItem == 1)
        pairData = pair.first;
      else
        pairData = pair.second;
      editor.startEdit(pairData, bounds, editSite);
    }
    else {
      this.pair = null;
      editor.startEdit(data, bounds, editSite);
    }
  }

  public void changeBounds(Rectangle bounds) {
    if (editor != null)
      editor.changeBounds(bounds);
  }

  public boolean canPost() {
    if (editor != null)
      return editor.canPost();
    else
      return false;
  }

  public void endEdit(boolean posted) {
    if (editor != null)
      editor.endEdit(posted);
    pair = null;
  }

  public void addKeyListener(KeyListener l) {
    if (editor != null)
      editor.addKeyListener(l);
  }
  public void removeKeyListener(KeyListener l) {
    if (editor != null)
      editor.removeKeyListener(l);
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(editor instanceof Serializable ? editor : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof ItemEditor)
      editor = (ItemEditor)data;
  }

  protected transient ItemEditor editor;
  protected transient Pair pair;
  protected int  pairItem = 1;
}

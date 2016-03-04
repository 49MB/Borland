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
package com.borland.jbcl.view;

import java.awt.AWTEvent;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.SingletonModelMulticaster;
import com.borland.jbcl.model.WritableSingletonModel;

/**
 *
 */
public class ChoiceView
     extends Choice
  implements SingletonModelView, SingletonModelListener, KeyListener, Serializable
{
  private static final long serialVersionUID = 200L;

  public ChoiceView() {
    super();
    enableEvents(AWTEvent.ITEM_EVENT_MASK);
    addKeyListener(this);
  }

  /**
   * Returns an array of Strings, which represent the items in the choice list
   */
  public synchronized String[] getItems() {
    int c = getItemCount();
    String[] s = new String[c];
    for (int i = 0; i < c; i++)
      s[i] = getItem(i);
    return s;
  }

  /**
   * Sets the list of items in the Choice
   * @param items The String array representing the items.
   */
  public synchronized void setItems(String[] items) {
    removeAll();
    if (items != null) {
      for (int i = 0; i < items.length; i++)
        super.addItem(items[i]);
    }
    if (model.get() != null)
      updateSelection();
    else if (!isReadOnly() && writeModel.canSet(true) && items != null && items.length > 0)
      writeModel.set(items[0]);
    if (items != null && getPeer() != null)
      addNotify();   //workaround for javaSoft bug 4130788 - live adding to a java.awt.Choice changes its position
  }

  public SingletonModel getModel() { return model; }
  public WritableSingletonModel getWriteModel() { return readOnly ? null : writeModel; }
  public void setModel(SingletonModel p) {
    if (model != null)
      model.removeModelListener(this);
    model = p;
    writeModel = (p instanceof WritableSingletonModel) ? (WritableSingletonModel)p : null;
    if (model != null) {
      model.addModelListener(this);
      modelContentChanged(null);
    }
    setEnabled(!isReadOnly());
  }

  public void addModelListener(SingletonModelListener listener) { model.addModelListener(listener); }
  public void removeModelListener(SingletonModelListener listener) { model.removeModelListener(listener); }

  public boolean isReadOnly() { return readOnly ? true : writeModel == null; }
  public void setReadOnly(boolean ro) {
    readOnly = ro;
    setEnabled(!readOnly);
  }

  public void modelContentChanged(SingletonModelEvent e) { updateSelection(); }

  public void select(String s) {
    if (!isReadOnly() && writeModel.canSet(true)) {
      super.select(s);
      String selected = getSelectedItem();
      writeModel.set(selected);
      // no item event gets fired!
    }
  }

  public void setAutoAdd(boolean autoAdd) {
    this.autoAdd = autoAdd;
  }
  public boolean isAutoAdd() {
    return autoAdd;
  }
  private boolean autoAdd = true;

  protected void updateSelection() {
    Object data = model.get();
    String text = data != null ? data.toString() : "";
    for (int i = 0; i < getItemCount(); i++) {
      if (text.equals(getItem(i))) {
        super.select(text);
        return;
      }
    }
    if (autoAdd && data != null) {
      super.addItem(text);
      super.select(text);
    }
    // no item event gets fired!
  }

  protected void processItemEvent(ItemEvent e) {
    if (!isReadOnly() && writeModel.canSet(true)) {
      String selected = getSelectedItem();
      writeModel.set(selected);
    }
    super.processItemEvent(e);
  }

  // KeyListener Implementation

  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {
    if (isReadOnly() || !writeModel.canSet(false) || e.isAltDown())
      return;
    char c = e.getKeyChar();
    String item = getSelectedItem();
    if (item == null)
      return;
    item = item.toLowerCase();
    char s = item.length() > 0 ? item.charAt(0) : e.CHAR_UNDEFINED;
    int[] matches = new int[0];
    if (c != e.CHAR_UNDEFINED) {
      // scan for all matching values
      for (int i = 0; i < getItemCount(); i++) {
        String value = getItem(i).toLowerCase();
        if (value.length() > 0 && value.charAt(0) == c) {
          // this has a different first char than currently selected, so select it
          if (s != c) {
            writeModel.set(getItem(i));
            return;
          }
          int[] old = matches;
          matches = new int[old.length + 1];
          System.arraycopy(old, 0, matches, 0, old.length);
          matches[matches.length - 1] = i;
        }
      }
      // place selection accordingly within matches (we know that s==c)
      if (matches.length > 0) {
        int sel = getSelectedIndex();
        for (int i = 0; i < matches.length; i++) {
          // if we are past the current value, select it
          if (matches[i] > sel) {
            writeModel.set(getItem(matches[i]));
            return;
          }
          // if we are at the current value, and it's the end, cycle back to the first one
          if (i == matches.length - 1 && matches[i] == sel) {
            writeModel.set(getItem(matches[0]));
            return;
          }
        }
      }
    }
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(model instanceof Serializable ? model : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof SingletonModel)
      model = (SingletonModel)data;
    if (model instanceof WritableSingletonModel)
      writeModel = (WritableSingletonModel)model;
  }

  private transient SingletonModel model;
  private transient WritableSingletonModel writeModel;
  private transient SingletonModelMulticaster modelAdapter = new SingletonModelMulticaster();
  private boolean readOnly;
}

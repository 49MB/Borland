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
import java.awt.TextField;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.SingletonModelMulticaster;
import com.borland.jbcl.model.WritableSingletonModel;

public class TextFieldView
     extends TextField
  implements SingletonModelView, SingletonModelListener, Serializable
{
  private static final long serialVersionUID = 200L;

  public TextFieldView() {
    super();
    enableEvents(AWTEvent.FOCUS_EVENT_MASK |
                 AWTEvent.KEY_EVENT_MASK);
  }

  public SingletonModel getModel() { return model; }
  public void setModel(SingletonModel sm) {
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = sm;
    writeModel = (model instanceof WritableSingletonModel) ? (WritableSingletonModel)sm : null;
    setEditable(!isReadOnly());
    if (model != null) {
      model.addModelListener(this);
      model.addModelListener(modelMulticaster);
      updateText();
    }
  }
  public WritableSingletonModel getWriteModel() { return readOnly ? null : writeModel; }

  public void addModelListener(SingletonModelListener l) { modelMulticaster.add(l); }
  public void removeModelListener(SingletonModelListener l) { modelMulticaster.remove(l); }

  public boolean isReadOnly() { return readOnly ? true : writeModel == null; }
  public void setReadOnly(boolean ro) {
    readOnly = ro;
    setEditable(!isReadOnly());
  }

  public boolean isPostOnFocusLost() { return postOnFocusLost; }
  public void setPostOnFocusLost(boolean post) { postOnFocusLost = post; }

  protected void updateText() {
    if (model != null) {
      Object o = model.get();
      int selectionStart = getSelectionStart();
      int selectionEnd = getSelectionEnd();
      super.setText((o != null) ? o.toString() : "");
      setSelectionStart(selectionStart);
      setSelectionEnd(selectionEnd);
    }
  }

  protected void setSuperText(String text) {
    super.setText(text);
  }

  public void setText(String text) {
    if (canSet(true) && !locateOnly) {
      String oldText = getText();
      if (oldText == null && text == null)
        return;
      if (oldText != null && text != null && oldText.equals(text))
        return;
      writeModel.set(text);
      // model will notify a repaint!
    }
  }

  protected void postText() {
    if (canSet(true) && !locateOnly) {
      String text = super.getText();
      writeModel.set(text);
    }
  }

  // SingletonModelListener implementation

  public void modelContentChanged(SingletonModelEvent e) {
    updateText();
  }

  protected void processKeyEvent(KeyEvent e) {
    super.processKeyEvent(e);
    canSet(true); // tell model we are editing!
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
          postText();
          break;
        case KeyEvent.VK_ESCAPE:
          updateText();
          break;
      }
    }
  }

  public boolean canSet(boolean startingEdit) {
    return locateOnly ? true : isReadOnly() ? false : writeModel.canSet(startingEdit);
  }

  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    if (e.getID() == FocusEvent.FOCUS_LOST) {
      String text = super.getText();
      Object data = model.get();
      if (postOnFocusLost && !text.equals(data))
        postText();
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

  protected transient SingletonModel         model;
  protected transient WritableSingletonModel writeModel;

  protected transient SingletonModelMulticaster modelMulticaster = new SingletonModelMulticaster();
  protected boolean readOnly;
  protected boolean postOnFocusLost = true;
  protected boolean locateOnly = false;
}

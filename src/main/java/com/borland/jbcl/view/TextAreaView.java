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
import java.awt.Dimension;
import java.awt.TextArea;
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

public class TextAreaView
     extends TextArea
  implements SingletonModelView, SingletonModelListener, Serializable
{
  private static final long serialVersionUID = 200L;

  public TextAreaView() {
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
    if (!isReadOnly() && writeModel.canSet(true)) {
      String oldText = getText();
      if (oldText == null && text == null)
        return;
      if (oldText != null && text != null && oldText.equals(text))
        return;
      writeModel.set(text);
      // model will notify a repaint!
    }
  }

  public void append(String text) {
    if (!isReadOnly() && writeModel.canSet(true)) {
      super.append(text);
      writeModel.set(getText());
      // model will notify a repaint!
    }
  }

  protected void postText() {
    if (writeModel != null && writeModel.canSet(true)) {
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
    if (!isReadOnly())
      writeModel.canSet(true); // tells model we are editing!
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
          if (e.isControlDown())
            postText();
          break;
        case KeyEvent.VK_ESCAPE:
          updateText();
          break;
      }
    }
  }

  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    if (e.getID() == FocusEvent.FOCUS_LOST) {
      String text = super.getText();
      Object obj  = model.get();
      if (postOnFocusLost && !text.equals(obj))
        postText();
    }
  }

  public Dimension getPreferredSize() {
    Dimension preferredSize = super.getPreferredSize();

    if (preferredHeight > preferredSize.height)
      preferredSize = new Dimension(preferredSize.width, preferredHeight);

    if (preferredWidth > preferredSize.width)
      preferredSize = new Dimension(preferredWidth, preferredSize.height);

    return preferredSize;
  }

  public void setPreferredHeight(int preferredHeight) {
    this.preferredHeight = preferredHeight;
  }
  public int getPreferredHeight() { return preferredHeight; }

  public void setPreferredWidth(int preferredWidth) {
    this.preferredWidth = preferredWidth;
  }
  public int getPreferredWidth() { return preferredWidth; }

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

  protected boolean   readOnly;
  protected boolean   postOnFocusLost = true;
  protected int       preferredHeight;
  protected int       preferredWidth;
  protected transient SingletonModelMulticaster modelMulticaster = new SingletonModelMulticaster();
}

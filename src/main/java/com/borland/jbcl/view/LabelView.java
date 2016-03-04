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

import java.awt.Dimension;
import java.awt.Label;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.SingletonModelMulticaster;
import com.borland.jbcl.model.WritableSingletonModel;

public class LabelView
     extends Label
  implements SingletonModelView, SingletonModelListener, Serializable
{
  private static final long serialVersionUID = 200L;

  public SingletonModel getModel() { return model; }
  public void setModel(SingletonModel sm) {
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = sm;
    writeModel = (model instanceof WritableSingletonModel) ? (WritableSingletonModel)sm : null;
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
  public void setReadOnly(boolean ro) { readOnly = ro; }

  public void setText(String text) {
    if (!isReadOnly() && writeModel.canSet(true)) {
      writeModel.set(text);
      // model will notify a repaint!
    }
  }

  protected void updateText() {
    if (model != null) {
      Object o = model.get();
      super.setText((o != null) ? o.toString() : "");
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

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    if (d.width < 2)
      d.width = 100;
    return d;
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
    if (model instanceof SingletonModel)
      writeModel = (WritableSingletonModel)model;
  }

  private transient SingletonModel model;
  private transient WritableSingletonModel writeModel;

  private boolean readOnly;
  private boolean postOnFocusLost = true;
  private transient SingletonModelMulticaster modelMulticaster = new SingletonModelMulticaster();
}

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
import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.SingletonModelMulticaster;
import com.borland.jbcl.model.WritableSingletonModel;

public class CheckboxView
     extends Checkbox
  implements SingletonModelListener, SingletonModelView, Serializable
{
  private static final long serialVersionUID = 200L;

  public SingletonModel getModel() { return model; }
  public void setModel(SingletonModel p) {
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = p;
    writeModel = (p instanceof WritableSingletonModel) ? (WritableSingletonModel)p : null;
    if (model != null) {
      model.addModelListener(this);
      model.addModelListener(modelMulticaster);
      modelContentChanged(null);
    }
    enableEvents(AWTEvent.ITEM_EVENT_MASK);
  }
  public WritableSingletonModel getWriteModel() { return readOnly ? null : writeModel; }

  public void addModelListener(SingletonModelListener l) { modelMulticaster.add(l); }
  public void removeModelListener(SingletonModelListener l) { modelMulticaster.remove(l); }

  public boolean isReadOnly() {
    return readOnly ? true : writeModel == null;
  }
  public void setReadOnly(boolean ro) {
    setEnabled(!ro);
    readOnly = ro;
  }

  /**
   * replicate changes originating in the model over to the checkbox
   */
  public void modelContentChanged(SingletonModelEvent e) {
    boolean b = isChecked();
    //System.err.println("\nmodelContentChanged model=" + b + " state=" + getState());
    if (b != getState())
      super.setState(b);
  }

  /**
   * replicate changes originating in the checkbox over to the model
   */
  protected void processItemEvent(ItemEvent e) {
    super.processItemEvent(e);
    //System.err.println("processItemStateChanged model=" + model.get() + " state=" + getState());
    setChecked(getState());
  }

  public boolean isChecked() {
    Object o = model.get();
    if (o instanceof Boolean)
      return ((Boolean)o).booleanValue();
    if (o instanceof Number)
      return ((Number)o).intValue() != 0;
    if (o instanceof String)
      return Boolean.valueOf((String)o).booleanValue();
    return false;
  }

  public void setState(boolean state) {
    if (!isReadOnly()) {
      super.setState(state);
      setChecked(state);
    }
  }

  public void setChecked(boolean value) {
    if (!isReadOnly()) {
      boolean b = isChecked();
      if (b != value) {
        Object o = model.get();
        if (o instanceof Boolean)
          writeModel.set(new Boolean(value));
        else if (o instanceof Number)
          writeModel.set(new Integer(value ? 1 : 0));
        else if (o instanceof String)
          writeModel.set(new Boolean(value).toString());
        else
          writeModel.set(new Boolean(value));
      }
    }
  }

  public Object get() { return model.get(); }
  public boolean canSet() { return isReadOnly() ? false : writeModel.canSet(true); }
  public void set(Object data) {
    if (!isReadOnly())
      writeModel.set(data);
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
  private transient SingletonModelMulticaster modelMulticaster = new SingletonModelMulticaster();
  private boolean readOnly;
}

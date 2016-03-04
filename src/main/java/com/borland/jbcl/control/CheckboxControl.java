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

package com.borland.jbcl.control;

import java.awt.Color;
import java.awt.Font;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.SingletonDataSetManager;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.WritableSingletonModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.view.CheckboxView;

public class CheckboxControl
     extends CheckboxView
  implements AccessListener, WritableSingletonModel,
             BlackBox, ColumnAware, java.io.Serializable
{
  public CheckboxControl() {
    super();
    setModel(new BasicSingletonContainer());
  }

  public void setModel(SingletonModel sm) {
    if (sm == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(sm);
  }

  public void setChecked(boolean value) {
    if (!isReadOnly()) {
      boolean b = isChecked();
      if (b != value) {
        Object o = get();
        if (o instanceof Variant) {
          switch (((Variant)o).getType()) {
            case Variant.BOOLEAN:
              ((Variant)o).setBoolean(value);
              set(o);
              break;
            case Variant.STRING:
              ((Variant)o).setString(String.valueOf(value));
              set(o);
              break;
            case Variant.BYTE:
              ((Variant)o).setByte((byte)(value ? 1 : 0));
              set(o);
              break;
            case Variant.SHORT:
              ((Variant)o).setShort((short)(value ? 1 : 0));
              set(o);
              break;
            case Variant.INT:
              ((Variant)o).setInt(value ? 1 : 0);
              set(o);
              break;
            case Variant.LONG:
              ((Variant)o).setLong(value ? 1 : 0);
              set(o);
              break;
            case Variant.FLOAT:
              ((Variant)o).setFloat(value ? 1 : 0);
              set(o);
              break;
            case Variant.DOUBLE:
              ((Variant)o).setDouble(value ? 1 : 0);
              set(o);
              break;
            default:
              break;
          }
        }
        else
          super.setChecked(value);
      }
    }
  }

  public boolean isChecked() {
    Object o = get();
    if (o instanceof Variant)
      return ((Variant)o).getAsBoolean();
    else
      return super.isChecked();
  }

  // AccessListener Implementation

  public void accessChange(AccessEvent e) {
    switch(e.getID()) {
      case AccessEvent.OPEN:
        try {
          openDataSet(dataSet);
        }
        catch (Exception ex) {
          e.appendException(ex);
        }
        break;
      case AccessEvent.CLOSE:
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  // NavigationListener Implementation
/*
  public void navigated(NavigationEvent e) {
//    System.err.println("CheckboxControl.naviaged() : isChecked()="+isChecked()+" setting checked...");
//    setChecked(isChecked());
//    System.err.println("                           : isChecked()="+isChecked());
  }
*/

  /**
   * The dataSet property specifies a com.borland.dx.dataset.DataSet
   * object to display data from in the field.
   */
  public DataSet getDataSet() { return dataSet; }
  public void setDataSet(DataSet newDataSet) {
    if (dataSet != null) {
      dataSet.removeAccessListener(this);
//      dataSet.removeNavigationListener(this);
    }
    openDataSet(newDataSet);
    if (dataSet != null) {
      dataSet.addAccessListener(this);
//      dataSet.addNavigationListener(this);
    }
  }

  public String getColumnName() { return columnName; }
  public void setColumnName(String newColumnName) {
    columnName = newColumnName;
    if (addNotifyCalled)
      openDataSet(dataSet);
  }

  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      if (dataSet != null)
        openDataSet(dataSet);
    }
  }

  private void openDataSet(DataSet newDataSet) {
    dataSet = newDataSet;
    if (dataSet == null) {
      setModel(new BasicSingletonContainer());
      return;
    }
    else if (addNotifyCalled && !dataSet.isOpen()) {
      try {
        dataSet.open();
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
        setModel(new BasicSingletonContainer());
        return;
      }
    }
    if (dataSet.isOpen()) {
      bindDataSet();
    }
  }

  private void bindDataSet() {
    Column column;
    if (dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      setModel(new SingletonDataSetManager(dataSet, column, this));
      bindProperties(column);
    }
  }

  private void bindProperties(Column column) {
//    String lab = column.getCaption();
    Color bg = column.getBackground();
    Color fg = column.getForeground();
    Font  f  = column.getFont();
//    if (lab != null) setLabel(lab);
    if (bg != null) setBackground(bg);
    if (fg != null) setForeground(fg);
    if (f != null)  setFont(f);
  }

  // SingletonModel implementation
  // (delegates to model)

  public Object get() { return getModel().get(); }

  // WritableSingletonModel implementation
  // (delegates to model)

  public boolean canSet(boolean startEdit) { return isReadOnly() ? false : getWriteModel().canSet(startEdit); }
  public void set(Object data) {
    if (!isReadOnly())
      getWriteModel().set(data);
  }
  public void touched() { getWriteModel().touched(); }
  public void enableModelEvents(boolean enable) { getWriteModel().enableModelEvents(enable); }

  // Data

  private DataSet dataSet;
  private String columnName;
  private boolean addNotifyCalled = false;
}

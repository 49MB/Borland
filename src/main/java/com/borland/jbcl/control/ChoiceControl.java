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
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.PickListDescriptor;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.SingletonDataSetManager;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.WritableSingletonModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.view.ChoiceView;

public class ChoiceControl
     extends ChoiceView
  implements DataChangeListener, AccessListener, WritableSingletonModel,
             BlackBox, ColumnAware, java.io.Serializable
{
  public ChoiceControl() {
    super();
    setModel(new BasicSingletonContainer());
  }

  public void setModel(SingletonModel sm) {
    if (sm == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(sm);
  }

  public void setItems(DataSet dataSet, String columnName) {
    if (dataSet != null && columnName != null) {
      try {
        // clone the cursor so we don't force a scan through the passed one
        DataSet clonedCursor = dataSet.cloneDataSetView();
        clonedCursor.open();
        clonedCursor.first();
        String[] s = new String[clonedCursor.getRowCount()];
        int i = 0;
        while (clonedCursor.inBounds()) {
          s[i++] = clonedCursor.getString(columnName);
          clonedCursor.next();
        }
        setItems(s);
      }
      catch (Exception x) {
        Diagnostic.printStackTrace(x);
        // Do nothing - just return
      }
    }
  }

  // AccessListener Implementation

  public void accessChange(AccessEvent event) {
    switch(event.getID()) {
      case AccessEvent.OPEN:
        try {
          openDataSet(dataSet);
        }
        catch (Exception ex) {
          event.appendException(ex);
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

//  public void navigated(NavigationEvent event) { updateSelection(); }
  public void dataChanged(DataChangeEvent event) { updateSelection(); }
  public void postRow(DataChangeEvent event) throws Exception { updateSelection(); }

  /**
   * The dataSet property specifies a com.borland.dx.dataset.DataSet
   * object to drive the ChoiceControl's selection
   */
  public DataSet getDataSet() { return dataSet; }
  public void setDataSet(DataSet newDataSet) {
    if (dataSet != null) {
      dataSet.removeAccessListener(this);
//      dataSet.removeNavigationListener(this);
      dataSet.removeDataChangeListener(this);
    }
    openDataSet(newDataSet);
    if (dataSet != null) {
      dataSet.addAccessListener(this);
//      dataSet.addNavigationListener(this);
      dataSet.addDataChangeListener(this);
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
      PickListDescriptor pl = column.getPickList();
      if (pl != null)
        fillFromPickList(pl);
      bindProperties(column);
    }
  }

  private void bindProperties(Column column) {
    Color bg = column.getBackground();
    Color fg = column.getForeground();
    Font  f  = column.getFont();
    if (bg != null) setBackground(bg);
    if (fg != null) setForeground(fg);
    if (f != null)  setFont(f);
  }

  private void fillFromPickList(PickListDescriptor pl) {
  }

  // SingletonModel implementation
  // (delegates to model)

  public Object get() { return getModel().get(); }

  // WritableSingletonModel implementation
  // (delegates to model)

  public boolean canSet(boolean startEdit) { return getWriteModel() != null ? getWriteModel().canSet(startEdit) : false; }
  public void set(Object data) { if (getWriteModel() != null) getWriteModel().set(data); }
  public void touched() { if (getWriteModel() != null) getWriteModel().touched(); }
  public void enableModelEvents(boolean enable) { if (getWriteModel() != null) getWriteModel().enableModelEvents(enable); }

  private DataSet    dataSet;
  private String     columnName;
  private boolean    addNotifyCalled = false;
}

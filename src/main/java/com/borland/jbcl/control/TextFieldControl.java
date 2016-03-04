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
import java.awt.event.KeyEvent;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.text.ItemFormatter;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.SingletonDataSetManager;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.WritableSingletonModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.view.TextFieldView;

public class TextFieldControl
     extends TextFieldView
  implements  DataChangeListener, AccessListener, WritableSingletonModel,
             BlackBox, ColumnAware, java.io.Serializable
{
  public TextFieldControl() {
    super();
    setModel(new BasicSingletonContainer());
  }

  public void setModel(SingletonModel sm) {
    if (sm == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(sm);
  }


  protected void updateText() {
    Column column;
//    Diagnostic.println("updateText:");
    if (model != null && dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      ItemFormatter formatter = column.getFormatter();
      if (formatter != null) {
        Object data = model.get();
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        String text = "";
        try {
          text = formatter.format(data);
        }
        catch (Exception x) {
          com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, x);
          return;
        }
        setSuperText(text != null ? text : "");
        setSelectionStart(selectionStart);
        setSelectionEnd(selectionEnd);
        return;
      }
    }
    super.updateText();
  }

  public void setText(String text) {
    Column column;
    if (canSet(true) && !locateOnly && dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      ItemFormatter formatter = column.getFormatter();
      if (formatter != null) {
        Object data = null;
        try {
          data = formatter.parse(text);
        }
        catch (Exception x) {
          com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, x);
          return;
        }
        try {
          writeModel.set(data);
        }
        catch (Exception x) {}
        return;
      }
    }
    super.setText(text);
  }

  protected void postText() {
    Column column;
    if (canSet(true) && !locateOnly && dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      ItemFormatter formatter = column.getFormatter();
      if (formatter != null) {
        String text = super.getText();
        Object data = null;
        try {
          data = formatter.parse(text);
        }
        catch (Exception x) {
          com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, x);
          return;
        }
        try {
          writeModel.set(data);
        }
        catch (Exception x) {}
        return;
      }
    }
    super.postText();
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

  public void dataChanged(DataChangeEvent e) {
  }

  private String formatData() {
    String text = "";
    Column column;
    if (model != null && dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      Object data = model.get();
      if (data != null) {
        ItemFormatter formatter = column.getFormatter();
        if (formatter != null) {
          try {
            text = formatter.format(data);
          }
          catch (Exception x) {
            com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, x);
          }
        }
        else
          text  = data.toString();
      }
    }
    else
      text  = getText();
    return text;
  }

  public void postRow(DataChangeEvent e) throws Exception {
//    Object data = get();
//    String text = data != null ? data.toString() : "";
    if (!formatData().equals(getText()))
      postText();
  }

  /**
   * The dataSet property specifies a com.borland.dx.dataset.DataSet
   * object to display data from in the field.
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

  // Override key events for DataSet specific actions.
  protected void processKeyEvent(KeyEvent e) {
    super.processKeyEvent(e);
    if (e.getID() != e.KEY_PRESSED || e.isConsumed() || dataSet == null || !dataSet.isOpen())
      return;
    try {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_INSERT :
          if (postOnFocusLost)
            postText();
          dataSet.insertRow(false);
          break;
        case KeyEvent.VK_DELETE :
          if (e.isControlDown())
            dataSet.deleteRow();
          break;
        case KeyEvent.VK_PAGE_DOWN :
          if (postOnFocusLost)
            postText();
          dataSet.next();
          break;
        case KeyEvent.VK_PAGE_UP :
          if (postOnFocusLost)
            postText();
          dataSet.prior();
          break;
      }
    }
    catch (DataSetException x) {
    }
  }

  // SingletonModel implementation
  // (delegates to model)

  public Object get() { return getModel().get(); }

  // WritableSingletonModel implementation
  // (delegates to model)

  public void set(Object data) { if (getWriteModel() != null) getWriteModel().set(data); }
  public void touched() { if (getWriteModel() != null) getWriteModel().touched(); }
  public void enableModelEvents(boolean enable) { if (getWriteModel() != null) getWriteModel().enableModelEvents(enable); }

  // Data

  private DataSet dataSet;
  private String columnName;
  private boolean addNotifyCalled = false;
}

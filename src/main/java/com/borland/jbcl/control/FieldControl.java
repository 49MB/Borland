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
import java.awt.Image;
import java.awt.event.KeyEvent;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.SingletonDataSetManager;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.WritableSingletonModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.FieldView;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.SelectableItemPainter;
import com.borland.jbcl.view.TextItemEditor;
import com.borland.jbcl.view.TextItemPainter;

public class FieldControl
     extends FieldView
  implements DataChangeListener, AccessListener, WritableSingletonModel,
             BlackBox, ColumnAware, java.io.Serializable
{
  public FieldControl() {
    super();
    setDefaultLayout();
  }

  public synchronized String getText() {
    Object data = get();
    return data != null ? data.toString() : "";
  }
  public synchronized void setText(String text) {
    if (canSet(true))
      set(text);
  }

  public void setModel(SingletonModel model) {
    if (model == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(model);
  }

  // AccessListener implementation

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
        safeEndEdit(false);
        setDefaultLayout();
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  public void setTextureName(String path) {
    if (path != null && !path.equals("")) {
      Image i = ImageLoader.load(path, this);
      if (i != null) {
        ImageLoader.waitForImage(this, i);
        textureName = path;
        setTexture(i);
      }
      else {
        throw new IllegalArgumentException(path);
      }
    }
    else {
      textureName = null;
      setTexture(null);
    }
  }
  public String getTextureName() {
    return textureName;
  }

  /**
   * The dataSet property specifies a com.borland.dx.dataset.DataSet
   * object to display data from in the field.
   */
  public DataSet getDataSet() { return dataSet; }
  public void setDataSet(DataSet newDataSet) {
    if (dataSet != null) {
      dataSet.removeAccessListener(this);
//       dataSet.removeNavigationListener(this);
      dataSet.removeDataChangeListener(this);
    }
    openDataSet(newDataSet);
    if (dataSet != null) {
      dataSet.addAccessListener(this);
//      dataSet.addNavigationListener(this);
      dataSet.addDataChangeListener(this);
    }
  }

  private void openDataSet(DataSet newDataSet) {
    dataSet = newDataSet;
    if (dataSet == null) {
      setDefaultLayout();
      return;
    }
    else if (addNotifyCalled && !dataSet.isOpen()) {
      try {
        dataSet.open();
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
        setDefaultLayout();
        return;
      }
    }
    if (dataSet.isOpen()) {
      bindDataSet();
    }
  }

  /**
   * The columnName property specifies a specific column in the dataSet
   * object to display data from in the field.
   */
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

  private void bindDataSet() {
    safeEndEdit(false);
    Column column;
    if (dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      SingletonDataSetManager cursorManager = new SingletonDataSetManager(dataSet, column, this);
      setModel(cursorManager);
      setViewManager(cursorManager);
      bindProperties(column);
    }
  }

  private void bindProperties(Column column) {
    Color bg = column.getBackground();
    Color fg = column.getForeground();
    Font  f  = column.getFont();
    int   a  = column.getAlignment();
    if (bg != null) setBackground(bg);
    if (fg != null) setForeground(fg);
    if (f != null)  setFont(f);
    if (a != 0)     setAlignment(a);
  }

  // NavigationListener Implementation

//  public void navigated(NavigationEvent e) {
//    safeEndEdit(false);
//    repaint();
//  }

  // DataChangeListener Implementation

  public void dataChanged(DataChangeEvent e) {
  }
  public void postRow(DataChangeEvent e) throws Exception {
    endEdit();
  }

  // Override of keyPressed in FieldView to check
  // for DataSet keystrokes
  //
  protected void processKeyPressed(KeyEvent e) {
    super.processKeyPressed(e);
    if (e.isConsumed() || dataSet == null || !dataSet.isOpen())
      return;
    try {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_INSERT :
          dataSet.insertRow(false);
          break;
        case KeyEvent.VK_DELETE :
          if (e.isControlDown())
            dataSet.deleteRow();
          break;
        case KeyEvent.VK_PAGE_DOWN :
          dataSet.next();
          break;
        case KeyEvent.VK_PAGE_UP :
          dataSet.prior();
          break;
      }
    }
    catch (DataSetException x) {
    }
  }

  private void setDefaultLayout() {
    setModel(new BasicSingletonContainer());
    setViewManager(
      new BasicViewManager(
        new FocusableItemPainter(new SelectableItemPainter(new TextItemPainter(getAlignment()))),
        new TextItemEditor(getAlignment())));
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

  protected DataSet dataSet;
  protected String columnName;
  protected boolean addNotifyCalled = false;
  protected String textureName;
}

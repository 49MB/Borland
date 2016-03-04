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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.NavigationEvent;
import com.borland.dx.dataset.NavigationListener;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.VetoException;
import com.borland.jbcl.model.BasicVectorContainer;
import com.borland.jbcl.model.BasicVectorSelection;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.SingleVectorSelection;
import com.borland.jbcl.model.VectorDataSetManager;
import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorSubfocusEvent;
import com.borland.jbcl.model.VectorSubfocusListener;
import com.borland.jbcl.model.WritableVectorModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.ListView;
import com.borland.jbcl.view.SelectableItemPainter;
import com.borland.jbcl.view.TextItemEditor;
import com.borland.jbcl.view.TextItemPainter;

public class ListControl
     extends ListView
  implements NavigationListener, DataChangeListener, AccessListener,
             VectorSubfocusListener, VectorModel, BlackBox, ColumnAware, java.io.Serializable
{
  public ListControl() {
    super();
    buildStringList(null);
    addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (!isReadOnly() && getCount() == 0 && isVariableSize() && isAutoInsert())
          addItem(null);
      }
    });
    addSubfocusListener(this);
  }

  public void setModel(VectorModel model) {
    if (model == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(model);
  }

  public synchronized String[] getItems() {
    VectorModel mod = getModel();
    if (mod == null || mod instanceof VectorDataSetManager)
      return new String[0];
    int count = mod.getCount();
    String[] items = new String[count];
    for (int i = 0; i < count; i++) {
      Object data = mod.get(i);
      items[i] = (data != null) ? data.toString() : "";
    }
    return items;
  }

  public synchronized void setItems(String[] newItems) {
    if (getModel() instanceof VectorDataSetManager)
      throw new IllegalStateException(Res._ItemsAndDataSet);     
    buildStringList(newItems);
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

  private void buildStringList(String[] newItems) {
    if (newItems == null)
      newItems = new String[0];
    navigateDataSet = false;
//    setAutoAppend(false);
    super.setModel(new BasicVectorContainer(newItems));
    super.setViewManager(new BasicViewManager(
      new FocusableItemPainter(
        new SelectableItemPainter(new TextItemPainter())),
        new TextItemEditor()));
    resetSelection();
    if (isShowing() && !isBatchMode())
      doLayout();
    if (topIndex > -1) {
      super.setTopIndex(topIndex);
      topIndex = -1;
    }
  }

  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      if (dataSet != null)
        openDataSet(dataSet);
    }
    if (topIndex > -1) {
      super.setTopIndex(topIndex);
      topIndex = -1;
    }
  }

  public void setTopIndex(int index) {
    if (!addNotifyCalled)
      topIndex = index;
    else
      super.setTopIndex(index);
  }

  // AccessListener Implementation (DataSet)

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
        buildStringList(null);
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  public boolean isMultiSelect() { return multiSelect; }
  public void setMultiSelect(boolean select) {
    multiSelect = select;
    resetSelection();
  }

  public void setAutoInsert(boolean auto) { autoInsert = auto; }
  public boolean isAutoInsert() { return autoInsert; }

  private void resetSelection() {
    if (multiSelect) {
      int[] selections = getSelection().getAll();
      setSelection(new BasicVectorSelection());
      getSelection().add(selections);
    }
    else {
      setSelection(new SingleVectorSelection());
      getSelection().add(getSubfocus());
    }
    repaint(50);
  }

  public void setNavigateWithDataSet(boolean navigate) {
    userSetNavigate = navigate;
    if (userSetNavigate && navigateDataSet && dataSet != null && dataSet.isOpen())
      setSubfocus(dataSet.getRow());
  }
  public boolean isNavigateWithDataSet() {
    return userSetNavigate;
  }

  public void subfocusChanging(VectorSubfocusEvent e) throws VetoException {
    if (dataSet != null && dataSet.isOpen() && userSetNavigate && navigateDataSet) {
      if (dataSet.getRow() != e.getLocation()) {
        try {
          dsNavigating = true;
          if (!dataSet.goToRow(e.getLocation())) {
            dsNavigating = false;
            throw new VetoException();
          }
        }
        catch (DataSetException ex) {
          com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
          dsNavigating = false;
          throw new VetoException();
        }
      }
      dsNavigating = false;
    }
  }

  public void subfocusChanged(VectorSubfocusEvent e) {}

  // NavigationListener Implementation (DataSet)

  public void navigated(NavigationEvent e) {
    if (!dsNavigating && userSetNavigate && navigateDataSet && getSubfocus() != dataSet.getRow())
      setSubfocus(dataSet.getRow());
  }

  public void dataChanged(DataChangeEvent e) {}
  public void postRow(DataChangeEvent e) throws Exception {
    endEdit();
  }

  /**
   * Sets the contents of a Column in a DataSet to the list's contents (strings)
   */
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

  /**
   * The dataSet property specifies a com.borland.dx.dataset.DataSet
   * object to fill the list contents.
   */
  public DataSet getDataSet() { return dataSet; }
  public void setDataSet(DataSet newDataSet) {
    if (dataSet != null) {
      dataSet.removeAccessListener(this);
      dataSet.removeNavigationListener(this);
      dataSet.removeDataChangeListener(this);
    }
    openDataSet(newDataSet);
    if (dataSet != null) {
      dataSet.addAccessListener(this);
      dataSet.addNavigationListener(this);
      dataSet.addDataChangeListener(this);
    }
  }

  /**
   * The columnName property specifies a column name in the dataSet
   * object to fill the list contents.
   */
  public String getColumnName() { return columnName; }
  public void setColumnName(String newColumnName) {
    columnName = newColumnName;
    if (addNotifyCalled)
      openDataSet(dataSet);
  }

  private void openDataSet(DataSet newDataSet) {
    dataSet = newDataSet;
    if (dataSet == null) {
      buildStringList(null);
      return;
    }
    else if (addNotifyCalled && !dataSet.isOpen()) {
      try {
        dataSet.open();
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
        return;
      }
    }
    if (dataSet.isOpen()) {
      bindDataSet();
    }
  }

  private boolean bindDataSet() {
    Column column;
    if (dataSet != null && (column = dataSet.hasColumn(columnName)) != null) {
      setBatchMode(true);
      VectorDataSetManager cursorManager = new VectorDataSetManager(dataSet, column, this);
      super.setModel(cursorManager);
      super.setViewManager(cursorManager);
      navigateDataSet = true;
//      setAutoAppend(true);
      resetSelection();
      bindProperties(column);
      if (topIndex > -1) {
        super.setTopIndex(topIndex);
        topIndex = -1;
      }
      if (isShowing() && !isBatchMode())
        doLayout();
      setBatchMode(false);
      return true;
    }
    else {
      buildStringList(getItems());
      return false;
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

  // VectorModel implementation
  // (delegate to contents)

  public Object get(int index) { return getModel().get(index); }
  public int find(Object index) { return getModel().find(index); }
  public int getCount() { return getModel().getCount(); }

  // WritableVectorModel implementation
  // (delegate to contents)

  public boolean canSet(int index, boolean startEdit) {
    WritableVectorModel wm = getWriteModel();
    return wm != null && wm.canSet(index, startEdit);
  }

  public void set(int index, Object object) {
    WritableVectorModel wm = getWriteModel();
    if (wm != null && wm.canSet(index, true))
      wm.set(index, object);
  }

  public void touched(int index) {
    WritableVectorModel wm = getWriteModel();
    if (wm != null)
      wm.touched(index);
  }

  public boolean isVariableSize() {
    WritableVectorModel wm = getWriteModel();
    return wm != null && wm.isVariableSize();
  }

  public void addItem(Object object) {
    WritableVectorModel wm = getWriteModel();
    if (wm != null && wm.isVariableSize())
      wm.addItem(object);
  }

  public void addItem(int aheadOf, Object object) {
    WritableVectorModel wm = getWriteModel();
    if (wm != null && wm.isVariableSize() && wm.getCount() >= aheadOf)
      wm.addItem(aheadOf, object);
  }

  /**
   * @DEPRECATED - To remove a data item, use removeItems(int index)
   * DANGER!  This method in java.awt.Container is intended to remove the child component at the
   * specified index from a Container.  In this case, that would be the viewport (ListCore) or the
   * scrollbar.
   * DO NOT CALL THIS METHOD unless you intend to remove the list's subcomponents (very unlikely).
   * To remove the list's data, use removeItem(int index).
   */
  public void remove(int index) {
    super.remove(index);
  }

  public void removeItem(int index) {
    WritableVectorModel wm = getWriteModel();
    if (wm != null && wm.isVariableSize() && wm.getCount() > index)
      wm.remove(index);
  }

  /**
   * @DEPRECATED - To remove data items, use removeAllItems()
   * DANGER!  This method in java.awt.Container is intended to remove all child components
   * of a Container.  In this case, that would be the viewport (ListCore), and the scrollbar.
   * DO NOT CALL THIS METHOD unless you intend to remove the list's subcomponents (very unlikely).
   * To remove the list's data, use removeAllItems().
   */
  public void removeAll() {
    super.removeAll();
  }

  public void removeAllItems() {
    WritableVectorModel wm = getWriteModel();
    if (wm != null && wm.isVariableSize())
      wm.removeAll();
  }

  public void enableModelEvents(boolean enable) {
    WritableVectorModel wm = getWriteModel();
    if (wm != null && wm.isVariableSize())
      wm.enableModelEvents(enable);
  }

  protected DataSet dataSet;
  protected String  columnName;
  protected boolean navigateDataSet  = false;
  protected boolean userSetNavigate  = true;
  protected boolean dsNavigating     = false;
  protected boolean multiSelect      = false;
  protected boolean addNotifyCalled  = false;
  protected boolean autoInsert       = true;
  protected int     topIndex         = -1;
  protected String textureName;
}


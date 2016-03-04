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
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetAware;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.NavigationEvent;
import com.borland.dx.dataset.NavigationListener;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.VetoException;
import com.borland.jbcl.model.BasicMatrixContainer;
import com.borland.jbcl.model.BasicMatrixSelection;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ColumnMatrixSelection;
import com.borland.jbcl.model.CrossMatrixSelection;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.MatrixDataSetManager;
import com.borland.jbcl.model.MatrixLocation;
import com.borland.jbcl.model.MatrixModel;
import com.borland.jbcl.model.MatrixSubfocusEvent;
import com.borland.jbcl.model.MatrixSubfocusListener;
import com.borland.jbcl.model.MultiColumnMatrixSelection;
import com.borland.jbcl.model.MultiRowMatrixSelection;
import com.borland.jbcl.model.RowMatrixSelection;
import com.borland.jbcl.model.SingleMatrixSelection;
import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorModelEvent;
import com.borland.jbcl.model.VectorModelListener;
import com.borland.jbcl.model.VectorViewManager;
import com.borland.jbcl.model.WritableMatrixModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.ButtonItemPainter;
import com.borland.jbcl.view.ColumnView;
import com.borland.jbcl.view.DefaultColumnHeaderManager;
import com.borland.jbcl.view.DefaultRowHeaderManager;
import com.borland.jbcl.view.FixedSizeVector;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.GridView;
import com.borland.jbcl.view.SelectableItemPainter;
import com.borland.jbcl.view.SizeVector;
import com.borland.jbcl.view.TextItemEditor;
import com.borland.jbcl.view.TextItemPainter;
import com.borland.jbcl.view.VariableSizeVector;

public class GridControl
     extends GridView
  implements MatrixSubfocusListener, NavigationListener, DataChangeListener, AccessListener,
             WritableMatrixModel, DataSetAware, BlackBox, Serializable
{
  public GridControl() {
    super();
    buildStringGrid(null);
    resetSelection();
    addFocusListener(new GridControl_FocusAdapter(this));
    addSubfocusListener(this);
    addMouseListener(pop);
    addKeyListener(pop);
  }

  public void setModel(MatrixModel model) {
    if (model == this)
      throw new IllegalArgumentException(Res._RecursiveModel);
    super.setModel(model);
  }

  public synchronized void setItems(String[][] newItems) {
    if (dataSet != null)
      throw new IllegalStateException(Res._ItemsAndDataSet);
    buildStringGrid(newItems);
  }
  public synchronized String[][] getItems() {
    MatrixModel mod = getModel();
    if (mod == null || mod instanceof MatrixDataSetManager)
      return new String[0][0];
    int rows = mod.getRowCount();
    int cols = mod.getColumnCount();
    String[][] items = new String[rows][cols];
    for (int r = 0; r < rows; r++)
      for (int c = 0; c < cols; c++) {
        Object data = mod.get(r,c);
        items[r][c] = data != null ? data.toString() : "";
      }
    return items;
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

  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      if (dataSet != null)
        openDataSet(dataSet);
    }
  }

  public void setColumnViews(ColumnView[] newViews) {
    generatedColumns = false;
    super.setColumnViews(newViews);
    if (columnHeader != null) {
      DefaultColumnHeaderManager columnManager = new DefaultColumnHeaderManager(this);
      columnHeader.setModel(columnManager);
      columnHeader.setViewManager(columnManager);
    }
    resetSelection();
  }

  /**
   * Sets the column captions to the values in the passed array.
   * If no columns exist (or the number of passed column captions is
   * different than the number that exist), it creates ColumnViews, and
   * a generic Column Header manager so that the given grid has a column
   * for each entry in columnNames[] - and plugs in a new empty model into
   * the grid (all data is lost).
   *
   * @param columnNames an array of column names (in the order you want them)
   */
  public void setColumnCaptions(String[] columnNames) {
    if (columnNames == null)
      throw new IllegalArgumentException();
    ColumnView[] tempColumns = getColumnViews();
    if (tempColumns != null && tempColumns.length == columnNames.length) {
      setBatchMode(true);
      for (int i = 0; i < columnNames.length; i++)
        tempColumns[i].setCaption(columnNames[i]);
      setBatchMode(false);
    }
    else {
      tempColumns = new ColumnView[columnNames.length];
      SizeVector columnSizes = new VariableSizeVector();
      for (int i = 0; i < tempColumns.length; i++) {
        tempColumns[i] = new ColumnView();
        tempColumns[i].setName(columnNames[i]);
        tempColumns[i].setAlignment(Alignment.LEFT | Alignment.MIDDLE);
        tempColumns[i].setWidth(getDefaultColumnWidth());
        tempColumns[i].setItemMargins(new Insets(0,2,0,2));
        // reset flags, then set caption
        tempColumns[i].resetUserFlags();
        tempColumns[i].setCaption(columnNames[i]);
        columnSizes.setSize(i, tempColumns[i].getWidth());
      }
      setBatchMode(true);
      setColumnSizes(columnSizes);
      String[][] items = new String[1][tempColumns.length];
      setModel(new BasicMatrixContainer(items));
      super.setColumnViews(tempColumns);
      generatedColumns = true;
      setBatchMode(false);
      validate();
    }
  }

  public String[] getColumnCaptions() {
    ColumnView[] tempColumns = getColumnViews();
    if (tempColumns == null || tempColumns.length == 0)
      return new String[0];
    String[] array = new String[tempColumns.length];
    for (int i = 0; i < tempColumns.length; i++)
      array[i] = tempColumns[i].getCaption();
    return array;
  }

  /**
   * The dataSet property binds this grid to the columns in a dataset.
   */
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

  public DataSet getDataSet() {
    return dataSet;
  }

  private void openDataSet(DataSet newDataSet) {
    dataSet = newDataSet;
    if (dataSet == null) {
      buildStringGrid(null);
      return;
    }
    else if (addNotifyCalled && !dataSet.isOpen()) {
      try {
        dataSet.open();
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
        buildStringGrid(null);
        return;
      }
    }
    if (dataSet.isOpen()) {
      bindDataSet();
    }
  }

  public void setSelectRow(boolean select) {
    if (selectRow != select) {
      selectRow = select;
      resetSelection();
    }
  }
  public boolean isSelectRow() { return selectRow; }

  public void setSelectColumn(boolean select) {
    if (selectColumn != select) {
      selectColumn = select;
      resetSelection();
    }
  }
  public boolean isSelectColumn() { return selectColumn; }

  public void setMultiSelect(boolean select) {
    if (multiSelect != select) {
      multiSelect = select;
      resetSelection();
    }
  }
  public boolean isMultiSelect() { return multiSelect; }

  private void resetSelection() {
    if (multiSelect && selectRow && selectColumn)
      setSelection(new CrossMatrixSelection(getRowCount(), getColumnCount(), getSubfocus()));
    else if (multiSelect && selectRow)
      setSelection(new MultiRowMatrixSelection(getColumnCount(), getSelection().getAll()));
    else if (multiSelect && selectColumn)
      setSelection(new MultiColumnMatrixSelection(getRowCount(), getSelection().getAll()));
    else if (multiSelect)
      setSelection(new BasicMatrixSelection(getSelection().getAll()));
    else if (selectRow && selectColumn)
      setSelection(new CrossMatrixSelection(getRowCount(), getColumnCount(), getSubfocus()));
    else if (selectRow)
      setSelection(new RowMatrixSelection(getColumnCount(), getSubfocus().row));
    else if (selectColumn)
      setSelection(new ColumnMatrixSelection(getRowCount(), getSubfocus().column));
    else
      setSelection(new SingleMatrixSelection(getSubfocus()));
    repaint(100);
  }

  /**
   * The 'autoInsert' property allows the grid to automatically insert a row when it
   * gains focus - if it has none.  By default, this property is true.
   */
  public void setAutoInsert(boolean auto) { autoInsert = auto; }
  public boolean isAutoInsert() { return autoInsert; }

  public void setSortOnHeaderClick(boolean sort) { sortOnClick = sort; }
  public boolean isSortOnHeaderClick() {
    if (getModel() instanceof MatrixDataSetManager)
      return sortOnClick;
    else
      return false;
  }

  public void setNavigateWithDataSet(boolean navigate) {
    userSetNavigate = navigate;
    if (userSetNavigate && navigateDataSet && dataSet != null && dataSet.isOpen())
      setSubfocus(dataSet.getRow(), getSubfocus().column);
  }
  public boolean isNavigateWithDataSet() {
    return userSetNavigate;
  }

  public void setShowPopup(boolean show) { pop.setAlive(show); }
  public boolean isShowPopup() { return pop.isAlive(); }

  private void buildStringGrid(String[][] newItems) {
    ColumnView[] oldColumns  = getColumnViews();
    String[] captions = getColumnCaptions();
    if (newItems == null || newItems.length < 1 || newItems[0].length < 1) {
      oldColumns = null;
      captions = new String[1];
      newItems = new String[1][1];
    }
    navigateDataSet = false;
    ColumnView[] tempColumns = new ColumnView[newItems[0].length];
    SizeVector columnSizes = new VariableSizeVector();
    for (int i = 0; i < tempColumns.length; i++) {
      String name = "StringGridColumn" + i;
      if (oldColumns != null && oldColumns.length > i && oldColumns[i].getName() == name)
        tempColumns[i] = oldColumns[i];
      else {
        tempColumns[i] = new ColumnView();
        tempColumns[i].setName(name);
        if (captions.length > i)
          tempColumns[i].setCaption(captions[i]);
        tempColumns[i].setAlignment(Alignment.LEFT | Alignment.MIDDLE);
        tempColumns[i].setWidth(getDefaultColumnWidth());
        tempColumns[i].setItemMargins(new Insets(0,2,0,2));
        tempColumns[i].resetUserFlags();
      }
      columnSizes.setSize(i, tempColumns[i].getWidth());
    }
    setColumnSizes(columnSizes);
    setModel(new BasicMatrixContainer(newItems));
    setViewManager(new BasicViewManager(
      new FocusableItemPainter(
        new SelectableItemPainter(
          new TextItemPainter(Alignment.LEFT | Alignment.MIDDLE))),
      new TextItemEditor(Alignment.LEFT | Alignment.MIDDLE)));
    super.setColumnViews(tempColumns);
    if (columnHeader != null) {
      DefaultColumnHeaderManager columnManager = new DefaultColumnHeaderManager(this);
      columnHeader.setModel(columnManager);
      columnHeader.setViewManager(columnManager);
      columnHeader.repaint(100);
    }
    if (rowHeader != null) {
      DefaultRowHeaderManager rowManager = new DefaultRowHeaderManager(this);
      rowHeader.setModel(rowManager);
      rowHeader.setViewManager(rowManager);
      rowHeader.repaint(100);
    }
    SizeVector rowSizes = new FixedSizeVector(20);
    setRowSizes(rowSizes);
    setNavigateWithDataSet(false);
    setAutoAppend(false);
    resetSelection();
    generatedColumns = true;
    if (isShowing() && !isBatchMode())
      validate();
  }

  // MatrixSubfocusListener Implementation

  private transient boolean dsNavigating = false; // ignore for serialization

  public void subfocusChanging(MatrixSubfocusEvent e) throws VetoException {
    if (dataSet != null && dataSet.isOpen() && userSetNavigate && navigateDataSet) {
      try {
        dataSet.setLastColumnVisited(getColumnView(e.getLocation().column).getName());
        if (dataSet.getRow() != e.getLocation().row) {
          dsNavigating = true;
          if (!dataSet.goToRow(e.getLocation().row)) {
            dsNavigating = false;
            throw new VetoException();
          }
        }
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
        dsNavigating = false;
        throw new VetoException();
      }
      finally {  // In case of a RuntimeException, make sure state cleared.
        dsNavigating = false;
      }
    }
  }

  public void subfocusChanged(MatrixSubfocusEvent e) {}

  // NavigationListener Implementation (DataSet)

  public void navigated(NavigationEvent e) {
    try {
      if (!dsNavigating && userSetNavigate && navigateDataSet && getSubfocus().row != dataSet.getRow() && dataSet.getRowCount() > dataSet.getRow())
        setSubfocus(dataSet.getRow(), getSubfocus().column);
    }
    catch (Exception x) {
    }
  }

  // DataChangeListener Implementation (DataSet)

  public void dataChanged(DataChangeEvent e) {}
  public void postRow(DataChangeEvent e) throws Exception {
    endEdit();
  }

  // AccessListener Implementation (DataSet)

  private transient SizeVector oldRSizes; // ignore for serialization
  private transient SizeVector oldCSizes; // ignore for serialization
  private StorageDataSet oldSDS;
  private MatrixLocation oldSubfocus;
  private ColumnView[] oldViews;

  public void accessChange(AccessEvent e) {
    switch(e.getID()) {
      case AccessEvent.CLOSE:
        if (e.getReason() == AccessEvent.STRUCTURE_CHANGE || e.getReason() == AccessEvent.PROPERTY_CHANGE) {
          // we know that we will get an open soon,
          // so turn on batchMode
          setBatchMode(true);
          oldSubfocus = new MatrixLocation(getSubfocus());
        }

        if (getModel() instanceof MatrixDataSetManager) {
          oldRSizes = getRowSizes();
          oldCSizes = getColumnSizes();
          oldViews  = getColumnViews();
        }

        safeEndEdit(false);

        buildStringGrid(null);
        break;

      case AccessEvent.OPEN:
        DataSet nds = (DataSet)e.getSource();
        try {
          // rebind the dataset
          openDataSet(nds);
        }
        catch (Exception ex) {
          e.appendException(ex);
        }

        if (e.getReason() == AccessEvent.DATA_CHANGE) {
          // A column was sorted, so simply move the subfocus as necessary,
          // and turn batchMode off
          if (navigateDataSet && userSetNavigate)
            if (oldSubfocus != null)
              setSubfocus(dataSet.getRow(), oldSubfocus.column);
            else
              setSubfocus(dataSet.getRow(), 0);
          // put the column views back to maintain column order
          if (oldViews != null)
            setColumnViews(oldViews);
        }
        if (e.getReason() == AccessEvent.UNSPECIFIED && (getColumnViews() == null || getColumnViews().length == 0)) {
          bindDataSet();
        }
        setBatchMode(false);
        oldRSizes = null;
        oldCSizes = null;
        oldViews  = null;
        break;
      default:
        setBatchMode(false);
        Diagnostic.fail();
    }
  }

  public void toggleColumnSort(int column) {
    if (dataSet == null)
      return;
    if (dataSet.isEditing()) {
      try {
        dataSet.post();
      }
      catch (DataSetException x) {
        return;
      }
    }
    String name = getColumnView(column).getName();
    Column c = dataSet.hasColumn(name);
    if (c == null || !dataSet.isSortable(c))
      return;
    try {
      dataSet.toggleViewOrder(name);
    }
    catch(Exception ex) {
      com.borland.jbcl.model.DataSetModel.handleException(ex);
    }
  }

  protected void columnHeaderClicked(int column) {
    if (isSortOnHeaderClick() && dataSet != null) {
      if (!isBatchMode())
        toggleColumnSort(column);
    }
    else
      super.columnHeaderClicked(column);
  }

  private synchronized void bindDataSet() {
    boolean wasBatch = isBatchMode();
    if (dataSet != null && dataSet.getStorageDataSet() != null) {
      buildColumnViews(dataSet);

      setBatchMode(true);
      navigateDataSet = true;

      ColumnView[] columnViews = getColumnViews();
      Column[] columnList  = new Column[columnViews.length];

      for (int index = 0; index < columnViews.length; index++) {
        columnList[index]  = dataSet.hasColumn(columnViews[index].getName());
      }

      if (columnHeader != null) {
        DefaultColumnHeaderManager columnManager = new DefaultColumnHeaderManager(this);
        columnHeader.setModel(columnManager);
        columnHeader.setViewManager(columnManager);
        columnHeader.repaint(100);
      }

      if (rowHeader != null) {
        GridControl_DSRowHeaderManager rowManager = new GridControl_DSRowHeaderManager(dataSet);
        rowHeader.setModel(rowManager);
        rowHeader.setViewManager(rowManager);
        rowHeader.repaint(100);
      }
      getModel();
      MatrixDataSetManager cursorManager = new MatrixDataSetManager(dataSet, columnList, this);
      setModel(cursorManager);
      setViewManager(cursorManager);
    }
    setNavigateWithDataSet(true);
    setAutoAppend(true);
    resetSelection();
    setSubfocus(dataSet.getRow(), getSubfocus().column);

    setBatchMode(wasBatch);
    if (isShowing() && !wasBatch)
      validate();
  }

  private int findOldView(String name) {
    if (oldViews != null) {
      for (int i=0; i<oldViews.length; i++) {
        if (oldViews[i].getName().equals(name)) {
          return i;
        }
      }
    }
    return -1;
  }

  // Returns an array of columnViews constructed from the passed DataSet and array
  // of old columnViews.  If rebuild is true, it ignores old columnViews and constructs
  // a new array based on the passed DataSet.  If rebuild is false, it merely fills
  // in (un-set) properties in the old columnViews with values from the DataSet.
  //
  protected void buildColumnViews(DataSet cursor) {
    ColumnView[] newViews = null;
    int[] newColumnSizes  = null;
    int count = 0;
    int oldColumnsFound   = 0;

    // Reconcile with the columnViews build from last time:
    int index = 0;
    count = cursor.getColumnCount();
    newViews  = new ColumnView[count];
    newColumnSizes = new int[count];

    for (int ordinal=0; ordinal<count; ordinal++) {
      try {
        Column column = cursor.getColumn(ordinal);
        if (column != null && cursor.columnIsVisible(column)) {
          int oldOrdinal = findOldView(column.getColumnName());
          if (oldOrdinal >= 0) {
            newViews[index] = new DatasetColumnView(column, oldViews[oldOrdinal]);
            if (java.beans.Beans.isDesignTime()) {
              newColumnSizes[index] = widthCheck(newViews[index]).getWidth();
            }
            index++;
            oldColumnsFound++;
            continue;
          }
          newViews[index++] = new DatasetColumnView(column);
        }
      }
      catch (DataSetException ex) {
      }
    }
    newViews = purgeViews(newViews);
    count    = newViews.length;

    super.setColumnViews(newViews);

    // Now get the default width of the 'new' columns:
    for (index=0; index<count; index++) {
      if (newColumnSizes[index] == 0)
        newColumnSizes[index] = newViews[index].getWidth();
    }

    SizeVector columnSizeVector = new VariableSizeVector(newColumnSizes);
    setColumnSizes(columnSizeVector);

    int columnDiff = oldColumnsFound - count;
    if (oldRSizes != null && columnDiff >= -1 && columnDiff <= 1)
      setRowSizes(oldRSizes);
    else {
      SizeVector rowSizeVector = new FixedSizeVector(20);
      setRowSizes(rowSizeVector);
    }
  }

  // remove all null ColumnViews
  private ColumnView[] purgeViews(ColumnView[] views) {
    int size = views.length;
    for (int i = 0; i < views.length; i++)
      if (views[i] == null)
        size--;
    ColumnView[] newViews = new ColumnView[size];
    int v = 0;
    for (int i = 0; i < views.length; i++) {
      if (views[i] != null) {
        newViews[v] = views[i];
        v++;
      }
    }
    return widthCheck(newViews);
  }

  private ColumnView[] widthCheck(ColumnView[] views) {
    for (int i = 0; i < views.length; i++) {
      ColumnView cv = views[i];
      GridControl_ColumnView gccv = new GridControl_ColumnView(cv);
      if (gccv.getWidth() == 0 || !gccv.getFlag(gccv.PROP_WIDTH)) {
        if (dataSet != null) {
          try {
            Column c = dataSet.getColumn(gccv.getName());
            Font f = gccv.getFont();
            if (f == null)
              f = getFont();
            FontMetrics fm = getGraphics().getFontMetrics(f);
            if (fm != null) {
              int width = Math.max(fm.stringWidth("e") * c.getWidth(),
                                   fm.stringWidth(gccv.getCaption() != null ? gccv.getCaption() : "") + 10);
              if (width > 0)
                gccv.setWidth(width < 500 ? width : 500);
              else
                gccv.setWidth(getDefaultColumnWidth());
              gccv.setFlag(gccv.PROP_WIDTH, false);
              views[i] = gccv;
            }
          }
          catch (Exception x) {
            gccv.setWidth(getDefaultColumnWidth());
            gccv.setFlag(gccv.PROP_WIDTH, false);
          }
        }
        else {
          gccv.setWidth(getDefaultColumnWidth());
          gccv.setFlag(gccv.PROP_WIDTH, false);
        }
      }
    }
    return views;
  }

  private ColumnView widthCheck(ColumnView cv) {
    GridControl_ColumnView gccv = new GridControl_ColumnView(cv);
    if (gccv.getWidth() == 0 || !gccv.getFlag(gccv.PROP_WIDTH)) {
      if (dataSet != null) {
        try {
          Column c = dataSet.getColumn(gccv.getName());
          Font f = gccv.getFont();
          if (f == null)
            f = getFont();
          FontMetrics fm = getGraphics().getFontMetrics(f);
          if (fm != null) {
            int width = Math.max(fm.stringWidth("e") * c.getWidth(),
                                 fm.stringWidth(gccv.getCaption() != null ? gccv.getCaption() : "") + 10);
            if (width > 0)
              gccv.setWidth(width < 500 ? width : 500);
            else
              gccv.setWidth(getDefaultColumnWidth());
            gccv.setFlag(gccv.PROP_WIDTH, false);
          }
        }
        catch (Exception x) {
          gccv.setWidth(getDefaultColumnWidth());
          gccv.setFlag(gccv.PROP_WIDTH, false);
        }
      }
      else {
        gccv.setWidth(getDefaultColumnWidth());
        gccv.setFlag(gccv.PROP_WIDTH, false);
      }
    }
    return gccv;
  }

  // WritableMatrixModel Implemenation

  public Object get(int row, int column) {
    MatrixModel m = getModel();
    return m != null ? m.get(row, column) : null;
  }

  public MatrixLocation find(Object data) {
    MatrixModel m = getModel();
    return m != null ? m.find(data) : null;
  }

  public int getRowCount() {
    MatrixModel m = getModel();
    return m != null ? m.getRowCount() : 0;
  }

  public int getColumnCount() {
    MatrixModel m = getModel();
    return m != null ? m.getColumnCount() : 0;
  }

  public boolean canSet(int row, int column, boolean startEdit) {
    WritableMatrixModel m = getWriteModel();
    return m != null ? m.canSet(row, column, startEdit) : false;
  }

  public void set(int row, int column, Object data) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.set(row, column, data);
  }

  public void touched(int row, int column) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.touched(row, column);
  }

  public boolean isVariableRows() {
    WritableMatrixModel m = getWriteModel();
    return m != null ? m.isVariableRows() : false;
  }

  public void addRow() {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.addRow();
  }

  public void addRow(int aheadOf) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.addRow(aheadOf);
  }

  public void removeRow(int row) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.removeRow(row);
  }

  public void removeAllRows() {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.removeAllRows();
  }

  public boolean isVariableColumns() {
    WritableMatrixModel m = getWriteModel();
    return m != null ? m.isVariableColumns() : false;
  }

  public void addColumn() {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.addColumn();
  }

  public void addColumn(int aheadOf) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.addColumn(aheadOf);
  }

  public void removeColumn(int column) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.removeColumn(column);
  }

  public void removeAllColumns() {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.removeAllColumns();
  }

  public void enableModelEvents(boolean enable) {
    WritableMatrixModel m = getWriteModel();
    if (m != null)
      m.enableModelEvents(enable);
  }

  private DataSet dataSet;
  private boolean generatedColumns = false;
  private boolean sortOnClick      = true;
  private boolean navigateDataSet  = false;
  private boolean userSetNavigate  = true;
  private boolean selectRow        = false;
  private boolean selectColumn     = false;
  private boolean multiSelect      = false;
  private boolean addNotifyCalled  = false;
  private boolean autoInsert       = true;
  private String[][] items = new String[1][1];
  protected String textureName;
  private GridControl_PopupSupport pop = new GridControl_PopupSupport(this);
}

// This the rowHeader's model and view manager
// This class is used when the GridControl is using a MatrixDataSetManager for its data.
class GridControl_DSRowHeaderManager implements VectorViewManager, VectorModel, DataChangeListener, Serializable
{
  public GridControl_DSRowHeaderManager(DataSet newDataSet) {
    dataSet = newDataSet;
  }
  public ItemPainter getPainter(int index, Object data, int state) { return painter; }
  public ItemEditor getEditor(int index, Object data, int state) { return null; }
  public Object get(int index) {
    if (getCount() > index)
      return Integer.toString(index + 1);
    else
      return "";
  }
  public int find(Object data) { return -1; }
  public int getCount() {
    try { return dataSet.getRowCount(); }
    catch (DataSetException ex) {
      com.borland.jbcl.model.DataSetModel.handleException(dataSet, null, ex);
      return 0;
    }
  }
  public void addModelListener(VectorModelListener listener) {
    modelListeners.add(listener);
    if (modelListeners.getListenerCount() == 1)
      dataSet.addDataChangeListener(this);
  }
  public void removeModelListener(VectorModelListener listener) {
    modelListeners.remove(listener);
    if (modelListeners.getListenerCount() == 0)
      dataSet.removeDataChangeListener(this);
  }
  protected void processModelEvent(VectorModelEvent e) { modelListeners.dispatch(e); }

  public void dataChanged(DataChangeEvent e) {
    processModelEvent(new VectorModelEvent(this, VectorModelEvent.ITEM_ADDED, 0));
  }
  public void postRow(DataChangeEvent e) throws Exception {}

  private transient com.borland.jb.util.EventMulticaster modelListeners = new com.borland.jb.util.EventMulticaster();
  private DataSet dataSet;
  private ItemPainter painter = new ButtonItemPainter(new FocusableItemPainter(new TextItemPainter(Alignment.RIGHT | Alignment.MIDDLE, new Insets(0,3,0,3))));
}

// Context popup menu support class
//
class GridControl_PopupSupport extends MouseAdapter implements ActionListener, KeyListener, Serializable
{
  public static final String CAPTION      = Res._Caption;
  public static final String ALIGNMENT    = Res._Alignment;
  public static final String LEFT         = Res._AlignLeft;
  public static final String RIGHT        = Res._AlignRight;
  public static final String CENTER       = Res._AlignCenter;
  public static final String HSTRETCH     = Res._AlignHStretch;
  public static final String TOP          = Res._AlignTop;
  public static final String MIDDLE       = Res._AlignMiddle;
  public static final String BOTTOM       = Res._AlignBottom;
  public static final String VSTRETCH     = Res._AlignVStretch;
  public static final String SORT         = Res._ToggleSort;
  public static final String POST         = Res._PostChanges;
  public static final String CANCELROW    = Res._CancelRow;
  public static final String INSERTROW    = Res._InsertRow;
  public static final String DELETEROW    = Res._DeleteRow;
  public static final String INSERTCOLUMN = Res._InsertColumn;
  public static final String DELETECOLUMN = Res._DeleteColumn;
  public static final String BACKGROUND   = Res._PickBackground;
  public static final String FOREGROUND   = Res._PickForeground;
  public static final String FONT         = Res._PickFont;

  public GridControl_PopupSupport(GridControl grid) {
    this.grid = grid;
    pop.addActionListener(this);
    grid.getCoreComponent().add(pop);
  }

  public void setAlive(boolean show) { alive = show; }
  public boolean isAlive() { return alive; }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger() && alive) {
      itemInspected(e.getX(), e.getY());
    }
  }

  public void keyPressed(KeyEvent e) {
    if (e.isConsumed() || e.isControlDown())
      return;
    int     key     = e.getKeyCode();
    boolean shift   = e.isShiftDown();
    boolean alt     = e.isAltDown();
    if ((key == KeyEvent.VK_F10 && shift && !alt) ||
        (key == KeyEvent.VK_ENTER && alt && !shift)) {
      if (alive) {
        Rectangle r = grid.getCellRect(grid.getSubfocus());
        itemInspected(r.x + r.width, r.y);
      }
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  /*
   * This works around a bug in JDK1.1: Menu.add(String) does
   * not use its container's font, but uses the default, which
   * is hardcoded to 11 points, very unreadable for some
   * environments, e.g. Japanese. Everything else uses a
   * default of 12.  Instead of calling
   * Menu.add(String), set the font of the menu container to
   * that of its container, then call this function to add
   * menu items.
   */
  private void addPopupItem(Menu menu, String label) {
    MenuItem menuItem = new MenuItem(label);
    menuItem.setFont(menu.getFont());
    menu.add(menuItem);
  }

  void itemInspected(int x, int y) {
    if (grid.getColumnCount() == 0)
      return;
    hit = grid.getSubfocus();
    popupFont = grid.getFont();
    pop.setFont(popupFont);
    pop.removeAll();
    boolean sep = false;
    DataSet ds = grid.getDataSet();
    if (hit != null) {
      ColumnView cv = grid.getColumnView(hit.column);
      String caption = cv != null ? cv.getCaption() : null;
      if (caption == null) {
        caption = java.text.MessageFormat.format(Res._Column, new Object[] {String.valueOf(hit.column)});
      }
      int r = ds != null ? ds.getRow() + 1 : hit.row;
      addPopupItem(pop, java.text.MessageFormat.format(Res._GridPopupTitle, new Object[] {caption, String.valueOf(r)}));
      pop.addSeparator();
      addPopupItem(pop, CAPTION);
      addPopupItem(pop, BACKGROUND);
      addPopupItem(pop, FOREGROUND);
      addPopupItem(pop, FONT);
      Menu align = new Menu(ALIGNMENT);
      align.setFont(popupFont);
      addPopupItem(align, LEFT);
      addPopupItem(align, CENTER);
      addPopupItem(align, RIGHT);
      addPopupItem(align, HSTRETCH);
      align.addSeparator();
      addPopupItem(align, TOP);
      addPopupItem(align, MIDDLE);
      addPopupItem(align, BOTTOM);
      addPopupItem(align, VSTRETCH);
      pop.add(align);
      align.addActionListener(this);
    }
    else {
      sep = true;
    }
    if (ds != null) {
      if (hit != null && grid.isSortOnHeaderClick() && grid.getRowCount() > 0) {
        if (!sep) {
          pop.addSeparator();
          sep = true;
        }
        addPopupItem(pop, SORT);
      }
      if (ds.isEditing() && !grid.isReadOnly()) {
        if (!sep) {
          pop.addSeparator();
          sep = true;
        }
        addPopupItem(pop, POST);
        addPopupItem(pop, CANCELROW);
      }
      if (!ds.isEditingNewRow() && !grid.isReadOnly() && (ds.isEnableInsert() || ds.isEnableDelete())) {
        if (!sep) {
          pop.addSeparator();
          sep = true;
        }
        if (ds.isEnableInsert())
          addPopupItem(pop, INSERTROW);
        if (ds.isEnableDelete())
          addPopupItem(pop, DELETEROW);
      }
    }
    else {
      if (grid.isVariableRows()) {
        if (!sep) {
          pop.addSeparator();
          sep = true;
        }
        addPopupItem(pop, INSERTROW);
        if (hit != null)
          addPopupItem(pop, DELETEROW);
      }
      if (grid.isVariableColumns()) {
        if (!sep) {
          pop.addSeparator();
          sep = true;
        }
        addPopupItem(pop, INSERTCOLUMN);
        if (hit != null)
          addPopupItem(pop, DELETECOLUMN);
      }
    }

    pop.show(grid.getCoreComponent(), x, y);
  }

  public void actionPerformed(ActionEvent e) {
    DataSet ds = grid.getDataSet();
    ColumnView cv = hit != null ? grid.getColumnView(hit.column) : null;
    String a = e.getActionCommand();
    int align = cv.getAlignment();
    if (a == LEFT) {
      align &= ~Alignment.HORIZONTAL;
      align |= Alignment.LEFT;
      cv.setAlignment(align);
    }
    else if (a == CENTER) {
      align &= ~Alignment.HORIZONTAL;
      align |= Alignment.CENTER;
      cv.setAlignment(align);
    }
    else if (a == RIGHT) {
      align &= ~Alignment.HORIZONTAL;
      align |= Alignment.RIGHT;
      cv.setAlignment(align);
    }
    else if (a == HSTRETCH) {
      align &= ~Alignment.HORIZONTAL;
      align |= Alignment.HSTRETCH;
      cv.setAlignment(align);
    }
    else if (a == TOP) {
      align &= ~Alignment.VERTICAL;
      align |= Alignment.TOP;
      cv.setAlignment(align);
    }
    else if (a == MIDDLE) {
      align &= ~Alignment.VERTICAL;
      align |= Alignment.MIDDLE;
      cv.setAlignment(align);
    }
    else if (a == BOTTOM) {
      align &= ~Alignment.VERTICAL;
      align |= Alignment.BOTTOM;
      cv.setAlignment(align);
    }
    else if (a == VSTRETCH) {
      align &= ~Alignment.VERTICAL;
      align |= Alignment.VSTRETCH;
      cv.setAlignment(align);
    }
    else if (a == SORT) {
      grid.toggleColumnSort(hit.column);
    }
    else if (a == CAPTION) {
      cv.setCaption(chooseString(Res._CaptionCaption, cv.getCaption()));
    }
    else if (a == BACKGROUND) {
      cv.setBackground(chooseColor(Res._BGColorCaption, cv.getBackground()));
    }
    else if (a == FOREGROUND) {
      cv.setForeground(chooseColor(Res._FGColorCaption, cv.getForeground()));
    }
    else if (a == FONT) {
      cv.setFont(chooseFont(Res._FontCaption, cv.getFont()));
    }
    else if (a == CANCELROW) {
      if (ds != null) {
        try { ds.cancel(); }
        catch (DataSetException x) {}
      }
    }
    else if (a == POST) {
      if (ds != null) {
        try { ds.post(); }
        catch (DataSetException x) {}
      }
    }
    else if (a == INSERTROW) {
      if (ds != null) {
        try { ds.insertRow(true); }
        catch (DataSetException x) {}
      }
      else if (grid.isVariableRows()) {
        if (hit != null)
          grid.addRow(hit.row);
        else
          grid.addRow();
      }
    }
    else if (a == DELETEROW) {
      if (ds != null) {
        try { ds.deleteRow(); }
        catch (DataSetException x) {}
      }
      else if (grid.isVariableRows())
        grid.removeRow(hit.row);
    }
    else if (a == INSERTCOLUMN) {
      if (grid.isVariableColumns()) {
        if (hit != null)
          grid.addColumn(hit.column);
        else
          grid.addColumn();
      }
    }
    else if (a == DELETECOLUMN) {
      if (grid.isVariableColumns()) {
        grid.removeColumn(hit.column);
      }
    }
  }

  Frame findFrame() {
    Component component = grid;
    while (component != null && !(component instanceof Frame))
      component = component.getParent();
    if (component instanceof Frame)
      return (Frame)component;
    else
      return null;
  }

  Color chooseColor(String title, Color color) {
    Frame f = findFrame();
    if (f == null)
      return null;
    Color old = color;
    ColorChooser cc = new ColorChooser(f, title, color);
    cc.setValue(color);
    cc.show();
    grid.requestFocus();
    if (cc.getResult() == ColorChooser.OK)
      return cc.getValue();
    else
      return old;
  }

  Font chooseFont(String title, Font font) {
    Frame f = findFrame();
    if (f == null)
      return null;
    Font old = font;
    FontChooser fc = new FontChooser(f, title, font);
    fc.setValue(font);
    fc.show();
    grid.requestFocus();
    if (fc.getResult() == FontChooser.OK)
      return fc.getValue();
    else
      return old;
  }

  String chooseString(String title, String string) {
    Frame f = findFrame();
    if (f == null)
      return null;
    String old = string;
    StringInput si = new StringInput(f, title, string);
    si.setValue(string);
    si.show();
    grid.requestFocus();
    if (si.getResult() == StringInput.OK)
      return si.getValue();
    else
      return old;
  }

  private PopupMenu pop = new PopupMenu();
  private GridControl grid;
  private MatrixLocation hit;
  private boolean alive = true;
  private Font popupFont = new Font("dialog", Font.PLAIN, 12);
}

class GridControl_FocusAdapter extends FocusAdapter implements Serializable {
  public GridControl_FocusAdapter(GridControl grid) {
    this.grid = grid;
  }
  public void focusGained(FocusEvent e) {
    if (!grid.isReadOnly() && grid.getRowCount() == 0 && grid.isVariableRows() && grid.isAutoInsert())
      grid.addRow();
  }
  private GridControl grid;
}

// this class allows us to resolve differences between column views
// without whacking user settings
//
class GridControl_ColumnView extends ColumnView implements Serializable
{
  public GridControl_ColumnView() {
    super();
  }
  public GridControl_ColumnView(ColumnView clonee) {
    super(clonee);
  }

  public boolean getFlag(int flag) {
    switch (flag) {
      case PROP_NAME:        return userSetName;
      case PROP_ORDINAL:     return userSetOrdinal;
      case PROP_FONT:        return userSetFont;
      case PROP_ALIGNMENT:   return userSetAlignment;
      case PROP_BACKGROUND:  return userSetBackground;
      case PROP_FOREGROUND:  return userSetForeground;
      case PROP_CAPTION:     return userSetCaption;
      case PROP_WIDTH:       return userSetWidth;
      case PROP_MARGINS:     return userSetMargins;
      case PROP_ITEMPAINTER: return userSetItemPainter;
      case PROP_ITEMEDITOR:  return userSetItemEditor;
    }
    return false;
  }

  public void setFlag(int flag, boolean value) {
    switch (flag) {
      case PROP_NAME:        userSetName        = value; break;
      case PROP_ORDINAL:     userSetOrdinal     = value; break;
      case PROP_FONT:        userSetFont        = value; break;
      case PROP_ALIGNMENT:   userSetAlignment   = value; break;
      case PROP_BACKGROUND:  userSetBackground  = value; break;
      case PROP_FOREGROUND:  userSetForeground  = value; break;
      case PROP_CAPTION:     userSetCaption     = value; break;
      case PROP_WIDTH:       userSetWidth       = value; break;
      case PROP_MARGINS:     userSetMargins     = value; break;
      case PROP_ITEMPAINTER: userSetItemPainter = value; break;
      case PROP_ITEMEDITOR:  userSetItemEditor  = value; break;
    }
  }
}

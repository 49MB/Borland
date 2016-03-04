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
package com.borland.jbcl.model;

import java.awt.Component;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.ExceptionHandler;

/**
 * MatrixDataSetModel is the dataset data-aware adapter that allows matrix jbcl
 * model-view components to talk to com.borland.jbcl.dataset components.
 */
public class MatrixDataSetManager implements WritableMatrixModel,
    MatrixViewManager, DataChangeListener, ExceptionHandler {
  public MatrixDataSetManager(DataSet dataSet, Column[] columns) {
    this(dataSet, columns, null);
  }
  
  public MatrixDataSetManager(DataSet dataSet, Column[] columns,
      Component component) {
    this.dataSet = dataSet;
    dataSetModels = new DataSetModel[columns.length];
    for (int i = 0; i < columns.length; i++) {
      dataSetModels[i] = new DataSetModel(dataSet, columns[i], component);
    }
  }
  
  // MatrixModel Implementation
  
  public Object get(int row, int column) {
    return dataSetModels.length > column ? dataSetModels[column].get(row)
        : null;
  }
  
  public MatrixLocation find(Object data) {
    /*
     * Object coming in must be inspected (via instanceof) to figure out what it
     * is (ie variant, String, Integer, Long, Date, Time, TimeStamp, Boolean,
     * etc) MatrixModel.find is problematic because it does not specify what
     * column to search on. API expects you to return a row and column address.
     * Locate expects you to specify what column to search on.
     */
    return null;
  }
  
  public int getColumnCount() {
    return dataSetModels.length;
  }
  
  public void addModelListener(MatrixModelListener listener) {
    modelListeners.add(listener);
    if (modelListeners.getListenerCount() == 1)
      dataSet.addDataChangeListener(this);
  }
  
  public void removeModelListener(MatrixModelListener listener) {
    modelListeners.remove(listener);
    if (modelListeners.getListenerCount() == 0)
      dataSet.removeDataChangeListener(this);
  }
  
  // WritableMatrixModel Implementation
  
  public boolean canSet(int row, int column, boolean startEdit) {
    return (dataSetModels.length > column) ? dataSetModels[column].canSet(row,
        startEdit) : false;
  }
  
  public void set(int row, int column, Object data) {
    if (dataSetModels.length > column) {
      dataSetModels[column].set(row, data);
      processModelEvent(new MatrixModelEvent(this,
          MatrixModelEvent.ITEM_CHANGED, new MatrixLocation(row, column)));
    }
  }
  
  public void touched(int row, int column) {
    if (dataSetModels.length > column)
      processModelEvent(new MatrixModelEvent(this,
          MatrixModelEvent.ITEM_TOUCHED, new MatrixLocation(row, column)));
  }
  
  public boolean isVariableRows() {
    return true;
  }
  
  public int getRowCount() {
    return (dataSetModels.length > 0) ? dataSetModels[0].getRowCount() : 0;
  }
  
  public final void addRow() {
    if (dataSetModels.length > 0)
      dataSetModels[0].addRow();
  }
  
  public void addRow(int aheadOf) {
    if (dataSetModels.length > 0)
      dataSetModels[0].addRow(aheadOf);
  }
  
  public final void removeRow(int row) {
    if (dataSetModels.length > 0)
      dataSetModels[0].removeRow(row);
  }
  
  public void removeAllRows() {
    // Do nothing. We really don't want this to happen with a DataSet.
  }
  
  public boolean isVariableColumns() {
    return false;
  }
  
  public void addColumn() {
  }
  
  public void addColumn(int aheadOf) {
  }
  
  public void removeColumn(int column) {
  }
  
  public void removeAllColumns() {
  }
  
  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        processModelEvent(new MatrixModelEvent(this,
            MatrixModelEvent.STRUCTURE_CHANGED));
    }
  }
  
  // MatrixViewManager implementation.
  
  public ItemPainter getPainter(int row, int column, Object value, int state) {
    return dataSetModels.length > column ? dataSetModels[column].getPainter(
        row, value) : null;
  }
  
  public ItemEditor getEditor(int row, int column, Object value, int state) {
    return dataSetModels.length > column ? dataSetModels[column].getEditor()
        : null;
  }
  
  // Event Translation Layer
  
  private void processModelEvent(MatrixModelEvent e) {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(e);
  }
  
  // (DataChangeEvent --> MatrixModelEvent)
  
  public void dataChanged(DataChangeEvent e) {
    // Diagnostic.printlnc("MatrixDataSetManager.dataChanged() " + e);
    switch (e.getID()) {
    case DataChangeEvent.ROW_ADDED:
      processModelEvent(new MatrixModelEvent(this, MatrixModelEvent.ROW_ADDED,
          new MatrixLocation(e.getRowAffected(), 0)));
      break;
    case DataChangeEvent.ROW_CANCELED:
    case DataChangeEvent.ROW_DELETED:
      processModelEvent(new MatrixModelEvent(this,
          MatrixModelEvent.ROW_REMOVED, new MatrixLocation(e.getRowAffected(),
              0)));
      break;
    case DataChangeEvent.ROW_CHANGED:
    case DataChangeEvent.ROW_CHANGE_POSTED:
      processModelEvent(new MatrixModelEvent(this,
          MatrixModelEvent.ROW_CHANGED, new MatrixLocation(e.getRowAffected(),
              0)));
      break;
    case DataChangeEvent.DATA_CHANGED:
      processModelEvent(new MatrixModelEvent(this, ModelEvent.STRUCTURE_CHANGED));
      break;
    default:
      Diagnostic.fail();
    }
  }
  
  public void postRow(DataChangeEvent e) throws Exception {
    // Handled directly by controls.
  }
  
  // ExceptionHandler Implementation
  
  public void handleException(Exception x) {
    if (dataSetModels.length > 0 && dataSetModels[0] != null)
      dataSetModels[0].handleThisException(x);
  }
  
  private final DataSet dataSet;
  private final DataSetModel[] dataSetModels;
  private boolean events = true;
  private transient final com.borland.jb.util.EventMulticaster modelListeners = new com.borland.jb.util.EventMulticaster();
}

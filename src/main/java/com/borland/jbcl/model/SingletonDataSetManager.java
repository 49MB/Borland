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
import com.borland.dx.dataset.NavigationEvent;
import com.borland.dx.dataset.NavigationListener;
import com.borland.jb.util.ExceptionHandler;

/**
 * SingletonDataSetManager is the dataset data-aware adapter that allows singleton jbcl
 * model-view components to talk to com.borland.jbcl.dataset components.
 */
public class SingletonDataSetManager
  implements WritableSingletonModel, SingletonViewManager, NavigationListener, DataChangeListener,
             ExceptionHandler
{
  public SingletonDataSetManager(DataSet dataSet, Column column) {
    this(dataSet, column, null);
  }

  public SingletonDataSetManager(DataSet dataSet, Column column, Component component) {
    this.dataSet = dataSet;
    this.dataSetModel = new DataSetModel(dataSet, column, component);
  }

  // SingletonModel Implementation

  public Object get() { return dataSetModel.get(); }

  public void addModelListener(SingletonModelListener listener) {
    modelListeners.add(listener);
    if (modelListeners.getListenerCount() == 1) {
      dataSet.addDataChangeListener(this);
      dataSet.addNavigationListener(this);
    }
  }

  public void removeModelListener(SingletonModelListener listener) {
    modelListeners.remove(listener);
    if (modelListeners.getListenerCount() == 0) {
      dataSet.removeDataChangeListener(this);
      dataSet.removeNavigationListener(this);
    }
  }

  // WritableSingletonModel Implementation

  public boolean canSet(boolean startEdit) { return dataSetModel.canSet(startEdit);}

  public void set(Object data) {
    dataSetModel.set(data);
    processModelEvent();
  }

  public void touched() { processModelEvent(); }

  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        processModelEvent();
    }
  }

  // SingletonViewManager implementation.

  public ItemPainter getPainter(Object value, int state) {
    return dataSetModel.getPainter(value);
  }

  public ItemEditor getEditor(Object value, int state) {
    return dataSetModel.getEditor();
  }

  // Event Translation Layer

  private void processModelEvent() {
    if (events && modelListeners.hasListeners())
      modelListeners.dispatch(singletonModelEvent);
  }

  // DataChangeListener Implementation

  // (DataChangeEvent --> SingletonModelEvent)

  public void dataChanged(DataChangeEvent e) {
    processModelEvent();
  }
  public void postRow(DataChangeEvent e) throws Exception
  {
    // Handled directly by controls.
  }

  // (NavigationEvent --> SingletonModelEvent)

  public void navigated(NavigationEvent event) { processModelEvent();}

  // ExceptionHandler Implementation

  public void handleException(Exception x) {
    dataSetModel.handleThisException(x);
  }

  private DataSet dataSet;
  private DataSetModel        dataSetModel;
  private boolean             events = true;
  private SingletonModelEvent singletonModelEvent = new SingletonModelEvent(this);

  private transient com.borland.jb.util.EventMulticaster modelListeners = new com.borland.jb.util.EventMulticaster();
}

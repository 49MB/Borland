//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.util.Locale;

import com.borland.dx.dataset.AggManager;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.MatrixData;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.Store;
import com.borland.dx.dataset.StoreInternals;

public class MemoryStore implements Store, StoreInternals {
  public void open() /*-throws DataSetException-*/{
  }
  
  public StoreInternals getStoreInternals() {
    return this;
  }
  
  public MatrixData open(StorageDataSet dataSet, MatrixData data,
      int matrixDataType, int aggGroupColumnCount, AggManager aggManager,
      boolean replaceColumns)
  /*-throws DataSetException-*/
  {
    if (data == null) {
      MemoryData memoryData = new MemoryData(dataSet);
      data = memoryData;
      Column[] columns = dataSet.getColumns();// dataSet.columnList.columns;
      if (columns != null) {
        Column column;
        for (Column column2 : columns) {
          column = column2;
          if (!data.validColumnType(column))
            DataSetException.invalidColumnType(column);
          data.addColumn(column);
        }
      }
    }
    data.openData(dataSet, replaceColumns);
    ((MemoryData) data).initRequiredOrdinals(dataSet);
    return data;
  }
  
  public void updateProperties(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    MemoryData data = (MemoryData) MatrixData.getData(dataSet);
    if (data != null)
      data.updateRequiredOrdinals(dataSet);
  }
  
  public void rename(String storeName, String newStoreName)
  /*-throws DataSetException-*/
  {
  }
  
  public StorageDataSet[] empty(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    return null;
  }
  
  public void attach(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
  }
  
  // ! public Store opening(StorageDataSet dataSet) { return this; }
  
  public boolean isReadOnly(String storeName) {
    return false;
  }
  
  public boolean exists(StorageDataSet dataSet) /*-throws DataSetException-*/{
    return false;
  }
  
  public Locale getLocale() {
    return null;
  }
  
  public boolean isDataStore() {
    return false;
  }
  
  public StorageDataSet getDuplicates(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    MemoryData data = (MemoryData) MatrixData.getData(dataSet);
    if (data != null)
      return data.duplicates;
    return null;
  }
  
  public void deleteDuplicates(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    MemoryData data = (MemoryData) MatrixData.getData(dataSet);
    if (data != null)
      data.deleteDuplicates();
  }
  
  public final boolean isSortable(Column column) {
    return column.isSortable();
  }
  
  // Not supported.
  //
  public Object setSavepoint(String name) {
    return null;
  }
  
  // Not supported.
  //
  public boolean rollback(Object savepoint) {
    return false;
  }
  
  public String getSchemaStoreName(String storeName) {
    return storeName;
  }
  
  public String getReadableTableName(String storeName) {
    return storeName;
  }
}

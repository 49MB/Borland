//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.Provider;
import com.borland.dx.dataset.ReadRow;
import com.borland.dx.dataset.StorageDataSet;

/**
  * DataModelProvider adds client-side properties for designer.
  * This class is used internally by other com.borland classes.
  * You should never use this class directly.
*/

public class DataModelProvider extends Provider {
  protected boolean executeOnOpen = true;
  protected int maxRows = 100;

  public void setExecuteOnOpen(boolean value) {
    executeOnOpen = value;
  }

  public boolean getExecuteOnOpen() {
    return executeOnOpen;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int value) {
    maxRows = value;
  }

  public void provideData(StorageDataSet dataSet, boolean toOpen) /*-throws DataSetException-*/ {
  }

  public void provideData(StorageDataSet dataSet, ReadRow fetchAsNeededData) {
  }
}



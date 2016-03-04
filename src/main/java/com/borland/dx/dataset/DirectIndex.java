//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.Vector;

// DO NOT DOCUMENT.
// Currently only DetailIndex does not extend from DirectIndex because
// it is an "indirectIndex" (scoped on detail group).  DetailIndex extends
// from Index.
//
public abstract class DirectIndex extends Index {
  
  public abstract void deleteStore(long internalRow) /*-throws DataSetException-*/;
  
  public abstract boolean addStore(long internalRow) /*-throws DataSetException-*/;
  
  public abstract void loadStore(long internalRow) /*-throws DataSetException-*/;
  
  public abstract void updateStore(long internalRow) /*-throws DataSetException-*/;
  
  public abstract void delete(long internalRow) /*-throws DataSetException-*/;
  
  public abstract void loadSearchValues(Column[] locateColumns,
      RowVariant[] values) /*-throws DataSetException-*/;
  
  public abstract int locate(int startRow, int locateOptions) /*-throws DataSetException-*/;
  
  @Override
  public abstract int locate(int startRow, Column[] scopedColumns,
      RowVariant[] values, int locateOptions) /*-throws DataSetException-*/;
  
  public abstract void sort() /*-throws DataSetException-*/;
  
  @Override
  public abstract void markStatus(int row, int status, boolean on) /*-throws DataSetException-*/;
  
  public abstract boolean resetPending(long internalRow, boolean resolved) /*-throws DataSetException-*/;
  
  // Use for insertes and updates.
  //
  public abstract void resetPending(boolean resolved) /*-throws DataSetException-*/;
  
  public abstract boolean resetPendingDelete(long internalRow, boolean resolved) /*-throws DataSetException-*/;
  
  public abstract void resetPendingDeletes(boolean resolved) /*-throws DataSetException-*/;
  
  public abstract void prepareInsert() /*-throws DataSetException-*/;
  
  public abstract void prepareUpdate(long internalRow) /*-throws DataSetException-*/;
  
  public abstract void prepareUpdate() /*-throws DataSetException-*/;
  
  public abstract void prepareDelete() /*-throws DataSetException-*/;
  
  public abstract void uniqueCheck(long internalRow, RowVariant[] values,
      boolean updating) /*-throws DataSetException-*/;
  
  public abstract boolean markForUpdate(RowVariant[] values) /*-throws DataSetException-*/;
  
  public abstract void close() /*-throws DataSetException-*/;
  
  public abstract boolean isMaintained();
  
  // Used to avoid descending indexes.
  //
  public abstract boolean isIndexMaintained();
  
  public abstract void dropIndex() /*-throws DataSetException-*/;
  
  public abstract boolean hasRowFilterListener(
      Vector<RowFilterListener> rowFilterListeners);
  
  public abstract SortDescriptor getSort();
  
  public abstract int getVisibleMask();
  
  public abstract int getInvisibleMask();
  
  public abstract Vector<RowFilterListener> getRowFilterListeners();
  
  public abstract DirectIndex getIndex();
  
  public abstract MatrixData getData();
  
  public abstract void note(int note) /*-throws DataSetException-*/;
  
  public abstract boolean isInverted();
  
  public abstract void resetStatus();
  
}

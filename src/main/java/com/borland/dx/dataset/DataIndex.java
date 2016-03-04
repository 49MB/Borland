//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.Vector;

import com.borland.jb.util.Diagnostic;

// DO NOT DOCUMENT.
//

public abstract class DataIndex extends DirectIndex {
  public DataIndex(MatrixData data, int visibleMask, int invisibleMask) {
    this.data = data;
    this.invisibleMask = invisibleMask;
    this.visibleMask = visibleMask;
  }
  
  @Override
  public int locate(int startRow, Column[] scopedColumns, RowVariant[] values,
      int locateOptions)
  /*-throws DataSetException-*/
  {
    Diagnostic.check(startRow == 0 || (startRow > 0 && startRow <= lastRow()));
    
    if (lastRow() < 0) {
      // ! Diagnostic.println("Locate on empty index.");
      return -1;
    }
    
    if ((locateOptions & Locate.DETAIL) == 0) {
      // startRow basically ignored if going to first or last.
      //
      if ((locateOptions & Locate.FIRST) != 0)
        startRow = 0;
      else if ((locateOptions & Locate.LAST) != 0)
        startRow = lastRow();
    }
    
    Column[] newLocateColumns = scopedColumns;
    boolean fastLoad = ((locateOptions & Locate.FAST)) != 0
        && locateColumns == newLocateColumns;
    locateColumns = newLocateColumns;
    locateColumnCount = locateColumns.length;
    
    if (!fastLoad)
      loadSearchValues(locateColumns, values);
    
    int lastColumn = locateColumnCount - 1;
    
    if ((Locate.PARTIAL & locateOptions) != 0
        || (Locate.FULL_PARTIAL & locateOptions) != 0) {
      if (locateColumns[lastColumn].getDataType() != Variant.STRING)
        DataSetException.partialSearchForString();
      locatePartialIndex = lastColumn;
    } else
      locatePartialIndex = -1;
    
    locateCaseInsensitive = (Locate.CASE_INSENSITIVE & locateOptions) != 0;
    locateFullPartial = (Locate.FULL_PARTIAL & locateOptions) != 0;
    
    return locate(startRow, locateOptions);
  }
  
  // Meaningless without keys.
  //
  @Override
  public void sort() /*-throws DataSetException-*/{
  }
  
  @Override
  public void markStatus(int row, int status, boolean on)
  /*-throws DataSetException-*/
  {
    long internalRow = internalRow(row);
    // !
    // Diagnostic.println(row+" "+internalRow+" markStatus:  "+Integer.toHexString(status));
    if (on)
      data.setStatus(internalRow, data.getStatus(internalRow) | status);
    else
      data.setStatus(internalRow, data.getStatus(internalRow) & ~status);
    // !
    // Diagnostic.println(row+" "+internalRow+" after markStatus:  "+Integer.toHexString(data.getStatus(internalRow)));
  }
  
  @Override
  public boolean resetPending(long internalRow, boolean resolved)
  /*-throws DataSetException-*/
  {
    int status = data.getStatus(internalRow);
    // !
    // Diagnostic.println(row+" "+internalRow+" resolvePending:  "+Integer.toHexString(status)+" "+Integer.toHexString(newStatus));
    if ((status & RowStatus.PENDING_RESOLVED) != 0) {
      if (!resolved)
        data.setStatus(internalRow, (status & ~RowStatus.PENDING_RESOLVED));
      else {
        status &= ~(RowStatus.PENDING_RESOLVED | RowStatus.UPDATED | RowStatus.INSERTED);
        status |= RowStatus.LOADED;
        data.setStatus(internalRow, status);
        delete(internalRow);
        return true;
      }
    }
    return false;
  }
  
  @Override
  public void resetStatus() {
    while (lastRow() >= 0) {
      delete(0);
    }
  }
  
  // Use for insertes and updates.
  //
  @Override
  public void resetPending(boolean resolved)
  /*-throws DataSetException-*/
  {
    for (int row = 0; row <= lastRow();) {
      if (!resetPending(internalRow(row), resolved))
        ++row;
    }
  }
  
  @Override
  public boolean resetPendingDelete(long internalRow, boolean resolved)
  /*-throws DataSetException-*/
  {
    int status = data.getStatus(internalRow);
    
    if ((status & RowStatus.PENDING_RESOLVED) != 0) {
      if (!resolved)
        data.setStatus(internalRow, (status & ~RowStatus.PENDING_RESOLVED));
      else {
        if ((status & RowStatus.DELETED) == RowStatus.DELETED)
          status = 0;
        else
          status &= ~(RowStatus.PENDING_RESOLVED | RowStatus.UPDATED
              | RowStatus.INSERTED | RowStatus.DELETED);
        data.setStatus(internalRow, status);
        delete(internalRow);
        return true;
      }
    }
    return false;
  }
  
  @Override
  public void resetPendingDeletes(boolean resolved)
  /*-throws DataSetException-*/
  {
    for (int row = 0; row <= lastRow();) {
      if (!resetPendingDelete(internalRow(row), resolved))
        ++row;
    }
  }
  
  @Override
  public void prepareInsert()
  /*-throws DataSetException-*/
  {
    Diagnostic.fail();
  }
  
  @Override
  public void prepareUpdate(long internalRow)
  /*-throws DataSetException-*/
  {
    Diagnostic.fail();
  }
  
  @Override
  public void prepareUpdate()
  /*-throws DataSetException-*/
  {
    Diagnostic.fail();
  }
  
  @Override
  public void prepareDelete()
  /*-throws DataSetException-*/
  {
    Diagnostic.fail();
  }
  
  @Override
  public void uniqueCheck(long internalRow, RowVariant[] values,
      boolean updating)
  /*-throws DataSetException-*/
  {
  }
  
  @Override
  public boolean markForUpdate(RowVariant[] values) /*-throws DataSetException-*/{
    return true;
  }
  
  @Override
  public void close()
  /*-throws DataSetException-*/
  {
  }
  
  @Override
  public boolean isMaintained() {
    return true;
  }
  
  // Used to avoid descending indexes.
  //
  @Override
  public boolean isIndexMaintained() {
    return true;
  }
  
  @Override
  public void dropIndex()
  /*-throws DataSetException-*/
  {
  }
  
  @Override
  public boolean hasRowFilterListener(
      Vector<RowFilterListener> rowFilterListeners) {
    return (rowFilterListeners == this.rowFilterListeners);
  }
  
  @Override
  public long getInternalRow() {
    return internalRow;
  }
  
  @Override
  public final SortDescriptor getSort() {
    return descriptor;
  }
  
  @Override
  public final int getVisibleMask() {
    return visibleMask;
  }
  
  @Override
  public final int getInvisibleMask() {
    return invisibleMask;
  }
  
  @Override
  public final Vector<RowFilterListener> getRowFilterListeners() {
    return rowFilterListeners;
  }
  
  @Override
  public final DirectIndex getIndex() {
    return this;
  }
  
  @Override
  public final MatrixData getData() {
    return data;
  }
  
  @Override
  public boolean isInverted() {
    return false;
  }
  
  // Set by findClosest() methods. Only valid for duration of a synchronized
  // block.
  //
  public long internalRow;
  
  protected Column[] locateColumns;
  protected int locateColumnCount;
  protected int locatePartialIndex;
  protected boolean locateCaseInsensitive;
  protected boolean locateFullPartial;
  
  protected int visibleMask;
  protected int invisibleMask;
  protected SortDescriptor descriptor;
  protected Vector<RowFilterListener> rowFilterListeners;
  
  private final MatrixData data;
}

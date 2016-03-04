//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.Locale;
import java.util.Vector;

import com.borland.dx.dataset.cons.ColumnConst;
import com.borland.jb.util.Diagnostic;

public abstract class IndexData extends MatrixData {
  // ! public abstract long insertStoreRow(ReadRow row, RowVariant[] values, int
  // status) /*-throws DataSetException-*/;
  public abstract void deleteStoreRow(long internalRow) /*-throws DataSetException-*/;
  
  public abstract void emptyStoreRow(long internalRow) /*-throws DataSetException-*/;
  
  public abstract DirectIndex createIndex(StorageDataSet dataSet,
      SortDescriptor descriptor, Vector<RowFilterListener> rowFilterListener,
      DataRow filterRow, RowVariant[] filterValues, int visibleMask,
      int invisibleMask) /*-throws DataSetException-*/;
  
  @Override
  public void prepareRestructure(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
  }
  
  @Override
  public void commitRestructure(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
  }
  
  public void restoreStoreRow(long internalRow) /*-throws DataSetException-*/
  {
  }
  
  public void replaceStoreRow(long internalRow, RowVariant[] values, int status) /*-throws DataSetException-*/{
    DataSetException.missingReplaceRow();
  }
  
  @Override
  public boolean canCalc() {
    return true;
  }
  
  @Override
  public final DirectIndex openIndex(DataSet dataSet, boolean create)
  /*-throws DataSetException-*/
  {
    
    Vector<RowFilterListener> rowFilterListeners = dataSet
        .getRowFilterListeners();
    
    DirectIndex index = openIndex(dataSet.getStorageDataSet(),
        dataSet.getSort(), rowFilterListeners, dataSet.visibleMask,
        dataSet.invisibleMask, create);
    
    Diagnostic.check(index != null || !create);
    return index;
  }
  
  public final DirectIndex openIndex(StorageDataSet dataSet,
      SortDescriptor descriptor, Vector<RowFilterListener> rowFilterListeners,
      int visibleMask, int invisibleMask, boolean create)
  /*-throws DataSetException-*/
  {
    
    if (visibleMask == RowStatus.UPDATED && updateIndex != null)
      return updateIndex;
    
    if (visibleMask == RowStatus.DELETED && deleteIndex != null)
      return deleteIndex;
    
    if (visibleMask == RowStatus.INSERTED && insertIndex != null)
      return insertIndex;
    
    DirectIndex index = findIndex(descriptor, dataSet.getLocale(),
        rowFilterListeners, visibleMask, invisibleMask);
    // if (index == null /* && openPersistentIndexes() */)
    // index = findIndex(descriptor, dataSet.getLocale(), rowFilterListeners,
    // visibleMask, invisibleMask);
    if (index == null && create) {
      if (descriptor != null && descriptor.isUnique()
          && dataSet.getDuplicates() != null) {
        DataSetException.deleteDuplicates();
      }
      DataRow filterRow = null;
      RowVariant[] filterValues = null;
      if (rowFilterListeners != null && !isMemoryData()) {
        filterRow = new DataRow(dataSet);
        filterValues = filterRow.getRowValues(dataSet.getColumnList());
      }
      
      if (descriptor != null && descriptor.isPrimary()) {
        if (autoIncrementOrdinal > -1
            && dataSet.getColumnList().cols[autoIncrementOrdinal]
                .isPrimaryKey())
          DataSetException.duplicatePrimary();
        SortDescriptor indexSort;
        for (int i = 0; i < indexesLength; ++i) {
          indexSort = this.indexes[i].getSort();
          if (indexSort != null && indexSort.isPrimary())
            DataSetException.duplicatePrimary();
        }
        // String[] keys = descriptor.getKeys();
        String[] keys = dataSet.getSortKeys(descriptor);
        
        if (keys == null || keys.length < 1)
          DataSetException.noPrimaryKey();
      }
      
      if (descriptor != null) {
        if (descriptor.isSortAsInserted()) {
          // ! This is comming in late, so we will have to resource the string
          // ! in the next build.
          // !
          if (descriptor.isUnique())
            throw new DataSetException(Res.bundle.format(
                ResIndex.NoSortAsInserted, descriptor.getIndexName()));
        } else if (dataSet.getSortKeys(descriptor).length < 1
            && !descriptor.isSortAsInserted()
            && visibleMask != RowStatus.INSERTED)
          DataSetException.invalidSort(descriptor.getIndexName());
      }
      
      index = createIndex(dataSet, descriptor, rowFilterListeners, filterRow,
          filterValues, visibleMask, invisibleMask);
      
      if (index.isMaintained()) {
        // ! addIndex(/*dataSet,*/ index, visibleMask);
        addIndex(index, visibleMask);
        if (descriptor != null && descriptor.isPrimary())
          initRequiredOrdinals(dataSet);
      }
    }
    return index;
  }
  
  /*
   * public boolean openPersistentIndexes() { return false; }
   */
  
  public void openPersistentIndex(int mask)
  /*-throws DataSetException-*/
  {
  }
  
  // !/*
  // ! public void addIndex(StorageDataSet dataSet, DirectIndex index, int
  // visibleMask) {
  // ! // MatrixData may have been reset due to a Restructure operation.
  // ! //
  // ! if (dataSet != null && dataSet.getMatrixData() != null) {
  // ! dataSet.getMatrixData().addIndex(index, visibleMask);
  // ! }
  // ! else
  // ! addIndex(index, visibleMask);
  // ! }
  // !*/
  
  // !Comment outdated? Should only be called by addIndex that takes a dataSet.
  // This protects against
  // !recursion issues where DataSet may need to be restructured before an index
  // can be built.
  // !
  // !OVERRIDDEN BY TableData!!!
  // !
  public void addIndex(DirectIndex index, int visibleMask) {
    if (visibleMask == RowStatus.UPDATED)
      updateIndex = index;
    else if (visibleMask == RowStatus.DELETED)
      deleteIndex = index;
    else if (visibleMask == RowStatus.INSERTED) {
      Diagnostic.check(insertIndex == null);
      insertIndex = index;
    } else {
      int oldLength = indexes == null ? 0 : indexesLength;
      DirectIndex[] newIndexes = new DirectIndex[oldLength + 1];
      if (oldLength > 0)
        System.arraycopy(indexes, 0, newIndexes, 0, oldLength);
      indexes = newIndexes;
      indexes[oldLength] = index;
      indexesLength = indexes.length;
      // ! Diagnostic.println("addIndex:  "+this);
    }
  }
  
  @Override
  public final void dropAllIndexes()
  /*-throws DataSetException-*/
  {
    
    // openPersistentIndexes();
    for (int i = 0; i < indexesLength; ++i)
      indexes[i].dropIndex();
    
    indexesLength = 0;
    indexes = null;
    // ! /* Don't know why this was done. Causes
    // ! trouble with DataStore because the indexes
    // ! have not been properly closed. TableData also
    // ! tracks when persistent indexes have been opened.
    // ! insertIndex = null;
    // ! deleteIndex = null;
    // ! updateIndex = null;
    // ! */
  }
  
  @Override
  public final void dropIndex(DataSet dataSet)
  /*-throws DataSetException-*/
  {
    dropIndex(dataSet.getSort(), dataSet.getStorageDataSet().getLocale(),
        dataSet.getRowFilterListeners(), dataSet.visibleMask,
        dataSet.invisibleMask);
  }
  
  // WARNING: method signature overridden by TableData.
  //
  @Override
  public boolean dropIndex(SortDescriptor descriptor, Locale locale,
      Vector<RowFilterListener> rowFilter, int visibleMask, int invisibleMask)
  /*-throws DataSetException-*/
  {
    DirectIndex index = findIndex(descriptor, locale, rowFilter, visibleMask,
        invisibleMask);
    if (index != null) {
      index.dropIndex();
      for (int i = 0; i < indexesLength; ++i) {
        if (indexes[i] == index) {
          // !This line was added when I tried to fix a bug,
          // !but did not fix the bug. So if it causes problems,
          // !that's how it got here. Steve.
          //
          index.close();
          --indexesLength;
          if (indexesLength > i)
            System.arraycopy(indexes, i + 1, indexes, i, indexesLength - i);
          DirectIndex[] newIndexes = new DirectIndex[indexesLength];
          System.arraycopy(indexes, 0, newIndexes, 0, indexesLength);
          indexes = newIndexes;
          return true;
        }
      }
    }
    return false;
  }
  
  @Override
  public void freeFetchIndex()
  /*-throws DataSetException-*/
  {
  }
  
  private final boolean indexNameEquals(SortDescriptor s1, SortDescriptor s2) {
    if (s1 == null || s2 == null)
      return false;
    return s1.nameEquals(s2);
  }
  
  private final boolean sortEquals(SortDescriptor s1, SortDescriptor s2,
      Locale locale) {
    if (s1 == s2)
      return true;
    // ! Diagnostic.println("  sortEquals:  "+s2);
    if (s1 == null || s2 == null)
      return false;
    return s1.equals(s2, locale);
  }
  
  // ! OVERRIDDEN BY TABLEDATA.
  //
  public DirectIndex findIndex(SortDescriptor descriptor, Locale locale,
      Vector<RowFilterListener> rowFilterListeners, int visibleMask,
      int invisibleMask) {
    
    // ! Diagnostic.println("findIndex:  "+descriptor);
    for (int index = 0; index < indexesLength; ++index) {
      // !
      // Diagnostic.println("  test:  "+indexes+" "+indexes[index].descriptor);
      if (indexNameEquals(descriptor, indexes[index].getSort()))
        return indexes[index];
      if (sortEquals(indexes[index].getSort(), descriptor, locale)) {
        if (indexes[index].getVisibleMask() == visibleMask
            && indexes[index].getInvisibleMask() == invisibleMask) {
          if (indexes[index].hasRowFilterListener(rowFilterListeners))
            return indexes[index];
        }
      }
    }
    // ! Diagnostic.println("nomatch:  ");
    return null;
  }
  
  @Override
  public final boolean indexExists(SortDescriptor descriptor,
      Vector<RowFilterListener> listener)
  /*-throws DataSetException-*/
  {
    // openPersistentIndexes();
    return (findIndex(descriptor, descriptor.getLocale(), listener,
        RowStatus.DEFAULT, RowStatus.DEFAULT_HIDDEN) != null);
  }
  
  @Override
  public DirectIndex[] getIndices() {
    return indexes;
  }
  
  // !/*
  // ! public final long insertRow(ReadWriteRow row, RowVariant[] values, int
  // status)
  // ! /*-throws DataSetException-*/
  // ! {
  // ! long internalRow = insertStoreRow(row, values, status);
  // ! return internalRow;
  // ! }
  // !*/
  
  @Override
  public final void deleteRow(long internalRow)
  /*-throws DataSetException-*/
  {
    
    if (resolvable) {
      int status = getStatus(internalRow);
      
      // !bug 12951
      // Delete an inserted row means forget that row al together !
      // This gives consistency and simplifies resolvers
      //
      if ((status & RowStatus.INSERTED) != 0)
        emptyStoreRow(internalRow);
      else {
        // Delete an updated row means...
        if ((status & RowStatus.UPDATED) != 0)
          restoreStoreRow(internalRow);
        
        // Now do the real delete work:
        deleteStoreRow(internalRow);
        
        if (deleteIndex != null && resolvable) {
          deleteIndex.addStore(internalRow);
        }
        
        // indexDelete() must come after deleteStoreRow for TableData because
        // that
        // is when tableData initializes the keys of the secondary indexs.
        //
        indexDelete(internalRow);
      }
    } else {
      emptyStoreRow(internalRow);
    }
  }
  
  @Override
  public final void emptyRow(long internalRow)
  /*-throws DataSetException-*/
  {
    emptyStoreRow(internalRow);
    
    // ! /*
    // ! if (deleteIndex != null) {
    // ! deleteIndex.deleteStore(internalRow);
    // ! }
    // ! */
  }
  
  @Override
  public final void updateRow(long internalRow, Variant[] originalValues,
      RowVariant[] values, Column[] updateColumns)
  /*-throws DataSetException-*/
  {
    
    int count = values.length;
    
    if (originalValues == null) {
      for (int ordinal = 0; ordinal < count; ++ordinal)
        values[ordinal].changed = false;
      count = updateColumns.length;
      for (int ordinal = 0; ordinal < count; ++ordinal)
        values[updateColumns[ordinal].getOrdinal()].changed = true;
    } else {
      for (int ordinal = 0; ordinal < count; ++ordinal) {
        values[ordinal].changed = (!values[ordinal]
            .equalsInstance(originalValues[ordinal]));
      }
      
      // !
      // Diagnostic.println("compare:  "+values[ordinal].changed+" "+values[ordinal]+" "+originalValues[ordinal]);
    }
    
    markIndexesForUpdate(values);
    
    updateStoreRow(internalRow, values, updateColumns);
  }
  
  @Override
  public long replaceLoadedRow(long internalRow, ReadWriteRow row,
      RowVariant[] values, int status)
  /*-throws DataSetException-*/
  {
    // ! What does this do for dataStore ?
    // ! markIndexesForUpdate(values);
    
    if (internalRow == -1 || getStatus(internalRow) != RowStatus.LOADED)
      return insertRow(row, values, status);
    replaceStoreRow(internalRow, values, status);
    return internalRow;
  }
  
  public final int saveRow(int status)
  /*-throws DataSetException-*/
  {
    saveOriginal = false;
    if (updateIndex != null && resolvable) {
      Diagnostic.check((status & RowStatus.DELETED) == 0);
      Diagnostic.check((status & RowStatus.ORIGINAL) == 0);
      
      if ((status & (RowStatus.UPDATED | RowStatus.INSERTED)) == 0)
        saveOriginal = true;
    }
    return status;
  }
  
  @Override
  public final void resetPendingStatus(long internalRow, boolean resolved)
  /*-throws DataSetException-*/
  {
    int status = getStatus(internalRow);
    if ((status & RowStatus.PENDING_RESOLVED) != 0) {
      if ((status & RowStatus.DELETED) != 0)
        deleteIndex.resetPendingDelete(internalRow, resolved);
      if ((status & RowStatus.UPDATED) != 0)
        updateIndex.resetPending(internalRow, resolved);
      if ((status & RowStatus.INSERTED) != 0)
        insertIndex.resetPending(internalRow, resolved);
    }
  }
  
  @Override
  public final void resetPendingStatus(boolean resolved)
  /*-throws DataSetException-*/
  {
    // ! bug 5632
    /*
     * updates must be resolved first because if previous insert was done, the
     * RowStatus.PENDING_RESOLVED bit will be cleared even though the insert was
     * done by a previous resolve. - comment is probably obsolete now that
     * resolved status no longer used.
     */
    
    if (updateIndex != null)
      updateIndex.resetPending(resolved);
    
    if (insertIndex != null)
      insertIndex.resetPending(resolved);
    
    if (deleteIndex != null)
      deleteIndex.resetPendingDeletes(resolved);
  }
  
  @Override
  public void resetStatus(StorageDataSet dataSet, int rowStatus) {
    updateIndex = null;
    insertIndex = null;
    deleteIndex = null;
    
    for (long row = getRowCount() - 1; row >= 0; row--) {
      int oldStatus = getStatus(row);
      if (oldStatus != rowStatus) {
        if (rowStatus == RowStatus.LOADED) {
          if ((rowStatus & (RowStatus.ORIGINAL | RowStatus.DELETED)) != 0) {
            deleteRow(row);
            continue;
          }
        }
        
        setStatus(row, rowStatus);
      }
    }
    
    updateProperties(dataSet);
  }
  
  @Override
  public void resetStatus(StorageDataSet dataSet, long row, int rowStatus) {
    updateIndex = null;
    insertIndex = null;
    deleteIndex = null;
    
    int oldStatus = getStatus(row);
    if (oldStatus != rowStatus) {
      if (rowStatus == RowStatus.LOADED) {
        if ((rowStatus & (RowStatus.ORIGINAL | RowStatus.DELETED)) != 0) {
          deleteRow(row);
        }
      }
      
      setStatus(row, rowStatus);
    }
    
    updateProperties(dataSet);
  }
  
  public final void indexAdd(long internalRow)
  /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].addStore(internalRow);
  }
  
  public final void indexDelete(long internalRow)
  /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].deleteStore(internalRow);
  }
  
  public final void markIndexesForUpdate(RowVariant[] values)
  /*-throws DataSetException-*/
  {
    indexUpdateCount = 0;
    for (int index = 0; index < indexesLength; ++index) {
      if (indexes[index].markForUpdate(values))
        ++indexUpdateCount;
    }
  }
  
  @Override
  public final void clearInternalReadRow() {
    internalReadRow = null;
  }
  
  // OVERRIDDEN by TxData.
  //
  @Override
  public final InternalRow getInternalReadRow(StorageDataSet dataSet) {
    if (internalReadRow == null) {
      internalReadRow = new InternalRow(dataSet);
    }
    return internalReadRow;
  }
  
  @Override
  public final void recalc(StorageDataSet storageDataSet, AggManager aggManager)
  /*-throws DataSetException-*/
  {
    // SS: Version: Dynamische CalcFields:
    boolean completeRecalc = storageDataSet.isCompleteRecalc();
    for (long row = 0; row < getRowCount(); row++) {
      int status = getStatus(row);
      int newStatus = status & ~RowStatus.FIELDS_CALCULATED;
      if (status != newStatus)
        setStatus(row, newStatus);
      if (completeRecalc)
        storageDataSet.forceCalcUpdate(row);
    }
  }
  
  @Override
  public void getInsertedRows(StorageDataSet dataSet,
      DataSetView insertedDataSet)
  /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.INSERTED);
    if (insertIndex != null)
      MatrixData.setStorageDataSet(insertedDataSet, dataSet);
  }
  
  @Override
  public void getDeletedRows(StorageDataSet dataSet, DataSetView deletedDataSet)
  /*-throws DataSetException-*/
  {
    MatrixData.setStorageDataSet(deletedDataSet, dataSet);
  }
  
  @Override
  public void getUpdatedRows(StorageDataSet dataSet, DataSetView updatedDataSet)
  /*-throws DataSetException-*/
  {
    MatrixData.setStorageDataSet(updatedDataSet, dataSet);
  }
  
  public final void indexUpdate(long internalRow)
  /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].updateStore(internalRow);
  }
  
  public final void closeIndexes()
  /*-throws DataSetException-*/{
    // ! Diagnostic.println("closeIndexes:  "+this+" "+indexesLength);
    for (int index = 0; index < indexesLength; ++index) {
      indexes[index].close();
    }
    if (insertIndex != null)
      insertIndex.close();
    if (deleteIndex != null)
      deleteIndex.close();
    if (updateIndex != null)
      updateIndex.close();
  }
  
  // Overridden by ResolverData in DataStore so that it synchronizes
  // on its associated TableData. This is important for blob optimizations
  // that reference both blob tables.
  // Also overridden by TableData.
  //
  @Override
  public Object getDataMonitor() {
    return this;
  }
  
  @Override
  public void cancelOperation() {
  }
  
  @Override
  public final void setLoadCancel(StorageDataSet dataSet, LoadCancel loader) {
    dataSet.setLoadCancel(loader);
  }
  
  @Override
  public final long getInsertedRowCount()
  /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.INSERTED);
    return insertIndex == null ? 0 : insertIndex.lastRow() + 1;
  }
  
  @Override
  public final long getDeletedRowCount()
  /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.DELETED);
    return deleteIndex == null ? 0 : deleteIndex.lastRow() + 1;
  }
  
  @Override
  public final long getUpdatedRowCount()
  /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.UPDATED);
    return updateIndex == null ? 0 : updateIndex.lastRow() + 1;
  }
  
  public boolean isMemoryData() {
    return false;
  }
  
  private class ListenerToString<E> {
    public String conv(E[] values) {
      String r = null;
      for (E item : values) {
        String className = getClassName(item);
        if (className != null)
          r = (r == null) ? className : r + "|" + className;
      }
      return r;
    }
  }
  
  @Override
  public boolean needsRecalc(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    String name = new ListenerToString<CalcFieldsListener>().conv(dataSet
        .getCalcFieldsListeners());
    if ((name == null) != (calcFieldsName == null))
      return true;
    
    if (name != null && !name.equals(calcFieldsName))
      return true;
    
    name = new ListenerToString<CalcAggFieldsListener>().conv(dataSet
        .getCalcAggFieldsListeners());
    if ((name == null) != (calcAggFieldsName == null))
      return true;
    
    if (name != null && !name.equals(calcAggFieldsName))
      return true;
    
    return false;
  }
  
  private final String getClassName(Object object) {
    if (object == null)
      return null;
    return object.getClass().getName();
  }
  
  @Override
  public void notifyRecalc(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    calcFieldsName = new ListenerToString<CalcFieldsListener>().conv(dataSet
        .getCalcFieldsListeners());
    calcAggFieldsName = new ListenerToString<CalcAggFieldsListener>()
        .conv(dataSet.getCalcAggFieldsListeners());
  }
  
  // OVERRIDDEN by MemoryData.
  //
  @Override
  public boolean isEmpty()
  /*-throws DataSetException-*/
  {
    return getRowCount() == 0;
  }
  
  @Override
  public String getCalcFieldsName() {
    return calcFieldsName;
  }
  
  @Override
  public String getCalcAggFieldsName() {
    return calcAggFieldsName;
  }
  
  @Override
  public void setCalcNames(String calcFieldsName, String calcAggFieldsName) {
    this.calcFieldsName = calcFieldsName;
    this.calcAggFieldsName = calcAggFieldsName;
  }
  
  @Override
  public MatrixData getData() {
    return this;
  }
  
  @Override
  public final void addDataSet(DataSet listener)
  /*-throws DataSetException-*/
  {
    dataChangeListeners = addDataSet(dataChangeListeners, listener);
  }
  
  @Override
  public final void removeDataSet(DataSet listener)
  /*-throws DataSetException-*/
  {
    dataChangeListeners = removeDataSet(dataChangeListeners, listener);
  }
  
  static int findDataSet(DataSet[] listeners, DataSet listener) {
    if (listeners != null) {
      for (int index = 0; index < listeners.length; ++index)
        if (listeners[index] == listener)
          return index;
    }
    return -1;
  }
  
  static final DataSet[] addDataSet(DataSet[] listeners, DataSet listener) {
    if (findDataSet(listeners, listener) < 0) {
      DataSet[] newListeners;
      
      if (listeners == null)
        newListeners = new DataSet[1];
      else {
        newListeners = new DataSet[listeners.length + 1];
        System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
      }
      
      newListeners[newListeners.length - 1] = listener;
      // Diagnostic.check(newListeners.length < 128);
      listeners = newListeners;
    }
    return listeners;
  }
  
  static final DataSet[] removeDataSet(DataSet[] listeners, DataSet listener) {
    int index = findDataSet(listeners, listener);
    if (index > -1) {
      // Important: hasListeners() expects listeners too be null if there are no
      // listeners.
      if (listeners.length == 1)
        listeners = null;
      else {
        DataSet[] newListeners = new DataSet[listeners.length - 1];
        System.arraycopy(listeners, 0, newListeners, 0, index);
        
        if (index < newListeners.length)
          System.arraycopy(listeners, index + 1, newListeners, index,
              newListeners.length - index);
        
        listeners = newListeners;
      }
    }
    return listeners;
  }
  
  @Override
  public IndexData getIndexData() {
    return this;
  }
  
  @Override
  public void updateRequiredOrdinals(StorageDataSet dataSet) {
    initRequiredOrdinals(dataSet);
  }
  
  public void initRequiredOrdinals(StorageDataSet dataSet) {
    ColumnList columnList = dataSet.getColumnList();
    Column[] columns = columnList.cols;
    int count = columnList.count;
    Column column;
    java.util.Vector list = new java.util.Vector();
    String keys[];
    SortDescriptor descriptor;
    boolean foundPrimary = false;
    requiredOrdinals = null;
    for (int index = 0; index < count; ++index) {
      if (columns[index].isRequired())
        list.addElement(columns[index]);
    }
    for (int index = 0; index < indexesLength; ++index) {
      descriptor = indexes[index].getSort();
      if (descriptor != null && descriptor.isPrimary()) {
        
        keys = dataSet.getSortKeys(descriptor);
        for (String key : keys) {
          column = dataSet.getColumn(key);
          column.setPrimaryKey(true);
          // if (!column.isRequired())
          if (!column.is(ColumnConst.REQUIRED))
            list.addElement(column);
        }
      }
    }
    if (list.size() > 0) {
      requiredOrdinals = new int[list.size()];
      for (int index = 0; index < requiredOrdinals.length; ++index) {
        requiredOrdinals[index] = ((Column) list.elementAt(index)).getOrdinal();
      }
    }
  }
  
  @Override
  public int[] getRequiredOrdinals() {
    return requiredOrdinals;
  }
  
  DataSet[] dataChangeListeners;
  
  protected DirectIndex insertIndex;
  protected DirectIndex updateIndex;
  protected DirectIndex deleteIndex;
  protected boolean resolvable;
  
  private InternalRow internalReadRow;
  protected DirectIndex[] indexes;
  protected int indexesLength;
  protected boolean saveOriginal;
  protected int indexUpdateCount;
  private String calcFieldsName;
  private String calcAggFieldsName;
  private int[] requiredOrdinals;
  public int autoIncrementOrdinal = -1;
}

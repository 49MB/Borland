//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.HashMap;

import com.borland.jb.util.Diagnostic;

/*  This module contains a high speed database column name lookup mechanism.
 This addresses the age old issue of Database Column identifiers.  Many
 database systems allow for identifing columns efficiently by specifying
 an integer "ordinal" that is bassically an index in to an array of columns.
 Typically there is also another column identification technique available
 that allows for column identification by column "name", where name is a
 String representation of a Column name such as "FristName", "Address", or
 "LastName".  Name identification is easier for database programmers to use
 because it is self describing.  It is also more robust because it can
 survive database restructure operations that change the "ordinal" position
 of a Column.  The draw back to using String identifiers has historically
 been that they provide slower access than ordinals because a column "lookup"
 must be performed to compute the ordinal position of the Column.

 The following is a description of a highly optimized system for column
 name lookups.  In this system column names are cached as immutable java
 String "references".  Once a reference is added to the cache, the
 value that the reference points to can never change.  This is because it
 is immutable and because memory in the java environment is only freed when
 no active objects are referencing it (java uses a garbage collection memory
 management scheme).  In this environment, name lookup
 can be sped up dramatically by caching the reference.  Once a String name
 is cached, future lookups can be performed by very efficient String object
 reference comparisons.  No comparison of the String value is necessary.
 once the string has been added to the cache.
 If a String name is not found in the reference cache, a more expensive
 hash lookup by value is performed then the String name is added to the
 reference cache. The reference cache is least recently "allocated".
 This means that the least recently allocated entry is reused when a new
 item needs to be added. The cache is not stored as an array of entries
 because inline compares of cache values is much faster (String arrays are
 range checked and have the overhead of array subscripting).
 */

public class ColumnList {
  public ColumnList() {
    cols = new Column[8];
  }
  
  public ColumnList(int size) {
    cols = new Column[size];
  }
  
  ColumnList(ColumnList columnList)
  /*-throws DataSetException-*/
  {
    this(null, columnList);
  }
  
  ColumnList(StorageDataSet dataSet, ColumnList columnList)
  /*-throws DataSetException-*/
  {
    setColumns(dataSet, columnList.copyColumns());
  }
  
  public ColumnList(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    setColumns(null, dataSet.cloneColumns());
  }
  
  ColumnList cloneColumnList(ColumnList columnList, Column[] shareColumns)
  /*-throws DataSetException-*/
  {
    ColumnList newList = new ColumnList(columnList.count);
    
    // newList.cols = columnList.cloneColumns();//columnList.columns;
    newList.cols = shareColumns;
    
    newList.count = count;
    
    return newList;
  }
  
  // ! ColumnList copyColumnList(ColumnList columnList)
  // ! /*-throws DataSetException-*/
  // ! {
  // ! ColumnList newList = new ColumnList();
  // !
  // ! newList.columns = columnList.columns;
  // !
  // ! return newList;
  // ! }
  
  synchronized ColumnList cloneColumnList(ColumnList columnList,
      String[] columnNames, Column[] shareColumns)
  /*-throws DataSetException-*/
  {
    ColumnList newList = new ColumnList(columnNames.length);
    
    if (columnNames == null)
      DataSetException.throwEmptyColumnNames();
    
    Column newScopedColumns[] = new Column[columnNames.length];
    
    // newList.cols = new Column[columnList.cols.length];
    newList.cols = shareColumns;
    
    newList.count = columnList.count;
    
    Column column;
    Column cloneColumn;
    
    for (int index = 0; index < columnNames.length; ++index) {
      column = columnList.getColumn(columnNames[index]);
      // cloneColumn = (Column)column.clone();
      // newScopedColumns[index] = cloneColumn;
      // newList.cols[column.ordinal] = cloneColumn;
      newScopedColumns[index] = shareColumns[column.getOrdinal()];
    }
    // !/*
    // ! newList.cols = columnList.cloneColumns();//cols;
    // !
    // ! Diagnostic.check(cols.length == columnList.cols.length);
    // !
    // ! for (int index = 0; index < columnNames.length; ++index) {
    // !//! Diagnostic.println("scoped column "+columnNames[index]);
    // !
    // ! newScopedColumns[index] = newList.getColumn(columnNames[index]);
    // ! }
    // !*/
    
    newList.scopedColumns = newScopedColumns;
    
    return newList;
    
  }
  
  synchronized ColumnList cloneColumnList(ColumnList columnList,
      Column[] columns, Column[] shareColumns)
  /*-throws DataSetException-*/
  {
    ColumnList newList = new ColumnList(columns.length);
    
    if (columns == null)
      DataSetException.throwEmptyColumnNames();
    
    Column newScopedColumns[] = new Column[columns.length];
    
    // newList.cols = new Column[columnList.cols.length];
    newList.cols = shareColumns;
    
    newList.count = columnList.count;
    
    Column column;
    Column cloneColumn;
    
    for (int index = 0; index < columns.length; ++index) {
      column = columns[index];
      // cloneColumn = (Column)column.clone();
      // newScopedColumns[index] = cloneColumn;
      // newList.cols[column.ordinal] = cloneColumn;
      newScopedColumns[index] = shareColumns[column.getOrdinal()];
    }
    // !/*
    // ! newList.cols = columnList.cloneColumns();//cols;
    // !
    // ! Diagnostic.check(cols.length == columnList.cols.length);
    // !
    // ! for (int index = 0; index < columnNames.length; ++index) {
    // !//! Diagnostic.println("scoped column "+columnNames[index]);
    // !
    // ! newScopedColumns[index] = newList.getColumn(columnNames[index]);
    // ! }
    // !*/
    
    newList.scopedColumns = newScopedColumns;
    
    return newList;
    
  }
  
  private synchronized final void setColumns(StorageDataSet dataSet,
      Column[] newColumns)
  /*-throws DataSetException-*/
  {
    clearCache();
    cols = new Column[newColumns.length];
    try {
      for (Column newColumn : newColumns)
        addColumn(dataSet, newColumn, true, false);
    } finally {
      setOrdinals();
    }
  }
  
  public synchronized final int addColumn(Column column)
  /*-throws DataSetException-*/
  {
    return addColumn(null, column, false, true);
  }
  
  final void checkChangeColumn(int oldOrdinal, Column newColumn)
  /*-throws DataSetException-*/
  {
    int hash = newColumn.hash;
    
    if (newColumn.getColumnName() == null)
      DataSetException.nullColumnName();
    
    // Cannot use hasColumn because old and new column may be the
    // same object.
    //
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      if (ordinal != oldOrdinal
          && cols[ordinal].hash == hash
          && cols[ordinal].getColumnName().equalsIgnoreCase(
              newColumn.getColumnName()))
        DataSetException.duplicateColumnName();
    }
  }
  
  final synchronized void changeColumn(StorageDataSet dataSet, int oldOrdinal,
      Column newColumn)
  /*-throws DataSetException-*/
  {
    newColumn.bindDataSet(dataSet);
    
    cols[oldOrdinal] = newColumn;
    
    setOrdinals();
  }
  
  public synchronized final int addColumn(StorageDataSet dataSet,
      Column column, boolean isUnique, boolean setOrdinals)
  /*-throws DataSetException-*/
  {
    if (!isUnique) {
      if (column.getColumnName() == null)
        DataSetException.nullColumnName();
      
      if (findOrdinal(column.getColumnName()) != -1) {
        DataSetException.duplicateColumnName();
      }
    }
    
    // Must make a copy, because the Column is about to be
    // bound by the dataSet (iff dataSet is non-null).
    //
    // ! column = (Column)column.clone();
    
    column.bindDataSet(dataSet);
    
    checkForScopedColumns();
    
    if (cols == null || cols.length == count) {
      Column[] newList = new Column[cols == null ? 8 : cols.length + 8];
      
      System.arraycopy(cols, 0, newList, 0, count);
      
      cols = newList;
    }
    
    cols[count] = column;
    ++count;
    
    if (setOrdinals)
      setOrdinals();
    
    if (colsCacheName != null) {
      if (column.ordinal < 0)
        clearCache();
      else {
        colsCacheName.put(column.getColumnName().toUpperCase(), column);
        colsCacheOrdinal.put(column.ordinal, column);
      }
    }
    
    return count - 1;
  }
  
  /*
   * final void addColumns(StorageDataSet dataSet, Column[] newColumns) {
   * checkForScopedColumns();
   * 
   * for (int index = 0; index < newColumns.length; ++index) { if
   * (newColumns[index].getColumnName() == null)
   * DataSetException.nullColumnName(); }
   * 
   * for (int index = 0; index < newColumns.length; ++index)
   * newColumns[index].bindDataSet(dataSet);
   * 
   * cols = new Column[newColumns.length];
   * 
   * System.arraycopy(newColumns, 0, cols, 0, newColumns.length);
   * 
   * setOrdinals();
   * 
   * }
   */
  
  private final void checkList() {
    for (int index = 0; index < count; ++index) {
      for (int index2 = 0; index2 < count; ++index2) {
        Diagnostic.check(index == index2 || cols[index] != cols[index2]);
        Diagnostic.check(index == index2
            || cols[index].ordinal != cols[index2].ordinal);
      }
    }
  }
  
  final synchronized void setOrdinals()
  /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < count; ++ordinal)
      cols[ordinal].ordinal = ordinal;
    
    checkForScopedColumns();
    
    if (check)
      checkList();
  }
  
  private void checkForScopedColumns()
  /*-throws DataSetException-*/
  {
    if (scopedColumns != null)
      DataSetException.cannotUpdateScopedDataRow();
  }
  
  final private void checkOrdinal(int ordinal)
  /*-throws DataSetException-*/
  {
    if (ordinal < 0 || ordinal >= count)
      DataSetException.invalidColumnPosition();
  }
  
  // WARNING. If you change column ordering, an update notification
  // must be sent to all observers of a dataSet. see callers of this
  // method in StorageDataSet.
  //
  // !/*
  // ! final void moveColumn(int oldOrdinal, int newOrdinal)
  // ! /*-throws DataSetException-*/
  // ! {
  // ! checkForScopedColumns();
  // !
  // !
  // ! checkOrdinal(oldOrdinal);
  // ! checkOrdinal(newOrdinal);
  // !
  // ! Column[] newList = new Column[count];
  // ! Column column = cols[oldOrdinal];
  // !
  // ! int pos = 0;
  // ! for (int ordinal = 0; ordinal < count; ++ordinal) {
  // ! if (cols[ordinal] != column) {
  // ! if (pos == newOrdinal)
  // ! ++pos;
  // ! newList[pos++] = cols[ordinal];
  // ! }
  // ! }
  // ! newList[newOrdinal] = column;
  // !
  // ! cols = newList;
  // !
  // ! setOrdinals();
  // ! }
  // !*/
  final synchronized void moveColumn(int oldOrdinal, int newOrdinal)
  /*-throws DataSetException-*/
  {
    checkForScopedColumns();
    
    checkOrdinal(oldOrdinal);
    checkOrdinal(newOrdinal);
    
    Column column = cols[oldOrdinal];
    
    int ordinal = oldOrdinal;
    if (newOrdinal < oldOrdinal) {
      for (; ordinal > newOrdinal; --ordinal)
        cols[ordinal] = cols[ordinal - 1];
    } else {
      for (; newOrdinal > ordinal; ++ordinal)
        cols[ordinal] = cols[ordinal + 1];
    }
    cols[ordinal] = column;
    
    setOrdinals();
  }
  
  final void setDefaultValues(Variant[] rowValues) {
    for (int index = 0; index < rowValues.length; ++index)
      cols[index].getDefault(rowValues[index]);
  }
  
  final synchronized int findScopedOrdinal(String columnName) {
    if (check)
      checkList();
    
    buildCache();
    Column col = colsCacheName.get(columnName.toUpperCase());
    if (col != null)
      return col.ordinal;
    return -1;
  }
  
  public final synchronized int findOrdinal(String columnName) {
    buildCache();
    Column col = colsCacheName.get(columnName.toUpperCase());
    if (col != null)
      return col.ordinal;
    return -1;
  }
  
  final synchronized void clearCache() {
    colsCacheOrdinal = null;
    colsCacheName = null;
  }
  
  final synchronized int getOrdinal(String columnName)
  /*-throws DataSetException-*/
  {
    return findOrdinal(columnName);
  }
  
  final synchronized int hasOrdinal(String columnName) {
    return findOrdinal(columnName);
  }
  
  public synchronized final Column getColumn(String columnName)
  /*-throws DataSetException-*/
  {
    int ordinal = getOrdinal(columnName);
    if (ordinal < 0)
      DataSetException.unknownColumnName(columnName);
    return cols[ordinal];
  }
  
  synchronized final String getBestLocateColumn(int ordinalHint) {
    
    if (ordinalHint > -1 && ordinalHint < count
        && cols[ordinalHint].isTextual())
      return cols[ordinalHint].getColumnName();
    
    for (int ordinal = 0; ordinal < count; ++ordinal)
      if (cols[ordinal].isTextual())
        return cols[ordinal].getColumnName();
    return null;
  }
  
  public final Column hasColumn(String columnName) {
    if (columnName == null)
      return null;
    int ordinal = hasOrdinal(columnName);
    if (ordinal < 0)
      return null;
    return cols[ordinal];
  }
  
  final synchronized Column dropColumn(Column column) /*-throws DataSetException-*/{
    
    checkForScopedColumns();
    
    // Make sure its there, throw exception if not.
    //
    column = getColumn(column.getColumnName());
    
    column.bindDataSet(null);
    
    if ((column.ordinal + 1) < count)
      System.arraycopy(cols, column.ordinal + 1, cols, column.ordinal, count
          - (column.ordinal + 1));
    
    --count;
    
    setOrdinals();
    clearCache();
    
    return column;
  }
  
  final boolean hasRowIds() {
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      if (cols[ordinal].isRowId())
        return true;
    }
    return false;
  }
  
  final void setAllRowIds(boolean setting) {
    for (int ordinal = 0; ordinal < count; ++ordinal)
      cols[ordinal]._setRowId(setting);
  }
  
  final void initColumns()
  /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      cols[ordinal].initColumn();
    }
  }
  
  final void initHasValidations()
  /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      cols[ordinal].initHasValidations();
    }
  }
  
  // The array is copied, but the elements of this list
  // are copied in.
  //
  private final Column[] copyColumns() {
    Column[] newColumns = new Column[count];
    System.arraycopy(cols, 0, newColumns, 0, count);
    return newColumns;
  }
  
  // The array is copied, but the elements of this list
  // are copied in.
  //
  public final Column[] cloneColumns() {
    Column[] newColumns = new Column[count];
    for (int index = 0; index < count; ++index) {
      newColumns[index] = (Column) cols[index].clone();
    }
    return newColumns;
  }
  
  final Column[] getColumns() {
    Column[] newColumns = new Column[count];
    System.arraycopy(cols, 0, newColumns, 0, count);
    return newColumns;
  }
  
  final int getScopedColumnLength() {
    if (scopedColumns == null)
      return count;
    return scopedColumns.length;
  }
  
  final Column[] getScopedColumns() {
    if (scopedColumns == null)
      return cols;
    return scopedColumns;
  }
  
  final Column[] getScopedArray() {
    if (scopedColumns == null)
      return getColumnsArray();
    return scopedColumns;
  }
  
  final boolean hasScopedColumns() {
    return scopedColumns != null;
  }
  
  final String[] getColumnNames(int columnCount) {
    Column[] cols = getScopedColumns();
    String[] columnNames = new String[columnCount];
    
    for (int ordinal = 0; ordinal < columnCount; ordinal++)
      for (int innerOrdinal = 0; innerOrdinal < count; innerOrdinal++)
        if (cols[innerOrdinal].getOrdinal() == ordinal) {
          columnNames[ordinal] = cols[innerOrdinal].getColumnName();
          break;
        }
    
    return columnNames;
  }
  
  final int countCalcColumns(boolean countCalcs, boolean countAggs) {
    int calcCount = 0;
    
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      switch (cols[ordinal].getCalcType()) {
      case CalcType.AGGREGATE:
        if (countAggs) {
          ++calcCount;
        }
        break;
      case CalcType.CALC:
        if (countCalcs)
          ++calcCount;
        break;
      }
    }
    return calcCount;
  }
  
  final int countAggCalcColumns() {
    int calcCount = 0;
    
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      if (cols[ordinal].getCalcType() == CalcType.AGGREGATE) {
        AggDescriptor descriptor = cols[ordinal].getAgg();
        if (descriptor != null) {
          AggOperator aggOperator = descriptor.getAggOperator();
          if (aggOperator == null || aggOperator instanceof CustomAggOperator)
            ++calcCount;
        }
      }
    }
    return calcCount;
  }
  
  final String[] getCalcColumnNames(boolean getCalcs, boolean getAggs) {
    
    int calcCount = countCalcColumns(getCalcs, getAggs);
    
    if (calcCount == 0)
      return null;
    
    String columnNames[] = new String[calcCount];
    
    int index = -1;
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      switch (cols[ordinal].getCalcType()) {
      case CalcType.AGGREGATE:
        if (getAggs)
          columnNames[++index] = cols[ordinal].getColumnName();
        break;
      case CalcType.CALC:
        if (getCalcs)
          columnNames[++index] = cols[ordinal].getColumnName();
        break;
      }
    }
    
    return columnNames;
  }
  
  int getSetType(int ordinal) {
    if (scopedColumns != null) {
      for (Column scopedColumn : scopedColumns) {
        if (scopedColumn.ordinal == ordinal)
          return cols[ordinal].getDataType();
      }
      // This prohibits the setting of a Variant that is created with this
      // setType. Useful to keep un scoped values from being set. Also useful
      // for locate operations to detect what should never be located on.
      //
      return Variant.UNASSIGNED_NULL;
    }
    return cols[ordinal].getDataType();
  }
  
  public final Column[] getColumnsArray() {
    // If bound to a DataSet, DataSet should never publicly expose its
    // ColumnList!!!
    //
    if (count != cols.length)
      cols = copyColumns();
    return cols;
  }
  
  public int getVisibleColumnCount() {
    buildCache();
    int n = 0;
    for (Column col : colsCacheOrdinal.values())
      if (col.isVisible())
        n++;
    return n;
  }
  
  private void buildCache() {
    if (colsCacheName != null)
      return;
    
    Column[] searchCols;
    int searchLen;
    if (scopedColumns != null) {
      searchCols = scopedColumns;
      searchLen = scopedColumns.length;
    } else {
      searchCols = cols;
      searchLen = count;
    }
    
    colsCacheOrdinal = new HashMap<Integer, Column>(searchLen);
    colsCacheName = new HashMap<String, Column>(searchLen);
    for (int i = 0; i < searchLen; i++) {
      Column col = searchCols[i];
      colsCacheOrdinal.put(col.ordinal, col);
      colsCacheName.put(col.getColumnName().toUpperCase(), col);
    }
  }
  
  @Override
  public int hashCode() {
    int code = 0;
    if (scopedColumns != null) {
      for (Column scopedColumn : scopedColumns)
        code += scopedColumn.hashCode() + scopedColumn.ordinal;
    } else {
      for (int i = 0; i < count; i++)
        code += cols[i].hashCode() + cols[i].ordinal;
    }
    return code;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ColumnList))
      return false;
    
    ColumnList o = (ColumnList) obj;
    
    if (o.count != count)
      return false;
    
    if ((scopedColumns == null) != (o.scopedColumns == null))
      return false;
    
    if (scopedColumns != null) {
      if (scopedColumns.length != o.scopedColumns.length)
        return false;
      
      for (int i = 0; i < scopedColumns.length; i++) {
        Column col1 = scopedColumns[i];
        Column col2 = o.scopedColumns[i];
        if (col1.hashCode() != col2.hashCode())
          return false;
        if (col1.ordinal != col2.ordinal)
          return false;
        if (col1.getDataType() != col2.getDataType())
          return false;
      }
    } else {
      for (int i = 0; i < count; i++) {
        Column col1 = cols[i];
        Column col2 = o.cols[i];
        if (col1.hashCode() != col2.hashCode())
          return false;
        if (col1.ordinal != col2.ordinal)
          return false;
        if (col1.getDataType() != col2.getDataType())
          return false;
      }
    }
    return true;
  };
  
  public static final boolean isCompatible(ColumnList a, ColumnList b) {
    return (a == b || (a != null && a.equals(b)));
  }
  
  Column[] cols;
  int count;
  private Column[] scopedColumns; // Are cols being scoped? This means
  // they are based on a sub columnList.
  
  private final static boolean check = false;
  
  HashMap<Integer, Column> colsCacheOrdinal;
  HashMap<String, Column> colsCacheName; // hash
}

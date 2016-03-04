//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.util.Locale;
import java.util.Vector;

import com.borland.dx.dataset.AggManager;
import com.borland.dx.dataset.CalcAggFieldsListener;
import com.borland.dx.dataset.CalcFieldsListener;
import com.borland.dx.dataset.CalcType;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnCompare;
import com.borland.dx.dataset.DataIndex;
import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.DirectIndex;
import com.borland.dx.dataset.InternalRow;
import com.borland.dx.dataset.MatrixData;
import com.borland.dx.dataset.ReadRow;
import com.borland.dx.dataset.RowFilterListener;
import com.borland.dx.dataset.RowStatus;
import com.borland.dx.dataset.RowVariant;
import com.borland.dx.dataset.SortDescriptor;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.TableDataSet;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

public class MemoryData extends com.borland.dx.dataset.IndexData {

  MemoryData(StorageDataSet dataSet)
  /*-throws DataSetException-*/
  {
    super();
    // ! System.err.println("mds.new()");
    statusColumn = new IntColumn(null);

    dataColumns = new DataColumn[0];
    // Reserve row 0 for locate operations.
    //
    rowCount = 1;
    MatrixData.setNeedsRecalc(dataSet, true);
    this.dataSet = dataSet;
  }

  private final void checkType(Column column)
  /*-throws DataSetException-*/
  {
    if (!validColumnType(column))
      DataSetException.invalidColumnType(column);
  }

  @Override
  public final void addColumn(Column column)
  /*-throws DataSetException-*/
  {
    checkType(column);

    MatrixData.setNeedsRecalc(dataSet, true);

    int count = (dataColumns == null) ? 1 : dataColumns.length + 1;

    // ! System.err.println("mds.addColumn(" + column + ")");

    DataColumn[] newColumns = new DataColumn[count];

    if (count > 1)
      System.arraycopy(dataColumns, 0, newColumns, 0, count - 1);

    // ! Diagnostic.println("column:  "+column.ordinal+" "+count);
    Diagnostic.check(column.getOrdinal() == -1
        || column.getOrdinal() == (count - 1));

    newColumns[count - 1] = createColumnStorage(column, allocateNullState());

    // ! if (rowCount > 1) {
    if (count > 1) {
      newColumns[count - 1].growTo(newColumns[0].vectorLength, rowCount);
    } else {
      // ! Diagnostic.println("added column:  "+this);
      // ! Diagnostic.printStackTrace();
      rowCount = 1;
      statusColumn.lastRow = 1;
    }

    dataColumns = newColumns;

    dropAllIndexes();
  }

  @Override
  public void changeColumn(int ordinal, Column oldColumn, Column newColumn)
  /*-throws DataSetException-*/
  {
    if (oldColumn.getDataType() != newColumn.getDataType()) {
      checkType(newColumn);
      dataColumns[ordinal] = createColumnStorage(newColumn, allocateNullState());

      // ! newColumn.ordinal = dataColumns.length;
      // ! addColumn(newColumn);
      // ! dataColumns[oldColumn.ordinal] = dataColumns[dataColumns.length-1];
      // ! newColumn.ordinal = oldColumn.ordinal;
      // ! dropColumn(dataColumns.length-1);

      dropAllIndexes();
    }
  }

  @Override
  public void moveColumn(int oldOrdinal, int newOrdinal)
  /*-throws DataSetException-*/
  {
    // Fixes cliffhanger app. StorageDataSet.calcFieldsRow gets out of synch
    // with StorageDataSet if columns moved, deleted, or added.
    //
    MatrixData.setNeedsRecalc(dataSet, true);

    DataColumn column = dataColumns[oldOrdinal];

    int ordinal = oldOrdinal;
    if (newOrdinal < oldOrdinal) {
      for (; ordinal > newOrdinal; --ordinal)
        dataColumns[ordinal] = dataColumns[ordinal - 1];
    } else {
      for (; newOrdinal > ordinal; ++ordinal)
        dataColumns[ordinal] = dataColumns[ordinal + 1];
    }
    dataColumns[ordinal] = column;

    dropAllIndexes();
  }

  @Override
  public void openData(StorageDataSet dataSet, boolean replaceColumns)
  /*-throws DataSetException-*/
  {
    updateProperties(dataSet);
  }

  @Override
  public void updateProperties(StorageDataSet dataSet) {
    resolvable = dataSet.isResolvable();
    if (insertIndex == null && resolvable) {
      insertIndex = openIndex(dataSet, null, null, RowStatus.INSERTED, 0, true);
      deleteIndex = openIndex(dataSet, null, null, RowStatus.DELETED, 0, true);
      updateIndex = openIndex(dataSet, null, null, RowStatus.UPDATED, 0, true);
    }
  }

  @Override
  public final boolean validColumnType(Column column) {
    switch (column.getDataType()) {
    case Variant.DATE:
    case Variant.TIME:
    case Variant.TIMESTAMP:
    case Variant.STRING:
    case Variant.BIGDECIMAL:
    case Variant.BYTE:
    case Variant.SHORT:
    case Variant.INT:
    case Variant.LONG:
    case Variant.BOOLEAN:
    case Variant.FLOAT:
    case Variant.DOUBLE:
    case Variant.INPUTSTREAM:
    case Variant.OBJECT:
      return true;
    default:
      return false;
    }
  }

  private final DataColumn createColumnStorage(Column column,
      NullState nullState) {
    // ! System.err.println("mds.createColumnStorage()");
    /*
     * switch (column.getCalcType()) { case CalcType.AGGREGATE: case
     * CalcType.LOOKUP: return new CalcPlaceHolderColumn(nullState); }
     */
    if (column instanceof ColumnCompare) // SS: Eigene Sortierung
      return new OwnCompareColumn(nullState, dataSet, column,
          (ColumnCompare) column);

    if (column.getCalcType() == CalcType.LOOKUP && column.getPickList() != null)
      return new LookupColumn(this, dataSet, column, nullState); // SS

    switch (column.getDataType()) {
    case Variant.STRING:
      Locale locale = column.getLocale();// vs
      /*
       * SS if(locale == null || locale.getLanguage().equals("en")) //NORES
       * return new StringColumn(nullState); else return new
       * LocaleStringColumn(nullState, locale);
       */
      if (locale == null) // SS
        return new StringColumn(nullState);
      return new LocaleStringColumn(nullState, locale);// SS
    case Variant.INPUTSTREAM:
      return new BinaryStreamColumn(nullState);
    case Variant.BYTE:
      return new ByteColumn(nullState);
    case Variant.SHORT:
      return new ShortColumn(nullState);
    case Variant.INT:
      return new IntColumn(nullState);

    case Variant.BOOLEAN:
      return new BooleanColumn(nullState);

    case Variant.FLOAT:
      return new FloatColumn(nullState);
    case Variant.DOUBLE:
      return new DoubleColumn(nullState);

    case Variant.LONG:
      return new LongColumn(nullState);

    case Variant.BIGDECIMAL:
      return new BigDecimalColumn(nullState);
    case Variant.TIME:
      return new TimeColumn(nullState);
    case Variant.TIMESTAMP:
      return new TimestampColumn(nullState);
    case Variant.DATE:
      return new DateColumn(nullState);
    case Variant.OBJECT:
      return new ObjectColumn(nullState);
    default:
      break; // to make compiler happy
    }
    Diagnostic.fail();
    return null;
  }

  @Override
  public void dropColumn(int ordinal)
  /*-throws DataSetException-*/
  {
    DataColumn[] newList = new DataColumn[dataColumns.length - 1];

    MatrixData.setNeedsRecalc(dataSet, true);

    System.arraycopy(dataColumns, 0, newList, 0, ordinal);
    if ((ordinal + 1) < dataColumns.length)
      System.arraycopy(dataColumns, ordinal + 1, newList, ordinal,
          dataColumns.length - (ordinal + 1));

    // Do this last in case there are exception.
    //
    dataColumns = newList;
  }

  NullState allocateNullState() {
    if (nullState == null || nullState.slot >= 6) {
      Diagnostic.check(nullState == null || nullState.slot == 6);
      return (nullState = new NullState());
    }
    nullState.slot += 2;
    Diagnostic.check(nullState.slot <= 6 && nullState.slot > 0);
    return nullState;
  }

  @Override
  public final int getStatus(long internalRow) {
    return statusColumn.getInt((int) internalRow);
  }

  @Override
  public final void setStatus(long internalRow, int status) {
    statusColumn.setInt((int) internalRow, status);
  }

  @Override
  public final long getRowCount() {
    return rowCount;
  }

  @Override
  public boolean isEmpty()
  /*-throws DataSetException-*/
  {
    return getRowCount() <= 1;
  }

  @Override
  public final long insertRow(ReadRow row, RowVariant[] values, int status)
  /*-throws DataSetException-*/
  {

    if (hasUnique)
      uniqueCheck(0, values, false);
    // ! System.err.println("addStoreRow: values = " + values + "[" +
    // values.length + "]");
    // ! System.err.println("dataColumns = " + dataColumns + "[" +
    // dataColumns.length + "]");
    int internalRow = appendRow();
    Diagnostic.check(status != 0);
    statusColumn.setInt(internalRow, status);

    for (int ordinal = 0; ordinal < values.length; ++ordinal) {
      // ! System.err.println("addStoreRow: ordinal = " + ordinal);
      // ! System.err.println(" value is " + values[ordinal]);
      dataColumns[ordinal].setVariant(internalRow, values[ordinal]);
    }

    // Add quick status check for high speed loading.
    //
    if (insertIndex != null && (status & RowStatus.INSERTED) != 0 && resolvable)
      insertIndex.addStore(internalRow);

    indexAdd(internalRow);

    return internalRow;
  }

  @Override
  public final void deleteStoreRow(long internalRow)
  /*-throws DataSetException-*/
  {
    // !/*
    // ! if ((status & (RowStatus.INSERTED|RowStatus.UPDATED)) != 0) {
    // ! if ((status & RowStatus.INSERTED) != 0) {
    // ! if (insertIndex != null)
    // ! insertIndex.deleteStore(internalRow);
    // ! }
    // ! else if ((status & RowStatus.UPDATED) != 0) {
    // ! if (updateIndex != null)
    // ! updateIndex.deleteStore(internalRow);
    // ! }
    // ! }
    // ! statusColumn.setInt((int)internalRow, status | RowStatus.DELETED);
    // !*/
    int status = statusColumn.getInt((int) internalRow);
    statusColumn.setInt((int) internalRow, status | RowStatus.DELETED);
  }

  @Override
  public final void emptyStoreRow(long internalRow)
  /*-throws DataSetException-*/
  {
    // !BUG:12381
    // Must do before emptyStoreRow, because values are wiped out.
    // TableData handles this differently. indexDelete must come after.
    //
    indexDelete(internalRow);

    int status = statusColumn.getInt((int) internalRow);

    if ((status & (RowStatus.INSERTED | RowStatus.UPDATED)) != 0) {
      if ((status & RowStatus.INSERTED) != 0) {
        if (insertIndex != null)
          insertIndex.deleteStore(internalRow);
      } else if ((status & RowStatus.UPDATED) != 0) {
        if (updateIndex != null) {
          updateIndex.deleteStore(internalRow);
          setNullValues(originalColumn.getInt((int) internalRow));
        }
      }
    }

    if (deleteIndex != null)
      deleteIndex.deleteStore(internalRow);
    statusColumn.setInt((int) internalRow, 0);
    // Array entries still stay allocated, but space for values can be freed
    // up.
    //
    setNullValues((int) internalRow);
    // ! if (emptyCount == 0)
    // ! lastEmpty = (int) internalRow;
    // ! ++emptyCount;
  }

  private final void setNullValues(int internalRow) {
    for (DataColumn dataColumn : dataColumns) {
      dataColumn.setVariant(internalRow, RowVariant.getNullVariant());
    }
  }

  private void uniqueCheck(long internalRow, RowVariant[] values,
      boolean updating)
  /*-throws DataSetException-*/
  {
    Diagnostic.check(hasUnique);
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].uniqueCheck(internalRow, values, updating);
  }

  @Override
  public final void updateStoreRow(long internalRow, RowVariant[] values,
      Column[] updateColumns)
  /*-throws DataSetException-*/
  {

    int status = getStatus((int) internalRow);
    if (status != 0) {
      if (hasUnique)
        uniqueCheck(internalRow, values, true);
      status = saveRow(status);

      indexPrepareUpdate((int) internalRow);

      if (saveOriginal) {

        Diagnostic.check(resolvable); // saveOriginal should be false otherwise.

        saveStoreRow((int) internalRow, status);

        if ((status & RowStatus.UPDATED) == 0) {
          status |= RowStatus.UPDATED;
          setStatus(internalRow, status);
          updateIndex.addStore(internalRow);
        } else
          setStatus(internalRow, status);

      }
      if (updateColumns != null) {
        int targetOrdinal;
        for (Column updateColumn : updateColumns) {
          targetOrdinal = updateColumn.getOrdinal();
          dataColumns[targetOrdinal].setVariant((int) internalRow,
              values[targetOrdinal]);
        }
      } else {
        for (int ordinal = 0; ordinal < values.length; ++ordinal)
          dataColumns[ordinal].setVariant((int) internalRow, values[ordinal]);
      }

      indexUpdate(internalRow);
    }
  }

  @Override
  public void restoreStoreRow(long internalRow)
  /*-throws DataSetException-*/
  {
    int savedRow = originalColumn.getInt((int) internalRow);
    indexPrepareUpdate((int) internalRow);
    if (savedRow != 0) {
      copyRow(savedRow, (int) internalRow);
      setNullValues(savedRow);
    }
    indexUpdate(internalRow);
    if (updateIndex != null)
      updateIndex.deleteStore(internalRow);
    setStatus(internalRow, RowStatus.LOADED);
  }

  @Override
  public void replaceStoreRow(long internalRow, RowVariant[] values, int status)
  /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < values.length; ++ordinal)
      dataColumns[ordinal].setVariant((int) internalRow, values[ordinal]);
    setStatus(internalRow, status);
  }

  private final void indexPrepareUpdate(int internalRow)
  /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].prepareUpdate(internalRow);
  }

  // ! public final void setNeedsRecalc(boolean recalc) {
  // ! needsRecalc = recalc;
  // ! }
  // ! public boolean getNeedsRecalc() {
  // ! return needsRecalc;
  // ! }

  @Override
  public final boolean copyStreams() {
    return true;
  }

  @Override
  public boolean getNeedsRestructure() {
    return false;
  }

  @Override
  public MatrixData restructure(StorageDataSet dataSet,
      Vector<CalcFieldsListener> calcListener,
      Vector<CalcAggFieldsListener> calcAggFieldsListener)
  /*-throws DataSetException-*/
  {
    return this;
  }

  @Override
  public final void getVariant(long internalRow, int ordinal, Variant value) {
    if (internalRow >= dataColumns[ordinal].vectorLength)
      value.setNull(Variant.UNASSIGNED_NULL);
    else
      dataColumns[ordinal].getVariant((int) internalRow, value);
  }

  @Override
  public final void setVariant(long internalRow, int ordinal, Variant value) {
    if (internalRow >= dataColumns[ordinal].vectorLength)
      dataColumns[ordinal].growTo((int) internalRow + 1, (int) internalRow + 1);
    dataColumns[ordinal].setVariant((int) internalRow, value);
  }

  @Override
  public final void getRowData(long internalRow, Variant[] values)
  /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal)
      dataColumns[ordinal].getVariant((int) internalRow, values[ordinal]);
  }

  // Keep private, doesn't do everything needed for adding rows!
  //
  private final int appendRow() {
    int pos;

    // ! Diagnostic..println("dataColumns:  "+dataColumns.length);
    for (DataColumn dataColumn : dataColumns) {
      // !
      // Diagnostic..println("ordinal "+ordinal+" rowCount "+rowCount+" ordinalCount "+dataColumns[ordinal].lastRow);
      Diagnostic.check(rowCount == dataColumn.lastRow);
      dataColumn.append();
    }
    // !
    // Diagnostic.println(this+" rowCount:  "+rowCount+" "+statusColumn.lastRow);
    Diagnostic.check(rowCount == statusColumn.lastRow);
    statusColumn.append();
    return rowCount++;
  }

  private final void copyRow(int sourceRow, int destRow) {

    for (DataColumn dataColumn : dataColumns)
      dataColumn.copy(sourceRow, destRow);
  }

  private final void copyValues(int destRow, Variant[] values) {

    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal)
      dataColumns[ordinal].setVariant(destRow, values[ordinal]);
  }

  private final void saveStoreRow(int internalRow, int status)
  /*-throws DataSetException-*/
  {
    int savedRow = 0;

    if (originalColumn == null)
      originalColumn = new IntColumn(null);
    growTo(originalColumn);

    savedRow = originalColumn.getInt(internalRow);

    if (savedRow == 0)
      savedRow = appendRow();

    copyRow(internalRow, savedRow);
    statusColumn.setInt(savedRow, RowStatus.ORIGINAL);
    // ! Diagnostic..println(Integer.toString(statusColumn.getInt(savedRow),
    // 16)+" setting change "+internalRow);
    originalColumn.setInt(internalRow, savedRow);
  }

  final void growTo(IntColumn intColumn) {
    intColumn.growTo(dataColumns[0].vectorLength, rowCount);
  }

  @Override
  public final DirectIndex createIndex(StorageDataSet dataSet,
      SortDescriptor descriptor, Vector<RowFilterListener> rowFilterListeners,
      DataRow filterRowDummy, RowVariant[] filterValues, int visibleMask,
      int invisibleMask)
  /*-throws DataSetException-*/
  {
    DataIndex index;

    InternalRow filterRow = null;
    if (rowFilterListeners != null)
      filterRow = getInternalReadRow(dataSet);

    index = null;
    boolean sortAsInserted = false;
    IntColumn insertColumn = null;
    if (descriptor != null) {
      sortAsInserted = descriptor.isSortAsInserted();
      int dataKeyCount = descriptor.keyCount();
      int keyCount = dataKeyCount + (sortAsInserted ? 1 : 0);
      DataColumn[] keyColumns = new DataColumn[keyCount];
      Column[] columns = new Column[keyCount];
      Column column;

      int keyIndex;
      for (keyIndex = 0; keyIndex < dataKeyCount; ++keyIndex) {
        column = dataSet.getColumn(descriptor.getKeys()[keyIndex]);
        if (!column.isSortable())
          DataSetException.notSortable();
        columns[keyIndex] = column;
        keyColumns[keyIndex] = dataColumns[column.getOrdinal()];
      }

      long rowCount = getRowCount();
      if (sortAsInserted) {
        insertColumn = new IntColumn(null);
        long minLength = keyColumns.length > 1 ? keyColumns[0].vectorLength
            : rowCount;
        if (minLength > insertColumn.vectorLength)
          insertColumn.grow((int) minLength);
        keyColumns[keyIndex] = insertColumn;
        ++keyIndex;
      }

      if (keyCount > 0 && keyIndex >= keyCount) {
        index = new SortedMemoryIndex(descriptor, rowFilterListeners,
            filterRow, this, dataColumns, visibleMask, invisibleMask,
            statusColumn, keyColumns, columns);
        if (descriptor.isUnique())
          hasUnique = true;
      }
    }

    if (index == null) {
      index = new MemoryIndex(descriptor, rowFilterListeners, filterRow, this,
          visibleMask, invisibleMask, statusColumn);
    }

    for (int internalRow = 1; internalRow < rowCount; ++internalRow)
      index.loadStore(internalRow);

    if (sortAsInserted) {
      long count = index.lastRow() + 1 + 1;
      for (int i = 1; i < count; ++i) {
        insertColumn.setInt(i, i);
      }
    }

    index.sort();

    return index;
  }

  @Override
  public final void getOriginalRow(long internalRow, Variant[] values)
  /*-throws DataSetException-*/
  {
    getRowData(originalColumn.getInt((int) internalRow), values);
  }

  @Override
  public final void getOriginalVariant(long internalRow, int ordinal,
      Variant value)
  /*-throws DataSetException-*/
  {
    getVariant(originalColumn.getInt((int) internalRow), ordinal, value);
  }

  // !/*
  // ! public void deleteDataSet(StorageDataSet dataSet)
  // ! /*-throws DataSetException-*/
  // ! {
  // ! // No persistance, so nothing to do. (when pointer reset, memory
  // ! // will be garbage collected.
  // ! }
  // !*/

  @Override
  public MatrixData closeDataSet(StorageDataSet dataSet, int matrixDataType,
      AggManager aggManager, StorageDataSet fetchDataSet, int reason,
      boolean closeData)
  /*-throws DataSetException-*/
  {
    if (closeData)
      return null;
    return this;
  }

  @Override
  public MatrixData setColumns(StorageDataSet dataSet, Column[] columns)
  /*-throws DataSetException-*/
  {
    return null;// emptyDataSet(dataSet);
  }

  final void deleteDuplicates()
  /*-throws DataSetException-*/
  {
    if (duplicates != null) {
      duplicates.close();
      duplicates = null;
      dupValue = null;
    }
  }

  final void copyDuplicate(int dupInternalRow)
  /*-throws DataSetException-*/
  {
    if (duplicates == null) {
      duplicates = new TableDataSet();
      duplicates.setColumns(dataSet.cloneColumns());
      duplicates.setResolvable(false);
      duplicates.open();
      dupValue = new Variant();
    }
    duplicates.insertRow(false);
    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal) {
      getVariant(dupInternalRow, ordinal, dupValue);
      duplicates.setVariant(ordinal, dupValue);
    }
    duplicates.post();
    emptyStoreRow(dupInternalRow);
  }

  @Override
  public boolean isMemoryData() {
    return true;
  }

  private final IntColumn statusColumn;
  private IntColumn originalColumn;
  DataColumn[] dataColumns;
  private int rowCount;
  private boolean hasUnique;

  private NullState nullState;
  private boolean needsRecalc;
  // ! private int emptyCount;
  // ! private int lastEmpty;

  TableDataSet duplicates;
  Variant dupValue;
  StorageDataSet dataSet;
}

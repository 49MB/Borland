//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

public class InternalRow extends ReadRow {
  StorageDataSet dataSet;
  
  public InternalRow(StorageDataSet dataSet) {
    this.dataSet = dataSet;
    this.data = dataSet.getMatrixData();
    this.columnList = dataSet.getColumnList();
    // Use a different variant for each column.
    // Good for dirty reads - at least the type will never be dirty,
    // just the value.
    //
    int count = columnList.count;
    internalRowValues = new RowVariant[count];
    for (int ordinal = 0; ordinal < count; ++ordinal)
      internalRowValues[ordinal] = new RowVariant(
          columnList.cols[ordinal].getDataType());
  }
  
  public void setInternalRow(long internalRow) {
    this.internalRow = internalRow;
  }
  
  @Override
  protected RowVariant getVariantStorage(ReadRow readRow, int ordinal) {
    return internalRowValues[readRow.columnList.getScopedColumns()[ordinal].ordinal];
  }
  
  public long getInternalRow() {
    return internalRow;
  }
  
  @Override
  public final RowVariant getVariantStorage(int ordinal)
  /*-throws DataSetException-*/
  {
    dataSet.calcUpdate(internalRow, ordinal);
    data.getVariant(internalRow, ordinal, internalRowValues[ordinal]);
    return internalRowValues[ordinal];
  }
  
  @Override
  public final RowVariant getVariantStorage(String columnName)
  /*-throws DataSetException-*/
  {
    int ordinal = columnList.getOrdinal(columnName);
    dataSet.calcUpdate(internalRow, ordinal);
    data.getVariant(internalRow, ordinal, internalRowValues[ordinal]);
    return internalRowValues[ordinal];
  }
  
  @Override
  public final RowVariant getVariantStorage(Column column) {
    dataSet.calcUpdate(internalRow, column.ordinal);
    data.getVariant(internalRow, column.ordinal,
        internalRowValues[column.ordinal]);
    return internalRowValues[column.ordinal];
  }
  
  private final MatrixData data;
  private long internalRow;
  private final RowVariant[] internalRowValues;
}

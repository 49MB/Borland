//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The DataRow contains one row's worth of storage for
 * {@link com.borland.dx.dataset.Column} components of the
 * {@link com.borland.dx.dataset.DataSet} it is constructed from. It is useful
 * for adding, updating, and locating rows in a DataSet.
 * <p>
 * A DataRow must be created with the DataSet it is used with. If the structure
 * of a DataSet changes, old DataRow objects will not work with the updated
 * DataSet and a new DataRow must be created for it.
 * <p>
 * When using a DataRow to locate data, all columns in the DataRow are included
 * in the locate operation. To limit the locate to include only specified
 * columns, use a "scoped" DataRow. A scoped DataRow includes only specified
 * columns and is created using the {@link #DataRow(DataSet, String)} or the
 * {@link #DataRow(DataSet, String[])} constructor.
 * <p>
 * To write code that handles columns of any data type, use the
 * {@link com.borland.dx.dataset.ReadWriteRow#setVariant(String, Variant)} or
 * the {@link com.borland.dx.dataset.ReadWriteRow#setVariant(int, Variant)
 * method and the
 * {@link com.borland.dx.dataset.ReadWriteRow#getVariant(String, Variant)} or
 * {@link com.borland.dx.dataset.ReadWriteRow#getVariant(int, Variant)} method.
 * For example, use these methods when writing code for locating data that is
 * not data type dependent.
 * <p>
 * Setting values in a DataRow does not automatically perform Column level
 * constraint checks such as minimum value, maximum value or readOnly. This
 * allows you to use the DataRow, for example, when locating a value in a
 * calculated (readOnly) column. To explicitly apply constraint tests on all
 * columns, call the {@link #validate()} method.
 * <p>
 * When working with DataRows and calling superclass methods such as
 * {@link com.borland.dx.dataset.ReadRow#getBigDecimal(int)} which refer to a
 * Column ordinal (int), the ordinal is the position of the Column in the
 * DataRow and not of the original DataSet. In addition, Column numbering begins
 * with zero (0) as the first Column. To avoid hard-coding the mapping between
 * the DataSet Column order and scoped Column order, use the following syntax:
 * <p>
 * <code>
 *  dataRow1.setString(dataset.getColumn(ordinal).getColumnName(),"test")
 *  </code>
 * <p>
 * For most methods taking an ordinal parameter there is an equivalent one
 * taking a String parameter. The latter is preferred over the ordinal one since
 * column names are more readable and are not affected by changes in column
 * order.
 */
public class DataRow extends ReadWriteRow
// ! implements AccessListener
{
  @Override
  final void rowEdited()
  /*-throws DataSetException-*/
  {
    // ! if (!open)
    // ! DataSetException.dataSetNotOpen();
  }
  
  /**
   * Constructs a DataRow containing all the Column components of the specified
   * DataSet, but no data values.
   * 
   * @param dataSet
   *          The DataSet component from which to clone the structure for the
   *          DataRow. All Column components of the DataSet are included in the
   *          DataRow.
   */
  public DataRow(DataSet dataSet)
  /*-throws DataSetException-*/
  {
    initRow(dataSet.getStorageDataSet(), false);
    
    // ! dataSet.addAccessListener(this);
  }
  
  DataRow(DataSet dataSet, boolean shareColumns)
  /*-throws DataSetException-*/
  {
    initRow(dataSet.getStorageDataSet(), shareColumns);
    
    // ! dataSet.addAccessListener(this);
  }
  
  /**
   * Constructs a "scoped" DataRow containing the data structure (but no data)
   * from specified columns of the DataSet.
   * 
   * @param dataSet
   *          The DataSet component from which to clone the structure for the
   *          DataRow. All Column components of the DataSet are included in the
   *          DataRow.
   * @param columnNames
   *          An array of String names of the Column components to include in
   *          the DataRow.
   */
  public DataRow(DataSet dataSet, String[] columnNames)
  /*-throws DataSetException-*/
  {
    initRow(dataSet.getStorageDataSet(), columnNames, false, false);
    
    // ! dataSet.addAccessListener(this);
  }
  
  /**
   * Creates a "scoped" DataRow containing the structure of (but no data from)
   * the specified column of the current row position.
   * 
   * @param dataSet
   *          The DataSet component from which to clone the structure for the
   *          DataRow. Only the Column component specified in the columnName
   *          parameter is included in the DataRow.
   * @param columnName
   *          The String name of the Column to include in the DataRow.
   */
  public DataRow(DataSet dataSet, String columnName)
  /*-throws DataSetException-*/
  {
    initRow(dataSet.getStorageDataSet(), new String[] { columnName }, false,
        false);
    
    // ! dataSet.addAccessListener(this);
  }
  
  // SS: Zusätzlicher Konstruktor mit Ordinal-Parametern
  public DataRow(DataSet dataSet, Column column) {
    this(dataSet, new Column[] { column });
  }
  
  public DataRow(DataSet dataSet, Column[] columns) {
    initRow(dataSet.getStorageDataSet(), columns, false, false);
  }
  
  // DO NOT MAKE PUBLIC!
  //
  DataRow(DataSet dataSet, String[] columnNames, boolean doValidations)
  /*-throws DataSetException-*/
  {
    initRow(dataSet.getStorageDataSet(), columnNames, doValidations, false);
    
    // ! dataSet.addAccessListener(this);
  }
  
  private void _initRow(DataSet dataSet) {
    this.dataSet = dataSet.getStorageDataSet();
    this.dataSet.reallocateColumns();
    columnList = dataSet.getColumnList();
    
    // Save off the compatible list. If dataSets, columns incur any strucutural
    // change, its columnList will be reallocated. When this occurs,
    // compatibleList
    // will no longer point to the same columnList that the DataSet contains so
    // ReadRow.getRowValues()
    // will be able to detect incompatibility of an out dated or mismatched
    // DataRow.
    //
    setCompatibleList(columnList);
  }
  
  private void _valueValidate(boolean doValidations) {
    initRowValues(doValidations);
    
    RowVariant value;
    if (!doValidations) {
      for (RowVariant rowValue : rowValues) {
        value = rowValue;
        if (value.column.hasValidations) {
          hasValidations = true;
          value.doValidations = true;
        }
      }
    }
  }
  
  private void initRow(DataSet dataSet, String[] columnNames,
      boolean doValidations, boolean shareColumns)
  /*-throws DataSetException-*/
  {
    // ! this.columnNames = columnNames;
    _initRow(dataSet);
    if (!shareColumns) {
      // Clones the ColumnList so that the integrity of the DataRow is not
      // compromised by
      // structural changes to the dataSet.
      //
      if (columnNames != null)
        columnList = columnList.cloneColumnList(columnList, columnNames,
            this.dataSet.getShareColumns());
      else
        columnList = columnList.cloneColumnList(columnList,
            this.dataSet.getShareColumns());
    }
    
    _valueValidate(doValidations);
  }
  
  private void initRow(DataSet dataSet, Column[] columns,
      boolean doValidations, boolean shareColumns)
  /*-throws DataSetException-*/
  {
    // ! this.columnNames = columnNames;
    _initRow(dataSet);
    if (!shareColumns) {
      // Clones the ColumnList so that the integrity of the DataRow is not
      // compromised by
      // structural changes to the dataSet.
      //
      if (columns != null)
        columnList = columnList.cloneColumnList(columnList, columns,
            this.dataSet.getShareColumns());
      else
        columnList = columnList.cloneColumnList(columnList,
            this.dataSet.getShareColumns());
    }
    
    _valueValidate(doValidations);
  }
  
  private void initRow(DataSet dataSet, boolean shareColumns) {
    _initRow(dataSet);
    if (!shareColumns)
      columnList = columnList.cloneColumnList(columnList,
          this.dataSet.getShareColumns());
    
    _valueValidate(false);
  }
  
  /**
   * @return The number of "scoped" columns in this DataRow as specified by the
   *         DataRow constructor.
   */
  @Override
  public int getColumnCount() {
    return columnList.getScopedColumnLength();
  }
  
  @Override
  public Column[] getColumns() {
    if (columnList.hasScopedColumns())
      return columnList.getScopedColumns();
    return columnList.getColumns();
  }
  
  @Override
  boolean canCopy(RowVariant value) {
    // return value.column.getCalcType() == CalcType.NO_CALC;
    return true;
  }
  
  /**
   * Tests all columns in the DataRow for constraints on the data such as
   * minimum or maximum value, readOnly, and so on.
   */
  public final void validate()
  /*-throws DataSetException-*/
  {
    
    RowVariant value;
    if (hasValidations) {
      for (RowVariant rowValue : rowValues) {
        value = rowValue;
        if (value.set)
          value.validateAndSet(dataSet);
      }
    }
  }
  
  @Override
  void processColumnPost(RowVariant value) /*-throws DataSetException-*/{
    value.set = true;
  }
  
  @Override
  int[] getRequiredOrdinals() {
    return dataSet.getRequiredOrdinals();
  }
  
  @Override
  public String toString() {
    return toStringValues();
  }
  
  private boolean validationsInited;
  StorageDataSet dataSet;
}

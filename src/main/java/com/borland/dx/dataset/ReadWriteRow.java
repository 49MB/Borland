//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

import com.borland.jb.util.Diagnostic;

// Internal class that is derived from by public classes.

public abstract class ReadWriteRow extends ReadRow {
  abstract void rowEdited() /*-throws DataSetException-*/;
  
  abstract void processColumnPost(RowVariant value) /*-throws DataSetException-*/;
  
  void notifyColumnPost(RowVariant value) /*-throws DataSetException-*/
  {
    Diagnostic.fail();
    
  }
  
  protected RowVariant getSetStorage(String columnName)
  /*-throws DataSetException-*/
  {
    rowEdited();
    return setValues[columnList.getOrdinal(columnName)];
  }
  
  protected RowVariant getSetStorage(int ordinal)
  /*-throws DataSetException-*/
  {
    rowEdited();
    if (columnList.hasScopedColumns()) {
      return setValues[columnList.getScopedColumns()[ordinal].ordinal];
    } else {
      // Diagnostic.check(columnList.getScopedColumns()[ordinal].ordinal ==
      // ordinal);
      return setValues[ordinal];
    }
  }
  
  protected RowVariant getSetStorageCalc(int ordinal)
  /*-throws DataSetException-*/
  {
    // rowEdited(); SS Eben nicht, da Datensatz sonst im editiermodus!
    if (columnList.hasScopedColumns()) {
      return setValues[columnList.getScopedColumns()[ordinal].ordinal];
    } else {
      // Diagnostic.check(columnList.getScopedColumns()[ordinal].ordinal ==
      // ordinal);
      return setValues[ordinal];
    }
  }
  
  protected RowVariant getSetStorage(Column column) {
    rowEdited();
    return setValues[getOrdinal(column)];
  }
  
  final void initRowValues(boolean doValidations)
  /*-throws DataSetException-*/
  {
    int setType;
    Column column;
    int count = columnList.count;
    
    // ! Diagnostic.check(count > 0);
    
    // ! this.notifyFieldPost = notifyFieldPost;
    
    rowValues = new RowVariant[count];
    if (doValidations)
      setValues = new RowVariant[count];
    else
      setValues = rowValues;
    
    // The initialization that takes place in the body of this loop is the
    // basic setup for a performant mechanism that deals with the
    // "possible" application requirement of field level validation. If there
    // is no field level validation, very little overhead will be incurred.
    // The scheme uses two arrays - setValues and rowValues. One array has the
    // values
    // that are initially set. If there are some validations required, the set
    // value is passed
    // to the Column for validation (could included application event handlers).
    // If
    // validation is passed, the set value can be copied to the row value. In
    // the
    // case that there is no validation, the set value does not have to be
    // copied to
    // the row value because they are the same - note that when there are no
    // validations,
    // the rowValues and setValues array elements are set to the same
    // RowVariant.
    // A RowVariant class was constructed to hold an extra column member. This
    // simplifies
    // the code for all the setters.
    //
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      column = columnList.cols[ordinal];
      if (column != null) {
        setType = columnList.getSetType(ordinal);
        rowValues[ordinal] = new RowVariant(setType, column, null,
            doValidations);
        if (doValidations) {
          if (rowValues[ordinal].column.hasValidations) {
            hasValidations = true;
            setValues[ordinal] = new RowVariant(setType, column,
                rowValues[ordinal], true);
          } else {
            setValues[ordinal] = rowValues[ordinal];
          }
        }
      } else {
        rowValues[ordinal] = RowVariant.nullVariant;
        setValues[ordinal] = RowVariant.nullVariant;
      }
    }
  }
  
  /**
   * Nulls out all values of the row. Sets them to unassigned nulls.
   */
  public final void clearValues()
  /*-throws DataSetException-*/
  {
    RowVariant variant;
    rowEdited();
    for (int ordinal = 0; ordinal < rowValues.length; ++ordinal) {
      variant = setValues[ordinal];
      if (notifyColumnPost
          || (variant.column.hasValidations && variant.doValidations)) {
        variant.setUnassignedNull();
        processColumnPost(variant);
      } else
        rowValues[ordinal].setUnassignedNull();
    }
  }
  
  final void _clearValues() {
    for (RowVariant rowValue : rowValues) {
      rowValue.setUnassignedNull();
    }
  }
  
  public void setByte(String columnName, byte value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setByte(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setShort(String columnName, short value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setShort(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setInt(String columnName, int value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setInt(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setLong(String columnName, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setLong(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations)) {
      processColumnPost(variant);
    }
  }
  
  public void setBoolean(String columnName, boolean value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setBoolean(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDouble(String columnName, double value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setDouble(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setFloat(String columnName, float value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setFloat(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setString(String columnName, String value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setString(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations)) {
      processColumnPost(variant);
    }
  }
  
  public void setBigDecimal(String columnName, BigDecimal value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setBigDecimal(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(String columnName, java.sql.Date value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setDate(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(String columnName, java.util.Date value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setDate(value.getTime());
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(String columnName, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setDate(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTime(String columnName, Time value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setTime(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTime(String columnName, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setTime(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTimestamp(String columnName, Timestamp value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setTimestamp(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTimestamp(String columnName, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setTimestamp(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  /**
   * @deprecated use setInputStream(columnName, value).
   */
  @Deprecated
  public void setBinaryStream(String columnName, InputStream value)
  /*-throws DataSetException-*/
  {
    setInputStream(columnName, value);
  }
  
  public void setInputStream(String columnName, InputStream value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setInputStream(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setByteArray(String columnName, byte[] value, int length)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setByteArray(value, length);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setObject(String columnName, Object value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setObject(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setAssignedNull(String columnName)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setAssignedNull();
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setUnassignedNull(String columnName)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setUnassignedNull();
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setVariant(String columnName, Variant value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.setVariant(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  final void copyVariant(String columnName, Variant value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    if (!(variant.column.hasValidations && variant.doValidations)
        || canCopy(variant)) {
      variant.setVariant(value);
      if (notifyColumnPost
          || (variant.column.hasValidations && variant.doValidations))
        processColumnPost(variant);
    }
  }
  
  public void setVariant(int ordinal, Variant value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setVariant(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  // OVERRIDDEN BY DataRow
  boolean canCopy(RowVariant value) {
    return value.column.canCopy();
  }
  
  final void copyVariant(int ordinal, Variant value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    if (canCopy(variant)) {
      variant.setVariant(value);
      if (notifyColumnPost
          || (variant.column.hasValidations && variant.doValidations))
        processColumnPost(variant);
    }
  }
  
  void setVariantNoValidate(int ordinal, Variant value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    if (variant.rowVariant != null)
      variant.rowVariant.setVariant(value);
    else
      variant.setVariant(value);
    if (notifyColumnPost)
      notifyColumnPost(variant);
  }
  
  void setVariantNoValidateCalc(int ordinal, Variant value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorageCalc(ordinal);
    if (variant.rowVariant != null)
      variant.rowVariant.setVariant(value);
    else
      variant.setVariant(value);
    if (notifyColumnPost)
      notifyColumnPost(variant);
  }
  
  public void setByte(int ordinal, byte value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setByte(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setShort(int ordinal, short value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setShort(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setInt(int ordinal, int value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setInt(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setLong(int ordinal, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setLong(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setBoolean(int ordinal, boolean value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setBoolean(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDouble(int ordinal, double value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setDouble(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setFloat(int ordinal, float value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setFloat(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setString(int ordinal, String value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setString(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setBigDecimal(int ordinal, BigDecimal value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setBigDecimal(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(int ordinal, java.sql.Date value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setDate(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(int ordinal, java.util.Date value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setDate(value.getTime());
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(int ordinal, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setDate(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTime(int ordinal, Time value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setTime(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTime(int ordinal, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setTime(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTimestamp(int ordinal, Timestamp value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setTimestamp(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTimestamp(int ordinal, long value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setTimestamp(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  /**
   * @deprecated use setInputStream(ordinal, value).
   */
  @Deprecated
  public void setBinaryStream(int ordinal, InputStream value)
  /*-throws DataSetException-*/
  {
    setInputStream(ordinal, value);
  }
  
  public void setInputStream(int ordinal, InputStream value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setInputStream(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setByteArray(int ordinal, byte[] value, int length)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setByteArray(value, length);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setObject(int ordinal, Object value)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setObject(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  /**
   * Sets a Column to null (as opposed to a value that is simply not assigned).
   * 
   * @param ordinal
   *          The ordinal position of the Column.
   */
  public void setAssignedNull(int ordinal)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setAssignedNull();
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setUnassignedNull(int ordinal)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.setUnassignedNull();
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  /**
   * Set column to default value for this column.
   * 
   * @param columnName
   */
  public void setDefault(String columnName)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(columnName);
    variant.column.getDefault(variant);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  /**
   * Set column to default value for this column.
   * 
   * @param ordinal
   */
  
  public void setDefault(int ordinal)
  /*-throws DataSetException-*/
  {
    RowVariant variant = getSetStorage(ordinal);
    variant.column.getDefault(variant);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDefaultValues()
  /*-throws DataSetException-*/
  {
    columnList.setDefaultValues(rowValues);
  }
  
  abstract int[] getRequiredOrdinals();
  
  /**
   * Throws an exception if any required columns have not been set to a non null
   * value.
   */
  public void requiredColumnsCheck()
  /*-throws ValidationException-*/
  {
    int[] requiredOrdinals = getRequiredOrdinals();
    int ordinal;
    if (requiredOrdinals != null) {
      RowVariant value;
      Diagnostic.check(rowValues != null);
      for (int requiredOrdinal : requiredOrdinals) {
        ordinal = requiredOrdinal;
        value = rowValues[ordinal];
        Diagnostic.check(value != null);
        if (value.isNull() && value.column != null)
          ValidationException.invalidRowValues(value.column);
      }
    }
  }
  
  /**
   * Throws an exception if any required columns have not been set to a non null
   * value.
   */
  final void requiredColumnsCheckForUpdate()
  /*-throws ValidationException-*/
  {
    int[] requiredOrdinals = getRequiredOrdinals();
    int ordinal;
    if (requiredOrdinals != null) {
      RowVariant value;
      Diagnostic.check(rowValues != null);
      for (int requiredOrdinal : requiredOrdinals) {
        ordinal = requiredOrdinal;
        value = rowValues[ordinal];
        Diagnostic.check(value != null);
        if (value.isNull() && value.column != null)
          ValidationException.invalidRowValues(value.column);
      }
    }
  }
  
  public void requiredColumnsCheck(RowVariant[] values)
  /*-throws ValidationException-*/
  {
    Column column;
    int ordinal;
    int[] requiredOrdinals = getRequiredOrdinals();
    if (requiredOrdinals != null) {
      for (int requiredOrdinal : requiredOrdinals) {
        ordinal = requiredOrdinal;
        if (values[ordinal].isNull()) {
          column = rowValues[ordinal].column;
          if (column != null
              && !(column.isAutoIncrement() && values[ordinal].isNull()))
            ValidationException.missingRequiredValue(column);
        }
      }
    }
  }
  
  final void copySetValuesTo(ReadWriteRow row) {
    row.setValues = setValues;
  }
  
  /*
   * final void validate(DataSet dataSet, RowVariant[] values) { if
   * (hasValidations) { for(int ordinal = 0; ordinal < values.length; ++ordinal)
   * { rowValues[ordinal].validateAndSet(dataSet, values[ordinal]); } } }
   */
  
  private transient RowVariant[] setValues;
  transient boolean hasValidations;
  transient boolean notifyColumnPost;
  private static final long serialVersionUID = 1L;
  
  // SS: Vereinfachungsfunktionen für den Zugriff auf Felder:
  public void setInt(Column column, int value) {
    RowVariant variant = getSetStorage(column);
    variant.setInt(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setString(Column column, String value) {
    RowVariant variant = getSetStorage(column);
    variant.setString(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDouble(Column column, double value) {
    RowVariant variant = getSetStorage(column);
    variant.setDouble(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setFloat(Column column, float value) {
    RowVariant variant = getSetStorage(column);
    variant.setFloat(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setBoolean(Column column, boolean value) {
    RowVariant variant = getSetStorage(column);
    variant.setBoolean(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setVariant(Column column, Variant value) {
    RowVariant variant = getSetStorage(column);
    variant.setVariant(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(Column column, java.sql.Date value) {
    RowVariant variant = getSetStorage(column);
    variant.setDate(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(Column column, java.util.Date value) {
    RowVariant variant = getSetStorage(column);
    variant.setDate(value.getTime());
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDate(Column column, long value) {
    RowVariant variant = getSetStorage(column);
    variant.setDate(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setLong(Column column, long value) {
    RowVariant variant = getSetStorage(column);
    variant.setLong(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTime(Column column, java.sql.Time value) {
    RowVariant variant = getSetStorage(column);
    variant.setTime(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTime(Column column, long value) {
    RowVariant variant = getSetStorage(column);
    variant.setTime(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTimestamp(Column column, java.sql.Timestamp value) {
    RowVariant variant = getSetStorage(column);
    variant.setTimestamp(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setTimestamp(Column column, long value) {
    RowVariant variant = getSetStorage(column);
    variant.setTimestamp(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setObject(Column column, Object value) {
    RowVariant variant = getSetStorage(column);
    variant.setObject(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setInputStream(Column column, InputStream value) {
    RowVariant variant = getSetStorage(column);
    variant.setInputStream(value);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setByteArray(Column column, byte[] value) {
    RowVariant variant = getSetStorage(column);
    variant.setByteArray(value, value.length);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setByteArray(Column column, byte[] value, int length) {
    RowVariant variant = getSetStorage(column);
    variant.setByteArray(value, length);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setAssignedNull(Column column) {
    RowVariant variant = getSetStorage(column);
    variant.setAssignedNull();
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setUnassignedNull(Column column) {
    RowVariant variant = getSetStorage(column);
    variant.setUnassignedNull();
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  public void setDefault(Column column) {
    RowVariant variant = getSetStorage(column);
    variant.column.getDefault(variant);
    if (notifyColumnPost
        || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  
  protected int copyFrom(ReadRow sourceRow, String[] sourceNames,
      String[] destNames) {
    int count = 0;
    for (int index = 0; index < sourceNames.length; ++index) {
      RowVariant value = sourceRow.getVariantStorage(sourceNames[index]);
      String columnName = destNames[index];
      Variant oldValue = getVariant(columnName);
      if (!oldValue.equals(value)) {
        copyVariant(columnName, value);
        count++;
      }
    }
    return count;
  }
  
  public int setValues(String[] columnNames, Variant[] values) {
    int count = 0;
    for (int index = 0; index < columnNames.length; ++index) {
      String columnName = columnNames[index];
      Variant value = values[index];
      Variant oldValue = getVariant(columnName);
      if (!oldValue.equals(value)) {
        copyVariant(columnName, value);
        count++;
      }
    }
    return count;
  }
}

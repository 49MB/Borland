//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * The ReadRow class provides read access to a row of data. It has methods to
 * read values from a single {@link Column} according to its data type, as well
 * as methods to read the value from a Column of any data type into a Variant.
 * It also has methods to compare or copy an entire row or a subset of its
 * columns to another row.
 * <p>
 * 
 * The ReadRow class is extended by ReadWriteRow, which provides similar methods
 * to write values to Columns. The ReadWriteRow class is in turn extended by
 * DataSet, DataRow, and ParameterRow. These three classes all use the read and
 * write methods in ReadRow and ReadWriteRow heavily to manipulate Column
 * values.
 * <p>
 * 
 * You can use the equals(), findDifference(), and copyTo() methods to compare
 * two rows from the same or different data sets, or to copy rows from one data
 * set to another.
 */
public abstract class ReadRow implements java.io.Serializable {
  
  protected RowVariant getVariantStorage(String columnName)
  /*-throws DataSetException-*/
  {
    return rowValues[columnList.getOrdinal(columnName)];
  }
  
  protected RowVariant getVariantStorage(int ordinal)
  /*-throws DataSetException-*/
  {
    return rowValues[columnList.getScopedColumns()[ordinal].ordinal];
  }
  
  protected RowVariant getVariantStorage(ReadRow readRow, int ordinal) {
    return readRow.getVariantStorage(ordinal);
  }
  
  protected RowVariant getVariantStorage(Column column) {
    return rowValues[getOrdinal(column)];
  }
  
  protected int getOrdinal(Column column) {
    if (column.dataSet != null && isCompatibleList(column.dataSet))
      return column.ordinal;
    
    // System.out.println("Column " + column.getColumnName()
    // + " inkompatible with this DataSet!\n" + column.toString() + "\n"
    // + this.toString());
    return getColumn(column.getColumnName()).ordinal;
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a Variant. The Variant is returned as the value parameter passed
   * into this method.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @param value
   *          The Variant containing the value in the Column.
   */
  public void getVariant(int ordinal, Variant value)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getVariant(ordinal: " + ordinal + ")");
    value.setVariant(getVariantStorage(ordinal));
  }
  
  /**
   * Returns the value in the Column named columnName as a Variant. The Variant
   * is returned as the value parameter passed into this method.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @param value
   *          The Variant containing the value in the Column.
   */
  public void getVariant(String columnName, Variant value)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getVariant(" + columnName + ")");
    value.setVariant(getVariantStorage(columnName));
  }
  
  /**
   * Returns the value in the Column named columnName as a byte.
   * <p>
   * This method is typically preferred over {@link #getByte(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the column containing the value.
   * @return The value in the Column named columnName as a byte.
   */
  public byte getByte(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getByte();
  }
  
  /**
   * Returns the value in the Column named columnName as a short.
   * <p>
   * This method is typically preferred over {@link #getShort(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the column containing the value.
   * @return The value in the Column named columnName as a short.
   */
  public short getShort(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getShort();
  }
  
  /**
   * Returns the value in the Column named columnName as an int.
   * <p>
   * This method is typically preferred over {@link #getInt(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as an int.
   */
  public int getInt(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getInt()");
    return getVariantStorage(columnName).getInt();
  }
  
  /**
   * Returns the value in the Column named columnName as a long.
   * <p>
   * This method is typically preferred over getLong(int) since it is more
   * reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a long.
   */
  public long getLong(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getLong()");
    return getVariantStorage(columnName).getLong();
  }
  
  /**
   * Returns the value in the Column named columnName as a boolean.
   * <p>
   * This method is typically preferred over {@link #getBoolean(int)} since it
   * is more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column containing the value.
   * @return The value in the Column named columnName as a boolean.
   */
  public boolean getBoolean(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getBoolean()");
    return getVariantStorage(columnName).getBoolean();
  }
  
  /**
   * Returns the value in the Column named columnName as a double.
   * <p>
   * This method is typically preferred over {@link #getDouble(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a double.
   */
  public double getDouble(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getDouble()");
    return getVariantStorage(columnName).getDouble();
  }
  
  /**
   * Returns the value in the Column named columnName as a float.
   * <p>
   * This method is typically preferred over getFloat(int) since it is more
   * reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a float.
   */
  public float getFloat(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getFloat();
  }
  
  /**
   * Returns the value in the Column named columnName as a String.
   * <p>
   * This method is typically preferred over {@link #getString(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a String.
   */
  public String getString(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getString()");
    return getVariantStorage(columnName).getAsString();
  }
  
  private String padString(Column column, String value) {
    int precision = column.getPrecision();
    if (precision > 0) {
      StringBuffer buf = new StringBuffer(precision);
      buf.append(value);
      for (int index = value.length(); index < precision; ++index)
        buf.append(' ');
      return buf.toString();
    }
    return value;
  }
  
  /**
   * Returns the value in the Column named columnName as a String padded by the
   * precision specified in the Column.getPrecision() property.
   * <p>
   * This method is typically preferred over {@link #getString(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a String.
   */
  public String getStringPadded(String columnName)
  /*-throws DataSetException-*/
  {
    return padString(getColumn(columnName), getString(columnName));
  }
  
  /**
   * Returns the value in the Column named columnName as a BigDecimal.
   * <p>
   * This method is typically preferred over {@link #getBigDecimal(int)} since
   * it is more reliable. A column's ordinal value may unexpectedly change due
   * to persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions. Note that ordinal
   * access can be slightly faster, especially for DataSets with more than 20
   * columns.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a BigDecimal.
   */
  public BigDecimal getBigDecimal(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getBigDecimal()");
    return getVariantStorage(columnName).getBigDecimal();
  }
  
  /**
   * Returns the value in the Column named columnName as a Date.
   * <p>
   * This method is typically preferred over getDate(int) since it is more
   * reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns (columns that are automatically added to a query to
   * provide a unique row identifier) and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a Date.
   */
  public java.sql.Date getDate(String columnName)
  /*-throws DataSetException-*/
  {
    // ! Diagnostic.println("getDate()");
    return getVariantStorage(columnName).getDate();
  }
  
  /**
   * Returns the value in the Column named columnName as a Time.
   * <p>
   * This method is typically preferred over {@link #getTime(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as a Time.
   */
  public Time getTime(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getTime();
  }
  
  public Timestamp getTimestamp(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getTimestamp();
  }
  
  public Timestamp getTimestamp(Column column)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(column).getTimestamp();
  }
  
  /**
   * @deprecated Use #getInputStream(String);
   */
  @Deprecated
  public InputStream getBinaryStream(String columnName)
  /*-throws DataSetException-*/
  {
    return getInputStream(columnName);
  }
  
  /**
   * Returns the value in the Column named columnName as an InputStream.
   * <p>
   * This method is typically preferred over {@link #getInputStream(int)} since
   * it is more reliable. A column's ordinal value may unexpectedly change due
   * to persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as an InputStream.
   */
  public InputStream getInputStream(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getInputStream();
  }
  
  /**
   * Get field value as a byte array.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return
   */
  public byte[] getByteArray(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getByteArray();
  }
  
  /**
   * Returns the length of the BYTE_ARRAY.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The length of the BYTE_ARRAY.
   */
  public int getArrayLength(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getArrayLength();
  }
  
  /**
   * Returns the value in the Column named columnName as an Object.
   * <p>
   * This method is typically preferred over getObject(int) since it is more
   * reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The value in the Column named columnName as an Object.
   */
  public Object getObject(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).getObject();
  }
  
  /**
   * Returns <b>true</b> if the value at the specified columnName is either an
   * assigned or unassigned null; <b>false</b> otherwise. To determine if the
   * value is an unassigned null, call the {@link #isUnassignedNull(String)}
   * method.
   * 
   * @param columnName
   *          The name of the column containing the value.
   * @return <b>true</b> if the value at the specified columnName is either an
   *         assigned or unassigned null; <b>false</b> otherwise.
   */
  public boolean isNull(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).isNull();
  }
  
  /**
   * Determines whether the data value identified by its column name is an
   * assigned null value. If it returns <b>true</b>, the value is an assigned
   * null value; otherwise, it is not and returns <b>false</b>.
   * 
   * @param columnName
   *          The name of the column identifying the data value.
   * @return <b>true</b> if the value is an assigned null value; otherwise, it
   *         is not and returns <b>false</b>.
   */
  public boolean isAssignedNull(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).isAssignedNull();
  }
  
  /**
   * Determines whether the data value identified by its column name is an
   * assigned null value. If it returns <b>true</b>, the value is an assigned
   * null value; otherwise, it is not and returns <b>false</b>.
   * 
   * @param columnName
   *          The name of the column identifying the data value.
   * @return <b>true</b> if the value is an assigned null value; otherwise, it
   *         returns <b>false</b>.
   */
  public boolean isUnassignedNull(String columnName)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(columnName).isUnassignedNull();
  }
  
  /**
   * Returns a String representation of the value at the specified column name,
   * using a Column formatter.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return
   */
  
  public String format(String columnName)
  /*-throws DataSetException-*/
  {
    Column column = getColumn(columnName);
    return column.format(getVariantStorage(getOrdinal(column)));
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a byte.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a byte.
   * @see #getByte(String)
   */
  public byte getByte(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getByte();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a short.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a short.
   * @see #getShort(String)
   */
  public short getShort(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getShort();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as an int.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as an int.
   * @see #getInt(String)
   */
  public int getInt(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getInt();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a long.
   * 
   * @param ordinal
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a long.
   * @see #getLong(String)
   */
  public long getLong(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getLong();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a boolean.
   * 
   * @see #getBoolean(java.lang.String)
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a boolean.
   */
  public boolean getBoolean(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getBoolean();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a float.
   * 
   * @see com.borland.dx.dataset.ReadRow#getFloat(String)
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a float.
   */
  public float getFloat(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getFloat();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a double.
   * 
   * @see com.borland.dx.dataset.ReadRow#getDate(String)
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a double.
   */
  public double getDouble(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getDouble();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a String.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a String.
   * @see #getString(String)
   */
  public String getString(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getAsString();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a String padded by the precision specified in the
   * Column.getPrecision() property.
   * <p>
   * This method is typically preferred over {@link #getString(int)} since it is
   * more reliable. A column's ordinal value may unexpectedly change due to
   * persistent columns, columns that are automatically added to a query to
   * provide a unique row identifier, and other conditions.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a String.
   * @see #getStringPadded(String)
   */
  public String getStringPadded(int ordinal)
  /*-throws DataSetException-*/
  {
    return padString(getColumn(ordinal), getString(ordinal));
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a BigDecimal.
   * 
   * @see com.borland.dx.dataset.ReadRow#getBigDecimal(java.lang.String)
   * @param ordinal
   *          The position of the Column in the ReadRow.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a BigDecimal.
   */
  public BigDecimal getBigDecimal(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getBigDecimal();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a Date.
   * 
   * @see com.borland.dx.dataset.ReadRow#getDate(String)
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return Returns the value in the Column indicated by its ordinal position
   *         in the ReadRow as a Date.
   */
  public java.sql.Date getDate(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getDate();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a Time.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a Time.
   * @see #getTime(String)
   */
  public Time getTime(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getTime();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as a Timestamp.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as a Timestamp.
   * @see #getTimestamp(String)
   */
  public Timestamp getTimestamp(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getTimestamp();
  }
  
  /**
   * @deprecated Use {@link #getInputStream(int)} instead.
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return
   */
  @Deprecated
  public InputStream getBinaryStream(int ordinal)
  /*-throws DataSetException-*/
  {
    return getInputStream(ordinal);
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as an InputStream.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as an InputStream.
   * @see #getInputStream(String)
   */
  public InputStream getInputStream(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getInputStream();
  }
  
  /**
   * Get field value as a byte array.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return
   */
  public byte[] getByteArray(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getByteArray();
  }
  
  /**
   * Returns the value in the Column indicated by its ordinal position in the
   * ReadRow as an Object.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The value in the Column indicated by its ordinal position in the
   *         ReadRow as an Object.
   * @see #getObject(String)
   */
  public Object getObject(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).getObject();
  }
  
  /**
   * Returns <b>true</b> if the value at the specified ordinal is either an
   * assigned or unassigned null; <b>false</b> otherwise. To determine if the
   * value is an unassigned null, call the {@link #isUnassignedNull(int)}
   * method.
   * 
   * @param ordinal
   *          The ordinal containing the value.
   * @return <b>true</b> if the value at the specified ordinal is either an
   *         assigned or unassigned null; <b>false</b> otherwise.
   */
  public boolean isNull(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).isNull();
  }
  
  /**
   * Determines whether the data value at location ordinal is an assigned null
   * value. If it returns <b>true</b>, the value is an assigned null value;
   * otherwise, it is not and returns <b>false</b>.
   * 
   * @param ordinal
   *          The location ordinal of the data.
   * @return <b>true</b> if the value is an assigned null value; otherwise, it
   *         is not and returns <b>false</b>.
   */
  public boolean isAssignedNull(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).isAssignedNull();
  }
  
  /**
   * Determines whether the data value at location ordinal is an assigned null
   * value. If it returns <b>true</b>, the value is an assigned null value;
   * otherwise, it is not and returns <b>false</b>.
   * 
   * @param ordinal
   *          The location ordinal of the data.
   * @return <b>true</b> if the value is an assigned null value; otherwise, it
   *         returns <b>false</b>.
   */
  public boolean isUnassignedNull(int ordinal)
  /*-throws DataSetException-*/
  {
    return getVariantStorage(ordinal).isUnassignedNull();
  }
  
  /**
   * Returns the String representation of the value at the ordinal position
   * using a Column formatter.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The String representation of the value at the ordinal position
   *         using a Column formatter.
   */
  public String format(int ordinal)
  /*-throws DataSetException-*/
  {
    return getColumn(ordinal).format(getVariantStorage(ordinal));
  }
  
  public String format(Column column) {
    return column.format(getVariantStorage(column));
  }
  
  /**
   * Returns the Column component at the specified ordinal index location.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return The Column component at the specified ordinal index location.
   * @throws DataSetException
   *           An error has occured.
   */
  public Column getColumn(int ordinal) throws DataSetException {
    return columnList.getScopedColumns()[ordinal];
  }
  
  /**
   * Get count of columns.
   */
  public int getColumnCount() {
    if (columnList != null)
      return columnList.count;
    return 0;
  }
  
  /**
   * @deprecated Use getColumnCount()
   */
  @Deprecated
  public int columnCount() {
    return getColumnCount();
  }
  
  /**
   * Returns the Column component for the specified columnName. Similar to
   * {@link #hasColumn(java.lang.String)}, however this method throws a
   * DataSetException if the Column is not found.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return
   * @throws DataSetException
   */
  public Column getColumn(String columnName) throws DataSetException {
    return columnList.getColumn(columnName);
  }
  
  /**
   * 
   * Returns the Column object as specified by its String name. Similar to the
   * getColumn method, however this method returns null if the Column is not
   * found instead of throwing a DataSetException.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The Column object as specified by its String name.
   */
  public Column hasColumn(String columnName) {
    if (columnList != null) {
      return columnList.hasColumn(columnName);
    }
    return null;
  }
  
  /**
   * Returns the ordinal of the column specified in columnName. This method is
   * slightly more performant than {@link #hasColumn(String)} if there is a good
   * chance the column does not exist.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return The ordinal of the column specified in columnName.
   */
  public int findOrdinal(String columnName) {
    if (columnList != null) {
      return columnList.findOrdinal(columnName);
    }
    return -1;
  }
  
  /*
   * Returns list manager for columns. Does not have much value to classes
   * outside the DataSet package.
   */
  final ColumnList getColumnList() {
    return columnList;
  }
  
  // Used Internally. Don't make public, it returns internal storage.
  //
  final RowVariant[] getRowValues(ColumnList compatibleList)
  /*-throws DataSetException-*/
  {
    // Critical. Other code relies on this to make sure a row is in sync
    // with a DataSet.
    //
    if (!ColumnList.isCompatible(compatibleList, this.compatibleList))
      DataSetException.incompatibleDataRow();
    return rowValues;
  }
  
  /**
   * Gets the Variants for a whole row. The array you pass to it must have at
   * least as many members as the return value of {@link #getColumnCount()}.
   * 
   * @param values
   */
  public final void getVariants(Variant[] values)
  /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < values.length; ++ordinal)
      getVariant(ordinal, values[ordinal]);
  }
  
  // Used Internally. Don't make public, it returns internal storage.
  //
  RowVariant[] getLocateValues(ColumnList compatibleList)
  /*-throws DataSetException-*/
  {
    // Critical. Other code relies on this to make sure a row is in sync
    // with a DataSet.
    //
    if (!ColumnList.isCompatible(compatibleList, this.compatibleList))
      DataSetException.incompatibleDataRow();
    return rowValues;
  }
  
  /**
   * Copies values of the ReadRow to a ReadWriteRow, given an array of source
   * and destination names. If a destination Column is readOnly, the copy is not
   * performed for that column; no Exception is generated. Use this method when
   * the structure of this ReadRow is not identical to that of the destRow, to
   * specify which Columns of the ReadRow get copied to which Columns in the
   * destRow.
   * 
   * @param sourceNames
   * @param sourceRow
   * @param destNames
   * @param destRow
   */
  public static void copyTo(String[] sourceNames, ReadRow sourceRow,
      String[] destNames, ReadWriteRow destRow)
  /*-throws DataSetException-*/
  {
    destRow.copyFrom(sourceRow, sourceNames, destNames);
  }
  
  public static int compare(String[] sourceNames, ReadRow sourceRow,
      String[] destNames, ReadWriteRow destRow) {
    for (int index = 0; index < sourceNames.length; ++index) {
      Variant a = sourceRow.getVariantStorage(sourceNames[index]);
      Variant b = destRow.getVariantStorage(destNames[index]);
      int comp = a.compareTo(b);
      if (comp != 0)
        return comp;
    }
    return 0;
  }
  
  /**
   * Copies the row values from this row to destRow. The copy is not performed
   * for destination Columns that are readOnly; no Exception is generated. If
   * the Column components of the destRow are not from the same DataSet, columns
   * with the same name are copied, assuming the data types of the columns
   * match. If this ReadRow does not have columns with the same type and name as
   * all columns in destRow, a DataSetException is thrown.
   * <p>
   * 
   * Call this method when this ReadRow and the destination destRow are
   * identical, or when the columns of the ReadRow are a subset of those in the
   * destRow. If the structure of this ReadRow component is not similar to that
   * of the destRow, call the
   * {@link #copyTo(String[], ReadRow, String[], ReadWriteRow)} method instead.
   * 
   * @param destRow
   */
  public void copyTo(ReadWriteRow destRow)
  /*-throws DataSetException-*/
  {
    Column column;
    Column[] columns = destRow.columnList.getScopedArray();
    if (columns.length > getColumnCount()) {
      columns = columnList.getScopedArray();
      for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
        column = destRow.hasColumn(columns[ordinal].getColumnName());
        if (column != null)
          destRow.copyVariant(destRow.getOrdinal(column),
              getVariantStorage(ordinal));
      }
    } else {
      for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
        column = hasColumn(columns[ordinal].getColumnName());
        if (column != null)
          destRow.copyVariant(ordinal, getVariantStorage(getOrdinal(column)));
      }
    }
  }
  
  public void copyTo(ReadWriteRow destRow, Column[] excludeCols) {
    Column column;
    Column[] columns = destRow.columnList.getScopedArray();
    if (columns.length > getColumnCount()) {
      columns = columnList.getScopedArray();
      for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
        column = destRow.hasColumn(columns[ordinal].getColumnName());
        if (column != null && !excluded(column, excludeCols))
          destRow.copyVariant(destRow.getOrdinal(column),
              getVariantStorage(ordinal));
      }
    } else {
      for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
        column = hasColumn(columns[ordinal].getColumnName());
        if (column != null && !excluded(column, excludeCols))
          destRow.copyVariant(ordinal, getVariantStorage(getOrdinal(column)));
      }
    }
  }
  
  private boolean excluded(Column col, Column[] excludeCols) {
    if (excludeCols == null)
      return false;
    
    for (Column exCol : excludeCols)
      if (exCol.equals(col)) {
        return true;
      }
    return false;
  }
  
  /**
   * Returns true if the scoped values of columns in compareRow are equal to the
   * column values in this row that have the same name. If this row does not
   * have columns with the same name and type as all columns in compareRow, a
   * DataSetException will be thrown.
   */
  /**
   * Returns <b>true</b> if the values of columns in compareRow are equal to the
   * column values in this row that have the same name. The compareRow parameter
   * may be a scoped row containing a subset of the columns in this row.
   * 
   * @param compareRow
   * @return <b>true</b> if the values of columns in compareRow are equal to the
   *         column values in this row that have the same name. The compareRow
   *         parameter may be a scoped row containing a subset of the columns in
   *         this row.
   */
  public final boolean equals(ReadRow compareRow)
  /*-throws DataSetException-*/
  {
    Column[] columns = compareRow.columnList.getScopedArray();
    if (ColumnList.isCompatible(compatibleList, compareRow.columnList)) {
      int compareOrdinal;
      for (Column column : columns) {
        compareOrdinal = column.ordinal;
        if (!getVariantStorage(compareOrdinal).equals(
            compareRow.getVariantStorage(compareOrdinal))) {
          return false;
        }
      }
      return true;
    } else {
      Column column;
      for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
        column = columns[ordinal];
        
        // ! Diagnostic.println("ordinal: "+ordinal+" "+column.ordinal+"
        // "+column.getColumnName());
        // ! Diagnostic.println("typeName:
        // "+compareRow.getVariantStorage(column.getColumnName()).getType());
        if (!getVariantStorage(column.getColumnName()).equals(
            compareRow.getVariantStorage(ordinal)))
          return false;
      }
      return true;
    }
  }
  
  /**
   * Returns the ordinal of the first column value that differs between this row
   * and compareRow starting from startOrdinal. If there are no more
   * differences, -1 is returned.
   * 
   * @param startOrdinal
   * @param compareRow
   * @return Returns the ordinal of the first column value that differs between
   *         this row and compareRow starting from startOrdinal. If there are no
   *         more differences, -1 is returned.
   */
  public final int findDifference(int startOrdinal, ReadRow compareRow)
  /*-throws DataSetException-*/
  {
    // if (compareRow.columnList.getScopedArray() !=
    // columnList.getScopedArray())
    // DataSetException.incompatibleDataRow();
    
    Column[] columns = compareRow.columnList.getScopedArray();
    int compareOrdinal;
    for (int ordinal = startOrdinal; ordinal < columns.length; ++ordinal) {
      compareOrdinal = columns[ordinal].ordinal;
      if (!getVariantStorage(compareOrdinal).equals(
          compareRow.getVariantStorage(columns[ordinal].getColumnName()))) {
        return compareOrdinal;
      }
    }
    return -1;
  }
  
  /**
   * Returns <b>true</b> if the value at the specified ordinal has been
   * modified.
   * 
   * @param ordinal
   *          The position in the ReadRow containing the value in the Column.
   * @return <b>true</b> if the value at the specified ordinal has been
   *         modified.
   */
  public boolean isModified(int ordinal)
  /*-throws DataSetException-*/
  {
    return !getVariantStorage(ordinal).isUnassignedNull();
  }
  
  /**
   * Returns <b>true</b> if the value at the specified ordinal has been
   * modified.
   * 
   * @param columnName
   *          The name of the column to compare.
   * @param otherRow
   *          The other row to compare against.
   * @param columnName
   *          The name of the column in otherRow to compare.
   * @return <b>true</b> if the value at the specified ordinal has been
   *         modified.
   */
  public boolean isModified(String columnName, ReadRow otherRow,
      String otherColumnName)
  /*-throws DataSetException-*/
  {
    return !getVariantStorage(otherColumnName).equalsInstance(
        otherRow.getVariantStorage(otherColumnName));
  }
  
  /**
   * Returns <b>true</b> if the value at the specified columnName has been
   * modified.
   * 
   * @param columnName
   *          The name of the Column that contains the value.
   * @return <b>true</b> if the value at the specified columnName has been
   *         modified.
   */
  public boolean isModified(String columnName)
  /*-throws DataSetException-*/
  {
    return !getVariantStorage(columnName).isUnassignedNull();
  }
  
  /**
   * Returns the ordinal of the first column value that has been modified to a
   * value that is not set to Variant.UNASSIGNED_NULL. If there are no more
   * modified columns after startOrdinal, -1 is returned.
   * 
   * @see #findDifference(int, ReadRow) to find at which column two rows differ.
   *      This is useful for finding out what changed in an updated row.
   * 
   * @param startOrdinal
   * @return The ordinal of the first column value that has been modified. If
   *         there are no more modified columns after startOrdinal, -1 is
   *         returned.
   */
  public final int findModified(int startOrdinal)
  /*-throws DataSetException-*/
  {
    Column[] columns = columnList.getScopedArray();
    int compareOrdinal;
    for (int ordinal = startOrdinal; ordinal < columns.length; ++ordinal) {
      compareOrdinal = columns[ordinal].ordinal;
      if (isModified(compareOrdinal))
        return ordinal;
    }
    return -1;
  }
  
  boolean _useCol(Column col, String... columnsDef) {
    if (columnsDef == null || columnsDef.length == 0)
      return true;
    String columnName = col.getColumnName().toUpperCase();
    for (String c : columnsDef) {
      if (columnName.startsWith(c.toUpperCase()))
        return true;
    }
    return false;
  }
  
  String formatRowValues(String separator, String... columnsDef) {
    String result = null;
    
    Column[] columns = getColumns();
    if (columns != null) {
      StringBuffer s = new StringBuffer();
      for (Column column : columns) {
        if (column.isTextual() && _useCol(column, columnsDef)) {
          String name = column.getColumnName();
          try {
            if (s.length() > 0)
              s.append(separator);
            if (this instanceof DataSet) {
              s.append(name).append("=").append(format(column));
            } else {
              s.append(name).append("=").append(getVariant(column).toString());
            }
          } catch (DataSetException ex) {
            // Diagnostic.printStackTrace(ex);
            s.append("??" + name + "??");
            break;
          }
        }
      }
      result = s.toString();
    }
    
    return result;
  }
  
  /**
   * Returns a String representation of this ReadRow object. Used by the
   * JBuilder design tools.
   * 
   * @return AString representation of this ReadRow object.
   */
  public String toStringValues(String... columnsDef) {
    try {
      String result = formatRowValues("\n", columnsDef);
      
      if (result == null)
        result = super.toString() + "|"
            + Res.bundle.getString(ResIndex.NoColumns);
      return result;
    } catch (Exception ex) {
      return super.toString() + ":IllegalData (" + ex.getMessage() + ")";
    }
  }
  
  public String toStringEclipseFormatter() {
    String result = formatRowValues("\n");
    if (result == null)
      result = Res.bundle.getString(ResIndex.NoColumns);
    return super.toString() + result;
  }
  
  /**
   * Returns an array of columns. This property is used by the JBuilder design
   * tools.
   * 
   * @return An array of columns. This property is used by the JBuilder design
   *         tools.
   */
  public Column[] getColumns() {
    return columnList.getColumns();
  }
  
  /**
   * Returns an array containing the names of the first columnCount Columns.
   * 
   * @param columnCount
   * @return An array containing the names of the first columnCount Columns.
   */
  public final String[] getColumnNames(int columnCount) {
    return columnList.getColumnNames(columnCount);
  }
  
  final void setCompatibleList(ColumnList list) {
    compatibleList = list;
  }
  
  final void setCompatibleList(ReadRow row) {
    compatibleList = row.compatibleList;
  }
  
  /**
   * This method is used internally by other com.borland classes. You should
   * never use this method directly.
   * 
   * @param row
   * @return
   */
  public final boolean isCompatibleList(ReadRow row) {
    return ColumnList.isCompatible(compatibleList, row.compatibleList);
  }
  
  transient ColumnList columnList;
  private transient ColumnList compatibleList;
  transient RowVariant[] rowValues;
  private static final long serialVersionUID = 1L;
  
  // SS: Vereinfachungsfunktionen für den Zugriff auf Felder:
  
  public int getInt(Column column) {
    return getVariantStorage(column).getInt();
  }
  
  public String getString(Column column) {
    return getVariantStorage(column).getAsString();
  }
  
  public float getFloat(Column column) {
    return getVariantStorage(column).getFloat();
  }
  
  public double getDouble(Column column) {
    return getVariantStorage(column).getDouble();
  }
  
  public boolean getBoolean(Column column) {
    return getVariantStorage(column).getBoolean();
  }
  
  public void getVariant(Column column, Variant value) {
    value.setVariant(getVariantStorage(column));
  }
  
  public Variant getVariant(Column column) {
    Variant v = new Variant();
    getVariant(column, v);
    return v;
  }
  
  public Variant getVariant(String columnName) {
    Variant v = new Variant();
    getVariant(columnName, v);
    return v;
  }
  
  public Variant getVariant(int ordinal) {
    Variant v = new Variant();
    getVariant(ordinal, v);
    return v;
  }
  
  public java.sql.Date getDate(Column column) {
    return getVariantStorage(column).getDate();
  }
  
  public long getLong(Column column) {
    return getVariantStorage(column).getLong();
  }
  
  public java.sql.Time getTime(Column column) {
    return getVariantStorage(column).getTime();
  }
  
  public Object getObject(Column column) {
    return getVariantStorage(column).getObject();
  }
  
  public InputStream getInputStream(Column column) {
    return getVariantStorage(column).getInputStream();
  }
  
  public byte[] getByteArray(Column column) {
    return getVariantStorage(column).getByteArray();
  }
  
  public boolean isAssignedNull(Column column) {
    return getVariantStorage(column).isAssignedNull();
  }
  
  public boolean isUnassignedNull(Column column) {
    return getVariantStorage(column).isUnassignedNull();
  }
  
  public boolean isNull(Column column) {
    return getVariantStorage(column).isNull();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ReadRow))
      return false;
    
    return (obj == this);
  }
  
  public boolean equals(Column column, String str) {
    return format(column).equals(str);
  }
  
  public boolean equals(Column column, int value) {
    return getInt(column) == value;
  }
}

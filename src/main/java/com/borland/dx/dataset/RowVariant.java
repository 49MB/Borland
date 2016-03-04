//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * This component is used internally by other com.borland classes. You should
 * never use this class directly.
 */
public class RowVariant extends Variant {
  static final RowVariant nullVariant = new RowVariant(UNASSIGNED_NULL);
  
  public RowVariant(int dataType, Column column, RowVariant rowVariant,
      boolean doValidations)
  /*-throws DataSetException-*/
  {
    super(dataType);
    this.column = column;
    this.doValidations = doValidations;
    this.rowVariant = rowVariant;
  }
  
  public static final RowVariant getNullVariant() {
    return nullVariant;
  }
  
  public RowVariant(int dataType) {
    super(dataType);
  }
  
  public RowVariant() {
    super();
  }
  
  final void validateAndSet(DataSet dataSet) /*-throws DataSetException-*/ {
    column.validate(dataSet, this);
    if (rowVariant != null)
      rowVariant.setVariant(this);
    column.changed(dataSet, this);
  }
  
  /*
   * final void validateAndSet(DataSet dataSet, RowVariant value) {
   * column.validate(dataSet, value); if (value.rowVariant != null)
   * value.rowVariant.setVariant(value); column.changed(dataSet, value); }
   */
  
  void validate(DataSet dataSet) /*-throws DataSetException-*/ {
    column.validate(dataSet, this);
  }
  
  public final boolean isSet() {
    return set;
  }
  
  public Column getColumn() {
    return column;
  }
  
  @Override
  protected boolean wrongSetType(int defType) {
    if (this == nullVariant)
      throw new IllegalStateException("NullVariant can't be changed!");
    return super.wrongSetType(defType);
  }
  
  @Override
  protected boolean wrongSetType(int defTypeFrom, int defTypeTo) {
    if (this == nullVariant)
      throw new IllegalStateException("NullVariant can't be changed!");
    return super.wrongSetType(defTypeFrom, defTypeTo);
  }
  
  Column column;
  RowVariant rowVariant;
  boolean doValidations;
  boolean set;
  public boolean changed;
  
  @Override
  public String toStringDebug() {
    if (column == null)
      return super.toStringDebug();
    return super.toStringDebug() + "\n" + column.toString();
  }
}

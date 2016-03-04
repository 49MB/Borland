//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * 
 * Extends AggOperator. Used to maintain a Min aggregation. Specified through
 * the aggDescriptor property of a Column.
 */
public class MinAggOperator extends BoundsAggOperator {
  
  /**
   * Locates the minimum value for the grouping column values specified in row.
   * For example, if the grouping column is region and the min column is sales,
   * calling this method moves to the lowest sales value for a region.
   * 
   * @param row
   *          The grouping column value.
   * @return <b>true</b> if row is located.
   */
  @Override
  public boolean locate(ReadRow row)
  /*-throws DataSetException-*/
  {
    if (searchRow == null) {
      dataSetView.first();
      found = dataSetView.getRowCount() > 0;
      return found;
    } else {
      row.copyTo(searchRow);
      found = dataSetView.locate(searchRow, Locate.FIRST);
      // !
      // Diagnostic.println(found+" locating:  "+searchRow+" "+dataSetView.getRow()+" "+dataSetView.getVariantStorage(ordinal));
      return found;
    }
  }
  
  // !/*
  // ! final boolean compare(Variant value1, Variant value2) {
  // !//! Diagnostic.println("min compare:  "+value1 +
  // "|"+value2+"|"+value1.compareTo(value2));
  // ! return value1.compareTo(value2) < 0;
  // ! }
  // !
  // ! boolean first()
  // ! /*-throws DataSetException-*/
  // ! {
  // ! if (searchRow != null)
  // ! return dataSetView.locate(searchRow, Locate.FIRST);
  // ! else {
  // ! dataSetView.first();
  // ! return true;
  // ! }
  // ! }
  // !
  // ! boolean next()
  // ! /*-throws DataSetException-*/
  // ! {
  // ! if (searchRow != null)
  // ! return dataSetView.locate(searchRow, Locate.NEXT_FAST);
  // ! else
  // ! return dataSetView.next();
  // ! }
  // !*/
  
  private static final long serialVersionUID = 1L;
}

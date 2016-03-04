//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * Extends AggOperator. Used to maintain a Max aggregation. Specified through
 * the aggDescriptor property of a Column.
 */
public class MaxAggOperator extends BoundsAggOperator {
  
  /**
   * Locates the maximum value for the grouping column values specified in row.
   * For example, if the grouping column is region and the max column is sales,
   * calling this method moves to the highest sales value for a region.
   * 
   * @param row
   *          The maximum value for the grouping column values specified in row
   * @return
   */
  @Override
  public boolean locate(ReadRow row)
  /*-throws DataSetException-*/
  {
    if (searchRow == null) {
      dataSetView.last();
      found = dataSetView.getRowCount() > 0;
      return found;
    } else {
      row.copyTo(searchRow);
      // !
      // Diagnostic.println(" locating:  "+searchRow+" "+aggDataSet.locate(searchRow,
      // Locate.FIRST));
      found = dataSetView.locate(searchRow, Locate.LAST);
      // Only allow null if that's all there is in the group.
      //
      if (found && dataSetView.getVariantStorage(ordinal).isNull()) {
        int lastRow = dataSetView.getRow();
        found = dataSetView.locate(searchRow, Locate.FIRST);
        if (found && !dataSetView.getVariantStorage(ordinal).isNull()) {
          int firstRow = dataSetView.getRow();
          int mid;
          do {
            mid = lastRow - firstRow;
            dataSetView.goToRow(firstRow + mid);
            if (dataSetView.getVariantStorage(ordinal).isNull())
              lastRow = firstRow + mid - 1;
            else
              firstRow = firstRow + mid;
          } while (mid > 1);
        }
        dataSetView.goToRow(lastRow);
        return true;
      }
      return found;
    }
  }
  
  // !/*
  // ! final boolean compare(Variant value1, Variant value2) {
  // !//! Diagnostic.println("max compare:  "+value1 +
  // "|"+value2+"|"+value1.compareTo(value2));
  // ! return value1.compareTo(value2) > 0;
  // ! }
  // !
  // ! boolean first()
  // ! /*-throws DataSetException-*/
  // ! {
  // ! if (searchRow != null)
  // ! return dataSetView.locate(searchRow, Locate.LAST);
  // ! else {
  // ! dataSetView.last();
  // ! return true;
  // ! }
  // ! }
  // !
  // ! boolean next()
  // ! /*-throws DataSetException-*/
  // ! {
  // ! if (searchRow != null)
  // ! return dataSetView.locate(searchRow, Locate.PRIOR_FAST);
  // ! else
  // ! return dataSetView.prior();
  // ! }
  // !*/
  
  private static final long serialVersionUID = 1L;
}

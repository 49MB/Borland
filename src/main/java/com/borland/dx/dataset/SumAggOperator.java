//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.Diagnostic;

/**
 * Extends AggOperator. Used to maintain a sum aggregation.
 * Specified through the aggDescriptor property of a Column.
 */
public class SumAggOperator extends AggOperator {

/**
 * A row has been added or updated.
 * @param row           The row containing the values.
 * @param internalRow   The unique identifier for the row.
 * @param first         Returns <b>true</b> if this is the first row
 *                      in the group, <b>false</b> otherwise.
 */
  public void add(ReadRow row, long internalRow, boolean first)
    /*-throws DataSetException-*/
  {
    Diagnostic.check(row!=null);
    Diagnostic.check(aggColumn!=null);
    row.getVariant(aggColumn.ordinal, aggValue);
    aggDataSet.getVariant(resultColumn.ordinal, resultValue);
    //! Diagnostic.println("add:  "+value1+"+"+value2);
    aggValue.add(resultValue, resultValue);
    //! Diagnostic.println("result:  "+aggDataSet.row()+" "+value2);
    aggDataSet.setVariant(resultColumn.ordinal, resultValue);
  }

  /**
   *  A row has been deleted or updated.
   * @param row               The row containing the values.
   * @param internalRow       The unique identifier for the row.
   */
  public void delete(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    row.getVariant(aggColumn.ordinal, aggValue);
    aggDataSet.getVariant(resultColumn.ordinal, resultValue);
    //! Diagnostic.println("subtract:  "+value2+"-"+value1);
    resultValue.subtract(aggValue, resultValue);
    //! Diagnostic.println("result row:  "+aggDataSet.row()+" result:  "+value2);
    aggDataSet.setVariant(resultColumn.ordinal, resultValue);
  }
  private static final long serialVersionUID = 1L;
}

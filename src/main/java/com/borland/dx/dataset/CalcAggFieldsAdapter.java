//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;


/**
 * Used for performing calculations on aggregated values.
 */

 /**
  * This class is an adapter class for {@link com.borland.dx.dataset.CalcAggFieldsListener}.
  * It is used for performing calculations on aggregated columns.
  */
public class CalcAggFieldsAdapter
  implements CalcAggFieldsListener
{
  public void calcAggAdd(ReadRow row, ReadWriteRow resultRow) /*-throws DataSetException-*/ {}
  public void calcAggDelete(ReadRow row, ReadWriteRow resultRow) /*-throws DataSetException-*/ {}

}

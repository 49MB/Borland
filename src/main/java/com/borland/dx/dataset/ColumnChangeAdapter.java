//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;


/**
    Column value editing events.
*/

/**
 * This class is an adapter class for
 * {@link com.borland.dx.dataset.ColumnChangeListener},
 * which is used for notification when a field value changes.
 */
public class ColumnChangeAdapter
  implements ColumnChangeListener
{
  public void validate(DataSet dataSet, Column column, Variant value)
    throws Exception, DataSetException
  {
  }
  public void changed(DataSet dataSet, Column column, Variant value)
    /*-throws DataSetException-*/
  {
  }
}

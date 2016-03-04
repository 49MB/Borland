//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;


/**
 * This is an adapter class for DataChangeListener, which is used as a notification
 * that changes to the data in a DataSet have occurred.
 */
public class DataChangeAdapter
  implements DataChangeListener
{
  /** Arbitrary data change to one or more rows.
  */
  public void dataChanged(DataChangeEvent event) {}
  public void postRow(DataChangeEvent event) throws Exception {}
}

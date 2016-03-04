//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The ColumnAware interface is a way for a component to declare
 * to JBuilder that it knows how to bind DataSets and Columns.
 * This allows the Inspector to provide additional help when
 * editing these properties.  Note that all the interface methods
 * defined here are valid JavaBeans property getters/setters
 * by design to simplify the component's implementation.
 */
public interface ColumnAware extends DataSetAware
{
/**
 * Determines which Column is accessed by the control that
 * implements this interface in the DataSet.
 *
 * @param columnName  The Column that implements this interface in the DataSet.
 */
  public void setColumnName(String columnName);

  /**
   * Determines which Column is accessed by the control that
   * implements this interface in the DataSet.
   * @return    The Column accessed by the control that implements this
   *            interface in the DataSet.
   */
  public String getColumnName();
}

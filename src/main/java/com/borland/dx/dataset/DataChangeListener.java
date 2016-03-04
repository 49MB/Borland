//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

/**
 * This interface is used to notify listeners that data has been changed
 * or a row has been posted. Not for general usage, this interface is useful
 * for component writers.
 * <p>
 * Developers of database applications should use the
 * {@link com.borland.dx.dataset.ColumnChangeListener} for field-level validation
 * and {@link com.borland.dx.dataset.EditListener} for row-level validation instead.
 * These listeners give you a finer distinction among types of changes to data,
 * access to data values, and the ability to block actions by throwing VetoExceptions.
 *
 * See com.borland.dbswing source code for usage examples.
 */
public interface DataChangeListener extends EventListener
{
  /** Arbitrary data change to one or more rows.
  */
  /**
   *  An event to warn listeners that an arbitrary data change occurred to
   *  one or more rows of data.
   *
   * @param event   An object telling what type of change was made, and to which row.
   */
  public void dataChanged(DataChangeEvent event);

  /**
   * An event to warn listeners that a row's data has changed and must be posted.
   * @param event       An object telling what type of change was made, and to which row.
   * @throws Exception
   */
  public void postRow(DataChangeEvent event) throws Exception;
}

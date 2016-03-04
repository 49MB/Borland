/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.jbcl.model;

/**
 * The WritableGraphSelection interface provides read/write access
 * to a selection pool for a GraphModel data source.
 * <P>
 * This is typically used to manage a selection set.
 */
public interface WritableGraphSelection extends GraphSelection
{
  /**
   * Sets the selection pool to a given set of indexes
   */
  public void set(GraphLocation[] locations);

  /**
   * Adds nodes to the selection pool.
   */
  public void add(GraphLocation location);
  public void add(GraphLocation[] location);

  /**
   * Removes nodes from the selection pool.
   */
  public void remove(GraphLocation location);
  public void remove(GraphLocation[] location);

  /**
   * Removes all nodes from the selection pool.
   */
  public void removeAll();

  /**
   * Enables / disables event broadcasting.
   * @param enable If false, events are disabled.
   * If true, events are enabled, and a SELECTION_CHANGED
   * event is fired to all selection listeners.
   */
  public void enableSelectionEvents(boolean enable);
}

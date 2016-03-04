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
 * The GraphSelection interface provides read access
 * to a selection pool for a GraphModel model.
 * <P>
 * This is typically used to pass a selection set.
 */
public interface GraphSelection
{
  /**
   * Returns true if location is in this selection.
   * @param node The GraphLocation to search for.
   * @return True if the pool contains the node, false if not.
   */
  public boolean contains(GraphLocation node);

  /**
   * Returns count of locations in this selection.
   * @return The count of GraphLocations in the pool.
   */
  public int getCount();

  /**
   * Returns all locations in this selection.
   * @return An array of GraphLocations in the pool.
   */
  public GraphLocation[] getAll();

  /**
   * Adds a selection event listener to this selection.
   * @param listener The GraphSelectionListener to add.
   */
  public void addSelectionListener(GraphSelectionListener listener);

  /**
   * Removes a selection event listener from this selection.
   * @param listener The GraphSelectionListener to remove.
   */
  public void removeSelectionListener(GraphSelectionListener listener);
}

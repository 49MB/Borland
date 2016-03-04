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
 * The GraphModel interface supplies read access to a
 * directed graph data structure.
 */
public interface GraphModel
{
  /**
   * Returns the root node for this graph.
   * @return The root GraphLocation for this graph.
   */
  public GraphLocation getRoot();

  /**
   * Returns data object at node.
   * @param node The node to retrieve the data object from.
   * @return The data object stored at this node.
   */
  public Object get(GraphLocation node);

  /**
   * Returns the storage location of the passed data object.
   * @param data The data object to search for in the graph.
   * @return The GraphLocation containing the data object,
   * or null if not found.
   */
  public GraphLocation find(Object data);

  /**
   * Adds a model event listener to this model.
   * @param listener The GraphModelListner to add.
   */
  public void addModelListener(GraphModelListener listener);

  /**
   * Removes a model event listener from this model.
   * @param listener The GraphModelListner to remove.
   */
  public void removeModelListener(GraphModelListener listener);
}

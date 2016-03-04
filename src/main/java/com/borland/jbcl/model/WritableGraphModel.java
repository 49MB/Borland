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
 * The WritableGraphModel interface supplies read/write access to a
 * directed graph data structure.
 */
public interface WritableGraphModel extends GraphModel
{
  /**
   * Sets this model's root node.
   * @param data The new root object to set.
   * @return The new root's GraphLocation
   */
  public GraphLocation setRoot(Object data);

  /**
   * Returns true if model can modify data object at node.
   * @param node The GraphLocation containing the data object in question.
   * @return True if the data object can be set at the node, false if not.
   */
  public boolean canSet(GraphLocation node);

  /**
   * Sets the data object at a given location/node.
   * @param node The GraphLocation containing the data object.
   * @param data The new data object to set.
   */
  public void set(GraphLocation node, Object data);

  /**
   * Notify all model listeners that the data object at the
   * specified location was touched.
   * @param node The GraphLocation containing the data object.
   */
  public void touched(GraphLocation node);

  /**
   * Returns true if model can insert or remove nodes from the graph.
   * @return True if this model can be modified, false if not.
   */
  public boolean isVariableSize();

  /**
   * Adds a value as the last child of a given node location.
   * @param parent The node to add the child to.
   * @param data The data object to put into the added node.
   * @return The new GraphLocation of the new data object.
   */
  public GraphLocation addChild(GraphLocation parent, Object data);

  /**
   * Adds a value as a child of a given node location.
   * @param parent The node to add the child to.
   * @param aheadOf The node to insert the child node before.  If null,
   * the child will be added as the last child (same as addChild(parent, data)).
   * @param data The data object to put into the added node.
   * @return The new GraphLocation of the new data object.
   */
  public GraphLocation addChild(GraphLocation parent, GraphLocation aheadOf, Object data);

  /**
   * Removes all children from a location/node from the graph.
   * @param parent The node to remove the children from.
   */
  public void removeChildren(GraphLocation parent);

  /**
   * Removes a location/node from the graph.
   * @param node The GraphLocation to remove from the graph.
   */
  public void remove(GraphLocation node);

  /**
   * Removes all locations/nodes from the graph.
   */
  public void removeAll();

  /**
   * Enables / disables event broadcasting.
   * @param enable If false, events are disabled.
   * If true, events are enabled, and a STRUCTURE_CHANGED
   * event is fired to all model listeners.
   */
  public void enableModelEvents(boolean enable);
}

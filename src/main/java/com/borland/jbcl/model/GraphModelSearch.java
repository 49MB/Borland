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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.util.TriState;

/**
 * This class is designed to facilitate searching through GraphModel data structures.
 * It supports four types of top-down searching:
 * <UL>
 * <LI> Standard first-match searching, where a single data object is located in the
 *      GraphModel structure - and returning the GraphLocation address (or null if
 *      not found).
 * <LI> Basic path-match searching, where an array of data objects is located by
 *      scanning the GraphModel structure, and subsequently drilling deeper into the
 *      GraphModel structure until each data object is located - and finally returning
 *      the located GraphLocation address (or null if not found).  This method allows
 *      for the passed data object array to not-directly correspond to the hierarchy
 *      of the GraphModel, but each data object must exist in the GraphModel - and
 *      must exist under the same parent blood line.
 * <LI> Partial path-match searching, where an array of data objects is located by
 *      scanning the GraphModel structure, and subsequently drilling deeper into the
 *      GraphModel structure until each data object is located - and finally returning
 *      the deepest located GraphLocation address that was found.  This method will
 *      return a GraphLocation address for the last data object it was successful in
 *      finding - ignoring the rest of the data object array contents.
 * <LI> Exact path-match searching, where an array of data objects is located by
 *      scanning the GraphModel structure, and subsequently drilling deeper into the
 *      GraphModel structure as each data object is located in the child list - and
 *      finally returning the located GraphLocation address (or null if not found).
 *      This method requires the passed data object array to <B>directly</B> correspond
 *      to the hierarchy of the GraphModel, following the exact parent blood line.
 * </UL>
 *
 * All searching algorythm logic can be modified by setting the 'depthFirstSearch'
 * and 'fullSearch' properties.<P>
 *
 * The 'depthFirstSearch' property, which defaults to false, tells the search
 * algorythms to check all sibling children of a node before drilling down into the
 * first child node's children, etc.  This will result in the first (most shallow)
 * occurance of a data object to be located.  If the 'depthFirstSearch' property is
 * set to true, the search will drill to the absolute bottom of each node before moving
 * onto the next.  This will result in a potentially deeper GraphLocation as a search
 * result.<P>
 *
 * The 'fullSearch' property tells the different search methods wether or not to fetch
 * children when node returns TriState.UNKNOWN to the GraphLocation.hasChildren() method.
 * Typically, 'UNKNOWN' is only returned by an implementor of GraphLocation when the process
 * of fetching the children is potentially costly (in CPU cycles).  If the 'fullSearch'
 * property is set to false, all search algorythms will treat an 'UNKNOWN' response to
 * GraphLocation.hasChildren() as 'NO' and continue on without fetching the child nodes of
 * that node.  This can potentially effect search results in that any nodes below an 'UNKNOWN'
 * children node will not be checked.  By default, this property is true, and all children are
 * checked.
 */
public class GraphModelSearch implements Serializable
{
  /**
   * Constructs a GraphModelSearch object with all default settings.
   * (null model, depthFirstSearch = false, fullSearch = true)
   */
  public GraphModelSearch() {}

  /**
   * Constructs a GraphModelSearch object with the specified model setting.
   * (depthFirstSearch = false, fullSearch = true)
   *
   * @param model The GraphModel to use for searching
   */
  public GraphModelSearch(GraphModel model) {
    this.model = model;
  }

  /**
   * Constructs a GraphModelSearch object with the specified model and depthFirstSearch
   * settings.
   * (fullSearch = true)
   *
   * @param model The GraphModel to use for searching
   * @param depthFirstSearch true to drill into child nodes before checking sibling nodes
   */
  public GraphModelSearch(GraphModel model, boolean depthFirstSearch) {
    this(model);
    this.depthFirstSearch = depthFirstSearch;
  }

  /**
   * Constructs a GraphModelSearch object with the specified model, depthFirstSearch, and
   * fullSearch settings.
   *
   * @param model The GraphModel to use for searching
   * @param depthFirstSearch true to drill into child nodes before checking sibling nodes
   * @param fullSearch true to retrieve and check children of nodes that return TriState.UNKNOWN
   *        to the GraphLocation.getChildren() method.
   */
  public GraphModelSearch(GraphModel model, boolean depthFirstSearch, boolean fullSearch) {
    this(model, depthFirstSearch);
    this.fullSearch = fullSearch;
  }

  /**
   * The model property specifies a GraphModel to search.  This property MUST be set
   * before any search routines can be called.
   *
   * @param model The GraphModel to use for searching
   */
  public void setModel(GraphModel model) {
    this.model = model;
  }
  public GraphModel getModel() {
    return this.model;
  }

  /**
   * The depthFirstSearch property specifies wether or not the search algorythms
   * should check all sibling children of a node before drilling down into the first
   * child node's children, etc.<P>
   * The default setting is false, which will result in the most shallow occurance of
   * a data object to be located.  If this property is set to true, the search will
   * drill to the absolute bottom of each node before moving onto the next.  This will
   * result in a potentially deeper GraphLocation as a search result.
   *
   * @param depthFirstSearch true to drill into child nodes before checking sibling nodes.
   */
  public void setDepthFirstSearch(boolean depthFirstSearch) {
    this.depthFirstSearch = depthFirstSearch;
  }
  public boolean isDepthFirstSearch() {
    return depthFirstSearch;
  }

  /**
   * The 'fullSearch' property tells the different search methods wether or not to fetch
   * children when node returns TriState.UNKNOWN to the GraphLocation.hasChildren() method.
   * Typically, 'UNKNOWN' is only returned by an implementor of GraphLocation when the process
   * of fetching the children is potentially costly (in CPU cycles).  If the 'fullSearch'
   * property is set to false, all search algorythms will treat an 'UNKNOWN' response to
   * GraphLocation.hasChildren() as 'NO' and continue on without fetching the child nodes of
   * that node.  This can potentially effect search results in that any nodes below an 'UNKNOWN'
   * children node will not be checked.  By default, this property is true, and all children are
   * checked.
   *
   * @param fullSearch true to fetch children of all nodes, even if GraphLocation.hasChildren()
   *        returns TriState.UNKNOWN
   */
  public void setFullSearch(boolean fullSearch) {
    this.fullSearch = fullSearch;
  }
  public boolean isFullSearch() {
    return fullSearch;
  }

  /**
   * This search method will search the entire GraphModel structure (starting at the
   * root node), looking for a match to the passed data object.  If the data object is
   * found, it will return a GraphLocation representing the address of the data object.
   * If the data object cannot be found in the GraphModel structure, this method returns
   * null.
   *
   * @param data The data object to search for in the GraphModel
   * @return The GraphLocation representing the address of the data object, or null if
   *         the data object could not be found in the GraphModel
   * @see setModel
   */
  public GraphLocation search(Object data) {
    checkModel();
    if (data instanceof GraphLocation)
      return (GraphLocation)data;
    else
      return search(model.getRoot(), data);
  }

  /**
   * This search method will search the GraphModel structure starting at the passed
   * node and down, looking for a match to the passed data object.  If the data object is
   * found, it will return a GraphLocation representing the address of the data object.
   * If the data object cannot be found in the GraphModel structure, this method returns
   * null.<P>
   *
   * NOTE: This is a recursive method (it calls itself), and subclasses should be <B>VERY</B>
   *       careful when overriding this method.
   *
   * @param node The GraphLocation to begin searching under for the data object
   * @param data The data object to search for in the GraphModel
   * @return The GraphLocation representing the address of the data object, or null if
   *         the data object could not be found in the GraphModel
   * @see setModel
   */
  public GraphLocation search(GraphLocation node, Object data) {
    checkModel();
    if (checkMatch(model.get(node), data))
      return node;
    int hasChildren = node.hasChildren();
    if (hasChildren == TriState.NO || !fullSearch && hasChildren == TriState.UNKNOWN)
      return null;
    GraphLocation[] children = node.getChildren();
    GraphLocation found = depthFirstSearch ? null : scanNodes(children, data);
    if (found != null)
      return found;
    for (int i = 0; i < children.length; i++) {
      found = search(children[i], data);
      if (found != null)
        return found;
    }
    return null;
  }

  /**
   * This path searching method will search the entire GraphModel structure (starting at
   * the root node), looking for matches to the passed data objects.  As each data object
   * is found, the algorythm will dig deeper under that node to find the next data object.
   * If the last data object is found, it will return a GraphLocation representing its
   * address.  If the last data object cannot be found in the GraphModel structure, this
   * method returns null.  This method allows for the passed data object array to not-
   * directly correspond to the hierarchy of the GraphModel, but each data object must
   * exist in the GraphModel - and must exist under the same parent blood line.
   *
   * @param data The array of data objects to search for in the GraphModel
   * @return The GraphLocation representing the address of the data object, or null if
   *         the data object could not be found in the GraphModel
   * @see setModel
   */
  public GraphLocation pathSearch(Object[] data) {
    checkModel();
    if (data != null && data.length > 0)
      return pathSearch(model.getRoot(), data);
    return null;
  }

  /**
   * This path searching method will search the GraphModel structure (starting at
   * the passed node), looking for matches to the passed data objects.  As each data object
   * is found, the algorythm will dig deeper under that node to find the next data object.
   * If the last data object is found, it will return a GraphLocation representing its
   * address.  If the last data object cannot be found in the GraphModel structure, this
   * method returns null.  This method allows for the passed data object array to not-
   * directly correspond to the hierarchy of the GraphModel, but each data object must
   * exist in the GraphModel - and must exist under the same parent blood line.
   *
   * @param data The array of data objects to search for in the GraphModel
   * @return The GraphLocation representing the address of the data object, or null if
   *         the data object could not be found in the GraphModel
   * @see setModel
   */
  public GraphLocation pathSearch(GraphLocation node, Object[] data) {
    checkModel();
    GraphLocation currentNode = node;
    for (int i = 0; i < data.length; i++) {
      GraphLocation found = search(currentNode, data[i]);
      if (found == null)
        return null;
      currentNode = found;
    }
    return currentNode;
  }

  /**
   * This path searching method will search the entire GraphModel structure (starting at
   * the root node), looking for matches to the passed data objects.  As each data object
   * is found, the algorythm will dig deeper under that node to find the next data object.
   * If a data object cannot be found, it will return a GraphLocation representing the
   * deepest object it was able to locate - ignoring the rest of the data object array
   * contents.
   *
   * @param data The array of data objects to search for in the GraphModel
   * @return The GraphLocation representing the address of the deepest found data object
   * @see setModel
   */
  public GraphLocation partialPathSearch(Object[] data) {
    checkModel();
    if (data != null && data.length > 0)
      return partialPathSearch(model.getRoot(), data);
    return null;
  }

  /**
   * This path searching method will search the GraphModel structure (starting at
   * the passed node), looking for matches to the passed data objects.  As each data object
   * is found, the algorythm will dig deeper under that node to find the next data object.
   * If a data object cannot be found, it will return a GraphLocation representing the
   * deepest object it was able to locate - ignoring the rest of the data object array
   * contents.
   *
   * @param data The array of data objects to search for in the GraphModel
   * @return The GraphLocation representing the address of the deepest found data object
   * @see setModel
   */
  public GraphLocation partialPathSearch(GraphLocation node, Object[] data) {
    checkModel();
    GraphLocation currentNode = node;
    for (int i = 0; i < data.length; i++) {
      GraphLocation found = search(currentNode, data[i]);
      if (found == null)
        return currentNode;
      currentNode = found;
    }
    return currentNode;
  }

  /**
   * This path searching method will search the entire GraphModel structure (starting at
   * the root node), looking for matches to the passed data objects.  As each data object
   * is found in the immediate child list of the previous, the algorythm will dig deeper
   * under that node to find the next data object.  If the last data object is found, this
   * method returns a GraphLocation representing its address in the GraphModel structure.
   * If the last data object cannot be found in the GraphModel structure, or any of the
   * passed data objects cannot be found in order in the child list of the previous found
   * node, this method returns null.  This method requires the passed data object array to
   * <B>directly</B> correspond to the hierarchy of the GraphModel, following the exact
   * parent blood line.
   *
   * @param data The array of data objects to search for in the GraphModel
   * @return The GraphLocation representing the address of the data object, or null if
   *         the data object could not be found in the GraphModel, or if the object array
   *         did not directly correspond to the hierarchy of the GraphModel.
   * @see setModel
   */
  public GraphLocation exactPathSearch(Object[] data) {
    checkModel();
    if (data != null && data.length > 0)
      return exactPathSearch(model.getRoot(), data);
    return null;
  }

  /**
   * This path searching method will search the GraphModel structure (starting at
   * the passed node), looking for matches to the passed data objects.  As each data object
   * is found in the immediate child list of the previous, the algorythm will dig deeper
   * under that node to find the next data object.  If the last data object is found, this
   * method returns a GraphLocation representing its address in the GraphModel structure.
   * If the last data object cannot be found in the GraphModel structure, or any of the
   * passed data objects cannot be found in order in the child list of the previous found
   * node, this method returns null.  This method requires the passed data object array to
   * <B>directly</B> correspond to the hierarchy of the GraphModel, following the exact
   * parent blood line.
   *
   * @param data The array of data objects to search for in the GraphModel
   * @return The GraphLocation representing the address of the data object, or null if
   *         the data object could not be found in the GraphModel, or if the object array
   *         did not directly correspond to the hierarchy of the GraphModel.
   * @see setModel
   */
  public GraphLocation exactPathSearch(GraphLocation node, Object[] data) {
    checkModel();
    if (data.length < 1 || !checkMatch(model.get(node), data[0]))
      return null;
    GraphLocation currentNode = node;
    for (int i = 1; i < data.length; i++) {
      int hasChildren = currentNode.hasChildren();
      if (hasChildren == TriState.NO || !fullSearch && hasChildren == TriState.UNKNOWN)
        return null;
      GraphLocation[] children = currentNode.getChildren();
      GraphLocation found = scanNodes(children, data[i]);
      if (found == null)
        return null;
      currentNode = found;
    }
    return currentNode;
  }

  // Internal

  /**
   * This method (called from nearly all other methods) checks to make sure the model
   * property is correctly set before proceeding.  It throws an IllegalStateException
   * if no model is set.
   * @throws IllegalStateException when model property is not set
   */
  protected void checkModel() {
    if (model == null)
      throw new IllegalStateException(Res._NoModelSet);     
  }

  /**
   * This method (called from several other methods) compares the two passed data objects
   * see if they match - first with a straight '==' check, then with a '.equals' check.<P>
   *
   * NOTE: subclasses may wish to override this method to allow for incremental searching
   *       or partial matches.
   *
   * @param first The first data object
   * @param second The second data object
   * @param return true if the objects match, otherwise false
   */
  protected boolean checkMatch(Object first, Object second) {
    return (first == second || first != null && first.equals(second));
  }

  /**
   * This method (called from several other methods) scans the passed GraphLocation array
   * to see if the corresponding data objects in the GraphModel are equal to the passed
   * data object.
   *
   * @param nodes The array of GraphLocations to scan
   * @param data The data object to compare to
   * @param return The GraphLocation address containing the matching data object, or null
   *        if no match was found.
   */
  protected GraphLocation scanNodes(GraphLocation[] nodes, Object data) {
    checkModel();
    for (int i = 0; i < nodes.length; i++) {
      if (checkMatch(model.get(nodes[i]), data))
        return nodes[i];
    }
    return null;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(model instanceof Serializable ? model : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof GraphModel)
      model = (GraphModel)data;
  }

  protected transient GraphModel model;
  protected boolean depthFirstSearch = false;
  protected boolean fullSearch = true;
}

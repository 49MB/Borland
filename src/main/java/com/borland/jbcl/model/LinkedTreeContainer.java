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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import com.borland.jb.util.EventMulticaster;

/**
 * Linked-tree implementation of WritableGraphModel, works with data as subclass
 * of node.
 */
@SuppressWarnings("serial")
public class LinkedTreeContainer implements WritableGraphModel, Serializable {
  /**
   * Creates a new LinkedTreeContainer with a null root node.
   */
  public LinkedTreeContainer() {
  }
  
  /**
   * Creates a new LinkedTreeContainer with the specified root node.
   * 
   * @param root
   *          The root node for the new tree.
   */
  public LinkedTreeContainer(LinkedTreeNode root) {
    this.root = root;
  }
  
  // WritableGraphModel Implementation
  
  public GraphLocation getRoot() {
    return root;
  }
  
  public Object get(GraphLocation gl) {
    return gl;
  }
  
  public GraphLocation find(Object data) {
    if (data instanceof GraphLocation)
      return (GraphLocation) data;
    else
      return null; // subclasses should search tree
  }
  
  public void addModelListener(GraphModelListener listener) {
    listeners.add(listener);
  }
  
  public void removeModelListener(GraphModelListener listener) {
    listeners.remove(listener);
  }
  
  public GraphLocation setRoot(Object data) {
    root = (LinkedTreeNode) data;
    processModelEvent(new GraphModelEvent(this,
        GraphModelEvent.STRUCTURE_CHANGED));
    return root;
  }
  
  public boolean canSet(GraphLocation gl) {
    return true;
  }
  
  public void set(GraphLocation gl, Object data) {
    if (gl != data) {
      LinkedTreeNode node = (LinkedTreeNode) gl;
      node.parent.insertChild((LinkedTreeNode) data, node);
      node.parent.removeChild(node);
      processModelEvent(new GraphModelEvent(this,
          GraphModelEvent.NODE_REPLACED, (GraphLocation) gl));
    } else
      processModelEvent(new GraphModelEvent(this, GraphModelEvent.ITEM_CHANGED,
          gl));
  }
  
  public void touched(GraphLocation gl) {
    processModelEvent(new GraphModelEvent(this, GraphModelEvent.ITEM_TOUCHED,
        gl));
  }
  
  public boolean isVariableSize() {
    return true;
  }
  
  public GraphLocation addChild(GraphLocation gl, Object data) {
    ((LinkedTreeNode) gl).appendChild((LinkedTreeNode) data);
    processModelEvent(new GraphModelEvent(this, GraphModelEvent.NODE_ADDED,
        (GraphLocation) data));
    return (GraphLocation) data;
  }
  
  public GraphLocation addChild(GraphLocation parent, GraphLocation aheadOf,
      Object data) {
    ((LinkedTreeNode) parent).insertChild((LinkedTreeNode) data,
        (LinkedTreeNode) aheadOf);
    processModelEvent(new GraphModelEvent(this, GraphModelEvent.NODE_ADDED,
        (GraphLocation) data));
    return (GraphLocation) data;
  }
  
  public void removeChildren(GraphLocation parent) {
    Enumeration<Object> i = ((LinkedTreeNode) parent).getChildIterator();
    while (i.hasMoreElements()) {
      LinkedTreeNode node = (LinkedTreeNode) i.nextElement();
      node.parent.removeChild(node);
    }
    processModelEvent(new GraphModelEvent(this, GraphModelEvent.NODE_REPLACED,
        parent));
  }
  
  public void remove(GraphLocation gl) {
    LinkedTreeNode node = (LinkedTreeNode) gl;
    if (node.parent != null) {
      node.parent.removeChild(node);
      processModelEvent(new GraphModelEvent(this, GraphModelEvent.NODE_REMOVED,
          gl));
    } else if (node == root) {
      root = null;
      processModelEvent(new GraphModelEvent(this,
          GraphModelEvent.STRUCTURE_CHANGED));
    }
  }
  
  public void removeAll() {
    root = null;
    processModelEvent(new GraphModelEvent(this,
        GraphModelEvent.STRUCTURE_CHANGED));
  }
  
  public void enableModelEvents(boolean enable) {
    if (events != enable) {
      events = enable;
      if (enable)
        processModelEvent(new GraphModelEvent(this,
            GraphModelEvent.STRUCTURE_CHANGED));
    }
  }
  
  // LinkedTreeContainer Methods
  
  /**
   * Returns an elements Enumeration.
   * 
   * @return An enumeration over the elements of the tree.
   */
  public Enumeration<LinkedTreeNode> elements() {
    Vector<LinkedTreeNode> nodes = new Vector<LinkedTreeNode>();
    fillNodes(root, nodes);
    return nodes.elements();
  }
  
  /**
   * Used to fill in the nodes from an input iterator.
   * 
   * @param node
   *          The node to fill in.
   * @param nodes
   *          The nodes to add to the node.
   */
  protected void fillNodes(LinkedTreeNode node, Vector<LinkedTreeNode> nodes) {
    while (node != null) {
      nodes.addElement(node);
      if (node.firstChild != null)
        fillNodes(node.firstChild, nodes);
      node = node.nextSibling;
    }
  }
  
  /**
   * Internal processing of GraphModelEvents. This method will dispatch the
   * passed model event to all registered listeners.
   * 
   * @param e
   *          The GraphModelEvent to dispatch.
   */
  public void processModelEvent(GraphModelEvent e) {
    if (events && listeners.hasListeners())
      listeners.dispatch(e);
  }
  
  protected LinkedTreeNode root;
  protected transient EventMulticaster listeners = new EventMulticaster();
  protected boolean events = true;
}

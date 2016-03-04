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
 * Linked-tree implementation of WritableGraphModel, hangs externally supplied data on built-in
 * treenodes when the supplied data is not a LinkedTreeNode or subclass thereof.
 */
public class BasicTreeContainer extends LinkedTreeContainer implements Serializable
{
  private static final long serialVersionUID = 200L;

  /**
   * Constructs a new BasicTreeContainer with a null root.
   */
  public BasicTreeContainer() {}

  /**
   * Constructs a new BasicTreeContainer with the specified root.
   */
  public BasicTreeContainer(Object root) {
    LinkedTreeNode newNode = root instanceof LinkedTreeNode ? (LinkedTreeNode)root : new BasicTreeNode(root);
    setRoot(newNode);
  }

  // WritableGraphModel Implementation (overrides of superclass)

  public Object get(GraphLocation gl) {
    return gl instanceof BasicTreeNode ? ((BasicTreeNode)gl).data : super.get(gl);
  }

  public GraphLocation find(Object data) {
//    if (data instanceof GraphLocation)
//      return (GraphLocation)data;
//    else
      return treeSearch(root, data);
  }

  /*
   * Recursive tree scan. Searches 'tree' container starting at element 'node' for 'data'.
   * @returns if source not found in tree, 'null' is returned.
   *          if source found, node of element where match was found is returned.
   */
  private LinkedTreeNode treeSearch(LinkedTreeNode node, Object data) {
    //System.err.println(" treeSearch node=" + node + " data=" + data);
    Object nodeData = get(node);
    if (nodeData == data || nodeData != null && nodeData.equals(data))
      return node;
    if (node.hasChildren() == TriState.NO)
      return null;
    LinkedTreeNode child = node.getFirstChild();
    while (child != null) {
      LinkedTreeNode found = treeSearch(child, data);
      if (found != null)
        return found;
      child = child.getNextSibling();
    }
    return null;
  }

  public GraphLocation setRoot(Object data) {
    if (data instanceof LinkedTreeNode)
      return super.setRoot(data);
    else {
      BasicTreeNode node = new BasicTreeNode(data);
      return super.setRoot(node);
    }
  }

  public void set(GraphLocation gl, Object data) {
    if (gl instanceof BasicTreeNode) {
      ((BasicTreeNode)gl).data = data;
      processModelEvent(new GraphModelEvent(this, GraphModelEvent.ITEM_CHANGED, gl));
    }
    else
      super.set(gl, data);
  }

  public GraphLocation addChild(GraphLocation gl, Object data) {
    LinkedTreeNode newNode = data instanceof LinkedTreeNode ? (LinkedTreeNode)data : new BasicTreeNode(data);
    if (gl == null) {
      setRoot(newNode);
      return root;
    }
    else {
      ((LinkedTreeNode)gl).appendChild(newNode);
      processModelEvent(new GraphModelEvent(this, GraphModelEvent.NODE_ADDED, (GraphLocation)newNode));
      return newNode;
    }
  }

  public GraphLocation addChild(GraphLocation parent, GraphLocation aheadOf, Object data) {
    LinkedTreeNode newNode = data instanceof LinkedTreeNode ? (LinkedTreeNode)data : new BasicTreeNode(data);
    if (parent == null) {
      setRoot(newNode);
      return root;
    }
    else {
      ((LinkedTreeNode)parent).insertChild(newNode, (LinkedTreeNode)aheadOf);
      processModelEvent(new GraphModelEvent(this, GraphModelEvent.NODE_ADDED, (GraphLocation)newNode));
      return newNode;
    }
  }
}

/**
 * BasicTreeNode is a private node that holds a "cookie" of data.
 */
class BasicTreeNode extends LinkedTreeNode implements Serializable
{
  public BasicTreeNode(Object data) {
    this.data = data;
  }

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(data instanceof Serializable ? data : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    data = s.readObject();
  }

  transient Object data;
}

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

import com.borland.jbcl.util.TriState;

/**
 * Linked tree node implementation of GraphLocation.  This class is the item
 * container for use with LinkedTreeContainer.
 * The LinkedTreeNode is separated into two sections:<P>
 * <UL><LI>GraphLocation Implementation<BR>
 * Provides an implementation of GraphLocation to comply with the GraphModel.
 * <LI>LinkedTreeNode Internals<BR>
 * These methods are the heart of the LinkedTreeNode's functionality.  A subclass
 * of LinkedTreeNode can use these methods to help manage the data structure.
 */
public class LinkedTreeNode implements GraphLocation, Serializable
{
  public LinkedTreeNode() {}

  public LinkedTreeNode(LinkedTreeNode parent) {
    this.parent = parent;
  }

  // GraphLocation Implementation

  public GraphLocation getParent() {
    return parent;
  }

  public int hasChildren() {
    return firstChild != null ? TriState.YES : TriState.NO;
  }

  public GraphLocation[] getChildren() {
    GraphLocation[] children = new GraphLocation[childCount];
    int i = 0;
    for (Enumeration enumerator = getChildIterator(); enumerator.hasMoreElements(); ) {
      // added safety feature (childCount is wrong!)
/*
      if (childCount == i) {
        System.err.println(" regrowing children from " + children.length);
        childCount++;
        GraphLocation[] c = new GraphLocation[childCount];
        System.arraycopy(c, 0, children, 0, childCount - 1);
        children = c;
        System.err.println("  grown to " + children.length);
      }
*/
      //System.err.println("children[] is size: " + children.length + " and i is " + i);
      if (i >= children.length) {
        //System.err.println("Detect late need to grow to fit child " + i);
        childCount = i + 1;
        GraphLocation[] c = new GraphLocation[childCount];
        System.arraycopy(children, 0, c, 0, childCount - 1);
        children = c;
        //System.err.println("  grown to " + children.length);
      }
      children[i++] = (GraphLocation)enumerator.nextElement();
    }
    return children;
  }

  // LinkedTreeNode Internals

  /**
   * Returns the first child of this node.
   * @return The first child LinkedTreeNode of this node.
   */
  public LinkedTreeNode getFirstChild() {
    return firstChild;
  }

  /**
   * Returns an enumeration over the children of this node.
   * @return An enumeration of this node's children
   * starting with the first child.
   */
  public Enumeration getChildIterator() {
    return new LinkedTreeIterator(firstChild);
  }

  /**
   * Returns the next sibling of this node.
   * @return The next sibling LinkedTreeNode of this node.
   */
  public LinkedTreeNode getNextSibling() {
    return nextSibling;
  }

  /**
   * Adds a node chain after the last child of this node.
   * @param newChild The node chain to append.
   */
  public void appendChild(LinkedTreeNode newChild) {
    doAddChild(newChild, null);
  }

  /**
   * Adds a node chain in front of the first child of this node.
   * @param newChild The node chain to insert.
   */
  public void insertChild(LinkedTreeNode newChild) {
    doAddChild(newChild, firstChild);
  }

  /**
   * Adds a node chain in front of a given child of this node.
   * @param newChild The node chain to insert.
   * @param aheadOf The existing node to insert the node chain before.
   */
  public void insertChild(LinkedTreeNode newChild, LinkedTreeNode aheadOf) {
    doAddChild(newChild, aheadOf);
  }

  /**
   * This is the main function doing to work with the LinkedTreeNodes to add
   * them to the child node chain.  A subclass can override this if any extra
   * functionality is needed - but most likely it will be used as-is.
   * @param newChild The node to add to the node chain.
   * @param aheadOf The existing node to insert the node chain before.  This
   * parameter is null if the node is to be appended to end of node chain.
   */
  protected void doAddChild(LinkedTreeNode newChild, LinkedTreeNode aheadOf) {
    // set up the new node(s) parent, find the end & check for problems as we go
    LinkedTreeNode newChildTail = newChild;
    for (;;) {
      if (newChildTail.parent == null) {
        newChildTail.setParent(this);
        childCount++;
      }
      else if (newChildTail.parent != this)
        throw new IllegalArgumentException(java.text.MessageFormat.format(Res._CantAdd, new Object[] {String.valueOf(newChildTail), String.valueOf(this), String.valueOf(newChildTail.parent)} ));     
      if (newChildTail.nextSibling == null)
        break;
      newChildTail = newChildTail.nextSibling;
    }

    // insert or append the new node chain
    if (firstChild == null || firstChild == aheadOf) {
      newChildTail.nextSibling = firstChild;
      firstChild = newChild;
    }
    else {
      for (LinkedTreeNode c = firstChild; ; c = c.nextSibling) {
        if (c.nextSibling == firstChild)
          throw new IllegalStateException(java.text.MessageFormat.format(Res._TreeCircularity, new Object[] {this, c} ));     
        if (c.nextSibling == newChild)
          throw new IllegalStateException(java.text.MessageFormat.format(Res._TreeNode, new Object[] {this, newChild} )); // not likely, but...     //RES TreeNode
        if (c.nextSibling == null || c.nextSibling == aheadOf) {
          newChildTail.nextSibling = c.nextSibling;
          c.nextSibling = newChild;
          return;
        }
      }
    }
  }

  /**
   * Adds node chain in front of the first sibling of this node.
   * @param newSibling The node to insert into node chain.
   */
  public void insertSibling(LinkedTreeNode newSibling) {
    for (LinkedTreeNode s = newSibling; ; s = s.nextSibling) {
      if (s.parent != null)
        throw new IllegalArgumentException(java.text.MessageFormat.format(Res._TreeNode1, new Object[] {s, s.parent} ));     
      if (s.nextSibling == this)
        throw new IllegalStateException(java.text.MessageFormat.format(Res._TreeNode2, new Object[] {this, newSibling} )); // not likely, but...     //RES TreeNode2
      s.setParent(parent);
      if (s.nextSibling == null) {
        s.nextSibling = nextSibling;
        break;
      }
      childCount++;
    }
    nextSibling = newSibling;
  }

  /**
   * Removes a single child from this node.
   * @param unwantedChild The node to remove from the child node chain.
   */
  public void removeChild(LinkedTreeNode unwantedChild) {
    if (firstChild == unwantedChild) {
      firstChild = unwantedChild.nextSibling;
    }
    else if (firstChild != null) {
      for (LinkedTreeNode c = firstChild; ; c = c.nextSibling) {
        if (c.nextSibling == unwantedChild) {
          c.nextSibling = unwantedChild.nextSibling;
          break;
        }
        if (c.nextSibling == null)
          throw new IllegalArgumentException(java.text.MessageFormat.format(Res._TreeNode3, new Object[] {unwantedChild, this} ));     
      }
    }
    unwantedChild.setParent(null);
    unwantedChild.nextSibling = null;
    childCount--;
  }

  /**
   * Removes this node from its tree. Pass in root for possible adjustment, new
   * root will be returned, possibly different.
   * @param root The root node of this tree.
   * @return The tree's root node (which may have been modified).
   */
  public LinkedTreeNode removeNode(LinkedTreeNode root) {
    if (parent != null) {
      parent.removeChild(this);
    }
    else { // deleting root level node
      if (this == root)
        root = nextSibling;
      else {
        LinkedTreeNode lastSibling = root;
        while (lastSibling.nextSibling != this)  
          lastSibling = lastSibling.nextSibling;
        lastSibling.nextSibling = nextSibling;
      }
    }
    nextSibling = null;
    return root;
  }

  /**
   * Sets this node's parent node.
   * @param parent The new parent node.
   */
  protected void setParent(LinkedTreeNode parent) {
    this.parent = parent;
  }

  // Debugging Aids

  /**
   *
   */
  public void print() {
    print(0);
  }

  /**
   *
   */
  public void print(int level) {
    for (int i = 0; i < 2*level; i++)
      System.err.print(" ");
    System.err.println(this + " L" + level); 
    if (firstChild != null)
      firstChild.print(level+1);
    if (nextSibling != null)
      nextSibling.print(level);
  }

  /**
   *
   */
  public void check() {
    for (LinkedTreeNode c = firstChild; c != null; c = c.nextSibling) {
      if (c.parent != this)
        throw new IllegalStateException(java.text.MessageFormat.format(Res._TreeNode4, new Object[] {c, this} ));     
      if (c.nextSibling == firstChild)
        throw new IllegalStateException(java.text.MessageFormat.format(Res._TreeCircularity, new Object[] {this, c} ));     
    }
  }

  // Member Data

  protected LinkedTreeNode parent;
  protected LinkedTreeNode nextSibling;
  protected LinkedTreeNode firstChild;
  protected int childCount;
}

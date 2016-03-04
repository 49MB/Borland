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

// Iterates over the siblings of a linked tree.
//
@SuppressWarnings("serial")
public class LinkedTreeIterator implements Enumeration<Object>, Serializable {
  
  public LinkedTreeIterator(LinkedTreeNode firstSibling) {
    this.firstSibling = firstSibling;
    sibling = firstSibling;
  }
  
  // Enumeration
  
  public Object nextElement() {
    Object cur = sibling;
    advance();
    return cur;
  }
  
  public boolean hasMoreElements() {
    return sibling != null;
  }
  
  public boolean atBegin() {
    return sibling == firstSibling;
  }
  
  public boolean atEnd() {
    return sibling == null;
  }
  
  public Object get() {
    return sibling;
  }
  
  public void advance() {
    if (sibling != null)
      sibling = sibling.nextSibling;
  }
  
  public void advance(int offset) {
    for (int i = 0; i < offset && sibling != null; i++)
      advance(); // sibling = sibling.nextSibling;
  }
  
  public Object clone() {
    LinkedTreeIterator that = new LinkedTreeIterator(firstSibling);
    that.sibling = sibling;
    return that;
  }
  
  public boolean equals(LinkedTreeIterator that) {
    return // that instanceof LinkedTreeIterator &&
    that.sibling == sibling && that.firstSibling == firstSibling;
  }
  
  // OutputIterator interface
  
  public void put(Object data) {
    if (sibling == null)
      firstSibling.appendChild((LinkedTreeNode) data);
    else
      firstSibling.insertChild((LinkedTreeNode) data, sibling);
  }
  
  public int distance(LinkedTreeIterator that) {
    int d = 0;
    LinkedTreeNode c = that.sibling;
    while (c != sibling && c != null) {
      c = c.nextSibling;
      d++;
    }
    return c == sibling ? d : -1;
  }
  
  // JGL3.1 additions
  public void put(int offset, Object object) {
    advance(offset);
    put(object);
  }
  
  public Object get(int offset) {
    advance(offset);
    return get();
  }
  
  // JGL3.1 addition
  
  protected LinkedTreeNode firstSibling;
  protected LinkedTreeNode sibling;
}

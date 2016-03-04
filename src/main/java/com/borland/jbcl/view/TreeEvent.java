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
//
// TreeView item event
//--------------------------------------------------------------------------------------------------
package com.borland.jbcl.view;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.GraphLocation;

/**
 * Tree specific events
 */
public class TreeEvent extends com.borland.jb.util.DispatchableEvent implements Serializable
{
  public static final int NODE_EXPANDED  = 1000;
  public static final int NODE_COLLAPSED = 1001;

  public TreeEvent(Object source, int id, GraphLocation node) {
    super(source);
    this.id = id;
    this.node = node;
  }

  /**
   * Returns the item where the event occurred.
   */
  public GraphLocation getLocation() {
    return node;
  }

  /**
   * Returns the event type
   * @see #NODE_EXPANDED
   * @see #NODE_COLLAPSED
   */
  public int getID() {
    return id;
  }

  public void dispatch(java.util.EventListener listener) {
    switch (id) {
      case NODE_EXPANDED:
        ((TreeListener)listener).nodeExpanded(this);
        break;
      case NODE_COLLAPSED:
        ((TreeListener)listener).nodeCollapsed(this);
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  protected String paramString() {
    String idStr;
    switch (id) {
      case NODE_EXPANDED:  idStr = "NODE_EXPANDED";   break;  
      case NODE_COLLAPSED: idStr = "NODE_COLLAPSED"; break;  
      default:             idStr = "<INVALID>";               
    }
    return super.paramString() + "id=" + idStr + ",location=" + node;  
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(node instanceof Serializable ? node : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof GraphLocation)
      node = (GraphLocation)data;
  }

  private int id;
  private transient GraphLocation node;
}

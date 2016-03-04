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
import java.util.Hashtable;

import com.borland.jb.util.Diagnostic;

public class GraphModelEvent extends ModelEvent implements Serializable
{
  private static final long serialVersionUID = 200L;

  public static final int CONTENT_CHANGED   = ModelEvent.CONTENT_CHANGED;   // model contents changed
  public static final int STRUCTURE_CHANGED = ModelEvent.STRUCTURE_CHANGED; // model structure changed

  public static final int ITEM_CHANGED    = CONTENT_CHANGED | 0x0010; // a node's data has changed
  public static final int ITEM_TOUCHED    = CONTENT_CHANGED | 0x0020; // a node's data has been manipulated

  public static final int NODE_ADDED      = STRUCTURE_CHANGED | 0x0010; // a node has been added at location (model structure change)
  public static final int NODE_REMOVED    = STRUCTURE_CHANGED | 0x0020; // a node has been removed at location (model structure change)
  public static final int NODE_REPLACED   = STRUCTURE_CHANGED | 0x0030; // a node has been replaced at location (model structure change)

  public GraphModelEvent(GraphModel model, int change) {
    super(model, change);
    this.model = model;
    this.change = change;
  }
  public GraphModelEvent(GraphModel model, int change, GraphLocation location) {
    this(model, change);
    this.location = location;
  }

  public GraphModel    getModel() { return model; }
  public int           getChange() { return change; }
  public GraphLocation getLocation() { return location; }

  public void dispatch(java.util.EventListener listener) {
    switch (getID()) {
      case CONTENT_CHANGED:
        ((GraphModelListener)listener).modelContentChanged(this);
        break;
      case STRUCTURE_CHANGED:
        ((GraphModelListener)listener).modelStructureChanged(this);
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  protected String paramString() {
    String changeString;
    switch (change) {
      case CONTENT_CHANGED:   changeString = "CONTENT_CHANGED";   break;  
      case STRUCTURE_CHANGED: changeString = "STRUCTURE_CHANGED"; break;  
      case ITEM_CHANGED:      changeString = "ITEM_CHANGED";      break;  
      case ITEM_TOUCHED:      changeString = "ITEM_TOUCHED";      break;  
      case NODE_ADDED:        changeString = "NODE_ADDED";        break;  
      case NODE_REMOVED:      changeString = "NODE_REMOVED";      break;  
      case NODE_REPLACED:     changeString = "NODE_REPLACED";     break;  
      default:                changeString = "<INVALID>";         break;  
    }
    return super.paramString() + ",model=" + model + ",change=" + changeString + ",location=" + location;  
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (model instanceof Serializable)
      hash.put("m", model); 
    if (location instanceof Serializable)
      hash.put("l", location); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("m"); 
    if (data instanceof GraphModel)
      model = (GraphModel)data;
    data = hash.get("l"); 
    if (data instanceof GraphLocation)
      location = (GraphLocation)data;
  }

  private transient GraphModel model;
  private transient GraphLocation location;
  private int change;
}

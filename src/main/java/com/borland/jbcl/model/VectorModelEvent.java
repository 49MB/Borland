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

import com.borland.jb.util.Diagnostic;

public class VectorModelEvent extends ModelEvent implements Serializable
{
  private static final long serialVersionUID = 200L;

  public static final int CONTENT_CHANGED   = ModelEvent.CONTENT_CHANGED;   // model contents changed
  public static final int STRUCTURE_CHANGED = ModelEvent.STRUCTURE_CHANGED; // model structure changed

  public static final int ITEM_CHANGED = CONTENT_CHANGED | 0x0010; // item's data changed
  public static final int ITEM_TOUCHED = CONTENT_CHANGED | 0x0020; // data object was manipulated

  public static final int ITEM_ADDED   = STRUCTURE_CHANGED | 0x0010; // an item has been added
  public static final int ITEM_REMOVED = STRUCTURE_CHANGED | 0x0020; // an item has been deleted

  public VectorModelEvent(VectorModel model, int change) {
    super(model, change);
    this.model    = model;
    this.change   = change;
  }
  public VectorModelEvent(VectorModel model, int change, int location) {
    this(model, change);
    this.location = location;
  }

  public VectorModel getModel() { return model; }
  public int         getChange() { return change; }
  public int         getLocation() { return location; }

  public void dispatch(java.util.EventListener listener) {
    switch (getID()) {
      case CONTENT_CHANGED:
        ((VectorModelListener)listener).modelContentChanged(this);
        break;
      case STRUCTURE_CHANGED:
        ((VectorModelListener)listener).modelStructureChanged(this);
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
      case ITEM_ADDED:        changeString = "ITEM_ADDED";        break;  
      case ITEM_REMOVED:      changeString = "ITEM_REMOVED";      break;  
      default:                changeString = "<INVALID>";         break;  
    }
    return super.paramString() + ",model=" + model + ",change=" + changeString + ",location=" + location;  
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(model instanceof Serializable ? model : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof VectorModel)
      model = (VectorModel)data;
  }

  private transient VectorModel model;
  private int change;
  private int location;
}

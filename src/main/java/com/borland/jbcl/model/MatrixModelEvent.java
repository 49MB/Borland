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

public class MatrixModelEvent extends ModelEvent implements Serializable
{
  private static final long serialVersionUID = 200L;

  public static final int CONTENT_CHANGED   = ModelEvent.CONTENT_CHANGED;   // model contents changed
  public static final int STRUCTURE_CHANGED = ModelEvent.STRUCTURE_CHANGED; // model structure changed

  public static final int ITEM_CHANGED    = CONTENT_CHANGED | 0x0010; // an item's data changed
  public static final int ITEM_TOUCHED    = CONTENT_CHANGED | 0x0020; // an item's data was manipulated
  public static final int ROW_CHANGED     = CONTENT_CHANGED | 0x0030; // a row of data changed
  public static final int COLUMN_CHANGED  = CONTENT_CHANGED | 0x0040; // a column of data changed

  public static final int ROW_ADDED       = STRUCTURE_CHANGED | 0x0010; // a row has been added
  public static final int ROW_REMOVED     = STRUCTURE_CHANGED | 0x0020; // a row has been removed

  public static final int COLUMN_ADDED    = STRUCTURE_CHANGED | 0x0030; // a column has been added
  public static final int COLUMN_REMOVED  = STRUCTURE_CHANGED | 0x0040; // a column has been removed

  public MatrixModelEvent(MatrixModel model, int change) {
    super(model, change);
    this.model    = model;
    this.change   = change;
  }
  public MatrixModelEvent(MatrixModel model, int change, MatrixLocation location) {
    this(model, change);
    this.location = location;
  }

  public MatrixModel    getModel() { return model; }
  public int            getChange() { return change; }
  public MatrixLocation getLocation() { return location; }

  public void dispatch(java.util.EventListener listener) {
    switch (getID()) {
      case CONTENT_CHANGED:
        ((MatrixModelListener)listener).modelContentChanged(this);
        break;
      case STRUCTURE_CHANGED:
        ((MatrixModelListener)listener).modelStructureChanged(this);
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
      case ROW_CHANGED:       changeString = "ROW_CHANGED";       break;  
      case COLUMN_CHANGED:    changeString = "COLUMN_CHANGED";    break;  
      case ROW_ADDED:         changeString = "ROW_ADDED";         break;  
      case ROW_REMOVED:       changeString = "ROW_REMOVED";       break;  
      case COLUMN_ADDED:      changeString = "COLUMN_ADDED";      break;  
      case COLUMN_REMOVED:    changeString = "COLUMN_REMOVED";    break;  
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
    if (data instanceof MatrixModel)
      model = (MatrixModel)data;
  }

  private transient MatrixModel model;
  private MatrixLocation location;
  private int change;
}

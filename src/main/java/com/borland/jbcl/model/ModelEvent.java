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

public abstract class ModelEvent extends com.borland.jb.util.DispatchableEvent
implements Serializable
{
  public static final int CONTENT_CHANGED   = 0x0001; // model contents changed
  public static final int STRUCTURE_CHANGED = 0x0002; // model structure changed

  public ModelEvent(Object source, int id) {
    super(source);
    this.id = id & 0x000F;
  }

  public int getID() { return id; }

  protected String paramString() {
    String idString;
    switch (id) {
      case CONTENT_CHANGED:   idString = "CONTENT_CHANGED";   break;  
      case STRUCTURE_CHANGED: idString = "STRUCTURE_CHANGED"; break;  
      default:                idString = "<INVALID>";         break;  
    }
    return "id=" + idString;  
  }

  private int id;
}

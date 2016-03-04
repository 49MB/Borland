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
package com.borland.jbcl.view;

import java.util.EventListener;

import com.borland.jb.util.Diagnostic;

public class HeaderEvent
     extends com.borland.jb.util.DispatchableEvent
  implements java.io.Serializable
{
  public static final int ITEM_CLICKED = 0;
  public static final int START_MOVE   = 1;
  public static final int WHILE_MOVE   = 2;
  public static final int STOP_MOVE    = 3;
  public static final int START_RESIZE = 4;
  public static final int WHILE_RESIZE = 5;
  public static final int STOP_RESIZE  = 6;

  public HeaderEvent(Object source, int id, int index, int x, int y) {
    super(source);
    this.id     = id;
    this.index  = index;
    this.x      = x;
    this.y      = y;
  }

  public void dispatch(EventListener listener) {
    switch (id) {
      case ITEM_CLICKED:
        ((HeaderListener)listener).headerItemClicked(this);
        break;
      case START_MOVE:
      case WHILE_MOVE:
      case STOP_MOVE:
        ((HeaderListener)listener).headerItemMoving(this);
        break;
      case START_RESIZE:
      case WHILE_RESIZE:
      case STOP_RESIZE:
        ((HeaderListener)listener).headerItemResizing(this);
        break;
      default: // should never happen!
       Diagnostic.fail();
    }
  }

  public int getID()    { return id; }
  public int getIndex() { return index; }
  public int getX()     { return x; }
  public int getY()     { return y; }

  protected String paramString() {
    return "id=" + id + " index=" + index + " x=" + x + " y=" + y;  
  }

  private int id;
  private int index;
  private int x;
  private int y;
}

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

public abstract class SelectionEvent extends com.borland.jb.util.DispatchableEvent implements Serializable
{
  /**
   * event IDs provide basic categorization of events, & correspond to
   * the listener methods
   */
  public static final int ITEM_CHANGE      = 0x1000; // selection item change
  public static final int RANGE_CHANGE     = 0x2000; // selection range change
  public static final int SELECTION_CHANGE = 0x3000; // selection general change

  /**
   * Specific changes can be determined from these change flags
   */
  public static final int ITEM_ADDED        = ITEM_CHANGE | 0x0001; // item was added to selection pool
  public static final int ITEM_REMOVED      = ITEM_CHANGE | 0x0002; // item was removed from selection pool

  public static final int RANGE_ADDED       = RANGE_CHANGE | 0x0001; // a range was added to the selection
  public static final int RANGE_REMOVED     = RANGE_CHANGE | 0x0002; // a range was removed from the selection

  public static final int SELECTION_CLEARED = SELECTION_CHANGE | 0x0001; // selection was cleared
  public static final int SELECTION_CHANGED = SELECTION_CHANGE | 0x0002; // changes were too massive to track

  public SelectionEvent(Object source, int change) {
    super(source);
    this.change = change;
  }

  public int getID() { return change & 0xF000; }
  public int getChange() { return change; }

  protected String paramString() {
    String changeString;
    switch (change) {
      case ITEM_ADDED:        changeString = "ITEM_ADDED";        break;  
      case ITEM_REMOVED:      changeString = "ITEM_REMOVED";      break;  
      case RANGE_ADDED:       changeString = "RANGE_ADDED";       break;  
      case RANGE_REMOVED:     changeString = "RANGE_REMOVED";     break;  
      case SELECTION_CLEARED: changeString = "SELECTION_CLEARED"; break;  
      case SELECTION_CHANGED: changeString = "SELECTION_CHANGED"; break;  
      default:                changeString = "<INVALID>";         break;  
    }
    return "change=" + changeString;  
  }

  private int change;
}

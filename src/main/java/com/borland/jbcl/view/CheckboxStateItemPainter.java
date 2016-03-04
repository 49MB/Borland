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

import java.awt.Dimension;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

public class CheckboxStateItemPainter extends CheckboxItemPainter implements java.io.Serializable
{
  public CheckboxStateItemPainter() {
    super();
  }

  public CheckboxStateItemPainter(int checkedStates) {
    super();
    this.checkedStates = checkedStates;
  }

  public CheckboxStateItemPainter(Dimension boxSize) {
    super(boxSize);
  }

  public CheckboxStateItemPainter(Dimension boxSize, int style) {
    super(boxSize, style);
  }

  public CheckboxStateItemPainter(Dimension boxSize, int style, int checkedStates) {
    super(boxSize, style);
    this.checkedStates = checkedStates;
  }

  public CheckboxStateItemPainter(Dimension boxSize, int style, boolean flat) {
    super(boxSize, style, flat);
  }

  public CheckboxStateItemPainter(Dimension boxSize, int style, boolean flat, int checkedStates) {
    super(boxSize, style, flat);
    this.checkedStates = checkedStates;
  }

  public void setCheckedStates(int checkedStates) {
    this.checkedStates = checkedStates;
  }

  public int getCheckedStates() {
    return checkedStates;
  }

  protected boolean isChecked(Object data, int state, ItemPaintSite site) {
    return (state & checkedStates) != 0;
  }

  protected int checkedStates = ItemPainter.SELECTED; // default to check on selection
}

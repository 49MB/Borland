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

package com.borland.jbcl.control;

import java.awt.Color;
import java.awt.Frame;

public class ColorChooserDialog extends ButtonDialog implements java.io.Serializable
{
  public ColorChooserDialog(Frame frame, String title, Color value) {
    super(frame, title, null, OK_CANCEL);
    super.setCenterPanel(panel);
    initialValue = value;
    panel.setColorValue(value != null ? value : Color.white);
  }

  public ColorChooserDialog(Frame frame, String title) {
    this(frame, title, null);
  }

  public ColorChooserDialog(Frame frame) {
    this(frame, "", null);
  }

  public void setValue(Color value) {
    panel.setColorValue(value);
  }

  public Color getValue() {
    return panel.getColorValue();
  }

  protected ColorChooserPanel panel = new ColorChooserPanel();
  private Color initialValue;
}

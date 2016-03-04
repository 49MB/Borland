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

import java.awt.Font;
import java.awt.Frame;

public class FontChooserDialog extends ButtonDialog implements java.io.Serializable
{
  public FontChooserDialog(Frame frame, String title, Font value) {
    super(frame, title, null, OK_CANCEL);
    super.setCenterPanel(panel);
    initialValue = value;
    this.value = value;
    panel.setFontValue(value);
  }

  public FontChooserDialog(Frame frame, String title) {
    this(frame, title, null);
  }

  public FontChooserDialog(Frame frame) {
    this(frame, "", null);
  }

  public void setValue(Font value) {
    panel.setFontValue(value);
  }

  public Font getValue() {
    return panel.getFontValue();
  }

  protected FontChooserPanel panel = new FontChooserPanel();
  protected Font value;
  private Font initialValue;
}

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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyListener;

import com.borland.dx.dataset.Variant;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ToggleItemEditor;

public class BooleanToggleItemEditor implements ToggleItemEditor, java.io.Serializable
{
  private static final long serialVersionUID = 200L;

  public BooleanToggleItemEditor() {}

  public Object getValue() {
    switch (type) {
      case 1 : // Boolean
        return new Boolean(state);
      case 2 : // Variant
        Variant v = new Variant();
        v.setBoolean(state);
        return v;
      case 3 : // String
        return String.valueOf(state);
      case 4 : // Integer
        return new Integer(state ? 1 : 0);
      default :
        return new Boolean(false);
    }
  }

  public Component getComponent() {
    // non-visual editor
    return null;
  }

  public void startEdit(Object data, Rectangle bounds, ItemEditSite site) {
    // toggle the value and get out!
    toggle(data);
    site.safeEndEdit(true);
  }

  public void changeBounds(Rectangle bounds) {
    // does nothing
  }

  public boolean canPost() {
    return true;
  }

  public void endEdit(boolean post) {
    // does nothing
  }

  public boolean isToggle(Object data, Rectangle rect, ItemEditSite site) {
    return true;
  }

  public void addKeyListener(KeyListener l) {}
  public void removeKeyListener(KeyListener l) {}

  protected void toggle(Object data) {
    if (data instanceof Boolean) {
      type = 1;
      state = !((Boolean)data).booleanValue();
    }
    else if (data instanceof Variant) {
      type = 2;
      if (((Variant)data).getType() == Variant.BOOLEAN)
        state = !((Variant)data).getBoolean();
      else
        state = !Boolean.valueOf(data.toString()).booleanValue();
    }
    else if (data instanceof String) {
      type = 3;
      state = !Boolean.valueOf((String)data).booleanValue();
    }
    else if (data instanceof Integer) {
      type = 4;
      state = data.equals(new Integer(0));
    }
  }

  protected int     type  = 1; // 1=Boolean 2=Variant 3=String 4=Integer
  protected boolean state = false;
}

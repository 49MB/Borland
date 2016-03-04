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
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTextField;

import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.ItemFormatter;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.util.JbclUtil;

public class JTextFieldItemEditor extends JTextField implements ItemEditor
{
  private static final long serialVersionUID = 200L;

  public JTextFieldItemEditor(int alignment, ItemFormatter itemFormatter) {
    this.itemFormatter = itemFormatter;
    this.alignment     = alignment;
  }

  public JTextFieldItemEditor(int alignment) {
    this(alignment, null);
  }

  public JTextFieldItemEditor() {
    this(Alignment.LEFT, null);
  }

  // TODO. site should be used.  Grid decides how to terminate the edit interaction now.
  public void startEdit(Object value, Rectangle bounds, ItemEditSite site) {
    String text;
    try {
      if (itemFormatter != null && value instanceof Variant)
        //text = itemFormatter.format((Variant)value);    // TODO <rac> Remove when proven
        text = itemFormatter.format(value);
      else if (value != null)
        text = value.toString();
      else
        text = "";
    }
    catch (InvalidFormatException ex) {
      Diagnostic.printStackTrace(ex);
      text = "";
    }
    setText(text);

    if (site != null) {
      setBackground(site.getBackground());
      setForeground(site.getForeground());
      setFont(site.getFont());
    }

    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    setVisible(true);

    // We are going to try to determine the best place to set the insertion point
    //
    Point clickPoint = site != null ? site.getEditClickPoint() : null;
    int position = 0;
    if (clickPoint == null && text != null) {
      // The editing was initiated without a mouse click. Here we will set the insertion point
      // at the end of the string
      position = text.length();
      select(0, position);
    }
    else if (text != null) {
      // The editing was initiated with a mouse click. Here we will set the insertion point
      // at the click point
      int xClick = clickPoint.x - bounds.x;
      position = JbclUtil.findInsertPoint(xClick, text, clickPoint, getFont());
      select(position, position);
    }
    requestFocus();
  }

  public void changeBounds(Rectangle bounds) {
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public Object getValue() {
    if (itemFormatter != null) {
      //Variant v = new Variant();            // TODO <rac> Remove when proven
      //itemFormatter.parse(getText(), v);
      //return v;
      try {
        return itemFormatter.parse(getText());
      }
      catch (InvalidFormatException x) {
        return getText();
      }
    }
    return getText();
    //return (itemFormatter != null) ? itemFormatter.parse(getText(), null) : getText();
  }

  public Component getComponent() { return this; }

  public boolean canPost() {
    return true;
  }

  public void endEdit(boolean posted) {
  }

  ItemFormatter itemFormatter;
  int           alignment;
}

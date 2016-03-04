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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;

import com.borland.dx.text.ItemFormatter;
import com.borland.jbcl.model.ItemEditSite;

public class ExpandingTextItemEditor extends TextItemEditor implements java.io.Serializable
{
  public ExpandingTextItemEditor(int alignment, ItemFormatter itemFormatter) {
    super(alignment, itemFormatter);
  }

  public ExpandingTextItemEditor(int alignment) {
    super(alignment);
  }

  public ExpandingTextItemEditor() {
    super();
  }

  public int getHMargin() {
    return hMargin;
  }

  public void setHMargin(int hMargin) {
    this.hMargin = hMargin;
  }

  public void startEdit(Object data, Rectangle bounds, ItemEditSite editSite) {
    bounds.width += hMargin;
    startRect = new Rectangle(bounds);
    super.startEdit(data, bounds, editSite);
    resizeComponent();
    // Following code moves text over so that first character is visible
    int ss = getSelectionStart();
    int se = getSelectionEnd();
    int cp = getCaretPosition();
    setCaretPosition(0);
    setCaretPosition(cp);
    setSelectionStart(ss);
    setSelectionEnd(se);
  }

  public void changeBounds(Rectangle bounds) {
    setLocation(bounds.x, bounds.y);
    resizeComponent();
  }

  protected void processKeyEvent(KeyEvent e) {
    super.processKeyEvent(e);
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      resizeComponent();
    }
  }

  private void resizeComponent() {
    Dimension ps = getPreferredSize();
    Dimension sz = getSize();
    if (sz.width != ps.width && ps.width > startRect.width) {
      Dimension newSize = new Dimension(0, sz.height);
      Rectangle hb = getHostBounds();
      newSize.width = (ps.width + hMargin) < (hb.width + hb.x - getLocation().x) ? ps.width + hMargin : hb.width + hb.x - getLocation().x;
      setSize(newSize);
    }
  }

  private Rectangle getHostBounds() {
    Component p = getParent();
    Component pp = p.getParent();
    if (pp instanceof ScrollPane) {
      Point sp = ((ScrollPane)pp).getScrollPosition();
      Dimension vp = ((ScrollPane)pp).getViewportSize();
      return new Rectangle(sp.x, sp.y, vp.width, vp.height);
    }
    else if (pp instanceof JScrollPane) {
      return new Rectangle(((JScrollPane)pp).getViewport().getViewRect());
    }
    Rectangle hb = p.getBounds();
    if (hb == null)
      return new Rectangle();
    hb.x = 0; hb.y = 0; // no offset if not a ScrollPane!
    return hb;
  }

  private int hMargin = 5;
  private Rectangle startRect;
}

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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

//
// Puts a button border that response to state around another item painter
//
public class ButtonItemPainter implements ItemPainter, Serializable
{
  private static final long serialVersionUID = 200L;

  public ButtonItemPainter() {}

  public ButtonItemPainter(ItemPainter painter) {
    this.painter = painter;
  }

  public ButtonItemPainter(ItemPainter painter, boolean showRollover) {
    this.painter = painter;
    this.showRollover = showRollover;
  }

  public ItemPainter getPainter() {
    return painter;
  }
  public void setPainter(ItemPainter v) {
    painter = v;
  }

  public void setShowRollover(boolean showRollover) {
    this.showRollover = showRollover;
  }
  public boolean isShowRollover() {
    return showRollover;
  }

  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    if (painter != null) {
      Dimension painterDim = painter.getPreferredSize(data, g, state, site);
      return new Dimension(painterDim.width+margin+margin, painterDim.height+margin+margin);
    }
    return new Dimension(margin+margin, margin+margin);
  }

  public void paint(Object data, Graphics g, Rectangle rect, int state, ItemPaintSite site) {
    boolean trans = site != null ? site.isTransparent() : false;
    if (!trans) {
      g.setColor(SystemColor.control);
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }
    if (showRollover) {
      if ((state & SELECTED) != 0)
        dnBorder.paint(null, g, rect, 0, site);
      else if ((state & ROLLOVER) != 0)
        upBorder.paint(null, g, rect, 0, site);
    }
    else if ((state & ItemPainter.SELECTED) == 0)
      upBorder.paint(null, g, rect, 0, site);
    else
      dnBorder.paint(null, g, rect, 0, site);

    if (painter != null) {
      Rectangle faceRect;
      if ((state & SELECTED) == 0) {  // button is up
        faceRect = new Rectangle(rect.x+margin, rect.y+margin, rect.width-(margin+margin), rect.height-(margin+margin));
      }
      else {  // button is pushed
        faceRect = new Rectangle(rect.x+margin+1, rect.y+margin+1, rect.width-(margin+margin)-1, rect.height-(margin+margin)-1);
      }
      painter.paint(data, g, faceRect, state, site);
    }
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(painter instanceof Serializable ? painter : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof ItemPainter)
      painter = (ItemPainter)data;
  }

  static final int margin = 2 + 1;  // 2 for border & 1 for margin

  protected transient ItemPainter painter;
  protected boolean showRollover = false;

  protected BorderItemPainter upBorder = new BorderItemPainter(BorderItemPainter.BUTTON_UP, BorderItemPainter.RECT, BorderItemPainter.FILL);
  protected BorderItemPainter dnBorder = new BorderItemPainter(BorderItemPainter.BUTTON_DN, BorderItemPainter.RECT, BorderItemPainter.FILL);
}

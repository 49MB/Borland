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

import java.awt.Color;
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
import com.borland.jbcl.util.DottedLine;

/**
 *
 */
public class FocusableItemPainter implements ItemPainter, Serializable
{
  private static final long serialVersionUID = 200L;

  public FocusableItemPainter() {
    this.painter = null;
  }

  public FocusableItemPainter(ItemPainter painter) {
    this.painter = painter;
  }

  public FocusableItemPainter(ItemPainter painter, boolean showRollover) {
    this.painter = painter;
    this.showRollover = showRollover;
  }

  // properties

  public void setPainter(ItemPainter painter) {
    this.painter = painter;
  }
  public ItemPainter getPainter() {
    return painter;
  }

  public void setShowRollover(boolean showRollover) {
    this.showRollover = showRollover;
  }
  public boolean isShowRollover() {
    return showRollover;
  }

  public void setRolloverColor(Color rollColor) {
    this.rollColor = rollColor;
  }
  public Color getRollColor() {
    return rollColor;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object object, Graphics g, int state, ItemPaintSite site) {
    return painter != null ? painter.getPreferredSize(object, g, state, site) : new Dimension(0,0);
  }

  public void paint(Object object, Graphics g, Rectangle r, int state, ItemPaintSite site) {
    Color c = g.getColor();
    if (painter != null)
      painter.paint(object, g, r, state, site);
    if ((state & FOCUSED) != 0) {
      g.setColor(Color.black);
      g.setXORMode(Color.white);
      DottedLine.drawRect(g, r.x, r.y, r.width, r.height);
      g.setPaintMode();
    }
    else if (showRollover && (state & ROLLOVER) != 0) {
      g.setColor(rollColor);
      g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
    }
    g.setColor(c);
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

  protected transient ItemPainter painter;
  protected boolean showRollover = true;
  protected Color rollColor = SystemColor.textHighlight;
}

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

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

/**
 * A painter that paints the color of the data object passed to it
 */
public class ColorItemPainter implements ItemPainter, java.io.Serializable
{
  public ColorItemPainter(Dimension size) {
    this.size = size;
  }

  public ColorItemPainter() {
    this.size = new Dimension(10,10);
  }

  //  Properties accessors

  public Dimension getSize() {
    return size;
  }

  public void setSize(Dimension s) {
    size = s;
  }

  // ItemPainter interface

  public Dimension getPreferredSize(Object object, Graphics g, int state, ItemPaintSite site) {
    return getSize();
  }

  public void paint(Object object, Graphics g, Rectangle r, int state, ItemPaintSite site) {
    Color color = getColor(object, g, site);
//    Diagnostic.println("ColorItemPainter.getColor()=" + color);
    g.setColor(color);
    g.fillRect(r.x, r.y, r.width, r.height);
  }

  protected Color getColor(Object object, Graphics g, ItemPaintSite site) {
    if (object instanceof Color)
      return (Color)object;
    else if (object instanceof Integer) {
      return new Color(((Integer)object).intValue());
    }
    else if (object instanceof String) {
      try {
        return new Color(Integer.valueOf(((String)object)).intValue());
      }
      catch (NumberFormatException nfe) {
      }
    }
    Color c = site != null ? site.getBackground() : null;
    return c != null ? c : Color.white;
  }

  Dimension size;
}

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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

/**
 * WrappedTextItemPainter - a text item painter which wraps long lines.
 */
public class WrappedTextItemPainter extends TextItemPainter implements java.io.Serializable
{
  public WrappedTextItemPainter() {
    super();
  }

  public WrappedTextItemPainter(int alignment) {
    super(alignment);
  }

  public WrappedTextItemPainter(int alignment, Insets margins) {
    super(alignment, margins);
  }

  public WrappedTextItemPainter(Insets margins, int alignment, int minWidth) {
    super(alignment, margins);
    this.minWidth = minWidth;
  }

  /**
   * The minWidth property defines a minimum width (in pixels) to stop wrapping
   * the text.  If none is set (or -1), the entire rectangle passed in the paint
   * method will be used.
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
  }
  public int getMinWidth() {
    return minWidth;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object object, Graphics g, int state, ItemPaintSite site) {
    Dimension textDim = new TextWrapper(g.getFont(), getText(object), alignment, minWidth == 0 ? 300 : minWidth).getSize(g);
    Insets m = site != null ? site.getItemMargins() : margins;
    if (m == null)
      m = margins;

    return new Dimension(textDim.width+m.left+m.right, textDim.height+m.top+m.bottom);
  }

  public void paint(Object object, Graphics g, Rectangle r, int state, ItemPaintSite site) {
    Font of = g.getFont();
    Color oc = g.getColor();

    Font f = site != null ? site.getFont() : g.getFont();
    if (f == null)
      f = g.getFont();
    g.setFont(f);

    int a = site != null ? site.getAlignment() : getAlignment();
    if (a == 0)
      a = getAlignment();

    Insets m = site != null ? site.getItemMargins() : getMargins();
    if (m == null)
      m = getMargins();

    Color bg = site != null ? site.getBackground() : getBackground();
    if (bg == null)
      bg = getBackground();

    if (paintBackground) {
      g.setColor(bg);
      g.fillRect(r.x, r.y, r.width, r.height);
    }

    Color fg = site != null ? site.getForeground() : getForeground();
    if (fg == null)
      fg = getForeground();

    int width = r.width - m.left - m.right;
    TextWrapper textWrapper = new TextWrapper(f, getText(object), a, minWidth > width ? minWidth : width);
    Dimension textDim = textWrapper.getSize(g);

    if ((state & ItemPainter.DISABLED) != 0) {
      g.setColor(SystemColor.controlLtHighlight);
      textWrapper.paint(g, r.x + m.left + 1, r.y + m.top + 1, r.width - m.left - m.right, r.height - m.top - m.bottom);
      g.setColor(SystemColor.controlShadow);
    }
    else {
      g.setColor(fg);
    }

    textWrapper.paint(g, r.x + m.left, r.y + m.top, r.width - m.left - m.right, r.height - m.top - m.bottom);

    g.setFont(of);
    g.setColor(oc);
  }

  protected int minWidth = 0;
}

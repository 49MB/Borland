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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import com.borland.dx.text.ItemFormatter;
import com.borland.jbcl.model.ItemPaintSite;

/**
 * An ItemPainter that extracts text from the passed data and paints it in the given
 * rectangle.  This ItemPainter also supports: <UL>
 * <LI>foreground, background colors
 * <LI>disabled text printing
 * <LI>transparency
 * <LI>margins around text
 * <LI>full alignment capabilities
 * <LI>text formatting using ItemFormatter interface
 */
public class EllipsisTextItemPainter extends TextItemPainter implements java.io.Serializable
{
  public EllipsisTextItemPainter() {
    super();
  }

  public EllipsisTextItemPainter(int alignment) {
    super(alignment);
  }

  public EllipsisTextItemPainter(int alignment, Insets margins) {
    super(alignment, margins);
  }

  public EllipsisTextItemPainter(int alignment, Insets margins, ItemFormatter formatter) {
    super(alignment, margins, formatter);
  }

  public void paint(Object data, Graphics g, Rectangle r, int state, ItemPaintSite site) {
    Font f = site != null ? site.getFont() : g.getFont();
    if (f == null)
      f = g.getFont();
    Insets m = site != null ? site.getItemMargins() : getMargins();
    if (m == null)
      m = getMargins();
    String text = getText(data);
    FontMetrics fm = g.getFontMetrics(f);

    if ((fm.stringWidth(text) + m.left + m.right) > r.width) {
      String ellipsis = Res._Ellipsis;     
      int dotsWidth = fm.stringWidth(ellipsis);
      while (!"".equals(text) && (fm.stringWidth(text) + dotsWidth + m.left + m.right) > r.width)
        text = text.substring(0, text.length() - 1);
      text += ellipsis;
    }

    super.paint(text, g, r, state, site);
  }
}

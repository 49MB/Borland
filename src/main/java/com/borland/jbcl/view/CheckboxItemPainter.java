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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;

import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

public class CheckboxItemPainter implements ItemPainter, java.io.Serializable
{
  public static final int CHECKMARK = 1;
  public static final int XMARK     = 2;

  public CheckboxItemPainter() {}

  public CheckboxItemPainter(Dimension boxSize) {
    if (boxSize != null)
      this.boxSize = boxSize;
  }

  public CheckboxItemPainter(Dimension boxSize, int checkStyle) {
    this(boxSize);
    this.checkStyle = checkStyle;
  }

  public CheckboxItemPainter(Dimension boxSize, int checkStyle, boolean flat) {
    this(boxSize, checkStyle);
    this.flat = flat;
  }

  public CheckboxItemPainter(Dimension boxSize, int checkStyle, boolean flat, boolean drawBox) {
    this(boxSize, checkStyle, flat);
    this.drawBox = drawBox;
  }

  public void setBoxSize(Dimension boxSize) {
    if (boxSize == null)
      throw new IllegalArgumentException();
    this.boxSize = boxSize;
  }
  public Dimension getBoxSize() {
    return new Dimension(boxSize.width + 2, boxSize.height + 2);
  }

  public void setStyle(int checkStyle) {
    if (checkStyle != CHECKMARK && checkStyle != XMARK)
      throw new IllegalArgumentException();
    this.checkStyle = checkStyle;
  }
  public int getStyle() {
    return checkStyle;
  }

  public void setFlat(boolean flat) {
    this.flat = flat;
  }
  public boolean isFlat() {
    return flat;
  }

  public void setDrawBox(boolean drawBox) {
    this.drawBox = drawBox;
  }
  public boolean isDrawBox() {
    return drawBox;
  }

  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    return new Dimension(boxSize);
  }

  /**
   * A subclass can override this method to define different criteria for the 'checked' state.
   * The default is to analyze the data object and extract a boolean state.
   */
  protected boolean isChecked(Object data, int state, ItemPaintSite site) {
    boolean checked = false;
    if (data instanceof Boolean)
      checked = ((Boolean)data).booleanValue();
    else if (data instanceof Variant) {
      if (((Variant)data).getType() == Variant.BOOLEAN)
        checked = ((Variant)data).getBoolean();
      else
        checked = Boolean.valueOf(data.toString()).booleanValue();
    }
    else if (data instanceof String)
      checked = Boolean.valueOf((String)data).booleanValue();
    else if (data instanceof Integer)
      checked = !data.equals(new Integer(0));
    return checked;
  }

  public void paint(Object data, Graphics g, Rectangle rect, int state, ItemPaintSite site) {
    Color oc = g.getColor();

    Insets m = site != null ? site.getItemMargins() : new Insets(1,1,1,1);
    if (m == null)
      m = new Insets(1,1,1,1);

    Rectangle boxRect = new Rectangle(rect.x + m.left, rect.y + m.top, boxSize.width, boxSize.height);

    int align = site != null ? site.getAlignment() : Alignment.CENTER | Alignment.MIDDLE;
    if (align == 0)
      align = Alignment.CENTER | Alignment.MIDDLE;

    int hAlign = Alignment.HORIZONTAL & align;
    switch (hAlign) {
      case Alignment.CENTER:
        boxRect.x = rect.x + (rect.width - boxSize.width) / 2;
        break;
      case Alignment.RIGHT:
        boxRect.x = rect.x + rect.width - boxSize.width - m.right;
        break;
      case Alignment.HSTRETCH:
        boxRect.width = rect.width - m.left - m.right;
    }
    int vAlign = Alignment.VERTICAL & align;
    switch (vAlign) {
      case Alignment.MIDDLE:
        boxRect.y = rect.y + (rect.height - boxSize.height) / 2;
        break;
      case Alignment.BOTTOM:
        boxRect.y = rect.y + rect.height - boxSize.height - m.bottom;
        break;
      case Alignment.VSTRETCH:
        boxRect.height = rect.height - m.top - m.bottom;
    }

    if (site != null && !site.isTransparent()) {
      g.setColor(site.getBackground());
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    if (drawBox)
      drawBox(g, boxRect.x, boxRect.y, boxRect.width, boxRect.height, state);

    if (isChecked(data, state, site)) {
      if (flat)
        drawCheck(g, boxRect.x + 1, boxRect.y + 1, boxRect.width - 2, boxRect.height - 2, state);
      else
        drawCheck(g, boxRect.x + 2, boxRect.y + 2, boxRect.width - 4, boxRect.height - 4, state);
    }
    g.setColor(oc);
  }

  /**
   * A subclass can override this method to paint the box around the check.
   */
  protected void drawBox(Graphics g, int x, int y, int w, int h, int state) {
    if (flat) {
      g.setColor(((state & ItemPainter.DISABLED) != 0) ? SystemColor.controlShadow : Color.black);
      g.drawRect(x, y, w - 1, h - 1);
    }
    else {
      // use soft edges if on a control colored face
      if (SystemColor.control.equals(g.getColor()))
        border.setFlags(border.SOFT);
      else
        border.setFlags(0);
      border.paint(null, g, new Rectangle(x, y, w, h), 0, null);
    }
    g.setColor(((state & ItemPainter.DISABLED) != 0) ? SystemColor.control : SystemColor.window);
    if (flat)
      g.fillRect(x + 1, y + 1, w - 2, h - 2);
    else
      g.fillRect(x + 2, y + 2, w - 4, h - 4);
  }

  /**
   * A subclass can override this method to paint the check as desired.
   */
  protected void drawCheck(Graphics g, int x, int y, int w, int h, int state) {
    x++; y++;
    g.setColor(((state & ItemPainter.DISABLED) != 0) ? SystemColor.controlShadow : SystemColor.windowText);
    if (checkStyle == CHECKMARK) {
      int thickness = h / 3;
      int xbase = x + w / 3 - 1;
      for (int t = 1; t <= thickness; t++) {
        // short left line
        g.drawLine(xbase, y + h - t - 2, x, y + thickness - t + h / 2 - 2);
        // long right line
        g.drawLine(xbase, y + h - t - 2, x + w - 3, y + thickness - t);
      }
    }
    else if (checkStyle == XMARK) {
      // top-left to bottom-right
      g.drawLine(x, y, x + w - 3, y + h - 3);
      g.drawLine(x + 1, y, x + w - 3, y + h - 4);
      g.drawLine(x, y + 1, x + w - 4, y + h - 3);
      // bottom-left to top-right
      g.drawLine(x, y + h - 3, x + w - 3, y);
      g.drawLine(x + 1, y + h - 3, x + w - 3, y + 1);
      g.drawLine(x, y + h - 4, x + w - 4, y);
    }
  }

  protected BorderItemPainter border = new BorderItemPainter(BorderItemPainter.WND_RECESSED);
  protected Dimension boxSize = new Dimension(13, 13);
  protected int checkStyle = CHECKMARK;
  protected boolean flat = false;
  protected boolean drawBox = true;
}

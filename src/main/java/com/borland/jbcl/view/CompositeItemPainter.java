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
//------------------------------------------------------------------------------
package com.borland.jbcl.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.util.Orientation;
import com.borland.jbcl.util.Pair;

/**
 *
 */
public class CompositeItemPainter implements ItemPainter, Serializable
{
  private static final long serialVersionUID = 200L;

  public CompositeItemPainter() {}

  public CompositeItemPainter(ItemPainter firstPainter, ItemPainter secondPainter) {
    this(firstPainter, secondPainter, Orientation.HORIZONTAL, Alignment.CENTER | Alignment.MIDDLE, 4);
  }

  public CompositeItemPainter(ItemPainter firstPainter, ItemPainter secondPainter, int orientation) {
    this(firstPainter, secondPainter, orientation, Alignment.CENTER | Alignment.MIDDLE, 4);
  }

  public CompositeItemPainter(ItemPainter firstPainter, ItemPainter secondPainter, int orientation, int alignment) {
    this(firstPainter, secondPainter, orientation, alignment, 4);
  }

  public CompositeItemPainter(ItemPainter firstPainter, ItemPainter secondPainter, int orientation, int alignment, int gap) {
    this.firstPainter = firstPainter;
    this.secondPainter = secondPainter;
    this.orientation = orientation;
    this.alignment = alignment;
    this.gap = gap;
  }

  public void setPainter1(ItemPainter firstPainter) {
    this.firstPainter = firstPainter;
  }
  public ItemPainter getPainter1() {
    return firstPainter;
  }

  public void setPainter2(ItemPainter secondPainter) {
    this.secondPainter = secondPainter;
  }
  public ItemPainter getPainter2() {
    return secondPainter;
  }

  /**
   * Margins property defines the margins for the CompositeItemPainter.
   * @see java.awt.Insets
   */
  public void setMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getMargins() {
    return margins;
  }

  public void setOrientation(int o) {
    orientation = o;
  }
  public int getOrientation() {
    return orientation;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  public int getAlignment() {
    return alignment;
  }

  public void setGap(int gap) {
    this.gap = gap;
  }
  public int getGap() {
    return gap;
  }

  /**
   * Background property defines the default background color for the TextItemPainter.  If the
   * ItemPaintSite passed into the getPreferredSize or paint methods has a valid background color
   * setting, it will override this one.
   * @see java.awt.Color
   */
  public void setBackground(Color background) {
    this.background = background;
  }
  public Color getBackground() {
    return background;
  }

  /**
   * the paintBackground property defines wether or not the background will be filled before
   * drawing the text in the paint method.
   */
  public void setPaintBackground(boolean paintBackground) {
    this.paintBackground = paintBackground;
  }
  public boolean isPaintBackground() {
    return paintBackground;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object object, Graphics g, int state, ItemPaintSite site) {
    delegate1.ips = site;
    delegate2.ips = site;
    Dimension dimFirst;
    Dimension dimSecond;
    if (object instanceof Pair) {
      dimFirst = firstPainter.getPreferredSize(((Pair)object).first, g, state, delegate1);
      dimSecond = secondPainter.getPreferredSize(((Pair)object).second, g, state, delegate2);
    }
    else {
      dimFirst = firstPainter.getPreferredSize(object, g, state, delegate1);
      dimSecond = secondPainter.getPreferredSize(object, g, state, delegate2);
    }
    if (orientation == Orientation.HORIZONTAL)
      return new Dimension(dimFirst.width + gap + dimSecond.width + margins.left + margins.right, Math.max(dimFirst.height, dimSecond.height) + margins.top + margins.bottom);
    else
      return new Dimension(Math.max(dimFirst.width, dimSecond.width) + margins.left + margins.right, dimFirst.height + gap + dimSecond.height + margins.top + margins.bottom);
  }

  public void paint(Object object, Graphics g, Rectangle r, int state, ItemPaintSite site) {
    delegate1.ips = site;
    delegate2.ips = site;
    Rectangle rectFirst = new Rectangle();
    Rectangle rectSecond = new Rectangle();
    calculateRects(object, g, r, state, site, rectFirst, rectSecond);

    boolean trans = site != null ? site.isTransparent() : !paintBackground;

    if (!trans) {
      Color oc = g.getColor();
      Color bg = site != null ? site.getBackground() : getBackground();
      if (bg == null)
        bg = getBackground();
      if (bg == null)
        bg = g.getColor();
      g.setColor(bg);
      g.fillRect(r.x, r.y, r.width, r.height);
      g.setColor(oc);
    }

    if (object instanceof Pair) {
      firstPainter.paint(((Pair)object).first, g, rectFirst, state, delegate1);
      secondPainter.paint(((Pair)object).second, g, rectSecond, state, delegate2);
    }
    else {
      firstPainter.paint(object, g, rectFirst, state, delegate1);
      secondPainter.paint(object, g, rectSecond, state, delegate2);
    }
  }

  /**
   * Uses the passed paint information (same parameters to paint) to calculate
   * the ItemPainter rectangles, and fills in the passed rectangles' values.
   * @param object The data object to use for calculations.
   * @param graphics The Graphics object to use for calculations.
   * @param rect The bounding Rectangle to use for calculations.
   * @param state The current view state of the item.
   * @param rectFirst The calculated Rectangle of the first ItemPainter (filled in).
   * @param rectSecond The calculated Rectangle of the second ItemPainter (filled in).
   */
  public void calculateRects(Object object, Graphics graphics, Rectangle rect, int state, ItemPaintSite site,
                             Rectangle rectFirst, Rectangle rectSecond) {
    if (rectFirst == null || rectSecond == null)
      throw new IllegalArgumentException(Res._NullRectangles);     
    delegate1.ips = site;
    delegate2.ips = site;
    Dimension dimFirst;
    Dimension dimSecond;
    if (object instanceof Pair) {
      dimFirst = firstPainter.getPreferredSize(((Pair)object).first, graphics, state, delegate1);
      dimSecond = secondPainter.getPreferredSize(((Pair)object).second, graphics, state, delegate2);
    }
    else {
      dimFirst = firstPainter.getPreferredSize(object, graphics, state, delegate1);
      dimSecond = secondPainter.getPreferredSize(object, graphics, state, delegate2);
    }

    int a = site != null ? site.getAlignment() : alignment;
    if (a == 0)
      a = alignment;

    int fullWidth = orientation == Orientation.VERTICAL
                    ? Math.max(dimFirst.width, dimSecond.width)
                    : dimFirst.width + dimSecond.width + gap + margins.left + margins.right;
    int fullHeight = orientation == Orientation.HORIZONTAL
                     ? Math.max(dimFirst.height, dimSecond.height)
                     : dimFirst.height + dimSecond.height + gap + margins.top + margins.bottom;

    int xOffset;
    int hAlign = (a & Alignment.HORIZONTAL);
    switch (hAlign) {
      default:
      case Alignment.LEFT:
      case Alignment.HSTRETCH:
        xOffset = margins.left;
        break;
      case Alignment.CENTER:
        xOffset = (rect.width - fullWidth) / 2;
        break;
      case Alignment.RIGHT:
        xOffset = rect.width - fullWidth - margins.right;
    }
    int yOffset;
    int vAlign = (a & Alignment.VERTICAL);
    switch (vAlign) {
      default:
      case Alignment.TOP:
      case Alignment.VSTRETCH:
        yOffset = margins.top;
        break;
      case Alignment.MIDDLE:
        yOffset = (rect.height - fullHeight) / 2;
        break;
      case Alignment.BOTTOM:
        yOffset = rect.height - fullHeight - margins.bottom;
        break;
    }

    if (orientation == Orientation.HORIZONTAL) {
      rectFirst.x = rect.x;
      rectFirst.y = rect.y + (rect.height - dimFirst.height) / 2;
      rectFirst.width = dimFirst.width;
      rectFirst.height = dimFirst.height;
      rectSecond.x = rect.x + dimFirst.width + gap;
      rectSecond.y = rect.y + (rect.height - dimSecond.height) / 2;
      rectSecond.width = dimSecond.width;
      rectSecond.height = dimSecond.height;
    }
    else {
      rectFirst.x = rect.x + (rect.width-dimFirst.width) / 2;
      rectFirst.y = rect.y;
      rectFirst.width = dimFirst.width;
      rectFirst.height = dimFirst.height;
      rectSecond.x = rect.x + (rect.width-dimSecond.width) / 2;
      rectSecond.y = rect.y + dimFirst.height + gap;
      rectSecond.width = dimSecond.width;
      rectSecond.height = dimSecond.height;
    }

    rectFirst.x += xOffset;
    rectFirst.y += yOffset;
    rectSecond.x += xOffset;
    rectSecond.y += yOffset;

    // now clean up the rectangles to use up the passed space...

    Rectangle r1 = new Rectangle(rectFirst);
    Rectangle r2 = new Rectangle(rectSecond);
    if (orientation == Orientation.HORIZONTAL) {
      rectFirst.x = rect.x;
      rectFirst.width = r1.x + r1.width - rect.x;
      rectSecond.width = rect.x + rect.width - r2.x;
      rectFirst.y = rect.y;
      rectFirst.height = rect.height;
      rectSecond.y = rect.y;
      rectSecond.height = rect.height;
    }
    else {
      rectFirst.y = rect.y;
      rectFirst.height = r1.y + r1.height - rect.y;
      rectSecond.height = rect.y + rect.height - r2.y;
      rectFirst.x = rect.x;
      rectFirst.width = rect.width;
      rectSecond.x = rect.x;
      rectSecond.width = rect.width;
    }
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (firstPainter instanceof Serializable)
      hash.put("1", firstPainter); 
    if (secondPainter instanceof Serializable)
      hash.put("2", secondPainter); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("1"); 
    if (data instanceof ItemPainter)
      firstPainter = (ItemPainter)data;
    data = hash.get("2"); 
    if (data instanceof ItemPainter)
      secondPainter = (ItemPainter)data;
  }

  protected transient ItemPainter firstPainter;
  protected transient ItemPainter secondPainter;

  protected Insets margins = new Insets(1,1,1,1);
  protected int orientation = Orientation.HORIZONTAL;
  protected int alignment = Alignment.CENTER | Alignment.MIDDLE;
  protected int gap;
  protected Color background;
  protected boolean paintBackground = true;

  ItemPaintSiteDelegate delegate1 = new ItemPaintSiteDelegate() {
    public int getAlignment() {
      int align = ips != null ? ips.getAlignment() : alignment;
      int hAlign = (align & Alignment.HORIZONTAL);
      int vAlign = (align & Alignment.VERTICAL);
      if (orientation == Orientation.HORIZONTAL) {
        if (hAlign == Alignment.CENTER)
          hAlign = Alignment.RIGHT;
      }
      else {
        if (vAlign == Alignment.MIDDLE)
          vAlign = Alignment.BOTTOM;
      }
      return hAlign | vAlign;
    }
  };

  ItemPaintSiteDelegate delegate2 = new ItemPaintSiteDelegate() {
    public int getAlignment() {
      int align = ips != null ? ips.getAlignment() : alignment;
      int hAlign = (align & Alignment.HORIZONTAL);
      int vAlign = (align & Alignment.VERTICAL);
      if (orientation == Orientation.HORIZONTAL) {
        if (hAlign == Alignment.CENTER)
          hAlign = Alignment.LEFT;
      }
      else {
        if (vAlign == Alignment.MIDDLE)
          vAlign = Alignment.TOP;
      }
      return hAlign | vAlign;
    }
  };

  abstract class ItemPaintSiteDelegate implements ItemPaintSite {
    public Color getBackground() { return ips != null ? ips.getBackground() : null; }
    public Color getForeground() { return ips != null ? ips.getForeground() : null; }
    public boolean isTransparent() { return ips != null ? ips.isTransparent() : false; }
    public Font getFont() { return ips != null ? ips.getFont() : null; }
    public Insets getItemMargins() { return ips != null ? ips.getItemMargins() : getMargins(); }
    public Component getSiteComponent() { return ips != null ? ips.getSiteComponent() : null; }
    public transient ItemPaintSite ips;
  }
}

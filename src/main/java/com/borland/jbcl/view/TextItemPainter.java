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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;

import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.ItemFormatter;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

/**
 * An ItemPainter that extracts text from the passed data and paints it in the given
 * rectangle.  This ItemPainter also supports: <UL>
 * <LI>foreground, background colors
 * <LI>disabled text printing
 * <LI>transparency
 * <LI>margins around text
 * <LI>full alignment capabilities
 * <LI>text formatting using ItemFormatter interface
 * </UL>
 */
public class TextItemPainter implements ItemPainter, java.io.Serializable
{
  private static final long serialVersionUID = 200L;

  public TextItemPainter() {}

  public TextItemPainter(int alignment) {
    this();
    setAlignment(alignment);
  }

  public TextItemPainter(Insets margins) {
    this();
    setMargins(margins);
  }

  public TextItemPainter(ItemFormatter formatter) {
    this();
    setFormatter(formatter);
  }

  public TextItemPainter(int alignment, Insets margins) {
    this();
    setAlignment(alignment);
    setMargins(margins);
  }

  public TextItemPainter(int alignment, Insets margins, ItemFormatter formatter) {
    this();
    setAlignment(alignment);
    setMargins(margins);
    setFormatter(formatter);
  }

  //  Properties

  /**
   * Alignment property defines the default alignment for the TextItemPainter.  If the ItemPaintSite
   * passed into the getPreferredSize or paint methods has a valid alignment setting, it will over-
   * ride this one.
   * @see com.borland.jbcl.util.Alignment for valid alignment values.
   */
  public void setAlignment(int a) {
    if ((alignment & Alignment.VERTICAL) < 0 || (alignment & Alignment.VERTICAL) > Alignment.BOTTOM)
      throw new IllegalArgumentException(java.text.MessageFormat.format(Res._BadVAlignment,     
        new Object[] {new Integer(alignment & Alignment.VERTICAL)} ));
    if ((alignment & Alignment.HORIZONTAL) < 0 || (alignment & Alignment.HORIZONTAL) > Alignment.RIGHT)
      throw new IllegalArgumentException(java.text.MessageFormat.format(Res._BadHAlignment,     
        new Object[] {new Integer(alignment & Alignment.HORIZONTAL)} ));
    alignment = a;
  }
  public int getAlignment() {
    return alignment;
  }

  /**
   * Margins property defines the default margins for the TextItemPainter.  If the ItemPaintSite
   * passed into the getPreferredSize or paint methods has a valid margins setting, it will over-
   * ride this one.
   * @see java.awt.Insets
   */
  public void setMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getMargins() {
    return margins;
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

  /**
   * Foreground property defines the default foreground color for the TextItemPainter.  If the
   * ItemPaintSite passed into the getPreferredSize or paint methods has a valid foreground color
   * setting, it will override this one.
   * @see java.awt.Color
   */
  public void setForeground(Color c) {
    foreground = c;
  }
  public Color getForeground() {
    return foreground;
  }

  /**
   * Formatter property defines the ItemFormatter to use to format the text passed in the
   * getPreferredSize and paint methods.
   * @see com.borland.jbcl.model.ItemFormatter for more info.
   */
  public void setFormatter(ItemFormatter formatter) {
    this.formatter = formatter;
  }
  public ItemFormatter getFormatter() {
    return formatter;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    if (g == null)            
      return new Dimension(); 
    Font of = g.getFont();
    Font f = site != null ? site.getFont() : of;
    if (f == null)
      f = of;
    if (f != null)
      g.setFont(f);
    FontMetrics fm = g.getFontMetrics(f);
    String text = getText(data);
    Insets m = site != null ? site.getItemMargins() : margins;
    g.setFont(of);
    return new Dimension(m.left + m.right + fm.stringWidth(text),
                         m.top + m.bottom + fm.getHeight());
  }

  public void paint(Object data, Graphics g, Rectangle r, int state, ItemPaintSite site) {
    Font of = g.getFont();
    Color oc = g.getColor();

    Font f = site != null ? site.getFont() : g.getFont();
    if (f == null)
      f = g.getFont();
    g.setFont(f);

    Insets m = site != null ? site.getItemMargins() : getMargins();
    if (m == null)
      m = getMargins();

    int a = site != null ? site.getAlignment() : getAlignment();
    if (a == 0)
      a = getAlignment();

    Color bg = site != null ? site.getBackground() : getBackground();
    if (bg == null)
      bg = getBackground();
    if (bg == null)
      bg = g.getColor();

    Color fg = site != null ? site.getForeground() : getForeground();
    if (fg == null)
      fg = getForeground();
    if (fg == null)
      fg = SystemColor.windowText;

    boolean trans = site != null ? site.isTransparent() : !paintBackground;

    String text = getText(data);
    FontMetrics fm = g.getFontMetrics(f);

    int xOffset;
    switch (a & Alignment.HORIZONTAL) {
      default:
      case Alignment.LEFT:
        xOffset = m.left;
        break;
      case Alignment.CENTER:
        xOffset = (r.width - fm.stringWidth(text)) / 2;
        break;
      case Alignment.RIGHT:
        xOffset = r.width - fm.stringWidth(text) - m.right;
    }

    int yOffset;
    switch (a & Alignment.VERTICAL) {
      default:
      case Alignment.TOP:
        yOffset = m.top;
        break;
      case Alignment.MIDDLE:
        yOffset = (r.height - fm.getHeight()) / 2;
        break;
      case Alignment.BOTTOM:
        yOffset = r.height - m.bottom - fm.getHeight();
        break;
    }
    yOffset += fm.getLeading() + fm.getAscent();

    if (!trans) {
      g.setColor(bg);
      g.fillRect(r.x, r.y, r.width, r.height);
    }

    if (text != null) {
      if ((state & ItemPainter.DISABLED) != 0) {
        g.setColor(SystemColor.controlLtHighlight);
        g.drawString(text, r.x + xOffset + 1, r.y + yOffset + 1);
        g.setColor(SystemColor.controlShadow);
      }
      else {
        g.setColor(fg);
      }
      g.drawString(text, r.x + xOffset, r.y + yOffset);
    }

    // revert back to old font and color settings
    g.setFont(of);
    g.setColor(oc);
  }

  protected String getText(Object data) {
    if (data != null) {
      if (formatter != null && data instanceof Variant) {
        try {
          return formatter.format(data);
        }
        catch (InvalidFormatException ex) {
          Diagnostic.printStackTrace(ex);
          return "";
        }
      }
      else
        return data.toString();
    }
    else
      return "";
  }

  protected int           alignment = Alignment.LEFT | Alignment.MIDDLE;
  protected Insets        margins = new Insets(1,1,1,1);
  protected Color         foreground = SystemColor.controlText;
  protected Color         background;
  protected boolean       paintBackground = true;
  protected ItemFormatter formatter;
}

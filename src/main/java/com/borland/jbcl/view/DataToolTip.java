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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.UIManager;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

public class DataToolTip extends JToolTip implements ItemPaintSite, java.io.Serializable
{
  public DataToolTip() {}

  public DataToolTip(JComponent c) {
    super();
    super.setComponent(c);
    if (c instanceof ItemPaintSite)
      site = (ItemPaintSite)c;
  }

  public void paintComponent(Graphics g) {
    if (active) {
      Dimension sz = getSize();
      if (painter != null) {
        state &= ~ItemPainter.FOCUSED;
        state &= ~ItemPainter.ROLLOVER;
        painter.paint(data, g, new Rectangle(1, 1, sz.width - 2, sz.height - 2), state, this);
        g.setColor(SystemColor.control);
        g.drawRect(0, 0, sz.width - 1, sz.height - 1);
      }
      else {
        super.paintComponent(g);
      }
    }
    else
      super.paintComponent(g);
  }

  public Dimension getPreferredSize() {
    if (active) {
      Graphics g = getComponent().getGraphics();
      if (g != null && painter != null) {
        Dimension sz = painter.getPreferredSize(data, g, state, this);
        return new Dimension(sz.width + 2, sz.height + 2);
      }
    }
    return super.getPreferredSize();
  }

  // ItemPaintSite implementation

  public Color getBackground() {
    if (active && site != null && !site.isTransparent())
      return site.getBackground();
    return UIManager.getColor("ToolTip.background"); 
  }

  public Color getForeground() {
    if (active && site != null)
      return site.getForeground();
    return UIManager.getColor("ToolTip.foreground"); 
  }

  public boolean isTransparent() { return false; }

  public Font getFont() {
    if (active && site != null)
      return site.getFont();
    return UIManager.getFont("ToolTip.font"); 
  }

  public int getAlignment() {
    return site != null ? site.getAlignment() : Alignment.LEFT | Alignment.MIDDLE;
  }

  public Insets getItemMargins() {
    return site != null ? site.getItemMargins() : new Insets(2, 2, 2, 2);
  }

  public Component getSiteComponent() {
    return site != null ? site.getSiteComponent() : this;
  }

  public transient Object        data;
  public transient ItemPainter   painter;
  public transient int           state;
  public transient ItemPaintSite site;
  public transient boolean       active;
}

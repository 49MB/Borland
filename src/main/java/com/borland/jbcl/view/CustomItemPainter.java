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
//-------------------------------------------------------------------------------------------------
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

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

public class CustomItemPainter
  implements ItemPainter, ItemPaintSite, CustomPaintSite, Serializable
{
  private static final long serialVersionUID = 200L;

  public CustomItemPainter() {}
  public CustomItemPainter(ItemPainter painter) {
    this.painter = painter;
  }

  /**
   * Nested ItemPainter chain.
   */
  public void setPainter(ItemPainter painter) {
    this.painter = painter;
  }
  public ItemPainter getPainter() {
    return painter;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object data, Graphics graphics, int state, ItemPaintSite site) {
    this.paintSite = site;
    return (painter != null) ? painter.getPreferredSize(data, graphics, state, this) : null;
  }

  public void paint(Object data, Graphics graphics, Rectangle rect, int state, ItemPaintSite site) {
    this.paintSite = site;
    if (painter != null)
      painter.paint(data, graphics, rect, state, this);
  }

  //  CustomPaintSite Implementation

  public void reset() {
    background = null;
    foreground = null;
    font = null;
    alignment = 0;
    margins = null;
  }

  public void setBackground(Color color) {
    this.background = color;
  }
  public Color getBackground() {
    if (background != null)
      return background;
    return paintSite != null ? paintSite.getBackground() : null;
  }

  public void setForeground(Color color) {
    this.foreground = color;
  }
  public Color getForeground() {
    if (foreground != null)
      return foreground;
    return paintSite != null ? paintSite.getForeground() : null;
  }

  // no override
  public boolean isTransparent() {
    return paintSite != null ? paintSite.isTransparent() : false;
  }

  public void setFont(Font font) {
    this.font = font;
  }
  public Font getFont() {
    if (font != null)
      return font;
    return paintSite != null ? paintSite.getFont() : null;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  public int getAlignment() {
    if (alignment != 0)
      return alignment;
    return paintSite != null ? paintSite.getAlignment() : 0;
  }

  public void setItemMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getItemMargins() {
    if (margins != null)
      return margins;
    return paintSite != null ? paintSite.getItemMargins() : null;
  }

  // no override
  public Component getSiteComponent() {
    return paintSite != null ? paintSite.getSiteComponent() : null;
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

  protected transient ItemPainter   painter;
  protected transient ItemPaintSite paintSite; // ignore for serialization
  protected Color background;
  protected Color foreground;
  protected Font font;
  protected int alignment;
  protected Insets margins;
}

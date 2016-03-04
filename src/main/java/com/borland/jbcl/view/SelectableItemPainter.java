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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

/**
 * An ItemPainter that fills the passed rectangle with background/foreground colors
 * based on state information - and passes the paint calls on to its contained ItemPainter.
 * This ItemPainter also overrides the background and foreground properties of the ItemPaintSite
 * thus allowing the nested ItemPainter to use the correct selection colors.
 */
public class SelectableItemPainter implements ItemPainter, ItemPaintSite, Serializable
{
  private static final long serialVersionUID = 200L;

  /**
   * Constructs a SelectableItemPainter using the default settings.
   */
  public SelectableItemPainter() {}

  /**
   * Constructs a SelectableItemPainter with a nested ItemPainter.
   * @param painter The nested ItemPainter.
   */
  public SelectableItemPainter(ItemPainter painter) {
    this.painter = painter;
  }

  /**
   * Constructs a SelectableItemPainter with a nested ItemPainter.
   * @param painter The nested ItemPainter.
   * @param drawBackground True to paint the background before passing paint calls onto the
   *        nested ItemPainter (default is false)
   */
  public SelectableItemPainter(ItemPainter painter, boolean paintBackground) {
    this.painter = painter;
    this.paintBackground = paintBackground;
  }

  // Properties

  /**
   * The painter property defines the nested ItemPainter(s) that will receive all ItemPainter
   * method calls after the SelectableItemPainter has drawn the appropriate background.
   */
  public void setPainter(ItemPainter painter) {
    this.painter = painter;
  }
  public ItemPainter getPainter() {
    return painter;
  }


  /**
   * The paintBackground property defines wether or not the background will be filled before
   * passing the paint method call on to the nested ItemPainter.  By default, this property
   * is false, and the proper colors are passed via the ItemPaintSite interface to the nested
   * ItemPainter.  If set to true, this ItemPainter will paint the background, then pass the
   * paint call to the nested ItemPainter (which may or may not also paint the background).
   */
  public void setPaintBackground(boolean paintBackground) {
    this.paintBackground = paintBackground;
  }
  public boolean isPaintBackground() {
    return paintBackground;
  }

  /**
   * The selectedBackground property defines the color to paint the background when the
   * item is selected and its window is active.
   */
  public void setSelectedBackground(Color c) {
    selectedBg = c;
  }
  public Color getSelectedBackground() {
    return selectedBg;
  }

  /**
   * The selectedForeground property defines the color to paint the text when the item
   * is selected and its window is active.
   */
  public void setSelectedForeground(Color c) {
    selectedFg = c;
  }
  public Color getSelectedForeground() {
    return selectedFg;
  }

  /**
   * The inactiveSelectedBackground property defines the color to paint the background when
   * the item is selected and its window is inactive.
   */
  public void setInactiveSelectedBackground(Color c) {
    inactiveSelectedBg = c;
  }
  public Color getInactiveSelectedBackground() {
    return inactiveSelectedBg;
  }

  /**
   * The inactiveSelectedForeground property defines the color to paint the text when
   * the item is selected and its window is inactive.
   */
  public void setInactiveSelectedForeground(Color c) {
    inactiveSelectedFg = c;
  }
  public Color getInactiveSelectedForeground() {
    return inactiveSelectedFg;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    this.site = site;
    this.state = state;
    if (painter != null)
      return painter.getPreferredSize(data, g, state, this);
    else
      return new Dimension();
  }

  public void paint(Object data, Graphics g, Rectangle rect, int state, ItemPaintSite site) {
    this.site = site;
    this.state = state;
    if (paintBackground) {
      Color oc = g.getColor();
      g.setColor(getBackground());
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setColor(oc);
    }
    if (painter != null)
      painter.paint(data, g, rect, state, this);
  }

  // ItemPaintSite Implementation

  public Color getBackground() {
    return (state & SELECTED) != 0
             ? (state & INACTIVE) != 0
               ? inactiveSelectedBg
               : selectedBg
             : site != null ? site.getBackground() : null;
  }

  public Color getForeground() {
    return (state & SELECTED) != 0
             ? (state & INACTIVE) != 0
               ? inactiveSelectedFg
               : selectedFg
             : site != null ? site.getForeground() : null;
  }

  public boolean isTransparent() {
    return ((state & SELECTED) != 0) ? false : site != null ? site.isTransparent() : false;
  }

  public Font getFont() {
    return site != null ? site.getFont() : null;
  }

  public int getAlignment() {
    return site != null ? site.getAlignment() : 0;
  }

  public Insets getItemMargins() {
    return site != null ? site.getItemMargins() : null;
  }

  public Component getSiteComponent() {
    return site != null ? site.getSiteComponent() : null;
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

  // Fields

  protected transient ItemPainter painter;
  protected transient ItemPaintSite site; // ignore for serialization
  protected int state;
  protected boolean paintBackground  = false;
  protected Color selectedFg         = SystemColor.textHighlightText;
  protected Color selectedBg         = SystemColor.textHighlight;
  protected Color inactiveSelectedFg = SystemColor.menuText;
  protected Color inactiveSelectedBg = SystemColor.menu;
}

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
package com.borland.jbcl.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

/**
 * The ItemPaintSite interface is implemented where ItemPainters can be provided
 * with more information about their host containers fonts, margins, colors, etc.<P>
 *
 * An ItemPaintSite interface (implemented by a component) is passed to the
 * ItemPainter.paint(...) and ItemPainter.getPreferredSize(...) methods.  The
 * ItemPainter is expected to use the settings from the ItemPaintSite to do its
 * painting and size calculations.
 *
 * @see ItemPainter
 */
public interface ItemPaintSite
{
  /**
   * The background color for the item being painted.
   * @return A java.awt.Color object representing the background color.
   */
  public Color getBackground();

  /**
   * The foreground color for the item being painted.
   * @return A java.awt.Color object representing the foreground color.
   */
  public Color getForeground();

  /**
   * Whether or not the ItemPainter should erase its background.
   * @return true if transparent, false if not.
   */
  public boolean isTransparent();

  /**
   * The font to use for the item being painted.
   * @return A java.awt.Font object representing the font to use.
   */
  public Font getFont();

  /**
   * The alignment setting for the item being painted.
   * @see com.borland.util.Alignment for alignment settings.
   * @return An int representing the alignment bitmask.
   */
  public int getAlignment();

  /**
   * The item margins for the item being painted.
   * @return An Insets object representing the margins for this item.
   */
  public Insets getItemMargins();

  /**
   * Returns the component representing the ItemPaintSite.  This is used
   * for coordinate space calculations, as well as to provide a component for
   * ItemPainter implementations that require one - like ImageItemPainter, which
   * requires an ImageObserver object.
   * @return The hosting site component.
   */
  public Component getSiteComponent();
}

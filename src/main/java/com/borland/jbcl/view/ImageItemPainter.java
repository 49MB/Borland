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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.io.IOException;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.util.ImageLoader;

public class ImageItemPainter implements ItemPainter, java.io.Serializable
{
  public ImageItemPainter() {
  }

  public ImageItemPainter(Component component) {
    this(component, Alignment.VSTRETCH | Alignment.HSTRETCH);
  }

  public ImageItemPainter(Component component, int alignment) {
    this.component = component;
    this.alignment = alignment;
  }

  public ImageItemPainter(Component component, int alignment, boolean paintBackground) {
    this.component = component;
    this.alignment = alignment;
    this.paintBackground = paintBackground;
  }

  // Properties

  /**
   * Alignment property defines the default alignment for the TextItemPainter.  If the ItemPaintSite
   * passed into the getPreferredSize or paint methods has a valid alignment setting, it will over-
   * ride this one.
   * @see com.borland.jbcl.util.Alignment for valid alignment values.
   */
  public void setAlignment(int alignment) {
    if ((alignment & Alignment.VERTICAL) < 0 || (alignment & Alignment.VERTICAL) > Alignment.VSTRETCH)
      throw new IllegalArgumentException(java.text.MessageFormat.format(Res._BadVAlignment,     
        new Object[] {new Integer(alignment & Alignment.VERTICAL)} ));
    if ((alignment & Alignment.HORIZONTAL) < 0 || (alignment & Alignment.HORIZONTAL) > Alignment.HSTRETCH)
      throw new IllegalArgumentException(java.text.MessageFormat.format(Res._BadHAlignment,     
        new Object[] {new Integer(alignment & Alignment.HORIZONTAL)}));
    this.alignment = alignment;
  }
  public int getAlignment() {
    return alignment;
  }

  /**
   * the paintBackground property defines whether or not the background will be filled before
   * drawing the image in the paint method.
   */
  public void setPaintBackground(boolean paintBackground) {
    this.paintBackground = paintBackground;
  }
  public boolean isPaintBackground() {
    return paintBackground;
  }

  /**
   * the genDisabledImage property defines whether or not a disabled image will be automatically
   * generated when the ItemPainter.DISABLED state is set.
   */
  public void setGenDisabledImage(boolean genDisabledImage) {
    this.genDisabledImage = genDisabledImage;
  }
  public boolean isGenDisabledImage() {
    return genDisabledImage;
  }

  // ItemPainter Implementation

  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    if (site != null && site.getSiteComponent() != null)
      component = site.getSiteComponent();
    Image image = getImage(data, state);
    return image == null
             ? new Dimension()
             : new Dimension(image.getWidth(component), image.getHeight(component));
  }

  public void paint(Object data, Graphics g, Rectangle rect, int state, ItemPaintSite site) {
    if (site != null && site.getSiteComponent() != null)
      component = site.getSiteComponent();

    Image image = getImage(data, state);
    Color oc = g.getColor();

    Color bg = site != null ? site.getBackground() : g.getColor();
    if (bg == null)
      bg = g.getColor();

    g.setColor(bg);

    boolean trans = site != null ? site.isTransparent() : !paintBackground;

    if (!trans)
      g.fillRect(rect.x, rect.y, rect.width, rect.height);

    if (streamResetError) {
      g.setColor(SystemColor.windowText);
      g.drawString(Res._CantResetImageStream, rect.x, rect.y);     
    }
    else if (image != null) {
      int imageWidth  = image.getWidth(component);
      int imageHeight = image.getHeight(component);

      int a = site != null ? site.getAlignment() : alignment;
      if (a == 0)
        a = alignment;

      int x;
      int width;
      switch (a & Alignment.HORIZONTAL) {
        default:
        case Alignment.LEFT:
          x = rect.x;
          width = imageWidth;
          break;
        case Alignment.CENTER:
          x = rect.x + (rect.width - imageWidth)/2;
          width = imageWidth;
          break;
        case Alignment.RIGHT:
          x = rect.x + rect.width - imageWidth;
          width = imageWidth;
          break;
        case Alignment.HSTRETCH:
          x = rect.x;
          width = rect.width;
          break;
      }

      int y;
      int height;
      switch (a & Alignment.VERTICAL) {
        default:
        case Alignment.TOP:
          y = rect.y;
          height = imageHeight;
          break;
        case Alignment.MIDDLE:
          y = rect.y + (rect.height - imageHeight)/2;
          height = imageHeight;
          break;
        case Alignment.BOTTOM:
          y = rect.y + rect.height - imageHeight;
          height = imageHeight;
          break;
        case Alignment.VSTRETCH:
          y = rect.y;
          height = rect.height;
      }
      g.drawImage(image, x, y, width, height, component);
    }
    g.setColor(oc);
  }

  // Internal methods

  protected Image getImage(Object data, int state) {
    if (data == null)
      return null;

    Image image;
    streamResetError = false;
    if (data instanceof Image)
      image = (Image)data;
    else {
      try {
        image = ImageLoader.loadFromBlob(data, component, true);
      }
      catch (IOException ex) {
        streamResetError = true;
        image = null;
      }
    }

    if (image == null) {
      // TODO.  Report error?  May not be a image file type like gif.
      return null;
    }
    if (genDisabledImage && ((state & ItemPainter.DISABLED) != 0))
      return ImageLoader.getDisabledImage(component, image);
    return image;
  }

  protected Component component;
  protected int       alignment;
  protected boolean   streamResetError;
  protected boolean   paintBackground = true;
  protected boolean   genDisabledImage = true;
}

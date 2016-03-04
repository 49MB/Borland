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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.util.SerializableImage;

public class ImageArrayItemPainter extends ImageItemPainter implements
    Serializable {
  private static final long serialVersionUID = 200L;
  
  public ImageArrayItemPainter() {
    super();
    images = new Image[0];
  }
  
  public ImageArrayItemPainter(Image[] images) {
    super();
    this.images = images;
    for (imageCount = 0; imageCount < images.length
        && images[imageCount] != null; imageCount++)
      ;
  }
  
  public ImageArrayItemPainter(Component component, int alignment,
      Image[] images) {
    super(component, alignment);
    this.images = images;
    for (imageCount = 0; imageCount < images.length
        && images[imageCount] != null; imageCount++)
      ;
  }
  
  public int add(Image image) {
    assureSpace(imageCount + 1);
    images[imageCount] = image;
    return imageCount++;
  }
  
  public int insert(Image image, int index) {
    if (index < 0 || index > imageCount)
      return -1;
    assureSpace(imageCount + 1);
    if (index < imageCount)
      System.arraycopy(images, index, images, index + 1, imageCount - index);
    imageCount++;
    images[index] = image;
    return index;
  }
  
  public boolean remove(Image image) {
    return remove(find(image));
  }
  
  public boolean remove(int index) {
    if (index < 0 || index >= imageCount)
      return false;
    if (index < imageCount - 1)
      System.arraycopy(images, index + 1, images, index, imageCount - index);
    else
      images[index] = null;
    imageCount--;
    return true;
  }
  
  public int find(Image image) {
    for (int i = 0; i < imageCount; i++)
      if (image == images[i] || image.equals(images[i]))
        return i;
    return -1;
  }
  
  public Dimension getPreferredSize(Object object, Graphics g, int state,
      ItemPaintSite site) {
    return super.getPreferredSize(images[((Number) object).intValue()], g,
        state, site);
  }
  
  public void paint(Object object, Graphics g, Rectangle bounds, int state,
      ItemPaintSite site) {
    super.paint(images[((Number) object).intValue()], g, bounds, state, site);
  }
  
  protected void assureSpace(int count) {
    if (count >= images.length) {
      Image[] newImages = new Image[Math.max(images.length * 2, count + 4)];
      System.arraycopy(images, 0, newImages, 0, images.length);
      images = newImages;
    }
  }
  
  // Serialization support
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Vector<SerializableImage> array = new Vector<SerializableImage>();
    for (int i = 0; i < images.length; i++)
      array.addElement(SerializableImage.create(images[i]));
    s.writeObject(array);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Vector<?> array = (Vector<?>) s.readObject();
    images = new Image[array.size()];
    for (int i = 0; i < images.length; i++)
      images[i] = ((SerializableImage) array.elementAt(i)).getImage();
    imageCount = images.length;
  }
  
  protected transient Image[] images;
  protected transient int imageCount;
}

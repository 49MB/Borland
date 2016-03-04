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
package com.borland.jbcl.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.Serializable;

/**
 * A class to enable serialization of java.awt.Image objects.<BR>
 * Use this class when you need to save an image object into a serialized stream.
 * When the image object is set, this class extracts the pixel information using a
 * PixelGrabber - and the image is serialized as pixel data.  When the image is de-
 * serialized, or retrieved - this class constructs a new MemoryImageSource object out
 * of the saved pixel data.<BR>
 * Following is an example useage in reading/writing an Image data member from a class
 * that implements java.io.Serializable.
 * <PRE>
 *   // Image object marked as transient
 *   private transient Image image;
 *
 *   // Custom serialization support - save to stream
 *   private void writeObject(ObjectOutputStream s) throws IOException {
 *     s.defaultWriteObject();
 *     s.writeObject(SerializableImage.create(image));
 *   }
 *
 *   // Custom serialization support - load from stream
 *   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
 *     s.defaultReadObject();
 *     image = ((SerializableImage)s.readObject()).getImage();
 *   }
 * </PRE>
 */
public class SerializableImage implements Serializable
{
  /**
   * Constructs a SerializableImage object with no associated java.awt.Image object.
   * @see setImage to set the associated java.awt.Image object.
   */
  public SerializableImage() {}

  /**
   * Constructs a SerializableImage object with the passed java.awt.Image object.
   * @param image the associated java.awt.Image object.
   */
  public SerializableImage(Image image) throws InterruptedException {
    setImage(image);
  }

  /**
   * The image property defines the java.awt.Image object that is to be serialized
   * with this class.
   */
  public void setImage(Image image) throws InterruptedException {
    PixelGrabber grabber = new PixelGrabber(image, 0, 0, -1, -1, true);
    grabber.grabPixels();
    imageData = (int[])grabber.getPixels();
    imageWidth = grabber.getWidth();
    imageHeight = grabber.getHeight();
    this.image = image;
  }

  public Image getImage() {
    if (image == null) {
      image = Toolkit.getDefaultToolkit().createImage(
        new MemoryImageSource(imageWidth, imageHeight, imageData, 0, imageWidth));
    }
    return image;
  }

  public static final SerializableImage create(Image source) {
    if (source == null)
      return null;
    try {
      SerializableImage newImage = new SerializableImage(source);
      return newImage;
    }
    catch (InterruptedException e) {
      return null;
    }
  }

  protected transient Image image;
  protected int imageData[];
  protected int imageWidth;
  protected int imageHeight;
}

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

package com.borland.jbcl.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.SerializableImage;

public class TransparentImage extends Component implements Serializable
{
  public TransparentImage() {
    setSize(100, 100);
  }

  public void setImageName(String path) throws IOException {
    if (path != null && !path.equals(""))
      setupImage(ImageLoader.load(path, this), path);
    else {
      imageName = null;
      image = null;
    }
  }
  public String getImageName() {
    return imageName;
  }

  public void setImage(Image image) throws IOException {
    setupImage(image, "");
  }
  public Image getImage() {
    return image;
  }

  public void setImageURL(URL url) throws IOException {
    this.url = url;
    setupImage(ImageLoader.load(url, this), url.toString());
  }
  public URL getImageURL() {
    return url;
  }

  public void setAlignment(int align) { alignment = align; }
  public int getAlignment() { return alignment; }

  protected void setupImage(Image im, String path) throws IOException {
    prepareImage(im, this);
    if ((checkImage(im, this) & ERROR) != 0)
      throw new IOException(java.text.MessageFormat.format(Res._FileNotFound, new Object[] {path} ));     
    imageName = path;
    if (isVisible()) {
      Graphics g = getGraphics();
      if (g != null) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getSize().width, getSize().height);
      }
    }
    image = im;
    repaint(100);
  }

  public void setTransparent(boolean transparent) {
    this.transparent = transparent;
    repaint(100);
  }
  public boolean isTransparent() {
    return transparent;
  }

  public void setEdgeColor(Color edgeColor) {
    this.edgeColor = edgeColor;
    repaint(100);
  }
  public Color getEdgeColor() {
    return edgeColor;
  }

  public void setDrawEdge(boolean drawEdge) {
    this.drawEdge = drawEdge;
    repaint(100);
  }
  public boolean isDrawEdge() {
    return drawEdge;
  }

  public Dimension getPreferredSize() {
    return image != null ? new Dimension(image.getWidth(this), image.getHeight(this))
                         : new Dimension(100, 100);
  }

  public void update(Graphics g) {
    paint(g);
  }

  private transient Image canvas;
  public void paint(Graphics pg) {
    Dimension size = getSize();
    Color old = pg.getColor();
    Graphics g = pg;
    if (!transparent) {
      if (canvas == null || canvas.getWidth(null) != size.width || canvas.getHeight(null) != size.height)
        canvas = createImage(size.width, size.height);
      g = canvas.getGraphics();
      g.setColor(getBackground());
      g.fillRect(0, 0, size.width, size.height);
    }
    if (image != null) {
      int imageWidth  = image.getWidth(null);
      int imageHeight = image.getHeight(null);
      // Horizontal alignment
      int x = 0;
      int width;
      switch (alignment & Alignment.HORIZONTAL) {
        default:
        case Alignment.LEFT:
          width = imageWidth;
          break;
        case Alignment.CENTER:
          x = (size.width - imageWidth)/2;
          width = imageWidth;
          break;
        case Alignment.RIGHT:
          x = size.width - imageWidth;
          width = imageWidth;
          break;
        case Alignment.HSTRETCH:
          width = size.width;
          break;
      }

      // Vertical alignment
      int y = 0;
      int height;
      switch (alignment & Alignment.VERTICAL) {
        default:
        case Alignment.TOP:
          height = imageHeight;
          break;
        case Alignment.MIDDLE:
          y = (size.height - imageHeight)/2;
          height = imageHeight;
          break;
        case Alignment.BOTTOM:
          y = size.height - imageHeight;
          height = imageHeight;
          break;
        case Alignment.VSTRETCH:
          height = size.height;
      }

      // paint the image first
      try {
        g.drawImage(image, x, y, width, height, this);
      }
      catch (java.lang.ArithmeticException e) {
      }

      if (!transparent) {
        pg.drawImage(canvas, 0, 0, null);
        g.dispose();
      }
    }

    // Draw the border
    if (drawEdge) {
      g.setColor(edgeColor);
      g.drawRect(0, 0, size.width - 1, size.height - 1);
    }

    g.setColor(old);
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(image != null ? SerializableImage.create(image) : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof SerializableImage)
      image = ((SerializableImage)data).getImage();
  }

  protected transient Image image;
  protected String  imageName;
  protected URL     url;
  protected Color   edgeColor = Color.black;
  protected boolean drawEdge = true;
  protected boolean transparent = true;
  protected int     alignment = Alignment.HSTRETCH | Alignment.VSTRETCH;
}

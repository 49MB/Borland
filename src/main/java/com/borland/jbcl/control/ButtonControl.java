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

import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.Pair;
import com.borland.jbcl.util.SerializableImage;
import com.borland.jbcl.view.ButtonView;
import com.borland.jbcl.view.CompositeItemPainter;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.ImageItemPainter;
import com.borland.jbcl.view.TextItemPainter;

public class ButtonControl extends ButtonView implements BlackBox, Serializable
{
  private static final long serialVersionUID = 200L;

  public ButtonControl() {
    setModel(new BasicSingletonContainer());
    setupPainters();
  }

  public ButtonControl(String label) {
    this();
    setLabel(label);
  }

  public ButtonControl(Image image) {
    this();
    setImage(image);
  }

  public ButtonControl(String label, Image image) {
    this();
    setLabel(label);
    setImage(image);
  }

  /**
   * orentation property defines how label & image are oriented: either
   * Orientation.HORIZONTAL or Orientation.VERTICAL
   */
  public void setOrientation(int o) {
    orientation = o;
    setupPainters();
  }

  public int getOrientation() {
    return orientation;
  }

  /**
   * imageFirst property defines how label & image are arranged: either
   * true for image on left/top or false for image on right/bottom
   */
  public void setImageFirst(boolean first) {
    imageFirst = first;
    setupPainters();
  }

  public boolean isImageFirst() {
    return imageFirst;
  }

  public void setLabel(String l) {
    label = l;
    setupPainters();
    invalidate();
    repaint(50);
  }

  public String getLabel() {
    return label;
  }

  public void setImage(Image image) {
    setImage(image, "");  // don't know the name in this one case
  }
//  public void setImage(String imageDirectory, String imageName) {
//    setImage(ImageLoader.loadFromPackage(imageDirectory, "", imageName, this));
//  }
  public Image getImage() {
    return image;
  }

  public void setImageURL(URL url) {
    this.url = url;
    setImage(ImageLoader.load(url, this), url.toString());
  }
  public URL getImageURL() {
    return url;
  }

  public void setImageName(String name) {

    Image image = null;
    if (name != null) {
      ImageLoader.loadFromResource(name, this);
      if (image == null) {
        try {
          image = ImageLoader.load(new URL(name), this);
        }
        catch (MalformedURLException e) {
          image = ImageLoader.load(name, this);
        }
      }
    }
    setImage(image, name);
  }
  public String getImageName() {
    return imageName;
  }

  /**
   *
   */
  protected void setImage(Image im, String path) {
    if (isReadOnly())
      throw new IllegalStateException(Res._ReadOnlySet);     
//    if (ImageLoader.waitForImage(this, image) == false)
//      Diagnostic.fail();
    if (im != null)
      prepareImage(im, this);
//    if ((checkImage(im, this) & ERROR) != 0)
//      throw new IOException(java.text.MessageFormat.format(Res._FileNotFound, new String[] {path} ));
    image = im;
    imageName = path;
    getWriteModel().set(image);
    setupPainters();
  }

  /**
   *
   */
  public boolean imageUpdate(Image im, int flags, int x, int y, int w, int h) {
    if ((flags & (HEIGHT|WIDTH)) != 0)
      invalidate();
    return super.imageUpdate(im, flags, x, y, w, h);
  }

  public void setTextureName(String path) {
    if (path != null && !path.equals("")) {
      Image i = ImageLoader.load(path, this);
      if (i != null) {
        ImageLoader.waitForImage(this, i);
        textureName = path;
        setTexture(i);
      }
      else {
        throw new IllegalArgumentException(path);
      }
    }
    else {
      textureName = null;
      setTexture(null);
    }
  }
  public String getTextureName() {
    return textureName;
  }

  /**
   *
   */
  private void setupPainters() {
    ItemPainter imagePainter = image != null ? new ImageItemPainter(this, getAlignment()) : null;
    ItemPainter labelPainter = new FocusableItemPainter(new TextItemPainter(getAlignment(), new Insets(1, 1, 1, 1)), false);
    ItemPainter painter = null;
    Object data = null;
    if (image == null/* && label != null*/) {
      painter = labelPainter;
      data = label;
    }
    else if (image != null && label == null) {
      painter = new FocusableItemPainter(imagePainter, false);
      data = image;
    }
    else if (image != null && label != null) {
      ItemPainter firstPainter;
      ItemPainter secondPainter;
      if (imageFirst) {
        firstPainter = imagePainter;
        secondPainter = labelPainter;
        data = new Pair(image, label);
      }
      else {
        firstPainter = labelPainter;
        secondPainter = imagePainter;
        data = new Pair(label, image);
      }
      painter = new CompositeItemPainter(firstPainter, secondPainter, orientation, getAlignment());
    }
    setViewManager(new BasicViewManager(painter, null));
    if (!isReadOnly())
      getWriteModel().set(data);
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
  protected int       orientation;
  protected boolean   imageFirst = true;
  protected URL       url;
  protected String    imageName;
  protected String    label;
  protected String    textureName;
}

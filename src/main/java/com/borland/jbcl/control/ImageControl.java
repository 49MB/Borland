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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.ImageItemEditor;
import com.borland.jbcl.view.ImageItemPainter;

public class ImageControl extends FieldControl implements BlackBox, Serializable
{
  public ImageControl() {
    setAlignment(Alignment.HSTRETCH | Alignment.VSTRETCH);
    setPreferredHeight(100);
    setEditInPlace(false);
    defaultLayout();
  }

  protected void defaultLayout() {
    setModel(new BasicSingletonContainer());
    setViewManager(new BasicViewManager(
      new FocusableItemPainter(new ImageItemPainter(this, getAlignment())),
      new ImageItemEditor()));
  }

  public void setModel(SingletonModel model) {
    if (model == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(model);
  }

  public void setImageName(String path) throws IOException {
    if (path != null && !path.equals(""))
      setupImage(ImageLoader.load(path, this), path);
    else {
      if (isReadOnly())
        throw new IllegalStateException(Res._ReadOnlySet);     
      imageName = null;
      getWriteModel().set(null);
    }
  }
  public String getImageName() {
    return imageName;
  }

  public void setImage(Image image) throws IOException {
    setupImage(image, "");
  }
  public Image getImage() {
    Object o = getModel().get();
    return o instanceof Image ? (Image)o : null;
  }

  public void setImageURL(URL url) throws IOException {
    this.url = url;
    setupImage(ImageLoader.load(url, this), url.toString());
  }
  public URL getImageURL() {
    return url;
  }

  protected void setupImage(Image im, String path) throws IOException {
    if (isReadOnly())
      throw new IllegalStateException(Res._ReadOnlySet);     
    prepareImage(im, this);
    if ((checkImage(im, this) & ERROR) != 0)
      throw new IOException(java.text.MessageFormat.format(Res._FileNotFound, new Object[] {path} ));     
//    if (ImageLoader.waitForImage(this, im) == false)
//      throw new IOException(java.text.MessageFormat.format(Res._FileNotFound, new String[] {path} ));
    imageName = path;
    if (isVisible()) {
      Graphics g = getGraphics();
      if (g != null) {
        Rectangle r = getInnerRect();
        g.setColor(getBackground());
        g.fillRect(r.x, r.y, r.width, r.height);
      }
    }
    if (getWriteModel() != null)
      getWriteModel().set(im);
  }

  public void modelContentChanged(SingletonModelEvent e) {
    if (isVisible()) {
      Graphics g = getGraphics();
      if (g != null) {
        Rectangle r = getInnerRect();
        g.setColor(getBackground());
        g.fillRect(r.x, r.y, r.width, r.height);
      }
    }
    super.modelContentChanged(e);
  }

  /**
   *
   */
  public boolean imageUpdate(Image im, int flags, int x, int y, int w, int h) {
    if ((flags & (HEIGHT|WIDTH)) != 0)
      invalidate();
    return super.imageUpdate(im, flags, x, y, w, h);
  }

  protected void updateSelection() {
    if (getDataSet() != null && getColumnName() != null) {
      try {
        getDataSet().getVariant(getColumnName(), value);
        try {
          Image image = ImageLoader.loadFromBlob(value, this, true);
          setupImage(image, "");
        }
        catch (IOException e) {
          Diagnostic.printStackTrace(e);
        }
      }
      catch (Exception ex) {
        Diagnostic.printStackTrace(ex);
      }
    }
    repaint(100);
  }

  private String imageName;
  private URL    url;
  private String boundName;
  private Variant value = new Variant();
}

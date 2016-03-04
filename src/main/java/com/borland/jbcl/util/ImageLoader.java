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

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.GrayFilter;

import com.borland.dx.dataset.Variant;
import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.SearchPath;

@SuppressWarnings("serial")
public class ImageLoader implements Serializable {
  static ImageCache urlImageCache = new ImageCache(32);
  static ImageCache blobImageCache = new ImageCache(16);
  static ImageCache disabledImageCache = new ImageCache(48);
  
  public static void setUrlImageCacheLimit(int limit) {
    urlImageCache.setLimit(limit);
  }
  
  public static int getUrlImageCacheLimit() {
    return urlImageCache.getLimit();
  }
  
  public static void setBlobImageCacheLimit(int limit) {
    blobImageCache.setLimit(limit);
  }
  
  public static int getBlobImageCacheLimit() {
    return blobImageCache.getLimit();
  }
  
  public static void setDisabledImageCacheLimit(int limit) {
    disabledImageCache.setLimit(limit);
  }
  
  public static int getDisabledImageCacheLimit() {
    return disabledImageCache.getLimit();
  }
  
  public static Image load(String path, Component component) {
    try {
      return component != null ? component.getToolkit().getImage(path)
          : Toolkit.getDefaultToolkit().getImage(path);
    } catch (SecurityException x) {
      return null;
    }
  }
  
  public static Image load(String path, Component component, boolean wait) {
    Image image = load(path, component);
    if (image != null && wait)
      waitForImage(component, image);
    return image;
  }
  
  public static Image load(URL url, Component component) {
    Image image = urlImageCache.get(url.toString());
    if (image != null) {
      return image;
    }
    try {
      image = component != null ? component.getToolkit().getImage(url)
          : Toolkit.getDefaultToolkit().getImage(url);
    } catch (SecurityException x) {
    }
    if (image != null) {
      urlImageCache.put(url.toString(), image, component);
    }
    return image;
  }
  
  public static Image load(URL url, Component component, boolean wait) {
    Image image = load(url, component);
    if (image != null && wait)
      waitForImage(component, image);
    return image;
  }
  
  // load an image along a given search path filename
  
  public static Image loadFromPath(SearchPath path, String name,
      Component component) {
    try {
      Toolkit toolkit = component == null ? Toolkit.getDefaultToolkit()
          : component.getToolkit();
      return toolkit.getImage(path.getPath(name));
    } catch (SecurityException x) {
      return null;
    }
  }
  
  public static Image loadFromPath(SearchPath path, String name,
      Component component, boolean wait) {
    Image image = loadFromPath(path, name, component);
    if (image != null && wait)
      waitForImage(component, image);
    return image;
  }
  
  public static Image loadFromResource(String name, Component component) {
    return loadFromResource(name, component, component.getClass());
  }
  
  public static Image loadFromResource(String name, Class cl) {
    return loadFromResource(name, null, cl);
  }
  
  public static Image loadFromResource(String name, Component component,
      Class cl) {
    try {
      URL url = cl.getResource(name);
      if (url == null)
        return null;
      Image image = urlImageCache.get(url.toString()); // because Url.hashcode()
      // can throw
      // exceptions...
      if (image != null) {
        return image;
      }
      Object content = url.getContent();
      if (content instanceof Image)
        image = (Image) content;
      else if (content instanceof ImageProducer) {
        if (component != null)
          image = component.createImage((ImageProducer) content);
        else
          image = Toolkit.getDefaultToolkit().createImage(
              (ImageProducer) content);
      } else
        return null;
      if (component != null)
        component.prepareImage(image, component);
      else
        Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, component);
      urlImageCache.put(url.toString(), image, component); // Url.hashCode()
      // throws
      // exceptions...
      return image;
    } catch (IOException e) {
      System.err.println("loadFromResource IOException name=" + name
          + " component=" + component + " cl=" + cl);
      // Diagnostic.printStackTrace(e);
    } catch (Exception e) {
      System.err.println("loadFromResource Exception" + e + " name=" + name
          + " component=" + component + " cl=" + cl);
      // Diagnostic.printStackTrace(e);
    }
    return null;
  }
  
  public static Image loadFromBlob(Object object, Component component)
      throws IOException {
    if (object == null) {
      return null;
    }
    
    if (object instanceof Variant) {
      Variant value = (Variant) object;
      if (value.isNull()) {
        return null;
      }
      object = value.getInputStream();
    }
    
    Image image = blobImageCache.get(object);
    if (image != null) {
      return image;
    }
    
    if (object instanceof Image)
      image = (Image) object;
    
    else if (object instanceof InputStream) {
      // stream = ((Variant)object).getInputStream();
      InputStream stream = (InputStream) object;
      stream.reset();
      byte[] buffer = byteArrayFromStream(stream);
      Toolkit tk = component != null ? component.getToolkit() : Toolkit
          .getDefaultToolkit();
      image = tk.createImage(buffer);
    }
    if (image != null) {
      blobImageCache.put(object, image, component);
    }
    return image;
  }
  
  public static Image loadFromBlob(Object object, Component component,
      boolean wait) throws IOException {
    Image image = loadFromBlob(object, component);
    if (image != null && wait)
      waitForImage(component, image);
    return image;
  }
  
  private static byte[] byteArrayFromStream(InputStream s) throws IOException {
    return InputStreamToByteArray.getBytes(s);
  }
  
  public static boolean waitForImage(Component component, Image image) {
    // The observer is ignored when null, so avoid much work when not needed
    if (image == null)
      return false;
    if (image.getWidth(null) > 0)
      return true;
    
    MediaTracker m = new MediaTracker(component);
    m.addImage(image, 1);
    try {
      m.waitForID(1);
    } catch (InterruptedException e) {
      Diagnostic.printStackTrace(e);
    }
    return !m.isErrorID(1);
  }
  
  public static Image getDisabledImage(Component component, Image image) {
    Image disabledImage = disabledImageCache.get(image);
    if (disabledImage != null)
      return disabledImage;
    disabledImage = GrayFilter.createDisabledImage(image);
    disabledImageCache.put(image, disabledImage, component);
    return disabledImage;
  }
}

/**
 *
 */
@SuppressWarnings("serial")
class ImageCache implements Serializable {
  // private OrderedMap map = new OrderedMap();
  private transient final Hashtable<Object, Image> map = new Hashtable<Object, Image>(); // do
  // not
  // Serialize
  private transient final Vector<Object> list = new Vector<Object>(); // do not
  // Serialize
  int limit;
  
  ImageCache() {
    this(10);
  }
  
  ImageCache(int limit) {
    if (limit <= 0)
      throw new IllegalArgumentException();
    this.limit = limit;
  }
  
  void setLimit(int newLimit) {
    if (newLimit <= 0)
      throw new IllegalArgumentException();
    int size;
    while ((size = list.size()) > newLimit) {
      Object lastKey = list.elementAt(size - 1);
      list.removeElementAt(size - 1);
      map.remove(lastKey);
    }
    this.limit = newLimit;
  }
  
  int getLimit() {
    return limit;
  }
  
  void put(Object key, Image image, Component component) {
    // throw away tail key if cache is too full
    if (limit > 0 && list.size() >= limit) {
      Object lastKey = list.elementAt(list.size() - 1);
      list.removeElementAt(list.size() - 1);
      if (lastKey != null) {
        Image last = map.get(lastKey);
        ImageLoader.waitForImage(component, last);
      }
      map.remove(lastKey);
    }
    map.put(key, image);
    list.insertElementAt(key, 0);
  }
  
  Image get(Object key) {
    Image image = map.get(key);
    if (image != null) {
      // every access moves key to head to maintain MRU
      list.removeElement(key);
      list.insertElementAt(key, 0);
    }
    return image;
  }
}

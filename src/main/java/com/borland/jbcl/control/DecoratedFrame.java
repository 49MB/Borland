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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.SystemColor;
import java.awt.event.WindowEvent;
import java.beans.Beans;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import com.borland.jbcl.util.ImageLoader;

/**
 *
 */
public class DecoratedFrame extends Frame implements Serializable
{
  public DecoratedFrame() {
    super();
    setLayout(new BorderLayout());  // center is the client, borders are decorations
    setBackground(SystemColor.control);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
  }

  public Component getClient() {
    return client;
  }

  public void setClient(Component client) {
    if (this.client != null)
      remove(this.client);
    this.client = client;
    super.add(this.client, BorderLayout.CENTER);
    Dimension cs = client.getSize();
    if (cs.width > 0 && cs.height > 0) {
      int w = cs.width + getInsets().left + getInsets().right;
      int h = cs.height + getInsets().top + getInsets().bottom;
      setSize(w, h);
    }
  }

  /**
   * The disposeOnClose property controls what this window does when it receives
   * a WindowEvent.WINDOW_CLOSING event.  Setting this property to true causes
   * the frame to dispose when it receives a WINDOW_CLOSING event.  If false, nothing
   * happens (by default) when a WINDOW_CLOSING event occurs.  A user must add a
   * WindowListener and call dispose() themselves.
   * By default, this property is true.
   */
  public void setDisposeOnClose(boolean dispose) {
    disposeOnClose = dispose;
  }

  public boolean isDisposeOnClose() {
    return disposeOnClose;
  }

  /**
   * The exitOnClose property controls what this window does when it receives
   * a WindowEvent.WINDOW_CLOSED event.  Setting this property to true causes
   * the application to exit - shutting down the VM - when it receives a WINDOW_CLOSED
   * event.  If false, nothing happens (by default) when a WINDOW_CLOSED event occurs.
   * By default, this property is true.
   */
  public void setExitOnClose(boolean eoc) {
    exitOnClose = eoc;
  }

  public boolean isExitOnClose() {
    return exitOnClose;
  }

  public void setIconImageName(String path) throws IOException {
    if (path != null && !path.equals(""))
      setupImage(ImageLoader.load(path, this), path);
    else {
      imageName = null;
      super.setIconImage(null);
    }
  }

  public String getIconImageName() {
    return imageName;
  }

  public void setIconImage(Image image) {
    try {
      setupImage(image, "");
    }
    catch (IOException x) {
      throw new IllegalArgumentException(x.getMessage());
    }
  }

  public void setIconImageURL(URL url) throws IOException {
    this.url = url;
    setupImage(ImageLoader.load(url, this), url.toString());
  }

  public URL getIconImageURL() {
    return url;
  }

  //------------------------------------------------------------------------------------------------
  // internal implementation

  protected void setupImage(Image im, String path) throws IOException {
    prepareImage(im, this);
    if ((checkImage(im, this) & ERROR) != 0)
      throw new IOException(java.text.MessageFormat.format(Res._FileNotFound, new Object[] {path} ));     
    imageName = path;
    super.setIconImage(im);
  }

  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING && disposeOnClose)
      dispose();
    else if (e.getID() == WindowEvent.WINDOW_CLOSED && exitOnClose && !Beans.isDesignTime())
      System.exit(0);
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    // Add the MenuBar (if any) height
    MenuBar m = getMenuBar();
    if (m != null) {
      Font f = m.getFont();
      FontMetrics fm = f != null ? getFontMetrics(f) : null;
      int h = fm != null ? fm.getHeight() : 0;
      d.height += h;
    }
    return d;
  }

  protected Component client;
  protected boolean   exitOnClose    = true;
  protected boolean   disposeOnClose = true;
  protected String    imageName;
  protected URL       url;
}

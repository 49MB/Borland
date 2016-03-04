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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import javax.swing.JComponent;

import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.util.ActionMulticaster;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.ImageTexture;
import com.borland.jbcl.util.SerializableImage;

/**
 * Convenient lightweight container to use as a superclass for JavaBean views and controls.  The
 * BeanPanel includes many commonly desired features, including:
 * <UL>
 * <LI>subdispatches focus, key and mouse events
 * <LI>manages action listeners
 * <LI>manages tab/focus awareness
 * <LI>surfaces a texture property
 * </UL>
 */
public class BeanPanel extends JComponent implements Serializable{
  private static final long serialVersionUID = 200L;

  public BeanPanel() {
    enableEvents(AWTEvent.FOCUS_EVENT_MASK |
                 AWTEvent.KEY_EVENT_MASK |
                 AWTEvent.MOUSE_EVENT_MASK |
                 AWTEvent.MOUSE_MOTION_EVENT_MASK);
    super.setLayout(new FlowLayout());
    super.setDoubleBuffered(true);
    super.setOpaque(true);
  }

  public BeanPanel(LayoutManager layout) {
    enableEvents(AWTEvent.FOCUS_EVENT_MASK |
                 AWTEvent.KEY_EVENT_MASK |
                 AWTEvent.MOUSE_EVENT_MASK |
                 AWTEvent.MOUSE_MOTION_EVENT_MASK);
    super.setLayout(layout);
    super.setDoubleBuffered(true);
    super.setOpaque(true);
  }

  /**
   * protected implementation of action event registration methods that a subclass
   * can expose as public methods if it sources action events
   */
  public synchronized void addActionListener(ActionListener l) {
    if (actionMulticaster == null)
      actionMulticaster = new ActionMulticaster();
    actionMulticaster.add(l);
  }

  public synchronized void removeActionListener(ActionListener l) {
    if (actionMulticaster != null)
      actionMulticaster.remove(l);
  }

  public void setTexture(Image texture) {
    this.texture = texture;
    if (texture != null)
      ImageLoader.waitForImage(this, texture);
    repaint();
  }
  public Image getTexture() {
    return texture;
  }

  public void setOpaque(boolean opaque) {
    super.setOpaque(opaque);
    if (isVisible())
      repaint();
  }

  public boolean isOpaque() {
    return texture != null ? true : super.isOpaque();
  }

  public void setBackground(Color c) {
    super.setBackground(c);
    repaint();
  }

  public void setForeground(Color c) {
    super.setForeground(c);
    repaint();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (isOpaque()) {
      Rectangle clip = g.getClipBounds();
      if (texture != null) {
        ImageTexture.texture(texture, g, clip.x, clip.y, clip.width, clip.height);
      }
      else {
        g.setColor(getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
      }
    }
  }

  // General events

  protected void processEvent(AWTEvent e) {
    if (e instanceof ActionEvent)
      processActionEvent((ActionEvent)e);
    else
      super.processEvent(e);
  }

  // Action events

  protected void processActionEvent(ActionEvent e) {
    if (actionMulticaster != null)
      actionMulticaster.dispatch(e);
  }

  // Key events

  protected void processKeyEvent(KeyEvent e) {
    //System.err.println("processKeyEvent(" + e + ")");
    if (!isEnabled())
      return;
    super.processKeyEvent(e);
    if (!e.isConsumed()) {
      switch (e.getID()) {
        case KeyEvent.KEY_PRESSED:  processKeyPressed(e); break;
        case KeyEvent.KEY_TYPED:    processKeyTyped(e); break;
        case KeyEvent.KEY_RELEASED: processKeyReleased(e); break;
      }
    }
  }
  protected void processKeyPressed(KeyEvent e) {}
  protected void processKeyTyped(KeyEvent e) {}
  protected void processKeyReleased(KeyEvent e) {}

  // Mouse events

  protected void processMouseEvent(MouseEvent e) {
    //System.err.println("processMouseEvent(" + e + ")");
    if (!isEnabled())
      return;
    super.processMouseEvent(e);
    if (!e.isConsumed()) {
      switch (e.getID()) {
        case MouseEvent.MOUSE_PRESSED:  processMousePressed(e);  break;
        case MouseEvent.MOUSE_RELEASED: processMouseReleased(e); break;
        case MouseEvent.MOUSE_CLICKED:  processMouseClicked(e);  break;
        case MouseEvent.MOUSE_ENTERED:  processMouseEntered(e);  break;
        case MouseEvent.MOUSE_EXITED:   processMouseExited(e);   break;
      }
    }
  }

  protected void processMouseMotionEvent(MouseEvent e) {
    if (!isEnabled())
      return;
    super.processMouseMotionEvent(e);
    if (!e.isConsumed()) {
      switch (e.getID()) {
        case MouseEvent.MOUSE_MOVED:    processMouseMoved(e);    break;
        case MouseEvent.MOUSE_DRAGGED:  processMouseDragged(e);  break;
      }
    }
  }

  protected void processMousePressed(MouseEvent e) {
    if (e.getClickCount() == 1 && isFocusTraversable()) {
      requestFocus();
    }
  }
  protected void processMouseReleased(MouseEvent e) {}
  protected void processMouseClicked(MouseEvent e) {}
  protected void processMouseEntered(MouseEvent e) {}
  protected void processMouseExited(MouseEvent e) {}
  protected void processMouseMoved(MouseEvent e) {}
  protected void processMouseDragged(MouseEvent e) {}

  // Focus events

  /**
   * protected support for a focusAware property that a subclass
   * can expose as public if it has the potential for accepting focus
   */
  protected void setFocusAware(boolean aware) {
    focusAware = aware;
  }
  protected boolean isFocusAware() {
    return focusAware;
  }

  /**
   * Overridden component method to allow us to control our focus-awareness.
   */
  public boolean isFocusTraversable() { return focusAware; }

  protected void processFocusEvent(FocusEvent e) {
    //System.err.println("BeanPanel.processFocusEvent e=" + e + " focusState=" + focusState + " focusAware=" + focusAware);
    if (focusAware) {
      switch (e.getID()) {
        case FocusEvent.FOCUS_GAINED:
          focusState |= ItemPainter.FOCUSED;
          break;
        case FocusEvent.FOCUS_LOST:
          focusState &= ~ItemPainter.FOCUSED;
          break;
      }
    }
    super.processFocusEvent(e);
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /**
   * Override this method to repaint when the containing window becomes active/inactive.
   * Remember to call super.windowActiveChanged(active) so this state tracking code can
   * execute.
   */
  public void windowActiveChanged(boolean active) {
    if (active)
      focusState &= ~ItemPainter.INACTIVE;
    else
      focusState |= ItemPainter.INACTIVE;
  }

  /**
   * Walk up the hierarchy to find the first window - and listen for activation/deactivation
   */
  public void addNotify() {
    super.addNotify();
    findParentWindow();
  }

  public void removeNotify() {
    dropParentWindow();
    super.removeNotify();
  }

  /**
   * Called to force the bean to locate its parent window in the component
   * hierarchy (to listen for focus changes)
   */
  protected void findParentWindow() {
    if (foundParentWindow)
      return;
    Component c = getParent();
    while (c != null && !(c instanceof Window))
      c = c.getParent();
    if (c instanceof Window) {
      ((Window)c).addWindowListener(winListener);
      foundParentWindow = true;
    }
  }

  /**
   * Called to force the bean to locate its parent window in the component
   * hierarchy, and remove its WindowListener
   */
  protected void dropParentWindow() {
    Component c = getParent();
    while (c != null && !(c instanceof Window))
      c = c.getParent();
    if (c instanceof Window)
      ((Window)c).removeWindowListener(winListener);
    foundParentWindow = false;
  }

  protected transient WindowListener winListener = new WindowAdapter() {
    public void windowActivated(WindowEvent e) { windowActiveChanged(true); }
    public void windowDeactivated(WindowEvent e) { windowActiveChanged(false); }
  };

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    if (texture != null)
      s.writeObject(SerializableImage.create(texture));
    else
      s.writeObject(null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof SerializableImage)
      texture = ((SerializableImage)data).getImage();
  }

  protected transient ActionMulticaster actionMulticaster;
  protected transient Image texture;
  protected boolean   focusAware = true;
  protected int       focusState;
  protected transient boolean foundParentWindow = false; // read back as false
  transient Rectangle _bounds = new Rectangle();

  static boolean is1dot3 = true;

  static {
    try {
      // Test if method introduced in 1.3 is available.
      Method method = JComponent.class.getMethod("getInputVerifier", (Class<?>[])null); 
      is1dot3 = (method != null);
    } catch (NoSuchMethodException e) {
      is1dot3 = false;
    }
  }
}


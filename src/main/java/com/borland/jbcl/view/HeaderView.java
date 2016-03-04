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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.UIManager;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.EventMulticaster;
import com.borland.jbcl.model.BasicVectorContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorModelEvent;
import com.borland.jbcl.model.VectorModelListener;
import com.borland.jbcl.model.VectorViewManager;
import com.borland.jbcl.util.Orientation;

/**
 * Horizontal or vertical header. Property names are in terms of a horizontal, top header
 */
public class HeaderView
     extends BeanPanel
  implements ItemPaintSite, VectorModelListener, Serializable
{
  private static final long serialVersionUID = 200L;

  public HeaderView() { this(Orientation.HORIZONTAL); }
  public HeaderView(int defaultOrientation) {
    super(null);
    orientation = defaultOrientation;
    horz = (orientation == Orientation.HORIZONTAL);
    model = createDefaultModel();
    viewManager = createDefaultViewManager();
    setFocusAware(false);
    super.setBackground(UIManager.getColor("TableHeader.background")); 
    super.setForeground(UIManager.getColor("TableHeader.foreground")); 
  }

  public void updateUI() {
    super.updateUI();
    super.setBackground(UIManager.getColor("TableHeader.background")); 
    super.setForeground(UIManager.getColor("TableHeader.foreground")); 
  }

  protected VectorModel createDefaultModel() {
    return new BasicVectorContainer();
  }

  protected VectorViewManager createDefaultViewManager() {
    return new BasicViewManager(new ButtonItemPainter(new TextItemPainter()));
  }

  public VectorModel getModel() { return model; }
  public void setModel(VectorModel vm) {
    if (model != null)
      model.removeModelListener(this);
    model = vm;
    if (model != null)
      model.addModelListener(this);
  }

  public boolean isTransparent() {
    return texture != null ? true : !isOpaque();
  }

  public void setItemMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getItemMargins() { return margins; }

  public void setAlignment(int a) {
    this.alignment = a;
  }
  public int getAlignment() { return alignment; }

  public Component getSiteComponent() { return this; }

  public VectorViewManager getViewManager() { return viewManager; }
  public void setViewManager(VectorViewManager newViewManager) { viewManager = newViewManager; }

  public void addHeaderListener(HeaderListener listener) { headerListeners.add(listener); }
  public void removeHeaderListener(HeaderListener listener) { headerListeners.remove(listener); }

  public int getOrientation() { return orientation; }
  public void setOrientation(int orientation) {
    this.orientation = orientation;
    horz = (orientation == Orientation.HORIZONTAL);
    invalidate();
    repaint();
  }

  public boolean isResizable() { return resizable; }
  public void setResizable(boolean resizable) {
    this.resizable = resizable;
  }

  public boolean isLiveResize() { return liveResize; }
  public void setLiveResize(boolean liveResize) {
    this.liveResize = liveResize;
  }

  public boolean isMoveable() { return moveable; }
  public void setMoveable(boolean moveable) {
    this.moveable = moveable;
  }

  public void setShowRollover(boolean showRollover) { this.showRollover = showRollover; }
  public boolean isShowRollover() { return showRollover; }

  public void setBatchMode(boolean batchMode) {
    this.batchMode = batchMode;
    if (!this.batchMode)
      repaint();
  }
  public boolean isBatchMode() { return batchMode; }

  // VectorModelListener implementation

  public void modelStructureChanged(VectorModelEvent e) { resetSize(); }
  public void modelContentChanged(VectorModelEvent e) { resetSize(); }

  /**
   * Thickness of this header--height when horizontal, width when vertical
   * set to 0 to auto-calculate
   */
  public int getThickness() {
    if (thickness == 0 && model != null && viewManager != null) {
      Graphics g = getGraphics();
      if (g == null) return 0;
      Font f = getFont();
      if (horz) {
        g.setFont(f);
        Object data = model.getCount() > 0 ? model.get(0) : null;
        ItemPainter ip = viewManager.getPainter(0, null, 0);
        Dimension ps = ip != null ? ip.getPreferredSize(data, g, 0, null) : new Dimension(0,0);
        if (model.getCount() > 0) {
          thickness = ps != null ? ps.height : 0;
        }
        else {
          FontMetrics fm = g.getFontMetrics(f);
          thickness = fm != null ? fm.getHeight() : 0;
        }
      }
      else {
        FontMetrics fm = g.getFontMetrics(f);
        thickness = fm != null ? fm.stringWidth("WWW") : 0; 
      }
    }
    return thickness;
  }

  public void setThickness(int thickness) {
    if (this.thickness != thickness) {
      this.thickness = thickness;
      getThickness();
      invalidate();
      repaint();
    }
  }

  public SizeVector getItemSizes() { return sizes; }
  public void setItemSizes(SizeVector newSizes) { sizes = newSizes; }

  public Dimension getPreferredSize() {
    getThickness();
    if (model == null || sizes == null)
      return new Dimension(thickness, thickness);
    if (horz)
      return new Dimension(sizes.getSizeUpTo(model.getCount()), thickness);
    else
      return new Dimension(thickness, sizes.getSizeUpTo(model.getCount()));
  }

  /**
   * Return an item index given a viewport distance into the header:
   * x when horizontal, y when vertical
   */
  private int hitTest(int pos) {
    int item = 0;
    int width = sizes.getSize(item);
    int count = model.getCount();
    while (pos >= width && ++item < count)
      width += sizes.getSize(item);
    if (item == count)
      item--;
    return item;
  }

  protected void resetSize() {
    getThickness();
    Dimension size = new Dimension(thickness, thickness);
    if (horz)
      size.width = sizes.getSizeUpTo(model.getCount());
    else
      size.height = sizes.getSizeUpTo(model.getCount());
    setSize(size);
  }

  // Events

  protected void processHeaderEvent(HeaderEvent e) {
    if (headerListeners.hasListeners())
      headerListeners.dispatch(e);
  }

  protected void processMousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    rollover = -1;
    if (headerListeners.hasListeners()) {
      int item = horz ? hitTest(x) : hitTest(y);
      Rectangle rect = getItemBounds(item);
      if (horz) {
        if (x <= rect.x + rect.width) {
          clickItem = item;
          if (x >= (rect.x + rect.width - margin) && resizable) {
            if (sizes instanceof FixedSizeVector && item != 0) {
              return;
            }
            offset = rect.x + rect.width - x;
            resizing = true;
            processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.START_RESIZE, clickItem, x, y));
          }
          else if (moveable) {
            startMove = true;
          }
        }
      }
      else {
        if (y <= rect.y + rect.height) {
          clickItem = item;
          if (y >= (rect.y + rect.height - margin) && resizable) {
            if (sizes instanceof FixedSizeVector && item != 0) {
              return;
            }
            offset = rect.y + rect.height - y;
            resizing = true;
            processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.START_RESIZE, clickItem, x, y));
          }
        }
      }
    }
  }

  protected void processMouseMoved(MouseEvent e) {
    int pos = horz ? e.getX() : e.getY();
    int item = hitTest(pos);
    if (showRollover) {
      if (item >= 0 && item != rollover) {
        int oldRollover = rollover;
        rollover = item;
        repaintItem(oldRollover);
        repaintItem(rollover);
      }
    }
    if (sizes instanceof FixedSizeVector && item != 0) {
      setCursor(DEFAULT_CURSOR);
      return;
    }
    Rectangle rect = getItemBounds(item);
    if (horz) {
      if (pos <= rect.x + rect.width) {
        if (pos >= (rect.x + rect.width - margin) && resizable) {
          setCursor(H_SIZE_CURSOR);
          return;
        }
      }
    }
    else {
      if (pos <= rect.y + rect.height) {
        if (pos >= (rect.y + rect.height - margin) && resizable) {
          setCursor(V_SIZE_CURSOR);
          return;
        }
      }
    }
    setCursor(DEFAULT_CURSOR);
  }

  protected void processMouseDragged(MouseEvent e) {
    rollover = -1;
    if (headerListeners.hasListeners()) {
      if (resizing || moving || startMove) {
        int x = e.getX();
        int y = e.getY();
        if (resizing) {
          if (sizes instanceof FixedSizeVector && clickItem != 0) {
            setCursor(DEFAULT_CURSOR);
            return;
          }
          if (liveResize) {
            Rectangle rect = getItemBounds(clickItem);
            int newSize = (horz ? x : y) - (horz ? rect.x : rect.y) + offset;
            if (newSize < GridCore.MIN_CELL_SIZE)
              newSize = GridCore.MIN_CELL_SIZE;
            sizes.setSize(clickItem, newSize);
            resetSize();
          }
          processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.WHILE_RESIZE, clickItem, x, y));
          return;
        }
        else if (startMove) {
          startMove = false;
          moving = true;
          processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.START_MOVE, clickItem, x, y));
          return;
        }
        else if (moving) {
          setCursor(MOVE_CURSOR);
          processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.WHILE_MOVE, clickItem, x, y));
          return;
        }
      }
      setCursor(DEFAULT_CURSOR);
    }
  }

  protected void processMouseReleased(MouseEvent e) {
    rollover = -1;
    int x = e.getX();
    int y = e.getY();
    if (headerListeners.hasListeners()) {
      if (resizing) {
        processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.STOP_RESIZE, clickItem, x, y));
        repaint();
      }
      if (moving) {
        processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.STOP_MOVE, clickItem, x, y));
        repaint();
      }
    }
    resizing  = false;
    moving    = false;
    startMove = false;
    setCursor(DEFAULT_CURSOR);
  }

  protected void processMouseExited(MouseEvent e) {
    if (showRollover) {
      int oldRollover = rollover;
      rollover = -1;
      repaintItem(oldRollover);
    }
    setCursor(DEFAULT_CURSOR);
  }

  protected void processMouseClicked(MouseEvent e) {
    rollover = -1;
    int x = e.getX();
    int y = e.getY();
    int pos = horz ? x : y;
    int index = hitTest(pos);
    if (index == clickItem) {
      processHeaderEvent(new HeaderEvent((Object)this, HeaderEvent.ITEM_CLICKED, index, x, y));
    }
    setCursor(DEFAULT_CURSOR);
  }

  /**
   * return the bounding rectangle for a given item
   */
  public Rectangle getItemBounds(int item) {
    Rectangle rect = getBounds();
    if (rect == null)
      return new Rectangle();
    // The bounds coordinates are container relative: offset them so that we start from 0,0 for drawing
    rect.x -= rect.x; rect.y -= rect.y;
    int l = 0;
    for (int index = 0; index < item; ++index)
      l += sizes.getSize(index);
    if (horz) {
      rect.x = l;
      rect.width = sizes.getSize(item);
    }
    else {
      rect.y = l;
      rect.height = sizes.getSize(item);
    }
    return rect;
  }

  public void repaintItem(int index) {
    Rectangle rect = getItemBounds(index);
    if (rect != null)
      repaint(rect.x, rect.y, rect.width, rect.height);
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paintComponent(Graphics g) {
    if (batchMode)
      return;
    super.paintComponent(g);
    Rectangle cr = g.getClipBounds();
    if (cr == null)
      return;
    Rectangle clip = new Rectangle(0,0, getSize().width, getSize().height);
    if (clip.isEmpty() || cr.isEmpty())
      return;
    g.setClip(cr.x, cr.y, cr.width, cr.height);
    int count = model != null ? model.getCount() : 0;
    int vx = horz ? cr.x : cr.y;
    int vw = horz ? cr.width : cr.height;
    int start = hitTest(vx) < count ? hitTest(vx) : count;
    int end   = hitTest(vx + vw) < count ? hitTest(vx + vw) : count;
    int viewstate = (isEnabled() ? 0 : ItemPainter.DISABLED) | focusState;
    if (count > 0 && start >= 0) {
      Diagnostic.check(end >= start);
      Rectangle rect = getItemBounds(start);
      for (int index = start; index <= end; ++index) {
        if (horz)
          rect.width = sizes.getSize(index);
        else
          rect.height = sizes.getSize(index);
        Object value = model.get(index);
        int state = viewstate | (index == selection ? ItemPainter.SELECTED : 0);
        state |= (index == rollover && showRollover) ? ItemPainter.ROLLOVER : 0;
        g.setColor(getBackground());
        g.setFont(getFont());
        ItemPainter ip = viewManager != null ? viewManager.getPainter(index, value, state) : null;
        if (ip != null) {
          g.clipRect(rect.x, rect.y, rect.width, rect.height);
          ip.paint(value, g, rect, state, this);
          g.setClip(cr.x, cr.y, cr.width, cr.height);
        }
        if (horz)
          rect.x += rect.width;
        else
          rect.y += rect.height;
      }
      if (horz)
        rect.width = clip.x + clip.width - rect.x;
      else
        rect.height = clip.y + clip.height - rect.y;
      if (rect.width > 0 && rect.height > 0) {
        if (isOpaque() && texture == null) {
          g.setColor(getBackground());
          g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
      }
    }
    else if (isOpaque() && texture == null) {
      g.setColor(getBackground());
      g.fillRect(clip.x, clip.y, clip.width, clip.height);
    }
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(3);
    if (model instanceof Serializable)
      hash.put("m", model); 
    if (viewManager instanceof Serializable)
      hash.put("v", viewManager); 
    if (sizes instanceof Serializable)
      hash.put("s", sizes); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("m"); 
    if (data instanceof VectorModel)
      model = (VectorModel)data;
    data = hash.get("v"); 
    if (data instanceof VectorViewManager)
      viewManager = (VectorViewManager)data;
    data = hash.get("s"); 
    if (data instanceof SizeVector)
      sizes = (SizeVector)data;
  }

  private transient VectorViewManager  viewManager;
  private transient VectorModel        model;
  private transient SizeVector         sizes = new VariableSizeVector(new int[0]);

  // properties
  private int                thickness = 0; // zero indicates that it must be calculated
  private int                orientation = Orientation.HORIZONTAL;
  private boolean            horz = true; // quick access boolean flag for orientation
  private int                selection = -1;
  private int                margin = 8;
  private int                alignment = Alignment.CENTER | Alignment.MIDDLE;
  private Insets             margins = new Insets(1, 1, 1, 1);

  // runtime state
  private boolean            resizable  = true;
  private boolean            liveResize = true;
  private boolean            moveable   = true;

  private boolean            resizing   = false;
  private boolean            startMove  = false;
  private boolean            moving     = false;
  private boolean            batchMode  = false;
  private boolean            showRollover = false;
  private int                rollover   = -1;
  private int                offset;

  private int                clickItem;

  private transient EventMulticaster   headerListeners = new EventMulticaster();

  private static Cursor      DEFAULT_CURSOR = Cursor.getDefaultCursor();
  private static Cursor      MOVE_CURSOR    = new Cursor(Cursor.MOVE_CURSOR);
  private static Cursor      H_SIZE_CURSOR  = new Cursor(Cursor.W_RESIZE_CURSOR);
  private static Cursor      V_SIZE_CURSOR  = new Cursor(Cursor.N_RESIZE_CURSOR);
}

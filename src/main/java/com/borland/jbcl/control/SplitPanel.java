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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.borland.jbcl.layout.PaneLayout;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.BeanPanel;

public class SplitPanel
     extends BeanPanel
  implements MouseListener, MouseMotionListener, java.io.Serializable
{
  /** default constructor- creates and hides the divider, sets its color to black
   *      sets the layout of the panel to PaneLayout
   *      sets the PaneLayout gap to 2
   *      listens for panel mouse events
   */
  public SplitPanel()  {
    super(null);
    //setBackground(SystemColor.control);
    setFocusAware(false);
    paneLayoutDivider.setName("SplitPanel.splitter"); 
    add(paneLayoutDivider);  //the layout will not know about this guy
    paneLayoutDivider.setVisible(false);
    paneLayoutDivider.setEnabled(false);
    setDividerColor(Color.black);
    layout.setGap(2);
    addMouseListener(this);
    addMouseMotionListener(this);
    super.setLayout(layout);
  }

  public void setGap(int gap) {
    layout.setGap(gap);
  }
  public int getGap() {
    return layout.getGap();
  }

  /**set the color of the Pane divider
   *   default is Black
   *
   */
  public void setDividerColor(Color color) {
    paneLayoutDivider.setBackground(color);
  }

  public Color getDividerColor() {
    return paneLayoutDivider.getBackground();
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
   * No-op if LayoutManager is not a PaneLayout
   * otherwise sets the LayoutManager to the supplied PaneLayout
   */
  public void setLayout(LayoutManager mgr) {
    if (mgr instanceof PaneLayout) {
      layout = (PaneLayout) mgr;
      super.setLayout(mgr);
    }
//    else
//      throw new IllegalArgumentException(Res._PaneLayoutOnly);
  }

  /**
   * (Internal) display the selection bar if user clicks on one
   *  disable all the panels components until mouse is released (so it is easy to follow the divider)
   * Mouse Listener Interface method
   */
  public void mousePressed(MouseEvent e) {
    if (e.getComponent() != this || !mouseOverPanel)  //dragging off the container will generate a bogus mouse pressed
      return;
    //System.out.println("mousePressed");
    int mouseX = e.getX();
    int mouseY = e.getY();
    if (mouseOverPanel)
    dividerRect = layout.getDividerRect(mouseX, mouseY);
    if (dividerRect != null) {
      dividerBounds = layout.getDividerBounds();
      if (dividerRect.width > dividerRect.height) {
        yChanges = true;
        yDelta = mouseY - dividerRect.y;
      }
      else  {
        yChanges = false;
        xDelta = mouseX - dividerRect.x;
      }
      isSizing = true;
      bounds = getBounds();
      Component[] all = getComponents();
      enabledComponents = new Component[all.length];
      for (int i=0;i< all.length;i++) {
        if (all[i].isEnabled())  {
          enabledComponents[i] = all[i];
          all[i].setEnabled(false);
        }
      }
      add(paneLayoutDivider,0);
      paneLayoutDivider.setBounds(dividerRect.x, dividerRect.y, dividerRect.width, dividerRect.height);
      paneLayoutDivider.setVisible(true);
    }
  }

  /**
   * re-enable components that where disabled.
   * hide the divider
   * (Internal) Mouse Listener Interface method
   */
  public void mouseReleased( MouseEvent e) {
    //System.out.println("mouseReleased");
    paneLayoutDivider.setVisible(false);
    if (isSizing) {
      isSizing = false;
      for (int i=0;i< enabledComponents.length;i++) {
       if (enabledComponents[i] != null)
         enabledComponents[i].setEnabled(true) ;
      }
      setCursor(cursor);
    }
    validate();
  }

  /**
   * (Internal) Mouse Listener Interface method  - no-op
   */
  public void mouseClicked(MouseEvent e) {
  }

  /**
   * (Internal) Mouse Listener Interface method  - restore cursor shape
   */
  public void mouseExited(MouseEvent e) {
    //System.err.println("mouseExited");
    mouseOverPanel = false;
    if (cursor != null  && !isSizing)
      setCursor(cursor);   //restore cursor state
  }

  /**
   * (Internal) Mouse  Listener Interface method  - remember cursor shape
   */
  public void mouseEntered(MouseEvent e) {
    //System.err.println("mouseEntered");
    mouseOverPanel = true;
    if (!isSizing)
      cursor = getCursor();   //remember cursor state
  }

  /**
   * (Internal) Change the cursor when the mouse is over a divider
   * Mouse Motion Listener Interface method
   */
  public void mouseMoved(MouseEvent e) {
    //System.out.println("Move");
    isSizing = false;
    if (e.getComponent() != this)
      return;
    Rectangle rect = layout.getDividerRect(e.getX(), e.getY());
    if (rect != null) {
      if (rect.width > rect.height)
        setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
      else
        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
    }
    else
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * (Internal) Mouse Motion Listener Interface method  -
   *  move the selection bar to track the mouse
   */
  public void mouseDragged(MouseEvent e) {
    if (isSizing) {
      int xD = dividerRect.x;
      int yD = dividerRect.y;
      int y = e.getY();
      int x = e.getX();
      if (y <= bounds.y || y >=  bounds.y + bounds.height)
        return;
      if (x <= bounds.x || x >= bounds.x + bounds.width)
        return;
      if (yChanges) {
        yD = y - yDelta;
        if (yD < dividerBounds.y)
          yD = dividerBounds.y;
        else if (yD >  dividerBounds.height + dividerBounds.y - 1)
          yD = dividerBounds.height + dividerBounds.y - 1;
      }
      else  {
        xD = x - xDelta;
        if (xD > dividerBounds.width  + dividerBounds.x - 1)
          xD = dividerBounds.width + dividerBounds.x - 1;
        else if (xD < dividerBounds.x)
          xD = dividerBounds.x;
      }
      //System.out.println("drag" + xD + " "  + yD);
      layout.dragDivider(xD, yD);
      dividerRect.x = xD;
      dividerRect.y = yD;
      paneLayoutDivider.setLocation(xD, yD);
    }
  }

  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    if (ps.width == 10)
      ps.width = 100;
    if (ps.height == 10)
      ps.height = 100;
    return ps;
  }

  PaneLayout layout = new PaneLayout();
  Canvas paneLayoutDivider = new Canvas();
  Cursor cursor;
  boolean yChanges;
  int xDelta;
  int yDelta;
  Rectangle dividerRect ;  //shape & location of the divider that is being dragged
  Rectangle dividerBounds; //the bounds  of the area that the divider can go
  boolean isSizing  = false;  //flag so drag operation knows if it was started on a divider
  boolean mouseOverPanel = false;
  transient Rectangle bounds; //bounds of this SplitPanel
  transient Component enabledComponents[]; //contains list of components that were disabled during dragging operation
  String textureName;
}

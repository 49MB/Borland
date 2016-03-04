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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.UIManager;

import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.BeanPanel;
import com.borland.jbcl.view.BorderItemPainter;

public class BevelPanel extends BeanPanel implements java.io.Serializable
{
  public static final int FLAT    = 0;
  public static final int RAISED  = 1;
  public static final int LOWERED = 2;
  public BevelPanel() {
    super(new XYLayout());
    super.setBackground(UIManager.getColor("Panel.background")); 
    super.setForeground(UIManager.getColor("Panel.foreground")); 
    super.setFont(UIManager.getFont("Panel.font")); 
    super.setOpaque(true);
    super.focusAware = false;  // JOAL: BevelPanel should not accept focus, BTS12018
    resetEdges();
  }

  public void updateUI() {
    super.updateUI();
    super.setBackground(UIManager.getColor("Panel.background")); 
    super.setForeground(UIManager.getColor("Panel.foreground")); 
    super.setFont(UIManager.getFont("Panel.font")); 
  }

  public BevelPanel(int bevelInner, int bevelOuter) {
    super.setLayout(new XYLayout());
    super.setBackground(SystemColor.control);
    super.setOpaque(true);
    if (bevelInner >= FLAT && bevelInner <= LOWERED)
      this.bevelInner = bevelInner;
    if (bevelOuter >= FLAT && bevelOuter <= LOWERED)
      this.bevelOuter = bevelOuter;
    super.focusAware = false;  // JOAL: BevelPanel should not accept focus, BTS12018
    resetEdges();
  }

  public void setBevelInner(int bevelInner) {
    if (bevelInner >= FLAT && bevelInner <= LOWERED && bevelInner != this.bevelInner) {
      this.bevelInner = bevelInner;
      resetEdges();
    }
  }
  public int getBevelInner() { return bevelInner; }

  public void setBevelOuter(int bevelOuter) {
    if ((bevelOuter == FLAT || bevelOuter == RAISED || bevelOuter == LOWERED)
        && bevelOuter != this.bevelOuter) {
      this.bevelOuter = bevelOuter;
      resetEdges();
    }
  }
  public int getBevelOuter() { return bevelOuter; }

  public void setSoft(boolean soft) {
    if (soft != this.soft) {
      this.soft = soft;
      resetEdges();
    }
  }
  public boolean isSoft() { return soft; }

  public void setMargins(Insets margins) {
    if (!this.margins.equals(margins)) {
      if (margins == null)
        this.margins = new Insets(0,0,0,0);
      else
        this.margins = margins;
      invalidate();
      repaint(100);
    }
  }
  public Insets getMargins() { return margins; }

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

  public Insets getInsets() {
    Insets i = super.getInsets();
    Insets b = border != null ? border.getInsets() : new Insets(0,0,0,0);
    return new Insets(i.top + b.top + margins.top,
                      i.left + b.left + margins.left,
                      i.bottom + b.bottom + margins.bottom,
                      i.right + b.right + margins.right);
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    if (d.width <= 10)
      d.width = 100;
    if (d.height <= 10)
      d.height = 100;
    return d;
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension size = getSize();
    if (border != null) {
      Rectangle rect = new Rectangle(0, 0, size.width, size.height);
      border.paint(null, g, rect, 0, null);
    }
  }

  protected void resetEdges() {
    if (bevelInner == FLAT && bevelOuter == FLAT)
      border = null;
    else {
      int style = 0;
      switch (bevelInner) {
        case FLAT:
          break;
        case RAISED:
          style |= BorderItemPainter.INNER_RAISED;
          break;
        case LOWERED:
          style |= BorderItemPainter.INNER_SUNKEN;
          break;
      }
      switch (bevelOuter) {
        case FLAT:
          break;
        case RAISED:
          style |= BorderItemPainter.OUTER_RAISED;
          break;
        case LOWERED:
          style |= BorderItemPainter.OUTER_SUNKEN;
          break;
      }
      border = new BorderItemPainter(style, BorderItemPainter.RECT, soft ? BorderItemPainter.SOFT : 0);
    }
    invalidate();
    repaint(100);
  }

  protected int bevelInner = RAISED;
  protected int bevelOuter = FLAT;
  protected boolean soft = false;
  protected BorderItemPainter border;
  protected Insets margins = new Insets(0,0,0,0);
  protected String textureName;
}

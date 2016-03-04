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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.SystemColor;

import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.BeanPanel;

public class GroupBox extends BeanPanel implements java.io.Serializable
{
  public GroupBox() {
    super(defaultLayout);
    label = "";
    super.focusAware = false;  // JOAL: GroupBox should not accept focus, BTS12018
  }

  public GroupBox(String label) {
    super(defaultLayout);
    this.label = label;
    super.focusAware = false;  // JOAL: GroupBox should not accept focus, BTS12018
  }

  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    if (label != null && !label.equals("")) {
      Font f = getFont();
      if (f != null) {
        FontMetrics fm = getFontMetrics(f);
        ps.width = Math.max(ps.width, fm.stringWidth(label) + fm.getHeight() * 2);
      }
    }
    return ps;
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void setLabel(String label) {
    this.label = label;
    repaint();
  }

  public String getLabel() {
    return label;
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

  /** @DEPRECATED */
  public Insets insets() {
    Font f = getFont();
    int inset = 0;
    if (f != null)
      inset = getFontMetrics(f).getHeight();
    return new Insets(inset, inset, inset, inset);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Save the old font
    Font of = g.getFont();
    Font f = getFont();
    g.setFont(f);
    FontMetrics fm = g.getFontMetrics(f);
    int ascent = fm.getAscent();

    int ww = ascent / 2 + 1;

    Rectangle r = getBounds();
    if (r == null)
      return;
    int xs = ww;
    int ys = ww;
    int xe = r.width  - ww - 1;
    int ye = r.height - ww - 1;

    g.setColor(getForeground());
    g.drawString(label, xs + xs, ascent);
    int tw = fm.stringWidth(label);

    for (int i = 1; i >= 0; i--) {
      g.setColor(i == 0 ? SystemColor.controlShadow : SystemColor.controlLtHighlight);
      g.drawLine(xs + xs - 1, ys + i, xs + i, ys + i);  // top left piece
      g.drawLine(xs + i, ys + i, xs + i, ye + i);   // left
      g.drawLine(xs + i, ye + i, xe + i, ye + i);   // bottom
      g.drawLine(xe + i, ye + i, xe + i, ys + i);   // right
      g.drawLine(xe + i, ys + i, xs + xs + tw, ys + i);  // top right piece
    }
    // Restore the old font
    g.setFont(of);
  }

  final static LayoutManager defaultLayout = new FlowLayout();
  protected String label;
  protected String textureName;
}
